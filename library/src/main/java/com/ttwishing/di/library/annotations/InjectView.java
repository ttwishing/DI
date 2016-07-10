package com.ttwishing.di.library.annotations;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kurt on 8/11/15.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface InjectView {
    int value() default -1;

    String resName() default "";

    Class resClass() default View.class;
}
