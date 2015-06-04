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

package multij.context;

import static multij.events.EventsTestingTools.*;
import static org.junit.Assert.*;
import multij.context.Context;
import multij.context.Context.Listener;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link Context}.
 *
 * @author codistmonk (creation 2010-06-23)
 */
public final class ContextTest {

    @Test
    public final <R extends EventRecorder<?> & Listener> void testSetAndGet() {
        final Context context = new Context();
        @SuppressWarnings("unchecked")
        final R recorder = (R) newEventRecorder(Context.Listener.class);

        context.addListener(recorder);

        assertNull(context.set("x", 42));
        assertNull(context.set("y", "z"));
        assertEquals(42, (Object) context.set("x", 33));

        assertEquals((Object) 33, context.get("x"));
        assertEquals("z", context.get("y"));

        context.remove("x");
        context.remove("y");

        assertTrue(recorder.getEvents().get(0) instanceof Context.VariableAddedEvent<?>);
        assertTrue(recorder.getEvents().get(1) instanceof Context.VariableAddedEvent<?>);
        assertTrue(recorder.getEvents().get(2) instanceof Context.VariableRemovedEvent<?>);
        assertTrue(recorder.getEvents().get(3) instanceof Context.VariableRemovedEvent<?>);
        assertEquals(4, recorder.getEvents().size());
   }

}