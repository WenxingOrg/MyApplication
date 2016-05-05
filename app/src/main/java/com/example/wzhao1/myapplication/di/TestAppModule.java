package com.example.wzhao1.myapplication.di;

import android.app.Application;
import android.content.Context;

import com.example.wzhao1.myapplication.entry.TestBean;
import com.example.wzhao1.myapplication.manager.MainActivityManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wzhao1 on 16/4/26.
 */
@Module
public class TestAppModule {

    private final Application application;

    public TestAppModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    TestBean provideTestBean() {
        return new TestBean("zwx", "wwww");
    }

    @Singleton
    @Provides
    MainActivityManager provideMainActivityManager() {
        return new MainActivityManager();
    }
}
