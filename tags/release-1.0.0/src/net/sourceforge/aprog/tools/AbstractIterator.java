/*
 *  The MIT License
 *
 *  Copyright 2011 Codist Monk.
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

package net.sourceforge.aprog.tools;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @param <E> The elements type
 * @author codistmonk (creation 2011-06-29)
 */
public abstract class AbstractIterator<E> implements Iterator<E> {

    private Boolean hasNext;

    private E nextElement;

    /**
     * @param hasNext
     * <br>Maybe null
     * <br>Range: any Boolean
     */
    protected AbstractIterator(final Boolean hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public final boolean hasNext() {
        if (this.hasNext == null) {
            this.hasNext = this.updateNextElement();
        }

        return this.hasNext;
    }

    @Override
    public final E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        this.hasNext = null;

        return this.nextElement;
    }

    /**
     * @param nextElement
     * <br>Maybe null
     * <br>Will be strong reference
     */
    protected final void setNextElement(final E nextElement) {
        this.nextElement = nextElement;
    }

    /**
     * @return
     * <br>Range: any boolean
     */
    protected abstract boolean updateNextElement();

}
