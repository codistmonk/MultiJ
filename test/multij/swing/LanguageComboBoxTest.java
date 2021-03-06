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

import java.util.Arrays;

import static org.junit.Assert.*;

import java.util.Locale;

import multij.i18n.Translator;
import multij.i18n.TranslatorTest;
import multij.swing.LanguageComboBox;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link LanguageComboBox}.
 *
 * @author codistmonk (creation 2010-07-01)
 */
public final class LanguageComboBoxTest {

    @Test
    public final void testConstructorAndUpdateTranslator() {
        final Translator translator = new Translator();

        translator.collectAvailableLocales(TranslatorTest.MESSAGES_BASE);
        translator.setLocale(Locale.FRENCH);

        assertTrue(Arrays.asList(translator.getAvailableLocales()).contains(Locale.ENGLISH));
        assertTrue(Arrays.asList(translator.getAvailableLocales()).contains(Locale.FRENCH));
        assertEquals(Locale.FRENCH, translator.getLocale());
        assertEquals(Locale.FRENCH, translator.getBestAvailableLocale());

        final LanguageComboBox languageComboBox = new LanguageComboBox(translator);

        assertEquals(translator.getAvailableLocales().length, languageComboBox.getItemCount());
        assertEquals(translator.getBestAvailableLocale(), languageComboBox.getSelectedItem());

        languageComboBox.setSelectedItem(Locale.ENGLISH);

        assertEquals(Locale.ENGLISH, translator.getLocale());
    }

}