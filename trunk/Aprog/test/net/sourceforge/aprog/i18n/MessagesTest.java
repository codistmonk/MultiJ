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

package net.sourceforge.aprog.i18n;

import static net.sourceforge.aprog.i18n.Messages.*;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

/**
 *
 * @author codistmonk (creation 2010-06-26)
 */
public final class MessagesTest {

    @Test
    public final void testTranslate() {
        final Translator translator = Translator.getDefaultTranslator();

        translator.setLocale(Locale.ENGLISH);
        setMessagesBase(TranslatorTest.MESSAGES_BASE);

        {
            assertEquals("Answer: 42", translate("life_universe_everything", 42));
        }
        {
            final Translatable translatable = translate(new Translatable(), "text", "public_key");

            assertEquals("Public key", translatable.getText());

            translator.setLocale(Locale.FRENCH);

            assertEquals("Clé publique", translatable.getText());

            translator.untranslate(translatable, "text");

            assertEquals("public_key", translatable.getText());

            translator.setLocale(Locale.ENGLISH);

            assertEquals("public_key", translatable.getText());
        }
        {
            final Translatable translatable = Messages.translate(
                    new Translatable("life_universe_everything"), createLocalizedException("What is the question?"));

            assertEquals("Answer: What is the question?", translatable.getText());

            translator.setLocale(Locale.FRENCH);

            assertEquals("Réponse : Quelle est la question ?", translatable.getText());
        }
    }

    /**
     *
     * @author codistmonk (creation 2010-06-26)
     */
    private static final class Translatable {

        private String text;

        Translatable() {
            this(null);
        }

        /**
         * 
         * @param text
         * <br>Maybe null
         * <br>Shared
         */
        Translatable(final String text) {
            this.text = text;
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public String getText() {
            return this.text;
        }

        /**
         *
         * @param text
         * <br>Maybe null
         * <br>Shared
         */
        public final void setText(final String text) {
            this.text = text;
        }

    }

}