/*
 *  The MIT License
 * 
 *  Copyright 2015 Codist Monk.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package multij.tools;

import static multij.tools.Tools.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author codistmonk (creation 2015-04-09)
 */
public final class Scripting implements Serializable {
	
	private final ScriptEngine scriptEngine;
	
	private final Scripting.Importer importer;
	
	public Scripting() {
		this("JavaScript");
	}
	
	/**
	 * @param engineName
	 * <br>Must not be null
	 */
	public Scripting(final String engineName) {
		this.scriptEngine = getEngine(engineName);
		this.importer = new Importer(this.scriptEngine, ScriptContext.ENGINE_SCOPE);
		
		{
			final Bindings bindings = this.getScriptEngine().getBindings(ScriptContext.ENGINE_SCOPE);
			
			if (!bindings.containsKey("help")) {
				bindings.put("help", new Runnable() {
					
					@Override
					public final void run() {
						help();
					}
					
				});
			}
			
			if (!bindings.containsKey("listMainClasses")) {
				bindings.put("listMainClasses", new Runnable() {
					
					@Override
					public final void run() {
						listMainClasses();
					}
					
				});
			}
			
			if (!bindings.containsKey("run")) {
				bindings.put("run", new Consumer<Object>() {
					
					@Override
					public final void accept(final Object classNameAndCommandLineArguments) {
						run(classNameAndCommandLineArguments.toString());
					}
					
				});
			}
		}
	}
	
	/**
	 * @param command
	 * <br>Must not be null
	 * @return
	 * <br>Maybe null
	 * <br>Maybe new
	 */
	public final Object eval(final String command) {
		try {
			return this.getScriptEngine().eval(command);
		} catch (final ScriptException exception) {
			throw unchecked(exception);
		}
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <br>Strong reference in <code>this</code>
	 */
	public final ScriptEngine getScriptEngine() {
		return this.scriptEngine;
	}
	
	/**
	 * @param keys
	 * <br>Must not be null
	 * @return <code>this</code>
	 * <br>Not null
	 */
	public final Scripting importAll(final Object... keys) {
		this.importer.importAll(keys);
		
		return this;
	}
	
	/**
	 * @param key
	 * <br>Must not be null
	 * @return <code>this</code>
	 * <br>Not null
	 */
	public final Scripting importPackage(final Object key) {
		this.importer.importPackage(key);
		
		return this;
	}
	
	/**
	 * @param key
	 * <br>Must not be null
	 * @return <code>this</code>
	 * <br>Not null
	 */
	public final Scripting importClass(final Object key) {
		this.importer.importClass(key);
		
		return this;
	}
	
	private static final long serialVersionUID = -3104607045331464557L;
	
	public static final void help() {
		System.out.println("--");
		System.out.println("help();");
		System.out.println("	Print this message.");
		System.out.println("listMainClasses();");
		System.out.println("	List available main classes.");
		System.out.println("run(\"name.of.Class arg0 arg1 ...\");");
		System.out.println("	List available main classes.");
		System.out.println("importPackage(name.of.package);");
		System.out.println("	Import a package.");
		System.out.println("importClass(name.of.Class);");
		System.out.println("	Import a class.");
		System.out.println("quit()");
		System.out.println("	Quit.");
		System.out.println("--");
		System.out.println();
	}
	
	public static final void listMainClasses() {
		try {
			final File applicationFile = getApplicationFile();
			
			if (applicationFile.isDirectory()) {
				final Path applicationPath = applicationFile.toPath();
				try (final Stream<Path> walk = Files.walk(applicationPath)) {
					walk.forEach(new Consumer<Path>() {
						
						@Override
						public final void accept(final Path p) {
							printIfMainClass(p.toString(), applicationPath);
						}
						
					});
				}
			} else {
				final int maximumHeaderSize = 10_000;
				try (final BufferedInputStream in = new BufferedInputStream(new FileInputStream(applicationFile))) {
					in.mark(maximumHeaderSize);
					
					for (int i = 0; 0 < maximumHeaderSize; ++i) {
						in.reset();
						in.skip(i);
						
						final ZipInputStream zip = new ZipInputStream(in);
						
						ZipEntry entry = zip.getNextEntry();
						
						if (entry != null) {
							while (entry != null) {
								printIfMainClass(entry.getName(), null);
								entry = zip.getNextEntry();
							}
							
							break;
						}
					}
				}
			}
		} catch (final IOException exception) {
			throw new UncheckedIOException(exception);
		}
	}
	
	/**
	 * @param classNameAndCommandLineArguments
	 * <br>Must not be null
	 */
	public static final void run(final String classNameAndCommandLineArguments) {
		final String[] strings = classNameAndCommandLineArguments.split("\\s+");
		
		try {
			getMainMethod(strings[0]).invoke(null, (Object) Arrays.copyOfRange(strings, 1, strings.length));
		} catch (final Exception exception) {
			throw unchecked(exception);
		}
	}
	
	/**
	 * Returns the main method from <code>className</code> or throws an exception.
	 * 
	 * @param className
	 * <br>Must not be null
	 * @return
	 * <br>Not null
	 */
	public static final Method getMainMethod(final String className) {
		try {
			return Class.forName(className).getMethod("main", String[].class);
		} catch (final Exception exception) {
			throw unchecked(exception);
		}
	}
	
	/**
	 * @param commandLineArguments
	 * <br>Must not be null
	 */
	public static final void main(final String... commandLineArguments) {
		final CommandLineArgumentsParser arguments = new CommandLineArgumentsParser(commandLineArguments);
		final String language = arguments.get("language", "JavaScript");
		final boolean showHelp = arguments.get("showHelp", true);
		final Scripting scripting = new Scripting(language);
		
		if (showHelp) {
			scripting.eval("help()");
		}
		
		try (final Scanner scanner = new Scanner(System.in)) {
			while (scanner.hasNext()) {
				try {
					final String command = scanner.nextLine();
					final Object result = scripting.eval(command);
					
					if (!command.endsWith(";")) {
						System.out.println(result);
					}
				} catch (final Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * @param preferredEngineName
	 * <br>Must not be null
	 * @return
	 * <br>Maybe null
	 */
	public static final ScriptEngine getEngine(final String preferredEngineName) {
		final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine result = scriptEngineManager.getEngineByName(preferredEngineName);
		
		if (result == null) {
			final List<ScriptEngineFactory> engineFactories = scriptEngineManager.getEngineFactories();
			
			if (!engineFactories.isEmpty()) {
				result = engineFactories.get(0).getScriptEngine();
			}
		}
		
		return result;
	}
	
	/**
	 * @param string
	 * <br>Must not be null
	 * @param applicationPath
	 * <br>May be null
	 */
	public static void printIfMainClass(final String string, final Path applicationPath) {
		if (string.endsWith(".class")) {
			String className = string;
			
			if (applicationPath != null) {
				className = className.substring(applicationPath.toString().length() + 1);
			}
			
			className = className.replaceFirst("\\.class$", "").replaceAll("[/$]+", ".");
			
			try {
				final Method method = getMainMethod(className);
				
				if (Modifier.isStatic(method.getModifiers())) {
					System.out.println(className);
				}
			} catch (final Throwable exception) {
				ignore(exception);
			}
		}
	}

	/**
	 * @author codistmonk (creation 2015-04-09)
	 */
	public static final class Importer implements Bindings, Serializable {
		
		private final ScriptEngine scriptEngine;
		
		private final Map<String, Object> map;
		
		private final Collection<String> imports;
		
		private final Collection<Thread> recursionFlags;
		
		/**
		 * @param scriptEngine
		 * <br>Must not be null
		 * <br>Stored as strong reference in <code>this</code>
		 * @param scope
		 * <br>Range: any int
		 */
		public Importer(final ScriptEngine scriptEngine, final int scope) {
			this.scriptEngine = scriptEngine;
			this.map = scriptEngine.getBindings(scope);
			this.imports = new HashSet<>();
			this.recursionFlags = new HashSet<>();
			
			if (!scriptEngine.getBindings(scope).containsKey("importPackage")) {
				this.put("importPackage", new Consumer<Object>() {
					
					@Override
					public final void accept(final Object object) {
						Importer.this.importPackage(object);
					}
					
				});
			}
			
			if (!scriptEngine.getBindings(scope).containsKey("importClass")) {
				this.put("importClass", new Consumer<Object>() {
					
					@Override
					public final void accept(final Object object) {
						Importer.this.importClass(object);
					}
					
				});
			}
			
			scriptEngine.setBindings(this, scope);
		}
		
		/**
		 * @return
		 * <br>Not null
		 * <br>Strong reference in <code>this</code>
		 */
		public final ScriptEngine getScriptEngine() {
			return this.scriptEngine;
		}
		
		/**
		 * @return
		 * <br>Not null
		 * <br>Strong reference in <code>this</code>
		 */
		public final Collection<String> getImports() {
			return this.imports;
		}
		
		/**
		 * @param keys
		 * <br>Must not be null
		 * @return <code>this</code>
		 * <br>Not null
		 */
		public final Importer importAll(final Object... keys) {
			for (final Object key : keys) {
				final String string = key.toString();
				
				if (string.startsWith(PACKAGE_KEY)) {
					this.importPackage(key);
				} else {
					this.importClass(key);
				}
			}
			
			return this;
		}
		
		/**
		 * @param key
		 * <br>Must not be null
		 * @return <code>this</code>
		 * <br>Not null
		 */
		public final Importer importPackage(final Object key) {
			this.importWithoutPrefix(key, PACKAGE_KEY);
			
			return this;
		}
		
		/**
		 * @param key
		 * <br>Must not be null
		 * @return <code>this</code>
		 * <br>Not null
		 */
		public final Importer importClass(final Object key) {
			return this.importWithoutPrefix(key, CLASS_KEY);
		}
		
		@Override
		public final boolean containsKey(final Object key) {
			if (!this.map.containsKey(key)) {
				final Thread flag = Thread.currentThread();
				final boolean flagSetInCurrentFrame = this.recursionFlags.add(flag);
				
				try {
					if (flagSetInCurrentFrame) {
						for (final String prefix : getImports()) {
							if (this.bind(prefix + "." + key, prefix, key) || this.bind(prefix, prefix, key)) {
								return true;
							}
						}
					}
					
					return false;
				} finally {
					if (flagSetInCurrentFrame) {
						this.recursionFlags.remove(flag);
					}
				}
			}
			
			return true;
		}
		
		@Override
		public final Set<String> keySet() {
			return this.map.keySet();
		}
		
		@Override
		public final Object put(final String key, final Object value) {
			final Thread flag = Thread.currentThread();
			final boolean flagSetInCurrentFrame = this.recursionFlags.add(flag);
			
			try {
				return flagSetInCurrentFrame ? this.map.put(key, value) : null;
			} finally {
				if (flagSetInCurrentFrame) {
					this.recursionFlags.remove(flag);
				}
			}
		}
		
		@Override
		public final Object get(final Object key) {
			final Thread flag = Thread.currentThread();
			final boolean flagSetInCurrentFrame = this.recursionFlags.add(flag);
			
			try {
				return flagSetInCurrentFrame ? this.map.get(key) : null;
			} finally {
				if (flagSetInCurrentFrame) {
					this.recursionFlags.remove(flag);
				}
			}
		}
		
		@Override
		public final Set<Map.Entry<String, Object>> entrySet() {
			return this.map.entrySet();
		}
		
		@Override
		public final int size() {
			return this.map.size();
		}
		
		@Override
		public final boolean isEmpty() {
			return this.map.isEmpty();
		}
		
		@Override
		public final boolean containsValue(final Object value) {
			return this.map.containsValue(value);
		}
		
		@Override
		public final Object remove(final Object key) {
			return this.map.remove(key);
		}
		
		@Override
		public final void putAll(final Map<? extends String, ? extends Object> m) {
			this.map.putAll(m);
		}
		
		@Override
		public final void clear() {
			this.map.clear();
		}
		
		@Override
		public final Collection<Object> values() {
			return this.map.values();
		}
		
		/**
		 * @param key
		 * <br>Must not be null
		 * @param prefix
		 * <br>Must not be null
		 * @return <code>this</code>
		 * <br>Not null
		 */
		private final Importer importWithoutPrefix(final Object key, final String prefix) {
			String name = key instanceof Package ? ((Package) key).getName() :
				key instanceof Class ? ((Class<?>) key).getName() : key.toString();
			final boolean prefixed = name.startsWith(prefix);
			name = name.substring(prefixed ? prefix.length() : 0, name.length() - (prefixed ? 1 : 0));
			
			this.getImports().add(name);
			
			return this;
		}
		
		/**
		 * @param className
		 * <br>Must not be null
		 * @param prefix
		 * <br>Must not be null
		 * @param key
		 * <br>Must not be null
		 * @return
		 * <br>Range: any boolean
		 */
		private final boolean bind(final String className, final String prefix, final Object key) {
			try {
				Class.forName(className);
				
				final Object candidate = this.getScriptEngine().eval("Packages." + prefix + "." + key);
				
				if (candidate != null) {
					this.map.put(key.toString(), candidate);
					
					return true;
				}
			} catch (final ClassNotFoundException | ScriptException exception) {
				ignore(exception);
			}
			
			return false;
		}
		
		private static final long serialVersionUID = -4733030105076545625L;
		
		private static final String PACKAGE_KEY = "[JavaPackage ";
		
		private static final String CLASS_KEY = "JavaClassStatics[";
		
	}
	
}
