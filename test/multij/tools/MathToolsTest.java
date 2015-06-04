/*
 *  The MIT License
 * 
 *  Copyright 2011 Codist Monk.
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

import static multij.tools.Tools.doubles;
import static org.junit.Assert.*;
import multij.tools.MathTools;
import multij.tools.MathTools.Statistics;
import multij.tools.MathTools.VectorStatistics;

import org.junit.Test;

/**
 * Automated tests using JUnit for {@link MathTools}.
 * 
 * @author codistmonk (creation 2011-09-02)
 */
public final class MathToolsTest {
	
	// TODO add more tests
	
	@Test
	public final void testGcd() {
		assertEquals(4L, MathTools.gcd(8L, 12L));
		assertEquals(4, MathTools.gcd(8, 12));
	}
	
	@Test
	public final void testLcm() {
		assertEquals(24L, MathTools.lcm(6L, 8L));
	}
	
	@Test
	public final void testFactorial() {
		assertEquals(6L, MathTools.factorial(3L));
	}
	
	@Test
	public final void testNPk() {
		assertEquals(12L, MathTools.nPk(4L, 2L));
	}
	
	@Test
	public void testMultichoose() {
		assertEquals(126L, MathTools.multichoose(5L, 5L));
	}
	
	@Test
	public final void testNCk() {
		assertEquals(1L, MathTools.nCk(0L, 0L));
		assertEquals(1L, MathTools.nCk(4L, 0L));
		assertEquals(6L, MathTools.nCk(4L, 2L));
		assertEquals(1L, MathTools.nCk(4L, 4L));
	}
	
	@Test
	public final void testSquare() {
		assertEquals(9L, MathTools.square(3));
		assertEquals(9L, MathTools.square(3L));
		assertEquals(9.0, MathTools.square(3F), 0.0);
		assertEquals(9.0, MathTools.square(3.0), 0.0);
	}
	
	@Test
	public final void testDet() {
		assertEquals(-2.0, MathTools.det(1.0, 2.0, 3.0, 4.0), 0.0);
	}
	
	@Test
	public final void testDot() {
		assertEquals(11.0, MathTools.dot(doubles(1.0, 2.0), doubles(3.0, 4.0)), 0.0);
	}
	
	@Test
	public final void testAdd() {
		assertArrayEquals(doubles(0.0, 0.0, 26.0, 31.0),
				MathTools.add(2, 1.0, doubles(2.0, 3.0), 0, 4.0, doubles(5.0, 6.0, 7.0), 1, new double[4], 2), 0.0);
		assertArrayEquals(doubles(26.0, 31.0),
				MathTools.add(1.0, doubles(2.0, 3.0), 4.0, doubles(6.0, 7.0), new double[2]), 0.0);
		assertArrayEquals(doubles(26.0, 31.0),
				MathTools.add(1.0, doubles(2.0, 3.0), 4.0, doubles(6.0, 7.0)), 0.0);
		assertArrayEquals(doubles(8.0, 10.0),
				MathTools.add(doubles(2.0, 3.0), doubles(6.0, 7.0), new double[2]), 0.0);
		assertArrayEquals(doubles(8.0, 10.0),
				MathTools.add(doubles(2.0, 3.0), doubles(6.0, 7.0)), 0.0);
	}
	
	@Test
	public final void testStatistics1() {
		final Statistics statistics = new Statistics();
		
		assertEquals(+0.0, statistics.getCount(), +0.0);
		assertEquals(+0.0, statistics.getSum(), +0.0);
		assertEquals(+0.0, statistics.getSumOfSquares(), +0.0);
		assertEquals(Double.NaN, statistics.getMean(), +0.0);
		assertEquals(Double.POSITIVE_INFINITY, statistics.getMinimum(), +0.0);
		assertEquals(Double.NEGATIVE_INFINITY, statistics.getMaximum(), +0.0);
		
		statistics.addValue(+1.0);
		statistics.addValue(+2.0);
		
		assertEquals(+2.0, statistics.getCount(), +0.0);
		assertEquals(+3.0, statistics.getSum(), +0.0);
		assertEquals(+5.0, statistics.getSumOfSquares(), +0.0);
		assertEquals(+1.5, statistics.getMean(), +0.0);
		assertEquals(+1.0, statistics.getMinimum(), +0.0);
		assertEquals(+2.0, statistics.getMaximum(), +0.0);
		assertEquals(+1.0, statistics.getAmplitude(), +0.0);
		assertEquals(+0.0, statistics.getNormalizedValue(+1.0), +0.0);
		assertEquals(+1.0, statistics.getNormalizedValue(+2.0), +0.0);
		assertEquals(+1.0, statistics.getDenormalizedValue(+0.0), +0.0);
		assertEquals(+2.0, statistics.getDenormalizedValue(+1.0), +0.0);
		assertEquals(+0.25, statistics.getVariance(), +0.0);
		
		statistics.reset();
		
		assertEquals(+0.0, statistics.getCount(), +0.0);
		assertEquals(+0.0, statistics.getSum(), +0.0);
		assertEquals(+0.0, statistics.getSumOfSquares(), +0.0);
		assertEquals(Double.NaN, statistics.getMean(), +0.0);
		assertEquals(Double.POSITIVE_INFINITY, statistics.getMinimum(), +0.0);
		assertEquals(Double.NEGATIVE_INFINITY, statistics.getMaximum(), +0.0);
	}
	
	@Test
	public final void testStatistics2() {
		final Statistics statistics = new Statistics();
		final Statistics moreStatistics = new Statistics();
		
		statistics.addValue(0.0);
		statistics.addValue(2.0);
		moreStatistics.addValue(4.0);
		moreStatistics.addValue(6.0);
		statistics.addAll(moreStatistics);
		
		assertEquals(3.0, statistics.getMean(), 0.0);
		assertEquals(0.0, statistics.getMinimum(), 0.0);
		assertEquals(6.0, statistics.getMaximum(), 0.0);
		assertEquals(4.0, statistics.getCount(), 0.0);
	}
	
	@Test
	public final void testVectorStatistics() {
		final VectorStatistics statistics = new VectorStatistics(2);
		
		statistics.addValues(0.0, 0.0);
		statistics.addValues(-1.0, 2.0);
		
		assertArrayEquals(doubles(-0.5, 1.0), statistics.getMeans(), 0.0);
		assertArrayEquals(doubles(-1.0, 0.0), statistics.getMinima(), 0.0);
		assertArrayEquals(doubles(0.0, 2.0), statistics.getMaxima(), 0.0);
		assertArrayEquals(doubles(1.0, 2.0), statistics.getAmplitudes(), 0.0);
	}
	
}