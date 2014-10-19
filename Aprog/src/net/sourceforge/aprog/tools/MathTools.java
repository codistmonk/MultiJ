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

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.Serializable;

/**
 * @author codistmonk (creation 2011-06-11)
 */
public final class MathTools {
    
    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private MathTools() {
        throw new IllegalInstantiationException();
    }
    
    // TODO Add tests
    
    private static final long[] FIRST_FACTORIALS = {
        1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L
    };
	
	/**
	 * @param a
	 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
	 * @param b
	 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
	 * @return
	 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
	 */
	public static final int gcd(final int a, final int b) {
		if (a == 0 || a == b) {
			return b;
		}
		
		if (b == 0) {
			return a;
		}
		
		int a1 = a;
		int b1 = b;
		
		while (true) {
			if (a1 < b1) {
				b1 %= a1;
				
				if (b1 == 0) {
					return a1;
				}
			} else {
				a1 %= b1;
				
				if (a1 == 0) {
					return b1;
				}
			}
		}
	}
    
    /**
     * @param a
     * <br>Range: any long
     * @param b
     * <br>Range: any long
     * @return
     * <br>Range: any long
     */
    public static final long gcd(final long a, final long b) {
        return b == 0 ? a : gcd(b, a % b);
    }
    
    /**
     * @param a
     * <br>Range: any long
     * @param b
     * <br>Range: any long
     * @return
     * <br>Range: any long
     */
    public static final long lcm(final long a, final long b) {
        return a * b / gcd(a, b);
    }
    
    /**
     * @param n
     * <br>Range: any long
     * @return
     * <br>Range: <code>[1 .. Long.MAX_VALUE]</code>
     */
    public static final long factorial(final long n) {
        return n < FIRST_FACTORIALS.length ? FIRST_FACTORIALS[(int) n] : nPk(n, n);
    }
    
    /**
     * @param n
     * <br>Range: any long
     * @param k
     * <br>Range: any long
     * @return <code>n!/(n-k)!</code>
     * <br>Range: <code>[0 .. Long.MAX_VALUE]</code>
     */
    public static final long nPk(final long n, final long k) {
        if (n < k) {
            return 1;
        }

        long result = 1;

        for (long i = n - k + 1; i <= n; ++i) {
            result *= i;
        }

        return result;
    }
    
    /**
     * "gamma nk" or "n multichoose k".
     *
     * @param n
     * <br>Range: any long
     * @param k
     * <br>Range: any long
     * @return <code>nCk(n + k - 1, k)</code>
     * <br>Range: <code>[0 .. Long.MAX_VALUE]</code>
     */
    public static final long multichoose(final long n, final long k) {
        return nCk(n + k - 1, k);
    }
    
    /**
     * "n choose k".
     * 
     * @param n
     * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
     * @param k
     * <br>Range: <code>[0L .. n]</code>
     * @return <code>n!/(k!(n-k)!)</code>
     * <br>Range: <code>[0L .. Long.MAX_VALUE]</code>
     */
    public static final long nCk(final long n, final long k) {
        final long m = min(k, n - k);
        
        if (m == 0) {
            return 1L;
        }
        
        long numerator = n - m + 1L;
        long result = numerator;
        
        for (long i = 2L; i <= m; ++i) {
            result = result * (++numerator) / i;
        }
        
        return result;
    }
    
    /**
     * @param value
     * <br>Range: any double
     * @return
     * <br>Range: any double
     */
    public static final double square(final double value) {
        return value * value;
    }
    
    /**
     * @author codistmonk (creation 2012-06-20)
     */
    public static final class Statistics implements Serializable {
    	
		private double sum;
        
        private double sumOfSquares;
        
        private double count;
        
        private double minimum;
        
        private double maximum;
        
        public Statistics() {
            this.reset();
        }
        
        public final void reset() {
            this.sum = +0.0;
            this.sumOfSquares = +0.0;
            this.count = 0;
            this.minimum = Double.POSITIVE_INFINITY;
            this.maximum = Double.NEGATIVE_INFINITY;
        }
        
        /**
         * @param statistics
         * <br>Not null
         */
        public final void addAll(final Statistics statistics) {
        	this.sum += statistics.getSum();
        	this.sumOfSquares += statistics.getSumOfSquares();
        	this.count += statistics.getCount();
        	
        	if (statistics.getMinimum() < this.getMinimum()) {
        		this.minimum = statistics.getMinimum();
        	}
        	
        	if (this.getMaximum() < statistics.getMaximum()) {
        		this.maximum = statistics.getMaximum();
        	}
        }
        
        /**
         * @param value
         * <br>Range: <code>[-Double.MAX_VALUE .. Double.MAX_VALUE]</code>
         */
        public final void addValue(final double value) {
        	this.addValue(value, 1.0);
        }
        
        /**
         * @param value
         * <br>Range: <code>[-Double.MAX_VALUE .. Double.MAX_VALUE]</code>
         * @param count
         * <br>Range: <code>[0.0 .. Double.MAX_VALUE]</code>
         */
        public final void addValue(final double value, final double count) {
        	this.sum += count * value;
        	this.sumOfSquares += count * square(value);
        	this.count += count;
        	this.minimum = min(this.getMinimum(), value);
        	this.maximum = max(this.getMaximum(), value);
        }
        
        /**
         * @return
         * <br>Range: <code>[-Double.MAX_VALUE .. Double.MAX_VALUE]</code>
         */
        public final double getSum() {
            return this.sum;
        }
        
        /**
         * @return
         * <br>Range: <code>[-Double.MAX_VALUE .. Double.MAX_VALUE]</code>
         */
        public final double getSumOfSquares() {
            return this.sumOfSquares;
        }
        
        /**
         * @return
         * <br>Range: <code>[0.0 .. Double.MAX_VALUE]</code>
         */
        public final double getCount() {
            return this.count;
        }
        
        /**
         * @return
         * <br>Range: <code>[-Double.MAX_VALUE .. Double.POSITIVE_INFINITY]</code>
         */
        public final double getMinimum() {
            return this.minimum;
        }
        
        /**
         * @return
         * <br>Range: <code>[Double.NEGATIVE_INFINITY .. Double.MAX_VALUE]</code>
         */
        public final double getMaximum() {
            return this.maximum;
        }
        
        /**
         * @return
         * <br>Range: <code>[0 .. Double.POSITIVE_INFINITY]</code>
         */
        public final double getAmplitude() {
            return this.getMaximum() - this.getMinimum();
        }
        
        /**
         * @param value
         * <br>Range: <code>[0.0 .. 1.0]</code>
         * @return
         * <br>Range: <code>[this.getMinimum() .. this.getMaximum()]</code>
         */
        public final double getDenormalizedValue(final double value) {
            return value * this.getAmplitude() + this.getMinimum();
        }
        
        /**
         * @param value
         * <br>Range: <code>[this.getMinimum() .. this.getMaximum()]</code>
         * @return
         * <br>Range: <code>[0.0 .. 1.0]</code>
         */
        public final double getNormalizedValue(final double value) {
            return (value - this.getMinimum()) / this.getAmplitude();
        }
        
        /**
         * @return
         * <br>Range: <code>[this.getMinimum() .. this.getMaximum()]</code>
         */
        public final double getMean() {
            return this.getSum() / this.getCount();
        }
        
        /**
         * @return
         * <br>Range: <code>[0 .. Double.MAX_VALUE]</code>
         */
        public final double getVariance() {
            return this.getSumOfSquares() / this.getCount() - square(this.getMean());
        }
        
        /**
		 * {@value}.
		 */
		private static final long serialVersionUID = -4510391899140592101L;
        
		public static final Factory<Statistics> FACTORY = Factory.DefaultFactory.forClass(Statistics.class);
		
    }
    
}
