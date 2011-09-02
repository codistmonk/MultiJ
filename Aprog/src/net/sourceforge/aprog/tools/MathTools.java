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

import java.util.ArrayList;
import java.util.List;

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
     * <br>Range: any long
     * @param k
     * <br>Range: any long
     * @return <code>n!/(k!(n-k)!)</code>
     * <br>Range: <code>[0 .. Long.MAX_VALUE]</code>
     */
    public static final long nCk(final long n, final long k) {
        List<Long> values = new ArrayList<Long>((int) n + 1);
        List<Long> newValues = new ArrayList<Long>((int) n + 1);

        values.add(1L);

        for (long i = 1; i <= n; ++i) {
            newValues.clear();

            for (long j = 0; j <= i; ++j) {
                newValues.add(get(values, j - 1) + get(values, j));
            }

            final List<Long> tmp = values;
            values = newValues;
            newValues = tmp;
        }

        return get(values, k);
    }

    /**
     * @param values
     * <br>Not null
     * @param index
     * <br>Range: any long
     * @return
     * <br>Range: any long
     */
    private static final long get(final List<Long> values, final long index) {
        return index < 0 || values.size() <= index ? 0 : values.get((int) index);
    }

}
