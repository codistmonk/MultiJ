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
import javax.swing.event.TreeSelectionEvent;

import static net.sourceforge.aprog.i18n.Messages.*;
import static net.sourceforge.aprog.markups.MarkupsConstants.Variables.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.menu;
import static net.sourceforge.aprog.swing.SwingTools.checkAWT;
import static net.sourceforge.aprog.swing.SwingTools.menuBar;
import static net.sourceforge.aprog.swing.SwingTools.packAndCenter;
import static net.sourceforge.aprog.swing.SwingTools.scrollable;
import static net.sourceforge.aprog.tools.Tools.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
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
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.Variable;
import net.sourceforge.aprog.events.Variable.ValueChangedEvent;
import net.sourceforge.aprog.i18n.Translator;
import net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterActions;
import net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterComponents;
import net.sourceforge.aprog.swing.SwingTools;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.xml.XMLTools;
import net.sourceforge.jmacadapter.MacAdapterTools;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
                menu("Node",
                        newAppendNewNodeMenuItem(context),
                        null,
                        newMoveUpMenuItem(context),
                        newMoveDownMenuItem(context)
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
    public static final JMenuItem newAppendNewNodeMenuItem(final Context context) {
        final JMenuItem result = item("Append New Node",
                MarkupsActions.class, "appendNewNode", context);

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
    public static final JMenuItem newMoveUpMenuItem(final Context context) {
        final JMenuItem result = item("Move Up",
                MarkupsActions.class, "moveUp", context);

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
    public static final JMenuItem newMoveDownMenuItem(final Context context) {
        final JMenuItem result = item("Move Down",
                MarkupsActions.class, "moveDown", context);

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

        result.add(newMainSplitPane(context));

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
                    MarkupsActions.open(context, files.get(0));
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
    public static final JSplitPane newMainSplitPane(final Context context) {
        return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollable(newDOMTreeView(context)),
                newContextualSplitPane(context));
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JSplitPane newContextualSplitPane(final Context context) {
        return new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                newNodePanel(context),
                newXPathPanel(context));
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JPanel newNodePanel(final Context context) {
        final JPanel result = new JPanel(new BorderLayout());

        result.add(newNodeNamePanel(context), BorderLayout.NORTH);
        result.add(newNodeValuePanel(context), BorderLayout.CENTER);

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
    public static final JPanel newNodeNamePanel(final Context context) {
        return newTitledPanel("Name", newNodeNameTextField(context));
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JTextField newNodeNameTextField(final Context context) {
        final JTextField result = new JTextField();

        // TODO

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
    public static final JPanel newNodeValuePanel(final Context context) {
        return newTitledPanel("Value", scrollable(newNodeValueTextArea(context)));
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JTextArea newNodeValueTextArea(final Context context) {
        final JTextArea result = new JTextArea();

        // TODO

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
    public static final JPanel newXPathPanel(final Context context) {
        final JPanel result = new JPanel(new BorderLayout());

        result.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "XPath",
                TitledBorder.CENTER,
                TitledBorder.TOP));

        result.add(newTitledPanel("Context", newContextTextField(context)), BorderLayout.NORTH);
        result.add(newXPathTabbedPane(context), BorderLayout.CENTER);

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
    public static final JTextField newContextTextField(final Context context) {
        final JTextField result = new JTextField();
        final Variable<Node> selectedNodeVariable = getOrCreateVariable(context, SELECTED_NODE, null);

        result.setEditable(false);

        selectedNodeVariable.addListener(new Variable.Listener<Node>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Node, ?> event) {
                result.setText(event.getNewValue() == null ? "" : getIdentifyingXPath(event.getNewValue()));
            }

        });

        return result;
    }

    /**
     *
     * @param node
     * <br>Maybe null
     * @return
     * <br>Not null
     */
    public static final String getIdentifyingXPath(final Node node) {
        if (node == null || node.getNodeType() == Node.DOCUMENT_NODE) {
            return "/";
        }

        final String selector = getSelector(node);

        debugPrint("../" + selector);

        if (set(Node.ATTRIBUTE_NODE, Node.DOCUMENT_NODE, Node.DOCUMENT_FRAGMENT_NODE, Node.ENTITY_NODE, Node.NOTATION_NODE).contains(node.getNodeType())) {
            return getIdentifyingXPath(XMLTools.getNode(node, "..")) +
                    "/" + selector;
        }

        final NodeList siblings = XMLTools.getNodes(node, "../" + selector);

        return getIdentifyingXPath(node.getParentNode()) +
                "/" + selector + "[" + (indexOf(siblings, node) + 1) + "]";
    }

    /**
     *
     * @param node
     * <br>Not null
     * @return
     * <br>Not null
     */
    public static final String getSelector(final Node node) {
        if (node.getNodeName().startsWith("#")) {
            return node.getNodeName().toLowerCase().substring(1) + "()";
        }

        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            return "@" + node.getNodeName();
        }

        return node.getNodeName();
    }

    /**
     *
     * @param nodes
     * <br>Not null
     * @param node
     * <br>Maybe null
     * @return
     * <br>Range: {@code [-1 .. nodes.getLength() - 1]}
     */
    public static final int indexOf(final NodeList nodes, final Node node) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            if (nodes.item(i) == node) {
                return i;
            }
        }

        return -1;
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JTabbedPane newXPathTabbedPane(final Context context) {
        final JTabbedPane result = new JTabbedPane();

        result.add("List", newXPathListSplitPane(context));
        result.add("Create", newQuasiXPathPanel(context));

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
    public static final JSplitPane newXPathListSplitPane(final Context context) {
        return new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                newTitledPanel("XPath Expression", scrollable(newXPathExpressionTextArea(context))),
                newTitledPanel("Matches", scrollable(newXPathList(context))));
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JTextArea newXPathExpressionTextArea(final Context context) {
        final JTextArea result = new JTextArea();

        // TODO

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
    public static final JList newXPathList(final Context context) {
        final JList result = new JList();

        // TODO

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
    public static final JPanel newQuasiXPathPanel(final Context context) {
        final JPanel result = new JPanel(new BorderLayout());

        result.add(newTitledPanel("Quasi-XPath expression", newQuasiXPathExpressionTextArea(context)), BorderLayout.CENTER);
        result.add(newQuasiXPathCreateButton(context), BorderLayout.SOUTH);

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
    public static final JTextArea newQuasiXPathExpressionTextArea(final Context context) {
        final JTextArea result = new JTextArea();

        // TODO

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
    public static final JButton newQuasiXPathCreateButton(final Context context) {
        final JButton result = translate(new JButton("Create Node"));

        // TODO

        return result;
    }

    /**
     *
     * @param translationKey
     * <br>Not null
     * @param component
     * <br>Not null
     * <br>Input-output
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JPanel newTitledPanel(final String translationKey, final Component component) {
        final JPanel result = new JPanel(new BorderLayout());

        result.setBorder(translate(BorderFactory.createTitledBorder(translationKey)));

        result.add(component);

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
    public static final String toXMLString(final Node node) {
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

        result.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public final void valueChanged(final TreeSelectionEvent event) {
                final DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode) event.getNewLeadSelectionPath().getLastPathComponent();

                if (selectedTreeNode != null) {
                    context.set(SELECTED_NODE, (Node) selectedTreeNode.getUserObject());
                }
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
    public static final <T> Variable<T> getOrCreateVariable(final Context context, final String variableName, final T defaultValue) {
        Variable<T> result = context.getVariable(variableName);

        if (result == null) {
            context.set(variableName, defaultValue);
            result = context.getVariable(variableName);
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
