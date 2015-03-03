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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author codistmonk (creation 2014-03-15)
 */
public abstract class MouseHandler extends MouseAdapter implements Serializable {
	
	private final AtomicBoolean updateNeeded;
	
	protected MouseHandler() {
		this(null);
	}
	
	/**
	 * @param sharedUpdateFlag
	 * <br>May be null
	 * <br>Saved as strong reference if not null
	 */
	protected MouseHandler(final AtomicBoolean sharedUpdateFlag) {
		this.updateNeeded = sharedUpdateFlag != null ? sharedUpdateFlag : new AtomicBoolean(true);
	}
	
	/**
	 * Adds <code>this</code> to <code>component</code> as {@link MouseListener},  {@link MouseMotionListener} and {@link MouseWheelListener}.
	 * 
	 * @param component
	 * <br>Must not be null
	 * @return <code>this</code>
	 * <br>Not null
	 */
	@SuppressWarnings("unchecked")
	public final <T extends MouseHandler> T addTo(final Component component) {
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
		
		return (T) this;
	}
	
	/**
	 * Removes <code>this</code> from <code>component</code> as {@link MouseListener},  {@link MouseMotionListener} and {@link MouseWheelListener}.
	 * 
	 * @param component
	 * <br>Must not be null
	 * @return <code>this</code>
	 * <br>Not null
	 */
	@SuppressWarnings("unchecked")
	public final <T extends MouseHandler> T removeFrom(final Component component) {
		component.removeMouseListener(this);
		component.removeMouseMotionListener(this);
		component.removeMouseWheelListener(this);
		
		return (T) this;
	}
	
	/**
	 * @return
	 * <br>Not null
	 * <br>Strong reference in <code>this</code>
	 */
	public final AtomicBoolean getUpdateNeeded() {
		return this.updateNeeded;
	}
	
	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = 2247894840683764453L;
	
}
