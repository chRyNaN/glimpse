package com.chrynan.glimpse;

/**
 * Created by ckeenan on 2/14/17. A model representation of all the field values for a particular
 * {@link Styleable} annotation.
 */
class StyleableAnnotationValues {

    private final RClassReference styleableValue;
    private final RClassReference defaultValue;

    StyleableAnnotationValues(RClassReference styleableValue, RClassReference defaultValue) {
        this.styleableValue = styleableValue;
        this.defaultValue = defaultValue;
    }

    RClassReference getStyleableValue() {
        return styleableValue;
    }

    RClassReference getDefaultValue() {
        return defaultValue;
    }
}
