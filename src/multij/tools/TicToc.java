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

package multij.tools;

import static java.lang.System.currentTimeMillis;

import java.io.Serializable;

/**
 * Simple time measuring class to manually instrument blocks of code.
 * <br>Call {@link #tic()} at the beginning and {@link #toc()} at the end.
 * <br>{@link #getTotalTime()} gives the total time (cumulative) spent in {@link #tic()} {@link #toc()} intervals.
 * 
 * @author codistmonk (creation 2013-01-25)
 */
public final class TicToc implements Serializable {
	
	private long totalTime;
	
	private long ticTocTime;
	
	private long t0 = currentTimeMillis();
	
	/**
	 * Sets total time to {@code 0L}.
	 * 
	 * @return {@code this}
	 */
	public final TicToc reset() {
		this.totalTime = 0L;
		this.ticTocTime = 0L;
		
		return this;
	}
	
	/**
	 * Starts a time interval.
	 * 
	 * @return The current time in milliseconds
	 * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
	 */
	public final long tic() {
		this.totalTime += this.ticTocTime;
		this.ticTocTime = 0L;
		
		return this.t0 = currentTimeMillis();
	}
	
	/**
	 * @return The time in milliseconds since the last call to {@link #tic()}
	 */
	public final long toc() {
		return this.ticTocTime = currentTimeMillis() - this.t0;
	}
	
	/**
	 * Calls {@link #toc()} and then {@link #tic()}.
	 * 
	 * @return The result of the call to {@link #toc()}
	 * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
	 */
	public final long toctic() {
		final long result = this.toc();
		
		this.tic();
		
		return result;
	}
	
	/**
	 * @return The cumulative time of this timer (sum of tic toc intervals)
	 * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
	 */
	public final long getTotalTime() {
		return this.totalTime + this.ticTocTime;
	}
	
	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = -3905069032704896507L;
	
}
