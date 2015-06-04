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

package multij.af;

import static multij.af.AFConstants.Variables.*;
import static multij.i18n.Messages.*;
import static multij.swing.SwingTools.*;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import multij.context.Context;
import multij.i18n.Translator;
import multij.swing.LanguageComboBox;
import multij.swing.SwingTools;
import multij.tools.Tools;

/**
 *
 * @author codistmonk (creation 2010-09-17)
 */
public final class AFPreferencesDialog extends JDialog {

    private final Context context;

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     */
    public AFPreferencesDialog(final Context context) {
        super((JFrame) context.get(MAIN_FRAME), "Preferences", true);
        this.context = context;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    public final Context getContext() {
        return this.context;
    }

    private static final long serialVersionUID = -6665475944273083387L;

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
    public static final AFPreferencesDialog newPreferencesDialog(final Context context) {
		checkAWT();

        final AFPreferencesDialog result = translate(new AFPreferencesDialog(context));

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

		Tools.ignore(context);

        final JPanel result = new JPanel();
        final GridBagConstraints constraints = new GridBagConstraints();

        {
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            constraints.weighty = 0.0;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.insets = new Insets(INSET, INSET, INSET, INSET);

            SwingTools.add(result, translate(new JLabel("Language")), constraints);
        }
        {
            ++constraints.gridx;
            constraints.weightx = 0.0;

            SwingTools.add(result, newLanguageComboBox(), constraints);
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

}
