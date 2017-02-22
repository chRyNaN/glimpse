package com.chrynan.glimpse;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * Created by ckeenan on 2/24/17.
 */
class PixelConverterWriter {

    private static final ClassName DISPLAY_METRICS_CLASS_NAME = ClassName.get("android.util", "DisplayMetrics");

    private final String targetContainerFieldName;
    private final String contextFieldName;
    private String targetFieldName;

    private PixelConverterWriter(final String targetContainerFieldName, final String contextFieldName) {
        // Private constructor - enforces to obtain an instance of this class with the writer() method
        this.targetContainerFieldName = targetContainerFieldName;
        this.contextFieldName = contextFieldName;
    }

    static PixelConverterWriter writer(final String targetContainerFieldName, final String contextFieldName) {
        return new PixelConverterWriter(targetContainerFieldName, contextFieldName);
    }

    CodeBlock convertPixelToDp(final String targetFieldName) {
        return CodeBlock.of("$L.$L = (int) ($L.$L / ((float) $L.getResources().getDisplayMetrics().densityDpi / $L.DENSITY_DEFAULT));", targetContainerFieldName, targetFieldName, targetContainerFieldName, targetFieldName, contextFieldName, DISPLAY_METRICS_CLASS_NAME);
    }
}
