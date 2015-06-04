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

package multij.tools;

import static multij.tools.Tools.*;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * Instances of this class contain weak references on their elements.
 * <br>Hard references are created only during iteration by {@link Iterator#hasNext()}
 * (to ensure that the returned element is never null),
 * and then removed by {@link Iterator#next()}.
 * <br>When elements need to be compared, the identity operator {@code ==} is used.
 * <br>Iteration goes through the elements in the same order they were inserted.
 *
 * @param <T> The element type
 * @author codistmonk (creation 2010-10-24)
 */
public final class WeakBag<T> implements Iterable<T> {

    private final List<WeakReference<T>> references;

    public WeakBag() {
        this.references = new LinkedList<WeakReference<T>>();
    }

    /**
     *
     * @param element
     * <br>Not null
     * <br>Shared
     */
    public final void append(final T element) {
        this.getReferences().add(new WeakReference<T>(element));
    }

    /**
     *
     * @param element
     * <br>Maybe null
     */
    public final void remove(final T element) {
        for (final Iterator<T> i = this.iterator(); i.hasNext();) {
            if (i.next() == element) {
                i.remove();
            }
        }
    }

    /**
     *
     * @return
     * <br>Range: {@code [0 .. Integer.MAX_VALUE]}
     */
    public final int getElementCount() {
        int result = 0;

        for (final T element : this) {
            if (element != null) {
                ++result;
            }
        }

        return result;
    }

    /**
     *
     * @return
     * <br>Range: any boolean
     */
    public final boolean isEmpty() {
        return 0 == this.getElementCount();
    }

    @Override
    public final Iterator<T> iterator() {
        return this.new WeakIterator();
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    final List<WeakReference<T>> getReferences() {
        return this.references;
    }

    /**
     * @author codistmonk (creation 2010-10-24)
     */
    private final class WeakIterator implements Iterator<T> {

        private final Iterator<WeakReference<T>> iterator;

        private T next;

        public WeakIterator() {
            this.iterator = WeakBag.this.getReferences().iterator();
        }

        @Override
        public final boolean hasNext() {
            while (this.next == null && this.iterator.hasNext()) {
                this.next = this.iterator.next().get();

                if (this.next == null) {
                    this.iterator.remove();
                }
            }

            return this.next != null;
        }

        @Override
        public final T next() {
            final T result = this.next;
            this.next = null;

            return result;
        }

        @Override
        public final void remove() {
            this.iterator.remove();
        }

    }

    /**
     * {@value} milliseconds.
     */
    public static final long GARBAGE_COLLECTOR_TIME = 100L;

    /**
     * Calls {@link System#gc()} and then waits for {@link #GARBAGE_COLLECTOR_TIME} milliseconds.
     * <br>This method should only be used for testing.
     * <br>Due to the unpredictable nature of the garbage collector, it may or may not have
     * finished its work at the end of the wait.
     */
    public static final void runGarbageCollector() {
        System.gc();

        try {
            // Give some time to the garbage collector to do its work
            Thread.sleep(GARBAGE_COLLECTOR_TIME);
        } catch (final InterruptedException exception) {
            getLoggerForThisMethod().log(Level.WARNING, null, exception);
        }
    }

}
