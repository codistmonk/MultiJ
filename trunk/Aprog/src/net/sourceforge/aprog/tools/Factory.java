/*
 *  The MIT License
 * 
 *  Copyright 2013 Codist Monk.
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

package net.sourceforge.aprog.tools;

import static net.sourceforge.aprog.tools.Tools.set;
import static net.sourceforge.aprog.tools.Tools.unchecked;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author codistmonk (creation 2013-07-02)
 * @param <T> The target instance type
 */
public abstract interface Factory<T> extends Serializable {
	
	/**
	 * @return
	 * <br>Maybe null
	 * <br>Maybe new
	 */
	public abstract T newInstance();
	
	public abstract Class<T> getInstanceClass();
	
	/**
	 * @author codistmonk (creation 2013-07-02)
	 * @param <T> The target instance type
	 */
	public static final class ConstantFactory<T> implements Factory<T> {
		
		private final Class<T> instanceClass;
		
		private final T instance;
		
		/**
		 * @param instance
		 * <br>Must not be null
		 * <br>Will be strongly referenced in <code>this</code>
		 */
		@SuppressWarnings("unchecked")
		public ConstantFactory(final T instance) {
			this((Class<T>) instance.getClass(), instance);
		}
		
		/**
		 * @param instanceClass
		 * <br>Must not be null
		 * <br>Will be strongly referenced in <code>this</code>
		 * @param instance
		 * <br>May be null
		 * <br>Will be strongly referenced in <code>this</code>
		 */
		public ConstantFactory(final Class<T> instanceClass, final T instance) {
			this.instanceClass = instanceClass;
			this.instance = instance;
		}
		
		@Override
		public final T newInstance() {
			return this.instance;
		}
		
		@Override
		public final Class<T> getInstanceClass() {
			return this.instanceClass;
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 3073959035314976367L;
		
		/**
		 * Creates a new factory using the specified parameters.
		 * 
		 * @param instance
		 * <br>Maybe null
		 * <br>Will be strongly referenced in the result
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public static final <T> ConstantFactory<T> forInstance(final T instance) {
			return new ConstantFactory<T>(instance);
		}
		
	}
	
	/**
	 * @author codistmonk (creation 2013-07-02)
	 * @param <T> The target instance type
	 */
	public static final class DefaultFactory<T> implements Factory<T> {
		
		private final Constructor<T> constructor;
		
		private final Object[] arguments;
		
		/**
		 * @param cls
		 * <br>Not null
		 * @param arguments
		 * <br>Not null
		 * <br>Will be strongly referenced in <code>this</code>
		 */
		public DefaultFactory(final Class<T> cls, final Object... arguments) {
			try {
				this.constructor = findConstructor(cls, arguments);
				this.arguments = arguments;
			} catch (final Exception exception) {
				throw unchecked(exception);
			}
		}
		
		@Override
		public final T newInstance() {
			try {
				return this.constructor.newInstance(this.arguments);
			} catch (final Exception exception) {
				throw unchecked(exception);
			}
		}
		
		@Override
		public final Class<T> getInstanceClass() {
			return this.constructor.getDeclaringClass();
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 6632177904525343272L;
		
		private static final Class<?>[] OBJECT_ARRAY_ARGUMENT_TYPE = { Object[].class };
		
		private static final Map<Class<?>, Collection<Class<?>>> primitiveCompatibilities;
		
		public static final DefaultFactory<ArrayList> ARRAY_LIST_FACTORY = forClass(ArrayList.class);
		
		public static final DefaultFactory<LinkedList> LINKED_LIST_FACTORY = forClass(LinkedList.class);
		
		public static final DefaultFactory<HashSet> HASH_SET_FACTORY = forClass(HashSet.class);
		
		public static final DefaultFactory<TreeSet> TREE_SET_FACTORY = forClass(TreeSet.class);
		
		public static final DefaultFactory<HashMap> HASH_MAP_FACTORY = forClass(HashMap.class);
		
		public static final DefaultFactory<LinkedHashMap> LINKED_HASH_MAP_FACTORY = forClass(LinkedHashMap.class);
		
		public static final DefaultFactory<TreeMap> TREE_MAP_FACTORY = forClass(TreeMap.class);
		
		static {
			primitiveCompatibilities = new HashMap<Class<?>, Collection<Class<?>>>();
			
			primitiveCompatibilities.put(boolean.class, (Collection) set(boolean.class, Boolean.class));
			primitiveCompatibilities.put(byte.class, (Collection) set(byte.class, Byte.class));
			primitiveCompatibilities.put(char.class, (Collection) set(char.class, Character.class));
			primitiveCompatibilities.put(short.class, (Collection) set(short.class, Short.class));
			primitiveCompatibilities.get(short.class).addAll(primitiveCompatibilities.get(byte.class));
			primitiveCompatibilities.put(int.class, (Collection) set(int.class, Integer.class));
			primitiveCompatibilities.get(int.class).addAll(primitiveCompatibilities.get(char.class));
			primitiveCompatibilities.get(int.class).addAll(primitiveCompatibilities.get(short.class));
			primitiveCompatibilities.put(long.class, (Collection) set(long.class, Long.class));
			primitiveCompatibilities.get(long.class).addAll(primitiveCompatibilities.get(int.class));
			primitiveCompatibilities.put(float.class, (Collection) set(float.class, Float.class));
			primitiveCompatibilities.get(float.class).addAll(primitiveCompatibilities.get(long.class));
			primitiveCompatibilities.put(double.class, (Collection) set(double.class, Double.class));
			primitiveCompatibilities.get(double.class).addAll(primitiveCompatibilities.get(float.class));
		}
		
		/**
		 * Creates a new factory using the specified parameters.
		 * 
		 * @param cls
		 * <br>Not null
		 * @param arguments
		 * <br>Not null
		 * <br>Will be strongly referenced in <code>this</code>
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public static final <T> DefaultFactory<T> forClass(final Class<T> cls, final Object... arguments) {
			return new DefaultFactory<T>(cls, arguments);
		}
		
		/**
		 * @param cls
		 * <br>Not null
		 * @param arguments
		 * <br>Not null
		 * @return
		 * <br>Maybe null
		 */
		@SuppressWarnings("unchecked")
		public static final <T> Constructor<T> findConstructor(final Class<T> cls, final Object... arguments) {
			final int n = arguments.length;
			Constructor<?> result = null;
			
			for (final Constructor<?> constructor : cls.getConstructors()) {
				final Class<?>[] parameterTypes = constructor.getParameterTypes();
				
				if (Arrays.equals(OBJECT_ARRAY_ARGUMENT_TYPE, parameterTypes)) {
					result = constructor;
				} else if (n == parameterTypes.length) {
					boolean ok = true;
					
					for (int i = 0; i < n; ++i) {
						if (arguments[i] != null) {
							final Class<? extends Object> argumentType = arguments[i].getClass();
							
							if (parameterTypes[i].isPrimitive()) {
								if (!primitiveCompatibilities.get(parameterTypes[i]).contains(argumentType)) {
									ok = false;
									break;
								}
							} else if (!parameterTypes[i].isAssignableFrom(argumentType)) {
								ok = false;
								break;
							}
						}
					}
					
					if (ok) {
						result = constructor;
						break;
					}
				}
			}
			
			return (Constructor<T>) result;
		}
		
	}
	
}
