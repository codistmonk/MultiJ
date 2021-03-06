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

package multij.swing;

import java.awt.event.ActionEvent;

import multij.tools.Tools;

/**
 *
 * @author codistmonk (creation 2010-06-27)
 */
public final class InvokerAction extends AbstractCustomizableAction {

    private final Object objectOrClass;

    private final String methodName;

    private final Object[] arguments;

    /**
     *
     * @param objectOrClass
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     */
    public InvokerAction(final Object objectOrClass, final String methodName, final Object... arguments) {
        this.objectOrClass = objectOrClass;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    public final void actionPerformed(final ActionEvent event) {
        Tools.invoke(this.objectOrClass, this.methodName, this.arguments);
    }

    private static final long serialVersionUID = -893792762462035594L;

}
