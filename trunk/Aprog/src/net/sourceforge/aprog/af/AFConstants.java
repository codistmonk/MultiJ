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

import net.sourceforge.aprog.tools.IllegalInstantiationException;

/**
 *
 * @author codistmonk (creation 2010-09-22)
 */
public final class AFConstants {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private AFConstants() {
        throw new IllegalInstantiationException();
    }

    /**
     * {@value}.
     */
    public static final String APPLICATION_NAME = "Application Framework";

    /**
     * {@value}.
     */
    public static final String APPLICATION_VERSION = "1.0.0-M5";

    /**
     * {@value}.
     */
    public static final String APPLICATION_COPYRIGHT = "© 2010 Codist Monk";

    /**
     * {@value}.
     */
    public static final String APPLICATION_ICON_PATH = null;

    /**
     * This class defines constants used to identify the application context variables.
     *
     * @author codistmonk (creation 2010-09-22)
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
        public static final String ACTIONS_SHOW_ABOUT_DIALOG = "actions.showAboutDialog";

        /**
         * {@value}.
         */
        public static final String ACTIONS_SHOW_PREFERENCES_DIALOG = "actions.showPreferencesDialog";

        /**
         * {@value}.
         */
        public static final String ACTIONS_QUIT = "actions.quit";

        /**
         * {@value}.
         */
        public static final String APPLICATION_NAME = "application.name";

        /**
         * {@value}.
         */
        public static final String APPLICATION_VERSION = "application.version";

        /**
         * {@value}.
         */
        public static final String APPLICATION_COPYRIGHT = "application.copyright";

        /**
         * {@value}.
         */
        public static final String APPLICATION_ICON_PATH = "application.icon.path";

        /**
         * {@value}.
         */
        public static final String MAIN_FRAME = "mainFrame";

        /**
         * {@value}.
         */
        public static final String MAIN_MENU_BAR = "mainMenuBar";

        /**
         * {@value}.
         */
        public static final String MAIN_PANEL = "mainPanel";

        /**
         * {@value}.
         */
        public static final String MAIN_FILES_TABBED_PANE = "mainFilesTabbedPane";

        /**
         * {@value}.
         */
        public static final String SELECTED_DOCUMENT = "selectedDocument";

    }

}
