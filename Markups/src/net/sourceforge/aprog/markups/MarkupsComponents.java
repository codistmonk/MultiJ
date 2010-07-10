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

import static net.sourceforge.aprog.i18n.Messages.*;
import static net.sourceforge.aprog.markups.MarkupsConstants.Variables.*;
import static net.sourceforge.aprog.markups.MarkupsTools.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.menu;
import static net.sourceforge.aprog.swing.SwingTools.checkAWT;
import static net.sourceforge.aprog.swing.SwingTools.menuBar;
import static net.sourceforge.aprog.swing.SwingTools.packAndCenter;
import static net.sourceforge.aprog.swing.SwingTools.scrollable;
import static net.sourceforge.aprog.tools.Tools.*;
import static net.sourceforge.aprog.xml.XMLTools.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.Variable;
import net.sourceforge.aprog.events.Variable.ValueChangedEvent;
import net.sourceforge.aprog.i18n.Translator;
import net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterActions;
import net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterComponents;
import net.sourceforge.aprog.swing.SwingTools;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.tools.Tools;
import net.sourceforge.aprog.xml.XMLTools;
import net.sourceforge.jmacadapter.MacAdapterTools;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.MutationEvent;

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
        result.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

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

        final EventListener domListener = new EventListener() {

            @Override
            public final void handleEvent(final Event event) {
                result.repaint();
            }

        };

        addListener(context, DOM, new AbstractDOMListenerReattacher(domListener) {
            // Deliberately left empty
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
        final Variable<Node> selectedNodeVariable = context.getVariable(SELECTED_NODE);
        final EventListener nodeListener = new EventListener() {

            @Override
            public final void handleEvent(final Event event) {
                final Node node = selectedNodeVariable.getValue();

                result.setText(node == null ? "" : node.getNodeName());
            }

        };

        selectedNodeVariable.addListener(new Variable.Listener<Node>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Node, ?> event) {
                if (event.getOldValue() != null) {
                    removeDOMEventListener(event.getOldValue(), nodeListener);
                }

                final Node node = event.getNewValue();

                if (node != null) {
                    addDOMEventListener(node, nodeListener);
                }

                result.setEditable(node != null && set(Node.ATTRIBUTE_NODE,
                        Node.ELEMENT_NODE).contains(node.getNodeType()));

                result.setText(node == null ? "" : node.getNodeName());
            }

        });

        result.getDocument().addDocumentListener(new AbstractDocumentHandler() {

            @Override
            protected final void eventReceived(final DocumentEvent event) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public final void run() {
                        final Node node = selectedNodeVariable.getValue();
                        final String text = result.getText();

                        if (node == null || node.getNodeName().equals(text)) {
                            return;
                        }

                        try {
                            rename(
                                    node,
                                    node.getNamespaceURI(),
                                    node.getNamespaceURI() == null ? text : node.getPrefix() + ":" + text);
                        } catch (final Exception exception) {
                            exception.printStackTrace();
                            result.setText(node.getNodeName());
                        }
                    }

                });
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
        final Variable<Node> selectedNodeVariable = context.getVariable(SELECTED_NODE);
        final JTextArea result = new JTextArea();

        final EventListener nodeListener = new EventListener() {

            @Override
            public final void handleEvent(final Event event) {
                final Node node = selectedNodeVariable.getValue();

                if (event.getTarget() == node) {
                    result.setText(node == null ? "" : node.getNodeValue());
                }
            }

        };

        selectedNodeVariable.addListener(new Variable.Listener<Node>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Node, ?> event) {
                if (event.getOldValue() != null) {
                    removeDOMEventListener(event.getOldValue(), nodeListener);
                }

                final Node node = event.getNewValue();

                if (node != null) {
                    addDOMEventListener(node, nodeListener);
                }

                result.setEditable(node != null && set(Node.ATTRIBUTE_NODE, Node.CDATA_SECTION_NODE, Node.COMMENT_NODE,
                        Node.PROCESSING_INSTRUCTION_NODE, Node.TEXT_NODE).contains(node.getNodeType()));

                result.setText(node == null ? "" : node.getNodeValue());
            }

        });

        result.getDocument().addDocumentListener(new AbstractDocumentHandler() {

            @Override
            protected final void eventReceived(final DocumentEvent event) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public final void run() {
                        final Node node = selectedNodeVariable.getValue();
                        final String text = result.getText();

                        if (node == null || emptyIfNull(node.getNodeValue()).equals(emptyIfNull(text))) {
                            return;
                        }

                        try {
                            node.setNodeValue(text);
                        } catch (final Exception exception) {
                            exception.printStackTrace();
                            result.setText(node.getNodeValue());
                        }
                    }

                });
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
        final Variable<Node> selectedNodeVariable = context.getVariable(SELECTED_NODE);
        final JTextField result = new JTextField();

        final EventListener nodeListener = new EventListener() {

            @Override
            public final void handleEvent(final Event event) {
                final Node node = selectedNodeVariable.getValue();

                if (event.getTarget() == node) {
                    result.setText(node == null ? "" : getIdentifyingXPath(node));
                }
            }

        };

        selectedNodeVariable.addListener(new Variable.Listener<Node>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Node, ?> event) {
                if (event.getOldValue() != null) {
                    removeDOMEventListener(event.getOldValue(), nodeListener);
                }

                final Node node = event.getNewValue();

                if (node != null) {
                    addDOMEventListener(node, nodeListener);
                }

                result.setText(node == null ? "" : getIdentifyingXPath(node));
            }

        });

        result.setEditable(false);

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

        updateVariableOnTextChanged(context, XPATH_EXPRESSION, result);

        highlightBackgroundOnError(context, XPATH_ERROR, result);

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
        final DefaultListModel model = new DefaultListModel();
        final JList result = new JList(model);

        addListener(context, XPATH_RESULT, new Variable.Listener<NodeList>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<NodeList, ?> event) {
                model.clear();

                if (event.getNewValue() != null) {
                    for (final Node node : toList(event.getNewValue())) {
                        model.addElement(node);
                    }
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
    public static final JPanel newQuasiXPathPanel(final Context context) {
        final JPanel result = new JPanel(new BorderLayout());

        result.add(newTitledPanel("Quasi-XPath Expression", newQuasiXPathExpressionTextArea(context)), BorderLayout.CENTER);
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

        updateVariableOnTextChanged(context, QUASI_XPATH_EXPRESSION, result);

        highlightBackgroundOnError(context, QUASI_XPATH_ERROR, result);

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

        result.addActionListener(SwingTools.action(
                MarkupsActions.class, "evaluateQuasiXPathExpression", context));

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
        final Variable<Node> domVariable = context.getVariable(DOM);
        final JTree result = new JTree(new DOMTreeModel(domVariable.getValue()));

        domVariable.addListener(new Variable.Listener<Node>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Node, ?> event) {
                result.setModel(new DOMTreeModel(event.getNewValue()));
            }

        });

        result.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public final void valueChanged(final TreeSelectionEvent event) {
                if (event.getNewLeadSelectionPath() == null) {
                    context.set(SELECTED_NODE, null);

                    return;
                }

                final DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode) event.getNewLeadSelectionPath().getLastPathComponent();

                if (selectedTreeNode != null) {
                    context.set(SELECTED_NODE, (Node) selectedTreeNode.getUserObject());
                }
            }

        });

        addListener(context, SELECTED_NODE, new Variable.Listener<Node>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Node, ?> event) {
                final DOMTreeModel treeModel = (DOMTreeModel) result.getModel();
                final DOMTreeModel.XMLTreeNode treeNode = treeModel.get(event.getNewValue());

                if (treeNode != null) {
                    result.setSelectionPath(new TreePath(treeModel.getPathToRoot(treeNode)));
                } else {
                    result.setSelectionPath(null);
                }
            }

        });

        return result;
    }

    /**
     *
     * @author codistmonk (creation 2010-07-04)
     */
    public static final class DOMTreeModel extends DefaultTreeModel {

        private final WeakHashMap<Node, XMLTreeNode> xmlTreeNodes;

        /**
         *
         * @param domNode
         * <br>Not null
         * <br>Shared
         */
        public DOMTreeModel(final Node domNode) {
            super(new DefaultMutableTreeNode());
            this.xmlTreeNodes = new WeakHashMap<Node, XMLTreeNode>();

            this.setRoot(this.new XMLTreeNode(domNode));
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

        /**
         *
         * @param domNode
         * <br>Not null
         * <br>Shared
         * @param xmlTreeNode
         * <br>Not null
         * <br>Shared
         */
        final void put(final Node domNode, final XMLTreeNode xmlTreeNode) {
            this.xmlTreeNodes.put(domNode, xmlTreeNode);
        }

        /**
         *
         * @param domNode
         * <br>Not null
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        final XMLTreeNode get(final Node domNode) {
            return this.xmlTreeNodes.get(domNode);
        }

        private static final long serialVersionUID = 4264388285566053331L;

        /**
         *
         * @author codistmonk (creation 2010-07-10)
         */
        public final class XMLTreeNode extends DefaultMutableTreeNode {

            /**
             *
             * @param domNode
             * <br>Not null
             * <br>Shared
             */
            public XMLTreeNode(final Node domNode) {
                super(domNode);

                this.getTreeModel().put(domNode, this);

                addDOMEventListener(domNode, new EventListener() {

                    @Override
                    public final void handleEvent(final Event event) {
//                        debugPrint("\ntype", event.getType(), "\ntarget", event.getTarget());
                        if (event.getTarget() == domNode) {
                            if (DOM_EVENT_NODE_REMOVED.equals(event.getType())) {
                                XMLTreeNode.this.getTreeModel().removeNodeFromParent(XMLTreeNode.this);
                            }
                            if (DOM_EVENT_NODE_INSERTED.equals(event.getType())) {
                                final Node domParent = getNode(domNode, "..");
                                final XMLTreeNode parent = XMLTreeNode.this.getTreeModel().get(domParent);
                                final List<Node> siblings = getChildren(domParent);

                                debugPrint(parent);
                                debugPrint(siblings);
                                debugPrint(siblings.indexOf(domNode));

                                XMLTreeNode.this.getTreeModel().insertNodeInto(XMLTreeNode.this, parent, siblings.indexOf(domNode));
                            }
//                            debugPrint(event);
//                            debugPrint("type", event.getType());
//                            debugPrint("target", event.getTarget());
//                            debugPrint("currentTarget", event.getCurrentTarget());
//
//                            if (event instanceof MutationEvent) {
//                                debugPrint("relatedNode", ((MutationEvent) event).getRelatedNode());
//                                debugPrint("attrChange", ((MutationEvent) event).getAttrChange());
//                                debugPrint("attrName", ((MutationEvent) event).getAttrName());
//                                debugPrint("prevValue", ((MutationEvent) event).getPrevValue());
//                                debugPrint("newValue", ((MutationEvent) event).getNewValue());
//                            }
                            // TODO
                        }
                    }

                });

                for (final Node domChild : getChildren(domNode)) {
                    this.add(new XMLTreeNode(domChild));
                }
            }

            /**
             *
             * @return
             * <br>Not null
             * <br>Shared
             */
            public final Node getDomNode() {
                return (Node) this.getUserObject();
            }

            /**
             *
             * @return
             * <br>Not null
             * <br>Shared
             */
            public final DOMTreeModel getTreeModel() {
                return DOMTreeModel.this;
            }

            @Override
            public final String toString() {
                switch (this.getDomNode().getNodeType()) {
                    case Node.ATTRIBUTE_NODE:
                        return this.getDomNode().getNodeName() +
                                (this.getDomNode().getNodeValue() == null ? "" : "=\"" + this.getDomNode().getNodeValue() + "\"");
                    default:
                        return this.getDomNode().getNodeName() +
                                (this.getDomNode().getNodeValue() == null ? "" : "[" + this.getDomNode().getNodeValue() + "]");
                }
            }

            private static final long serialVersionUID = 8090552131823122052L;

        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-07)
     */
    public static abstract class AbstractDocumentHandler implements DocumentListener {

        @Override
        public final void insertUpdate(final DocumentEvent event) {
            this.doInsertUpdate(event);
        }

        @Override
        public final void removeUpdate(final DocumentEvent event) {
            this.doRemoveUpdate(event);
        }

        @Override
        public final void changedUpdate(final DocumentEvent event) {
            this.doChangedUpdate(event);
        }

        /**
         *
         * @param event
         * <br>Not null
         */
        protected void doInsertUpdate(final DocumentEvent event) {
            this.eventReceived(event);
        }

        /**
         *
         * @param event
         * <br>Not null
         */
        protected void doRemoveUpdate(final DocumentEvent event) {
            this.eventReceived(event);
        }

        /**
         *
         * @param event
         * <br>Not null
         */
        protected void doChangedUpdate(final DocumentEvent event) {
            this.eventReceived(event);
        }

        /**
         *
         * @param event
         * <br>Not null
         */
        protected abstract void eventReceived(DocumentEvent event);

    }

}
