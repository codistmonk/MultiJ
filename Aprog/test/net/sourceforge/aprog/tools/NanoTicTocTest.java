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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link TicToc}.
 *
 * @author codistmonk (creation 2014-09-30)
 */
public final class NanoTicTocTest {
	
	@Test
	public final void testGetTotalTime() {
		final NanoTicToc timer = new NanoTicToc();
		long time;
		final long sleepMilliseconds = 500L;
		final long sleepNanoseconds = sleepMilliseconds * 1_000_000L;
		final long upperLimit = sleepNanoseconds + 200_000_000L;
		
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
