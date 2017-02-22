package com.chrynan.glimpse;

import android.support.annotation.AnyRes;
import android.support.annotation.StyleableRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ckeenan on 2/5/17. An annotation to indicate that the annotated fields value will come
 * from a styleable attribute from a {@link android.content.res.TypedArray} using the provided
 * resource name and the default resource fallback.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Styleable {

    /**
     * The styleable resource id that the annotated field retrieves its value from.
     *
     * @return The resource id of the fields value.
     */
    @StyleableRes
    int value();

    /**
     * The resource id used to retrieve a default value if the styleable field wasn't specified.
     *
     * @return The resource id of the fields default value.
     */
    @AnyRes
    int defaultRes() default 0;
}
