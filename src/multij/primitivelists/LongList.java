/* MACHINE-GENERATED FILE */
package multij.primitivelists;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static multij.tools.Tools.DEBUG_STACK_OFFSET;
import static multij.tools.Tools.debug;
import static multij.tools.Tools.gc;
import static multij.tools.Tools.ignore;

import java.io.Serializable;
import java.util.Arrays;

import multij.tools.Factory.DefaultFactory;

/**
 * @author codistmonk (creation 2013-01-24)
 */
public final class LongList implements PrimitiveList {
	
	private long[] values;
	
	private int first;
	
	private int end;
	
	private boolean beingTraversed;
	
	public LongList() {
		this(16);
	}
	
	public LongList(final int initialCapacity) {
		this(new long[initialCapacity], 0, 0);
	}
	
	public LongList(final long[] values) {
		this(values, 0, values.length);
	}
	
	public LongList(final long[] values, final int first, final int end) {
		this.values = values;
		this.first = first;
		this.end = end;
	}
	
	@Override
	public final LongList clear() {
		this.first = 0;
		this.end = 0;
		
		return this;
	}
	
	@Override
	public final int size() {
		return this.end - this.first;
	}
	
	public final LongList add(final long value) {
		if (this.values.length <= this.end) {
			if (0 < this.first) {
				System.arraycopy(this.values, this.first, this.values, 0, this.size());
				this.end -= this.first;
				this.first = 0;
			} else {
				final int newBufferSize = (int) min(Integer.MAX_VALUE, max(1, 2L * this.size()));
				
				try {
					try {
						this.values = copyOf(this.values, newBufferSize);
					} catch (final OutOfMemoryError error) {
						ignore(error);
						
						gc(10L);
						
						this.values = copyOf(this.values, newBufferSize);
					}
				} catch (final OutOfMemoryError error) {
					System.err.println(debug(DEBUG_STACK_OFFSET, "Failed to allocate", newBufferSize, this.values.getClass().getComponentType().getSimpleName() + "s"));
					
					throw error;
				}
			}
		}
		
		this.values[this.end++] = value;
		
		return this;
	}
	
	public final LongList addAll(final long... values) {
		for (final long value : values) {
			this.add(value);
		}
		
		return this;
	}
	
	public final long get(final int index) {
		this.checkIndex(index);
		
		return this.values[this.first + index];
	}
	
	public final LongList set(final int index, final long value) {
		this.checkIndex(index);
		
		this.values[this.first + index] = value;
		
		return this;
	}
	
	@Override
	public final boolean isBeingTraversed() {
		return this.beingTraversed;
	}
	
	@Override
	public final LongList resize(final int newSize) {
		if (newSize < 0) {
			throw new IllegalArgumentException();
		}
		
		if (this.values.length < newSize) {
			final long[] newValues = new long[newSize];
			System.arraycopy(this.values, this.first, newValues, 0, this.size());
			this.values = newValues;
			this.first = 0;
		} else {
			if (0 < this.first) {
				System.arraycopy(this.values, this.first, this.values, 0, this.size());
				this.first = 0;
			}
		}
		
		this.end = this.first + newSize;
		
		return this;
	}
	
	@Override
	public final LongList pack() {
		if (this.values.length != this.size()) {
			this.values = copyOfRange(this.values, this.first, this.end);
			this.first = 0;
			this.end = this.values.length;
		}
		
		return this;
	}
	
	public final long remove(final int index) {
		this.checkIndex(index);
		
		if (index == 0) {
			return this.values[this.first++];
		}
		
		final long result = this.get(index);
		
		System.arraycopy(this.values, this.first + index + 1, this.values, this.first + index, this.size() - 1 - index);
		--this.end;
		
		return result;
	}
	
	@Override
	public final boolean isEmpty() {
		return this.size() <= 0;
	}
	
	@Override
	public final LongList sort() {
		Arrays.sort(this.values, this.first, this.end);
		
		return this;
	}
	
	public final long[] toArray() {
		return this.pack().values;
	}
	
	public final LongList forEach(final Processor processor) {
		this.beingTraversed = true;
		
		try {
			for (int first = this.first, i = first; i < this.end; i += 1 + this.first - first, first = this.first) {
				if (!processor.process(this.values[i])) {
					break;
				}
			}
		} finally {
			this.beingTraversed = false;
		}
		
		return this;
	}
	
	@Override
	public final String toString() {
		final StringBuilder resultBuilder = new StringBuilder();
		
		resultBuilder.append('[');
		
		if (!this.isEmpty()) {
			resultBuilder.append(this.get(0));
			
			final int n = this.size();
			
			for (int i = 1; i < n; ++i) {
				resultBuilder.append(' ').append(this.get(i));
			}
		}
		
		resultBuilder.append(']');
		
		return resultBuilder.toString();
	}
	
	@Override
	public final void checkIndex(final int index) {
		if (index < 0 || this.size() <= index) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
	}
	
	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = -4559136848618882482L;
	
	public static final DefaultFactory<LongList> FACTORY = DefaultFactory.forClass(LongList.class);
	
	/**
	 * @author codistmonk (creation 2013-04-27)
	 */
	public static abstract interface Processor extends Serializable {
		
		public abstract boolean process(long value);
		
	}
	
}
