package com.ttwishing.di;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by kurt on 8/11/15.
 */

@Singleton
public class MyManager {

    private long key;

    @Inject
    public MyManager() {
        key = System.nanoTime();
    }

    public long getKey() {
        return key;
    }
}
