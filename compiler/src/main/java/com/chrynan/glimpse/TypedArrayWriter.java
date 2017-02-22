package com.chrynan.glimpse;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * Created by ckeenan on 2/5/17. A convenience class used to obtain {@link CodeBlock}
 * representations of calls to the Android TypedArray class methods. The benefit of using this class
 * is that it provides type safety and removes String editing from the business logic.
 */
class TypedArrayWriter {

    static final ClassName CLASS_NAME = ClassName.get("android.content.res", "TypedArray");

    private final String targetFieldName;
    private final String typedArrayName;
    private final String contextName;

    private TypedArrayWriter(final String targetFieldName, final String typedArrayName, final String contextName) {
        // Private constructor - enforces to obtain an instance of this class with the writer() method
        this.targetFieldName = targetFieldName;
        this.typedArrayName = typedArrayName;
        this.contextName = contextName;
    }

    static TypedArrayWriter writer(final String targetFieldName, final String typedArrayName, final String contextName) {
        return new TypedArrayWriter(targetFieldName, typedArrayName, contextName);
    }

    String getTargetFieldName() {
        return targetFieldName;
    }

    String getTypedArrayFieldName() {
        return typedArrayName;
    }

    String getContextName() {
        return contextName;
    }

    CodeBlock obtainStyledAttributes(final String attributeSetName, final String attributesArrayName, final String defStyleAttrName, final String defStyleResName) {
        return CodeBlock.of("$T $L = $L.obtainStyledAttributes($L, $L, $L, $L);", CLASS_NAME, typedArrayName, contextName, attributeSetName, attributesArrayName, defStyleAttrName, defStyleResName);
    }

    CodeBlock obtainAttributes(final String attributeSetName, final String attributesArrayName) {
        return CodeBlock.of("$T $L = $L.obtainStyledAttributes($L, $L);", CLASS_NAME, typedArrayName, contextName, attributeSetName, attributesArrayName);
    }

    CodeBlock getBoolean(final String fieldName, final String indexReference, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getBoolean($L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, defValue);
    }

    CodeBlock getColor(final String fieldName, final String indexReference, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getColor($L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, defValue);
    }

    CodeBlock getColorStateList(final String fieldName, final String indexReference) {
        return CodeBlock.of("$L.$L = $L.getColorStateList($L);", targetFieldName, fieldName, typedArrayName, indexReference);
    }

    CodeBlock getDimension(final String fieldName, final String indexReference, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getDimension($L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, defValue);
    }

    CodeBlock getDimensionPixelOffset(final String fieldName, final String indexReference, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getDimensionPixelOffset($L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, defValue);
    }

    CodeBlock getDimensionPixelSize(final String fieldName, final String indexReference, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getDimensionPixelSize($L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, defValue);
    }

    CodeBlock getDrawable(final String fieldName, final String indexReference) {
        return CodeBlock.of("$L.$L = $L.getDrawable($L);", targetFieldName, fieldName, typedArrayName, indexReference);
    }

    CodeBlock getFloat(final String fieldName, final String indexReference, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getFloat($L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, defValue);
    }

    CodeBlock getFraction(final String fieldName, final String indexReference, final int base, final int pBase, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getFraction($L, $L, $L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, base, pBase, defValue);
    }

    CodeBlock getInt(final String fieldName, final String indexReference, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getInt($L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, defValue);
    }

    CodeBlock getInteger(final String fieldName, final String indexReference, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getInteger($L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, defValue);
    }

    CodeBlock getNonResourceString(final String fieldName, final String indexReference) {
        return CodeBlock.of("$L.$L = $L.getNonResourceString($L);", targetFieldName, fieldName, typedArrayName, indexReference);
    }

    CodeBlock getResourceId(final String fieldName, final String indexReference, final String defValue) {
        return CodeBlock.of("$L.$L = $L.getResourceId($L, $L);", targetFieldName, fieldName, typedArrayName, indexReference, defValue);
    }

    CodeBlock getString(final String fieldName, final String indexReference) {
        return CodeBlock.of("$L.$L = $L.getString($L);", targetFieldName, fieldName, typedArrayName, indexReference);
    }

    CodeBlock getText(final String fieldName, final String indexReference) {
        return CodeBlock.of("$L.$L = $L.getText($L);", targetFieldName, fieldName, typedArrayName, indexReference);
    }

    CodeBlock getTextArray(final String fieldName, final String indexReference) {
        return CodeBlock.of("$L.$L = $L.getTextArray($L);", targetFieldName, fieldName, typedArrayName, indexReference);
    }

    CodeBlock recycle() {
        return CodeBlock.of("$L.recycle();", typedArrayName);
    }
}
