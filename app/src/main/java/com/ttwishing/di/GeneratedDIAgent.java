package com.ttwishing.di;//package com.kurt.android.di.generated;

import android.widget.TextView;

import com.ttwishing.di.generated.GeneratedIdBasedInjectorProvider;
import com.ttwishing.di.library.DIMaster;
import com.ttwishing.di.library.IdBasedDIAgent;
import com.ttwishing.di.library.IdBasedViewInjector;
import com.ttwishing.di.library.InjectViewProvider;
import com.ttwishing.di.library.util.IdBasedInjectorMap;

/**
 * Created by kurt on 8/11/15.
 */
public class GeneratedDIAgent implements IdBasedDIAgent, IdBasedViewInjector {

    private DIMaster diMaster;
    private GeneratedIdBasedInjectorProvider injectorProvider;

    @Override
    public IdBasedDIAgent init(DIMaster diMaster, IdBasedInjectorMap injectorMap) {
        this.diMaster = diMaster;
        if (injectorMap instanceof GeneratedIdBasedInjectorProvider) {
            this.injectorProvider = ((GeneratedIdBasedInjectorProvider) injectorMap);
        }
        return this;
    }

    @Override
    public int[] getInjectableClassIds() {
        return new int[]{0};
    }

    @Override
    public int[] getMembersInjectableClassIds() {
        return new int[]{0};
    }

    @Override
    public <T> T injectViaId(int id) {
        return null;
    }

    @Override
    public void injectMembersViaId(int id, Object target) {

    }

    @Override
    public void injectViewsViaId(InjectViewProvider provider, int id, Object target) {
        switch (id) {
            case 0:
                MainActivity mainActivity = (MainActivity) target;
                mainActivity.textView1 = provider.getValue(TextView.class, R.id.text_1, "text1", null);
                mainActivity.textView2 = provider.getValue(TextView.class, R.id.text_2, "text2", null);
                mainActivity.textView3 = provider.getValue(TextView.class, R.id.text_3, "text3", null);
                mainActivity.textView4 = provider.getValue(TextView.class, R.id.text_4, "text4", null);
                mainActivity.textView5 = provider.getValue(TextView.class, R.id.text_5, "text5", null);
                break;
        }
    }
}
