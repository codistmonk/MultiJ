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

import static multij.af.AFConstants.Variables.ACTIONS_QUIT;
import static multij.tools.Tools.ignore;
import multij.context.Context;

/**
 *
 * @author codistmonk (creation 2010-09-24)
 */
public final class QuitAction extends AbstractAFAction {
	
	/**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * <br>Input-output
     */
    public QuitAction(final Context context) {
        super(context, ACTIONS_QUIT);
    }
    
    @Override
    public final void perform(final Object object) {
    	ignore(object);
    	
        System.exit(0);
    }
	
    /**
	 * {@value}.
	 */
	private static final long serialVersionUID = 4019533368417446166L;
	
}
