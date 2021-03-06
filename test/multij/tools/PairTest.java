/*
 *  The MIT License
 * 
 *  Copyright 2012 Codist Monk.
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

import static org.junit.Assert.*;
import multij.tools.Pair;

import org.junit.Test;

/**
 * Automated tests using JUnit for {@link Pair}.
 *
 * @author codistmonk (creation 2011-09-02)
 */
public final class PairTest {

    @Test
    public final void test() {
        final Object first = new Object();
        final Object second = new Object();
        final Pair<Object, Object> pair = new Pair<Object, Object>(first, second);
        
        assertSame(first, pair.getFirst());
        assertSame(second, pair.getSecond());
        assertEquals(pair, new Pair<Object, Object>(first, second));
    }

}
