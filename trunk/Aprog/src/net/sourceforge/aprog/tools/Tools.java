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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

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
     *
     * @param <T>
     * @param array
     * <br>Not null
     * @param moreElements
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final <T> T[] append(final T[] array, final T... moreElements) {
        @SuppressWarnings("unchecked")
        final T[] result = (T[]) Array.newInstance(
                array.getClass().getComponentType(), array.length + moreElements.length);

        System.arraycopy(array, 0, result, 0, array.length);
        System.arraycopy(moreElements, 0, result, array.length, moreElements.length);

        return result;
    }

    /**
     * Searches for and invokes a method named {@code methodName} that can accept {@code arguments}.
     *
     * @param <T> The expected return type
     * @param objectOrClass
     * <br>Not null
     * @param methodName
     * <br>Not null
     * @param arguments
     * <br>Not null
     * @return
     * <br>Maybe null
     * @throws RuntimeException if an appropriate method isn't found or if it throws an exception
     */
    @SuppressWarnings("unchecked")
    public static final <T> T invoke(final Object objectOrClass,
            final String methodName, final Object... arguments) {
        final Object object = objectOrClass instanceof Class<?> ? null : objectOrClass;
        final Class<?> objectClass = (Class<?>) (objectOrClass instanceof Class<?> ? objectOrClass : objectOrClass.getClass());

        for (final Method method : Tools.append(objectClass.getMethods(), objectClass.getDeclaredMethods())) {
            if (method.getName().equals(methodName)) {
                try {
                    return (T) method.invoke(object, arguments);
                } catch (final InvocationTargetException exception) {
                    throwUnchecked(exception.getCause());
                } catch (final Exception exception) {
                    // Ignore
                }
            }
        }

        throw new RuntimeException(
                "Method " + methodName + " accepting arguments " + Arrays.toString(arguments) +
                " was not found for object " + object + " of class " + objectClass);
    }

    /**
     * Tries to find a setter starting with "set" for the specified property of the object.
     * <br>Eg: {@code getSetter(object, "text", String.class)} tries to find a method {@code setText(String)}
     *
     * @param object
     * <br>Should not be null
     * @param propertyName
     * <br>Should not be null
     * @param propertyClass
     * <br>Should not be null
     * @return
     * <br>A non-null value
     * @throws RuntimeException if an appropriate setter cannot be retrieved
     */
    public static final Method getSetter(final Object object, final String propertyName, final Class<?> propertyClass) {
        final String setterName = "set" + toUpperCamelCase(propertyName);

        try {
            // Try to retrieve a public setter
            return object.getClass().getMethod(setterName, propertyClass);
        } catch (final Exception exception) {
            // Do nothing
        }

        try {
            // Try to retrieve a setter declared in object's class, regardless of its visibility
            return object.getClass().getDeclaredMethod(setterName, propertyClass);
        } catch (final Exception exception) {
            // Do nothing
        }

        throw new RuntimeException("Unable to retrieve a getter for property " + propertyName);
    }

    /**
     * Tries to find a getter starting with "get", "is", or "has" (in that order) for the specified property of the object.
     * <br>Eg: {@code getGetter(object, "empty")} tries to find a method {@code getEmpty()} or {@code isEmpty()} or {@code hasEmpty()}
     *
     * @param object
     * <br>Should not be null
     * @param propertyName the camelCase name of the property
     * <br>Should not be null
     * @return
     * <br>A non-null value
     * @throws RuntimeException if an appropriate getter cannot be retrieved
     */
    public static final Method getGetter(final Object object, final String propertyName) {
        final String upperCamelCase = toUpperCamelCase(propertyName);

        for (final String prefix : array("get", "is", "has")) {
            final String getterName = prefix + upperCamelCase;

            try {
                // Try to retrieve a public getter
                return object.getClass().getMethod(getterName);
            } catch (final Exception exception) {
                // Do nothing
            }

            try {
                // Try to retrieve a getter declared in object's class, regardless of its visibility
                return object.getClass().getDeclaredMethod(getterName);
            } catch (final Exception exception) {
                // Do nothing
            }
        }

        throw new RuntimeException("Unable to retrieve a getter for property " + propertyName);
    }

    /**
     * Converts "someName" into "SomeName".
     *
     * @param lowerCamelCase
     * <br>Should not be null
     * @return
     * <br>A new value
     * <br>A non-null value
     */
    public static final String toUpperCamelCase(final String lowerCamelCase) {
        return Character.toUpperCase(lowerCamelCase.charAt(0)) + lowerCamelCase.substring(1);
    }

    /**
     * Converts {@code null} into "", otherwise returns the parameter untouched.
     *
     * @param string
     * <br>Can be null
     * <br>Shared parameter
     * @return {@code string} or ""
     * <br>A non-null value
     * <br>A shared value
     */
    public static final String emptyIfNull(final String string) {
        return string == null ? "" : string;
    }

    /**
     * Returns "package/name/" if the package of {@code cls} is of the form "package.name".
     *
     * @param cls
     * <br>Not null
     * @return
     * <br>Not null
     */
    public static final String getPackagePath(final Class<?> cls) {
        return cls.getPackage().getName().replaceAll("\\.", "/") + "/";
    }

    /**
     * Returns "package/name/" if the package of the caller class is of the form "package.name".
     *
     * @return
     * <br>Not null
     */
    public static final String getCallerPackagePath() {
        return getPackagePath(getCallerClass());
    }

    /**
     *
     * @param cls
     * <br>Not null
     * @return the top level class enclosing {@code cls}, or {@code cls} itself if it is a top level class
     * <br>Not null
     */
    public static final Class<?> getTopLevelEnclosingClass(final Class<?> cls) {
        return cls.getEnclosingClass() == null ? cls : getTopLevelEnclosingClass(cls.getEnclosingClass());
    }

    /**
     * If a method {@code A.a()} calls a method {@code B.b()},
     * then the result of calling this method in {@code b()} will be {@code A.class}.
     * <br>Warning: this method can only be used directly.
     * <br>If you want to refactor your code, you can re-implement the functionality
     * using {@code Thread.currentThread().getStackTrace()}.
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
     * If a method {@code a()} calls a method {@code b()}, then the result of calling this method in b() will be "a".
     * <br>Warning: this method can only be used directly.
     * <br>If you want to refactor your code, you can re-implement the functionality using {@code Thread.currentThread().getStackTrace()}.
     *
     * @return {@code null} if the caller method cannot be retrieved
     * <br>A possibly null value
     */
    public static final String getCallerMethodName() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        return stackTrace.length > 3 ? stackTrace[3].getMethodName() : null;
    }

    /**
     * Calls {@link Logger#getLogger(String)} using the fully qualified name of the calling method.
     * <br>Warning: this method can only be used directly.
     * <br>If you want to refactor your code, you can re-implement the functionality using {@code Thread.currentThread().getStackTrace()}.
     *
     * @return
     * <br>A non-null value
     * @throws NullPointerException if the caller class cannot be retrieved
     */
    public static final Logger getLoggerForThisMethod() {
        return Logger.getLogger(getCallerClass().getCanonicalName() + "." + getCallerMethodName());
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
     * @throws RuntimeException with {@code cause} as cause if it is a checked exception,
     * otherwise {@code cause} is re-thrown
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
     * Does the same thing as {@link Class#cast(Object)},
     * but returns {@code null} instead of throwing an exception if the cast cannot be performed.
     *
     * @param <T> the type into which {@code object} is tentatively being cast
     * @param cls
     * <br>Not null
     * @param object
     * <br>Maybe null
     * @return {@code null} if {@code object} is {@code null} or cannot be cast into {@code T},
     * otherwise {@code object}
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
     * @return {@code null} if {@code object} is {@code null} or cannot be cast into the caller type
     * (obtained using {@link #getCallerClass()}) , otherwise {@code object}
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
     * Concatenates the source location of the call and
     * the string representations of the parameters separated by spaces.
     * <br>This is method helps to perform console debugging using System.out or System.err.
     *
     * @param stackIndex 1 is the source of this method, 2 is the source of the call,
     * 3 is the source of the call's caller, and so forth
     * <br>Range: {@code [O .. Integer.MAX_VALUE]}
     * @param objects
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     * @throws IndexOutOfBoundsException if {@code stackIndex} is invalid
     */
    public static final String debug(final int stackIndex, final Object... objects) {
        final StringBuilder builder = new StringBuilder(
                Thread.currentThread().getStackTrace()[stackIndex].toString());

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
