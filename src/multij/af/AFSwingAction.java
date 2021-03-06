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

package multij.af;

import java.awt.event.ActionEvent;

import multij.context.Context;
import multij.swing.AbstractCustomizableAction;

/**
 *
 * @author codistmonk (creation 2010-09-24)
 */
public final class AFSwingAction extends AbstractCustomizableAction {

    private final Context context;

    private final String actionKey;

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @param actionKey
     * <br>Not null
     * <br>Shared
     */
    public AFSwingAction(final Context context, final String actionKey) {
        this.context = context;
        this.actionKey = actionKey;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    public final Context getContext() {
        return this.context;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    public final String getActionKey() {
        return this.actionKey;
    }

    @Override
    public final void actionPerformed(final ActionEvent event) {
        ((AbstractAFAction) this.getContext().get(this.getActionKey())).perform(event);
    }

    private static final long serialVersionUID = 3352877175960985617L;

}
