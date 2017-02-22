package com.chrynan.glimpse;

import com.squareup.javapoet.ClassName;

/**
 * Created by ckeenan on 2/14/17. A model representation of an Android R class reference
 * (ex: R.styleable.someview).
 */
class RClassReference {

    private final ClassName rClassName;
    private final String name;
    private final String groupName;

    RClassReference(final ClassName rClassName, final String name, final String groupName) {
        this.rClassName = rClassName;
        this.name = name;
        this.groupName = groupName;
    }

    /**
     * Retrieves the actual R {@link ClassName} representation (ex: com.myapp.R).
     *
     * @return The {@link ClassName} representation of the R class.
     */
    ClassName getRClassName() {
        return rClassName;
    }

    /**
     * Retrieves the full field name (ex: R.styleable.MyCustomView_someField).
     *
     * @return The full String field name
     */
    String getFullName() {
        return name;
    }

    /**
     * Retrieves the group name of the field (ex: R.styleable.MyCustomView). In some cases this may
     * be the same value as returned by the getFullName() method (ex: getFullName() ==
     * R.anim.someAnimation, getGroupName() == R.anim.someAnimation).
     *
     * @return The String representation of the group field.
     */
    String getGroupName() {
        return groupName;
    }
}
