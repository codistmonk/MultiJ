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

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.Collection;
import java.util.List;

import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.xml.XMLTools;

import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

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
     * <br>Not null
     * <br>Input-output
     * @param listener
     * <br>Not null
     * <br>Shared
     * @throws IllegalArgumentException If {@code node} doesn't have the "events 2.0" feature
     */
    public static final void addDOMEventListener(final Node node, final EventListener listener) {
        checkHasEventsFeature(node);

        for (final String eventType : DOM_EVENT_TYPES) {
            ((EventTarget) node).addEventListener(eventType, listener, false);
        }
    }

    /**
     *
     * @param node
     * <br>Not null
     * <br>Input-output
     * @param listener
     * <br>Not null
     * <br>Shared
     * @throws IllegalArgumentException If {@code node} doesn't have the "events 2.0" feature
     */
    public static final void removeDOMEventListener(final Node node, final EventListener listener) {
        checkHasEventsFeature(node);

        for (final String eventType : DOM_EVENT_TYPES) {
            ((EventTarget) node).removeEventListener(eventType, listener, false);
        }
    }

    /**
     *
     * @param node
     * @throws IllegalArgumentException If {@code node} doesn't have the "events 2.0" feature
     */
    public static final void checkHasEventsFeature(final Node node) {
        if (!XMLTools.getOwnerDocument(node).getImplementation().hasFeature("events", "2.0")) {
            throw new IllegalArgumentException("Events 2.0 feature unavailable for node " + node);
        }
    }

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_SUBTREE_MODIFIED = "DOMSubtreeModified";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_NODE_INSERTED = "DOMNodeInserted";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_NODE_REMOVED = "DOMNodeRemoved";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_NODE_REMOVED_FROM_DOCUMENT = "DOMNodeRemovedFromDocument";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_NODE_INSERTED_INTO_DOCUMENT = "DOMNodeInsertedIntoDocument";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_ATTRIBUTE_MODIFIED = "DOMAttrModified";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_CHARACTER_DATA_MODIFIED = "DOMCharacterDataModified";

    public static final List<String> DOM_EVENT_TYPES = unmodifiableList(asList(
            DOM_EVENT_ATTRIBUTE_MODIFIED,
            DOM_EVENT_CHARACTER_DATA_MODIFIED,
            DOM_EVENT_NODE_INSERTED,
            DOM_EVENT_NODE_INSERTED_INTO_DOCUMENT,
            DOM_EVENT_NODE_REMOVED,
            DOM_EVENT_NODE_REMOVED_FROM_DOCUMENT,
            DOM_EVENT_SUBTREE_MODIFIED
    ));

}
