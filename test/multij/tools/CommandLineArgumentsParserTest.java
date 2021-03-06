/*
 *  The MIT License
 * 
 *  Copyright 2012 Codist Monk.
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import multij.tools.CommandLineArgumentsParser;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link CommandLineArgumentsParser}.
 *
 * @author codistmonk (creation 2012-09-03)
 */
public final class CommandLineArgumentsParserTest {
    
    @Test
    public final void test1() {
        final CommandLineArgumentsParser arguments = new CommandLineArgumentsParser("a", "42", "b", "1:2:5", "c", "0,-2,8:10", "e", "0x00FFFF", "f", "#FF000000", "g", "1:.5:2");
        
        assertEquals(42L, arguments.get1("a", 42));
        assertEquals(42L, arguments.get1("d", 42));
        assertEquals(42L, arguments.get1("a", 42L));
        assertEquals(42L, arguments.get1("d", 42L));
        assertEquals(42.0, arguments.get1("a", 42F), 0.0);
        assertEquals(42.0, arguments.get1("d", 42F), 0.0);
        assertEquals(42.0, arguments.get1("a", 42.0), 0.0);
        assertEquals(42.0, arguments.get1("d", 42.0), 0.0);
        assertArrayEquals(new int[] { 42 }, arguments.get("a", 41));
        assertArrayEquals(new int[] { 42 }, arguments.get("d", 42));
        assertArrayEquals(new int[] { 1, 3, 5 }, arguments.get("b"));
        assertArrayEquals(new int[] { 0, -2, 8, 9, 10 }, arguments.get("c"));
        assertArrayEquals(new int[] { 0x00FFFF }, arguments.get("e"));
        assertArrayEquals(new int[] { 0xFF000000 }, arguments.get("f"));
        assertArrayEquals(new float[] { 1F, 1.5F, 2F }, arguments.get("g", 0F), 0F);
        assertArrayEquals(new double[] { 1.0, 1.5, 2.0 }, arguments.get("g", 0.0), 0.0);
    }
    
}
