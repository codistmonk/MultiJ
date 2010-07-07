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

package net.sourceforge.aprog.markups;

import static net.sourceforge.aprog.markups.MarkupsConstants.Variables.*;
import static net.sourceforge.aprog.i18n.Messages.*;
import static net.sourceforge.aprog.tools.Tools.*;
import static net.sourceforge.aprog.swing.SwingTools.*;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.xml.XMLTools;
import net.sourceforge.jmacadapter.MacAdapterTools;

/**
 *
 * @author codistmonk (creation 2010-07-03)
 */
public final class Markups {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private Markups() {
        throw new IllegalInstantiationException();
    }

    static {
        MacAdapterTools.setApplicationName(MarkupsConstants.APPLICATION_NAME);
        useSystemLookAndFeel();
        setMessagesBase(getThisPackagePath() + "Messages");
    }

    /**
     * @param arguments the command line arguments
     */
    public static final void main(final String[] arguments) {
        if (canInvokeLaterThisMethodInAWT(null, (Object) arguments)) {
            MarkupsComponents.newMainFrame(newContext()).setVisible(true);
        }
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Context newContext() {
        final Context result = new Context();

        result.set(APPLICATION_NAME, MarkupsConstants.APPLICATION_NAME);
        result.set(APPLICATION_VERSION, MarkupsConstants.APPLICATION_VERSION);
        result.set(APPLICATION_COPYRIGHT, MarkupsConstants.APPLICATION_COPYRIGHT);
        result.set(FILE, null);
        result.set(FILE_MODIFIED, false);
        result.set(DOM, XMLTools.newDocument());
        result.set(SELECTED_NODE, null);
        result.set(XPAH_EXPRESSION, null);

        return result;
    }

}
