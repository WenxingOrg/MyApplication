package com.example.wzhao1.myapplication.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.wzhao1.myapplication.MyApplication;
import com.example.wzhao1.myapplication.R;
import com.example.wzhao1.myapplication.di.TestComponent;
import com.example.wzhao1.myapplication.entry.TestBean;
import com.example.wzhao1.myapplication.manager.MainActivityManager;

import javax.inject.Inject;

public class MainActivity extends Activity {

    @Inject
    TestBean testBean;
    protected TestComponent testComponent;

    protected MainActivityManager mainActivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication)getApplication()).getComponent().inject(this);
        testComponent = ((MyApplication)getApplication()).getComponent();
        mainActivityManager = testComponent.getMainActivityManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("zwx", testBean.getName());

    }
}
