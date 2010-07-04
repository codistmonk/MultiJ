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
import static javax.swing.KeyStroke.getKeyStroke;

import static net.sourceforge.aprog.markups.Constants.Variables.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.menu;
import static net.sourceforge.aprog.swing.SwingTools.checkAWT;
import static net.sourceforge.aprog.swing.SwingTools.menuBar;
import static net.sourceforge.aprog.swing.SwingTools.packAndCenter;
import static net.sourceforge.aprog.swing.SwingTools.scrollable;

import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.i18n.Translator;
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
        result.add(newMainPanel());

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
                        newOpenMenuItem(context),
                        null,
                        newSaveMenuItem(context)
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

        net.sourceforge.aprog.subtitlesadjuster.Components
                .synchronizeComponentEnabledWithFileVariableNullity(result, context);

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
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JPanel newMainPanel() {
        final JPanel result = new JPanel(new BorderLayout());

        result.add(scrollable(newDOMTreeView()));

        return result;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JTree newDOMTreeView() {
        final JTree result = new JTree(new DOMTreeModel(XMLTools.parse("<a><b c='d'/><b c='e'/></a>")));

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

            for (final Node domChild : XMLTools.toList(domNode.getChildNodes())) {
                result.add(newTreeNode(domChild));
            }


            return result;
        }

    }

}
