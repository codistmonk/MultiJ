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

package net.sourceforge.aprog.swing;

import static net.sourceforge.aprog.tools.Tools.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
     * Private default constructor to prevent instantiation.
     */
    private SwingTools() {
        // Do nothing
    }

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

            final ImageIcon icon = new ImageIcon(ImageIO.read(
                    SwingTools.class.getClassLoader().getResourceAsStream(getImagesBase() + resourceName)));

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
            return null;
        }
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
			result.add(menu);
        }

		return result;
	}

	/**
	 *
	 * @param title
	 * <br>Not null
	 * @param subMenuItems
	 * <br>Not null
	 * @return
	 * <br>Not null
	 * <br>New
	 */
	public static final JMenu menu(final String title, final JMenuItem... subMenuItems) {
		checkAWT();

		final JMenu result = new JMenu(title);

		for (final JMenuItem subMenu : subMenuItems) {
			if (subMenu == null) {
				result.addSeparator();
            } else {
				result.add(subMenu);
            }
		}

		return result;
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
			if (event.getCurrentDataFlavorsAsList().contains(DataFlavor.javaFileListFlavor)) {
				return (List<File>)event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
			}

			if (event.getCurrentDataFlavorsAsList().contains(DataFlavor.stringFlavor)) {
				return Arrays.asList(new File((String)event.getTransferable().getTransferData(DataFlavor.stringFlavor)));
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
     *     // Warning: this section might get executed 2 times in different threads
     *
     *     if (SwingTools.canInvokeThisMethodInAWT(this)) {
     *         // This section is executed only once in the AWT Event Dispatching Thread
     *         // For instance, the following instruction doesn't throw
     *         SwingTools.checkAWT();
     *     }
     *
     *     // Warning: this section might get executed 2 times in different threads
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
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public final void run() {
                    invoke(object == null ? callerClass : object, callerMethodName, arguments);
                }

            });
        } catch (final InterruptedException exception) {
            getLoggerForThisMethod().log(Level.WARNING, null, exception);
        } catch (final InvocationTargetException exception) {
            throw unchecked(exception);
        }

        return false;
    }

    /**
     *
     * @throws IllegalStateException if the current thread is not the AWT Event Dispatching Thread
     */
    public static final void checkAWT() {
        if (!SwingUtilities.isEventDispatchThread()) {
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

}
