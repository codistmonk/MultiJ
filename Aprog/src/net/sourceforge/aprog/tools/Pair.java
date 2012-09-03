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

package net.sourceforge.aprog.tools;

/**
 * @author codistmonk (creation 2012-06-16)
 * @param <F> The first element type
 * @param <S> The second element type
 */
public final class Pair<F, S> {
	
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
	
}
