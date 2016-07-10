package com.ttwishing.di.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

/**
 * Created by kurt on 7/10/16.
 */
public class DIFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.sInjectorAgent.injectMembers(this);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Injector.sInjectorAgent.injectViewMembers(this);
    }
}
