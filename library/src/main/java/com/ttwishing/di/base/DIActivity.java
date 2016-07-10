package com.ttwishing.di.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by kurt on 7/10/16.
 */
public class DIActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Injector.sInjectorAgent.injectMembers(this);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        Injector.sInjectorAgent.injectViewMembers(this);
    }
}
