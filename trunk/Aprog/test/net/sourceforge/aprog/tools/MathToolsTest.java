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

package net.sourceforge.aprog.tools;

import static org.junit.Assert.*;

import net.sourceforge.aprog.tools.MathTools.Statistics;

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
    public final void testStatistics() {
        final Statistics statistics = new Statistics();
        
        assertEquals(0, statistics.getCount());
        assertEquals(+0.0, statistics.getSum(), +0.0);
        assertEquals(+0.0, statistics.getSumOfSquares(), +0.0);
        assertEquals(Double.NaN, statistics.getMean(), +0.0);
        assertEquals(Double.POSITIVE_INFINITY, statistics.getMinimum(), +0.0);
        assertEquals(Double.NEGATIVE_INFINITY, statistics.getMaximum(), +0.0);
        
        statistics.addValue(+1.0);
        statistics.addValue(+2.0);
        
        assertEquals(2, statistics.getCount());
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
        
        assertEquals(0, statistics.getCount());
        assertEquals(+0.0, statistics.getSum(), +0.0);
        assertEquals(+0.0, statistics.getSumOfSquares(), +0.0);
        assertEquals(Double.NaN, statistics.getMean(), +0.0);
        assertEquals(Double.POSITIVE_INFINITY, statistics.getMinimum(), +0.0);
        assertEquals(Double.NEGATIVE_INFINITY, statistics.getMaximum(), +0.0);
    }

}