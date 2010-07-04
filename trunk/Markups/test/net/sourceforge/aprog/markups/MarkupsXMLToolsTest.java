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

import static net.sourceforge.aprog.events.EventsTestingTools.*;
import static net.sourceforge.aprog.xml.XMLTools.*;

import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.MutationEvent;

/**
 * Automated tests using JUnit 4 for {@link MarkupsXMLTools}.
 * <br>WARNING: This test reuses part of Aprog's tests, so Aprog's tests classes must be compiled before
 * trying to compile or run this test.
 *
 * @author codistmonk (creation 2010-07-04)
 */
public final class MarkupsXMLToolsTest {

    @Test
    public final <R extends EventRecorder<Event> & EventListener> void testEvents() {
        final Document document = parse("<a><b c='d'/></a>");
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(EventListener.class);

        MarkupsXMLTools.addDOMEventListener(document, recorder);
        getNode(document, "a/b/@c").setNodeValue("e");

        {
            final MutationEvent event = recorder.getEvent(0);

            assertEquals(MarkupsXMLTools.DOM_EVENT_ATTRIBUTE_MODIFIED, event.getType());
            assertSame(getNode(document, "a/b"), event.getTarget());
            assertEquals(MutationEvent.MODIFICATION, event.getAttrChange());
            assertEquals("d", event.getPrevValue());
            assertEquals("e", event.getNewValue());
        }

        {
            final MutationEvent event = recorder.getEvent(1);

            assertEquals(MarkupsXMLTools.DOM_EVENT_SUBTREE_MODIFIED, event.getType());
            assertSame(getNode(document, "a/b"), event.getTarget());
            assertEquals(0, event.getAttrChange());
            assertNull(event.getPrevValue());
            assertNull(event.getNewValue());
        }

        assertEquals(2, recorder.getEvents().size());
    }

}