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

package net.sourceforge.aprog.tools;

import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author codistmonk (creation 2010-06-11)
 */
public final class ToolsTest {

    @Test
    public final void testArray() {
        final Object[] array = new Object[] { 42, 33 };

        assertSame(array, Tools.array(array));
        assertArrayEquals(array, Tools.array(42, 33));
    }

    @Test
    public final void testSet() {
        final Set<?> set = Tools.set(42, 33, 42);

        assertArrayEquals(Tools.array(42, 33), set.toArray());
    }

    @Test
    public final void testGetCallerClass() {
        assertEquals(this.getClass(), ToolsTest.getCallerClass());
    }

	@Test
	public final void testThrowUnchecked() {
        {
            final Throwable originalThrowable = new RuntimeException();

            try {
                Tools.throwUnchecked(originalThrowable);
            } catch(final RuntimeException caughtThrowable) {
                assertSame(originalThrowable, caughtThrowable);
            }
        }

        {
            final Throwable originalThrowable = new Exception();

            try {
                Tools.throwUnchecked(originalThrowable);
            } catch(final RuntimeException caughtThrowable) {
                assertNotNull(caughtThrowable.getCause());
                assertSame(originalThrowable, caughtThrowable.getCause());
            }
        }

        {
    		final Throwable originalThrowable = new Error();

            try {
                Tools.throwUnchecked(originalThrowable);
            } catch(final Error caughtThrowable) {
                assertSame(originalThrowable, caughtThrowable);
            }
        }

        {
            final Throwable originalThrowable = new Throwable();

            try {
                Tools.throwUnchecked(originalThrowable);
            } catch(final Throwable caughtThrowable) {
                assertSame(originalThrowable, caughtThrowable.getCause());
            }
        }
	}

	@Test
	public final void testCast() {
		final Object object = "42";
		final String that = Tools.cast(String.class, object);

		assertSame(object, that);

		final Integer badCast = Tools.cast(Integer.class, object);

		assertNull(badCast);
	}

	@Test
	public final void testCastToCurrentClass() {
        assertNull(Tools.castToCurrentClass(42));
        assertSame(this, Tools.castToCurrentClass(this));
        assertNotNull(Tools.castToCurrentClass(new ToolsTest()));
    }

	@Test
	public final void testEquals() {
		final Object object = "42";

		assertTrue(Tools.equals(null, null));
		assertFalse(Tools.equals(object, null));
		assertTrue(Tools.equals(object, object));
		assertTrue(Tools.equals(new Integer(6 * 7).toString(), object));
		assertFalse(Tools.equals(object, 42));
	}

	@Test
	public final void testHashCode() {
		final Object object = "42";

		assertEquals(0, Tools.hashCode(null));
		assertEquals(object.hashCode(), Tools.hashCode(object));
	}

    /**
     *
     * @return
     * <br>Maybe null
     */
    private static final Class<?> getCallerClass() {
        return Tools.getCallerClass();
    }

}