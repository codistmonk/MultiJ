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

import static net.sourceforge.aprog.subtitlesadjuster.Constants.Variables.*;
import static net.sourceforge.aprog.tools.Tools.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.i18n.Messages;

/**
 * Instances of this class manipulate the SRT file and update the context correspondingly.
 *
 * @author codistmonk (creation 2010-06-28)
 */
public final class Subtitles {

    private final Context context;

    private final List<Subtitle> subtitles;

    private long offset;

    private long newOffset;

    private double timeWarp;

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     */
    public Subtitles(final Context context) {
        this.context = context;
        this.subtitles = new ArrayList<Subtitle>();

        this.context.set(SUBTITLES, this);
    }

    public final void save() {
        this.offset = this.subtitles.get(0).getBeginTime();

        final long duration = this.subtitles.get(this.subtitles.size() - 1).getBeginTime() - this.offset;

        this.newOffset = ((Date) this.context.get(FIRST_TIME)).getTime();

        final long newDuration = ((Date) this.context.get(LAST_TIME)).getTime() - this.newOffset;

        this.timeWarp = (double) newDuration / duration;

        this.writeWithTimeWarp();
    }

    /**
     *
     * @param srtFile
     * <br>Not null
     * <br>Shared
     */
    public final void load(final File srtFile) {
        debugPrint(srtFile);

        Scanner scanner = null;

        try {
            scanner = new Scanner(new FileInputStream(srtFile));

            this.updateSubtitles(scanner);

            this.updateContext(srtFile);
        } catch (final Exception exception) {
            this.context.set(FILE, null);

            throw unchecked(exception);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     *
     * @param scanner
     * <br>Not null
     * <br>Input-output
     * @throws ParseException If the input is malformed
     */
    private final void updateSubtitles(final Scanner scanner) throws ParseException {
        this.subtitles.clear();

        while (scanner.hasNextInt()) {
            this.subtitles.add(new Subtitle(scanner.nextInt(), scanner));
        }
    }

    private final void writeWithTimeWarp() {
        PrintStream output = null;

        try {
            output = new PrintStream((File) this.context.get(FILE));

            for (final Subtitle subtitle : this.subtitles) {
                this.writeWithTimeWarp(subtitle, output);
            }

            this.context.set(FILE_MODIFIED, false);
        } catch (final Exception exception) {
            throw unchecked(exception);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     *
     * @param subtitle
     * <br>Not null
     * @param output
     * <br>Not null
     * <br>Input-output
     */
    private final void writeWithTimeWarp(final Subtitle subtitle, final PrintStream output) {
        final long newBeginTime = this.newOffset + Math.round((subtitle.getBeginTime() - this.offset) * this.timeWarp);

        output.println(subtitle.getIndex());
        output.println(format(new Date(newBeginTime), new Date(newBeginTime + subtitle.getDuration()), subtitle.getLines()));
    }

    /**
     *
     * @param srtFile
     * <br>Not null
     * <br>Shared
     */
    private void updateContext(final File srtFile) {
        if (this.subtitles.isEmpty()) {
            throw Messages.newLocalizedException("Invalid file $0", srtFile);
        }

        this.context.set(FILE, srtFile);
        this.context.set(FIRST_TIME, this.subtitles.get(0).getBegin());
        this.context.set(LAST_TIME, this.subtitles.get(this.subtitles.size() - 1).getBegin());
        this.context.set(FILE_MODIFIED, false);
    }

    /**
     *
     * @author codistmonk (creation 2010-06-28)
     */
    private static final class Subtitle {

        private final int index;

        private Date begin;

        private Date end;

        private String lines;

        /**
         * @param index
         * <br>Range: {@code [1 .. Integer.MAX_VALUE]}
         * @param scanner
         * <br>Not null
         * <br>Input-output
         * @throws ParseException If the input is malformed
         */
        Subtitle(final int index, final Scanner scanner) throws ParseException {
            this.index = index;
            this.lines = "";

            // Parse: begin --> end
            {
                this.begin = TIME_FORMAT.parse(scanner.next(TIME_PATTERN));

                scanner.findInLine(ARROW_PATTERN);

                this.end = TIME_FORMAT.parse(scanner.next(TIME_PATTERN));

                scanner.nextLine();
            }

            // Parse: lines
            {
                String line;

                while (!"".equals(line = scanner.nextLine())) {
                    this.lines += line + "\n";
                }
            }
        }

        /**
         *
         * @return
         * <br>Range: {@code [1 .. Integer.MAX_VALUE]}
         */
        public final int getIndex() {
            return this.index;
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Date getBegin() {
            return this.begin;
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Date getEnd() {
            return this.end;
        }

        /**
         *
         * @return Time in milliseconds
         * <br>Range: {@code [0L .. Long.MAX_VALUE]}
         */
        public final long getBeginTime() {
            return this.getBegin().getTime();
        }

        /**
         *
         * @return Time in milliseconds
         * <br>Range: {@code [0L .. Long.MAX_VALUE]}
         */
        public final long getDuration() {
            return this.getEnd().getTime() - this.getBeginTime();
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final String getLines() {
            return this.lines;
        }

        @Override
        public final String toString() {
            return format(this.getBegin(), this.getEnd(), this.getLines());
        }

    }

    /**
     * {@value}.
     */
    public static final String TIME_PATTERN = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";

    /**
     * {@value}.
     */
    public static final String ARROW_PATTERN = "\\-\\->";

    /**
     * Simple date format "HH:mm:ss,SSS".
     */
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss,SSS");

    /**
     *
     * @param begin
     * <br>Not null
     * @param end
     * <br>Not null
     * @param lines
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final String format(final Date begin, final Date end, final String lines) {
        return
                TIME_FORMAT.format(begin) + " --> " + TIME_FORMAT.format(end) + "\n" +
                lines;
    }

}
