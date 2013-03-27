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

import java.util.ArrayList;
import java.util.Collection;

/**
 * This abstract class provides a default implementation of {@link Observable} to be used as a base class
 * for observable objects.
 * <br>A default abstract event implementation ({@link AbstractEvent}) is also provided.
 * <br>The use of {@link AbstractEvent} is recommended but not mandatory;
 * you can define your own event hierarchy and dispatching mechanism.
 *
 * @param <L> The event listener type
 * @author codistmonk (creation 2010-06-14)
 */
public abstract class AbstractObservable<L> implements Observable<L> {

    private final Collection<L> listeners;

    public AbstractObservable() {
        this.listeners = new ArrayList<L>();
    }

    @Override
    public final synchronized void addListener(final L listener) {
        this.listeners.add(listener);
    }

    @Override
    public final synchronized void removeListener(final L listener) {
        this.listeners.remove(listener);
    }

    @Override
    public final synchronized Iterable<L> getListeners() {
        return new ArrayList<L>(this.listeners);
    }

    /**
     * This abstract event class provides simpler constructors than {@link Observable.AbstractEvent}, but
     * derived concrete classes need to be instantiated from a subclass of {@link AbstractObservable}.
     *
     * @param <S> The event source type
     * @param <L2> The event listener type
     * <br>XXX
     * <br>Specifying {@code L2} seems redundant and even wrong because it should be {@code L}.
     * <br>This additional parameter is needed in NetBeans
     * to avoid compilation errors of the form: "type parameter ... is not within its bound".
     * <br>Hopefully someday this will be fixed and {@code L2} will no longer be needed.
     * @author codistmonk (creation 2010-06-15)
     */
    public abstract class AbstractEvent<S extends AbstractObservable<L2>, L2> extends Observable.AbstractEvent<S, L2> {

        /**
         * @param time in milliseconds
         * <br>Range: {@code [0 .. Long.MAX_VALUE]}
         */
        @SuppressWarnings("unchecked")
        protected AbstractEvent(final long time) {
            super((S) AbstractObservable.this, time);
        }

        @SuppressWarnings("unchecked")
        protected AbstractEvent() {
            super((S) AbstractObservable.this);
        }

    }

}
