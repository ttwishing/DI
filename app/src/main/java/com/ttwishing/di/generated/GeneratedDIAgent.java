package com.ttwishing.di.generated;//package com.kurt.android.di.generated;

import com.ttwishing.di.MyManager;
import com.ttwishing.di.library.DIMaster;
import com.ttwishing.di.library.IdBasedDIAgent;
import com.ttwishing.di.library.IdBasedViewInjector;
import com.ttwishing.di.library.InjectViewProvider;
import com.ttwishing.di.library.util.IdBasedInjectorMap;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by kurt on 8/11/15.
 */
public class GeneratedDIAgent implements IdBasedDIAgent, IdBasedViewInjector {

    private DIMaster diMaster;
    private GeneratedIdBasedInjectorProvider injectorProvider;

    /**
     * myManager 为单例模式
     */
    private MyManager myManager;
    private final CountDownLatch myManagerLock = new CountDownLatch(1);
    private final AtomicLong myManagerThreadId = new AtomicLong(-1L);

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
        return new int[]{1};
    }

    @Override
    public int[] getMembersInjectableClassIds() {
        return new int[]{1};
    }

    @Override
    public <T> T injectViaId(int id) {
        switch (id) {
            case 1:
                return (T) getMyManager();
        }
        return null;
    }

    @Override
    public void injectMembersViaId(int id, Object target) {

    }

    @Override
    public void injectViewsViaId(InjectViewProvider provider, int id, Object target) {

    }

    public MyManager getMyManager() {
        if (myManagerLock.getCount() != 0L) {
            long threadId = Thread.currentThread().getId();
            if (myManagerThreadId.compareAndSet(-1L, threadId)) {
                //未初始化
                myManager = new MyManager();
                myManagerLock.countDown();
            } else if (myManagerThreadId.get() != threadId) {
                //其他线程正初始化,待其初始化完成
                try {
                    myManagerLock.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException("got interrupted while waiting for singleton to complete");
                }
            }else{
                //其他线程已初始化完成
            }

        }
        return this.myManager;
    }
}
