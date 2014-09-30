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

package net.sourceforge.aprog.tools;

import static net.sourceforge.aprog.tools.Tools.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link Launcher}.
 *
 * @author codistmonk (creation 2010-06-23)
 */
public final class LauncherTest {

    @Test(timeout = TIMEOUT)
    public final void testLaunch() throws InterruptedException {
        assertEquals(0, Launcher.launch(EchoApplicationFile.class).waitFor());
    }

    @Test
    public final void testCreateLibraryPath() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testExecute() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testGetClassPathInIDE() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testGetJars() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testGetLibraryPath() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testGetNativeLibraryFiles() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testIsJar() {
        assertTrue(Launcher.isJar(new File("aprog.jar")));
    }

    @Test
    public final void testIsNativeLibrary() {
        switch (OS.getCurrentOS()) {
        case LINUX:
            assertTrue(Launcher.isNativeLibrary(new File("library.so")));
            break;
        case MAC_OS_X:
            assertTrue(Launcher.isNativeLibrary(new File("library.dylib")));
            assertTrue(Launcher.isNativeLibrary(new File("library.jnilib")));
            break;
        case SOLARIS:
            assertTrue(Launcher.isNativeLibrary(new File("library.so")));
            break;
        case WINDOWS:
            assertTrue(Launcher.isNativeLibrary(new File("library.dll")));
            break;
        default:
            debugPrint("TODO: " + SystemProperties.getOSName());
            break;
        }
    }

    @Test(timeout = TIMEOUT)
    public final void testPipe() throws IOException, InterruptedException {
        final String string = "42" + SystemProperties.getLineSeparator();
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ByteArrayInputStream input = new ByteArrayInputStream(string.getBytes());
        
        final Thread pipe = Launcher.pipe(input, new PrintStream(buffer));
        
        input.close();
        buffer.flush();
        pipe.join();
        
        assertEquals(string, buffer.toString());
    }

    @Test
    public final void testRedirectOutputsToConsole() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testStartApplicationFromJar() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testStartApplicationFromIDE() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testRecursiveCollector() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testJarCollector() {
        assertEquals(1, new Launcher.JarCollector().collect(new File("test")).size());
    }

    @Test
    public final void testNativeLibraryCollector() {
        final List<File> nativeLibraries = new Launcher.NativeLibraryCollector().collect(new File("test"));
        
        switch (OS.getCurrentOS()) {
        case MAC_OS_X:
            assertEquals(2, nativeLibraries.size());
            break;
        case LINUX:
        case SOLARIS:
        case WINDOWS:
            assertEquals(1, nativeLibraries.size());
            break;
        default:
            debugPrint("TODO: " + SystemProperties.getOSName());
            break;
        }
    }

    /**
     * {@value} milliseconds.
     */
    public static final long TIMEOUT = 10000L;

    /**
     * 
     * @author codistmonk (creation 2010-10-26)
     *
     */
    public static enum OS {

        LINUX, MAC_OS_X, SOLARIS, WINDOWS;

        /**
         * 
         * @return
         * <br>Not null
         */
        public static final OS getCurrentOS() {
            final String osName = SystemProperties.getOSName().toLowerCase(Locale.ENGLISH);

            if (osName.startsWith("linux")) {
                return LINUX;
            } else if (osName.startsWith("mac os x")) {
                return MAC_OS_X;
            } else if (osName.startsWith("solaris")) {
                return SOLARIS;
            } else if (osName.startsWith("windows")) {
                return WINDOWS;
            }

            return null;
        }
        
    }
    
}
