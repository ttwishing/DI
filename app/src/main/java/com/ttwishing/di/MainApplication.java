package com.ttwishing.di;

import com.ttwishing.di.base.App;
import com.ttwishing.di.generated.GeneratedIdBasedInjectorProvider;
import com.ttwishing.di.library.DIMaster;

/**
 * Created by kurt on 7/10/16.
 */
public class MainApplication extends App{

    @Override
    protected DIMaster initDIMaster() {
        return new DIMaster(new GeneratedIdBasedInjectorProvider());
    }
}
