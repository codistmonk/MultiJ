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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import static javax.swing.KeyStroke.getKeyStroke;
import net.sourceforge.aprog.events.Variable.ValueChangedEvent;

import static net.sourceforge.aprog.markups.Constants.Variables.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.menu;
import static net.sourceforge.aprog.swing.SwingTools.checkAWT;
import static net.sourceforge.aprog.swing.SwingTools.menuBar;
import static net.sourceforge.aprog.swing.SwingTools.packAndCenter;
import static net.sourceforge.aprog.swing.SwingTools.scrollable;

import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.Variable;
import net.sourceforge.aprog.i18n.Messages;
import net.sourceforge.aprog.i18n.Translator;
import net.sourceforge.aprog.swing.SwingTools;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.xml.XMLTools;
import net.sourceforge.jmacadapter.MacAdapterTools;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author codistmonk (creation 2010-07-03)
 */
public final class Components {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private Components() {
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
                Actions.class, "quit", context));

        invokeOnVariableChanged(context, FILE,
                net.sourceforge.aprog.subtitlesadjuster.Actions.class, "updateMainFrameTitle", context);
        invokeOnVariableChanged(context, FILE_MODIFIED,
                net.sourceforge.aprog.subtitlesadjuster.Actions.class, "updateMainFrameTitle", context);

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

        return net.sourceforge.aprog.subtitlesadjuster.Components.newAboutMenuItem(context);
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

        return net.sourceforge.aprog.subtitlesadjuster.Components.newPreferencesMenuItem(context);
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
                    Actions.class, "quit", context)) {
                return null;
            }
        }

        return item("Quit", getKeyStroke(META + " Q"),
                Actions.class, "quit", context);
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
                Actions.class, "newFile", context);
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
                Actions.class, "open", context);
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
                Actions.class, "save", context);
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
                Actions.class, "saveAs", context);

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
                Actions.class, "undo", context);

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
                Actions.class, "redo", context);

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
                Actions.class, "copy", context);

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
                Actions.class, "cut", context);

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
                Actions.class, "paste", context);

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
                SwingTools.action(Actions.class, "tree", context)
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
                SwingTools.action(Actions.class, "text", context)
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
                net.sourceforge.aprog.subtitlesadjuster.Actions.class, "showManual", context);
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

        result.add(scrollable(newDOMTreeView(context)));

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

                if (!files.isEmpty() && Actions.confirm(context)) {
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
    public static final JTree newDOMTreeView(final Context context) {
        final JTree result = new JTree(new DOMTreeModel(XMLTools.newDocument()));

        final Variable<File> fileVariable = context.getVariable(FILE);

        fileVariable.addListener(new Variable.Listener<File>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<File, ?> event) {
                final Document domDocument = event.getNewValue() == null ? XMLTools.newDocument() :
                    XMLTools.parse(new InputSource(event.getNewValue().getAbsolutePath()));
                final DOMTreeModel treeModel = new DOMTreeModel(domDocument);

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
            final DefaultMutableTreeNode result = new DefaultMutableTreeNode(domNode);
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
