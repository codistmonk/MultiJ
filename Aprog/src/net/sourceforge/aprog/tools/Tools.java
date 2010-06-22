/*
 *  The MIT License
 * 
 *  Copyright 2010 The Codist Monk.
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

import java.util.LinkedHashSet;

/**
 *
 * @author codistmonk (creation 2010-06-11)
 */
public final class Tools {

	/**
	 * Private default constructor to prevent instantiation.
	 */
	private Tools() {
		// Do nothing
	}

    /**
     *
     * @param <T>
     * @param array
     * <br>Maybe null
     * @return
     * <br>Maybe null
     * <br>Maybe New
     */
    public static final <T> T[] array(final T... array) {
        return array;
    }

    /**
     *
     * @param <T> the common type of the elements
     * @param elements
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final <T> LinkedHashSet<T> set(T... elements) {
        final LinkedHashSet<T> result = new LinkedHashSet<T>();

        for (final T element : elements) {
            result.add(element);
        }

        return result;
    }

    /**
     * If a method {@code A.a()} calls a method {@code B.b()}, then the result of calling this method in {@code b()} will be {@code A.class}.
     * <br>Warning: this method can only be used directly.
     * <br>If you want to refactor your code, you can re-implement the functionality using {@code Thread.currentThread().getStackTrace()}.
     *
     * @return {@code null} if the caller class cannot be retrieved
	 * <br>Maybe null
     */
    public static final Class<?> getCallerClass() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace.length > 3) {
            try {
                return Class.forName(stackTrace[3].getClassName());
            } catch (final ClassNotFoundException exception) {
                // Do nothing
            }
        }

        return null;
    }

    /**
     * Use this method when you want to propagate a checked exception wrapped in a runtime exception
     * instead of using the normal checked exception mechanism.
     *
     * @param <T> the type that the caller is supposed to return
     * @param cause
	 * <br>Not null
     * <br>Shared
     * @return
     * <br>Does not return
     * @throws RuntimeException with {@code cause} as cause if it is a checked exception, otherwise {@code cause} is re-thrown
     */
    public static final <T> T throwUnchecked(final Throwable cause) {
        if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        } else if (cause instanceof Error) {
            throw (Error) cause;
        }

        throw new RuntimeException(cause);
    }

	/**
	 * Does the same thing as {@link Class#cast(Object)}, but returns {@code null} instead of throwing an exception if the cast cannot be performed.
	 *
	 * @param <T> the type into which {@code object} is tentatively being cast
	 * @param cls
	 * <br>Not null
	 * @param object
	 * <br>Maybe null
	 * @return {@code null} if {@code object} is {@code null} or cannot be cast into {@code T}, otherwise {@code object}
	 * <br>Maybe null
	 */
	public static final <T> T cast(final Class<T> cls, final Object object) {
		if (object == null || !cls.isAssignableFrom(object.getClass())) {
			return null;
		}

		return cls.cast(object);
	}

    /**
     *
     * @param <T> the caller type
     * @param object
	 * <br>Maybe null
     * @return {@code null} if {@code object} is {@code null} or cannot be cast into the caller type (obtained using {@link #getCallerClass()}) , otherwise {@code object}
	 * <br>Maybe null
     */
    @SuppressWarnings("unchecked")
    public static final <T> T castToCurrentClass(final Object object) {
        return (T) cast(getCallerClass(), object);
    }

	/**
	 *
	 * @param object1
	 * <br>Maybe null
	 * @param object2
	 * <br>Maybe null
	 * @return {@code true} if both objects are the same (using {@code ==}) or equal (using {@code equals()})
	 */
	public static final boolean equals(final Object object1, final Object object2) {
		return object1 == object2 || (object1 != null && object1.equals(object2));
	}

	/**
	 *
	 * @param object
	 * <br>Maybe null
	 * @return {@code 0} if {@code object is null}, otherwise {@code object.hashcode()}
	 * <br>Range: any integer
	 */
	public static final int hashCode(final Object object) {
		return object == null ? 0 : object.hashCode();
	}

    /**
     * Concatenates the source location of the call and the string representations of the parameters separated by spaces.
     * <br>This is method helps to perform console debugging using System.out or System.err.
     *
     * @param stackIndex 1 is the source of this method, 2 is the source of the call, 3 is the source of the call's caller, and so forth
     * <br>Range: {@code [O .. Integer.MAX_VALUE]}
     * @param objects
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     * @throws IndexOutOfBoundsException if {@code stackIndex} is invalid
     */
    public static final String debug(final int stackIndex, final Object... objects) {
        final StringBuilder builder = new StringBuilder(Thread.currentThread().getStackTrace()[stackIndex].toString());

        for (final Object object : objects) {
            builder.append(" ").append(object);
        }

        return builder.toString();
    }

    /**
     * Prints on the standard output the concatenation of the source location of the call
     * and the string representations of the parameters separated by spaces.
     *
     * @param objects
     * <br>Not null
     */
    public static final void debugPrint(final Object... objects) {
        System.out.println(debug(3, objects));
    }

}
