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

package net.sourceforge.aprog.swing;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link ScriptingPanel}.
 *
 * @author codistmonk (creation 2010-06-26)
 */
public final class ScriptingPanelTest {
	
	@Test
	public final void test() throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public final void run() {
				final JFrame frame = new JFrame();
				
				try {
					final ScriptingPanel scriptingPanel = new ScriptingPanel();
					final JTextPane messages = scriptingPanel.getMessages();
					final JTextArea inputArea = scriptingPanel.getInputArea();
					
					frame.add(scriptingPanel);
					frame.setVisible(true);
					
					inputArea.setText("1+1");
					inputArea.dispatchEvent(new KeyEvent(inputArea, KeyEvent.KEY_PRESSED,
							System.currentTimeMillis(), KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_ENTER, '\n'));
					
					assertEquals(inputArea.getSelectedText(), inputArea.getText());
					
					final String text = messages.getText();
					
					final int i = text.indexOf("1+1");
					
					assertTrue(0 <= i);
					
					final int j = text.indexOf("2", i);
					
					assertTrue(i < j);
				} finally {
					frame.dispose();
				}
			}
			
		});
	}
	
}
