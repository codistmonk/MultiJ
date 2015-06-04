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

package multij.tools;

import java.io.File;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This class contains some of the keys used to retrieve information with {@link System#getProperty(String)}.
 * It also contains some methods that retrieve some system information which are not available through {@link System#getProperty(String)}.
 * 
 * @author codistmonk (creation 2010-10-24)
 */
public final class SystemProperties {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private SystemProperties() {
        throw new IllegalInstantiationException();
    }

    /**
     * {@value}.
     */
    public static final String AWT_TOOLKIT = "awt.toolkit";

    /**
     * {@value}.
     */
    @MaybeNull
    public static final String AWT_USE_SYSTEM_AA_FONT_SETTINGS = "awt.useSystemAAFontSettings";

    /**
     * {@value}.
     */
    @MaybeNull
    public static final String BROWSER = "browser";

    /**
     * {@value}.
     */
    @MaybeNull
    public static final String BROWSER_VERSION = "browser.version";

    /**
     * {@value}.
     */
    public static final String FILE_ENCODING = "file.encoding";

    /**
     * {@value}.
     */
    public static final String FILE_SEPARATOR = "file.separator";

    /**
     * {@value}.
     */
    public static final String JAVA_CLASS_PATH = "java.class.path";

    /**
     * {@value}.
     */
    public static final String JAVA_CLASS_VERSION = "java.class.version";

    /**
     * {@value}.
     * <br>Path of Java extensions.
     */
    public static final String JAVA_EXT_DIRS = "java.ext.dirs";

    /**
     * {@value}.
     */
    public static final String JAVA_HOME = "java.home";

    /**
     * {@value}.
     */
    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    /**
     * {@value}.
     */
    public static final String JAVA_LIBRARY_PATH = "java.library.path";

    /**
     * {@value}.
     */
    @MaybeNull
    public static final String JAVA_PROTOCOL_HANDLER_PKGS = "java.protocol.handler.pkgs";

    /**
     * {@value}.
     */
    public static final String JAVA_RUNTIME_VERSION = "java.runtime.version";

    /**
     * {@value}.
     */
    public static final String JAVA_SPECIFICATION_NAME = "java.specification.name";

    /**
     * {@value}.
     */
    public static final String JAVA_SPECIFICATION_VENDOR = "java.specification.vendor";

    /**
     * {@value}.
     */
    public static final String JAVA_SPECIFICATION_VERSION = "java.specification.version";

    /**
     * {@value}.
     */
    public static final String JAVA_VENDOR = "java.vendor";

    /**
     * {@value}.
     */
    public static final String JAVA_VENDOR_URL = "java.vendor.url";

    /**
     * {@value}.
     */
    public static final String JAVA_VERSION = "java.version";

    /**
     * {@value}.
     */
    public static final String JAVA_VM_NAME = "java.vm.name";

    /**
     * {@value}.
     */
    public static final String JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name";

    /**
     * {@value}.
     */
    public static final String JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";

    /**
     * {@value}.
     */
    public static final String JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version";

    /**
     * {@value}.
     */
    public static final String JAVA_VM_VENDOR = "java.vm.vendor";

    /**
     * {@value}.
     */
    public static final String JAVA_VM_VERSION = "java.vm.version";

    /**
     * {@value}.
     */
    public static final String LINE_SEPARATOR = "line.separator";

    /**
     * {@value}.
     * <br>Operating system architecture (eg {@code "x86"}).
     */
    public static final String OS_ARCH = "os.arch";

    /**
     * {@value}.
     * <br>32-bit or 64-bit addressing (eg {@code "32"}).
     */
    @MaybeNull
    public static final String OS_ARCH_DATA_MODEL = "os.arch.data.model";

    /**
     * {@value}.
     */
    public static final String OS_NAME = "os.name";

    /**
     * {@value}.
     */
    @MaybeNull
    public static final String SWING_AATEXT = "swing.aatext";

    /**
     * {@value}.
     */
    public static final String USER_COUNTRY = "user.country";

    /**
     * {@value}.
     * <br>User's current working directory.
     */
    public static final String USER_DIR = "user.dir";

    /**
     * {@value}.
     */
    public static final String USER_NAME = "user.name";

    /**
     * {@value}.
     * <br>Two-letter language code, lower case.
     */
    public static final String USER_LANGUAGE = "user.language";

    /**
     * {@value}.
     */
    public static final String USER_HOME = "user.home";

    /**
     * See {@link Runtime#availableProcessors()}.
     *
     * @return
     * <br>Range: {@code [0 .. Integer.MAX_VALUE]}
     */
    public static final int getAvailableProcessorCount() {
        return Runtime.getRuntime().availableProcessors();
    }
    
    /**
     * 
     * @return
     * <br>Not null
     */
    public static final String getJavaIOTemporaryDirectory() {
        return System.getProperty(JAVA_IO_TMPDIR);
    }
    
    /**
     * This property is available as a constant in {@link File}.
     * 
     * @return
     * <br>Not null
     */
    public static final String getFilePathSeparator() {
        return File.pathSeparator;
    }

    /**
     * This property is also available as a constant in {@link File}.
     * 
     * @return
     * <br>Not null
     */
    public static final String getFileSeparator() {
        return System.getProperty(FILE_SEPARATOR);
    }

    /**
     * 
     * @return
     * <br>Not null
     */
    public static final String getLineSeparator() {
        return System.getProperty(LINE_SEPARATOR);
    }

    /**
     * Some examples:<ul>
     *  <li>AIX
     *  <li>Digital Unix
     *  <li>FreeBSD
     *  <li>HP UX
     *  <li>Irix
     *  <li>Linux
     *  <li>Mac OS
     *  <li>Mac OS X
     *  <li>MPE/iX
     *  <li>Netware 4.11
     *  <li>OS/2
     *  <li>Solaris
     *  <li>Windows 2000
     *  <li>Windows 7
     *  <li>Windows 95
     *  <li>Windows 98
     *  <li>Windows NT
     *  <li>Windows Vista
     *  <li>Windows XP
     * </ul>
     * 
     * @return
     * <br>Not null
     */
    public static final String getOSName() {
        return System.getProperty(OS_NAME);
    }
    
    /**
     * Annotation indicating that the annotated entity may be {@code null}.
     * 
     * @author codistmonk (creation 2010-10-24)
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface MaybeNull {
        // Deliberately left empty
    }

}
