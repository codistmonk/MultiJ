/*
 *  The MIT License
 * 
 *  Copyright 2015 Codist Monk.
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

package multij.swing;

import static org.junit.Assert.*;

import javax.swing.JComponent;
import javax.swing.JLabel;

import multij.swing.MouseHandler;

import org.junit.Test;

/**
 * @author codistmonk (creation 2015-03-02)
 */
public final class MouseHandlerTest {
	
	@Test
	public final void test() {
		final JComponent component = new JLabel();
		final int n1 = component.getMouseListeners().length;
		final int n2 = component.getMouseMotionListeners().length;
		final int n3 = component.getMouseWheelListeners().length;
		final MouseHandler mouseHandler = new MouseHandler() {
			
			private static final long serialVersionUID = -469601602892295148L;
			
		}.addTo(component);
		
		assertNotNull(mouseHandler.getUpdateNeeded());
		
		assertEquals(n1 + 1, component.getMouseListeners().length);
		assertEquals(n2 + 1, component.getMouseMotionListeners().length);
		assertEquals(n3 + 1, component.getMouseWheelListeners().length);
		assertSame(mouseHandler, component.getMouseListeners()[n1]);
		assertSame(mouseHandler, component.getMouseMotionListeners()[n2]);
		assertSame(mouseHandler, component.getMouseWheelListeners()[n3]);
		
		mouseHandler.removeFrom(component);
		
		assertEquals(n1, component.getMouseListeners().length);
		assertEquals(n2, component.getMouseMotionListeners().length);
		assertEquals(n3, component.getMouseWheelListeners().length);
	}
	
}
