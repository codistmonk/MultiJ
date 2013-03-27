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
		
		timer.tic();
		Tools.gc(500L);
		time = timer.toc();
		
		assertTrue(500L <= time && time < 600L);
		assertTrue(500L <= timer.getTotalTime() && timer.getTotalTime() < 600L);
		
		Tools.gc(500L);
		
		timer.tic();
		Tools.gc(500L);
		time = timer.toc();
		
		assertTrue(500L <= time && time < 600L);
		assertTrue(1000L <= timer.getTotalTime() && timer.getTotalTime() < 1200L);
	}
	
}
