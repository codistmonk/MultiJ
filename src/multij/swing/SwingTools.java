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

package multij.swing;

import static java.lang.Thread.currentThread;
import static multij.i18n.Messages.translate;
import static multij.tools.Tools.cast;
import static multij.tools.Tools.getCallerClass;
import static multij.tools.Tools.getCallerMethodName;
import static multij.tools.Tools.getLoggerForThisMethod;
import static multij.tools.Tools.getResourceAsStream;
import static multij.tools.Tools.ignore;
import static multij.tools.Tools.invoke;
import static multij.tools.Tools.unchecked;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import multij.af.MacOSXTools;
import multij.i18n.Messages;
import multij.tools.IllegalInstantiationException;
import multij.tools.Tools;

/**
 * This class provides utility static methods to help build Swing GUIs.
 * <br>According to the JDK, accessing and modifying AWT components should only be done
 * in the AWT Event Dispatching Thread.
 * <br>The methods in this class enforce this rule by calling {@link #checkAWT()} to make sure
 * that they are used in the proper thread.
 *
 * @author codistmonk (creation 2010-06-26)
 */
public final class SwingTools {
	
	/**
	 * @throws IllegalInstantiationException To prevent instantiation
	 */
	private SwingTools() {
		throw new IllegalInstantiationException();
	}
    
    private static DataFlavor uriListAsStringFlavor = null;
	
	/**
	 * {@value}.
	 */
	public static final String META = MacOSXTools.MAC_OS_X ? "meta" : "control";
	
	/**
	 * {@value}.
	 */
	public static final String DEFAULT_IMAGES_BASE = "images/";
	
	/**
	 * {@value}.
	 */
	public static final String ICON_FORMAT = "png";
	
	/**
	 * {@value}.
	 */
	public static final String ROLLOVER_DISABLED_ICON_SUFFIX = "_disabled." + ICON_FORMAT;

	/**
	 * {@value}.
	 */
	public static final String ROLLOVER_NORMAL_ICON_SUFFIX = "." + ICON_FORMAT;

	/**
	 * {@value}.
	 */
	public static final String ROLLOVER_SELECTED_ICON_SUFFIX = "_selected." + ICON_FORMAT;

	/**
	 * {@value}.
	 */
	public static final String ROLLOVER_ROLLOVER_ICON_SUFFIX = "_rollover." + ICON_FORMAT;

	/**
	 * {@value}.
	 */
	public static final String ROLLOVER_ROLLOVER_SELECTED_ICON_SUFFIX = "_rollover_selected." + ICON_FORMAT;
	
	private static final Map<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();
	
	private static String imagesBase = DEFAULT_IMAGES_BASE;
	
	private static final AtomicBoolean checkAWT = new AtomicBoolean(true);
	
	static WeakReference<Thread> awtEventDispatchingThread = null;
	
	/**
	 * @return
	 * <br>Maybe null
	 */
	public static final synchronized Thread getAWTEventDispatchingThread() {
		if (awtEventDispatchingThread == null) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					
					@Override
					public final void run() {
						awtEventDispatchingThread = new WeakReference<Thread>(currentThread());
					}
					
				});
			} catch (final Exception exception) {
				throw unchecked(exception);
			}
		}
		
		return awtEventDispatchingThread.get();
	}
	
	/**
	 * @param checkAWT
	 * <br>Range: any boolean
	 */
	public static final void setCheckAWT(final boolean checkAWT) {
		SwingTools.checkAWT.set(checkAWT);
	}
	
	/**
	 * @param name
	 * <br>Maybe null
	 * @param root
	 * <br>Maybe null
	 * @return
	 * <br>Maybe null
	 */
	public static final <C extends Component> C find(final String name, final Container root) {
		return find(c -> Tools.equals(name, c.getName()), root);
	}
	
	/**
	 * @param cls
	 * <br>Must not be null
	 * @param root
	 * <br>Maybe null
	 * @return
	 * <br>Maybe null
	 */
	public static final <C extends Component> C find(final Class<C> cls, final Container root) {
		return find(cls::isInstance, root);
	}
	
	/**
	 * @param predicate
	 * <br>Maybe null
	 * @param root
	 * <br>Maybe null
	 * @return
	 * <br>Maybe null
	 */
	@SuppressWarnings("unchecked")
	public static final <C extends Component> C find(final Predicate<? super Component> predicate, final Container root) {
		if (predicate == null || root == null) {
			return null;
		}
		
		if (predicate.test(root)) {
			return (C) root;
		}
		
		for (final Component child : root.getComponents()) {
			final Container container = cast(Container.class, child);
			
			if (container != null) {
				final C candidate = find(predicate, container);
				
				if (candidate != null) {
					return candidate;
				}
			} else if (predicate.test(child)) {
				return (C) child;
			}
		}
		
		return null;
	}
	
	/**
	 *
	 * @return
	 * <br>Not null
	 * <br>Shared
	 */
	public static final String getImagesBase() {
		return imagesBase;
	}

	/**
	 *
	 * @param imagesBase
	 * <br>Not null
	 * <br>Shared
	 */
	public static final void setImagesBase(final String imagesBase) {
		SwingTools.imagesBase = imagesBase;
	}

	/**
	 * Returns the icon located at {@code getImageBase() + resourceName}.
	 * <br>Icons are cached using {@code resourceName} as a key.
	 *
	 * @param resourceName
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 * @throws RuntimeException if the resource cannot be loaded
	 */
	public static final ImageIcon getIcon(final String resourceName) {
		try {
			final ImageIcon cachedIcon = iconCache.get(resourceName);

			if (cachedIcon != null) {
				return cachedIcon;
			}

			final ImageIcon icon = new ImageIcon(ImageIO.read(getResourceAsStream(getImagesBase() + resourceName)));

			iconCache.put(resourceName, icon);

			return icon;
		} catch (final IOException exception) {
			throw unchecked(exception);
		}
	}

	/**
	 *
	 * @param resourceName
	 * <br>Not null
	 * @return
	 * <br>Maybe null
	 * <br>New
	 */
	public static final ImageIcon getIconOrNull(final String resourceName) {
		try {
			return getIcon(resourceName);
		} catch (final Exception exception) {
			ignore(exception);

			return null;
		}
	}
	
	/**
	 * @param image
	  * <br>Not null
	 * @param title
	 * <br>Not null
	 * @param modal
	 * <br>Range: any boolean
	 * @return
	 * <br>New
	 * <br>Maybe null
	*/
	public static final Window show(final BufferedImage image, final String title, final boolean modal) {
		final JLabel imageLabel = new JLabel(new ImageIcon(image));
		
		imageLabel.addMouseMotionListener(new MouseAdapter() {
			
			@Override
			public final void mouseMoved(final MouseEvent event) {
				final int x = event.getX();
				final int y = event.getY();
				
				if (0 <= x && x < image.getWidth() && 0 <= y && y < image.getHeight()) {
					final Color color = new Color(image.getRGB(x, y));
					
					invoke(imageLabel.getRootPane().getParent(), "setTitle",
							title + " (x: " + x + ") (y: " + y + ") (r: " + color.getRed() +
							") (g: " + color.getGreen() + ") (b: " + color.getBlue() + ") (a: " + color.getAlpha() + ")");
				}
			}
			
		});
		
		return show(new JScrollPane(imageLabel), title, modal);
	}
	
	/**
	 * @param component
	 * <br>Not null
	 * @param title
	 * <br>Not null
	 * @param modal
	 * <br>Range: any boolean
	 * @return
	 * <br>New
	 * <br>Maybe null
	 */
	public static final Window show(final Component component, final String title, final boolean modal) {
		final Window[] result = { null };
		
		try {
			final Runnable task = new Runnable() {
				
				@Override
				public final void run() {
					if (modal) {
						result[0] = new JDialog((JFrame) null, title, modal);
						((JDialog) result[0]).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					} else {
						result[0] = new JFrame(title);
						((JFrame) result[0]).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					}
					
					result[0].add(component);
					
					packAndCenter(result[0]).setVisible(true);
				}
				
			};
			
			if (SwingUtilities.isEventDispatchThread()) {
				task.run();
			} else {
				SwingUtilities.invokeAndWait(task);
			}
		} catch (final Exception exception) {
			throw unchecked(exception);
		}
		
		return result[0];
	}

	/**
	 *
	 * @param container
	 * <br>Not null
	 * <br>Input-output
	 * @param component
	 * <br>Not null
	 * <br>Input-output
	 * <br>Shared
	 * @param constraints
	 * <br>Not null
	 */
	public static final void add(final Container container, final Component component, final GridBagConstraints constraints) {
		checkAWT();

		if (!(container.getLayout() instanceof GridBagLayout)) {
			container.setLayout(new GridBagLayout());
		}

		final GridBagLayout layout = (GridBagLayout) container.getLayout();

		layout.setConstraints(component, constraints);

		container.add(component);
	}

	/**
	 *
	 * @param <T> the actual type of {@code button}
	 * @param button
	 * <br>Not null
	 * <br>Input-output
	 * @param imageName
	 * <br>Not null
	 * @param borderPainted if {@code false}, then the preferred size is set to the size of the image,
	 * and the background and border are not drawn; if {@code true}, then {@code button} is left in its current state
	 * @return {@code button}
	 * <br>Not null
	 */
	public static final <T extends AbstractButton> T rollover(final T button, final String imageName, final boolean borderPainted) {
		checkAWT();

		button.setRolloverEnabled(true);
		button.setDisabledIcon(getIconOrNull(imageName + ROLLOVER_DISABLED_ICON_SUFFIX));
		button.setIcon(getIconOrNull(imageName + ROLLOVER_NORMAL_ICON_SUFFIX));
		button.setSelectedIcon(getIconOrNull(imageName + ROLLOVER_SELECTED_ICON_SUFFIX));
		button.setRolloverIcon(getIconOrNull(imageName + ROLLOVER_ROLLOVER_ICON_SUFFIX));
		button.setRolloverSelectedIcon(getIconOrNull(imageName + ROLLOVER_ROLLOVER_SELECTED_ICON_SUFFIX));

		if (!borderPainted) {
			if (button.getIcon() != null) {
				button.setPreferredSize(new Dimension(button.getIcon().getIconWidth(), button.getIcon().getIconHeight()));
			}

			button.setBorderPainted(false);
		}

		return button;
	}

	/**
	 * Encloses {@code component} in a scroll pane.
	 *
	 * @param component
	 * <br>Not null
	 * <br>Input-output
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final JScrollPane scrollable(final Component component) {
		checkAWT();

		return new JScrollPane(component);
	}

	/**
	 * Packs and updates {@code window}'s minimum size so that it cannot be resized to be smaller than its packed size.
	 *
	 * @param <W> The actual type of {@code window}
	 * @param window
	 * <br>Not null
	 * <br>input-output
	 * @return {@code window}
	 * <br>Not null
	 */
	public static final <W extends Window> W packAndUpdateMinimumSize(final W window) {
		checkAWT();

		window.setMinimumSize(null);
		window.pack();
		window.setMinimumSize(window.getSize());

		return window;
	}

	/**
	 * Packs and centers {@code window} on the screen.
	 *
	 * @param <W> The actual type of {@code window}
	 * @param window
	 * <br>Not null
	 * <br>input-output
	 * @return {@code window}
	 * <br>Not null
	 */
	public static final <W extends Window> W packAndCenter(final W window) {
		checkAWT();

		window.pack();

		return center(window);
	}

	/**
	 * Centers {@code window} on the screen.
	 *
	 * @param <W> the actual type of {@code window}
	 * @param window
	 * <br>Not null
	 * <br>input-output
	 * <br>Shared
	 * @return {@code window}
	 * <br>Not null
	 * <br>Shared
	 */
	public static final <W extends Window> W center(final W window) {
		checkAWT();

		window.setLocationRelativeTo(null);

		return window;
	}

	/**
	 * Creates a menu bar from the nonnull elements of {@code menus}.
	 *
	 * @param menus
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final JMenuBar menuBar(final JMenu... menus) {
		checkAWT();

		final JMenuBar result = new JMenuBar();

		for (final JMenu menu : menus) {
			if (menu != null) {
				boolean menuHasNonnullItems = false;

				for (int i = 0; i < menu.getItemCount(); ++i) {
					menuHasNonnullItems |= menu.getItem(i) != null;
				}

				if (menuHasNonnullItems) {
					result.add(menu);
				}
			}
		}

		return result;
	}

	/**
	 * Creates a menu from the elements in {@code items}.
	 * <br>A null element generates a separator.
	 * <br>Consecutive null elements are coalesced into only one null element.
	 * <br>If all the elements are null, then the generated menu is empty
	 * (it doesn't even contain a separator).
	 *
	 * @param title
	 * <br>Not null
	 * @param items
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final JMenu menu(final String title, final JMenuItem... items) {
		checkAWT();

		final JMenu result = new JMenu(title);

		boolean lastItemWasNull = true;

		for (final JMenuItem item : items) {
			if (item != null) {
				result.add(item);
			} else if (!lastItemWasNull) {
				result.addSeparator();
			}

			lastItemWasNull = item == null;
		}

		return result;
	}
    
    /**
     * @return
     * <br>Not null
     */
    public static final DataFlavor getURIListAsStringFlavor() {
        if (uriListAsStringFlavor == null) {
            try {
                uriListAsStringFlavor = new DataFlavor("text/uri-list; class=java.lang.String");
            } catch (final ClassNotFoundException exception) {
                getLoggerForThisMethod().log(Level.SEVERE, "", exception);
            }
        }
        
        return uriListAsStringFlavor;
    }
    
	/**
	 *
	 * @param event
	 * <br>Not null
	 * <br>Input-output
	 * @return a list of files, or an empty list if {@code event} cannot provide a list of files or a string
	 * <br>Not null
	 * <br>Maybe new
	 * @throws RuntimeException if an error occurs
	 */
	@SuppressWarnings("unchecked")
	public static final List<File> getFiles(final DropTargetDropEvent event) {
		event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		
		try {
			if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
			}
			
			final String transferDataString = (String) event.getTransferable().getTransferData(DataFlavor.stringFlavor);
			
			if (event.isDataFlavorSupported(getURIListAsStringFlavor())) {
				final List<File> result = new ArrayList<File>();
				final Scanner scanner = new Scanner(transferDataString);
				
				try {
					while (scanner.hasNext()) {
						final String fileURL = URLDecoder.decode(scanner.nextLine(), "UTF-8");
						final String protocol = "file:";
						
						result.add(new File(fileURL.startsWith(protocol) ? fileURL.substring(protocol.length()) : fileURL));
					}
				} finally {
					scanner.close();
				}
				
				return result;
			}
			
			if (event.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return Arrays.asList(new File(transferDataString));
			}
			
			return Collections.emptyList();
		} catch (final Exception exception) {
			throw unchecked(exception);
		}
	}
	
	public static final void useSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception exception) {
			getLoggerForThisMethod().log(Level.WARNING, "", exception);
		}
	}
	
	/**
	 * Executes in the AWT Event Dispatching Thread a runnable invoking
	 * the caller method with the specified arguments,
	 * or does nothing if the method is called in that thread.
	 * <br>This method can be used to simplify code that needs to be executed in AWT
	 * by taking care of generating an anonymous inner class implementing {@link Runnable}.
	 * <br>Example:
	 * <pre>
	 * public final void f() {
	 *	 // Warning: this section might get executed 2 times in different threads
	 *
	 *	 if (SwingTools.canInvokeThisMethodInAWT(this)) {
	 *		 // This section is executed only once in the AWT Event Dispatching Thread
	 *		 // For instance, the following instruction doesn't throw
	 *		 SwingTools.checkAWT();
	 *	 }
	 *
	 *	 // Warning: this section might get executed 2 times in different threads
	 * }
	 * </pre>
	 *
	 * @param object The caller object or {@code null} if the caller is static
	 * <br>Maybe null
	 * <br>Shared
	 * @param arguments
	 * <br>Not null
	 * <br>Shared
	 * @return {@code true} if and only if the method is called in the AWT Event Dispatching Thread
	 * @throws RuntimeException if an error occurs
	 */
	public static final boolean canInvokeThisMethodInAWT(final Object object, final Object... arguments) {
		if (SwingUtilities.isEventDispatchThread()) {
			return true;
		}

		final Class<?> callerClass = getCallerClass();
		final String callerMethodName = getCallerMethodName();

		try {
			SwingUtilities.invokeAndWait(createInvoker(object, callerClass, callerMethodName, arguments));
		} catch (final InterruptedException exception) {
			getLoggerForThisMethod().log(Level.WARNING, null, exception);
		} catch (final InvocationTargetException exception) {
			throw unchecked(exception.getCause());
		}

		return false;
	}

	/**
	 * Non-blocking version of {@link #canInvokeThisMethodInAWT(java.lang.Object, java.lang.Object[])}.
	 *
	 * @param object
	 * <br>Maybe null
	 * <br>Shared
	 * @param arguments
	 * <br>Not null
	 * <br>Shared
	 * @return {@code true} if and only if the method is called in the AWT Event Dispatching Thread
	 */
	public static final boolean canInvokeLaterThisMethodInAWT(final Object object, final Object... arguments) {
		if (SwingUtilities.isEventDispatchThread()) {
			return true;
		}

		final Class<?> callerClass = getCallerClass();
		final String callerMethodName = getCallerMethodName();

		SwingUtilities.invokeLater(createInvoker(object, callerClass, callerMethodName, arguments));

		return false;
	}

	/**
	 * Creates an action that will invoke the specified method with
	 * the specified arguments when it is performed.
	 *
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
	public static final InvokerAction action(final Object objectOrClass,
			final String methodName, final Object... arguments) {
		return new InvokerAction(objectOrClass, methodName, arguments);
	}

	/**
	 *
	 * @throws IllegalStateException if the current thread is not the AWT Event Dispatching Thread
	 */
	public static final void checkAWT() {
		if (SwingTools.checkAWT.get() && !SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("This section must be executed in the AWT Event Dispatching Thread");
		}
	}

	/**
	 *
	 * @throws IllegalStateException if the current thread is the AWT Event Dispatching Thread
	 */
	public static final void checkNotAWT() {
		if (SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("This section must not be executed in the AWT Event Dispatching Thread");
		}
	}

	/**
	 *
	 * @param object
	 * <br>Maybe null
	 * <br>Shared
	 * @param callerClass
	 * <br>Not null
	 * <br>Shared
	 * @param callerMethodName
	 * <br>Not null
	 * <br>Shared
	 * @param arguments
	 * <br>Not null
	 * <br>Shared
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	private static final Runnable createInvoker(final Object object, final Class<?> callerClass, final String callerMethodName, final Object... arguments) {
		return new Runnable() {

			@Override
			public final void run() {
				invoke(object == null ? callerClass : object, callerMethodName, arguments);
			}

		};
	}

	/**
	 * @param components
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final Box horizontalBox(final Component... components) {
		checkAWT();

		final Box result = Box.createHorizontalBox();

		for (final Component component : components) {
			result.add(component);
		}

		return result;
	}

	/**
	 * @param components
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final Box verticalBox(final Component... components) {
		checkAWT();

		final Box result = Box.createVerticalBox();

		for (final Component component : components) {
			result.add(component);
		}

		return result;
	}

	/**
	 * @param leftComponent
	 * <br>Maybe null
	 * <br>Will become reference
	 * @param rightComponent
	 * <br>Maybe null
	 * <br>Will become reference
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final JSplitPane horizontalSplit(final Component leftComponent, final Component rightComponent) {
		checkAWT();

		return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftComponent, rightComponent);
	}

	/**
	 * @param topComponent
	 * <br>Maybe null
	 * <br>Will become reference
	 * @param bottomComponent
	 * <br>Maybe null
	 * <br>Will become reference
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final JSplitPane verticalSplit(final Component topComponent, final Component bottomComponent) {
		checkAWT();

		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, topComponent, bottomComponent);
	}
	
	/**
	 * The methods in this class create localized menu elements using {@link Messages#translate(java.lang.Object, java.lang.Object[])}.
	 * 
	 * @author codistmonk (creation 2012-04-15)
	 */
	public static final class I18N {
		
		/**
		 * @throws IllegalInstantiationException To prevent instantiation
		 */
		private I18N() {
			throw new IllegalInstantiationException();
		}
		
		/**
		 * @param translationKey
		 * <br>Not null
		 * <br>Will become reference
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
		 * @param translationKey
		 * <br>Not null
		 * <br>Will become reference
		 * @param objectOrClass
		 * <br>Not null
		 * <br>Will become reference
		 * @param methodName
		 * <br>Not null
		 * <br>Will become reference
		 * @param arguments
		 * <br>Not null
		 * <br>Will become reference
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public static final JMenuItem item(final String translationKey,
				final Object objectOrClass, final String methodName, final Object... arguments) {
			return translate(new JMenuItem(
					action(objectOrClass, methodName, arguments)
					.setName(translationKey)));
		}
		
		/**
		 * @param translationKey
		 * <br>Not null
		 * <br>Will become reference
		 * @param shortcut
		 * <br>Not null
		 * <br>Will become reference
		 * @param objectOrClass
		 * <br>Not null
		 * <br>Will become reference
		 * @param methodName
		 * <br>Not null
		 * <br>Will become reference
		 * @param arguments
		 * <br>Not null
		 * <br>Will become reference
		 * @return
		 * <br>Not null
		 * <br>New
		 */
		public static final JMenuItem item(final String translationKey, final KeyStroke shortcut,
				final Object objectOrClass, final String methodName, final Object... arguments) {
			return translate(new JMenuItem(
					action(objectOrClass, methodName, arguments)
					.setName(translationKey)
					.setShortcut(shortcut)));
		}
		
	}
	
}
