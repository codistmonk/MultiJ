/*
 *  The MIT License
 * 
 *  Copyright 2014 Codist Monk.
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

package multij.primitivelists;

import static multij.tools.Tools.array;
import static multij.tools.Tools.intRange;
import static multij.tools.Tools.invoke;
import static multij.tools.Tools.toUpperCamelCase;
import static org.junit.Assert.*;

import multij.primitivelists.PrimitiveList;
import multij.primitivelists.PrimitiveListTemplate;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link PrimitiveList}.
 * 
 * @author codistmonk (creation 2014-04-27)
 */
public final class PrimitiveListTest {
	
	@Test
	public final void test1() {
		final PrimitiveList list = new PrimitiveListTemplate();
		
		assertEquals(0L, list.size());
	}
	
	@Test
	public final void test2() throws Exception {
		final String prefix = this.getClass().getPackage().getName() + ".";
		
		for (final String primitive : array("boolean", "byte", "char", "short", "int", "long", "float", "double")) {
			final Class<?> cls = Class.forName(prefix + toUpperCamelCase(primitive + "List"));
			final PrimitiveList list = invoke(cls.getField("FACTORY").get(cls), "newInstance");
			
			assertEquals(0L, list.size());
		}
	}
	
	@Test
	public final void test3() {
		final int[] ints = intRange(3);
		final IntList list = new IntList(ints);
		
		assertSame(ints, list.toArray());
		
		list.add(-1);
		
		assertNotSame(ints, list.toArray());
		assertEquals(-1L, list.get(3));
	}
	
}
