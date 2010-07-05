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

import static javax.swing.KeyStroke.getKeyStroke;

import static net.sourceforge.aprog.i18n.Messages.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterConstants.Variables.*;
import static net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterTools.*;
import static net.sourceforge.aprog.swing.SwingTools.action;
import static net.sourceforge.aprog.swing.SwingTools.add;
import static net.sourceforge.aprog.swing.SwingTools.center;
import static net.sourceforge.aprog.swing.SwingTools.checkAWT;
import static net.sourceforge.aprog.swing.SwingTools.getFiles;
import static net.sourceforge.aprog.swing.SwingTools.menuBar;
import static net.sourceforge.aprog.swing.SwingTools.packAndUpdateMinimumSize;
import static net.sourceforge.aprog.swing.SwingTools.scrollable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.events.Variable;
import net.sourceforge.aprog.events.Variable.Listener;
import net.sourceforge.aprog.events.Variable.ValueChangedEvent;
import net.sourceforge.aprog.i18n.Translator;
import net.sourceforge.aprog.swing.LanguageComboBox;
import net.sourceforge.aprog.swing.SwingTools;
import net.sourceforge.jmacadapter.MacAdapterTools;
import net.sourceforge.jmacadapter.eawtwrappers.Application;

/**
 * This class defines all the Swing components needed to build the GUI.
 *
 * @author codistmonk (creation 2010-06-27)
 */
public final class SubtitlesAdjusterComponents {
    
    /**
     * Private default constructor to prevent instantiation.
     */
    private SubtitlesAdjusterComponents() {
        // Do nothing
    }

    /**
     * {@value}.
     */
    private static final int INSET = 8;

    /**
     *
     * @param context
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JDialog newPreferencesDialog(final Context context) {
		checkAWT();

        final JDialog result = translate(new JDialog((JFrame) context.get(MAIN_FRAME), "Preferences", true));

        result.add(newPreferencesPanel(context));

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
    public static final JPanel newPreferencesPanel(final Context context) {
		checkAWT();

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

            add(result, newLanguageComboBox(), constraints);
        }

        return result;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JComboBox newLanguageComboBox() {
		checkAWT();

        final JComboBox result = new LanguageComboBox(Translator.getDefaultTranslator());

        result.addActionListener(new ActionListener() {

            @Override
            public final void actionPerformed(final ActionEvent event) {
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
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JFrame newMainFrame(final Context context) {
		checkAWT();

        final JFrame result = new JFrame();

        context.set(MAIN_FRAME, result);

        result.setJMenuBar(newMenuBar(context));
        result.add(newMainPanel(context));

        result.addWindowListener(newListener(WindowListener.class, "windowClosing",
                SubtitlesAdjusterActions.class, "quit", context));

        invokeOnVariableChanged(context, FILE,
                SubtitlesAdjusterActions.class, "updateMainFrameTitle", context);
        invokeOnVariableChanged(context, FILE_MODIFIED,
                SubtitlesAdjusterActions.class, "updateMainFrameTitle", context);

        Translator.getDefaultTranslator().addListener(newListener(Translator.Listener.class, "localeChanged",
                SwingTools.class, "packAndUpdateMinimumSize", result));

        return center(packAndUpdateMinimumSize(result));
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
    public static final JMenuBar newMenuBar(final Context context) {
		checkAWT();

        if (MacAdapterTools.isMacOSX()) {
            MacAdapterTools.setUseScreenMenuBar(true);
        }

        return menuBar(
                menu("Application",
                    newAboutMenuItem(context),
                    null,
                    newPreferencesMenuItem(context),
                    null,
                    newQuitMenuItem(context)
                ),
                menu("File",
                        newOpenMenuItem(context),
                        null,
                        newSaveMenuItem(context)
                ),
                menu("Help",
                        newManualMenuItem(context)
                ));
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Maybe null
     * <br>New
     */
    public static final JMenuItem newAboutMenuItem(final Context context) {
		checkAWT();

        if (MacAdapterTools.isMacOSX() && MacAdapterTools.getUseScreenMenuBar()) {
            Application.getApplication().setEnabledAboutMenu(true);

            if (registerMacOSXApplicationListener("handleAbout",
                    SubtitlesAdjusterActions.class, "showAboutDialog", context)) {
                return null;
            }
        }

        return item("About",
                SubtitlesAdjusterActions.class, "showAboutDialog", context);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Maybe null
     * <br>New
     */
    public static final JMenuItem newPreferencesMenuItem(final Context context) {
		checkAWT();

        if (MacAdapterTools.isMacOSX() && MacAdapterTools.getUseScreenMenuBar()) {
            Application.getApplication().setEnabledPreferencesMenu(true);

            if (registerMacOSXApplicationListener("handlePreferences",
                    SubtitlesAdjusterActions.class, "showPreferencesDialog", context)) {
                return null;
            }
        }

        return item("Preferences...", getKeyStroke(META + " R"),
                SubtitlesAdjusterActions.class, "showPreferencesDialog", context);
    }

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Maybe null
     * <br>New
     */
    public static final JMenuItem newQuitMenuItem(final Context context) {
		checkAWT();

        if (MacAdapterTools.isMacOSX() && MacAdapterTools.getUseScreenMenuBar()) {
            if (registerMacOSXApplicationListener("handleQuit",
                    SubtitlesAdjusterActions.class, "quit", context)) {
                return null;
            }
        }

        return item("Quit", getKeyStroke(META + " Q"),
                SubtitlesAdjusterActions.class, "quit", context);
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
    public static final JMenuItem newOpenMenuItem(final Context context) {
		checkAWT();

        return item("Open...", getKeyStroke(META + " O"),
                SubtitlesAdjusterActions.class, "open", context);
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
    public static final JMenuItem newSaveMenuItem(final Context context) {
		checkAWT();

        final JMenuItem result = item("Save", getKeyStroke(META + " S"),
                SubtitlesAdjusterActions.class, "save", context);

        net.sourceforge.aprog.subtitlesadjuster.SubtitlesAdjusterComponents
                .synchronizeComponentEnabledWithFileVariableNullity(result, context);

        return result;
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
    public static final JMenuItem newManualMenuItem(final Context context) {
		checkAWT();

        return item("Manual", getKeyStroke("F1"),
                SubtitlesAdjusterActions.class, "showManual", context);
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
    public static final JPanel newMainPanel(final Context context) {
		checkAWT();

        final JPanel result = new JPanel();
        final GridBagConstraints constraints = new GridBagConstraints();

        {
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            constraints.weighty = 0.0;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.insets = new Insets(INSET, INSET, INSET, INSET);

            add(result, translate(new JLabel("First subtitle time")), constraints);
        }
        {
            ++constraints.gridx;
            constraints.weightx = 0.0;

            add(result, newTimeSpinner(context, FIRST_TIME), constraints);
        }
        {
            constraints.gridx = 0;
            ++constraints.gridy;
            constraints.weightx = 1.0;

            add(result, translate(new JLabel("Last subtitle time")), constraints);
        }
        {
            ++constraints.gridx;
            constraints.weightx = 0.0;

            add(result, newTimeSpinner(context, LAST_TIME), constraints);
        }
        {
            ++constraints.gridy;

            add(result, newSaveButton(context), constraints);
        }

        final Color defaultBackground = result.getBackground();

        new DropTarget(result, new DropTargetAdapter() {

            @Override
            public final void dragEnter(final DropTargetDragEvent event) {
                this.highlighBackground();
            }

            @Override
            public final void dragExit(final DropTargetEvent event) {
                this.resetBackground();
            }

            @Override
            public final void drop(final DropTargetDropEvent event) {
                this.resetBackground();

                final List<File> files = getFiles(event);

                if (files.size() == 1) {
                    ((Subtitles) context.get(SUBTITLES)).load(files.get(0));
                }
            }

            private final void highlighBackground() {
                result.setBackground(Color.GREEN);
            }

            private final void resetBackground() {
                result.setBackground(defaultBackground);
            }

        });

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
    public static final JButton newSaveButton(final Context context) {
		checkAWT();

        final JButton result = translate(new JButton(action(SubtitlesAdjusterActions.class, "save", context).setName("Save")));

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
    public static final JSpinner newTimeSpinner(final Context context, final String variableName) {
		checkAWT();

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
     * @param throwable
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final JPanel newErrorMessagePanel(final Throwable throwable) {
		checkAWT();

        final JPanel result = new JPanel(new BorderLayout());
        final JToggleButton detailsToggle = new JToggleButton(translate("Details"));
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        throwable.printStackTrace(new PrintStream(buffer));

        final JTextArea stackTrace = new JTextArea(buffer.toString());
        final JScrollPane stackTraceContainer = scrollable(stackTrace);

        result.add(new JLabel(throwable.getLocalizedMessage()), BorderLayout.CENTER);
        result.add(verticalBox(detailsToggle, stackTraceContainer), BorderLayout.SOUTH);

        stackTrace.setEditable(false);
        stackTraceContainer.setVisible(false);
        detailsToggle.addActionListener(new ActionListener() {

            @Override
            public final void actionPerformed(final ActionEvent event) {
                stackTraceContainer.setVisible(detailsToggle.isSelected());

                final Component root = SwingUtilities.getRoot(result);

                if (root instanceof Window) {
                    SwingTools.packAndUpdateMinimumSize((Window) root);
                }
            }

        });

        return result;
    }

	/**
	 *
	 * @param components
     * <br>Not null
	 * @return
     * <br>Not null
     * <br>New
	 */
	public static final Box verticalBox(final Component... components) {
		checkAWT();

		final Box verticalBox = Box.createVerticalBox();

		for (final Component component : components) {
			verticalBox.add(component);
		}

		return verticalBox;
	}

    /**
     *
     * @param component
     * <br>Not null
     * <br>Shared
     * @param context
     * <br>Not null
     */
    public static final void synchronizeComponentEnabledWithFileVariableNullity(
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

}
