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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sourceforge.aprog.events.Observable.Event;
import net.sourceforge.aprog.tools.Tools;
import org.junit.Test;

/**
 *
 * @author codistmonk (creation 2010-06-23)
 */
public final class ObservableTest {

    @Test
    public final <R extends EventRecorder & DummyObservable.Listener> void testFireEvent() {
        final DummyObservable observable = new DummyObservable();
        @SuppressWarnings("unchecked")
        final R recorder1 = (R) newEventRecorder(DummyObservable.Listener.class);
        @SuppressWarnings("unchecked")
        final R recorder2 = (R) newEventRecorder(DummyObservable.Listener.class);

        observable.addListener(recorder1);
        observable.addListener(recorder2);
        observable.fireNewEvent();
        observable.removeListener(recorder2);
        observable.fireNewEvent();

        assertTrue(recorder1.getEvent(0) instanceof DummyObservable.EventFiredEvent);
        assertTrue(recorder1.getEvent(1) instanceof DummyObservable.EventFiredEvent);
        assertSame(recorder1.getEvent(0), recorder2.getEvent(0));
        assertEquals(2, recorder1.getEvents().size());
        assertEquals(1, recorder2.getEvents().size());
    }

    /**
     *
     * @param <R> the (multi)listener recorder proxy type
     * @param listenerTypes
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    @SuppressWarnings("unchecked")
    public static final <R extends EventRecorder> R newEventRecorder(
            final Class<?>... listenerTypes) {
        return (R) Proxy.newProxyInstance(
                Tools.getCallerClass().getClassLoader(),
                add(listenerTypes, EventRecorder.class),
                new RecorderInvocationHandler());
    }

    /**
     *
     * @param <T>
     * @param array
     * <br>Not null
     * @param moreElements
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    private static final <T> T[] add(final T[] array, final T... moreElements) {
        @SuppressWarnings("unchecked")
        final T[] result = (T[]) Array.newInstance(
                array.getClass().getComponentType(), array.length + moreElements.length);

        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(moreElements, 0, result, array.length, moreElements.length);

        return result;
    }

    /**
     *
     * @author codistmonk (creation 2010-06-18)
     */
    public static interface EventRecorder {

        /**
         *
         * @return
         * <br>Not null
         * <br>Not shared
         */
        public abstract List<Event<?>> getEvents();

        /**
         *
         * @param <E> the expected event type
         * @param index
         * <br>Range: {@code [0 .. this.getEvents().size() - 1]}
         * @return
         * <br>Not null
         * <br>Shared
         * @throws IndexOutOfBoundsException if {@code index} is out of range
         */
        public abstract <E extends Event<?>> E getEvent(int index);

    }

    /**
     *
     * @author codistmonk (creation 2010-06-18)
     */
    private static class RecorderInvocationHandler implements InvocationHandler, EventRecorder {

        private final List<Event<?>> events;

        RecorderInvocationHandler() {
            this.events = new ArrayList<Event<?>>();
        }

        @Override
        public final Object invoke(final Object proxy, final Method method, final Object[] arguments)
                throws Throwable {
            if (method.getDeclaringClass().isAssignableFrom(EventRecorder.class)) {
                return method.invoke(this, arguments);
            }

            if (arguments.length == 1 && arguments[0] instanceof Event<?>) {
                this.events.add((Event<?>) arguments[0]);
            }

            return null;
        }

        @Override
        public final List<Event<?>> getEvents() {
            return Collections.unmodifiableList(this.events);
        }

        @Override
        @SuppressWarnings("unchecked")
        public final <E extends Event<?>> E getEvent(final int index) {
            return (E) this.getEvents().get(index);
        }

        @Override
        public final boolean equals(final Object object) {
            return this == object ||
                    object != null &&
                    Proxy.isProxyClass(object.getClass()) &&
                    this == Proxy.getInvocationHandler(object);
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-06-23)
     */
    private static final class DummyObservable extends AbstractObservable<DummyObservable.Listener> {

        public final void fireNewEvent() {
            new EventFiredEvent().fire();
        }

        /**
         *
         * @author codistmonk (creation 2010-06-23)
         */
        public static interface Listener {

            /**
             *
             * @param event
             * <br>Not null
             */
            public abstract void eventFired(final EventFiredEvent event);

        }

        /**
         *
         * @author codistmonk (creation 2010-06-23)
         */
        public final class EventFiredEvent extends AbstractEvent<DummyObservable, Listener> {

            @Override
            protected final void notifyListener(final Listener listener) {
                listener.eventFired(this);
            }

        }

    }

}