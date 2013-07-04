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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.aprog.tools.AbstractInvocationHandler;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.tools.Tools;

/**
 * @author codistmonk (creation 2010-07-04)
 */
public final class EventsTestingTools {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private EventsTestingTools() {
        throw new IllegalInstantiationException();
    }

    /**
     *
     * @param <R> The (multi)listener recorder proxy type
     * @param listenerTypes
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    @SuppressWarnings("unchecked")
    public static final <R extends EventRecorder<?>> R newEventRecorder(
            final Class<?>... listenerTypes) {
        return (R) Proxy.newProxyInstance(
                Tools.getCallerClass().getClassLoader(),
                Tools.append(listenerTypes, EventRecorder.class),
                new RecorderInvocationHandler<Object>());
    }

    /**
     * @author codistmonk (creation 2010-06-18)
     *
     * @param <E> The base event type
     */
    public static interface EventRecorder<E> {

        /**
         *
         * @return
         * <br>Not null
         * <br>Not shared
         */
        public abstract List<E> getEvents();

        /**
         *
         * @param <T> the expected event type
         * @param index
         * <br>Range: {@code [0 .. this.getEvents().size() - 1]}
         * @return
         * <br>Not null
         * <br>Shared
         * @throws IndexOutOfBoundsException if {@code index} is out of range
         */
        public abstract <T extends E> T getEvent(int index);

    }

    /**
     *
     * @author codistmonk (creation 2010-06-18)
     * @param <E> The events base type
     */
    private static class RecorderInvocationHandler<E> extends AbstractInvocationHandler implements EventRecorder<E> {

        private final List<E> events;

        RecorderInvocationHandler() {
            this.events = new ArrayList<E>();
        }

        @Override
        @SuppressWarnings("unchecked")
        public final Object invoke(final Object proxy, final Method method, final Object[] arguments)
                throws Throwable {
            if (method.getDeclaringClass().isAssignableFrom(EventRecorder.class)) {
                return method.invoke(this, arguments);
            }

            if (arguments.length == 1) {
                this.events.add((E) arguments[0]);
            }

            return null;
        }

        @Override
        public final List<E> getEvents() {
            return Collections.unmodifiableList(this.events);
        }

        @Override
        @SuppressWarnings("unchecked")
        public final <T extends E> T getEvent(final int index) {
            return (T) this.getEvents().get(index);
        }

    }

}
