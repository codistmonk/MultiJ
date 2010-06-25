/*
 *  The MIT License
 * 
 *  Copyright 2010 greg.
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

import static net.sourceforge.aprog.i18n.Translator.*;
import static net.sourceforge.aprog.tools.Tools.*;

/**
 * This class contains static methods that help manipulate the default translator.
 * <br>It is recommended to use a static import so that the only name to remember is translate.
 *
 * @author codistmonk (creation 2010-06-25)
 */
public final class Messages {

    /**
     * Private default constructor to prevent instantiation.
     */
    private Messages() {
        // Do nothing
    }

    /**
     * This method registers {@code object} in the default translator and translates it using the specified translation key and optional parameters.
     * <br>The messages bundle is the one associated with the caller class.
     *
     * @param <T> the actual type of {@code object}
     * @param object the object whose properties need to be translated
     * <br>Should not be null
     * <br>Input-output parameter
     * <br>Shared parameter
     * @param textPropertyName the lowerCamelCase name of the property to translate; a setter named "set" + UpperCamelCase name is expected
     * <br>Should not be null
     * <br>Shared parameter
     * @param translationKey
     * <br>Should not be null
     * <br>Shared parameter
     * @param parameters optional parameters to build the translated message; if an exception is passed, its localized message will be used
     * <br>Should not be null
     * <br>Shared parameter
     * @return {@code object}
     * <br>A non-null value
     * <br>A shared value
     */
    public static final <T> T translate(final T object, final String textPropertyName, final String translationKey, final Object... parameters) {
        return getDefaultTranslator().translate(object, textPropertyName, translationKey, makeResourceBundleBaseName(getCallerClass()), parameters);
    }

    /**
     * This method tries to translate {@code component}'s properties named "text", "title" and "toolTipText".
     * <br>If the property doesn't exist or is not accessible with public getter and setter, nothing happens.
     * <br>The translation key for each property is the value of the property before the call.
     * <br>Warning: {@code parameters} will be used for all 3 properties if they are accessible.
     * <br>The messages bundle is the one associated with the caller class.
     *
     * @param <T> the actual type of {@code component}
     * @param component
     * <br>Should not be null
     * <br>Input-output parameter
     * <br>Shared parameter
     * @param parameters
     * <br>Should not be null
     * <br>Shared parameter
     * @return {@code component}
     * <br>A non-null value
     * <br>A shared value
     */
    public static final <T> T translate(final T component, final Object... parameters) {
        for (final String textPropertyName : array("text", "title", "toolTipText")) {
            try {
                final String translationKey = (String) getGetter(component, textPropertyName).invoke(component);

                if (translationKey != null && !translationKey.isEmpty()) {
                    getDefaultTranslator().translate(component, textPropertyName, translationKey, makeResourceBundleBaseName(getCallerClass()), parameters);
                }
            } catch (final Exception exception) {
                // Do nothing
            }
        }

        return component;
    }

    /**
     * This method tries to translate {@code translationKey} with the specified parameter using the caller's class
     * to obtain a resource bundle.
     *
     * @param translationKey
     * <br>Should not be null
     * @param parameters
     * <br>Should not be null
     * <br>Shared parameter
     * @return
     * <br>A non-null value
     */
    public static final String translate(final String translationKey, final Object... parameters) {
        return getDefaultTranslator().translate(translationKey, makeResourceBundleBaseName(getCallerClass()), parameters);
    }

    /**
     *
     * @param cls
     * <br>Should not be null
     * @return the top level class enclosing {@code cls}, or {@code cls} itself if it is a top level class
     * <br>A non-null value
     */
    private static final Class<?> getTopLevelEnclosingClass(final Class<?> cls) {
        return cls.getEnclosingClass() == null ? cls : getTopLevelEnclosingClass(cls.getEnclosingClass());
    }

    /**
     *
     *
     * @param callerClass
     * <br />The class {@code translate} was called from
     * <br />Should not be null
     * @return the correct ResourceBundle base name for the calling class
     */
    private static final String makeResourceBundleBaseName(final Class<?> callerClass) {
        return "l10n/" + getTopLevelEnclosingClass(callerClass).getName().substring("net.sourceforge.transfile.".length()).replace(".", "/");
    }

}
