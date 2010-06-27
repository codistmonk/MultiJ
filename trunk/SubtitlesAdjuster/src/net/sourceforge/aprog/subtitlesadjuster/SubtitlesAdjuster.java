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

import java.awt.event.WindowEvent;
import static net.sourceforge.aprog.i18n.Messages.*;
import static net.sourceforge.aprog.swing.SwingTools.*;
import static net.sourceforge.aprog.tools.Tools.*;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.Variable;
import net.sourceforge.aprog.events.Variable.Listener;
import net.sourceforge.aprog.events.Variable.ValueChangedEvent;
import net.sourceforge.aprog.i18n.Translator;

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
    public static final String APPLICATION_NAME = "SubtitlesAdjuster";

    /**
     * {@value}.
     */
    public static final String APPLICATION_VERSION = "1.0.0-M3";

    /**
     * {@value}.
     */
    public static final String APPLICATION_COPYRIGHT = "© 2010 Codist Monk";

    /**
     * {@value}.
     */
    public static final String MAIN_FRAME = "mainFrame";

    /**
     * {@value}.
     */
    public static final String FILE = "file";

    /**
     * {@value}.
     */
    public static final String FILE_MODIFIED = "file.modified";

    /**
     * {@value}.
     */
    public static final String FIRST_TIME = "firstTime";

    /**
     * {@value}.
     */
    public static final String LAST_TIME = "lastTime";

    static {
        useSystemLookAndFeel();
        setMessagesBase(getCallerPackagePath() + "Messages");
    }

    /**
     * @param arguments the command line arguments
     * <br>Not null
     * <br>Shared
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
        final JFrame result = new JFrame();

        context.set(MAIN_FRAME, result);

        result.setJMenuBar(createMenuBar(context));
        result.add(createMainPanel(context));

        result.addWindowListener(new WindowAdapter() {

            @Override
            public final void windowClosing(final WindowEvent event) {
                quit(context);
            }

        });

        invokeOnVariableChanged(context, FILE, SubtitlesAdjuster.class, "updateMainFrameTitle", context);
        invokeOnVariableChanged(context, FILE_MODIFIED, SubtitlesAdjuster.class, "updateMainFrameTitle", context);

        return center(packAndUpdateMinimumSize(result));
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void updateMainFrameTitle(final Context context) {
        ((JFrame) context.get(MAIN_FRAME)).setTitle(createMainFrameTitle(context));
    }

    /**
     *
     * @param context
     * <br>Not null
     * @param variableName
     * <br>Not null
     * @param objectOrClass
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     */
    public static final void invokeOnVariableChanged(final Context context, final String variableName, final Object objectOrClass, final String methodName, final Object... arguments) {
        final Variable<Object> variable = context.getVariable(variableName);

        variable.addListener(new Listener<Object>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<Object, ?> event) {
                invoke(objectOrClass, methodName, arguments);
            }

        });

        invoke(objectOrClass, methodName, arguments);
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     */
    public static final String createMainFrameTitle(final Context context) {
        final File file = context.get(FILE);
        final Boolean fileModified = context.get(FILE_MODIFIED);

        return file == null ? APPLICATION_NAME : file.getName() + (fileModified ? "*" : "");
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
    public static final JMenuBar createMenuBar(final Context context) {
        return menuBar(
                translate(menu("Application",
                        item("About", "showAboutDialog", context),
                        null,
                        item("Preferences...", "showPreferencesDialog", context),
                        null,
                        item("Quit", "quit", context)
                )),
                translate(menu("File",
                        item("Open...", "open", context),
                        item("Save", "save", context)
                )),
                translate(menu("Help",
                        item("Manual", "showManual", context)
                )));
    }

    /**
     *
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JMenuItem item(final String translationKey, final String methodName, final Object... arguments) {
        return translate(new JMenuItem(action(translationKey, SubtitlesAdjuster.class, methodName, arguments)));
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void showAboutDialog(final Context context) {
        JOptionPane.showMessageDialog(
                (Component) context.get(MAIN_FRAME),
                APPLICATION_NAME + "\n" + APPLICATION_VERSION + "\n" + APPLICATION_COPYRIGHT,
                translate("About $0", APPLICATION_NAME),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void showPreferencesDialog(final Context context) {
        createPreferencesDialog(context).setVisible(true);
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JDialog createPreferencesDialog(final Context context) {
        final JDialog result = translate(new JDialog((JFrame) context.get(MAIN_FRAME), "Preferences", true));

        result.add(createPreferencesPanel(context));

        return center(packAndUpdateMinimumSize(result));
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JPanel createPreferencesPanel(final Context context) {
        final JPanel result = new JPanel();
        final GridBagConstraints constraints = new GridBagConstraints();

        {
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            constraints.weighty = 0.0;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.insets = new Insets(INSET, INSET, INSET, INSET);

            add(result, translate(new JLabel("Language")), constraints);
        }
        {
            ++constraints.gridx;
            constraints.weightx = 0.0;

            add(result, createLanguageComboBox(), constraints);
        }

        return result;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JComboBox createLanguageComboBox() {
        final Translator translator = Translator.getDefaultTranslator();
        final JComboBox result = new JComboBox(translator.getAvailableLocales());

        result.setSelectedItem(translator.getBestAvailableLocale());

        result.addActionListener(new ActionListener() {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                translator.setLocale((Locale) result.getSelectedItem());

                final Component root = SwingUtilities.getRoot(result);

                if (root instanceof Window) {
                    ((Window) root).pack();
                }
            }

        });

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void quit(final Context context) {
        System.exit(0);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void open(final Context context) {
        showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Input-output
     */
    public static final void save(final Context context) {
        showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void showManual(final Context context) {
        showTODOMessage(context);
    }

    /**
     *
     * @param context
     * <br>Not null
     */
    public static final void showTODOMessage(final Context context) {
        System.out.println(debug(3, "TODO"));
        JOptionPane.showMessageDialog(
                (Component) context.get(MAIN_FRAME),
                "Not implemented",
                APPLICATION_NAME,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates an action that will invoke the specified method with the specified arguments when it is performed.
     *
     * @param name
     * <br>Not null
     * <br>Shared
     * @param objectOrClass
     * <br>Not null
     * <br>Shared
     * @param methodName
     * <br>Not null
     * <br>Shared
     * @param arguments
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Action action(final String name, final Object objectOrClass, final String methodName, final Object... arguments) {
        // TODO scan objectOrClass and throw an exception if there are no candidate methods

        return new AbstractAction(name) {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                invoke(objectOrClass, methodName, arguments);
            }

            private static final long serialVersionUID = -7021271248658829634L;

        };
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

            add(result, translate(new JLabel("First message time")), constraints);
        }
        {
            ++constraints.gridx;
            constraints.weightx = 0.0;

            add(result, createTimeSpinner(context, FIRST_TIME), constraints);
        }
        {
            constraints.gridx = 0;
            ++constraints.gridy;
            constraints.weightx = 1.0;

            add(result, translate(new JLabel("Last message time")), constraints);
        }
        {
            ++constraints.gridx;
            constraints.weightx = 0.0;

            add(result, createTimeSpinner(context, LAST_TIME), constraints);
        }
        {
            ++constraints.gridy;

            add(result, createSaveButton(context), constraints);
        }

        return result;
    }

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JButton createSaveButton(final Context context) {
        final JButton result = translate(new JButton("Save"));

        synchronizeComponentEnabledWithFileVariableNullity(result, context);

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

        result.setValue(context.get(variableName));

        synchronizeComponentEnabledWithFileVariableNullity(result, context);

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
        result.set(FILE_MODIFIED, false);
        result.set(FIRST_TIME, new Date(0L));
        result.set(LAST_TIME, new Date(0L));

        setFileModifiedOnVariableChanged(result, FIRST_TIME, true);
        setFileModifiedOnVariableChanged(result, LAST_TIME, true);
        setFileModifiedOnVariableChanged(result, FILE, false);

        return result;
    }

    /**
     *
     * @param component
     * <br>Not null
     * <br>Shared
     * @param context
     * <br>Not null
     */
    private static final void synchronizeComponentEnabledWithFileVariableNullity(
            final Component component, final Context context) {
        final Variable<File> fileVariable = context.getVariable(FILE);

        fileVariable.addListener(new Listener<File>() {

            @Override
            public final void valueChanged(final ValueChangedEvent<File, ?> event) {
                component.setEnabled(event.getNewValue() != null);
            }

        });

        component.setEnabled(context.get(FILE) != null);
    }

    /**
     *
     * @param context
     * <br>Not null
     * @param variableName
     * <br>Not null
     * @param value
     */
    private static final void setFileModifiedOnVariableChanged(
            final Context context, final String variableName, final boolean value) {
        invokeOnVariableChanged(context, variableName, context.getVariable(FILE_MODIFIED), "setValue", value);
    }

}
