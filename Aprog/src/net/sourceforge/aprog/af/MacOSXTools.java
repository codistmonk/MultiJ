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

package net.sourceforge.aprog.af;

import static net.sourceforge.aprog.tools.Tools.array;
import static net.sourceforge.aprog.tools.Tools.cast;
import static net.sourceforge.aprog.tools.Tools.getCallerClass;
import static net.sourceforge.aprog.tools.Tools.getLoggerForThisMethod;
import static net.sourceforge.aprog.tools.Tools.getResourceAsStream;
import static net.sourceforge.aprog.tools.Tools.ignore;
import static net.sourceforge.aprog.tools.Tools.invoke;

import java.awt.Image;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import net.sourceforge.aprog.tools.AbstractInvocationHandler;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.tools.Tools;

/**
 *
 * @author codistmonk (creation 2010-09-16)
 */
public final class MacOSXTools {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private MacOSXTools() {
        throw new IllegalInstantiationException();
    }
    
	public static final boolean MAC_OS_X = System.getProperty("os.name").equalsIgnoreCase("mac os x");
	
	/**
	 * Sets up the GUI with:<ul>
	 *     <li>{@link #setApplicationName(String)};
	 *     <li>{@link #setApplicationDockIcon(String)};
	 *     <li>{@link #useScreenMenuBar()};
	 *     <li>{@link #enableAboutMenu()};
	 *     <li>{@link #enablePreferencesMenu()}.
	 * </ul>
	 * @param applicationName
	 * <br>Maybe null
	 * @param applicationIconPath
	 * <br>Maybe null
	 */
	public static final void setupUI(final String applicationName, final String applicationIconPath) {
	    if (applicationName != null) {
	        setApplicationName(applicationName);
	    }
	    
	    if (applicationIconPath != null) {
	        setApplicationDockIcon(applicationIconPath);
	    }
	    
        useScreenMenuBar();
        enableAboutMenu();
        enablePreferencesMenu();
	}

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
     * Sets the application dock icon that will be displayed in the Mac OS X Dock.
     *
     * <br>This must be invoked before loading any UI-related class (AWT, Swing, ...).
     *
     * @param iconResourcePath
     * <br>Not null
     */
    public static void setApplicationDockIcon(final String iconResourcePath) {
        try {
            setApplicationDockIcon(ImageIO.read(getResourceAsStream(iconResourcePath)));
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }
    
    /**
     * Sets the application dock icon that will be displayed in the Mac OS X Dock.
     *
     * <br>This must be invoked before loading any UI-related class (AWT, Swing, ...).
     *
     * @param icon
     * <br>Not null
     */
    public static void setApplicationDockIcon(final Image icon) {
        invoke(invoke(getApplicationClass(), "getApplication"), "setDockIconImage", icon);
    }

	/**
	 * Sets the application title that will be displayed in the Mac OS X Application Menu.
	 *
	 * <br>This must be invoked before loading any UI-related class (AWT, Swing, ...).
	 *
	 * @param applicationName
	 * <br>Not null
	 */
	public static final void setApplicationName(final String applicationName) {
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", applicationName);
	}

	/**
	 * @return
	 * <br>Maybe null
	 */
	public static final String getApplicationName() {
        return System.getProperty("com.apple.mrj.application.apple.menu.about.name");
	}

    /**
     * Calls {@code setUseScreenMenuBar(true)}.
     */
	public static final void useScreenMenuBar() {
		setUseScreenMenuBar(true);
	}

    /**
     * 
     * @param useScreenMenuBar
     * <br>Range: any boolean
     */
	public static final void setUseScreenMenuBar(final boolean useScreenMenuBar) {
		System.setProperty("apple.laf.useScreenMenuBar", "" + useScreenMenuBar);
	}

	/**
	 * @return
     * <br>Range: any boolean
	 */
	public static final boolean getUseScreenMenuBar() {
		return "true".equals(System.getProperty("apple.laf.useScreenMenuBar"));
	}

    public static final void enableAboutMenu() {
        invoke(invoke(getApplicationClass(), "getApplication"), "setEnabledAboutMenu", true);
    }

    public static final void enablePreferencesMenu() {
        invoke(invoke(getApplicationClass(), "getApplication"), "setEnabledPreferencesMenu", true);
    }

    /**
     * @return
     * <br>Maybe null
     */
    public static final Class<?> getApplicationClass() {
        try {
            return Class.forName("com.apple.eawt.Application");
        } catch (final ClassNotFoundException exception) {
            ignore(exception);

            return null;
        }
    }

}
