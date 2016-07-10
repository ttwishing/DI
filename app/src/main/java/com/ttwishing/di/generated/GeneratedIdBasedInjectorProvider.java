package com.ttwishing.di.generated;

import com.ttwishing.di.MainActivity;
import com.ttwishing.di.MyManager;
import com.ttwishing.di.library.IdBasedDIAgent;
import com.ttwishing.di.library.util.IdBasedInjectorMap;

/**
 * Created by kurt on 8/11/15.
 */
public class GeneratedIdBasedInjectorProvider extends IdBasedInjectorMap {

    public final GeneratedDIAgent mainInjector = new GeneratedDIAgent();
    public final com.ttwishing.di.GeneratedDIAgent otherInjector = new com.ttwishing.di.GeneratedDIAgent();

    public GeneratedIdBasedInjectorProvider() {
        Class[] classArray = {
                MainActivity.class,
                MyManager.class,
        };

        IdBasedDIAgent[] injectors = {
                mainInjector,
                otherInjector,
        };

        init(classArray, injectors, 10);
    }
}
