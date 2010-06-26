/*
 *  The MIT License
 * 
 *  Copyright 2010 greg.
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

package net.sourceforge.aprog.swing;

import static org.junit.Assert.*;

import javax.swing.SwingUtilities;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link SwingTools}.
 *
 * @author codistmonk (creation 2010-06-26)
 */
public final class SwingToolsTest {

    @Test
    public final void testGetIcon() {
        fail("TODO");
    }

    @Test
    public final void testGetIconOrNull() {
        assertNull(SwingTools.getIconOrNull("inexisting_icon"));
    }

    @Test
    public final void testAdd() {
        fail("TODO");
    }

    @Test
    public final void testRollover() {
        fail("TODO");
    }

    @Test(expected=IllegalStateException.class)
    public final void testCheckAWT() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public final void run() {
                SwingTools.checkAWT();
            }

        });

        SwingTools.checkAWT();
    }

    @Test
    public final void testCheckNotAWT() throws Exception {
        SwingTools.checkNotAWT();

        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public final void run() {
                try {
                    SwingTools.checkNotAWT();

                    fail("This section wasn't supposed to be reached");
                } catch (final IllegalStateException expectedException) {
                    // Ignore
                }
            }

        });
    }

}