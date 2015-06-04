/*
 *  The MIT License
 *
 *  Copyright 2010 Codist Monk.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import multij.tools.IllegalInstantiationException;

/**
 * @author codistmonk (creation 2010-10-22)
 */
public final class Launcher {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private Launcher() {
        throw new IllegalInstantiationException();
    }
    
    /**
     * {@value}.
     * This is the root folder that should contain jar and native libraries.
     */
    public static final String LIBRARY_ROOT = "lib/";

    /**
     * 
     * @param mainClass
     * <br>Not null
     * @param commandLineArguments
     * <br>Not null
     * @return 
     * <br>Not null
     * @throws RuntimeException If an error occurs
     */
    public static final Process launch(final Class<?> mainClass, final String... commandLineArguments) {
        try {
            final File applicationFile = getClassRoot(mainClass);

            getLoggerForThisMethod().log(Level.INFO, "Launching application in {0}", applicationFile);

            final Process process;

            if (isJar(applicationFile)) {
                process = startApplicationFromJar(applicationFile, mainClass, commandLineArguments);
            } else {
                process = startApplicationInIDE(new File(LIBRARY_ROOT), mainClass, commandLineArguments);
            }

            redirectOutputsToConsole(process);

            return process;
        } catch (final Exception exception) {
            return throwUnchecked(exception);
        }
    }

    /**
     *
     * @param process
     * <br>Not null
     */
    public static final void redirectOutputsToConsole(final Process process) {
        pipe(process.getErrorStream(), System.err);
        pipe(process.getInputStream(), System.out);
    }

    /**
     * Creates and starts a new thread that scans {@code input} (with a {@link Scanner}) and writes each line to {@code output}.
     * 
     * @param input
     * <br>Not null
     * <br>Input-output
     * @param output
     * <br>Not null
     * <br>Input-output
     * @return 
     * <br>Not null
     */
    public static final Thread pipe(final InputStream input, final PrintStream output) {
        final Thread result = new Thread() {

            @Override
            public final void run() {
                final Scanner errorScanner = new Scanner(input);

                try {
                    while (errorScanner.hasNext()) {
                        output.println(errorScanner.nextLine());
                    }
                } catch (final Exception exception) {
                    Tools.ignore(exception);
                }
            }

        };
        
        result.start();
        
        return result;
    }

    /**
     * @param mainClass 
     * <br>Not null
     * @param applicationJar
     * <br>Not null
     * @param commandLineArguments 
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Process startApplicationFromJar(final File applicationJar,
            final Class<?> mainClass, final String... commandLineArguments) {
        return execute(
                applicationJar.toString(),
                createLibraryPath(applicationJar),
                mainClass,
                commandLineArguments);
    }

    /**
     * @param mainClass 
     * <br>Not null
     * @param libraryRoot 
     * <br>Not null
     * @param commandLineArguments 
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Process startApplicationInIDE(final File libraryRoot,
            final Class<?> mainClass, final String... commandLineArguments) {
        return execute(
                getClassPathInIDE(getClassRoot(mainClass), libraryRoot),
                getLibraryPath(libraryRoot),
                mainClass,
                commandLineArguments);
    }
    
    /**
     * 
     * @param classPath
     * <br>Maybe null
     * @param libraryPath
     * <br>Maybe null
     * @param mainClass
     * <br>Not null
     * @param commandLineArguments 
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Process execute(final String classPath, final String libraryPath,
            final Class<?> mainClass, final String... commandLineArguments) {
        final String[] command = append(array(
                "java",
                "-Djava.library.path=" + libraryPath,
                "-cp", classPath,
                mainClass.getName()),
                commandLineArguments);
        
        getLoggerForThisMethod().info(debug(DEBUG_STACK_OFFSET, (Object[]) command));
        
        try {
            return Runtime.getRuntime().exec(command);
        } catch (final IOException exception) {
            throw unchecked(exception);
        }
    }

    /**
     * @param classRoot
     * <br>Not null
     * @param jarRoot
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final String getClassPathInIDE(final File classRoot, final File jarRoot) {
        final StringBuilder result = new StringBuilder(classRoot.toString());

        for (final File jar : getJars(jarRoot)) {
            result.append(File.pathSeparator);
            result.append(jar);
        }

        return result.toString();
    }

    /**
     * @param applicationJar
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     * @throws RuntimeException If {@code applicationJar} cannot be read as a jar file
     */
    public static final String createLibraryPath(final File applicationJar) {
        JarFile jarFile;
        try {
            jarFile = new JarFile(applicationJar);
        } catch (final IOException exception) {
            throw unchecked(exception);
        }
        
        final File nativeLibraryBase = createTemporaryFile("lib", "", null).getParentFile();

        for (final JarEntry entry : iterable(jarFile.entries())) {
            final File entryFile = new File(entry.getName());
            final String entryName = entryFile.getName();

            if (isNativeLibrary(entryFile)) {
                getLoggerForThisMethod().log(Level.INFO, "Unpacking native library: {0}", entryName);

                try {
                    write(jarFile.getInputStream(entry), new FileOutputStream(new File(nativeLibraryBase, entryName)));
                } catch (final IOException exception) {
                    getLoggerForThisMethod().log(Level.SEVERE, null, exception);
                }
            }
        }

        getLoggerForThisMethod().log(Level.INFO, "Native libraries unpacked in: {0}", nativeLibraryBase);

        return getLibraryPath(nativeLibraryBase);
    }

    /**
     * @param base
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final String getLibraryPath(final File base) {
        final StringBuilder result = new StringBuilder();
        final Set<String> parents = new HashSet<String>();

        for (final File nativeLibrary : getNativeLibraries(base)) {
            final String parent = nativeLibrary.getParentFile().getAbsolutePath();

            if (!parents.contains(parent)) {
                parents.add(parent);

                if (!result.toString().isEmpty()) {
                    result.append(File.pathSeparator);
                }

                result.append(nativeLibrary.getParent());
            }
        }

        return result.toString();
    }

    /**
     *
     * @param base
     * <br>Not null
     * <br>Maybe shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final List<File> getJars(final File base) {
        return new JarCollector().collect(base);
    }

    /**
     *
     * @param base
     * <br>Not null
     * <br>Maybe shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final List<File> getNativeLibraries(final File base) {
        return new NativeLibraryCollector().collect(base);
    }

    /**
     *
     * @param file
     * <br>Not null
     * @return
     * <br>Range: any boolean
     */
    public static final boolean isJar(final File file) {
        return file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar");
    }

    /**
     * Determines if {@code file} is a native library by looking at its extension (case insensitive):<ul>
     *  <li>Linux: so;
     *  <li>Mac OS X: dylib, jnilib;
     *  <li>Windows: dll;
     *  <li>Solaris: so.
     * </ul>
     * 
     * @param file
     * <br>Not null
     * @return
     * <br>Range: any boolean
     */
    public static final boolean isNativeLibrary(final File file) {
        final String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        final String fileName = file.getName().toLowerCase();

        if (osName.startsWith("linux")) {
            return fileName.endsWith(".so");
        }

        if (osName.startsWith("mac os x")) {
            return fileName.endsWith(".dylib") || fileName.endsWith(".jnilib");
        }

        if (osName.startsWith("windows")) {
            return fileName.endsWith(".dll");
        }

        if (osName.startsWith("solaris")) {
            return fileName.endsWith(".so");
        }

        return false;
    }

    /**
     * @author codistmonk (creation 2010-10-22)
     */
    public static abstract class AbstractRecursiveCollector {

        /**
         *
         * @param base
         * <br>Not null
         * <br>Maybe shared
         * @return
         * <br>Not null
         * <br>New
         */
        public final List<File> collect(final File base) {
            final List<File> result = new ArrayList<File>();

            if (this.accept(base)) {
                result.add(base);
            } else if (base.isDirectory()) {
                for (final File subFile : base.listFiles()) {
                    result.addAll(this.collect(subFile));
                }
            }

            return result;
        }

        /**
         *
         * @param file
         * <br>Not null
         * @return
         * <br>Range: any boolean
         */
        public abstract boolean accept(final File file);

    }

    /**
     * @author codistmonk (creation 2010-10-22)
     */
    public static final class JarCollector extends AbstractRecursiveCollector {

        @Override
        public final boolean accept(final File file) {
            return isJar(file);
        }

    }

    /**
     * @author codistmonk (creation 2010-10-22)
     */
    public static final class NativeLibraryCollector extends AbstractRecursiveCollector {

        @Override
        public final boolean accept(final File file) {
            return isNativeLibrary(file);
        }

    }

}
