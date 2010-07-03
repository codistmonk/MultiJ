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

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.tools.Tools;

/**
 *
 * @author codistmonk (creation 2010-07-03)
 */
public final class Actions {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private Actions() {
        throw new IllegalInstantiationException();
    }

    /**
     *
     * @param context
     * <br>Unused
     */
    public static final void quit(final Context context) {
        Tools.debugPrint("TODO: check if file needs saving");

        System.exit(0);
    }

    /**
     *
     * @param context
     * <br>Unused
     */
    public static final void open(final Context context) {
        Tools.debugPrint("TODO");

        net.sourceforge.aprog.subtitlesadjuster.Actions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Unused
     */
    public static final void save(final Context context) {
        Tools.debugPrint("TODO");

        net.sourceforge.aprog.subtitlesadjuster.Actions.showTODOMessage(context);
    }

}
