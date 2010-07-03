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
import static net.sourceforge.aprog.subtitlesadjuster.Components.*;
import static net.sourceforge.aprog.subtitlesadjuster.Constants.Variables.*;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.swing.SwingTools;
import net.sourceforge.aprog.tools.Tools;

/**
 * This class defines all the operations that can be executed on the application context object.
 * 
 * @author codistmonk (creation 2010-06-27)
 */
public final class Actions {

    /**
     * Private default constructor to prevent instantiation.
     */
    private Actions() {
        // Do nothing
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void showAboutDialog(final Context context) {
        JOptionPane.showMessageDialog(
                (Component) context.get(MAIN_FRAME),
                context.get(APPLICATION_NAME) + "\n" +
                        context.get(APPLICATION_VERSION) + "\n" +
                        context.get(APPLICATION_COPYRIGHT),
                translate("About $0", context.get(APPLICATION_NAME)),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void showPreferencesDialog(final Context context) {
        newPreferencesDialog(context).setVisible(true);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void quit(final Context context) {
        System.exit(0);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void open(final Context context) {
        final JFileChooser fileChooser = new JFileChooser();

        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new FileFilter() {

            @Override
            public final boolean accept(final File file) {
                return file.isDirectory() || file.getName().endsWith(".srt");
            }

            @Override
            public final String getDescription() {
                return translate("Subtitles file $0", "(*.srt)");
            }

        });

        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog((Component) context.get(MAIN_FRAME)) &&
                fileChooser.getSelectedFile() != null) {
            ((Subtitles) context.get(SUBTITLES)).load(fileChooser.getSelectedFile());
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void save(final Context context) {
        ((Subtitles) context.get(SUBTITLES)).save();
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void showManual(final Context context) {
        showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void showTODOMessage(final Context context) {
        System.out.println(Tools.debug(3, "TODO"));
        JOptionPane.showMessageDialog(
                (Component) context.get(MAIN_FRAME),
                translate("Not implemented"),
                context.get(APPLICATION_NAME).toString(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 
     * @param context
     * <br>Not null
     * @param throwable
     * <br>Not null
     */
    public static final void showErrorMessage(final Context context, final Throwable throwable) {
        if (SwingTools.canInvokeLaterThisMethodInAWT(null, throwable)) {
            JOptionPane.showMessageDialog(
                    null,
                    newErrorMessagePanel(throwable),
                    context.get(APPLICATION_NAME).toString(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void updateMainFrameTitle(final Context context) {
        ((JFrame) context.get(MAIN_FRAME)).setTitle(makeMainFrameTitle(context));
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     */
    private static final String makeMainFrameTitle(final Context context) {
        final File file = context.get(FILE);
        final Boolean fileModified = context.get(FILE_MODIFIED);

        return file == null ? context.get(APPLICATION_NAME).toString() : file.getName() + (fileModified ? "*" : "");
    }

}
