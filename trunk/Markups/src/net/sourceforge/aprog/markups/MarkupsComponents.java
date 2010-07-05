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

import static javax.swing.KeyStroke.getKeyStroke;

import static net.sourceforge.aprog.markups.MarkupsConstants.Variables.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.menu;
import static net.sourceforge.aprog.swing.SwingTools.checkAWT;
import static net.sourceforge.aprog.swing.SwingTools.menuBar;
import static net.sourceforge.aprog.swing.SwingTools.packAndCenter;
import static net.sourceforge.aprog.swing.SwingTools.scrollable;
import static net.sourceforge.aprog.tools.Tools.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.Variable;
import net.sourceforge.aprog.events.Variable.ValueChangedEvent;
import net.sourceforge.aprog.i18n.Messages;
import net.sourceforge.aprog.i18n.Translator;
import net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterActions;
import net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterComponents;
import net.sourceforge.aprog.swing.SwingTools;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.xml.XMLTools;
import net.sourceforge.jmacadapter.MacAdapterTools;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author codistmonk (creation 2010-07-03)
 */
public final class MarkupsComponents {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private MarkupsComponents() {
        throw new IllegalInstantiationException();
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JFrame newMainFrame(final Context context) {
        final JFrame result = new JFrame();

        context.set(MAIN_FRAME, result);

        result.setJMenuBar(newMenuBar(context));
        result.add(newMainPanel(context));

        result.addWindowListener(newListener(WindowListener.class, "windowClosing",
                MarkupsActions.class, "quit", context));

        invokeOnVariableChanged(context, FILE,
                SubtitlesAdjusterActions.class, "updateMainFrameTitle", context);
        invokeOnVariableChanged(context, FILE_MODIFIED,
                SubtitlesAdjusterActions.class, "updateMainFrameTitle", context);

        Translator.getDefaultTranslator().addListener(newListener(Translator.Listener.class, "localeChanged",
                SwingTools.class, "packAndUpdateMinimumSize", result));

        return packAndCenter(result);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuBar newMenuBar(final Context context) {
		checkAWT();

        if (MacAdapterTools.isMacOSX()) {
            MacAdapterTools.setUseScreenMenuBar(true);
        }

        return menuBar(
                menu("Application",
                    newAboutMenuItem(context),
                    null,
                    newPreferencesMenuItem(context),
                    null,
                    newQuitMenuItem(context)
                ),
                menu("File",
                        newNewMenuItem(context),
                        null,
                        newOpenMenuItem(context),
                        null,
                        newSaveMenuItem(context),
                        newSaveAsMenuItem(context)
                ),
                menu("Edit",
                        newUndoMenuItem(context),
                        newRedoMenuItem(context),
                        null,
                        newCopyMenuItem(context),
                        newCutMenuItem(context),
                        newPasteMenuItem(context)
                ),
                menu("View",
                        newTreeMenuItem(context),
                        newTextMenuItem(context)
                ),
                menu("Help",
                        newManualMenuItem(context)
                ));
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newAboutMenuItem(final Context context) {
		checkAWT();

        return SubtitlesAdjusterComponents.newAboutMenuItem(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newPreferencesMenuItem(final Context context) {
		checkAWT();

        return SubtitlesAdjusterComponents.newPreferencesMenuItem(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newQuitMenuItem(final Context context) {
		checkAWT();

        if (MacAdapterTools.isMacOSX() && MacAdapterTools.getUseScreenMenuBar()) {
            if (registerMacOSXApplicationListener("handleQuit",
                    MarkupsActions.class, "quit", context)) {
                return null;
            }
        }

        return item("Quit", getKeyStroke(META + " Q"),
                MarkupsActions.class, "quit", context);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newNewMenuItem(final Context context) {
        return item("New", getKeyStroke(META + " N"),
                MarkupsActions.class, "newFile", context);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newOpenMenuItem(final Context context) {
        return item("Open...", getKeyStroke(META + " O"),
                MarkupsActions.class, "open", context);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newSaveMenuItem(final Context context) {
        final JMenuItem result = item("Save", getKeyStroke(META + " S"),
                MarkupsActions.class, "save", context);
        final Variable<Boolean> fileModifiedVariable = context.getVariable(FILE_MODIFIED);

        fileModifiedVariable.addListener(new Variable.Listener<Boolean>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Boolean, ?> event) {
                result.setEnabled(event.getNewValue());
            }

        });

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newSaveAsMenuItem(final Context context) {
        final JMenuItem result = item("Save As...", getKeyStroke(META + " shift S"),
                MarkupsActions.class, "saveAs", context);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newUndoMenuItem(final Context context) {
        final JMenuItem result = item("Undo", getKeyStroke(META + " Z"),
                MarkupsActions.class, "undo", context);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newRedoMenuItem(final Context context) {
        final JMenuItem result = item("Redo", getKeyStroke(META + " Y"),
                MarkupsActions.class, "redo", context);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newCopyMenuItem(final Context context) {
        final JMenuItem result = item("Copy", getKeyStroke(META + " C"),
                MarkupsActions.class, "copy", context);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newCutMenuItem(final Context context) {
        final JMenuItem result = item("Cut", getKeyStroke(META + " X"),
                MarkupsActions.class, "cut", context);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newPasteMenuItem(final Context context) {
        final JMenuItem result = item("Paste", getKeyStroke(META + " V"),
                MarkupsActions.class, "paste", context);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newTreeMenuItem(final Context context) {
        final JMenuItem result = Messages.translate(new JRadioButtonMenuItem(
                SwingTools.action(MarkupsActions.class, "tree", context)
                .setName("Tree")));

        getOrCreateButtonGroup(context, VIEW_RADIO_GROUP).add(result);
        getOrCreateButtonGroup(context, VIEW_RADIO_GROUP).setSelected(result.getModel(), true);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newTextMenuItem(final Context context) {
        final JMenuItem result = Messages.translate(new JRadioButtonMenuItem(
                SwingTools.action(MarkupsActions.class, "text", context)
                .setName("Text")));

        getOrCreateButtonGroup(context, VIEW_RADIO_GROUP).add(result);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem newManualMenuItem(final Context context) {
        return item(
                "Manual", getKeyStroke("F1"),
                SubtitlesAdjusterActions.class, "showManual", context);
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JPanel newMainPanel(final Context context) {
        final JPanel result = new JPanel(new BorderLayout());

        result.add(newLayeredViews(context));

        new DropTarget(result, new DropTargetAdapter() {

            @Override
            public final void dragEnter(final DropTargetDragEvent event) {
                result.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            }

            @Override
            public final void dragExit(final DropTargetEvent event) {
                result.setBorder(null);
            }

            @Override
            public final void drop(final DropTargetDropEvent event) {
                result.setBorder(null);

                final List<File> files = SwingTools.getFiles(event);

                if (!files.isEmpty() && MarkupsActions.confirm(context)) {
                    context.set(FILE, files.get(0));
                }
            }

        });

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JPanel newLayeredViews(final Context context) {
        final JPanel views = new JPanel(new CardLayout());

        views.add(scrollable(newDOMTreeView(context)), MarkupsConstants.VIEW_MODE_TREE);
        views.add(scrollable(newDOMTextView(context)), MarkupsConstants.VIEW_MODE_TEXT);

        getOrCreateViewModeVariable(context).addListener(new Variable.Listener<String>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<String, ?> event) {
                ((CardLayout) views.getLayout()).show(views, event.getNewValue());
            }

        });

        return views;
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JTextPane newDOMTextView(final Context context) {
        final Variable<Node> domVariable = context.getVariable(DOM);
        final JTextPane result = new JTextPane();

        result.getDocument().addDocumentListener(new DocumentListener() {

            private final int[] newCaretPosition = new int[1];

            @Override
            public final void insertUpdate(final DocumentEvent event) {
                this.newCaretPosition[0] = result.getCaretPosition() + 1;
                this.updateContext();
            }

            @Override
            public final void removeUpdate(final DocumentEvent event) {
                this.newCaretPosition[0] = result.getCaretPosition() - 1;
                this.updateContext();
            }

            @Override
            public final void changedUpdate(final DocumentEvent event) {
                this.newCaretPosition[0] = result.getCaretPosition();
                this.updateContext();
            }

            private final void updateContext() {
                final int caretPosition = this.newCaretPosition[0];

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        debugPrint(result.getText());
                        if (!asString(domVariable.getValue()).equals(result.getText())) {
                            context.set(FILE_MODIFIED, true);
                            try {
                                domVariable.setValue(XMLTools.parse(result.getText()));
                                result.setCaretPosition(caretPosition);
                            } catch (final Exception exception) {
                                SubtitlesAdjusterActions.showErrorMessage(context, exception);
                            }
                        }
                    }

                });
            }

        });

        domVariable.addListener(new Variable.Listener<Node>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Node, ?> event) {
                result.setText(asString(domVariable.getValue()));
            }

        });

        result.setText(asString(domVariable.getValue()));

        return result;
    }

    /**
     *
     * @param node
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final String asString(final Node node) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        XMLTools.write(node, buffer, 0);

        return buffer.toString();
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JTree newDOMTreeView(final Context context) {
        final Variable<Node> domVariable = context.getVariable(DOM);
        final JTree result = new JTree(new DOMTreeModel(domVariable.getValue()));


        domVariable.addListener(new Variable.Listener<Node>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Node, ?> event) {
                final DOMTreeModel treeModel = new DOMTreeModel(event.getNewValue());

                result.setModel(treeModel);

                context.set(TREE_MODEL, treeModel);
            }

        });

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     * @param variableName
     * <br>Not null
     * @return
     * <br>Not null
     * <br>Maybe new
     */
    public static final ButtonGroup getOrCreateButtonGroup(final Context context, final String variableName) {
        ButtonGroup result = context.get(variableName);

        if (result == null) {
            context.set(variableName, result = new ButtonGroup());
        }

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     * @return
     * <br>Not null
     * <br>Maybe new
     */
    public static final Variable<String> getOrCreateViewModeVariable(final Context context) {
        Variable<String> result = context.getVariable(VIEW_MODE);

        if (result == null) {
            context.set(VIEW_MODE, MarkupsConstants.VIEW_MODE_TREE);
            result = context.getVariable(VIEW_MODE);
        }

        return result;
    }

    /**
     *
     * @author codistmonk (creation 2010-07-04)
     */
    public static final class DOMTreeModel extends DefaultTreeModel {

        /**
         *
         * @param domNode
         * <br>Not null
         * <br>Shared
         */
        public DOMTreeModel(final Node domNode) {
            super(newTreeNode(domNode));
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Node getDOMNode() {
            return (Node) ((DefaultMutableTreeNode) this.getRoot()).getUserObject();
        }

        private static final long serialVersionUID = 4264388285566053331L;

        /**
         *
         * @param domNode
         * <br>Not null
         * <br>Shared
         * @return
         * <br>Not null
         * <br>New
         */
        public static final DefaultMutableTreeNode newTreeNode(final Node domNode) {
            final DefaultMutableTreeNode result = new DefaultMutableTreeNode(domNode) {

                @Override
                public final String toString() {
                    switch (domNode.getNodeType()) {
                        case Node.ATTRIBUTE_NODE:
                            return domNode.getNodeName() +
                                    (domNode.getNodeValue() == null ? "" : "=\"" + domNode.getNodeValue() + "\"");
                        default:
                            return domNode.getNodeName() +
                                    (domNode.getNodeValue() == null ? "" : "[" + domNode.getNodeValue() + "]");
                    }
                }

                private static final long serialVersionUID = 8090552131823122052L;

            };
            final NamedNodeMap attributes = domNode.getAttributes();

            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    result.add(newTreeNode(attributes.item(i)));
                }
            }

            if (domNode.getNodeType() != Node.ATTRIBUTE_NODE) {
                for (final Node domChild : XMLTools.toList(domNode.getChildNodes())) {
                    result.add(newTreeNode(domChild));
                }
            }


            return result;
        }

    }

}
