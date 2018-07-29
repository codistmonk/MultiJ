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

package multij.af;

import static multij.tools.Tools.ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import multij.af.AFConstants;
import multij.af.AFTools;
import multij.af.AbstractAFAction;
import multij.af.MacOSXTools;
import multij.af.QuitAction;
import multij.af.ShowAboutDialogAction;
import multij.af.ShowPreferencesDialogAction;
import multij.context.Context;
import multij.events.Variable.Listener;
import multij.events.Variable.ValueChangedEvent;
import multij.swing.SwingTools;
import multij.tools.IllegalInstantiationException;

import org.junit.Test;

/**
 * @author codistmonk (creation 2010-10-16)
 */
public final class AFToolsTest {
	
	@Test
	public final void testSetupSystemLookAndFeel() {
		AFTools.setupSystemLookAndFeel(this.getClass().getSimpleName());

		if (MacOSXTools.MAC_OS_X) {
			assertTrue(MacOSXTools.getUseScreenMenuBar());
			assertEquals(this.getClass().getSimpleName(), MacOSXTools.getApplicationName());
		}
	}
	
	@Test
	public final void testFireUpdate() {
		final Context context = new Context();
		final boolean[] eventReceived = { false };
		
		context.set("a", "b");
		
		context.getVariable("a").addListener(new Listener<>() {
			
			@Override
			public final void valueChanged(final ValueChangedEvent<Object, ?> event) {
				eventReceived[0] = true;
			}
			
		});
		
		AFTools.fireUpdate(context, "a");
		
		assertTrue(eventReceived[0]);
	}
	
	@Test
	public final void testNewContext() {
		final Context context = AFTools.newContext();

		assertNotNull(context);
		assertEquals(AFConstants.APPLICATION_NAME, context.get(AFConstants.Variables.APPLICATION_NAME));
		assertEquals(AFConstants.APPLICATION_VERSION, context.get(AFConstants.Variables.APPLICATION_VERSION));
		assertEquals(AFConstants.APPLICATION_COPYRIGHT, context.get(AFConstants.Variables.APPLICATION_COPYRIGHT));
		assertEquals(AFConstants.APPLICATION_ICON_PATH, context.get(AFConstants.Variables.APPLICATION_ICON_PATH));
	}

	@Test
	public final void testNewAboutItem() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final Context context = AFTools.newContext();

			if (MacOSXTools.MAC_OS_X && MacOSXTools.getUseScreenMenuBar()) {
				assertEquals(null, AFTools.newAboutItem(context));
			} else {
				assertNotNull(AFTools.newAboutItem(context));
			}

			final ShowAboutDialogAction action = context.get(AFConstants.Variables.ACTIONS_SHOW_ABOUT_DIALOG);

			assertNotNull(action);
		}
	}

	@Test
	public final void testNewPreferencesItem() {
		this.testSetupSystemLookAndFeel();
		
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final Context context = AFTools.newContext();
			
			if (MacOSXTools.MAC_OS_X && MacOSXTools.getUseScreenMenuBar()) {
				assertEquals(null, AFTools.newPreferencesItem(context));
			} else {
				assertNotNull(AFTools.newPreferencesItem(context));
			}
			
			final ShowPreferencesDialogAction action = context.get(AFConstants.Variables.ACTIONS_SHOW_PREFERENCES_DIALOG);
			
			assertNotNull(action);
		}
	}

	@Test
	public final void testNewQuitItem() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final Context context = AFTools.newContext();

			if (MacOSXTools.MAC_OS_X && MacOSXTools.getUseScreenMenuBar()) {
				assertEquals(null, AFTools.newQuitItem(context));
			} else {
				assertNotNull(AFTools.newQuitItem(context));
			}

			final QuitAction action = context.get(AFConstants.Variables.ACTIONS_QUIT);

			assertNotNull(action);
		}
	}

	@Test
	public final void testPerform() {
		final Context context = new Context();

		new AbstractAFAction(context, Variables.TEST) {
			
			@Override
			public final void perform(final Object object) {
				ignore(object);
				
				context.set(Variables.SUCCESS, true);
			}
			
			/**
			 * {@value}.
			 */
			private static final long serialVersionUID = -5350135806380729762L;

		};

		AFTools.perform(context, Variables.TEST);

		assertEquals(true, context.get(Variables.SUCCESS));
	}

	@Test
	public final void testIndexOf() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			final JTabbedPane tabbedPane = new JTabbedPane();
			final JComponent component = new JLabel();

			tabbedPane.addTab("test", component);

			assertEquals(0, AFTools.indexOf(tabbedPane, component));
			assertEquals(-1, AFTools.indexOf(tabbedPane, new JLabel()));
		}
	}

	@Test
	public final void testMenu() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			assertNotNull(AFTools.menu("test"));
		}
	}

	@Test
	public final void testItem() {
		if (SwingTools.canInvokeThisMethodInAWT(this)) {
			assertNotNull(AFTools.item("test", new Context(), Variables.TEST));
			assertNotNull(AFTools.item("test", KeyStroke.getKeyStroke(AFTools.META + " + A"), new Context(), Variables.TEST));
		}
	}

	@Test
	public final void testNewListener() {
		final Context context = new Context();
		final Context.Listener listener = AFTools.newListener(Context.Listener.class, "variableAdded",
				this.getClass(), "updateSuccess", context);

		context.addListener(listener);
		context.set(Variables.SUCCESS, false);

		assertEquals(true, context.get(Variables.SUCCESS));
	}

	/**
	 *
	 * @param context
	 * <br>Not null
	 * <br>Input-output
	 */
	public static final void updateSuccess(final Context context) {
		context.set(Variables.SUCCESS, true);
	}
	
	/**
	 * @author codistmonk (creation 2010-10-16)
	 */
	public static final class Variables {
		
		/**
		 * @throws IllegalInstantiationException To prevent instantiation
		 */
		private Variables() {
			throw new IllegalInstantiationException();
		}

		/**
		 * {@value}.
		 */
		public static final String SUCCESS = "success";

		/**
		 * {@value}.
		 */
		public static final String TEST = "test";

	}

}