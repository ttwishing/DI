package com.ttwishing.di.library;

/**
 * Created by kurt on 8/11/15.
 */
public interface IdBasedViewInjector {
    void injectViewsViaId(InjectViewProvider provider, int id, Object target);
}

