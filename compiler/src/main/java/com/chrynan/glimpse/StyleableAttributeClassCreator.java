package com.chrynan.glimpse;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by ckeenan on 2/7/17. A class that handles the generation of code to obtain fields values
 * based on the {@link Styleable} annotation.
 */
class StyleableAttributeClassCreator {

    private static final String ATTRIBUTE_CLASS_SUFFIX = "_ViewStyleableAttr";

    private static final ClassName OBJECT = ClassName.OBJECT;
    private static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    private static final ClassName ATTRIBUTE_SET = ClassName.get("android.util", "AttributeSet");
    private static final ClassName RESOURCES = ClassName.get("android.content.res", "Resources");
    private static final ClassName COLOR_STATE_LIST = ClassName.get("android.content.res", "ColorStateList");
    private static final ClassName DRAWABLE = ClassName.get("android.graphics.drawable", "Drawable");
    private static final ClassName CHAR_SEQUENCE = ClassName.get(CharSequence.class);
    private static final TypeName CHAR_SEQUENCE_ARRAY = ArrayTypeName.of(CHAR_SEQUENCE);
    private static final ClassName STRING = ClassName.get(String.class);
    private static final TypeName INT = TypeName.INT;
    private static final ClassName GENERATED_ANNOTATION = ClassName.get("javax.annotation", "Generated");

    private static final String CONTEXT_FIELD_NAME = StringUtils.getNormalizedCamelCasedName(CONTEXT.simpleName());
    private static final String ATTRIBUTE_SET_FIELD_NAME = StringUtils.getNormalizedCamelCasedName(ATTRIBUTE_SET.simpleName());
    private static final String RESOURCES_FIELD_NAME = "res";
    private static final String DEF_STYLE_FIELD_NAME = "defStyle";
    private static final String DEF_STYLE_RESOURCE_FIELD_NAME = "defStyleRes";
    private static final String GENERATED_ANNOTATION_VALUE_FIELD = "value";

    private StyleableAttributeClassCreator() {
        // Private constructor to prevent initialization of this object.
    }

    static JavaFile create(final TypeElement typeElement, final List<StyleableField> fields) {
        final TypeSpec.Builder classBuilder = TypeSpec.classBuilder(typeElement.getSimpleName().toString() + ATTRIBUTE_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(GENERATED_ANNOTATION)
                        .addMember(GENERATED_ANNOTATION_VALUE_FIELD, "$S", GlimpseAnnotationProcessor.CLASS_NAME)
                        .build());

        final TypeName containingClassTypeName = TypeName.get(typeElement.asType());
        final String containingClassFieldName = StringUtils.getNormalizedCamelCasedName(typeElement.getSimpleName().toString());

        // TODO these should probably be methods rather than constructors since an instance isn't needed - static methods?
        final MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(containingClassTypeName, containingClassFieldName, Modifier.FINAL)
                .addParameter(CONTEXT, CONTEXT_FIELD_NAME, Modifier.FINAL)
                .addParameter(ATTRIBUTE_SET, ATTRIBUTE_SET_FIELD_NAME, Modifier.FINAL);

        final MethodSpec.Builder constructorWithExtrasBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(containingClassTypeName, containingClassFieldName, Modifier.FINAL)
                .addParameter(CONTEXT, CONTEXT_FIELD_NAME, Modifier.FINAL)
                .addParameter(ATTRIBUTE_SET, ATTRIBUTE_SET_FIELD_NAME, Modifier.FINAL)
                .addParameter(INT, DEF_STYLE_FIELD_NAME, Modifier.FINAL)
                .addParameter(INT, DEF_STYLE_RESOURCE_FIELD_NAME, Modifier.FINAL);

        constructorBuilder.addStatement("$T $L = $L.getResources()", RESOURCES, RESOURCES_FIELD_NAME, CONTEXT_FIELD_NAME);
        constructorWithExtrasBuilder.addStatement("$T $L = $L.getResources()", RESOURCES, RESOURCES_FIELD_NAME, CONTEXT_FIELD_NAME);

        constructorBuilder.beginControlFlow("if ($L != null)", ATTRIBUTE_SET_FIELD_NAME);
        constructorWithExtrasBuilder.beginControlFlow("if ($L != null)", ATTRIBUTE_SET_FIELD_NAME);

        final Map<String, List<StyleableField>> groupMap = new HashMap<>();

        for (final StyleableField field : fields) {
            final String groupName = field.getStyleableGroupName();

            List<StyleableField> group = groupMap.get(groupName);

            if (group == null) {
                group = new ArrayList<>();
            }

            group.add(field);

            groupMap.put(groupName, group);
        }

        int typedArrayCounter = 0;
        for (final Map.Entry<String, List<StyleableField>> entry : groupMap.entrySet()) {

            final String attributeArrayName = entry.getKey();

            final String typedArrayFieldName = StringUtils.getNormalizedCamelCasedName(TypedArrayWriter.CLASS_NAME.simpleName()) + typedArrayCounter;
            final TypedArrayWriter typedArrayWriter = TypedArrayWriter.writer(containingClassFieldName, typedArrayFieldName, CONTEXT_FIELD_NAME);

            constructorBuilder.addCode(typedArrayWriter.obtainAttributes(ATTRIBUTE_SET_FIELD_NAME, attributeArrayName));
            constructorBuilder.addCode("\n");
            constructorWithExtrasBuilder.addCode(typedArrayWriter.obtainStyledAttributes(ATTRIBUTE_SET_FIELD_NAME, attributeArrayName, DEF_STYLE_FIELD_NAME, DEF_STYLE_RESOURCE_FIELD_NAME));
            constructorWithExtrasBuilder.addCode("\n");

            constructorBuilder.beginControlFlow("try");
            constructorWithExtrasBuilder.beginControlFlow("try");

            for (final StyleableField field : entry.getValue()) {
                createFieldBinding(field, constructorBuilder, typedArrayWriter);
                createFieldBinding(field, constructorWithExtrasBuilder, typedArrayWriter);
            }

            constructorBuilder.nextControlFlow("finally");
            constructorWithExtrasBuilder.nextControlFlow("finally");

            constructorBuilder.addCode(typedArrayWriter.recycle());
            constructorBuilder.addCode("\n");
            constructorWithExtrasBuilder.addCode(typedArrayWriter.recycle());
            constructorWithExtrasBuilder.addCode("\n");

            // end finally block
            constructorBuilder.endControlFlow();
            constructorWithExtrasBuilder.endControlFlow();

            typedArrayCounter++;
        }

        constructorBuilder.nextControlFlow("else");
        constructorWithExtrasBuilder.nextControlFlow("else");

        for (final StyleableField field : fields) {
            if (field.hasDefaultValue()) {
                createDefaultBinding(containingClassFieldName, CONTEXT_FIELD_NAME, field, constructorBuilder);
                createDefaultBinding(containingClassFieldName, CONTEXT_FIELD_NAME, field, constructorWithExtrasBuilder);
            }
        }

        // end if-else condition
        constructorBuilder.endControlFlow();
        constructorWithExtrasBuilder.endControlFlow();

        classBuilder.addMethod(constructorBuilder.build());
        classBuilder.addMethod(constructorWithExtrasBuilder.build());

        return JavaFile.builder(StringUtils.getPackageName(typeElement), classBuilder.build()).build();
    }

    private static void createFieldBinding(final StyleableField field, final MethodSpec.Builder builder, final TypedArrayWriter writer) {
        final ResourcesWriter resWriter = ResourcesWriter.writer(writer.getTargetFieldName(), RESOURCES_FIELD_NAME);
        final PixelConverterWriter pixelWriter = PixelConverterWriter.writer(writer.getTargetFieldName(), writer.getContextName());
        final TypeName typeName = field.getTypeName();
        int exceptionCount = 0;

        if (typeName.isPrimitive() || typeName.isBoxedPrimitive()) {
            final TypeName unboxedTypeName = field.getTypeName().unbox();

            if (unboxedTypeName.equals(TypeName.BOOLEAN)) {
                final String defValue = field.hasDefaultValue() ? resWriter.getBooleanDefault(field.getDefaultValue()) : String.valueOf(false);
                builder.addCode(writer.getBoolean(field.getName(), field.getStyleableValue(), defValue));
            } else if (unboxedTypeName.equals(TypeName.INT)) {
                // getColor(), getDimensionPixelOffset(), getDimensionPixelSize(), getInteger()
                final String colorDefValue = field.hasDefaultValue() ? resWriter.getColorDefault(field.getDefaultValue()) : "0";
                final String dimenDefValue = field.hasDefaultValue() ? resWriter.getDimensionPixelOffsetDefault(field.getDefaultValue()) : "0";
                final String intDefValue = field.hasDefaultValue() ? resWriter.getIntegerDefault(field.getDefaultValue()) : "0";

                if (field.isColorInt()) {
                    builder.addCode(writer.getColor(field.getName(), field.getStyleableValue(), colorDefValue));
                } else if (field.isDimension()) {
                    StyleableField.DimensionUnit unit = field.getDimensionUnit();

                    if (unit == null || unit == StyleableField.DimensionUnit.PX) {
                        builder.addCode(writer.getDimensionPixelOffset(field.getName(), field.getStyleableValue(), dimenDefValue));
                    } else if (unit == StyleableField.DimensionUnit.DP) {
                        builder.addCode(writer.getDimensionPixelOffset(field.getName(), field.getStyleableValue(), dimenDefValue));
                        builder.addCode("\n");
                        builder.addCode(pixelWriter.convertPixelToDp(field.getName()));
                    } else if (unit == StyleableField.DimensionUnit.SP) {
                        builder.addCode(writer.getDimensionPixelSize(field.getName(), field.getStyleableValue(), dimenDefValue));
                        builder.addCode("\n");
                        builder.addCode(pixelWriter.convertPixelToDp(field.getName()));
                    }
                } else {
                    builder.beginControlFlow("try");
                    builder.addCode(writer.getColor(field.getName(), field.getStyleableValue(), colorDefValue));
                    builder.addCode("\n");
                    builder.nextControlFlow("catch (Exception e$L)", exceptionCount);
                    exceptionCount++;
                    builder.beginControlFlow("try");
                    builder.addCode(writer.getDimensionPixelOffset(field.getName(), field.getStyleableValue(), dimenDefValue));
                    builder.addCode("\n");
                    builder.nextControlFlow("catch (Exception e$L)", exceptionCount);
                    builder.addCode(writer.getInteger(field.getName(), field.getStyleableValue(), intDefValue));
                    builder.addCode("\n");
                    builder.endControlFlow();
                    builder.endControlFlow();
                }
            } else if (unboxedTypeName.equals(TypeName.FLOAT)) {
                final String dimenDefValue = field.hasDefaultValue() ? resWriter.getDimensionDefault(field.getDefaultValue()) : "0";
                final String floatDefValue = field.hasDefaultValue() ? resWriter.getFractionDefault(field.getDefaultValue(), 0, 0) : "0";

                if (field.isDimension()) {
                    builder.addCode(writer.getDimension(field.getName(), field.getStyleableValue(), dimenDefValue));
                } else {
                    builder.beginControlFlow("try");
                    builder.addCode(writer.getDimension(field.getName(), field.getStyleableValue(), dimenDefValue));
                    builder.addCode("\n");
                    builder.nextControlFlow("catch (Exception e$L)", exceptionCount);
                    builder.addCode(writer.getFloat(field.getName(), field.getStyleableValue(), floatDefValue));
                    builder.addCode("\n");
                    builder.endControlFlow();
                }
            }
        } else if (typeName.equals(COLOR_STATE_LIST)) {
            builder.addCode(writer.getColorStateList(field.getName(), field.getStyleableValue()));
            builder.addCode("\n");

            if (field.hasDefaultValue()) {
                builder.beginControlFlow("if ($L.$L == null)", writer.getTargetFieldName(), field.getName());
                builder.addCode(writer.getColorStateList(field.getName(), resWriter.getColorStateListDefault(field.getDefaultValue())));
                builder.addCode("\n");
                builder.endControlFlow();
            }
        } else if (typeName.equals(DRAWABLE)) {
            builder.addCode(writer.getDrawable(field.getName(), field.getStyleableValue()));
            builder.addCode("\n");

            if (field.hasDefaultValue()) {
                builder.beginControlFlow("if ($L.$L == null)", writer.getTargetFieldName(), field.getName());
                builder.addCode(writer.getDrawable(field.getName(), resWriter.getDrawableDefault(field.getDefaultValue())));
                builder.addCode("\n");
                builder.endControlFlow();
            }
        } else if (typeName.equals(CHAR_SEQUENCE)) {
            builder.addCode(writer.getText(field.getName(), field.getStyleableValue()));
            builder.addCode("\n");

            if (field.hasDefaultValue()) {
                builder.beginControlFlow("if ($L.$L == null)", writer.getTargetFieldName(), field.getName());
                builder.addCode(writer.getText(field.getName(), resWriter.getTextDefault(field.getDefaultValue())));
                builder.addCode("\n");
                builder.endControlFlow();
            }
        } else if (typeName.equals(CHAR_SEQUENCE_ARRAY)) {
            if (field.hasDefaultValue()) {
                builder.beginControlFlow("try");
                builder.addCode(writer.getTextArray(field.getName(), field.getStyleableValue()));
                builder.addCode("\n");
                builder.nextControlFlow("catch (Exception e$L)", exceptionCount);
                builder.addCode(resWriter.getTextArray(field.getName(), resWriter.getTextArrayDefault(field.getDefaultValue())));
                builder.addCode("\n");
                builder.endControlFlow();
            } else {
                builder.addCode(writer.getTextArray(field.getName(), field.getStyleableValue()));
                builder.addCode("\n");
            }
        } else if (typeName.equals(STRING)) {
            builder.addCode(writer.getString(field.getName(), field.getStyleableValue()));
            builder.addCode("\n");

            if (field.hasDefaultValue()) {
                builder.beginControlFlow("if ($L.$L == null)", writer.getTargetFieldName(), field.getName());
                builder.addCode(writer.getString(field.getName(), resWriter.getStringDefault(field.getDefaultValue())));
                builder.addCode("\n");
                builder.endControlFlow();
            }
        }

        builder.addCode("\n");
    }

    private static void createDefaultBinding(final String containingClassFieldName, final String contextFieldName, final StyleableField field, final MethodSpec.Builder builder) {
        final ResourcesWriter resWriter = ResourcesWriter.writer(containingClassFieldName, RESOURCES_FIELD_NAME);
        final PixelConverterWriter pixelWriter = PixelConverterWriter.writer(containingClassFieldName, contextFieldName);
        final TypeName typeName = field.getTypeName();
        int exceptionCount = 0;

        if (typeName.isBoxedPrimitive() || typeName.isPrimitive()) {
            final TypeName unboxedTypeName = typeName.unbox();

            if (unboxedTypeName.equals(TypeName.BOOLEAN)) {
                builder.addCode(resWriter.getBoolean(field.getName(), field.getDefaultValue()));
            } else if (unboxedTypeName.equals(TypeName.INT)) {
                // getColor(), getDimensionPixelOffset(), getDimensionPixelSize(), getInteger()
                if (field.isColorInt()) {
                    builder.addCode(resWriter.getColor(field.getName(), field.getDefaultValue()));
                } else if (field.isDimension()) {
                    StyleableField.DimensionUnit unit = field.getDimensionUnit();

                    if (unit == null || unit == StyleableField.DimensionUnit.PX) {
                        builder.addCode(resWriter.getDimensionPixelOffset(field.getName(), field.getDefaultValue()));
                    } else if (unit == StyleableField.DimensionUnit.DP) {
                        builder.addCode(resWriter.getDimensionPixelOffset(field.getName(), field.getDefaultValue()));
                        builder.addCode("\n");
                        builder.addCode(pixelWriter.convertPixelToDp(field.getName()));
                    } else if (unit == StyleableField.DimensionUnit.SP) {
                        builder.addCode(resWriter.getDimensionPixelSize(field.getName(), field.getDefaultValue()));
                        builder.addCode("\n");
                        builder.addCode(pixelWriter.convertPixelToDp(field.getName()));
                    }
                } else {
                    builder.beginControlFlow("try");
                    builder.addCode(resWriter.getColor(field.getName(), field.getDefaultValue()));
                    builder.addCode("\n");
                    builder.nextControlFlow("catch (Exception e$L)", exceptionCount);
                    exceptionCount++;
                    builder.beginControlFlow("try");
                    builder.addCode(resWriter.getDimensionPixelOffset(field.getName(), field.getDefaultValue()));
                    builder.addCode("\n");
                    builder.nextControlFlow("catch (Exception e$L)", exceptionCount);
                    builder.addCode(resWriter.getInteger(field.getName(), field.getDefaultValue()));
                    builder.addCode("\n");
                    builder.endControlFlow();
                    builder.endControlFlow();
                }
            } else if (unboxedTypeName.equals(TypeName.FLOAT)) {
                if (field.isDimension()) {
                    builder.addCode(resWriter.getDimension(field.getName(), field.getDefaultValue()));
                } else {
                    builder.beginControlFlow("try");
                    builder.addCode(resWriter.getDimension(field.getName(), field.getDefaultValue()));
                    builder.addCode("\n");
                    builder.nextControlFlow("catch (Exception e$L)", exceptionCount);
                    builder.addCode(resWriter.getFraction(field.getName(), field.getDefaultValue(), 0, 1));
                    builder.addCode("\n");
                    builder.endControlFlow();
                }
            }
        } else if (typeName.equals(COLOR_STATE_LIST)) {
            builder.addCode(resWriter.getColorStateList(field.getName(), field.getDefaultValue()));
        } else if (typeName.equals(DRAWABLE)) {
            builder.addCode(resWriter.getDrawable(field.getName(), field.getDefaultValue()));
        } else if (typeName.equals(CHAR_SEQUENCE)) {
            builder.addCode(resWriter.getText(field.getName(), field.getDefaultValue()));
        } else if (typeName.equals(CHAR_SEQUENCE_ARRAY)) {
            builder.addCode(resWriter.getTextArray(field.getName(), field.getDefaultValue()));
        } else if (typeName.equals(STRING)) {
            builder.addCode(resWriter.getString(field.getName(), field.getDefaultValue()));
        }

        builder.addCode("\n");
    }
}
