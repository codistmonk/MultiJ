package net.sourceforge.aprog.tools;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link TicToc}.
 *
 * @author codistmonk (creation 2013-02-03)
 */
public class TicTocTest {
	
	@Test
	public final void testGetTotalTime() {
		final TicToc timer = new TicToc();
		long time;
		final long sleepMilliseconds = 500L;
		final long upperLimit = sleepMilliseconds + 200L;
		
		timer.tic();
		Tools.gc(sleepMilliseconds);
		time = timer.toc();
		
		assertTrue(sleepMilliseconds <= time && time < upperLimit);
		assertTrue(sleepMilliseconds <= timer.getTotalTime() && timer.getTotalTime() < upperLimit);
		
		Tools.gc(sleepMilliseconds);
		
		timer.tic();
		Tools.gc(sleepMilliseconds);
		time = timer.toc();
		
		assertTrue(sleepMilliseconds <= time && time < upperLimit);
		assertTrue(2L * sleepMilliseconds <= timer.getTotalTime() && timer.getTotalTime() < 2L * upperLimit);
	}
	
}
