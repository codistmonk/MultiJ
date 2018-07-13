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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author codistmonk (creation 2010-06-11)
 */
public final class Tools {
	
	/**
	 * @throws IllegalInstantiationException To prevent instantiation
	 */
	private Tools() {
		throw new IllegalInstantiationException();
	}
	
	private static PrintStream debugOutput = new PrintStream(new SystemOutOutputStream());
	
	private static PrintStream debugErrorOutput = new PrintStream(new SystemErrOutputStream());
	
	public static final int DEBUG_STACK_OFFSET = getDebugStackOffset();
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	/**
	 * Throws {@link RuntimeException} if <code>!test</code>.
	 * 
	 * @param test
	 * <br>Range: any boolean
	 */
	public static final void check(final boolean test) {
		check(test, () -> "Internal check failed");
	}
	
	/**
	 * Throws {@link RuntimeException} if <code>!test</code>.
	 * 
	 * @param test
	 * <br>Range: any boolean
	 * @param message
	 * <br>Must not be null
	 */
	public static final void check(final boolean test, final Supplier<String> message) {
		if (!test) {
			throw new RuntimeException(message.get());
		}
	}
	
	/**
	 * Throws {@link IllegalArgumentException} if <code>!test</code>.
	 * 
	 * @param test
	 * <br>Range: any boolean
	 */
	public static final void checkArgument(final boolean test) {
		checkArgument(test, () -> "");
	}
	
	/**
	 * Throws {@link IllegalArgumentException} if <code>!test</code>.
	 * 
	 * @param test
	 * <br>Range: any boolean
	 * @param message
	 * <br>Must not be null
	 */
	public static final void checkArgument(final boolean test, final Supplier<String> message) {
		if (!test) {
			throw new IllegalArgumentException(message.get());
		}
	}
	
	/**
	 * Generates a sequence of <code>int</code> tuples covering the hyperblock specified with <code>bounds</code>.
	 * The generated tuples only differ by their values (the int array is updated during iteration).
	 * <br>Example:<code>
	 * <br>for (final int[] i : cartesian(-2, -1,&nbsp;&nbsp;0, 2)) {
	 * <br>&nbsp;&nbsp;System.out.println(java.util.Arrays.toString(i));
	 * <br>}
	 * <br>// Expected output:
	 * <br>// [-2, 0]
	 * <br>// [-2, 1]
	 * <br>// [-2, 2]
	 * <br>// [-1, 0]
	 * <br>// [-1, 1]
	 * <br>// [-1, 2]
	 * </code>
	 * @param bounds Concatenation of pairs (lower bound, upper bound)
	 * <br>Must not be null
	 * @return
	 * <br>New
	 * <br>Not null
	 */
	public static final Iterable<int[]> cartesian(final int... bounds) {
		return new Iterable<int[]>() {
			
			@Override
			public final Iterator<int[]> iterator() {
				final int n = bounds.length / 2;
				
				return new Iterator<int[]>() {
					
					private int[] result;
					
					@Override
					public final boolean hasNext() {
						if (this.result == null) {
							this.result = new int[n];
							
							for (int i = 0; i < n; ++i) {
								this.result[i] = bounds[2 * i + 0];
							}
							
							--this.result[n - 1];
						}
						
						for (int i = 0; i < n; ++i) {
							if (this.result[i] < bounds[2 * i + 1]) {
								return true;
							}
						}
						
						return false;
					}
					
					@Override
					public final int[] next() {
						for (int i = n - 1; bounds[2 * i + 1] < ++this.result[i] && 0 < i; --i) {
							this.result[i] = bounds[2 * i + 0];
						}
						
						return this.result;
					}
					
				};
			}
			
		};
	}
	
	/**
	 * Generates filtered coordinates inside <code>r</code>-dimensional hypercube of side <code>[0 .. n - 1]</code>.
	 * @param n
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE]</code>
	 * @param r
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE / 2]</code>
	 * @param filter
	 * <br>Must not be null
	 * @return
	 * <br>New
	 * <br>Not null
	 */
	public static final Iterable<int[]> hcf(final int n, final int r, final Predicate<int[]> filter) {
		return new Iterable<int[]>() {
			
			@Override
			public final Iterator<int[]> iterator() {
				
				return new Iterator<int[]>() {
					
					private final Iterator<int[]> i = cartesian(hypercubeBounds(r, n - 1)).iterator();
					
					private int[] result;
					
					@Override
					public final boolean hasNext() {
						while (this.i.hasNext()) {
							this.result = this.i.next();
							
							if (filter.test(this.result)) {
								return true;
							}
						}
						
						return false;
					}
					
					@Override
					public final int[] next() {
						return this.result;
					}
					
				};
			}
			
		};
	}
	
	/**
	 * Generates indices for ordered choice of <code>r</code> elements in </code>n</code>
	 * <br>Example:<code>
	 * <br>for (final int[] i : permutations(3, 2)) {
	 * <br>&nbsp;&nbsp;System.out.println(java.util.Arrays.toString(i));
	 * <br>}
	 * <br>// Expected output:
	 * <br>// [0, 1]
	 * <br>// [0, 2]
	 * <br>// [1, 0]
	 * <br>// [1, 2]
	 * <br>// [2, 0]
	 * <br>// [2, 1]
	 * </code>
	 * @param n
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE]</code>
	 * @param r
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE / 2]</code>
	 * @return <code>{@link hcf}(n, r, {@link #isUniques})</code>
	 */
	public static final Iterable<int[]> permutations(final int n, final int r) {
		return hcf(n, r, Tools::isUniques);
	}
	
	/**
	 * Generates indices for unordered choice of <code>r</code> elements in </code>n</code>
	 * <br>Example:<code>
	 * <br>for (final int[] i : combinations(3, 2)) {
	 * <br>&nbsp;&nbsp;System.out.println(java.util.Arrays.toString(i));
	 * <br>}
	 * <br>// Expected output:
	 * <br>// [0, 1]
	 * <br>// [0, 2]
	 * <br>// [1, 2]
	 * </code>
	 * @param n
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE]</code>
	 * @param r
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE / 2]</code>
	 * @return <code>{@link hcf}(n, r, {@link #isStrictlySorted})</code>
	 */
	public static final Iterable<int[]> combinations(final int n, final int r) {
		return hcf(n, r, Tools::isStrictlySorted);
	}
	
	/**
	 * Generates bounds for {@link #cartesian(int...)}.
	 * <br>In this context, a hyperbox is a <code>upperBounds.length</code>-dimensional box with each side <code>i</code> being <code>[0 .. upperBounds[i]]</code>. 
	 * @param upperBounds
	 * <br>Must not be null
	 * @return A concatenation of pairs (lower bound, upper bound)
	 * <br>New
	 * <br>Not null
	 */
	public static final int[] hyperboxBounds(final int... upperBounds) {
		final int n = upperBounds.length;
		final int[] result = new int[n * 2];
		
		for (int i = 0; i < n; ++i) {
			result[2 * i + 1] = upperBounds[i];
		}
		
		return result;
	}
	
	/**
	 * Generates bounds for {@link #cartesian(int...)}.
	 * <br>In this context, a hypercube is a <code>n</code>-dimensional box with each side being <code>[0 .. upperBound]</code>. 
	 * @param n
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE / 2]</code>
	 * @param upperBound
	 * <br>Range: <code>[1 .. Integer.MAX_VALUE]</code>
	 * @return A concatenation of pairs (lower bound, upper bound)
	 * <br>New
	 * <br>Not null
	 */
	public static final int[] hypercubeBounds(final int n, final int upperBound) {
		final int[] result = new int[n * 2];
		
		for (int i = 0; i < n; ++i) {
			result[2 * i + 1] = upperBound;
		}
		
		return result;
	}
	
	/**
	 * @param values
	 * <br>Must not be null
	 * @return <code>{@link #testPairwise}(&lt;, values)</code>
	 */
	public static final boolean isStrictlySorted(final int... values) {
		return testPairwise((a, b) -> a < b, values);
	}
	
	/**
	 * @param values
	 * <br>Must not be null
	 * @return <code>{@link #testPairwise}(!=, values)</code>
	 */
	public static final boolean isUniques(final int... values) {
		return testPairwise((a, b) -> a != b, values);
	}
	
	/**
	 * @param predicate
	 * <br>Must not be null
	 * @param values
	 * <br>Must not be null
	 * @return <code>true</code> iff <code>&forall;i,j&in;values i&lt;j &Rightarrow; predicate(i,j)</code>
	 */
	public static final boolean testPairwise(final IntBinaryPredicate predicate, final int... values) {
		final int n = values.length;
		
		for (int i = 0; i < n; ++i) {
			for (int j = i + 1; j < n; ++j) {
				if (!predicate.test(values[i], values[j])) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @author codistmonk (creation 2017-09-09)
	 */
	public static abstract interface IntBinaryPredicate {
		
		public abstract boolean test(int left, int right);
		
	}
	
	/**
	 * Replaces all:<ol>
	 * 	<li><code>&lt;</code> with <code>&amp;lt;</code></li>
	 * 	<li><code>&amp;</code> with <code>&amp;amp;</code></li>
	 * 	<li><code>\n</code> with <code>&lt;br></code></li>
	 * </ol>
	 * <br>
	 * in <code>object</code>'s string representation.
	 * 
	 * @param object
	 * <br>Maybe null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final String escapeHTML(final Object object) {
		return ("" + object).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\n", "<br>");
	}
	
	/**
	 * @param array
	 * <br>Must not be null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T deepClone(final T array) {
		final Class<? extends Object> cls = array.getClass();
		final int n = Array.getLength(array);
		final Object result = Array.newInstance(cls.getComponentType(), n);
		
		if (cls.getComponentType().isArray()) {
			for (int i = 0; i < n; ++i) {
				Array.set(result, i, deepClone(Array.get(array, i)));
			}
		} else {
			System.arraycopy(array, 0, result, 0, n);
		}
		
		return (T) result;
	}
	
	/**
	 * @param array
	 * <br>Must not be null
	 * <br>Input-output
	 * @param i
	 * <br>Range: <code>[0 .. array.length - 1]</code>
	 * @param j
	 * <br>Range: <code>[0 .. array.length - 1]</code>
	 */
	public static final void swap(final int[] array, final int i, final int j) {
		final int tmp = array[i];
		array[i] = array[j];
		array[j] = tmp;
	}
	
	/**
	 * Returns the first non-null argument, or <code>null</code> if there isn't one.
	 * 
	 * @param option0
	 * <br>May be null
	 * @param option1
	 * <br>May be null
	 * @return
	 * <br>May be null
	 */
	public static final <T> T select(final T option0, final T option1) {
		return option0 != null ? option0 : option1;
	}
	
	/**
	 * @param values
	 * <br>Must not be null
	 * @param comparator
	 * <br>Must not be null
	 * @throws RuntimeException if the values are not sorted according to <code>comparator</code>
	 */
	public static final void checkSorted(final int[] values, final IntComparator comparator) {
		final int n = values.length - 1;
		
		for (int i = 0; i < n; ++i) {
			if (0 < comparator.compare(values[i], values[i + 1])) {
				throw new RuntimeException("element(" + i + ") > element(" + (i + 1) + ")");
			}
		}
	}
	
	/**
	 * @param values
	 * <br>Must not be null
	 * <br>Input-output
	 * @param start
	 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
	 * @param end
	 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
	 * @param comparator
	 * <br>Must not be null
	 */
	public static final void sort(final int[] values, final int start, final int end, final IntComparator comparator) {
		DualPivotQuicksort.sort(values, start, end - 1, null, 0, 0, comparator);
	}
	
	/**
	 * @param values
	 * <br>Must not be null
	 * <br>Input-output
	 * @param comparator
	 * <br>Must not be null
	 */
	public static final void sort(final int[] values, final IntComparator comparator) {
		sort(values, 0, values.length, comparator);
	}
	
	/**
	 * Extracts the substring of <code>fileName</code> before the last <code>'.'</code>.
	 * 
	 * @param fileName
	 * <br>Must not be null
	 * @return A nonempty string <code>base</code> if <code>fileName</code>
	 * matches <code>base.extension</code>, otherwise <code>fileName</code>
	 * <br>Not null
	 * <br>Maybe new
	 */
	public static final String baseName(final String fileName) {
		final int lastDotIndex = fileName.lastIndexOf('.');
		
		return lastDotIndex <= 0 ? fileName : fileName.substring(0, lastDotIndex);
	}
	
	/**
	 * Creates a sequence of ints from <code>0</code> to <code>n-1</code>.
	 * 
	 * @param n
	 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final int[] intRange(final int n) {
		final int[] result = new int[n];
		
		for (int i = 0; i < n; ++i) {
			result[i] = i;
		}
		
		return result;
	}
	
	/**
	 * Creates an array of <code>n</code> objects instantiated using <code>factory</code>.
	 * 
	 * @param n
	 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code> 
	 * @param factory
	 * <br>Must not be null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final <T> T[] instances(final int n, final Factory<T> factory) {
		try {
			@SuppressWarnings("unchecked")
			final T[] result = (T[]) Array.newInstance(factory.getInstanceClass(), n);
			
			for (int i = 0; i < n; ++i) {
				result[i] = factory.newInstance();
			}
			
			return result;
		} catch (final NegativeArraySizeException exception) {
			throw unchecked(exception);
		}
	}
	
	/**
	 * Runs the garbage collector and waits for a while.
	 * 
	 * @param sleepMilliseconds
	 * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
	 */
	public static final void gc(final long sleepMilliseconds) {
		System.gc();
		
		sleep(sleepMilliseconds);
	}
	
	/**
	 * Waits for a while.
	 * 
	 * @param milliseconds
	 * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
	 */
	public static final void sleep(final long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (final InterruptedException exception) {
			throw unchecked(exception);
		}
	}
	
	/**
	 * Runs the garbage collector twice.
	 */
	public static final void gc() {
		System.gc();
		System.gc();
	}
	
	/**
	 * @return
	 * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
	 */
	public static final long usedMemory() {
		final Runtime runtime = Runtime.getRuntime();
		
		return runtime.totalMemory() - runtime.freeMemory();
	}
	
	/**
	 * @param object
	 * <br>Not null
	 * @param filePath
	 * <br>Not null
	 */
	public static final void writeObject(final Serializable object, final String filePath) {
		try {
			final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
			
			try {
				oos.writeObject(object);
			} finally {
				oos.close();
			}
		} catch (final Exception exception) {
			throw unchecked(exception);
		}
	}
	
	/**
	 * @param filePath
	 * <br>Not null
	 * @return
	 * <br>Not null
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T readObject(final String filePath) {
		try {
			final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
			
			try {
				return (T) ois.readObject();
			} finally {
				ois.close();
			}
		} catch (final Exception exception) {
			throw unchecked(exception);
		}
	}
	
	/**
	 * Does nothing, but prevents the IDE from displaying "unused" warning.
	 *
	 * @param object
	 * <br>Maybe null
	 */
	public static final void ignore(final Object object) {
		final boolean FALSE = false;
		
		if (FALSE) {
			ignore(object);
		}
	}
	
	/**
	 * This method can be used to determine if a particular compiler performs tail-recursion elimination.
	 * <br>If this particular derecursifiation isn't performed, calling this method will result in an {@link StackOverflowError}.
	 * <br>Otherwise, the program won't terminate.
	 *
	 * @throws StackOverflowError If tail-recursion elimination isn't performed by the compiler
	 */
	public static final void tailRecursiveInfiniteLoop() {
		if (Tools.class != null) {
			tailRecursiveInfiniteLoop();
		}
	}
	
	/**
	 * Creates a new string by concatenating the representations of the <code>array</code>
	 * and using the specified <code>separator</code>.
	 * 
	 * @param separator
	 * <br>Not null
	 * @param array
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final String joinArray(final String separator, final Object array) {
		final StringBuilder resultBuilder = new StringBuilder();
		final int n = Array.getLength(array);
		
		if (0 < n) {
			resultBuilder.append(Array.get(array, 0));
			
			for (int i = 1; i < n; ++i) {
				resultBuilder.append(separator).append(Array.get(array, i));
			}
		}
		
		return resultBuilder.toString();
	}
	
	/**
	 * Creates a new string by concatenating the representations of the <code>objects</code>
	 * and using the specified <code>separator</code>.
	 * 
	 * @param separator
	 * <br>Not null
	 * @param objects
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final String join(final String separator, final Object... objects) {
		final StringBuilder resultBuilder = new StringBuilder();
		final int n = objects.length;
		
		if (0 < n) {
			resultBuilder.append(objects[0]);
			
			for (int i = 1; i < n; ++i) {
				resultBuilder.append(separator).append(objects[i]);
			}
		}
		
		return resultBuilder.toString();
	}
	
	/**
	 * Creates a new string by concatenating the representations of the <code>objects</code>
	 * and using the specified <code>separator</code>.
	 * 
	 * @param separator
	 * <br>Not null
	 * @param objects
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final String join(final String separator, final Iterable<?> objects) {
		final StringBuilder resultBuilder = new StringBuilder();
		final Iterator<?> i = objects.iterator();
		
		if (i.hasNext()) {
			resultBuilder.append(i.next());
			
			while (i.hasNext()) {
				resultBuilder.append(separator).append(i.next());
			}
		}
		
		return resultBuilder.toString();
	}
	
	/**
	 * Returns the value associated with <code>key</code> in <code>map</code>;
	 * if necessary, the value is created using <code>valueFactory</code>.
	 * 
	 * @param map
	 * <br>Not null
	 * <br>Input-output
	 * @param key
	 * <br>Not null
	 * @param valueFactory
	 * <br>Not null
	 * @return
	 * <br>Maybe null
	 */
	@Deprecated
	public static final <K, V> V getOrCreate(final Map<K, V> map, final K key, final Factory<V> valueFactory) {
		V result = map.get(key);
		
		if (result == null) {
			try {
				result = valueFactory.newInstance();
			} catch (final Exception exception) {
				throw unchecked(exception);
			}
			
			map.put(key, result);
		}
		
		return result;
	}
	
	/**
	 * Tries to create a temporary file and initialize it using {@code contents}.
	 * {@code contents} is closed at the end of this method.
	 *
	 * @param prefix
	 * <br>Not null
	 * @param suffix
	 * <br>Not null
	 * @param contents
	 * <br>Not null
	 * <br>Input-output
	 * @return a temporary file that is deleted when the program exits
	 * <br>Maybe null
	 * <br>New
	 * @throws RuntimeException if<ul>
	 *  <li>the file cannot be created
	 *  <li>an I/O error occurs while writing {@code contents} to the file
	 * </ul>
	 */
	public static final File createTemporaryFile(final String prefix, final String suffix,
			final InputStream contents) {
		try {
			final File result = File.createTempFile(prefix, suffix);

			result.deleteOnExit();

			if (contents == null) {
				return result;
			}

			final OutputStream output = new FileOutputStream(result);

			try {
				write(contents, output);

				return result;
			} finally {
				close(output);
			}
		} catch (final IOException exception) {
			throw unchecked(exception);
		} finally {
			close(contents);
		}
	}
	
	/**
	 * Writes {@code input} to {@code output}; this method does NOT close the streams when it terminates.
	 *
	 * @param input
	 * <br>Not null
	 * <br>Input-output
	 * @param output
	 * <br>Not null
	 * <br>Input-output
	 * @throws RuntimeException if an I/O error occurs
	 */
	public static final void write(final InputStream input, final OutputStream output) {
		try {
			final byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} catch (final IOException exception) {
			throw unchecked(exception);
		}
	}
	
	/**
	 * Writes {@code input} to {@code output}; when it terminates
	 * , this method uses {@link #close(Object)} to close <code>input</code> if <code>closeInput</code> is <code>true</code>
	 * , and <code>output</code> if <code>closeOutput</code> is <code>true</code>.
	 *
	 * @param input
	 * <br>Not null
	 * <br>Input-output
	 * @param closeInput
	 * <br>Range: any boolean
	 * @param output
	 * <br>Not null
	 * <br>Input-output
	 * @param closeOutput
	 * <br>Range: any boolean
	 * @throws RuntimeException if an I/O error occurs during the writing phase
	 */
	public static final void writeAndClose(final InputStream input, final boolean closeInput, final OutputStream output, final boolean closeOutput) {
		try {
			write(input, output);
		} finally {
			if (closeInput) {
				close(input);
			}
			
			if (closeOutput) {
				close(output);
			}
		}
	}
	
	/**
	 * Writes {@code input} to {@code output}; this method CLOSES the OUTPUT stream when it terminates.
	 *
	 * @param input
	 * <br>Not null
	 * <br>Input-output
	 * @param output
	 * <br>Not null
	 * <br>Input-output
	 * @throws RuntimeException if an I/O error occurs
	 */
	@Deprecated
	public static final void writeAndCloseOutput(final InputStream input, final OutputStream output) {
		writeAndClose(input, false, output, true);
	}
	
	/**
	 * Tries to close {@code closable} using reflection and without throwing an exception if it fails.
	 * If an exception occurs, it is logged in the caller's logger.
	 *
	 * @param closable
	 * <br>Maybe null
	 */
	public static final void close(final Object closable) {
		try {
			if (closable != null) {
				invoke(closable, "close");
			}
		} catch (final Exception exception) {
			Logger.getLogger(getCallerClass().getName() + "." + getCallerMethodName())
					.log(Level.WARNING, null, exception);
		}
	}
	
	/**
	 * 
	 * @param cls
	 * <br>Not null
	 * @return
	 * <br>Not null
	 */
	public static final URL getClassRootURL(final Class<?> cls) {
		return cls.getProtectionDomain().getCodeSource().getLocation();
	}
	
	/**
	 * 
	 * @param cls
	 * <br>Not null
	 * @return
	 * <br>Not null
	 */
	public static final File getClassRoot(final Class<?> cls) {
		try {
			return new File(getClassRootURL(cls).toURI());
		} catch (final URISyntaxException exception) {
			throw unchecked(exception);
		}
	}
	
	/**
	 * Retrieves the application URL (it could be a folder, a compiled
	 * class file or a jar, depending on the packaging).
	 *
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final URL getApplicationURL() {
		return getClassRootURL(getCallerClass());
	}
	
	/**
	 * Retrieves the local file associated with the application URL (it could be a folder, a compiled
	 * class file or a jar, depending on the packaging).
	 * 
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final File getApplicationFile() {
		return getClassRoot(getCallerClass());
	}
	
	/**
	 * Returns the index of the last element of <code>elements</code>, or <code>-1</code> if <code>elements</code> is empty.
	 * 
	 * @param elements
	 * <br>Must not be null
	 * @return
	 * <br>Range: <code>{elements.length - 1}</code>
	 */
	public static final int lastIndex(final Object[] elements) {
		return elements.length - 1;
	}
	
	/**
	 * Returns the last element of <code>elements</code>.
	 * 
	 * @param <T> The type of the elements
	 * @param elements
	 * <br>Must not be null
	 * <br>Must have at least one element
	 * @return
	 * <br>Maybe null
	 */
	public static final <T> T last(final T... elements) {
		return elements[lastIndex(elements)];
	}
	
	/**
	 * Returns the index of the last element of <code>elements</code>, or <code>-1</code> if <code>elements</code> is empty.
	 * 
	 * @param elements
	 * <br>Must not be null
	 * @return
	 * <br>Range: <code>{elements.length - 1}</code>
	 */
	public static final int lastIndex(final List<?> elements) {
		return elements.size() - 1;
	}
	
	/**
	 * Returns the last element of <code>elements</code>.
	 * 
	 * @param <T> The type of the elements
	 * @param elements
	 * <br>Must not be null
	 * <br>Must have at least one element
	 * @return
	 * <br>Maybe null
	 */
	public static final <T> T last(final List<T> elements) {
		return elements.get(lastIndex(elements));
	}
	
	/**
	 *
	 * @param <T> The type of the elements
	 * @param iterable
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final <T> ArrayList<T> list(final Iterable<T> iterable) {
		final ArrayList<T> result = new ArrayList<T>();
		
		for (final T element : iterable) {
			result.add(element);
		}
		
		return result;
	}
	
	/**
	 *
	 * @param <T> The type of the elements
	 * @param enumeration
	 * <br>Not null
	 * <br>Input-output
	 * <br>Shared
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final <T> Iterable<T> iterable(final Enumeration<T> enumeration) {
		return new Iterable<T>() {
			
			@Override
			public final Iterator<T> iterator() {
				return new Iterator<T>() {
					
					@Override
					public final boolean hasNext() {
						return enumeration.hasMoreElements();
					}
					
					@Override
					public final T next() {
						return enumeration.nextElement();
					}
					
					@Override
					public final void remove() {
						throw new UnsupportedOperationException();
					}
					
				};
			}
			
		};
	}
	
	/**
	 *
	 * @param <T> The common type of the elements
	 * @param array
	 * <br>Maybe null
	 * @return
	 * <br>Maybe null
	 */
	public static final <T> T[] array(final T... array) {
		return array;
	}
	
	/**
	 * @param values
	 * <br>Maybe null
	 * @return <code>values</code>
	 * <br>Maybe null
	 */
	public static final boolean[] booleans(final boolean... values) {
		return values;
	}
	
	/**
	 * @param values
	 * <br>Maybe null
	 * @return <code>values</code>
	 * <br>Maybe null
	 */
	public static final char[] chars(final char... values) {
		return values;
	}
	
	/**
	 * @param values
	 * <br>Maybe null
	 * @return <code>values</code>
	 * <br>Maybe null
	 */
	public static final int[] ints(final int... values) {
		return values;
	}
	
	/**
	 * @param values
	 * <br>Maybe null
	 * @return <code>values</code>
	 * <br>Maybe null
	 */
	public static final long[] longs(final long... values) {
		return values;
	}
	
	/**
	 * @param values
	 * <br>Maybe null
	 * @return <code>values</code>
	 * <br>Maybe null
	 */
	public static final float[] floats(final float... values) {
		return values;
	}
	
	/**
	 * @param values
	 * <br>Maybe null
	 * @return <code>values</code>
	 * <br>Maybe null
	 */
	public static final double[] doubles(final double... values) {
		return values;
	}
	
	/**
	 *
	 * @param <T> The common type of the elements
	 * @param elements
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final <T> LinkedHashSet<T> set(final T... elements) {
		final LinkedHashSet<T> result = new LinkedHashSet<T>();

		for (final T element : elements) {
			result.add(element);
		}

		return result;
	}

	/**
	 *
	 * @param <T> The common type of the elements
	 * @param array
	 * <br>Not null
	 * @param moreElements
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final <T> T[] append(final T[] array, final T... moreElements) {
		@SuppressWarnings("unchecked")
		final T[] result = (T[]) Array.newInstance(
				array.getClass().getComponentType(), array.length + moreElements.length);

		System.arraycopy(array, 0, result, 0, array.length);
		System.arraycopy(moreElements, 0, result, array.length, moreElements.length);

		return result;
	}

	/**
	 *
	 * @param resourcePath
	 * <br>Not null
	 * @return
	 * <br>Maybe null
	 * <br>New
	 */
	public static final InputStream getResourceAsStream(final String resourcePath) {
		final Class<?> callerClass = getCallerClass();
		InputStream candidate = callerClass.getResourceAsStream(resourcePath);

		if (candidate == null) {
			candidate = getCallerClass().getClassLoader().getResourceAsStream(resourcePath);
		}

		if (candidate == null) {
			try {
				return new FileInputStream(resourcePath);
			} catch (final FileNotFoundException exception) {
				ignore(exception);
			}
		}

		return candidate;
	}

	/**
	 *
	 * @param resourcePath
	 * <br>Not null
	 * @return
	 * <br>Maybe null
	 * <br>New
	 */
	public static final URL getResourceURL(final String resourcePath) {
		final Class<?> callerClass = getCallerClass();
		URL candidate = callerClass.getResource(resourcePath);

		if (candidate == null) {
			candidate = getCallerClass().getClassLoader().getResource(resourcePath);
		}

		if (candidate == null) {
			try {
				final File file = new File(resourcePath);

				candidate = file.exists() ? file.toURI().toURL() : null;
			} catch (final MalformedURLException exception) {
				ignore(exception);
			}
		}

		return candidate;
	}

	/**
	 * Searches for and invokes a method named {@code methodName} that can accept {@code arguments}.
	 *
	 * @param <T> The expected return type
	 * @param objectOrClass
	 * <br>Not null
	 * @param methodName
	 * <br>Not null
	 * @param arguments
	 * <br>Not null
	 * @return
	 * <br>Maybe null
	 * @throws RuntimeException if an appropriate method isn't found or if it throws an exception
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T invoke(final Object objectOrClass,
			final String methodName, final Object... arguments) {
		final Object object = objectOrClass instanceof Class<?> ? null : objectOrClass;
		final Class<?> objectClass = (Class<?>) (objectOrClass instanceof Class<?> ? objectOrClass : objectOrClass.getClass());

		for (final Method method : append(objectClass.getMethods(), objectClass.getDeclaredMethods())) {
			if (method.getName().equals(methodName)) {
				try {
					method.setAccessible(true);

					return (T) method.invoke(object, arguments);
				} catch (final InvocationTargetException exception) {
					throwUnchecked(exception.getCause());
				} catch (final Exception exception) {
					ignore(exception);
				}
			}
		}

		throw new RuntimeException(
				"Method " + methodName + " accepting arguments " + Arrays.toString(arguments) +
				" was not found for object " + object + " of class " + objectClass);
	}

	/**
	 * Tries to find a setter starting with "set" for the specified property of the object.
	 * <br>Eg: {@code getSetter(object, "text", String.class)} tries to find a method {@code setText(String)}
	 *
	 * @param object
	 * <br>Should not be null
	 * @param propertyName
	 * <br>Should not be null
	 * @param propertyClass
	 * <br>Should not be null
	 * @return
	 * <br>A non-null value
	 * @throws RuntimeException if an appropriate setter cannot be retrieved
	 */
	public static final Method getSetter(final Object object, final String propertyName, final Class<?> propertyClass) {
		final String setterName = "set" + toUpperCamelCase(propertyName);

		try {
			// Try to retrieve a public setter
			return object.getClass().getMethod(setterName, propertyClass);
		} catch (final Exception exception) {
			// Do nothing
		}

		try {
			// Try to retrieve a setter declared in object's class, regardless of its visibility
			return object.getClass().getDeclaredMethod(setterName, propertyClass);
		} catch (final Exception exception) {
			// Do nothing
		}

		throw new RuntimeException("Unable to retrieve a getter for property " + propertyName);
	}

	/**
	 * Tries to find a getter starting with "get", "is", or "has" (in that order) for the specified property of the object.
	 * <br>Eg: {@code getGetter(object, "empty")} tries to find a method {@code getEmpty()} or {@code isEmpty()} or {@code hasEmpty()}
	 *
	 * @param object
	 * <br>Should not be null
	 * @param propertyName the camelCase name of the property
	 * <br>Should not be null
	 * @return
	 * <br>A non-null value
	 * @throws RuntimeException if an appropriate getter cannot be retrieved
	 */
	public static final Method getGetter(final Object object, final String propertyName) {
		final String upperCamelCase = toUpperCamelCase(propertyName);

		for (final String prefix : array("get", "is", "has")) {
			final String getterName = prefix + upperCamelCase;

			try {
				// Try to retrieve a public getter
				return object.getClass().getMethod(getterName);
			} catch (final Exception exception) {
				// Do nothing
			}

			try {
				// Try to retrieve a getter declared in object's class, regardless of its visibility
				return object.getClass().getDeclaredMethod(getterName);
			} catch (final Exception exception) {
				// Do nothing
			}
		}

		throw new RuntimeException("Unable to retrieve a getter for property " + propertyName);
	}

	/**
	 * Converts "someName" into "SomeName".
	 *
	 * @param lowerCamelCase
	 * <br>Should not be null
	 * @return
	 * <br>A new value
	 * <br>A non-null value
	 */
	public static final String toUpperCamelCase(final String lowerCamelCase) {
		return Character.toUpperCase(lowerCamelCase.charAt(0)) + lowerCamelCase.substring(1);
	}

	/**
	 * Converts {@code null} into "", otherwise returns the parameter untouched.
	 *
	 * @param string
	 * <br>Can be null
	 * <br>Shared parameter
	 * @return {@code string} or ""
	 * <br>A non-null value
	 * <br>A shared value
	 */
	public static final String emptyIfNull(final String string) {
		return string == null ? "" : string;
	}

	/**
	 * Returns "package/name/" if the package of {@code cls} is of the form "package.name".
	 *
	 * @param cls
	 * <br>Not null
	 * @return
	 * <br>Not null
	 */
	public static final String getPackagePath(final Class<?> cls) {
		return cls.getPackage().getName().replace(".", "/") + "/";
	}

	/**
	 * Returns "package/name/" if the package of the caller class is of the form "package.name".
	 *
	 * @return
	 * <br>Not null
	 */
	public static final String getThisPackagePath() {
		return getPackagePath(getCallerClass());
	}

	/**
	 *
	 * @param cls
	 * <br>Not null
	 * @return the top level class enclosing {@code cls}, or {@code cls} itself if it is a top level class
	 * <br>Not null
	 */
	public static final Class<?> getTopLevelEnclosingClass(final Class<?> cls) {
		return cls.getEnclosingClass() == null ? cls : getTopLevelEnclosingClass(cls.getEnclosingClass());
	}

	/**
	 * If a method {@code A.a()} calls a method {@code B.b()},
	 * then the result of calling this method in {@code b()} will be {@code A.class}.
	 * <br>Warning: this method can only be used directly.
	 * <br>If you want to refactor your code, you can re-implement the functionality
	 * using {@code Thread.currentThread().getStackTrace()}.
	 *
	 * @return {@code null} if the caller class cannot be retrieved
	 * <br>Maybe null
	 */
	public static final Class<?> getCallerClass() {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		if (stackTrace.length > DEBUG_STACK_OFFSET + 2) {
			try {
				return Class.forName(stackTrace[DEBUG_STACK_OFFSET + 2].getClassName());
			} catch (final ClassNotFoundException exception) {
				// Do nothing
			}
		}

		return null;
	}

	/**
	 * If a method {@code a()} calls a method {@code b()}, then the result of calling this method in b() will be "a".
	 * <br>Warning: this method can only be used directly.
	 * <br>If you want to refactor your code, you can re-implement the functionality using {@code Thread.currentThread().getStackTrace()}.
	 *
	 * @return {@code null} if the caller method cannot be retrieved
	 * <br>A possibly null value
	 */
	public static final String getCallerMethodName() {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		return stackTrace.length > 3 ? stackTrace[3].getMethodName() : null;
	}

	/**
	 * @return {@code null} if the method cannot be retrieved
	 * <br>A possibly null value
	 */
	public static final String getThisMethodName() {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		return stackTrace.length > 2 ? stackTrace[2].getMethodName() : null;
	}

	/**
	 * Calls {@link Logger#getLogger(String)} using the fully qualified name of the calling method.
	 * <br>Warning: this method can only be used directly.
	 * <br>If you want to refactor your code, you can re-implement the functionality using {@code Thread.currentThread().getStackTrace()}.
	 *
	 * @return
	 * <br>A non-null value
	 * @throws NullPointerException if the caller class cannot be retrieved
	 */
	public static final Logger getLoggerForThisMethod() {
		return Logger.getLogger(getCallerClass().getName() + "." + getCallerMethodName());
	}

	/**
	 * Use this method when you want to propagate a checked exception wrapped in a runtime exception
	 * instead of using the normal checked exception mechanism.
	 *
	 * @param <T> the type that the caller is supposed to return
	 * @param cause
	 * <br>Not null
	 * <br>Shared
	 * @return
	 * <br>Does not return
	 * @throws RuntimeException with {@code cause} as cause if it is a checked exception,
	 * otherwise {@code cause} is re-thrown
	 */
	public static final <T> T throwUnchecked(final Throwable cause) {
		if (cause instanceof Error) {
			throw (Error) cause;
		}

		throw unchecked(cause);
	}

	/**
	 * Returns an instance of {@link RuntimeException} which is either {@code cause} itself,
	 * if it is already a runtime exception, or a new runtime exception wrapping {@code cause}.
	 * <br>This method can be used as an alternative to {@link #throwUnchecked(java.lang.Throwable)},
	 * <br>with the difference that error types are wrapped.
	 * <br>It is up to the caller to decide what to do with the returned exception.
	 *
	 * @param cause
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>Maybe new
	 */
	public static final RuntimeException unchecked(final Throwable cause) {
		if (cause instanceof RuntimeException) {
			return (RuntimeException) cause;
		}

		return new RuntimeException(cause);
	}

	/**
	 * Does the same thing as {@link Class#cast(Object)},
	 * but returns {@code null} instead of throwing an exception if the cast cannot be performed.
	 *
	 * @param <T> the type into which {@code object} is tentatively being cast
	 * @param cls
	 * <br>Not null
	 * @param object
	 * <br>Maybe null
	 * @return {@code null} if {@code object} is {@code null} or cannot be cast into {@code T},
	 * otherwise {@code object}
	 * <br>Maybe null
	 */
	public static final <T> T cast(final Class<T> cls, final Object object) {
		if (object == null || !cls.isAssignableFrom(object.getClass())) {
			return null;
		}

		return cls.cast(object);
	}

	/**
	 * Warning: calling this method is much slower than using its equivalent <code>cast(this.getClass(), object)</code>.
	 * @param <T> the caller type
	 * @param object
	 * <br>Maybe null
	 * @return {@code null} if {@code object} is {@code null} or cannot be cast into the caller type
	 * (obtained using {@link #getCallerClass()}) , otherwise {@code object}
	 * <br>Maybe null
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T castToCurrentClass(final Object object) {
		return (T) cast(getCallerClass(), object);
	}

	/**
	 *
	 * @param object1
	 * <br>Maybe null
	 * @param object2
	 * <br>Maybe null
	 * @return {@code true} if both objects are the same (using {@code ==}) or equal (using {@code equals()})
	 */
	public static final boolean equals(final Object object1, final Object object2) {
		return object1 == object2 || (object1 != null && object1.equals(object2));
	}

	/**
	 *
	 * @param object
	 * <br>Maybe null
	 * @return {@code 0} if {@code object is null}, otherwise {@code object.hashcode()}
	 * <br>Range: any integer
	 */
	public static final int hashCode(final Object object) {
		return object == null ? 0 : object.hashCode();
	}

	/**
	 * Concatenates the source location of the call and
	 * the string representations of the parameters separated by spaces.
	 * <br>This is method helps to perform console debugging using System.out or System.err.
	 *
	 * @param stackOffset {@link #DEBUG_STACK_OFFSET} is the source of the call,
	 * {@code DEBUG_STACK_OFFSET + 1} is the source of the call's caller, and so forth
	 * <br>Range: {@code [O .. Integer.MAX_VALUE]}
	 * @param objects
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 * @throws IndexOutOfBoundsException if {@code stackIndex} is invalid
	 */
	public static final String debug(final int stackOffset, final Object... objects) {
		final StringBuilder builder = new StringBuilder(
				Thread.currentThread().getStackTrace()[stackOffset + 1].toString());

		for (final Object object : objects) {
			builder.append(" ").append(object);
		}

		return builder.toString();
	}
	
	/**
	 * Prints on the standard output the concatenation of the source location of the call
	 * and the string representations of the parameters separated by spaces.
	 *
	 * @param objects
	 * <br>Must not be null
	 */
	public static final synchronized void debugPrint(final Object... objects) {
		getDebugOutput().println(debug(DEBUG_STACK_OFFSET + 1, objects));
		getDebugOutput().flush();
	}
	
	/**
	 * Prints on the error output the concatenation of the source location of the call
	 * and the string representations of the parameters separated by spaces.
	 *
	 * @param objects
	 * <br>Must not be null
	 */
	public static final synchronized void debugError(final Object... objects) {
		getDebugErrorOutput().println(debug(DEBUG_STACK_OFFSET + 1, objects));
		getDebugErrorOutput().flush();
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <br>Strong reference
	 */
	public static final synchronized PrintStream getDebugOutput() {
		return debugOutput;
	}
	
	public static final synchronized void setDebugOutput(final PrintStream output) {
		debugOutput = notNull(output);
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <br>Strong reference
	 */
	public static final synchronized PrintStream getDebugErrorOutput() {
		return debugErrorOutput;
	}
	
	/**
	 * @param output
	 * <br>Must not be null
	 * <br>Will be stored as strong reference
	 */
	public static final synchronized void setDebugErrorOutput(final PrintStream output) {
		debugErrorOutput = notNull(output);
	}
	
	/**
	 * @param output
	 * <br>Must not be null
	 * <br>Will be stored as strong reference
	 */
	public static final synchronized void setDebugOutputs(final PrintStream output) {
		setDebugOutput(output);
		setDebugErrorOutput(output);
	}
	
	/**
	 * @param output
	 * <br>Must not be null
	 * <br>Will be stored as string reference
	 */
	public static final synchronized void teeDebugOutputs(final OutputStream output) {
		notNull(output);
		
		setDebugOutput(new PrintStream(new Tee(getDebugOutput(), output)));
		setDebugErrorOutput(new PrintStream(new Tee(getDebugErrorOutput(), output)));
	}
	
	/**
	 * @param object
	 * <br>Must not be null
	 * @return <code>object</code>
	 * @throws NullPointerException if <code>object</code> is <code>null</code>
	 */
	public static final <T> T notNull(final T object) {
		if (object == null) {
			throw new NullPointerException();
		}
		
		return object;
	}
	
	/**
	 *
	 * @return
	 * <br>Range: {@code [0 .. Integer.MAX_VALUE]}
	 */
	private static final int getDebugStackOffset() {
		int result = 0;

		for (final StackTraceElement element : Thread.currentThread().getStackTrace()) {
			if (element.getClassName().equals(Tools.class.getName())) {
				break;
			}

			++result;
		}

		return result;
	}
	
	/**
	 * This class delegates all calls to <code>System.out</code>.
	 * 
	 * @author codistmonk (creation 2014-06-03)
	 */
	public static final class SystemOutOutputStream extends OutputStream implements Serializable {
		
		@Override
		public final void write(final int b) throws IOException {
			System.out.write(b);
		}
		
		@Override
		public final void write(final byte[] bytes) throws IOException {
			System.out.write(bytes);
		}

		@Override
		public final void write(final byte[] bytes, final int offset, final int length) throws IOException {
			System.out.write(bytes, offset, length);
		}
		
		@Override
		public final void flush() throws IOException {
			System.out.flush();
		}
		
		@Override
		public final void close() throws IOException {
			System.out.close();
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = -3530080431199360807L;
		
	}
	
	/**
	 * This class delegates all calls to <code>System.err</code>.
	 *  
	 * @author codistmonk (creation 2014-06-03)
	 */
	public static final class SystemErrOutputStream extends OutputStream implements Serializable {
		
		@Override
		public final void write(final int b) throws IOException {
			System.err.write(b);
		}
		
		@Override
		public final void write(final byte[] bytes) throws IOException {
			System.err.write(bytes);
		}
		
		@Override
		public final void write(final byte[] bytes, final int offset, final int length) throws IOException {
			System.err.write(bytes, offset, length);
		}
		
		@Override
		public final void flush() throws IOException {
			System.err.flush();
		}
		
		@Override
		public final void close() throws IOException {
			System.err.close();
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = -656567263773327906L;
		
	}
	
}
