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

/**
 *
 * @author codistmonk (creation 2010-06-27)
 */
public final class Constants {

    /**
     * Private default constructor to prevent instantiation.
     */
    private Constants() {
        // Do nothing
    }

    /**
     * {@value}.
     */
    public static final String APPLICATION_NAME = "SubtitlesAdjuster";

    /**
     * {@value}.
     */
    public static final String APPLICATION_VERSION = "1.0.0-M3";

    /**
     * {@value}.
     */
    public static final String APPLICATION_COPYRIGHT = "© 2010 Codist Monk";

    /**
     * This class defines constants used to identify the application context variables.
     * 
     * @author codistmonk (creation 2010-06-27)
     */
    public static final class Variables {

        /**
         * Private default constructor to prevent instantiation.
         */
        private Variables() {
            // Do nothing
        }

        /**
         * {@value}.
         */
        public static final String MAIN_FRAME = "mainFrame";

        /**
         * {@value}.
         */
        public static final String FILE = "file";

        /**
         * {@value}.
         */
        public static final String FILE_MODIFIED = "file.modified";

        /**
         * {@value}.
         */
        public static final String FIRST_TIME = "firstTime";

        /**
         * {@value}.
         */
        public static final String LAST_TIME = "lastTime";

        /**
         * {@value}.
         */
        public static final String SUBTITLES = "subtitles";

    }

}
