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

import java.util.concurrent.atomic.AtomicReference;

import net.sourceforge.aprog.tools.Tools;

/**
 *
 * @param <T> the type of the variable value
 * @author codistmonk (creation 2010-06-20)
 */
public final class AtomicVariable<T> extends AbstractObservable<Variable.Listener<T>> implements Variable<T> {

    private final Class<T> type;

    private final String name;

    private final AtomicReference<T> valueReference;

    /**
     *
     * @param type
     * <br>Not null
     * <br>Shared
     * @param name
     * <br>Not null
     * <br>Shared
     * @param value
     * <br>Maybe null
     * <br>Shared
     */
    public AtomicVariable(final Class<T> type, final String name, final T value) {
        this.type = type;
        this.name = name;
        this.valueReference = new AtomicReference<T>(value);
    }

    @Override
    public final Class<T> getType() {
        return this.type;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final T getValue() {
        return this.valueReference.get();
    }

    @Override
    public final void setValue(final T value) {
        if (this.getValue() != value) {
            this.new ValueChangedEvent(this.valueReference.getAndSet(this.getType().cast(value)), value).fire();
        }
    }

    @Override
    public final boolean equals(final Object object) {
        final Variable<T> that = Tools.castToCurrentClass(object);

        return this == that ||
                that != null &&
                this.getType().equals(that.getType()) &&
                this.getName().equals(that.getName()) &&
                Tools.equals(this.getValue(), that.getValue());
    }

    @Override
    public final int hashCode() {
        return this.getType().hashCode() + this.getName().hashCode();
    }

    @Override
    public final String toString() {
        return "AtomicVariable { " + this.getType() + " " + this.getName() + " " + this.getValue() + " }";
    }

    /**
     *
     * @author codistmonk (creation 2010-06-20)
     */
    public final class ValueChangedEvent
            extends AbstractObservable<Variable.Listener<T>>.AbstractEvent<AtomicVariable<T>, Variable.Listener<T>>
            implements Variable.ValueChangedEvent<T, AtomicVariable<T>> {

        private final T oldValue;

        private final T newValue;

        /**
         *
         * @param oldValue
         * <br>Maybe null
         * <br>Shared
         * @param newValue
         * <br>Maybe null
         * <br>Shared
         */
        public ValueChangedEvent(final T oldValue, final T newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public final T getOldValue() {
            return this.oldValue;
        }

        @Override
        public final T getNewValue() {
            return this.newValue;
        }

        @Override
        protected final void notifyListener(final Variable.Listener<T> listener) {
            listener.valueChanged(this);
        }
        
    }

}
