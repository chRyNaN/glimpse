package com.chrynan.glimpse;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * Created by ckeenan on 2/8/17.
 */
class ResourcesWriter {

    static final ClassName RESOURCES = ClassName.get("android.content.res", "Resources");

    private final String targetFieldName;
    private final String resoucesFieldName;

    private ResourcesWriter(final String targetFieldName, final String resourcesFieldName) {
        // Private constructor - enforces to obtain an instance of this class with the writer() method
        this.targetFieldName = targetFieldName;
        this.resoucesFieldName = resourcesFieldName;
    }

    static ResourcesWriter writer(final String targetFieldName, final String resoucesFieldName) {
        return new ResourcesWriter(targetFieldName, resoucesFieldName);
    }

    String getTargetFieldName() {
        return targetFieldName;
    }

    String getResoucesFieldName() {
        return resoucesFieldName;
    }

    CodeBlock getString(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getString($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getStringDefault(final String idReference) {
        return CodeBlock.of("$L.getString($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getStringArray(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getStringArray($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getStringArrayDefault(final String idReference) {
        return CodeBlock.of("$L.getStringArray($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getBoolean(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getBoolean($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getBooleanDefault(final String idReference) {
        return CodeBlock.of("$L.getBoolean($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getColor(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getColor($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getColorDefault(final String idReference) {
        return CodeBlock.of("$L.getColor($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getColorStateList(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getColorStateList($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getColorStateListDefault(final String idReference) {
        return CodeBlock.of("$L.getColorStateList($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getDimension(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getDimension($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getDimensionDefault(final String idReference) {
        return CodeBlock.of("$L.getDimension($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getDimensionPixelOffset(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getDimensionPixelOffset($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getDimensionPixelOffsetDefault(final String idReference) {
        return CodeBlock.of("$L.getDimensionPixelOffset($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getDimensionPixelSize(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getDimensionPixelSize($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getDimensionPixelSizeDefault(final String idReference) {
        return CodeBlock.of("$L.getDimensionPixelSize($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getDrawable(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getDrawable($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getDrawableDefault(final String idReference) {
        return CodeBlock.of("$L.getDrawable($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getFraction(final String fieldName, final String idReference, final int base, final int pBase) {
        return CodeBlock.of("$L.$L = $L.getFraction($L, $L, $L);", targetFieldName, fieldName, resoucesFieldName, idReference, base, pBase);
    }

    String getFractionDefault(final String idReference, final int base, final int pBase) {
        return CodeBlock.of("$L.getFraction($L, $L, $L)", resoucesFieldName, idReference, base, pBase).toString();
    }

    CodeBlock getInteger(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getInteger($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getIntegerDefault(final String idReference) {
        return CodeBlock.of("$L.getInteger($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getIntArray(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getIntArray($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getIntArrayDefault(final String idReference) {
        return CodeBlock.of("$L.getIntArray($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getText(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getText($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getTextDefault(final String idReference) {
        return CodeBlock.of("$L.getText($L)", resoucesFieldName, idReference).toString();
    }

    CodeBlock getTextArray(final String fieldName, final String idReference) {
        return CodeBlock.of("$L.$L = $L.getTextArray($L);", targetFieldName, fieldName, resoucesFieldName, idReference);
    }

    String getTextArrayDefault(final String idReference) {
        return CodeBlock.of("$L.getTextArray($L)", resoucesFieldName, idReference).toString();
    }
}
