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
import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;
import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.Variable;
import net.sourceforge.aprog.events.Variable.ValueChangedEvent;
import net.sourceforge.aprog.i18n.Messages;
import net.sourceforge.aprog.markups.MarkupsComponents.AbstractDocumentHandler;
import static net.sourceforge.aprog.tools.Tools.*;

import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.tools.Tools;
import net.sourceforge.aprog.xml.XMLTools;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;

/**
 *
 * @author codistmonk (creation 2010-07-04)
 */
public final class MarkupsTools {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private MarkupsTools() {
        throw new IllegalInstantiationException();
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

        result.setBorder(Messages.translate(BorderFactory.createTitledBorder(translationKey)));

        result.add(component);

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     * @param variableName
     * <br>Not null
     * <br>Shared
     * @param textComponent
     * <br>Not null
     * <br>Shared
     * <br>Input-output
     */
    public static final void updateVariableOnTextChanged(final Context context, final String variableName,
            final JTextComponent textComponent) {
        textComponent.getDocument().addDocumentListener(new AbstractDocumentHandler() {

            @Override
            protected final void eventReceived(final DocumentEvent event) {
                context.set(variableName, textComponent.getText());
            }

        });
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     * @param errorVariableName
     * <br>Not null
     * <br>Shared
     * @param component
     * <br>Not null
     * <br>Shared
     */
    public static final void highlightBackgroundOnError(final Context context, final String errorVariableName,
            final Component component) {
        addListener(context, errorVariableName, new Variable.Listener<Object>() {

            private Color defaultBackground;

            @Override
            public final void valueChanged(final ValueChangedEvent<Object, ?> event) {
                if (this.defaultBackground == null) {
                    this.defaultBackground = component.getBackground();
                }

                component.setBackground(event.getNewValue() == null ? this.defaultBackground : Color.RED);
            }

        });
    }

    /**
     *
     * @param <T> The variable value type
     * @param context
     * <br>Not null
     * <br>Input-output
     * @param variableName
     * <br>Not null
     * @param listener
     * <br>Not null
     * <br>Shared
     */
    public static final <T> void addListener(final Context context, final String variableName,
            final Variable.Listener<T> listener) {
        final Variable<T> variable = context.getVariable(variableName);

        variable.addListener(listener);
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
     * @param node
     * <br>Maybe null
     * @return
     * <br>Not null
     */
    public static final String getIdentifyingXPath(final Node node) {
        if (node == null || node.getNodeType() == Node.DOCUMENT_NODE) {
            return "/";
        }

        final String selector = getXPathSelector(node);

        debugPrint("../" + selector);
        debugPrint(node.getParentNode());

        final Node parent = node.getParentNode() != null ? node.getParentNode() : XMLTools.getNode(node, "..");
        final StringBuilder pathSelector = new StringBuilder(getIdentifyingXPath(parent));

        if (!pathSelector.toString().endsWith("/")) {
            pathSelector.append("/");
        }

        if (set(Node.ATTRIBUTE_NODE, Node.DOCUMENT_FRAGMENT_NODE,
                Node.ENTITY_NODE, Node.NOTATION_NODE).contains(node.getNodeType())) {
            return pathSelector + selector;
        }

        final NodeList siblings = XMLTools.getNodes(parent, selector);

        return pathSelector + selector + "[" + (indexOf(siblings, node) + 1) + "]";
    }

    /**
     * Returns a XPath expression that can be used to selects nodes like {@code node}.
     *
     * @param node
     * <br>Not null
     * @return
     * <br>Not null
     */
    public static final String getXPathSelector(final Node node) {
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
     * @return {@code -1} if {@code nodes} doesn't contain {@code node}
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
     * @param node
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final List<Node> getAttributeChildren(final Node node) {
        final List<Node> result = new ArrayList<Node>();
        final NamedNodeMap attributes = node.getAttributes();

        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); ++i) {
                result.add(attributes.item(i));
            }
        }

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
    public static final List<Node> getNonattributeChildren(final Node node) {
        final List<Node> result = new ArrayList<Node>();

        if (node.getNodeType() != Node.ATTRIBUTE_NODE) {
            for (final Node domChild : XMLTools.toList(node.getChildNodes())) {
                result.add(domChild);
            }
        }

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
    public static final List<Node> getChildren(final Node node) {
        final List<Node> result = new ArrayList<Node>();

        result.addAll(getAttributeChildren(node));
        result.addAll(getNonattributeChildren(node));

        return result;
    }

}

/**
 *
 * @author codistmonk (creation 2010-07-07)
 */
abstract class AbstractDOMListenerReattacher implements Variable.Listener<Node> {

    private final EventListener domListener;

    /**
     *
     * @param domListener
     * <br>Not null
     * <br>Shared
     */
    public AbstractDOMListenerReattacher(final EventListener domListener) {
        this.domListener = domListener;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    public final EventListener getDOMListener() {
        return this.domListener;
    }

    @Override
    public final void valueChanged(final ValueChangedEvent<Node, ?> event) {
        if (event.getOldValue() != null) {
            XMLTools.removeDOMEventListener(event.getOldValue(), this.getDOMListener());
        }

        final Node dom = event.getNewValue();

        if (dom != null) {
            XMLTools.addDOMEventListener(dom, this.getDOMListener());
        }
    }

    /**
     * The default implementation does nothing.
     * 
     * @param event
     * <br>Not null
     */
    protected void afterReattachment(final ValueChangedEvent<Node, ?> event) {
        Tools.suppressWarningUnused(event);
    }

}
