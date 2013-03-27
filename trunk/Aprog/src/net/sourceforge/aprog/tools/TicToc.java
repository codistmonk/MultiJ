package net.sourceforge.aprog.tools;

import static java.lang.System.currentTimeMillis;

/**
 * Simple time measuring class to manually instrument blocks of code.
 * <br>Call {@link #tic()} at the beginning and {@link #toc()} at the end.
 * <br>{@link #getTotalTime()} gives the total time (cumulative) spent in {@link #tic()} {@link #toc()} intervals.
 * 
 * @author codistmonk (creation 2013-01-25)
 */
public final class TicToc {
	
	private long totalTime;
	
	private long ticTocTime;
	
	private long t0;
	
	/**
	 * Starts a time interval.
	 * 
	 * @return The current time in milliseconds
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
		this.ticTocTime = currentTimeMillis() - this.t0;
		
		return this.ticTocTime;
	}
	
	/**
	 * @return The cumulative time of this timer (sum of tic toc intervals)
	 */
	public final long getTotalTime() {
		return this.totalTime + this.ticTocTime;
	}
	
}
