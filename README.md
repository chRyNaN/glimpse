# glimpse
##Android View Styleable Attribute Binding

Easily obtain and bind styleable attribute values. Consider the following custom View class:

```java
public class CustomView extends View {
  
  boolean showText;
  @ColorInt
  int textColor;
  
  public CustomView(Context context) {
    this(context, null);
  }
  
  public CustomView(Context context, AttributeSet attrs) {
    if (attrs != null) {
      // Set fields defined in attributes
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0);
      
      try {
        showText = a.getBoolean(R.styleable.CustomView_showText, false);
        textColor = a.getColor(R.styleable.CustomView_textColor, getResources().getColor(R.color.default_text_color));
      } finally {
        a.recycle();
      }
    } else {
      // Set defaults
      textColor = getResources().getColor(R.color.default_text_color);
    }
  }
```

This verbose code gets worse the more attributes there are. **Glimpse** removes the need to write this boilerplate code. With **Glimpse**, simply annotate your fields with the `@Styleable` annotation providing their attribute names:

```java
@Styleable(R.styleable.CustomView_showText)
boolean showText;
@ColorInt
@Styleable(value = R.styleable.CustomView_textColor, defaultRes = R.color.default_text_color)
int textColor;
```

Then call the static `obtain` method providing the View object instance as the target, the AttributeSet containing the values, and any additional parameters:

```java
Glimpse.obtain(this, attrs);
```

## Using the library

Currently, in order to use the library, you must clone the repo and run the `./gradlew build` command. There should be two jars generated in the following locations:

```
LOCATION_TO_CLONED_GLIMPSE_FOLDER/compiler/build/libs/glimpse-compiler-VERSION_NUMBER.jar
LOCATION_TO_CLONED_GLIMPSE_FOLDER/annotation/build/libs/glimpse-annotation-VERSION_NUMBER.jar
```

Add these jars to your application's top level `libs` folder. Then add the following lines to your app module's `build.gradle` file:

```
apt files('../libs/glimpse-compiler-VERSION_NAME.jar')
compile files('../libs/glimpse-annotation-VERSION_NAME.jar')
```

**Note:** If you are using the Jack tool chain with the a newer Gradle version, you can replace the `apt` command with the `annotationProcessor` command.

Finally, sync and build your project.
