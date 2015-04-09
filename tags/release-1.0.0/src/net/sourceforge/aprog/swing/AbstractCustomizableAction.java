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

package net.sourceforge.aprog.swing;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 *
 * @author codistmonk (creation 2010-06-27)
 */
public abstract class AbstractCustomizableAction extends AbstractAction {

    /**
     *
     * @param name
     * <br>Maybe null
     * <br>Shared
     * @return {@code this}
     * <br>Not null
     */
    public final AbstractCustomizableAction setName(final String name) {
        this.putValue(NAME, name);

        return this;
    }

    /**
     *
     * @param icon
     * <br>Maybe null
     * <br>Shared
     * @return {@code this}
     * <br>Not null
     */
    public final AbstractCustomizableAction setSmallIcon(final Icon icon) {
        this.putValue(SMALL_ICON, icon);

        return this;
    }

    /**
     *
     * @param icon
     * <br>Maybe null
     * <br>Shared
     * @return {@code this}
     * <br>Not null
     */
    public final AbstractCustomizableAction setLargeIcon(final Icon icon) {
        this.putValue(LARGE_ICON_KEY, icon);

        return this;
    }

    /**
     *
     * @param shortcut
     * <br>Maybe null
     * <br>Shared
     * @return {@code this}
     * <br>Not null
     */
    public final AbstractCustomizableAction setShortcut(final KeyStroke shortcut) {
        this.putValue(ACCELERATOR_KEY, shortcut);

        return this;
    }

    private static final long serialVersionUID = -2429711084032575051L;

}