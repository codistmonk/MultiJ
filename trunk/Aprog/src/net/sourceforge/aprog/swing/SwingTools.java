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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.sourceforge.aprog.tools.Tools;

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

    private static final Map<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();

    public static final String IMAGES_BASE = "images/";

    /**
     *
     * @param resourceName
     * <br>Should not be null
     * @return
     * <br>A non-null value
     * <br>A new value
     * @throws RuntimeException if the resource cannot be loaded
     */
    public static final ImageIcon getIcon(final String resourceName) {
        try {
            final ImageIcon cachedIcon = iconCache.get(resourceName);

            if (cachedIcon != null) {
                return cachedIcon;
            }

            final ImageIcon icon = new ImageIcon(ImageIO.read(
                    SwingTools.class.getClassLoader().getResourceAsStream(IMAGES_BASE + resourceName)));

            iconCache.put(resourceName, icon);

            return icon;
        } catch (final IOException exception) {
            return Tools.throwUnchecked(exception);
        }
    }

    /**
     *
     * @param resourceName
     * <br>Should not be null
     * @return
     * <br>A possibly null value
     * <br>A new value
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
     * <br>Should not be null
     * <br>Input-output parameter
     * @param component
     * <br>Should not be null
     * <br>Input-output parameter
     * <br>Shared parameter
     * @param constraints
     * <br>Should not be null
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
     * <br>Should not be null
     * <br>Input-output parameter
     * <br>Shared parameter
     * @param imageName
     * <br>Should not be null
     * @param borderPainted if {@code false}, then the preferred size is set to the size of the image,
     * and the background and border are not drawn; if {@true}, {@code button} is left in its current state
     * @return {@code button}
     * <br>A non-null value
     * <br>A shared value
     */
    public static final <T extends AbstractButton> T rollover(final T button, final String imageName, final boolean borderPainted) {
        checkAWT();

        button.setRolloverEnabled(true);
        button.setDisabledIcon(getIconOrNull(imageName + "_disabled.png"));
        button.setIcon(getIconOrNull(imageName + ".png"));
        button.setSelectedIcon(getIconOrNull(imageName + "_selected.png"));
        button.setRolloverIcon(getIconOrNull(imageName + "_rollover.png"));
        button.setRolloverSelectedIcon(getIconOrNull(imageName + "_rollover_selected.png"));

        if (!borderPainted) {
            if (button.getIcon() != null) {
                button.setPreferredSize(new Dimension(button.getIcon().getIconWidth(), button.getIcon().getIconHeight()));
            }

            button.setBorderPainted(false);
        }

        return button;
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
