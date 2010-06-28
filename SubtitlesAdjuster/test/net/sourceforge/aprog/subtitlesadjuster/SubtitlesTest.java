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

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static net.sourceforge.aprog.subtitlesadjuster.Constants.Variables.*;
import static net.sourceforge.aprog.tools.Tools.*;

import static org.junit.Assert.*;

import net.sourceforge.aprog.context.Context;

import org.junit.Test;

/**
 *
 * @author codistmonk (creation 2010-06-28)
 */
public final class SubtitlesTest {

    @Test
    public final void testSave() {
        fail("TODO");
    }

    @Test
    public final void testLoad() {
        final Context context = SubtitlesAdjuster.createContext();

        assertNull(context.get(FILE));

        ((Subtitles) context.get(SUBTITLES)).load(SRT_FILE);

        assertSame(SRT_FILE, context.get(FILE));
        assertEquals("00:00:08,040", Subtitles.TIME_FORMAT.format(context.get(FIRST_TIME)));
        assertEquals("01:10:27,760", Subtitles.TIME_FORMAT.format(context.get(LAST_TIME)));
    }

    private static final String SRT_RESOURCE_PATH = getCallerPackagePath() + "REC.en.srt";

    private static final File SRT_FILE = getFile(SRT_RESOURCE_PATH);

    /**
     *
     * @param resourcePath
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    private static final File getFile(final String resourcePath) {
        try {
            return new File(getCallerClass().getClassLoader().getResource(resourcePath).toURI());
        } catch (final URISyntaxException exception) {
            throw unchecked(exception);
        }
    }

}