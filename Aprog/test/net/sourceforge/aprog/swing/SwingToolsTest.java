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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sourceforge.aprog.tools.Tools;

import org.junit.Test;
/**
 * Automated tests using JUnit 4 for {@link SwingTools}.
 *
 * @author codistmonk (creation 2010-06-26)
 */
public final class SwingToolsTest {

    @Test
    public final void testGetIcon() {
        SwingTools.setImagesBase(Tools.getCallerPackagePath());

        assertNotNull(SwingTools.getIcon("start.png"));
    }

    @Test
    public final void testGetIconOrNull() {
        assertNull(SwingTools.getIconOrNull("inexisting_icon"));

        SwingTools.setImagesBase(Tools.getCallerPackagePath());

        assertNotNull(SwingTools.getIconOrNull("start.png"));
    }

    @Test
    public final void testAdd() throws Exception {
        if (SwingTools.canInvokeThisMethodInAWT(this)) {
            final JPanel panel = new JPanel(new FlowLayout());
            final JLabel label = new JLabel();

            assertTrue(panel.getLayout() instanceof FlowLayout);

            SwingTools.add(panel, label, new GridBagConstraints());

            assertTrue(panel.getLayout() instanceof GridBagLayout);
            assertSame(label, panel.getComponents()[0]);
            assertEquals(1, panel.getComponentCount());
        }
    }

    @Test
    public final void testRollover() throws Exception {
        if (SwingTools.canInvokeThisMethodInAWT(this)) {
            SwingTools.setImagesBase(Tools.getCallerPackagePath());

            final JButton button = SwingTools.rollover(new JButton(), "start", false);

            assertNotNull(button.getIcon());
            assertNotNull(button.getRolloverIcon());
            assertFalse(button.isBorderPainted());
        }
    }

    @Test
    public final void testPackAndCenter() throws Exception {
        if (SwingTools.canInvokeThisMethodInAWT(this)) {
            fail("TODO");
        }
    }

    @Test
    public final void testCenter() throws Exception {
        if (SwingTools.canInvokeThisMethodInAWT(this)) {
            fail("TODO");
        }
    }

    @Test
    public final void testScrollable() throws Exception {
        if (SwingTools.canInvokeThisMethodInAWT(this)) {
            fail("TODO");
        }
    }

    @Test
    public final void testMenuBar() throws Exception {
        if (SwingTools.canInvokeThisMethodInAWT(this)) {
            fail("TODO");
        }
    }

    @Test
    public final void testMenu() throws Exception {
        if (SwingTools.canInvokeThisMethodInAWT(this)) {
            fail("TODO");
        }
    }

    @Test
    public final void testCanInvokeThisMethodInAWT() {
        if (SwingTools.canInvokeThisMethodInAWT(this)) {
            SwingTools.checkAWT();
        }
    }

    @Test(expected=IllegalStateException.class)
    public final void testCheckAWT() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public final void run() {
                // Doesn't throw
                SwingTools.checkAWT();
            }

        });

        // Throws
        SwingTools.checkAWT();
    }

    @Test
    public final void testCheckNotAWT() throws Exception {
        // Doesn't throw
        SwingTools.checkNotAWT();

        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public final void run() {
                try {
                    // Throws
                    SwingTools.checkNotAWT();

                    fail("This section wasn't supposed to be reached");
                } catch (final IllegalStateException expectedException) {
                    // Ignore
                }
            }

        });
    }

}