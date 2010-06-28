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
import static net.sourceforge.aprog.subtitlesadjuster.Constants.*;
import static net.sourceforge.aprog.subtitlesadjuster.Constants.Variables.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.*;
import static net.sourceforge.aprog.swing.SwingTools.*;
import static net.sourceforge.aprog.tools.Tools.*;

import java.util.Date;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.jmacadapter.MacAdapterTools;

/**
 * Main class.
 *
 * @author codistmonk (creation 2010-06-26)
 */
public final class SubtitlesAdjuster {

    /**
     * Private default constructor to prevent instantiation.
     */
    private SubtitlesAdjuster() {
        // Do nothing
    }

    static {
        MacAdapterTools.setApplicationName(APPLICATION_NAME);
        useSystemLookAndFeel();
        setMessagesBase(getCallerPackagePath() + "Messages");
    }

    /**
     * @param arguments the command line arguments
     * <br>Not null
     * <br>Shared
     * <br>Unused
     */
    public static final void main(final String[] arguments) {
        if (canInvokeLaterThisMethodInAWT(null, (Object) arguments)) {
            // FIXME The following doesn't seem to work well on Windows XP
            Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler());

            Components.createMainFrame(createContext()).setVisible(true);
        }
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Context createContext() {
        final Context result = new Context();

        result.set(FILE, null);
        result.set(FILE_MODIFIED, false);
        result.set(FIRST_TIME, new Date(0L));
        result.set(LAST_TIME, new Date(0L));

        new Subtitles(result);

        setFileModifiedOnVariableChanged(result, FIRST_TIME, true);
        setFileModifiedOnVariableChanged(result, LAST_TIME, true);
        setFileModifiedOnVariableChanged(result, FILE, false);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * @param variableName
     * <br>Not null
     * @param value
     */
    private static final void setFileModifiedOnVariableChanged(
            final Context context, final String variableName, final boolean value) {
        invokeOnVariableChanged(context, variableName, context.getVariable(FILE_MODIFIED), "setValue", value);
    }

    /**
     *
     * @author codistmonk (creation 2010-06-28)
     */
    private static final class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        private final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

        UncaughtExceptionHandler() {
            this.defaultUncaughtExceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
        }

        @Override
        public final void uncaughtException(final Thread thread, final Throwable throwable) {
            this.defaultUncaughtExceptionHandler.uncaughtException(thread, throwable);

            Actions.showErrorMessage(throwable);
        }

    }

}
