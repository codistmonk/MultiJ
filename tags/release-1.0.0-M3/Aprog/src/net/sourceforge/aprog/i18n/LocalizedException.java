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

import java.util.Locale;

/**
 *
 * @author codistmonk (creation 2010-06-26)
 */
public final class LocalizedException extends RuntimeException {

    private final Translator translator;

    private final String messagesBase;

    private final Object[] messageParameters;

    /**
     *
     * @param cause
     * <br>Maybe null
     * <br>Shared
     * @param translator
     * <br>Not null
     * <br>Shared
     * @param messagesBase
     * <br>Not null
     * <br>Shared
     * @param messageParameters
     * <br>Not null
     * <br>Shared
     */
    public LocalizedException(
            final Throwable cause,
            final Translator translator,
            final String messagesBase,
            final Object[] messageParameters) {
        this(cause, translator, getTranslationKey(cause), messagesBase, messageParameters);
    }

    /**
     *
     * @param cause
     * <br>Maybe null
     * <br>Shared
     * @param translator
     * <br>Not null
     * <br>Shared
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param messagesBase
     * <br>Not null
     * <br>Shared
     * @param messageParameters
     * <br>Not null
     * <br>Shared
     */
    public LocalizedException(
            final Throwable cause,
            final Translator translator,
            final String translationKey,
            final String messagesBase,
            final Object[] messageParameters) {
        super(translationKey, cause);
        this.translator = translator;
        this.messagesBase = messagesBase;
        this.messageParameters = messageParameters;
    }

    /**
     *
     * @param translator
     * <br>Not null
     * <br>Shared
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param messagesBase
     * <br>Not null
     * <br>Shared
     * @param messageParameters
     * <br>Not null
     * <br>Shared
     */
    public LocalizedException(
            final Translator translator,
            final String translationKey,
            final String messagesBase,
            final Object[] messageParameters) {
        super(translationKey);
        this.translator = translator;
        this.messagesBase = messagesBase;
        this.messageParameters = messageParameters;
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

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    public final String getMessagesBase() {
        return this.messagesBase;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    public final Object[] getMessageParameters() {
        return this.messageParameters;
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    public final String getTranslationKey() {
        return super.getMessage();
    }

    @Override
    public final String getLocalizedMessage() {
        return this.getTranslator().translate(
                this.getTranslationKey(), this.getMessagesBase(), this.getMessageParameters());
    }

    @Override
    public final String getMessage() {
        return Translator.translate(
                Locale.ENGLISH, this.getTranslationKey(), this.getMessagesBase(), this.getMessageParameters());
    }

    private static final long serialVersionUID = 6532213135316797933L;

    /**
     *
     * @param cause
     * <br>Not null
     * @return
     * <br>Not null
     * <br>Maybe new
     */
    private static final String getTranslationKey(final Throwable cause) {
        if (cause instanceof LocalizedException) {
            return ((LocalizedException) cause).getTranslationKey();
        }

        if (cause == null || cause.getMessage() == null || cause.getMessage().length() == 0) {
            return cause.getClass().getName();
        }

        return  cause.getMessage();
    }

}
