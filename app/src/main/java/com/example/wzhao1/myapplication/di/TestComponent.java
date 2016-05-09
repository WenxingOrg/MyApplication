package com.example.wzhao1.myapplication.di;

import com.example.wzhao1.myapplication.activity.NfcBaseActivity;
import com.example.wzhao1.myapplication.manager.MainActivityManager;
import com.example.wzhao1.myapplication.manager.NfcManager;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;

/**
 * Created by wzhao1 on 16/4/26.
 */
@Singleton
@Component(modules = {TestAppModule.class})
public interface TestComponent {

    void inject(NfcBaseActivity nfcBaseActivity);

    MainActivityManager getMainActivityManager();

    NfcManager getNfcManager();

    Bus getBus();
}
