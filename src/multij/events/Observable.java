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

package multij.events;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This interface defines the common behavior to observable objects,
 * namely managing a collection of listeners.
 * <br>A classic event interface is provided ({@link Event}),
 * as well as an abstract implementation ({@link AbstractEvent})
 * that takes care of dispatching itself using a visitor pattern.
 * <br>The use of the {@link Event} interface is recommended but not mandatory;
 * you can define your own event hierarchy and dispatching mechanism.
 *
 * @param <L> the event listener type
 * @author codistmonk (creation 2010-06-18)
 */
public interface Observable<L> extends Serializable {

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
     * This interface defines a classic event interface that you can use to notify listeners.
     *
     * @param <S> the event source type
     * @author codistmonk (creation 2010-06-18)
     */
    public static interface Event<S extends Observable<?>> extends Serializable {

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
         * @return {@code true} if and only if the event should not be dispatched to the remaining listeners
         */
        public abstract boolean isInterrupted();

        /**
         *
         * @param interrupted Boolean indicating whether or not the event should be dispatched to the remaining listeners
         */
        public abstract void setInterrupted(boolean interrupted);

    }

    /**
     *
     * @param <S> the event source type
     * @param <L> the event listener type
     * @author codistmonk (creation 2010-06-15)
     */
    public static abstract class AbstractEvent<S extends Observable<L>, L> implements Event<S> {
    	
		private final S source;
		
        private final long time;
        
        private boolean interrupted;
        
        private boolean alreadyFired;
        
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
        
        /**
         * Dispatches this event to its source listeners.
         * <br>If a listener throws an exception (subclass of {@link Exception}, checked or unchecked),
         * the exception is logged and the dispatching continues to the next listener.
         * <br>If you want to stop the dispatching before all listeners have been notified,
         * use {@link #setInterrupted(boolean)}.
         *
         * @throws IllegalStateException if this event has already been fired
         */
        public final void fire() {
            if (this.alreadyFired) {
                throw new IllegalStateException("Already fired");
            }

            this.alreadyFired = true;

            this.notifyListeners();
        }
        
        /**
         *
         * @param listener
         * <br>Not null
         * <br>Input-output
         */
        protected abstract void notifyListener(L listener);
        
        private final void notifyListeners() {
            for (final L listener : this.getSource().getListeners()) {
                this.tryToNotifyListener(listener);

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
        private final void tryToNotifyListener(final L listener) {
            try {
                this.notifyListener(listener);
            } catch (final Exception exception) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, exception);
            }
        }
    	
        /**
		 * {@value}.
		 */
		private static final long serialVersionUID = 8150049535482287327L;
		
    }

}
