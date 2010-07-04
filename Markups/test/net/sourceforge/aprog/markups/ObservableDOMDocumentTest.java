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

import static net.sourceforge.aprog.events.ObservableTest.*;
import static net.sourceforge.aprog.markups.ObservableDOMDocument.*;
import static net.sourceforge.aprog.tools.Tools.*;

import static org.junit.Assert.*;

import net.sourceforge.aprog.events.ObservableTest.*;
import net.sourceforge.aprog.markups.ObservableDOMDocument.*;
import net.sourceforge.aprog.xml.XMLTools;

import org.junit.Test;
import org.w3c.dom.Node;

/**
 * Automated tests using JUnit 4 for {@link ObservableDOMDocument}.
 * <br>WARNING: This test reuses part of Aprog's tests, so Aprog's tests classes must be compiled before
 * trying to compile or run this test.
 *
 * @author codistmonk (creation 2010-07-03)
 */
public final class ObservableDOMDocumentTest {

    @Test
    public final <R extends EventRecorder & Listener> void testSetUserData() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        assertNull(document.getUserData(USER_DATA_KEY));

        document.addListener(recorder);
        document.setUserData(USER_DATA_KEY, 42, null);

        assertEquals(42, document.getUserData(USER_DATA_KEY));

        document.removeListener(recorder);
        document.setUserData(USER_DATA_KEY, 33, null);

        assertEquals(33, document.getUserData(USER_DATA_KEY));

        final UserDataChangedEvent event = recorder.getEvent(0);

        assertEquals(USER_DATA_KEY, event.getKey());
        assertEquals(null, event.getOldUserData());
        assertEquals(42, event.getNewUserData());
        assertEquals(1, recorder.getEvents().size());
    }

    @Test
    public final <R extends EventRecorder & Listener> void testReplaceChild() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        document.addListener(recorder);

        assertNull(document.getDocumentElement());

        final Node oldChild = document.createElement("root");
        final Node newChild = document.createElement("new-root");

        document.appendChild(oldChild);

        assertSame(oldChild, document.getDocumentElement());

        document.replaceChild(newChild, oldChild);

        assertSame(newChild, document.getDocumentElement());

        {
            final ChildReplacedEvent event = recorder.getEvent(1);

            assertSame(oldChild, event.getOldChild());
            assertSame(newChild, event.getNewChild());
        }

        assertEquals(2, recorder.getEvents().size());
    }

    @Test
    public final <R extends EventRecorder & Listener> void testNormalize() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        document.addListener(recorder);

        document.normalize();

        final NodeNormalizedEvent event = recorder.getEvent(0);

        assertSame(document, event.getEventNode());
        assertEquals(1, recorder.getEvents().size());
    }

    @Test
    public final void testCloneNode() {
        fail("TODO");
    }

    @Test
    public final <R extends EventRecorder & Listener> void testAppendChild() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        document.addListener(recorder);

        assertNull(document.getDocumentElement());

        final Node child = document.createElement("root");

        document.appendChild(child);

        assertSame(child, document.getDocumentElement());

        final ChildAppendedEvent event = recorder.getEvent(0);

        assertSame(child, event.getAppendedChild());
        assertEquals(1, recorder.getEvents().size());
    }

    @Test
    public final <R extends EventRecorder & Listener> void testSetXmlVersion() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        assertEquals("1.0", document.getXmlVersion());

        document.addListener(recorder);
        document.setXmlVersion("1.1");

        assertEquals("1.1", document.getXmlVersion());

        final XmlVersionChangedEvent event = recorder.getEvent(0);

        assertEquals("1.0", event.getOldXmlVersion());
        assertEquals("1.1", event.getNewXmlVersion());
        assertEquals(1, recorder.getEvents().size());
    }

    @Test
    public final <R extends EventRecorder & Listener> void testSetXmlStandalone() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        assertTrue(document.getXmlStandalone());

        document.addListener(recorder);
        document.setXmlStandalone(false);

        assertFalse(document.getXmlStandalone());

        final XmlStandaloneChangedEvent event = recorder.getEvent(0);

        assertTrue(event.getOldXmlStandalone());
        assertFalse(event.getNewXmlStandalone());
        assertEquals(1, recorder.getEvents().size());
    }

    @Test
    public final <R extends EventRecorder & Listener> void testSetStrictErrorChecking() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        assertTrue(document.getStrictErrorChecking());

        document.addListener(recorder);
        document.setStrictErrorChecking(false);

        assertFalse(document.getStrictErrorChecking());

        final StrictErrorCheckingChangedEvent event = recorder.getEvent(0);

        assertTrue(event.getOldStrictErrorChecking());
        assertFalse(event.getNewStrictErrorChecking());
        assertEquals(1, recorder.getEvents().size());
    }

    @Test
    public final <R extends EventRecorder & Listener> void testSetDocumentURI() {
        final String documentURI = NAMESPACE_URI + ":observable-document-test.xml";
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        assertNull(document.getDocumentURI());

        document.addListener(recorder);
        document.setDocumentURI(documentURI);

        assertEquals(documentURI, document.getDocumentURI());

        final DocumentURIChangedEvent event = recorder.getEvent(0);

        assertNull(event.getOldDocumentURI());
        assertEquals(documentURI, event.getNewDocumentURI());
        assertEquals(1, recorder.getEvents().size());
    }

    @Test
    public final <R extends EventRecorder & Listener> void testRenameNode() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);
        final Node node = document.createElement("root");

        assertNull(node.getNamespaceURI());
        assertEquals("root", getQualifiedName(node));

        document.addListener(recorder);

        {
            assertSame(node, document.renameNode(node, null, "renamed-root"));
            assertNull(node.getNamespaceURI());
            assertEquals("renamed-root", getQualifiedName(node));

            final NodeRenamedEvent event = recorder.getEvent(0);

            assertSame(node, event.getOldNode());
            assertNull(event.getOldNamespaceURI());
            assertEquals("root", event.getOldQualifiedName());
            assertSame(node, event.getNewNode());
            assertNull(event.getNewNamespaceURI());
            assertEquals("renamed-root", event.getNewQualifiedName());
        }
        {
            final Node newNode = document.renameNode(node, NAMESPACE_URI, "renamed:root");

            assertNotSame(node, newNode);
            assertEquals(NAMESPACE_URI, newNode.getNamespaceURI());
            assertEquals("renamed:root", getQualifiedName(newNode));

            final NodeRenamedEvent event = recorder.getEvent(1);

            assertSame(node, event.getOldNode());
            assertNull(event.getOldNamespaceURI());
            assertEquals("renamed-root", event.getOldQualifiedName());
            assertSame(newNode, event.getNewNode());
            assertEquals(NAMESPACE_URI, event.getNewNamespaceURI());
            assertEquals("renamed:root", event.getNewQualifiedName());
        }

        assertEquals(2, recorder.getEvents().size());
    }

    @Test
    public final <R extends EventRecorder & Listener> void testNormalizeDocument() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        document.addListener(recorder);

        document.normalizeDocument();

        final DocumentNormalizedEvent event = recorder.getEvent(0);

        assertNotNull(event);
        assertEquals(1, recorder.getEvents().size());
    }

    @Test
    public final <R extends EventRecorder & Listener> void testImportNode() {
        final ObservableDOMDocument document = new ObservableDOMDocument();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Listener.class);

        document.addListener(recorder);

        assertNull(document.getDocumentElement());

        final Node importedNode = document.importNode(XMLTools.parse("<a/>").getDocumentElement(), false);

        assertNotNull(importedNode);
        assertSame(document, importedNode.getOwnerDocument());
        assertEquals("a", importedNode.getNodeName());

        final NodeImportedEvent event = recorder.getEvent(0);

        assertSame(importedNode, event.getImportedNode());
        assertEquals(1, recorder.getEvents().size());
    }

    /**
     * {@value}.
     */
    private static final String USER_DATA_KEY = "key";

    /**
     * {@value}.
     */
    private static final String NAMESPACE_URI = "urn:net.sourceforge.aprog";

}