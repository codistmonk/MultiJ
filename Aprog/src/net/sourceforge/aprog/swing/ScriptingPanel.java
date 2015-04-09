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

import static net.sourceforge.aprog.swing.SwingTools.*;
import static net.sourceforge.aprog.tools.Tools.escapeHTML;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import net.sourceforge.aprog.tools.CommandLineArgumentsParser;
import net.sourceforge.aprog.tools.Scripting;

/**
 * @author codistmonk (creation 2015-04-09)
 */
public final class ScriptingPanel extends JPanel {
	
	private final Scripting scripting;
	
	private final JTextPane messages;
	
	private final JTextArea inputArea;
	
	public ScriptingPanel() {
		this(new Scripting("JavaScript"));
	}
	
	/**
	 * @param scripting
	 * <br>Must not be null
	 * <br>Stored as strong reference in <code>this</code>
	 */
	public ScriptingPanel(final Scripting scripting) {
		super(new BorderLayout());
		this.scripting = scripting;
		this.messages = new JTextPane();
		this.inputArea = new JTextArea();
		
		this.messages.setEditorKit(new HTMLEditorKit());
		this.messages.setText("<html><body><p id=\"end\"/></body></html>");
		this.messages.setEditable(false);
		
		this.inputArea.addKeyListener(new KeyAdapter() {
			
			@Override
			public final void keyPressed(final KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER && event.isControlDown()) {
					ScriptingPanel.this.getInputArea().selectAll();
					
					final String command = ScriptingPanel.this.getInputArea().getText();
					
					final HTMLDocument document = (HTMLDocument) ScriptingPanel.this.getMessages().getDocument();
					final Element element = document.getElement("end");
					
					try {
						document.insertBeforeStart(element, "<p>" + escapeHTML(command) + "</p>");
						document.insertBeforeStart(element, "<span>" + scripting.eval(command) + "</span>");
					} catch (final Exception exception) {
						final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						
						exception.printStackTrace(new PrintStream(buffer));
						
						try {
							document.insertBeforeStart(element, "<p style=\"color: red;\">" + escapeHTML(new String(buffer.toByteArray(), "UTF-8")) + "</p>");
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
					
					SwingUtilities.invokeLater(() -> ScriptingPanel.this.getMessages().scrollRectToVisible(new Rectangle(0, ScriptingPanel.this.getMessages().getHeight(), 0, 0)));
				}
			}
			
		});
		
		final JSplitPane verticalSplit = verticalSplit(scrollable(this.messages), this.inputArea);
		
		verticalSplit.setResizeWeight(0.9);
		
		this.add(verticalSplit);
		this.setPreferredSize(new Dimension(640, 480));
	}
	
	public final void eval(final String command) {
		this.getInputArea().setText(command);
		this.getInputArea().dispatchEvent(new KeyEvent(this.getInputArea(), KeyEvent.KEY_PRESSED,
				System.currentTimeMillis(), InputEvent.CTRL_DOWN_MASK, KeyEvent.VK_ENTER, '\n'));
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <bR>Strong reference in <code>this</code>
	 */
	public final Scripting getScripting() {
		return this.scripting;
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <bR>Strong reference in <code>this</code>
	 */
	public final JTextPane getMessages() {
		return this.messages;
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <bR>Strong reference in <code>this</code>
	 */
	public final JTextArea getInputArea() {
		return this.inputArea;
	}
	
	private static final long serialVersionUID = -9201314005312078638L;
	
	/**
	 * @param commandLineArguments
	 * <br>Must not be null
	 */
	public static final void main(final String... commandLineArguments) {
		final CommandLineArgumentsParser arguments = new CommandLineArgumentsParser(commandLineArguments);
		final String language = arguments.get("language", "JavaScript");
		final Scripting scripting = new Scripting(language);
		final String title = arguments.get("title", scripting.getScriptEngine().getFactory().getEngineName());
		final boolean useSystemLookAndFeel = arguments.get("systemLookAndFeel", 1)[0] != 0;
		
		if (useSystemLookAndFeel) {
			useSystemLookAndFeel();
		}
		
		SwingUtilities.invokeLater(() -> SwingTools.show(new ScriptingPanel(), title, false));
	}
	
	public static final void openScriptingPanelOnCtrlF2() {
		openScriptingPanelOn("ctrl F2");
	}
	
	/**
	 * @param keyStroke
	 * <br>Must not be null
	 */
	public static final void openScriptingPanelOn(final String keyStroke) {
		openScriptingPanelOn(KeyStroke.getKeyStroke(keyStroke));
	}
	
	/**
	 * @param keyStroke
	 * <br>Must not be null
	 * <br>Stored as string reference
	 */
	public static final void openScriptingPanelOn(final KeyStroke keyStroke) {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			
			@Override
			public final boolean dispatchKeyEvent(final KeyEvent event) {
				if (!event.isConsumed() && event.getID() == keyStroke.getKeyEventType()
						&& event.getKeyCode() == keyStroke.getKeyCode()
						&& (event.getModifiers() | event.getModifiersEx()) == keyStroke.getModifiers()) {
					main("systemLookAndFeel", "0");
					
					return true;
				}
				
				return false;
			}
			
		});
	}
	
}
