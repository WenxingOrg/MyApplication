package com.example.wzhao1.myapplication.di;

import com.example.wzhao1.myapplication.activity.MainActivity;
import com.example.wzhao1.myapplication.manager.MainActivityManager;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;

/**
 * Created by wzhao1 on 16/4/26.
 */
@Singleton
@Component(modules = {TestAppModule.class})
public interface TestComponent {

    void inject(MainActivity mainActivity);

    MainActivityManager getMainActivityManager();
}
