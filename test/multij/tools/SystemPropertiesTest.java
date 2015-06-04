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

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import multij.tools.SystemProperties;
import multij.tools.SystemProperties.MaybeNull;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link SystemProperties}.
 * 
 * @author codistmonk (creation 2010-10-24)
 */
public final class SystemPropertiesTest {

    @Test
    public final void testKeys() throws Exception {
        final Set<String> keys = new HashSet<String>();

        for (final Field field : SystemProperties.class.getFields()) {
            if (String.class.equals(field.getType()) && field.getAnnotation(MaybeNull.class) == null) {
                final String key = field.get(null).toString();
                final String fieldSummary = field.getName() + " " + key;
                
                assertTrue("Duplicate property " + fieldSummary, !keys.contains(key));
                
                keys.add(key);
                
                assertNotNull("Null property " + fieldSummary, System.getProperty(key));
            }
        }
    }

    @Test
    public final void testGetAvailableProcessorCount() {
        final int processorCount = SystemProperties.getAvailableProcessorCount();
        
        assertTrue("Processor count " + processorCount, 0 <= SystemProperties.getAvailableProcessorCount());
    }

    @Test
    public final void testGetJavaIOTemporaryDirectory() {
        assertNotNull(SystemProperties.getJavaIOTemporaryDirectory());
    }

    @Test
    public final void testGetFilePathSeparator() {
        assertNotNull(SystemProperties.getFilePathSeparator());
    }

    @Test
    public final void testGetFileSeparator() {
        assertNotNull(SystemProperties.getFileSeparator());
    }

    @Test
    public final void testGetLineSeparator() {
        assertNotNull(SystemProperties.getLineSeparator());
    }

    @Test
    public final void testGetOSName() {
        assertNotNull(SystemProperties.getOSName());
    }

}
