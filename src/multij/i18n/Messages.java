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

package multij.i18n;

import static java.util.Collections.unmodifiableSet;
import static multij.i18n.Translator.*;
import static multij.tools.Tools.*;

import java.util.HashSet;
import java.util.Set;

import multij.tools.IllegalInstantiationException;
import multij.tools.Tools;

/**
 * This class contains static methods that help manipulate the default translator.
 * <br>It is recommended to use a static import so that the only name to remember is translate.
 * <br>Static getters and setters are provided to customize the location of the message bundle.
 * <br>If you need to alternate between different message bases or property sets, you can define your
 * own utility class with appropriate static "translate" methods,
 * so that the messagesBase and propertyNames attributes can be precisely controlled.
 *
 * @author codistmonk (creation 2010-06-25)
 */
public final class Messages {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private Messages() {
        throw new IllegalInstantiationException();
    }

    /**
     * {@value}.
     */
    public static final String DEFAULT_MESSAGES_BASE = "i18n/Messages";

    /**
     * {{@code "text"}, {@code "title"}, {@code "toolTipText"}, {@code "string"}}.
     */
    public static final Set<String> DEFAULT_PROPERTY_NAMES =
            unmodifiableSet(Tools.set("text", "title", "toolTipText", "string"));

    private static String messagesBase = DEFAULT_MESSAGES_BASE;

    private static final Set<String> propertyNames = new HashSet<String>(DEFAULT_PROPERTY_NAMES);

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    public static final String getMessagesBase() {
        return messagesBase;
    }

    /**
     *
     * @param messagesBase
     * <br>Not null
     * <br>Shared
     */
    public static final void setMessagesBase(final String messagesBase) {
        Messages.messagesBase = messagesBase;
    }

    /**
     *
     * @return A mutable set
     * <br>Not null
     * <br>Shared
     */
    public static final Set<String> getPropertyNames() {
        return propertyNames;
    }

    /**
     * This method registers {@code object} in the default translator and translates it
     * using the specified translation key and optional parameters.
     * <br>The message bundle is the one associated with the caller class.
     *
     * @param <T> The actual type of {@code object}
     * @param object The object whose properties need to be translated
     * <br>Not null
     * <br>Input-output
     * <br>Shared
     * @param textPropertyName The lowerCamelCase name of the property to translate;
     * a setter named "set" + UpperCamelCase name is expected
     * <br>Not null
     * <br>Shared
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param parameters Optional parameters to build the translated message;
     * if an exception is passed, its localized message will be used
     * <br>Not null
     * <br>Shared
     * @return {@code object}
     * <br>Not null
     * <br>Shared
     */
    public static final <T> T translate(
            final T object, final String textPropertyName, final String translationKey, final Object... parameters) {
        return getDefaultTranslator().translate(
                object, textPropertyName, translationKey, getMessagesBase(), parameters);
    }

    /**
     * This method tries to translate {@code component}'s properties found in {@link #getPropertyNames()}.
     * <br>If the property doesn't exist or is not accessible with a getter and setter, nothing happens.
     * <br>The translation key for each property is the value of the property before the call.
     * <br>The message bundle is the one retrieved from {@link #getMessagesBase()}.
     *
     * @param <T> The actual type of {@code component}
     * @param component
     * <br>Not null
     * <br>Input-output
     * <br>Shared
     * @param parameters
     * <br>Not null
     * <br>Shared
     * @return {@code component}
     * <br>Not null
     * <br>Shared
     */
    public static final <T> T translate(final T component, final Object... parameters) {
        for (final String textPropertyName : getPropertyNames()) {
            try {
                final String translationKey = (String) getGetter(component, textPropertyName).invoke(component);

                if (translationKey != null && !translationKey.isEmpty()) {
                    getDefaultTranslator().translate(
                            component, textPropertyName, translationKey, getMessagesBase(), parameters);
                }
            } catch (final Exception exception) {
                // Do nothing
            }
        }

        return component;
    }

    /**
     * This method tries to translate {@code translationKey} with the specified parameter using
     * the message bundle obtained from {@link #getMessagesBase()}.
     *
     * @param translationKey
     * <br>Not null
     * @param parameters
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     */
    public static final String translate(final String translationKey, final Object... parameters) {
        return getDefaultTranslator().translate(translationKey, getMessagesBase(), parameters);
    }

    /**
     *
     * @param translationKey
     * <br>Not null
     * <br>Shared
     * @param messageParameters
     * <br>Not null
     * <br>Shared
     * @return
     * <br>Not null
     * <br>New
     */
    public static final LocalizedException newLocalizedException(
            final String translationKey, final Object... messageParameters) {
        return new LocalizedException(Translator.getDefaultTranslator(), translationKey, getMessagesBase(), messageParameters);
    }

}
