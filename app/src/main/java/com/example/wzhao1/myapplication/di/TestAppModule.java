package com.example.wzhao1.myapplication.di;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.wzhao1.myapplication.manager.MainActivityManager;
import com.example.wzhao1.myapplication.manager.NfcManager;
import com.example.wzhao1.myapplication.manager.NfcManagerImpl;
import com.example.wzhao1.myapplication.manager.OnNfcSuccessListener;
import com.squareup.otto.Bus;

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
    MainActivityManager provideMainActivityManager() {
        return new MainActivityManager();
    }

    @Singleton
    @Provides
    Bus provideBus() {
        return new Bus();
    }

    @Singleton
    @Provides
    NfcManager provideNfcManager(final Bus bus) {
        return new NfcManagerImpl(bus, new OnNfcSuccessListener() {
            Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void onSuccess(final Object nfcResponseEvent) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bus.post(nfcResponseEvent);
                    }
                });
            }
        });
    }

}
