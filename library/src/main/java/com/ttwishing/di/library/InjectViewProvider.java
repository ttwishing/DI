package com.ttwishing.di.library;

/**
 * Created by kurt on 8/11/15.
 */
public interface InjectViewProvider {

    <T> T getValue(Class<T> klass, int resId, String fieldName, String className);
}
