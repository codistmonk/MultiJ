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

import static javax.swing.KeyStroke.getKeyStroke;
import static net.sourceforge.aprog.af.AFConstants.Variables.*;
import static net.sourceforge.aprog.af.MacOSXTools.*;
import static net.sourceforge.aprog.swing.SwingTools.*;
import static net.sourceforge.aprog.i18n.Messages.*;
import static net.sourceforge.aprog.tools.Tools.*;

import java.awt.Component;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.AtomicVariable;
import net.sourceforge.aprog.swing.SwingTools;
import net.sourceforge.aprog.tools.AbstractInvocationHandler;
import net.sourceforge.aprog.tools.IllegalInstantiationException;
import net.sourceforge.aprog.tools.Tools;

/**
 *
 * @author codistmonk (creation 2010-09-22)
 */
public final class AFTools {
	
	/**
	 * @throws IllegalInstantiationException To prevent instantiation
	 */
	private AFTools() {
		throw new IllegalInstantiationException();
	}
	
	public static final String META = MacOSXTools.MAC_OS_X ? "meta" : "control";
	
	/**
	 * Prepares the application to use the OS default look and feel.
	 * <br>On Mac OS X, sets up the use of the screen menu bar and the application name.
	 *
	 * @param applicationName
	 * <br>Not null
	 */
	public static final void setupSystemLookAndFeel(final String applicationName) {
		if (MAC_OS_X) {
			useScreenMenuBar();
			setApplicationName(applicationName);
		}

		useSystemLookAndFeel();
	}
	
	/**
	 * Prepares the application to use the OS default look and feel.
	 * <br>On Mac OS X, sets up the use of the screen menu bar and the application name.
	 *
	 * @param applicationName
	 * <br>Maybe null
	 * @param applicationIconPath 
	 * <br>Maybe null
	 */
	public static final void setupSystemLookAndFeel(final String applicationName, final String applicationIconPath) {
		MacOSXTools.setupUI(applicationName, applicationIconPath);
		useSystemLookAndFeel();
	}
	
	public static final void fireUpdate(final Context context, final String variableName) {
		final Object value = context.get(variableName);
		@SuppressWarnings("unchecked")
		final AtomicVariable<Object> variable = cast(AtomicVariable.class, context.getVariable(variableName));
		
		if (variable != null) {
			variable.new ValueChangedEvent(value, value).fire();
		}
	}
	
	/**
	 * Creates a new context with the folowing properties:<ul>
	 *  <li>{@link AFConstants.Variables#APPLICATION_NAME} -&gt; {@link AFConstants#APPLICATION_NAME}
	 *  <li>{@link AFConstants.Variables#APPLICATION_VERSION} -&gt; {@link AFConstants#APPLICATION_VERSION}
	 *  <li>{@link AFConstants.Variables#APPLICATION_COPYRIGHT} -&gt; {@link AFConstants#APPLICATION_COPYRIGHT}
	 *  <li>{@link AFConstants.Variables#APPLICATION_ICON_PATH} -&gt; {@link AFConstants#APPLICATION_ICON_PATH}
	 * </ul>.
	 *
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final Context newContext() {
		final Context result = new Context();

		result.set(APPLICATION_NAME, AFConstants.APPLICATION_NAME);
		result.set(APPLICATION_VERSION, AFConstants.APPLICATION_VERSION);
		result.set(APPLICATION_COPYRIGHT, AFConstants.APPLICATION_COPYRIGHT);
		result.set(APPLICATION_ICON_PATH, AFConstants.APPLICATION_ICON_PATH);

		return result;
	}

	/**
	 * If {@code context} value for the key {@link AFConstants.Variables#ACTIONS_SHOW_ABOUT_DIALOG} is null,
	 * then associates a new {@link ShowAboutDialogAction} to the key.
	 * <br>On Mac OS X, if the use of the screen menu bar is enabled then registers a listener for the appropriate menu item.
	 * 
	 * @param context
	 * <br>Not null
	 * <br>Shared
	 * @return An autotranslated menu item or null if the screen menu bar is used on Mac OS X
	 * <br>Maybe null
	 * <br>New
	 */
	public static final JMenuItem newAboutItem(final Context context) {
		checkAWT();

		if (context.get(ACTIONS_SHOW_ABOUT_DIALOG) == null) {
			new ShowAboutDialogAction(context);
		}

		if (MAC_OS_X && getUseScreenMenuBar()) {
			enableAboutMenu();

			if (registerMacOSXApplicationListener("handleAbout",
					AFTools.class, "perform", context, ACTIONS_SHOW_ABOUT_DIALOG)) {
				return null;
			}
		}

		return item("About", context, ACTIONS_SHOW_ABOUT_DIALOG);
	}

	/**
	 *
	 * @param context
	 * <br>Not null
	 * <br>Shared
	 * @return
	 * <br>Maybe null
	 * <br>New
	 */
	public static final JMenuItem newPreferencesItem(final Context context) {
		checkAWT();

		if (context.get(ACTIONS_SHOW_PREFERENCES_DIALOG) == null) {
			new ShowPreferencesDialogAction(context);
		}

		if (MAC_OS_X && getUseScreenMenuBar()) {
			enablePreferencesMenu();

			if (registerMacOSXApplicationListener("handlePreferences",
					AFTools.class, "perform", context, ACTIONS_SHOW_PREFERENCES_DIALOG)) {
				return null;
			}
		}

		return item("Preferences...", getKeyStroke(META + " R"),
				context, ACTIONS_SHOW_PREFERENCES_DIALOG);
	}

	/**
	 *
	 * @param context
	 * <br>Not null
	 * <br>Shared
	 * @return
	 * <br>Maybe null
	 * <br>New
	 */
	public static final JMenuItem newQuitItem(final Context context) {
		checkAWT();

		if (context.get(ACTIONS_QUIT) == null) {
			new QuitAction(context);
		}

		if (MAC_OS_X && getUseScreenMenuBar()) {
			if (registerMacOSXApplicationListener("handleQuit",
					AFTools.class, "perform", context, ACTIONS_QUIT)) {
				return null;
			}
		}

		return item("Quit", getKeyStroke(META + " Q"),
				context, ACTIONS_QUIT);
	}

	/**
	 * 
	 * @param context
	 * <br>Not null
	 * @param actionKey
	 * <br>Not null
	 */
	public static final void perform(final Context context, final String actionKey) {
		((AbstractAFAction) context.get(actionKey)).perform(null);
	}
	
	/**
	 *
	 * @param tabbedPane
	 * <br>Not null
	 * @param component
	 * <br>Not null
	 * @return The index of {@code component} in {@code tabbedPane}, or {@code -1} if it cannot be found
	 * <br>Range: {@code [-1 .. tabbedPane.getTabCount() - 1]}
	 */
	public static final int indexOf(final JTabbedPane tabbedPane, final Component component) {
		checkAWT();

		for (int i = 0; i < tabbedPane.getTabCount(); ++i) {
			if (component == tabbedPane.getComponentAt(i)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Creates a localized menu from the elements in {@code items}.
	 * <br>Uses {@link net.sourceforge.aprog.i18n.Messages#translate(java.lang.Object, java.lang.Object[])}
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
		checkAWT();

		return translate(SwingTools.menu(translationKey, items));
	}

	/**
	 *
	 * @param translationKey
	 * <br>Not null
	 * <br>Shared
	 * @param context
	 * <br>Not null
	 * <br>Shared
	 * @param actionKey
	 * <br>Not null
	 * <br>Shared
	 * @return An autotranslated menu item
	 * <br>Not null
	 * <br>New
	 */
	public static final JMenuItem item(final String translationKey, final Context context,
			final String actionKey) {
		checkAWT();

		return translate(new JMenuItem(new AFSwingAction(context, actionKey)
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
	 * @param context
	 * <br>Not null
	 * <br>Shared
	 * @param actionKey
	 * <br>Not null
	 * <br>Shared
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final JMenuItem item(final String translationKey, final KeyStroke shortcut,
			final Context context, final String actionKey) {
		checkAWT();

		return translate(new JMenuItem(new AFSwingAction(context, actionKey)
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
