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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * @author codistmonk (creation 2010-07-03)
 */
public abstract class AbstractInvocationHandler implements InvocationHandler, Serializable {

	@Override
    public final boolean equals(final Object object) {
        return this == object ||
                object != null &&
                Proxy.isProxyClass(object.getClass()) &&
                this == Proxy.getInvocationHandler(object);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Invokes {@code method} if it belongs to this class, otherwise returns {@code null}.
     *
     * @param proxy
     * <br>Unused
     * @param method
     * <br>Not null
     * @param arguments
     * <br>Not null
     * @return
     * <br>Maybe null
     * @throws Throwable If an error occurs
     */
    protected final Object defaultInvoke(final Object proxy,
            final Method method, final Object[] arguments) throws Throwable {
        Tools.ignore(proxy);
        
        if (method.getDeclaringClass().isAssignableFrom(this.getClass())) {
            return method.invoke(this, arguments);
        }

        return null;
    }

    /**
	 * {@value}.
	 */
	private static final long serialVersionUID = 2703009495595641202L;

}
