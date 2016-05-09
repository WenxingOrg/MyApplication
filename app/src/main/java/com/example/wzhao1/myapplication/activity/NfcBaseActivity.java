package com.example.wzhao1.myapplication.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.wzhao1.myapplication.MyApplication;
import com.example.wzhao1.myapplication.R;
import com.example.wzhao1.myapplication.di.TestComponent;
import com.example.wzhao1.myapplication.fragment.FragmentAction;
import com.example.wzhao1.myapplication.fragment.MainFragment;
import com.example.wzhao1.myapplication.fragment.QueryFragment;
import com.example.wzhao1.myapplication.manager.MainActivityManager;
import com.example.wzhao1.myapplication.manager.NfcManager;
import com.example.wzhao1.myapplication.manager.NfcManagerImpl;
import com.example.wzhao1.myapplication.util.navigator.NavigationEntry;
import com.example.wzhao1.myapplication.util.navigator.Navigator;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;

public class NfcBaseActivity extends FragmentActivity implements FragmentAction {
    protected TestComponent testComponent;

    protected MainActivityManager mainActivityManager;
    protected NfcManager nfcManager;
    private PendingIntent pendingIntent;
    private NfcAdapter nfcAdapter;
    private Navigator navigator;

    public String[][] TECHLISTS = new String[][] {
        { IsoDep.class.getName() },
        { NfcV.class.getName() }, { NfcF.class.getName() }, };

    public IntentFilter[] FILTERS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApplication) getApplication()).getComponent().inject(this);
        testComponent = ((MyApplication) getApplication()).getComponent();
        mainActivityManager = testComponent.getMainActivityManager();
        nfcManager = testComponent.getNfcManager();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        FILTERS = new IntentFilter[] { new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED), };
        navigator = new Navigator(this, savedInstanceState, R.id.fragment_layout, new Navigator.NavigationListener() {
            @Override
            public boolean onNavigate(Navigator navigator, NavigationEntry<?> entry) {
                return false;
            }
        });
        navigator.to(QueryFragment.newInstance()).navigate();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcManager.setIsoDep(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_layout);
        if (fragment instanceof QueryFragment) {
            ((QueryFragment) fragment).onNewIntent();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    FILTERS, TECHLISTS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public Bus getBus() {
        return testComponent.getBus();
    }

    @Override
    public NfcManager getNfcManager() {
        return nfcManager;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            nfcManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
