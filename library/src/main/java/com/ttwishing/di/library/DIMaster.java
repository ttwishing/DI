package com.ttwishing.di.library;

import com.ttwishing.di.library.util.IdBasedInjectorMap;

/**
 * Created by kurt on 8/11/15.
 */
public class DIMaster {

    ReflectiveDIAgent reflectiveDIAgent;//blq
    IdBasedInjectorMap injectorMap;//blr

    //是否支持通过reflect来di, 通常关闭以提高效率
    private boolean supportReflect = true;

    public DIMaster() {
        this(new IdBasedInjectorMap(new Class[0], new IdBasedDIAgent[0], 0));
    }

    public DIMaster(IdBasedInjectorMap injectorMap) {
        this.injectorMap = injectorMap;
        this.reflectiveDIAgent = new ReflectiveDIAgent(this);

        this.injectorMap.initAllInjectors(this, injectorMap);
    }

    /**
     * 设置是否允许通过反射来inject, 在appp初始化时调用
     * @param supportReflect
     */
    public void setSupportReflect(boolean supportReflect) {
        this.supportReflect = supportReflect;
    }

    /**
     * inject非View类型的Members
     * @param klass
     * @param inst members所属实例
     */
    public void injectMembers(Class klass, Object inst) {
        injectMembers(klass, inst, this.injectorMap.getIdViaClass(klass));
    }

    /**
     * inject非View类型的Members
     *
     * @param klass
     * @param inst
     * @param id
     */
    public void injectMembers(Class klass, Object inst, int id) {
        if (klass == null) {
            return;
        }

        if (id != -1) {
            IdBasedDIAgent injector = this.injectorMap.getMembersInjector(id);
            if (injector != null) {
                injector.injectMembersViaId(id, inst);
            }
            return;
        }

        if (this.supportReflect) {
            //通过反射inject
            this.reflectiveDIAgent.injectMembers(klass, inst);
            return;
        }

        //如果不通过反射inject, 则inject父类
        if (hasSupperClass(klass)) {
            injectMembers(klass.getSuperclass(), inst);
        }
    }

    /**
     * inject View.class类型的members
     * @param provider
     * @param klass
     * @param inst
     */
    public void injectViewMembers(InjectViewProvider provider, Class klass, Object inst) {
        injectViewMembers(provider, klass, inst, this.injectorMap.getIdViaClass(klass));
    }

    /**
     * inject View.class类型的members
     * @param provider
     * @param klass
     * @param inst
     * @param id
     */
    private void injectViewMembers(InjectViewProvider provider, Class klass, Object inst, int id) {
        if (id != -1) {
            IdBasedDIAgent injector = this.injectorMap.getMembersInjector(id);
            if (injector != null && injector instanceof IdBasedViewInjector) {
                ((IdBasedViewInjector) injector).injectViewsViaId(provider, id, inst);
            }
            return;
        }

        if (this.supportReflect) {
            //通过反射inject
            this.reflectiveDIAgent.injectViewMembers(provider, klass, inst);
            return;
        }

        //如果不通过反射inject, 则inject父类
        if (hasSupperClass(klass)) {
            injectViewMembers(provider, klass.getSuperclass(), inst);
            return;
        }
    }

    /**
     * 父类是否可inject
     * @param klass
     * @return
     */
    protected static boolean hasSupperClass(Class klass) {
        if (klass.getSuperclass() != null && !Object.class.equals(klass.getSuperclass()) && !klass.equals(klass.getSuperclass())) {
            return true;
        }
        return false;
    }

    /**
     * 通过inject来获取某个实例
     * @param klass
     * @param <T>
     * @return
     */
    public <T> T injectInstance(Class<T> klass) {
        return injectInstance(klass, this.injectorMap.getIdViaClass(klass));
    }

    /**
     * 通过inject来获取某个实例
     * @param klass
     * @param id
     * @param <T>
     * @return
     */
    private <T> T injectInstance(Class<T> klass, int id) {
        if (id != -1) {
            IdBasedDIAgent injector = this.injectorMap.getInjector(id);
            if (injector != null) {
                return injector.injectViaId(id);
            }
        }

        if (this.supportReflect) {
            return this.reflectiveDIAgent.getInstance(klass);
        }

        throw new RuntimeException("cannot inject class, try enabling reflection: " + klass.getCanonicalName() + " or provide it");
    }

    /**
     * 注入点
     * @param target
     */
    public void inject(Object target) {
        inject(target.getClass(), target);
    }

    /**
     * 注入点
     * @param klass
     * @param target
     */
    public void inject(Class klass, Object target) {
        inject(klass, target, injectorMap.getIdViaClass(klass));
    }

    /**
     * 注入点
     * @param klass
     * @param target
     * @param id
     */
    public void inject(Class klass, Object target, int id) {
        if (klass == null) {
            return;
        }
        if (id != -1) {
            IdBasedDIAgent agent = injectorMap.getMembersInjector(id);
            if (agent != null) {
                agent.injectMembersViaId(id, target);
            }
            return;
        }
        if (this.supportReflect) {
            this.reflectiveDIAgent.injectMembers(klass, target);
            return;
        }

        if (hasSupperClass(klass)) {
            inject(klass.getSuperclass(), target);
        }
    }
}
