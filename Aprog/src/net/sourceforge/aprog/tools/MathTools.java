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
import static net.sourceforge.aprog.tools.Tools.instances;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

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
     * <br>Range: any int
     * @return
     * <br>Range: any int
     */
    public static final int square(final int value) {
    	return value * value;
    }
    
    /**
     * @param value
     * <br>Range: any long
     * @return
     * <br>Range: any long
     */
    public static final long square(final long value) {
    	return value * value;
    }
    
    /**
     * @param value
     * <br>Range: any float
     * @return
     * <br>Range: any float
     */
    public static final float square(final float value) {
    	return value * value;
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
     * @param a
     * <br>Range: any double
     * @param b
     * <br>Range: any double
     * @param c
     * <br>Range: any double
     * @param d
     * <br>Range: any double
     * @return <code>a * d - b * c</code>
     * <br>Range: any double
     */
    public static final double det(final double a, final double b, final double c, final double d) {
    	return a * d - b * c;
    }
    
    /**
     * <code>result[resultOffset .. resultOffset + n - 1] =
     * <br>scale1 * v1[offset1 .. offset1 + n - 1] + scale2 * v2[offset2 .. offset2 + n - 1]</code>.
     * 
     * @param n
     * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
     * @param scale1
     * <br>Range: any double
     * <br>Must not be null
     * @param v1
     * <br>Size range: <code>[n .. Integer.MAX_VALUE]</code>
     * @param offset1
     * <br>Range: <code>[0 .. v1.length - n]</code>
     * @param scale2
     * <br>Range: any double
     * @param v2
     * <br>Must not be null
     * <br>Size range: <code>[n .. Integer.MAX_VALUE]</code>
     * @param offset2
     * <br>Range: <code>[0 .. v2.length - n]</code>
     * @param result
     * <br>Must not be null
     * <br>Size range: <code>[n .. Integer.MAX_VALUE]</code>
     * @param resultOffset
     * <br>Range: <code>[0 .. result.length - n]</code>
     * @return <code>result</code>
     * <br>Not null
     * <br>Not new
     */
    public static final double[] add(final int n,
    		final double scale1, final double[] v1, final int offset1,
    		final double scale2, final double[] v2, final int offset2,
    		final double[] result, final int resultOffset) {
    	for (int i = 0; i < n; ++i) {
    		result[resultOffset + i] = scale1 * v1[offset1 + i] + scale2 * v2[offset2 + i];
    	}
    	
    	return result;
    }
    
    /**
     * Inner product of <code>v1</code> and <code>v2</code>.
     * 
     * @param v1
     * <br>Must not be null
     * <br>Size range: <code>[0 .. Integer.MAX_VALUE]</code>
     * @param v2
     * <br>Must not be null
     * <br>Size range: <code>{v1.length}</code>
     * @return
     * <br>Range: any double
     */
    public static final double dot(final double[] v1, final double[] v2) {
    	final int n = v1.length;
    	double result = 0.0;
    	
    	for (int i = 0; i < n; ++i) {
    		result += v1[i] * v2[i];
    	}
    	
    	return result;
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
    
	/**
	 * @author codistmonk (creation 2014-03-07)
	 */
	public static final class VectorStatistics implements Serializable {
		
		private final Statistics[] statistics;
		
		/**
		 * @param dimension
		 * <br>Range: <code>[0 .. Integer.MAX_VALUE]</code>
		 */
		public VectorStatistics(final int dimension) {
			this.statistics = instances(dimension, Statistics.FACTORY);
		}
		
		public final void reset() {
			for (final Statistics statistics : this.getStatistics()) {
				statistics.reset();
			}
		}
		
		/**
		 * @return
		 * <br>Not null
		 * <br>Strong reference in <code>this</code>
		 */
		public final Statistics[] getStatistics() {
			return this.statistics;
		}
		
		/**
		 * @param values
		 * <br>Must not be null
		 * <br>Size range: <code>{this.getStatistics().length}</code>
		 */
		public final void addValues(final double... values) {
			this.forEach(Statistics::addValue, values);
		}
		
		/**
		 * @param f
		 * <br>Must not be null
		 * @param values
		 * <br>Must not be null
		 * <br>Size range: <code>{this.getStatistics().length}</code>
		 */
		public final void forEach(final BiConsumer<? super Statistics, Double> f, final double... values) {
			final int n = this.getStatistics().length;
			
			for (int i = 0; i < n; ++i) {
				f.accept(this.getStatistics()[i], values[i]);
			}
		}
		
		/**
		 * @param f
		 * <br>Must not be null
		 * @param values
		 * <br>Must not be null
		 * <br>Size range: <code>{this.getStatistics().length}</code>
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public final double[] apply(final ToDoubleBiFunction<? super Statistics, Double> f, final double... values) {
			final int n = this.getStatistics().length;
			final double[] result = new double[n];
			
			for (int i = 0; i < n; ++i) {
				result[i] = f.applyAsDouble(this.getStatistics()[i], values[i]);
			}
			
			return result;
		}
		
		/**
		 * @param mapper
		 * <br>Must not be null
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public final double[] map(final ToDoubleFunction<? super Statistics> mapper) {
			return Arrays.stream(this.getStatistics()).mapToDouble(mapper).toArray();
		}
		
		/**
		 * @param values
		 * <br>Must not be null
		 * <br>Size range: <code>{this.getStatistics().length}</code>
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public final double[] getNormalizedValues(final double... values) {
			return this.apply(Statistics::getNormalizedValue, values);
		}
		
		/**
		 * @param values
		 * <br>Must not be null
		 * <br>Size range: <code>{this.getStatistics().length}</code>
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public final double[] getDenormalizedValues(final double... values) {
			return this.apply(Statistics::getDenormalizedValue, values);
		}
		
		/**
		 * @return
		 * <br>Range: <code>[0.0 .. Double.MAX_VALUE]</code>
		 */
		public final double getCount() {
			return this.getStatistics()[0].getCount();
		}
		
		/**
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public final double[] getMeans() {
			return this.map(Statistics::getMean);
		}
		
		/**
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public final double[] getMinima() {
			return this.map(Statistics::getMinimum);
		}
		
		/**
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public final double[] getMaxima() {
			return this.map(Statistics::getMaximum);
		}
		
		/**
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public final double[] getAmplitudes() {
			return this.map(Statistics::getAmplitude);
		}
		
		/**
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public final double[] getVariances() {
			return this.map(Statistics::getVariance);
		}
		
		/**
		 * {@value}.
		 */
		private static final long serialVersionUID = 8371199963847573845L;
		
	}
    
}
