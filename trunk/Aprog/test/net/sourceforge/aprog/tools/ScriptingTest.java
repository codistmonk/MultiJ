/*
 *  The MIT License
 * 
 *  Copyright 2015 Codist Monk.
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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link Scripting}.
 *
 * @author codistmonk (creation 2015-04-09)
 */
public final class ScriptingTest {
	
	@Test
	public final void testMain1() {
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		System.setIn(new ByteArrayInputStream("print(1+1);".getBytes()));
		System.setOut(new PrintStream(buffer));
		
		Scripting.main();
		
		assertEquals("2", buffer.toString().trim());
	}
	
	@Test
	public final void testMain2() {
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		System.setIn(new ByteArrayInputStream("1+1".getBytes()));
		System.setOut(new PrintStream(buffer));
		
		Scripting.main();
		
		assertEquals("2", buffer.toString().trim());
	}
	
	@Test
	public final void testEval1() {
		final Scripting scripting = new Scripting();
		
		assertEquals(new Integer(2), scripting.eval("1+1"));
	}
	
	@Test
	public final void testEval2() {
		final Scripting scripting = new Scripting();
		
		scripting.eval("importClass('" + this.getClass().getName() + "');");
		
		assertEquals(new Integer(9), scripting.eval("f(3)"));
	}
	
	@Test
	public final void testImportAll() {
		final Scripting scripting = new Scripting();
		
		scripting.importAll(Tools.class.getName(), Tools.class.getPackage().getName());
		
		assertNotNull(scripting.eval("Tools"));
		assertNotNull(scripting.eval("debugPrint"));
	}
	
	@Test
	public final void testImportPackage1() {
		final Scripting scripting = new Scripting();
		
		scripting.importAll(Tools.class.getPackage().getName());
		
		assertNotNull(scripting.eval("Tools"));
	}
	
	@Test
	public final void testImportPackage2() {
		final Scripting scripting = new Scripting();
		
		scripting.importAll(Tools.class.getPackage());
		
		assertNotNull(scripting.eval("Tools"));
	}
	
	@Test
	public final void testImportClass1() {
		final Scripting scripting = new Scripting();
		
		scripting.importAll(Tools.class.getName());
		
		assertNotNull(scripting.eval("debugPrint"));
	}
	
	@Test
	public final void testImportClass2() {
		final Scripting scripting = new Scripting();
		
		scripting.importAll(Tools.class);
		
		assertNotNull(scripting.eval("debugPrint"));
	}
	
	public static final int f(final int x) {
		return x * x;
	}
	
}
