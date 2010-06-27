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

package net.sourceforge.aprog.subtitlesadjuster;

import static net.sourceforge.aprog.i18n.Messages.*;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.sourceforge.aprog.swing.InvokerAction;

/**
 *
 * @author codistmonk (creation 2010-06-27)
 */
public final class SubtitlesAdjusterTools {

    /**
     * Private default constructor to prevent instantiation.
     */
    private SubtitlesAdjusterTools() {
        // Do nothing
    }

    /**
     *
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem item(final String translationKey, final String methodName, final Object... arguments) {
        return translate(new JMenuItem(
                action(SubtitlesAdjuster.class, methodName, arguments)
                .setName(translationKey)));
    }

    /**
     *
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param shortcut
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem item(final String translationKey, final KeyStroke shortcut, final String methodName, final Object... arguments) {
        return translate(new JMenuItem(
                action(SubtitlesAdjuster.class, methodName, arguments)
                .setName(translationKey)
                .setShortcut(shortcut)));
    }

    /**
     * Creates an action that will invoke the specified method with the specified arguments when it is performed.
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
     * @return
     * <br>Not null
     * <br>New
     */
    public static final InvokerAction action(final Object objectOrClass, final String methodName, final Object... arguments) {
        return new InvokerAction(objectOrClass, methodName, arguments);
    }

}
