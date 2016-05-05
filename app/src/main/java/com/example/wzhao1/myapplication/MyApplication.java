package com.example.wzhao1.myapplication;

import android.app.Application;

import com.example.wzhao1.myapplication.di.DaggerTestComponent;
import com.example.wzhao1.myapplication.di.TestAppModule;
import com.example.wzhao1.myapplication.di.TestComponent;

/**
 * Created by wzhao1 on 16/4/26.
 */
public class MyApplication extends Application {

    private TestComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerTestComponent.builder().testAppModule(new TestAppModule(this)).build();
    }

    public TestComponent getComponent() {
        return component;
    }
}
