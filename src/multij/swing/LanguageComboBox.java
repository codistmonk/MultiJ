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

package multij.swing;

import java.util.Locale;

import javax.swing.JComboBox;

import multij.i18n.Translator;

/**
 *
 * @author codistmonk (creation 2010-07-01)
 */
public final class LanguageComboBox extends JComboBox {

    private final Translator translator;

    /**
     *
     * @param translator
     * <br>Not null
     * <br>Shared
     */
    public LanguageComboBox(final Translator translator) {
        super(translator.getAvailableLocales());
        this.translator = translator;

        this.setSelectedItem(this.getTranslator().getBestAvailableLocale());
        this.addActionListener(SwingTools.action(this, "updateTranslator"));
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    public final Translator getTranslator() {
        return this.translator;
    }

    public final void updateTranslator() {
        this.getTranslator().setLocale((Locale) this.getSelectedItem());
    }

    private static final long serialVersionUID = 182549930460210274L;

}