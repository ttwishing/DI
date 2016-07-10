package com.ttwishing.di.library.util;

import com.ttwishing.di.library.DIMaster;
import com.ttwishing.di.library.IdBasedDIAgent;

/**
 * Created by kurt on 8/11/15.
 *
 * 以class id为索引
 */
public class IdBasedInjectorMap {

    private Class[] klasses;

    //以class id为索引的列表,
    private IdBasedDIAgent[] injectors;
    //以class id为索引的列表
    private IdBasedDIAgent[] membersInjectors;

    //以class为索引的id Map
    private ClassToIntMap klassBasedIdMap;

    // >= 可injectable classes
    private int capacity;

    public IdBasedInjectorMap() {
    }

    public IdBasedInjectorMap(Class[] klasses, IdBasedDIAgent[] injectors, int capacity) {
        init(klasses, injectors, capacity);
    }

    //wheatbiscuit
    public IdBasedInjectorMap init(Class[] klasses, IdBasedDIAgent[] injectors, int capacity) {
        this.klasses = klasses;
        this.capacity = capacity;

        this.klassBasedIdMap = new ClassToIntMap(klasses.length);
        this.injectors = new IdBasedDIAgent[klasses.length];
        this.membersInjectors = new IdBasedDIAgent[klasses.length];

        for (int id = 0; id < klasses.length; id++) {
            klassBasedIdMap.put(klasses[id], id);
        }

        for (int i = 0; i < injectors.length; i++) {
            indexInjector(injectors[i]);
        }
        return this;
    }

    public void initAllInjectors(DIMaster diMaster, IdBasedInjectorMap idBasedInjectorMap) {
        for (IdBasedDIAgent agent : this.injectors) {
            if (agent != null) {
                agent.init(diMaster, idBasedInjectorMap);
            }
        }
        for (IdBasedDIAgent agent : this.membersInjectors) {
            if (agent != null) {
                agent.init(diMaster, idBasedInjectorMap);
            }
        }
    }

    /**
     * 为agent建立列表的索引
     * @param agent
     */
    public void indexInjector(IdBasedDIAgent agent) {
        int[] injectableClassIds = agent.getInjectableClassIds();
        for (int id : injectableClassIds) {
            this.injectors[id] = agent;
        }
        int[] membersInjectableClassIds = agent.getMembersInjectableClassIds();
        for (int id : membersInjectableClassIds) {
            this.membersInjectors[id] = agent;
        }
    }

    //获取class的id
    public int getIdViaClass(Class klass) {
        return klassBasedIdMap.get(klass);
    }

    public IdBasedDIAgent getInjector(int id) {
        return this.injectors[id];
    }

    public IdBasedDIAgent getMembersInjector(int id) {
        return this.membersInjectors[id];
    }

}
