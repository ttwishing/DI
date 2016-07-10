package com.ttwishing.di.base;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;

import com.ttwishing.di.library.DIMaster;
import com.ttwishing.di.library.InjectViewProvider;

/**
 * Created by kurt on 8/13/15.
 */
public abstract class App extends Application {

    private static App sApp;
    public DIMaster diMaster;
    //app运行标识,采用nanoTime
    private long createTime;

    public App() {
        sApp = this;
        try {
            this.diMaster = initDIMaster();
            //TODO 在必要的情况下为效率关闭,设置为false
            this.diMaster.setSupportReflect(true);
            Injector.sInjectorAgent = new Injector.Agent() {

                @Override
                public void injectMembers(Object target) {
                    diMaster.injectMembers(target.getClass(), target);
                }

                @Override
                public void injectViewMembers(final Activity activity) {
                    diMaster.injectViewMembers(new InjectViewProvider() {
                        @Override
                        public <T> T getValue(Class<T> cls, int resId, String fieldName, String defPackage) {
                            Log.d("kurt_test", resId+", "+fieldName);
                            View view = activity.findViewById(resId);
                            if (view == null) {
                                Resources resources = activity.getResources();
                                if (defPackage == null) {
                                    defPackage = getPackageName();
                                }
                                resId = resources.getIdentifier(fieldName, "id", defPackage);
                                view = activity.findViewById(resId);
                            }
                            return (T) view;
                        }
                    }, activity.getClass(), activity);
                }

                @Override
                public void injectViewMembers(final Fragment fragment) {
                    diMaster.injectViewMembers(new InjectViewProvider() {
                        @Override
                        public <T> T getValue(Class<T> cls, int resId, String fieldName, String defPackage) {
                            View fragmentView = fragment.getView();
                            View view = fragmentView.findViewById(resId);
                            if (view == null) {
                                Resources resources = fragment.getResources();
                                if (defPackage == null) {
                                    defPackage = getPackageName();
                                }
                                resId = resources.getIdentifier(fieldName, "id", defPackage);
                                view = fragmentView.findViewById(resId);
                            }
                            return (T) view;
                        }
                    }, fragment.getClass(), fragment);
                }
            };
        } catch (Throwable t) {
            throw t;
        }
    }

    public static Context getContext() {
        return sApp;
    }

    public static <T extends App> T getInstance() {
        return (T) sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.createTime = System.nanoTime();
    }

    protected abstract DIMaster initDIMaster();

    /**
     * 在合适的时机注入
     * @param target
     */
    public static void inject(Object target) {
        getInstance().diMaster.inject(target);
    }

    /**
     * 由构造器获取实例
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<T> cls) {
        return getInstance().diMaster.injectInstance(cls);
    }

    public long getCreateTime() {
        return this.createTime;
    }
}
