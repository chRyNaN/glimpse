package com.chrynan.glimpse;

import javax.lang.model.element.TypeElement;

/**
 * Created by ckeenan on 2/5/17.
 * A collection of String utilities.
 */
class StringUtils {

    private StringUtils() {
        // Private constructor to prevent initialization of this object.
    }

    /**
     * Determines whether the provided String is null or has a length of zero. If the provided
     * String is null or has a length of zero, it returns true, otherwise false.
     *
     * @param stringToTest The String to determine if is empty.
     * @return True if the provided String is empty, false otherwise.
     */
    static boolean isEmpty(final String stringToTest) {
        return stringToTest == null || stringToTest.length() == 0;
    }

    /**
     * Retrieves an altered version of the provided String that is headless camel cased to the best
     * of its ability. The first character in the String will be lower cased. All other characters
     * are assumed to be properly cased. The only exception is an underscore character. An
     * underscore character will be removed and the following character will be capitalized.
     * Note that this method can return null.
     *
     * @param stringToCamelCase The String to camel case.
     * @return A headless camel cased version of the provided String without underscores.
     */
    static String getNormalizedCamelCasedName(final String stringToCamelCase) {
        if (!isEmpty(stringToCamelCase)) {
            final StringBuilder sb = new StringBuilder();

            boolean capitalize = false;

            for (int i = 0; i < stringToCamelCase.length(); i++) {
                if (i == 0) {
                    sb.append(Character.toLowerCase(stringToCamelCase.charAt(i)));
                } else if (capitalize) {
                    sb.append(Character.toUpperCase(stringToCamelCase.charAt(i)));
                    capitalize = false;
                } else if ('_' == stringToCamelCase.charAt(i)) {
                    capitalize = true;
                } else {
                    sb.append(stringToCamelCase.charAt(i));
                }
            }

            return sb.toString();
        }

        return null;
    }

    /**
     * Retrieves the name of the parent for the provided String styleable name. The parent is
     * described as the first part of the String until the first underscore is reached. Note that
     * this method can return null.
     *
     * @param fullStyleableString The full name of the styleable resource.
     * @return The parent name of the provided resource.
     */
    static String getStyleableParentName(final String fullStyleableString) {
        if (!isEmpty(fullStyleableString)) {
            int index = fullStyleableString.indexOf('_');

            if (index != -1) {
                return fullStyleableString.substring(0, index);
            }
        }

        return null;
    }

    /**
     * Retrieves the package name of the provided TypeElement.
     *
     * @param typeElement The {@link TypeElement} object to get the package name of.
     * @return The package name. Note that this method can return null.
     */
    static String getPackageName(final TypeElement typeElement) {
        if (typeElement != null && !isEmpty(typeElement.getQualifiedName().toString())) {
            final String s = typeElement.getQualifiedName().toString();

            return s.substring(0, s.lastIndexOf('.'));
        }

        return null;
    }
}
