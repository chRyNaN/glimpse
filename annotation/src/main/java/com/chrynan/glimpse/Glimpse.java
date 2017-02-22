package com.chrynan.glimpse;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ckeenan on 2/5/17. A class that is used to obtain styleable attributes for fields
 * annotated with the {@link Styleable} annotation by binding a class with a generated one.
 */
public class Glimpse {

    private static final String TAG = "Glimpse";

    private static final String ATTRIBUTE_CLASS_SUFFIX = "_ViewStyleableAttr";

    private static final String ANDROID_CLASS_PREFIX = "android.";
    private static final String JAVA_CLASS_PREFIX = "java.";

    private static final int PARAM_COUNT_WITH_EXTRAS = 5;
    private static final int PARAM_COUNT_WITHOUT_EXTRAS = 3;

    private static final Map<Class<?>, Constructor<?>> BINDINGS = new HashMap<>();

    private static boolean logDebug = false;

    /**
     * States whether to show logs. Logs will be output to LogCat using the
     * {@link Log#d(String, String)} method. These logs will be for runtime not the compile time
     * code generation.
     *
     * @param showDebug A boolean stating whether or not to display logs.
     */
    public static void showDebugLogsInLogCat(final boolean showDebug) {
        logDebug = showDebug;
    }

    /**
     * Obtain and set all the styleable attributes for the fields annotated with {@link Styleable}
     * within the provided target class using the provided parameters.
     *
     * @param target       The target object whose class contains global fields annotated with {@link Styleable}.
     * @param context      The {@link Context} used to retrieve the styleable attributes.
     * @param attributeSet The {@link AttributeSet} containing the attribute values.
     */
    @UiThread
    public static void obtain(@NonNull final Object target, @NonNull final Context context, final AttributeSet attributeSet) {
        bindAttributes(target, context, attributeSet);
    }

    /**
     * Obtain and set all the styleable attributes for the fields annotated with {@link Styleable}
     * within the provided target class using the provided parameters.
     *
     * @param target       The target object whose class contains global fields annotated with {@link Styleable}.
     * @param context      The {@link Context} used to retrieve the styleable attributes.
     * @param attributeSet The {@link AttributeSet} containing the attribute values.
     * @param defStyle     This field corresponds to the {@link Context#obtainStyledAttributes(AttributeSet, int[], int, int)} defStyle parameter.
     * @param defStyleRes  This field corresponds to the {@link Context#obtainStyledAttributes(AttributeSet, int[], int, int)} defStyleRes parameter.
     */
    @UiThread
    public static void obtain(@NonNull final Object target, @NonNull final Context context, final AttributeSet attributeSet,
                              final int defStyle, final int defStyleRes) {
        bindAttributes(target, context, attributeSet, defStyle, defStyleRes);
    }

    /**
     * Sets the values for the targetClass that are annotated with the {@link Styleable} annotation.
     * It does this by getting an instance of the generated attribute binding class for the provided
     * target class and invokes its constructor. The constructor of that class should have the logic
     * to look up and set the values.
     *
     * @param target       The target object container {@link Styleable} fields.
     * @param context      The {@link Context} used to obtain the
     *                     {@link android.content.res.TypedArray}.
     * @param attributeSet The {@link AttributeSet} containing the styleable attributes.
     * @param extras       The int extras corresponding to the default style and the default style
     *                     resource id, respectively. Only either zero or two extras are supported.
     */
    @SuppressWarnings("unchecked")
    @UiThread
    private static <T> void bindAttributes(@NonNull final Object target, @NonNull final Context context, final AttributeSet attributeSet,
                                           final int... extras) {
        if (logDebug) {
            Log.d(TAG, "Starting the process to retrieve and set styleable values for class: " + target.getClass().getName());
        }

        final boolean hasExtras = extras.length == 2;
        final Constructor<?> constructor = getConstructorForClass(target, hasExtras);

        if (constructor != null) {
            try {
                if (hasExtras) {
                    constructor.newInstance((T) target, context, attributeSet, extras[0], extras[1]);
                } else {
                    constructor.newInstance((T) target, context, attributeSet);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error attempting to invoke the generated attribute binding class' constructor. Constructor might be private.", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Error attempting to invoke the generated attribute binding class' constructor. Class might be abstract.", e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Error attempting to invoke the generated attribute binding class' constructor. Constructor might have thrown an exception.", e);
            }
        } else if (logDebug) {
            Log.d(TAG, "Generated class' constructor is null. It must not have been found.");
        }
    }

    /**
     * Retrieves the appropriate constructor instance from the generated binding attribute class for
     * the provided class. This method is heavily inspired (yet different) by the open source
     * Butterknife library's "findBindingConstructorForClass" method within the "ButterKnife" class.
     *
     * @param target    The target object that has a generated attribute binding class.
     * @param hasExtras Whether the constructor with extra parameters should be used.
     * @return The appropriate {@link Constructor} instance. This Constructor will be cached for
     * faster retrieval access later.
     */
    @Nullable
    @CheckResult
    @UiThread
    private static Constructor<?> getConstructorForClass(@NonNull final Object target, final boolean hasExtras) {
        final Class<?> clazz = target.getClass();
        final Constructor<?> cachedConstructor = BINDINGS.get(clazz);

        if (cachedConstructor != null) {
            final int paramCount = cachedConstructor.getParameterTypes().length;

            if (hasExtras && paramCount == PARAM_COUNT_WITH_EXTRAS || !hasExtras && paramCount == PARAM_COUNT_WITHOUT_EXTRAS) {
                if (logDebug) {
                    Log.d(TAG, "Appropriate generated class' constructor was found in the cache!");
                }
                return cachedConstructor;
            }
        }

        final String className = clazz.getName();

        // Since this method can call itself recursively, this condition prevents from going to far into a framework class
        if (className.startsWith(ANDROID_CLASS_PREFIX) || className.startsWith(JAVA_CLASS_PREFIX)) {
            if (logDebug) {
                Log.d(TAG, "Unable to find generated class. Framework class was reached.");
            }
            return null;
        }

        Constructor<?> constructor;

        try {
            final Class<?> attributeClass = Class.forName(className + ATTRIBUTE_CLASS_SUFFIX);

            if (hasExtras) {
                constructor = attributeClass.getConstructor(clazz, Context.class, AttributeSet.class, int.class, int.class);
            } else {
                constructor = attributeClass.getConstructor(clazz, Context.class, AttributeSet.class);
            }

            if (logDebug) {
                Log.d(TAG, "Generated class and it's appropriate constructor were found and created! Adding to the cache.");
            }
        } catch (ClassNotFoundException e) {
            if (logDebug) {
                Log.d(TAG, "Generated class was not found for the provided target. Checking superclass.");
            }
            // Recursive call - if the class wasn't found, perhaps the super class had a binding class generated
            constructor = getConstructorForClass(clazz.getSuperclass(), hasExtras);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Error retrieving attribute class constructor for the class: " + className, e);
        }

        BINDINGS.put(clazz, constructor);

        return constructor;
    }
}
