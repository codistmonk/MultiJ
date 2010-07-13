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

package net.sourceforge.aprog.subtitlesadjuster;

import static net.sourceforge.aprog.i18n.Messages.*;
import static net.sourceforge.aprog.tools.Tools.*;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.Variable;
import net.sourceforge.aprog.events.Variable.Listener;
import net.sourceforge.aprog.i18n.Messages;
import net.sourceforge.aprog.swing.SwingTools;
import net.sourceforge.aprog.tools.AbstractInvocationHandler;
import net.sourceforge.aprog.tools.Tools;
import net.sourceforge.jmacadapter.MacAdapterTools;

/**
 * Utility class.
 * <br>If it turns out that some methods defined here are reused in other projects,
 * then they could be moved directly into Aprog.
 *
 * @author codistmonk (creation 2010-06-27)
 */
public final class SubtitlesAdjusterTools {

    /**
     * Private default constructor to prevent instantiation.
     */
    private SubtitlesAdjusterTools() {
        // Do nothing
    }

    public static final String META = MacAdapterTools.isMacOSX() ? "meta" : "control";

    /**
     *
     * @param listenerMethodName
     * <br>Not null
     * <br>Shared
     * <br>Range: { {@code "handleAbout"}, {@code "handlePreferences"}, {@code "handleQuit"} }
     * @param objectOrClass
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     * @return {@code true} if a listener was successfully created and registered
     */
    @SuppressWarnings("unchecked")
    public static final boolean registerMacOSXApplicationListener(final String listenerMethodName,
            final Object objectOrClass, final String methodName, final Object... arguments) {
        try {
            final Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
            final Class<?> listenerClass = Class.forName("com.apple.eawt.ApplicationListener");
            final Class<?> eventClass = Class.forName("com.apple.eawt.ApplicationEvent");
            final Object application = invoke(applicationClass, "getApplication");

            invoke(application, "addApplicationListener", Proxy.newProxyInstance(
                getCallerClass().getClassLoader(),
                array(listenerClass),
                new AbstractInvocationHandler() {

                    @Override
                    public final Object invoke(final Object proxy,
                            final Method method, final Object[] proxyMethodArguments) throws Throwable {
                        if (method.getName().equals(listenerMethodName) &&
                                proxyMethodArguments.length == 1 && null != cast(eventClass, proxyMethodArguments[0])) {
                            Tools.invoke(proxyMethodArguments[0], "setHandled", true);

                            return Tools.invoke(objectOrClass, methodName, arguments);
                        }

                        return this.defaultInvoke(proxy, method, proxyMethodArguments);
                    }

            }));

            return true;
        } catch (final Exception exception) {
            getLoggerForThisMethod().log(Level.SEVERE, null, exception);

            return false;
        }
    }

    /**
     * Creates a localized menu from the elements in {@code items}.
     * <br>Uses {@link Messages#translate(java.lang.Object, java.lang.Object[])}
     * and {@link SwingTools#menu(java.lang.String, javax.swing.JMenuItem[])}.
     *
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param items
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenu menu(final String translationKey, final JMenuItem... items) {
        return translate(SwingTools.menu(translationKey, items));
    }

    /**
     *
     * @param context
     * <br>Not null
     * @param variableName
     * <br>Not null
     * @param objectOrClass
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     */
    @SuppressWarnings("unchecked")
    public static final void invokeOnVariableChanged(final Context context, final String variableName,
            final Object objectOrClass, final String methodName, final Object... arguments) {
        final Variable<Object> variable = context.getVariable(variableName);

        variable.addListener(newListener(Listener.class, "valueChanged",
                Tools.class, "invoke", objectOrClass, methodName, arguments));

        Tools.invoke(objectOrClass, methodName, arguments);
    }

    /**
     *
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param objectOrClass
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem item(final String translationKey,
            final Object objectOrClass, final String methodName, final Object... arguments) {
        return translate(new JMenuItem(
                SwingTools.action(objectOrClass, methodName, arguments)
                .setName(translationKey)));
    }

    /**
     *
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param shortcut
     * <br>Not null
     * <br>Shared
     * @param objectOrClass
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem item(final String translationKey, final KeyStroke shortcut,
            final Object objectOrClass, final String methodName, final Object... arguments) {
        return translate(new JMenuItem(
                SwingTools.action(objectOrClass, methodName, arguments)
                .setName(translationKey)
                .setShortcut(shortcut)));
    }

    /**
     *
     * @param <L> The listener type
     * @param listenerClass
     * <br>Not null
     * @param listenerMethodName
     * <br>Not null
     * @param objectOrClass
     * <br>Not null
     * @param methodName
     * <br>Not null
     * @param arguments
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    @SuppressWarnings("unchecked")
    public static final <L> L newListener(final Class<L> listenerClass, final String listenerMethodName,
            final Object objectOrClass, final String methodName, final Object... arguments) {
        return (L) Proxy.newProxyInstance(getCallerClass().getClassLoader(), array(listenerClass),
                new ListenerInvocationHandler(listenerMethodName, objectOrClass, methodName, arguments));
    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public static final class ListenerInvocationHandler extends AbstractInvocationHandler {

        private final String listenerMethodName;

        private final Object objectOrClass;

        private final String methodName;

        private final Object[] arguments;

        /**
         *
         * @param listenerMethodName
         * <br>Not null
         * <br>Shared
         * @param objectOrClass
         * <br>Not null
         * <br>Shared
         * @param methodName
         * <br>Not null
         * <br>Shared
         * @param arguments
         * <br>Not null
         * <br>Shared
         */
        public ListenerInvocationHandler(final String listenerMethodName,
                final Object objectOrClass, final String methodName, final Object... arguments) {
            this.listenerMethodName = listenerMethodName;
            this.objectOrClass = objectOrClass;
            this.methodName = methodName;
            this.arguments = arguments;
        }

        @Override
        public final Object invoke(final Object proxy,
                final Method method, final Object[] arguments) throws Throwable {
            if (method.getName().equals(this.listenerMethodName)) {
                return Tools.invoke(this.objectOrClass, this.methodName, this.arguments);
            }

            return this.defaultInvoke(proxy, method, arguments);
        }

    }

}
