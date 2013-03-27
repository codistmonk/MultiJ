/*
 *  The MIT License
 * 
 *  Copyright 2010 Codist Monk.
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
 * Automated tests using JUnit 4 for {@link WeakBag}.
 *
 * @author codistmonk (creation 2010-10-24)
 */
public final class WeakBagTest {

    @Test
    public final void testScenario() {
        final WeakBag<String> weakBag = new WeakBag<String>();
        final String object1 = "42";
        String object2 = new String(object1);

        assertEquals(0, weakBag.getElementCount());

        weakBag.append(object1);
        weakBag.append(object2);

        assertEquals(2, weakBag.getElementCount());

        WeakBag.runGarbageCollector();

        assertEquals(2, weakBag.getElementCount());

        object2 = null;

        WeakBag.runGarbageCollector();

        assertEquals(1, weakBag.getElementCount());

        weakBag.remove(object1);

        assertEquals(0, weakBag.getElementCount());
    }

}