package com.ttwishing.di.library;

import com.ttwishing.di.library.util.IdBasedInjectorMap;

/**
 * Created by kurt on 8/11/15.
 */
public interface IdBasedDIAgent {

    IdBasedDIAgent init(DIMaster diMaster, IdBasedInjectorMap injectorMap);

    int[] getInjectableClassIds();

    int[] getMembersInjectableClassIds();

    <T> T injectViaId(int id);

    void injectMembersViaId(int id, Object target);
}
