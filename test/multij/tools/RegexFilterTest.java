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

package multij.tools;

import static org.junit.Assert.*;
import multij.tools.RegexFilter;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link RegexFilter}.
 *
 * @author codistmonk (creation 2014-06-01)
 */
public final class RegexFilterTest {
	
	@Test
	public final void testAcceptExtension() {
		final RegexFilter filter = RegexFilter.newExtensionFilter("ext");
		
		assertTrue(filter.accept(null, "a.ext"));
		assertTrue(filter.accept(null, "a/b.c.ext"));
		assertFalse(filter.accept(null, ".ext"));
		assertFalse(filter.accept(null, "ext"));
	}
	
	@Test
	public final void testAcceptPrefix() {
		final RegexFilter filter = RegexFilter.newPrefixFilter("prefix");
		
		assertTrue(filter.accept(null, "prefix"));
		assertTrue(filter.accept(null, "prefix."));
		assertFalse(filter.accept(null, ".prefix"));
	}
	
	@Test
	public final void testAcceptSuffix() {
		final RegexFilter filter = RegexFilter.newSuffixFilter("suffix");
		
		assertTrue(filter.accept(null, "suffix"));
		assertTrue(filter.accept(null, ".suffix"));
		assertFalse(filter.accept(null, "suffix."));
	}
	
}
