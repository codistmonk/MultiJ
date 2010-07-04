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

import java.awt.Component;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.sourceforge.aprog.markups.Constants.Variables.*;

import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeModel;
import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.i18n.Messages;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.tools.Tools;
import net.sourceforge.aprog.xml.XMLTools;
import org.w3c.dom.Node;

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
        if ((Boolean) context.get(FILE_MODIFIED)) {
            switch (JOptionPane.showConfirmDialog(null, "Save?", null, JOptionPane.YES_NO_CANCEL_OPTION)) {
                case JOptionPane.YES_OPTION:
                    save(context);
                    break;
                case JOptionPane.NO_OPTION:
                    break;
                case JOptionPane.CANCEL_OPTION:
                    return;
                default:
                    Tools.getLoggerForThisMethod().log(Level.WARNING, "Unhandled option");
                    break;
            }
        }

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

        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog((Component) context.get(MAIN_FRAME)) && fileChooser.getSelectedFile() != null) {
            context.set(FILE, fileChooser.getSelectedFile());
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void save(final Context context) {
        final File file = context.get(FILE);

        if (file != null) {
            save(context, file);
        } else {
            saveAs(context);
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void saveAs(final Context context) {
        final JFileChooser fileChooser = new JFileChooser();

        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog((Component) context.get(MAIN_FRAME)) && fileChooser.getSelectedFile() != null) {
            save(context, fileChooser.getSelectedFile());
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     * @param file
     * <br>Not null
     * <br>Shared
     */
    private static final void save(final Context context, final File file) {
        try {
            XMLTools.write((Node) ((TreeModel) context.get(TREE_MODEL)).getRoot(), new FileOutputStream(file), 0);
            context.set(FILE, file);
        } catch (final FileNotFoundException exception) {
            net.sourceforge.aprog.subtitlesadjuster.Actions.showErrorMessage(context, exception);
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void undo(final Context context) {
        Tools.debugPrint("TODO");

        net.sourceforge.aprog.subtitlesadjuster.Actions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void redo(final Context context) {
        Tools.debugPrint("TODO");

        net.sourceforge.aprog.subtitlesadjuster.Actions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void copy(final Context context) {
        Tools.debugPrint("TODO");

        net.sourceforge.aprog.subtitlesadjuster.Actions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void cut(final Context context) {
        Tools.debugPrint("TODO");

        net.sourceforge.aprog.subtitlesadjuster.Actions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void paste(final Context context) {
        Tools.debugPrint("TODO");

        net.sourceforge.aprog.subtitlesadjuster.Actions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void tree(final Context context) {
        Tools.debugPrint("TODO");

        net.sourceforge.aprog.subtitlesadjuster.Actions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void text(final Context context) {
        Tools.debugPrint("TODO");

        net.sourceforge.aprog.subtitlesadjuster.Actions.showTODOMessage(context);
    }

}
