package com.chrynan.glimpse;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.JavaFile;
import com.sun.source.util.Trees;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * An annotation processor that looks for {@link Styleable} annotated fields and creates an
 * attribute binding class that will retrieve and set their values when invoked.
 */
@AutoService(Processor.class)
public class GlimpseAnnotationProcessor extends AbstractProcessor {

    static final String CLASS_NAME = "com.chrynan.glimpse.GlimpseAnnotationProcessor";

    private final Map<TypeElement, List<StyleableField>> fieldMap = new HashMap<>();

    private Filer filer;
    private Messager messager;
    private Trees trees;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Styleable.class.getCanonicalName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        filer = env.getFiler();
        messager = env.getMessager();
        trees = Trees.instance(env);
        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        fieldMap.clear();

        for (final Element element : roundEnvironment.getElementsAnnotatedWith(Styleable.class)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Element = " + element.getSimpleName().toString());
            try {
                final TypeElement containingClass = (TypeElement) element.getEnclosingElement();
                final StyleableField styleableField = new StyleableField(element, RClassUtil.getStyleableAnnotationValues(trees, elementUtils, typeUtils, messager, element));

                List<StyleableField> fields = fieldMap.get(containingClass);

                if (fields == null) {
                    fields = new ArrayList<>();
                }

                fields.add(styleableField);

                fieldMap.put(containingClass, fields);
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Error processing Styleable annotation. Message = " + e.getMessage(), element);
            }
        }

        for (final Map.Entry<TypeElement, List<StyleableField>> entry : fieldMap.entrySet()) {
            messager.printMessage(Diagnostic.Kind.NOTE, entry.getKey().getSimpleName().toString());
            try {
                JavaFile file = StyleableAttributeClassCreator.create(entry.getKey(), entry.getValue());
                file.writeTo(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Error generating styleable attribute binding class. " +
                        "There was an issue writing the file out to the Filer. Message = " + e.getMessage());
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Error generating styleable attribute binding class. " +
                        "The JavaFile object seems not to of been properly created. Message = " + e.getMessage());
            }
        }

        return false;
    }
}
