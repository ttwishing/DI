package com.ttwishing.di.library;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by kurt on 8/11/15.
 */
public class ReflectiveDIAgent {

    private final Class injectViewClass;

    private final Method resIdMethod;
    private final Method resNameMethod;
    private final Method resClassMethod;

    //非View.class类型的fields缓存
    private final Map<Class, List<Field>> fieldsCache;
    //View.class类型的fields缓存
    private final Map<Class, Map<Field, ViewAttrs>> viewFieldsCache;
    //构造器缓存
    private final Map<Class, Constructor> constructorCache;

    //单例模式的class
    private final Set<Class> singletonClasses;
    //单例模式的实例
    private final Map<Class, Object> singletonInstanceMap;

    private final DIMaster diMaster;

    public ReflectiveDIAgent(DIMaster diMaster) {
        this.diMaster = diMaster;

        this.fieldsCache = new ConcurrentHashMap();
        this.constructorCache = new ConcurrentHashMap();
        this.viewFieldsCache = new HashMap();

        this.singletonClasses = new HashSet();
        this.singletonInstanceMap = new HashMap();
        Class cls;
        try {
            cls = Class.forName("com.cooper.di.library.annotations.InjectView");
        } catch (ClassNotFoundException localClassNotFoundException) {
            cls = null;
        }

        this.injectViewClass = cls;

        if (injectViewClass == null) {//cond_0
            this.resIdMethod = null;
            this.resNameMethod = null;
            this.resClassMethod = null;
        } else {
            Method method;
            try {
                method = injectViewClass.getDeclaredMethod("value", new Class[0]);
            } catch (NoSuchMethodException e) {
                method = null;
            }
            this.resIdMethod = method;

            try {
                method = injectViewClass.getDeclaredMethod("resName", new Class[0]);
            } catch (NoSuchMethodException e) {
                method = null;
            }
            this.resNameMethod = method;

            try {
                method = injectViewClass.getDeclaredMethod("resClass", new Class[0]);
            } catch (NoSuchMethodException e) {
                method = null;
            }
            this.resClassMethod = method;
        }
    }

    /**
     * 是否可反射
     * @param kclass
     * @return
     */
    public boolean canReflect(Class kclass) {
        //非基本类 && 非接口 && 非Object.class
        if (!kclass.isPrimitive() && !kclass.isInterface() && !Object.class.equals(kclass)) {
            return true;
        }
        return false;
    }

    /**
     * 反射设置非View.class类型的Members
     * @param klass
     * @param inst
     */
    public void injectMembers(Class klass, Object inst) {
        if (klass == null)
            return;

        if (!canReflect(klass)) {
            throw new RuntimeException("cannot inject members of class " + klass.getCanonicalName() + ". is it an interface?");
        }

        if (DIMaster.hasSupperClass(klass)) {
            //如果有父类则优先处理父类
            diMaster.injectMembers(klass.getSuperclass(), inst);
        }

        List<Field> fields = getFields(klass);

        if (fields != null) {
            for (Field field : fields) {
                try {
                    field.set(inst, diMaster.injectInstance(field.getClass()));
                } catch (IllegalAccessException e) {
                }
            }
        }
    }

    /**
     * 获取非View.class类型的Field列表,如果缓存中不存在,则反射生成
     * @param klass
     * @return
     */
    private List<Field> getFields(Class klass) {
        List fields = (List) fieldsCache.get(klass);
        if (fields == null) {
            synchronized (klass) {
                fields = (List) fieldsCache.get(klass);
                if (fields == null) {
                    fields = filterInjectableFields(klass);
                    fieldsCache.put(klass, fields);
                }
            }
        }
        return fields;
    }

    /**
     * 过滤非View.class 类型的Fields
     * @param klass
     * @return
     */
    private List<Field> filterInjectableFields(Class klass) {
        Field[] allFields = getClassField(klass);
        ArrayList list = new ArrayList();
        for(Field field: allFields){
            if (field.getAnnotation(Inject.class) != null) {
                if (!field.isAccessible()) {
                    //字段是否可访问
                    field.setAccessible(true);
                }
                list.add(field);
            }
        }
        return list;
    }

    /**
     * 反射设置View.class类型的Members
     * @param provider
     * @param klass
     * @param inst
     */
    public void injectViewMembers(InjectViewProvider provider, Class klass, Object inst) {
        try {
            if (DIMaster.hasSupperClass(klass)) {
                this.diMaster.injectViewMembers(provider, klass.getSuperclass(), inst);
            }

            Map<Field, ViewAttrs> map = getViewAttrsMap(klass);
            if (map != null) {
                for(Map.Entry entry : map.entrySet()){
                    ViewAttrs viewAttrs = (ViewAttrs) entry.getValue();
                    //通过provider来获取value
                    ((Field) entry.getKey()).set(inst, provider.getValue(((Field) entry.getKey()).getType(), viewAttrs.resId, viewAttrs.resName, viewAttrs.className));
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * 根据class来获取View member的Annotation信息
     * @param klass
     * @return
     */
    private Map<Field, ViewAttrs> getViewAttrsMap(Class klass) {
        Map viewAttrsMap = (Map) viewFieldsCache.get(klass);
        if (viewAttrsMap == null) {
            synchronized (klass) {
                viewAttrsMap = (Map) this.viewFieldsCache.get(klass);
                if (viewAttrsMap == null) {
                    //不存在则创建
                    viewAttrsMap = generateViewAttrsMap(klass);
                    viewFieldsCache.put(klass, viewAttrsMap);
                }
            }
        }
        return viewAttrsMap;
    }

    /**
     * 通过反射生成
     * @param klass
     * @return
     */
    private Map<Field, ViewAttrs> generateViewAttrsMap(Class klass) {
        if (resIdMethod == null) {
            return Collections.emptyMap();
        }
        HashMap<Field, ViewAttrs> viewAttrsMap = new HashMap();
        Field[] fields = getClassField(klass);
        for (Field f : fields) {
            Annotation injectViewAnnotation = f.getAnnotation(injectViewClass);
            if (injectViewAnnotation != null) {
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                try {
                    Integer resId = (Integer) resIdMethod.invoke(injectViewAnnotation, new Object[0]);
                    String resName = (String) resNameMethod.invoke(injectViewAnnotation, new Object[0]);
                    if (resName == null) {
                        resName = f.getName();
                    }
                    Class result = (Class) resClassMethod.invoke(injectViewAnnotation, new Object[0]);
                    viewAttrsMap.put(f, new ViewAttrs(resId.intValue(), resName, result));
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        }

        return viewAttrsMap;
    }

    /**
     * 获取Field列表
     * @param klass
     * @return
     */
    private Field[] getClassField(Class klass) {
        try {
            Field[] fields = klass.getDeclaredFields();
            return fields;
        } catch (Throwable t) {
            return new Field[0];
        }
    }

    /**
     * 生成实例,并inject
     * @param klass
     * @param <T>
     * @return
     */
    public <T> T getInstance(Class<T> klass) {
        if (!instantiable(klass)) {
            throw new RuntimeException("cannot inject class " + klass.getCanonicalName() + ". are you sure " + "it has injectable constructor?");
        }
        Object instance = newInstance(klass);
        this.diMaster.injectMembers(klass, instance);
        return (T) instance;
    }

    /**
     * 生成新实例
     * @param klass
     * @param <T>
     * @return
     */
    private <T> T newInstance(Class<T> klass) {
        Constructor constructor = getConstructor(klass);
        synchronized (this.singletonClasses) {
            Object instance;

            //单例模式的实例缓存存在
            if (this.singletonClasses.contains(klass)) {
                synchronized (this.singletonInstanceMap) {
                    instance = this.singletonInstanceMap.get(klass);
                    if (instance == null) {
                        instance = newInstance(constructor);
                        this.singletonInstanceMap.put(klass, instance);
                    }
                }
            } else {
                instance = newInstance(constructor);
            }
            return (T) instance;
        }
    }

    /**
     * 由构造器来实例化
     * @param constructor
     * @param <T>
     * @return
     */
    private <T> T newInstance(Constructor<T> constructor) {
        Class[] parametersArray = constructor.getParameterTypes();
        Object[] parameters = new Object[parametersArray.length];
        int len = parametersArray.length;
        for (int i = 0; i < len; i++) {
            Class parameterClass = parametersArray[i];
            parameters[0] = this.diMaster.injectInstance(parameterClass);
        }
        try {
            Object instance = constructor.newInstance(parameters);
            return (T) instance;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * 是否可实例化
     * @param klass
     * @return
     */
    private boolean instantiable(Class klass) {
        //非基本类别 && 非接口 && 非抽象类 && 非Object.class && 有构造器
        if (!klass.isPrimitive() && !klass.isInterface() && !Modifier.isAbstract(klass.getModifiers()) && !Object.class.equals(klass) && getConstructor(klass) != null) {
            return true;
        }
        return false;
    }

    /**
     * 获取构造器
     *
     * @param klass
     * @param <T>
     * @return
     */
    private <T> Constructor<T> getConstructor(Class<T> klass) {
        Constructor constructor = constructorCache.get(klass);
        if (constructor != null) {
            return constructor;
        }

        constructor = reflectConstructor(klass);
        this.constructorCache.put(klass, constructor);
        return constructor;
    }

    /**
     * 通过反射机制获取构造器
     * @param klass
     * @param <T>
     * @return
     */
    private <T> Constructor<T> reflectConstructor(Class<T> klass) {
        Constructor[] constructors = klass.getDeclaredConstructors();
        if (constructors.length == 0) {
            throw new RuntimeException("cannot inject class " + klass.getCanonicalName() + " because it does not have a constructor");
        }

        Constructor constructor = null;
        //通过Annotation获取可用的构造器
        int i = 0;
        while (i < constructors.length) {
            Constructor item = constructors[i];
            if (item.getAnnotation(Inject.class) != null) {
                //重复
                if (constructor != null) {
                    throw new RuntimeException("multiple injectable constructors for class " + klass.getCanonicalName());
                }
            } else {
                item = constructor;
            }
            constructor = item;
            i++;
        }

        if (constructor == null) {
            StringBuilder sb = new StringBuilder().append("cannot find injectable constructor in ").append(klass.getCanonicalName()).append(" are there any constructors?=");
            if (constructors.length > 0) {
                sb.append(true);
            } else {
                sb.append(false);
            }

            throw new RuntimeException(sb.toString());
        }

        //如果是单例模式
        if (klass.getAnnotation(Singleton.class) != null) {
            synchronized (singletonClasses) {
                singletonClasses.add(klass);
            }
        }
        return (Constructor<T>) constructor;
    }


    class ViewAttrs {
        private int resId;
        private String resName;
        private String className;

        private ViewAttrs(int resId, String resName, Class cls) {
            this.resId = resId;
            this.resName = resName;
            this.className = cls.getCanonicalName();
        }
    }
}
