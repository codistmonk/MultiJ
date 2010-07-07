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

import static net.sourceforge.aprog.tools.Tools.*;

import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.xml.XMLTools;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author codistmonk (creation 2010-07-04)
 */
public final class MarkupsXMLTools {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private MarkupsXMLTools() {
        throw new IllegalInstantiationException();
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

//        debugPrint("../" + selector);

        if (set(Node.ATTRIBUTE_NODE, Node.DOCUMENT_NODE, Node.DOCUMENT_FRAGMENT_NODE,
                Node.ENTITY_NODE, Node.NOTATION_NODE).contains(node.getNodeType())) {
            return getIdentifyingXPath(XMLTools.getNode(node, "..")) +
                    "/" + selector;
        }

        final NodeList siblings = XMLTools.getNodes(node, "../" + selector);

        return getIdentifyingXPath(node.getParentNode()) +
                "/" + selector + "[" + (indexOf(siblings, node) + 1) + "]";
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

}
