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

/**
 *
 * @param <T> the type of the variable value
 * @author codistmonk (creation 2010-06-20)
 */
public interface Variable<T> extends Observable<Variable.Listener<T>> {

    /**
     *
     * @return
     * <br>Not null
     */
    public abstract Class<T> getType();

    /**
     *
     * @return
     * <br>Not null
     */
    public abstract String getName();

    /**
     *
     * @return
     * <br>Maybe null
     * <br>Shared
     */
    public abstract T getValue();

    /**
     * Sets this variable value to {@code value} and notifies the listeners, unless {@code value == this.getValue()}.
     *
     * @param value
     * <br>Maybe null
     * <br>Shared
     */
    public abstract void setValue(T value);

    /**
     *
     * @param <T> the type of the variable value
     * @author codistmonk (creation 2010-06-20)
     */
    public static interface Listener<T> {

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void valueChanged(ValueChangedEvent<T, ?> event);

    }

    /**
     *
     * @param <T> the type of the variable value
     * @param <V> the observable variable type
     * @author codistmonk (creation 2010-06-20)
     */
    public static interface ValueChangedEvent<T, V extends Variable<T>> extends Observable.Event<V> {

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public abstract T getOldValue();

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public abstract T getNewValue();

    }

}
