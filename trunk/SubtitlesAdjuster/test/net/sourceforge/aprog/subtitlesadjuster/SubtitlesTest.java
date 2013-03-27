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

import static net.sourceforge.aprog.subtitlesadjuster.Subtitles.TIME_FORMAT;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterConstants.Variables.*;
import static net.sourceforge.aprog.tools.Tools.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.aprog.context.Context;

import org.junit.Test;

/**
 *
 * @author codistmonk (creation 2010-06-28)
 */
public final class SubtitlesTest {

    @Test
    public final void testSave() throws ParseException {
        final File file = createTemporaryFile("tmp", ".srt", getInputStream(SRT_RESOURCE_PATH));
        final Context context = SubtitlesAdjuster.newContext();

        assertNull(context.get(FILE));

        ((Subtitles) context.get(SUBTITLES)).load(file);

        assertSame(file, context.get(FILE));
        assertFalse((Boolean) context.get(FILE_MODIFIED));
        assertEquals("00:00:08,040", TIME_FORMAT.format(context.get(FIRST_TIME)));
        assertEquals("01:10:27,760", TIME_FORMAT.format(context.get(LAST_TIME)));

        context.set(FIRST_TIME, TIME_FORMAT.parse("00:00:10,000"));
        context.set(LAST_TIME, TIME_FORMAT.parse("01:10:40,000"));

        assertTrue((Boolean) context.get(FILE_MODIFIED));

        ((Subtitles) context.get(SUBTITLES)).save();

        assertFalse((Boolean) context.get(FILE_MODIFIED));

        ((Subtitles) context.get(SUBTITLES)).load(file);

        assertSame(file, context.get(FILE));
        assertFalse((Boolean) context.get(FILE_MODIFIED));
        assertEquals("00:00:10,000", TIME_FORMAT.format(context.get(FIRST_TIME)));
        assertEquals("01:10:40,000", TIME_FORMAT.format(context.get(LAST_TIME)));
    }

    @Test
    public final void testLoad() {
        final Context context = SubtitlesAdjuster.newContext();

        assertNull(context.get(FILE));

        ((Subtitles) context.get(SUBTITLES)).load(SRT_FILE);

        assertSame(SRT_FILE, context.get(FILE));
        assertEquals("00:00:08,040", TIME_FORMAT.format(context.get(FIRST_TIME)));
        assertEquals("01:10:27,760", TIME_FORMAT.format(context.get(LAST_TIME)));
    }

    private static final String SRT_RESOURCE_PATH = getThisPackagePath() + "REC.en.srt";

    private static final File SRT_FILE = getFile(SRT_RESOURCE_PATH);

    /**
     *
     * @param resourcePath
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    private static final InputStream getInputStream(final String resourcePath) {
        return getCallerClass().getClassLoader().getResourceAsStream(resourcePath);
    }

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

    /**
     * Creates and initializes a temporary file with data read from {@code contentsSource};
     * closes {@code contentsSource} when the initialization is done.
     *
     * @param prefix
     * <br>Not null
     * @param suffix
     * <br>Not null
     * @param contentsSource
     * <br>Not null
     * <br>Input-output
     * @return A file that will be deleted upon exit
     * <br>Not null
     * <br>New
     */
    private static final File createTemporaryFile(final String prefix, final String suffix, final InputStream contentsSource) {
        OutputStream output = null;

        try {
            final File result = File.createTempFile(prefix, suffix);

            result.deleteOnExit();

            output = new FileOutputStream(result);

            write(contentsSource, output);

            return result;
        } catch (final IOException exception) {
            throw unchecked(exception);
        } finally {
            close(output);
            close(contentsSource);
        }
    }

    /**
     *
     * @param source
     * <br>Not null
     * <br>Input-output
     * @param destination
     * <br>Not null
     * <br>Input-output
     * @throws IOException if an I/O error occurs
     */
    private static final void write(final InputStream source, final OutputStream destination) throws IOException {
        final byte[] buffer = new byte[1024];
        int readBytes = source.read(buffer);

        while (readBytes > 0) {
            destination.write(buffer, 0, readBytes);
            readBytes = source.read(buffer);
        }
    }

    /**
     * Tries to close {@code closable} using reflection and logs the eventual error;
     * does nothing if {@code closable} is null.
     *
     * @param closable
     * <br>Maybe null
     * <br>Input-output
     */
    private static final void close(final Object closable) {
        if (closable == null) {
            return;
        }

        try {
            invoke(closable, "close");
        } catch (final Throwable error) {
            Logger.getLogger(getCallerClass().getName() + "." + getCallerMethodName())
                    .log(Level.WARNING, "", error);
        }
    }

}