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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @param <L> the event listener type
 * @author codistmonk (creation 2010-06-18)
 */
public interface Observable<L extends Observable.Listener> {

    /**
     *
     * @param listener
     * <br>Not null
     * <br>Shared
     */
    public abstract void addListener(L listener);

    /**
     *
     * @param listener
     * <br>Maybe null
     * <br>Shared
     */
    public abstract void removeListener(L listener);

    /**
     *
     * @return
     * <br>Not null
     * <br>New
     */
    public abstract Iterable<L> getListeners();

    /**
     *
     * @author codistmonk (creation 2010-06-14)
     */
    public static interface Listener {
        // Nothing
    }

    /**
     *
     * @param <S> the event source type
     * @author codistmonk (creation 2010-06-18)
     */
    public static interface Event<S extends Observable<?>> {

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public abstract S getSource();

        /**
         * Time in milliseconds.
         *
         * @return
         * <br>Range: {@code [0 .. Long.MAX_VALUE]}
         */
        public abstract long getTime();

        /**
         *
         * @return
         */
        public abstract boolean isInterrupted();

        /**
         *
         * @param interrupted
         */
        public abstract void setInterrupted(boolean interrupted);

    }

    /**
     *
     * @param <S> the event source type
     * @param <L> the event listener type
     * @author codistmonk (creation 2010-06-15)
     */
    public static abstract class AbstractEvent<S extends Observable<L>, L extends Observable.Listener> implements Event<S> {

        private final S source;

        private final long time;

        private boolean interrupted;

        /**
         *
         * @param source
         * <br>Not null
         * <br>Shared
         * @param time in milliseconds
         * <br>Range: {@code [0 .. Long.MAX_VALUE]}
         */
        protected AbstractEvent(final S source, final long time) {
            this.time = time;
            this.source = source;
        }

        /**
         *
         * @param source
         * <br>Not null
         * <br>Shared
         */
        protected AbstractEvent(final S source) {
            this(source, System.currentTimeMillis());
        }

        @Override
        public final S getSource() {
            return this.source;
        }

        @Override
        public final long getTime() {
            return this.time;
        }

        @Override
        public final boolean isInterrupted() {
            return this.interrupted;
        }

        @Override
        public final void setInterrupted(final boolean interrupted) {
            this.interrupted = interrupted;
        }

        public final void fire() {
            for (final L listener : this.getSource().getListeners()) {
                try {
                    this.notifyListener(listener);
                } catch (final Exception exception) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, exception);
                }

                if (this.isInterrupted()) {
                    break;
                }
            }
        }

        /**
         *
         * @param listener
         * <br>Not null
         * <br>Input-output
         */
        protected abstract void notifyListener(L listener);

    }

}
