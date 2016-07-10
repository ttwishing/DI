package com.ttwishing.di.base;

import android.app.Activity;
import android.app.Fragment;

/**
 * Created by kurt on 8/13/15.
 */
public class Injector {

    public static Agent sInjectorAgent;

    public interface Agent {
        void injectMembers(Object target);

        void injectViewMembers(Activity activity);

        void injectViewMembers(Fragment fragment);
    }
}
