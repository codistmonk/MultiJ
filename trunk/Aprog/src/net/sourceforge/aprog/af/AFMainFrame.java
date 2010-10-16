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

package net.sourceforge.aprog.af;

import static net.sourceforge.aprog.af.AFConstants.Variables.*;
import static net.sourceforge.aprog.af.AFTools.*;
import static net.sourceforge.aprog.swing.SwingTools.*;

import java.awt.Component;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import net.sourceforge.aprog.context.Context;
import net.sourceforge.aprog.tools.Tools;

/**
 *
 * @author codistmonk (creation 2010-09-16)
 */
public final class AFMainFrame extends JFrame {

    private final Context context;

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     */
    private AFMainFrame(final Context context) {
        super(context.get(APPLICATION_NAME).toString());
        this.context = context;

        try {
            this.setIconImage(ImageIO.read(Tools.getResourceAsStream(context.get(APPLICATION_ICON_PATH).toString())));
        } catch (final IOException exception) {
            Tools.getLoggerForThisMethod().log(Level.WARNING, null, exception);
        }
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

    private static final long serialVersionUID = -3668377952583428013L;

    /**
     *
     * @param context
     * <br>Not null
     * <br>Shared
     * @param actionClass
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final AFMainFrame newMainFrame(final Context context) {
        checkAWT();

        final AFMainFrame result = new AFMainFrame(context);

        context.set(MAIN_FRAME, result);

        result.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        result.addWindowListener(newListener(WindowListener.class, "windowClosing",
                AFTools.class, "perform", context, ACTIONS_QUIT));
        
        result.setJMenuBar((JMenuBar) context.get(MAIN_MENU_BAR));

        result.add((Component) context.get(MAIN_PANEL));

        return packAndCenter(result);
    }

}
