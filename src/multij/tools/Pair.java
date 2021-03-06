/*
 *  The MIT License
 * 
 *  Copyright 2012 Codist Monk.
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

import java.io.Serializable;

/**
 * @author codistmonk (creation 2012-06-16)
 * @param <F> The first element type
 * @param <S> The second element type
 */
public final class Pair<F, S> implements Serializable {
	
	private final F first;
	
	private final S second;
	
	/**
	 * @param first
     * <br>Maybe null
	 * @param second
	 * <br>Maybe null
	 */
	public Pair(final F first, final S second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * @return
     * <br>Maybe null
	 */
	public final F getFirst() {
		return this.first;
	}
	
    /**
     * @return
     * <br>Maybe null
     */
	public final S getSecond() {
		return this.second;
	}
	
	@Override
	public final int hashCode() {
		return Tools.hashCode(this.getFirst()) + Tools.hashCode(this.getSecond());
	}
	
	@Override
	public final boolean equals(final Object object) {
		@SuppressWarnings("unchecked")
		final Pair<F, S> that = Tools.cast(this.getClass(), object);
		
		return that != null && Tools.equals(this.getFirst(), that.getFirst())
				&& Tools.equals(this.getSecond(), that.getSecond());
	}

	@Override
	public final String toString() {
		return "(" + this.getFirst() + " " + this.getSecond() + ")";
	}

	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = -628218309847037531L;
	
}
