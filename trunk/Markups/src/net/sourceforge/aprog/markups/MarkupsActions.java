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
import static net.sourceforge.aprog.xml.XMLTools.*;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.util.logging.Level;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterActions;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.tools.Tools;
import net.sourceforge.aprog.xml.XMLTools;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author codistmonk (creation 2010-07-03)
 */
public final class MarkupsActions {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private MarkupsActions() {
        throw new IllegalInstantiationException();
    }

    /**
     *
     * @param context
     * <br>Unused
     */
    public static final void quit(final Context context) {
        if (confirm(context)) {
            System.exit(0);
        }
    }

    /**
     * Returns {@code true} if the file doesn't need saving;
     * otherwise, asks the user what to do.
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     * @return {@code true} if the caller can proceed
     */
    public static final boolean confirm(final Context context) {
        if ((Boolean) context.get(FILE_MODIFIED)) {
            switch (JOptionPane.showConfirmDialog(null, "Save?", null, JOptionPane.YES_NO_CANCEL_OPTION)) {
                case JOptionPane.YES_OPTION:
                    save(context);
                    break;
                case JOptionPane.NO_OPTION:
                    break;
                case JOptionPane.CANCEL_OPTION:
                    return false;
                default:
                    Tools.getLoggerForThisMethod().log(Level.WARNING, "Unhandled option");
                    break;
            }
        }

        return true;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void newFile(final Context context) {
        if (confirm(context)) {
            context.set(FILE, null);
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void open(final Context context) {
        if (!confirm(context)) {
            return;
        }

        final JFileChooser fileChooser = new JFileChooser();

        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog((Component) context.get(MAIN_FRAME)) &&
                fileChooser.getSelectedFile() != null) {
            open(context, fileChooser.getSelectedFile());
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
    public static final void open(final Context context, final File file) {
        context.set(DOM, XMLTools.parse(new InputSource(file.getAbsolutePath())));
        context.set(FILE, file);
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

        if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog((Component) context.get(MAIN_FRAME)) &&
                fileChooser.getSelectedFile() != null) {
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
            XMLTools.write(
                    (Node) context.get(DOM),
                    new FileOutputStream(file),
                    0);
            context.set(FILE, file);
            context.set(FILE_MODIFIED, false);
        } catch (final FileNotFoundException exception) {
            SubtitlesAdjusterActions.showErrorMessage(context, exception);
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void undo(final Context context) {
        Tools.debugPrint("TODO");

        SubtitlesAdjusterActions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void redo(final Context context) {
        Tools.debugPrint("TODO");

        SubtitlesAdjusterActions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void copy(final Context context) {
        Tools.debugPrint("TODO");

        SubtitlesAdjusterActions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void cut(final Context context) {
        Tools.debugPrint("TODO");

        SubtitlesAdjusterActions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void paste(final Context context) {
        Tools.debugPrint("TODO");

        SubtitlesAdjusterActions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void appendNewNode(final Context context) {
        Tools.debugPrint("TODO");

        SubtitlesAdjusterActions.showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void moveUp(final Context context) {
        final Node node = context.get(SELECTED_NODE);
        final Node parent = getNode(node, "..");

        context.set(SELECTED_NODE, null);

        {
            final List<Node> siblings = MarkupsTools.getAttributeChildren(parent);
            final int index = siblings.indexOf(node);

            if (index >= 0) {
                Tools.debugPrint("TODO");

                return;
            }
        }

        {
            final List<Node> siblings = MarkupsTools.getNonattributeChildren(parent);
            final int index = siblings.indexOf(node);

            if (index < 0) {
                Tools.debugPrint(siblings);
                throw new IllegalStateException("Orphan node: " + node);
            }

            if (index > 0) {
                parent.insertBefore(node, siblings.get(index - 1));
            }
        }

        parent.normalize();

        context.set(SELECTED_NODE, node);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void moveDown(final Context context) {
        final Node node = context.get(SELECTED_NODE);
        final Node parent = getNode(node, "..");

        {
            final List<Node> siblings = MarkupsTools.getAttributeChildren(parent);
            final int index = siblings.indexOf(node);

            if (index >= 0) {
                if (index == siblings.size() - 2) {
                    parent.getAttributes().removeNamedItem(node.getNodeName());
                    parent.getAttributes().setNamedItem(node);
                } else if (index < siblings.size() - 2) {
                    Tools.debugPrint("TODO");
                }

                return;
            }
        }

        {
            final List<Node> siblings = MarkupsTools.getNonattributeChildren(parent);
            final int index = siblings.indexOf(node);

            if (index < 0) {
                Tools.debugPrint(siblings);
                throw new IllegalStateException("Orphan node: " + node);
            }

            if (index == siblings.size() - 2) {
                parent.appendChild(node);
            } else {
                parent.insertBefore(node, siblings.get(index + 2));
            }
        }

        parent.normalize();

        context.set(SELECTED_NODE, node);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void evaluateXPathExpression(final Context context) {
        final Node node = context.get(SELECTED_NODE);

        try {
            context.set(XPATH_RESULT, XMLTools.getNodes(node, (String) context.get(XPATH_EXPRESSION)));
            context.set(XPATH_ERROR, null);
        } catch (final Exception exception) {
            context.set(XPATH_RESULT, null);
            context.set(XPATH_ERROR, exception);
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void evaluateQuasiXPathExpression(final Context context) {
        try {
            XMLTools.getOrCreateNode((Node) context.get(SELECTED_NODE), (String) context.get(QUASI_XPATH_EXPRESSION));

            context.set(QUASI_XPATH_ERROR, null);
        } catch (final Exception exception) {
            context.set(QUASI_XPATH_ERROR, exception);
        }
    }

}
