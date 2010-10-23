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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link Launcher}.
 *
 * @author codistmonk (creation 2010-06-11)
 */
public class LauncherTest {

    @Test(timeout = 10000L)
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
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testIsNativeLibrary() {
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testPipe() {
        debugPrint("TODO"); // TODO
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
        debugPrint("TODO"); // TODO
    }

    @Test
    public final void testNativeLibraryCollector() {
        debugPrint("TODO"); // TODO
    }
    
}
