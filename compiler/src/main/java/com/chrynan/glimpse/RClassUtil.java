package com.chrynan.glimpse;

import com.squareup.javapoet.ClassName;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;

import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by ckeenan on 2/14/17. A Utility class for android "R" classes. Use the
 * {@link #getStyleableAnnotationValues(Trees, Elements, Types, Messager, Element)} method to obtain
 * a {@link StyleableAnnotationValues} representing the exact values used in the {@link Styleable}
 * annotation.
 */
class RClassUtil {

    private RClassUtil() {
        // Private constructor to prevent initialization of this object.
    }

    static StyleableAnnotationValues getStyleableAnnotationValues(final Trees trees, final Elements elementUtils, final Types typeUtils, final Messager messager, final Element annotatedElement) {
        RClassReference styleableReference = null;
        RClassReference defaultValueReference = null;
        AnnotationMirror annotationMirror = null;

        for (final AnnotationMirror mirror : annotatedElement.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(Styleable.class.getCanonicalName())) {
                annotationMirror = mirror;
                break;
            }
        }

        AnnotationValue value = null;
        AnnotationValue defaultValue = null;

        //noinspection ConstantConditions
        for (final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals("value")) {
                value = entry.getValue();
            } else if (entry.getKey().getSimpleName().toString().equals("defaultRes")) {
                defaultValue = entry.getValue();
            }
        }

        styleableReference = getRClassReference(trees, elementUtils, typeUtils, messager, annotatedElement, annotationMirror, value, StyleableField.VALUE_FIELD);

        if (defaultValue != null) {
            defaultValueReference = getRClassReference(trees, elementUtils, typeUtils, messager, annotatedElement, annotationMirror, defaultValue, StyleableField.DEFAULT_RES_FIELD);
        }

        return new StyleableAnnotationValues(styleableReference, defaultValueReference);
    }

    private static RClassReference getRClassReference(Trees trees, Elements elementUtils, Types typeUtils, Messager messager, Element annotatedElement, AnnotationMirror annotationMirror, AnnotationValue annotationValue, String annotationFieldName) {
        JCTree tree = (JCTree) trees.getTree(annotatedElement, annotationMirror, annotationValue);

        if (tree != null) {
            RClassFinder finder = new RClassFinder();

            tree.accept(finder);

            String rClassName = finder.getClassName();

            TypeElement typeElement;

            try {
                typeElement = elementUtils.getTypeElement(rClassName);
            } catch (MirroredTypeException mte) {
                typeElement = (TypeElement) typeUtils.asElement(mte.getTypeMirror());
            }

            TreePath path = trees.getPath(annotatedElement, annotationMirror, annotationValue);
            AnnotationFieldRClassReferenceVisitor annotationVisitor = new AnnotationFieldRClassReferenceVisitor(annotationFieldName);
            String rFieldResourceName = annotationVisitor.scan(path, null);

            ClassName className = ClassName.get(typeElement);

            messager.printMessage(Diagnostic.Kind.NOTE, "fieldName = " + rFieldResourceName);
            messager.printMessage(Diagnostic.Kind.NOTE, "groupName = " + StringUtils.getStyleableParentName(rFieldResourceName));

            return new RClassReference(className, rFieldResourceName, StringUtils.getStyleableParentName(rFieldResourceName));
        }

        return null;
    }
}
