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

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import net.sourceforge.aprog.tools.Tools;

import org.junit.Test;

/**
 * Automated tests using JUnit 4 for {@link Translator}.
 *
 * @author codistmonk (creation 2010-06-03)
 */
public final class TranslatorTest {

    @Test
    public final void testTranslateObject() {
        final String translationKey1 = "public_key";
        final String textPropertyName1 = "publicText";
        final String translationKey2 = "package_key";
        final String textPropertyName2 = "packageText";
        final Translator translator = new Translator();

        translator.setLocale(Locale.ENGLISH);

        final Translatable translatable = translator.translate(
                new Translatable(),
                textPropertyName1,
                translationKey1,
                MESSAGES_BASE);

        assertEquals("", translatable.getPackageText());

        translator.translate(translatable, textPropertyName2, translationKey2, MESSAGES_BASE);

        assertEquals("Public key", translatable.getPublicText());
        assertEquals("Package key", translatable.getPackageText());

        translator.setLocale(Locale.FRENCH);

        assertEquals("Clé publique", translatable.getPublicText());
        assertEquals("Clé package", translatable.getPackageText());

        translator.setLocale(Locale.ENGLISH);

        assertEquals("Public key", translatable.getPublicText());
        assertEquals("Package key", translatable.getPackageText());
    }

    @Test
    public final void testTranslateObjectWithParameterizedMessage() {
        final String translationKey = "life_universe_everything";
        final String textPropertyName = "parameterizedText";
        final Translator translator = new Translator();

        translator.setLocale(Locale.ENGLISH);

        final Translatable translatable = translator.translate(
                new Translatable(),
                textPropertyName,
                translationKey,
                MESSAGES_BASE,
                42);

        assertEquals("Answer: 42", translatable.getParameterizedText());

        translator.setLocale(Locale.FRENCH);

        assertEquals("Réponse : 42", translatable.getParameterizedText());

        translator.setLocale(Locale.ENGLISH);

        assertEquals("Answer: 42", translatable.getParameterizedText());
    }

    @Test
    public final void testUntranslateObject() {
        final String translationKey1 = "public_key";
        final String textPropertyName1 = "publicText";
        final String translationKey2 = "package_key";
        final String textPropertyName2 = "packageText";
        final Translator translator = new Translator();

        translator.setLocale(Locale.ENGLISH);

        final Translatable translatable = translator.translate(
                new Translatable(),
                textPropertyName1,
                translationKey1,
                MESSAGES_BASE);

        assertEquals("", translatable.getPackageText());

        translator.translate(translatable, textPropertyName2, translationKey2, MESSAGES_BASE);

        assertEquals("Public key", translatable.getPublicText());
        assertEquals("Package key", translatable.getPackageText());

        translator.setLocale(Locale.FRENCH);

        assertEquals("Clé publique", translatable.getPublicText());
        assertEquals("Clé package", translatable.getPackageText());

        translator.untranslate(translatable, textPropertyName1);
        translator.setLocale(Locale.ENGLISH);

        assertEquals(translationKey1, translatable.getPublicText());
        assertEquals("Package key", translatable.getPackageText());

    }

    @Test
    public final void testTranslateMessage() {
        final String translationKey = "life_universe_everything";
        final Translator translator = new Translator();

        {
            translator.setLocale(Locale.ENGLISH);

            final String result = translator.translate(translationKey, MESSAGES_BASE, 42);

            assertNotNull(result);
            assertEquals("Answer: 42", result);
        }
        {
            translator.setLocale(Locale.FRENCH);

            final String result = translator.translate(translationKey, MESSAGES_BASE, 42);

            assertNotNull(result);
            assertEquals("Réponse : 42", result);
        }
    }

    @Test
    public final void testGetBestAvailableLocale() {
        final Translator translator = new Translator();

        translator.collectAvailableLocales(MESSAGES_BASE);

        translator.setLocale(new Locale("fr", "FR"));

        assertEquals(new Locale("fr"), translator.getBestAvailableLocale());

        translator.setLocale(new Locale("en", "US"));

        assertEquals(new Locale("en"), translator.getBestAvailableLocale());
    }

    @Test
    public final void testCreateLocale() {
        {
            final Locale result = Translator.createLocale("fr");

            assertNotNull(result);
            assertEquals(Locale.FRENCH, result);
        }
        {
            final Locale result = Translator.createLocale("fr_CA");

            assertNotNull(result);
            assertEquals(Locale.CANADA_FRENCH, result);
        }
        {
            final Locale result = Translator.createLocale("fr_FR_parisien");

            assertNotNull(result);
            assertEquals(new Locale("fr", "FR", "parisien"), result);
        }
    }

    @Test
    public final void testGetLanguageCountryVariant() {
        final String language = "fr";
        final String country = "FR";
        final String variant = "parisien";

        {
            final String result = Translator.getLanguageCountryVariant(new Locale(language));

            assertNotNull(result);
            assertEquals(language, result);

        }
        {
            final String result = Translator.getLanguageCountryVariant(new Locale(language, country));

            assertNotNull(result);
            assertEquals(language + "_" + country, result);

        }
        {
            final String result = Translator.getLanguageCountryVariant(new Locale(language, country, variant));

            assertNotNull(result);
            assertEquals(language + "_" + country + "_" + variant, result);

        }
    }

    @Test
    public final void testIso88591ToUTF8() throws UnsupportedEncodingException {
        {
            final String iso88591 = new String(new byte[] { (byte) 0xCE, (byte) 0xA9 }, "ISO-8859-1");
            final String convertedString = Translator.iso88591ToUTF8(iso88591);
            final String expectedResult = "Ω";

            assertNotNull(convertedString);
            assertNotSame(convertedString, iso88591);
            assertEquals("Î©", iso88591);
            assertFalse(expectedResult.equals(iso88591));
            assertEquals(expectedResult, convertedString);
        }
        {
            final String iso88591 = new String(new byte[] { (byte) 0xC3, (byte) 0xA9, (byte) 0xE2, (byte) 0x80, (byte) 0x99 }, "ISO-8859-1");
            final String convertedString = Translator.iso88591ToUTF8(iso88591);
            final String expectedResult = "é’";

            assertNotNull(convertedString);
            assertNotSame(convertedString, iso88591);
            assertFalse(expectedResult.equals(iso88591));
            assertEquals(expectedResult, convertedString);
        }
    }

    public static final String MESSAGES_BASE = Tools.getCallerPackagePath() + "Messages";

    /**
     *
     * @author codistmonk (creation 2010-06-03)
     */
    private static final class Translatable {

        private String publicText;

        private String parameterizedText;

        private String packageText;

        Translatable() {
            this.publicText = "";
            this.packageText = "";
            this.parameterizedText = "";
        }

        /**
         *
         * @return
         * <br>A non-null value
         * <br>A shared value
         */
        public final String getPublicText() {
            return this.publicText;
        }

        /**
         *
         * @param publicText
         * <br>Should not be null
         * <br>Shared parameter
         */
        @SuppressWarnings("unused")
        public final void setPublicText(final String publicText) {
            this.publicText = publicText;
        }

        /**
         *
         * @return
         * <br>A non-null value
         * <br>A shared value
         */
        public final String getParameterizedText() {
            return this.parameterizedText;
        }

        /**
         *
         * @param parameterizedText
         * <br>Should not be null
         * <br>Shared parameter
         */
        @SuppressWarnings("unused")
        public final void setParameterizedText(final String parameterizedText) {
            this.parameterizedText = parameterizedText;
        }

        /**
         *
         * @return
         * <br>A non-null value
         * <br>A shared value
         */
        final String getPackageText() {
            return this.packageText;
        }

        /**
         *
         * @param packageText
         * <br>Should not be null
         * <br>Shared parameter
         */
        @SuppressWarnings("unused")
        final void setPackageText(final String packageText) {
            this.packageText = packageText;
        }

    }

}
