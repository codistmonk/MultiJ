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

import static net.sourceforge.aprog.i18n.Messages.*;
import static net.sourceforge.aprog.swing.SwingTools.*;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.util.Date;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.Variable;
import net.sourceforge.aprog.events.Variable.Listener;
import net.sourceforge.aprog.events.Variable.ValueChangedEvent;
import net.sourceforge.aprog.i18n.Messages;
import net.sourceforge.aprog.tools.Tools;

/**
 *
 * @author codistmonk (creation 2010-06-26)
 */
public final class SubtitlesAdjuster {

    /**
     * Private default constructor to prevent instantiation.
     */
    private SubtitlesAdjuster() {
        // Do nothing
    }

    /**
     * {@value}.
     */
    private static final int INSET = 8;

    /**
     * {@value}.
     */
    public static final String FILE = "file";

    /**
     * {@value}.
     */
    public static final String START_TIME = "startTime";

    /**
     * {@value}.
     */
    public static final String END_TIME = "endTime";

    static {
        useSystemLookAndFeel();
        setMessagesBase(Tools.getCallerPackagePath() + "Messages");
    }

    /**
     * @param arguments the command line arguments
     * <br>Not null
     * <br>Unused
     */
    public static final void main(final String[] arguments) {
        if (canInvokeThisMethodInAWT(null, (Object) arguments)) {
            createMainFrame(createContext()).setVisible(true);
        }
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JFrame createMainFrame(final Context context) {
        final JFrame result = new JFrame() {

            @Override
            public final void pack() {
                this.setMinimumSize(null);

                super.pack();

                this.setMinimumSize(this.getSize());
            }

        };

        result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        result.setJMenuBar(createMenuBar());
        result.add(createMainPanel(context));

        return packAndCenter(result);
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuBar createMenuBar() {
        return menuBar(
                translate(menu("Application",
                        translate(new JMenuItem("About")),
                        null,
                        translate(new JMenuItem("Preferences...")),
                        null,
                        translate(new JMenuItem("Quit"))
                )),
                translate(menu("File",
                        translate(new JMenuItem("Open...")),
                        translate(new JMenuItem("Save"))
                )),
                translate(menu("Help",
                        translate(new JMenuItem("Manual"))
                )));
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JPanel createMainPanel(final Context context) {
        final JPanel result = new JPanel();
        final GridBagConstraints constraints = new GridBagConstraints();

        {
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            constraints.weighty = 0.0;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.insets = new Insets(INSET, INSET, INSET, INSET);

            add(result, translate(new JLabel("Start time")), constraints);
        }
        {
            ++constraints.gridx;
            constraints.weightx = 0.0;

            add(result, createTimeSpinner(context, START_TIME), constraints);
        }
        {
            constraints.gridx = 0;
            ++constraints.gridy;
            constraints.weightx = 1.0;

            add(result, translate(new JLabel("End time")), constraints);
        }
        {
            ++constraints.gridx;
            constraints.weightx = 0.0;

            add(result, createTimeSpinner(context, END_TIME), constraints);
        }

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @param variableName
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JSpinner createTimeSpinner(final Context context, final String variableName) {
        final JSpinner result = new JSpinner(new SpinnerDateModel());

        result.setEditor(new JSpinner.DateEditor(result, "HH:mm:ss,SSS"));

        result.addChangeListener(new ChangeListener() {

            @Override
            public final void stateChanged(final ChangeEvent event) {
                context.set(variableName, result.getValue());
            }

        });

        final Variable<Date> timeVariable = context.getVariable(variableName);

        timeVariable.addListener(new Listener<Date>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Date, ?> event) {
                result.setValue(event.getNewValue());
            }

        });

        final Variable<File> fileVariable = context.getVariable(FILE);

        fileVariable.addListener(new Listener<File>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<File, ?> event) {
                result.setEnabled(event.getNewValue() != null);
            }

        });

        result.setValue(context.get(variableName));
        result.setEnabled(context.get(FILE) != null);

        return result;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Context createContext() {
        final Context result = new Context();

        result.set(FILE, null);
        result.set(START_TIME, new Date(0L));
        result.set(END_TIME, new Date(0L));

        return result;
    }

}
