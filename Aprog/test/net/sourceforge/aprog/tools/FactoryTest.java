/*
 *  The MIT License
 * 
 *  Copyright 2013 Codist Monk.
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

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import net.sourceforge.aprog.tools.Factory.ConstantFactory;
import net.sourceforge.aprog.tools.Factory.DefaultFactory;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link Factory}.
 *
 * @author codistmonk (creation 2013-07-02)
 */
public final class FactoryTest {
	
	@Test
	public final void testConstantFactory() {
		final Factory<?> factory = ConstantFactory.forInstance(42);
		final Object object1 = factory.newInstance();
		final Object object2 = factory.newInstance();
		
		assertEquals(42, object1);
		assertSame(object1, object2);
	}
	
	@Test
	public final void testDefaultFactory1() {
		final Factory<?> factory = DefaultFactory.forClass(Object.class);
		final Object object1 = factory.newInstance();
		final Object object2 = factory.newInstance();
		
		assertNotNull(object1);
		assertNotNull(object2);
		assertNotSame(object1, object2);
	}
	
	@Test
	public final void testDefaultFactory2() {
		final Factory<?> factory = DefaultFactory.forClass(Integer.class, 42);
		final Object object1 = factory.newInstance();
		final Object object2 = factory.newInstance();
		
		assertEquals(42, object1);
		assertEquals(object1, object2);
	}
	
	@Test
	public final void testDefaultFactory3() {
		assertEquals(true, DefaultFactory.forClass(Boolean.class, true).newInstance());
		assertEquals((Object) (byte) 42, DefaultFactory.forClass(Byte.class, (byte) 42).newInstance());
		assertEquals((Object) (char) 42, DefaultFactory.forClass(Character.class, (char) 42).newInstance());
		assertEquals((Object) (short) 42, DefaultFactory.forClass(Short.class, (short) 42).newInstance());
		assertEquals((Object) (short) 42, DefaultFactory.forClass(Short.class, (byte) 42).newInstance());
		assertEquals((Object) (int) 42, DefaultFactory.forClass(Integer.class, (int) 42).newInstance());
		assertEquals((Object) (int) 42, DefaultFactory.forClass(Integer.class, (short) 42).newInstance());
		assertEquals((Object) (int) 42, DefaultFactory.forClass(Integer.class, (char) 42).newInstance());
		assertEquals((Object) (long) 42, DefaultFactory.forClass(Long.class, (long) 42).newInstance());
		assertEquals((Object) (long) 42, DefaultFactory.forClass(Long.class, (int) 42).newInstance());
		assertEquals((Object) (float) 42, DefaultFactory.forClass(Float.class, (float) 42).newInstance());
		assertEquals((Object) (float) 42, DefaultFactory.forClass(Float.class, (long) 42).newInstance());
		assertEquals((Object) (double) 42, DefaultFactory.forClass(Double.class, (double) 42).newInstance());
		assertEquals((Object) (double) 42, DefaultFactory.forClass(Double.class, (float) 42).newInstance());
	}
	
}
