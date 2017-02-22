package com.chrynan.glimpse;

import com.squareup.javapoet.TypeName;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

/**
 * Created by ckeenan on 2/7/17.
 * Represents a field annotated with {@link Styleable}.
 */
class StyleableField {

    static final String VALUE_FIELD = "value";
    static final String DEFAULT_RES_FIELD = "defaultRes";

    private static final String COLOR_INT_CLASS_NAME = "ColorInt";
    private static final String DIMENSION_CLASS_NAME = "Dimension";
    private static final String DIMENSION_UNIT_FIELD_NAME = "unit";

    private final String fieldName;
    private final TypeName typeName;
    private final RClassReference styleableReference;
    private final RClassReference defaultReference;
    private final boolean hasDefaultValue;
    private final String styleableGroupName;

    private boolean colorInt;
    private boolean dimension;
    private DimensionUnit dimensionUnit;

    StyleableField(final Element element, final StyleableAnnotationValues annotationValues) {
        if (element == null || element.getAnnotation(Styleable.class) == null || element.getKind() != ElementKind.FIELD || element.getModifiers().contains(Modifier.PRIVATE)) {
            throw new IllegalArgumentException("Element parameter in StyleableField constructor must represent a non-private field annotated with the Styleable annotation");
        }

        final Styleable styleable = element.getAnnotation(Styleable.class);

        this.fieldName = element.getSimpleName().toString();
        this.typeName = TypeName.get(element.asType());
        this.styleableReference = annotationValues.getStyleableValue();
        this.defaultReference = annotationValues.getDefaultValue();
        this.hasDefaultValue = annotationValues.getDefaultValue() != null;
        this.styleableGroupName = annotationValues.getStyleableValue().getRClassName().packageName() + "." + annotationValues.getStyleableValue().getGroupName();

        for (final AnnotationMirror mirror : element.getAnnotationMirrors()) {
            String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();

            if (annotationName.equals(COLOR_INT_CLASS_NAME)) {
                colorInt = true;
            } else if (annotationName.equals(DIMENSION_CLASS_NAME)) {
                dimension = true;

                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().contentEquals(DIMENSION_UNIT_FIELD_NAME)) {
                        int value = (int) entry.getValue().getValue();

                        if (value == 0) {
                            dimensionUnit = DimensionUnit.DP;
                        } else if (value == 1) {
                            dimensionUnit = DimensionUnit.PX;
                        } else if (value == 2) {
                            dimensionUnit = DimensionUnit.SP;
                        }
                    }
                }
            }
        }
    }

    String getName() {
        return fieldName;
    }

    TypeName getTypeName() {
        return typeName;
    }

    String getStyleableValue() {
        return styleableReference.getRClassName().packageName() + "." + styleableReference.getFullName();
    }

    String getDefaultValue() {
        return defaultReference != null ? defaultReference.getRClassName().packageName() + "." + defaultReference.getFullName() : null;
    }

    boolean hasDefaultValue() {
        return hasDefaultValue;
    }

    String getStyleableGroupName() {
        return styleableGroupName;
    }

    boolean isColorInt() {
        return colorInt;
    }

    boolean isDimension() {
        return dimension;
    }

    DimensionUnit getDimensionUnit() {
        return dimensionUnit;
    }

    enum DimensionUnit {
        DP,
        PX,
        SP
    }
}
