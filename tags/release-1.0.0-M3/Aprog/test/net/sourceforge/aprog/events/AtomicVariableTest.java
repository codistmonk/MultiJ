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

package net.sourceforge.aprog.events;

import static org.junit.Assert.*;

import net.sourceforge.aprog.events.ObservableTest.EventRecorder;
import net.sourceforge.aprog.events.Variable.Listener;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link AtomicVariable}.
 *
 * @author codistmonk (creation 2010-06-23)
 */
public final class AtomicVariableTest {

    @Test
    public final <R extends EventRecorder & Listener<Integer>> void testEvents() {
        final AtomicVariable<Integer> x = new AtomicVariable<Integer>(Integer.class, "x", 42);
        @SuppressWarnings("unchecked")
        final R recorder = (R) ObservableTest.newEventRecorder(Listener.class);

        x.addListener(recorder);

        assertEquals(42, (Object) x.getValue());

        x.setValue(33);

        assertEquals(33, (Object) x.getValue());
        assertEquals(1, recorder.getEvents().size());

        {
            final AtomicVariable<Integer>.ValueChangedEvent event = recorder.getEvent(0);

            assertSame(x, event.getSource());
            assertEquals(42, (Object) event.getOldValue());
            assertEquals(33, (Object) event.getNewValue());
        }

        x.setValue(x.getValue());

        assertEquals(1, recorder.getEvents().size());

        x.setValue(42);

        assertEquals(42, (Object) x.getValue());
        assertEquals(2, recorder.getEvents().size());

        {
            final AtomicVariable<Integer>.ValueChangedEvent event = recorder.getEvent(1);

            assertSame(x, event.getSource());
            assertEquals(33, (Object) event.getOldValue());
            assertEquals(42, (Object) event.getNewValue());
        }
    }

}