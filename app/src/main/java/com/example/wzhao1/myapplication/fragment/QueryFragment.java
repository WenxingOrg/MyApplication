package com.example.wzhao1.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wzhao1.myapplication.R;
import com.example.wzhao1.myapplication.manager.NfcManagerImpl;
import com.squareup.otto.Subscribe;

/**
 * Created by wzhao1 on 16/5/9.
 */
public class QueryFragment extends Fragment {

    private FragmentAction fragmentAction;

    private static final String TAG = "QueryFragment";
    private TextView tvMoney;

    public static QueryFragment newInstance() {
        Log.v(TAG, "QueryFragment init()");
        Bundle args = new Bundle();

        QueryFragment fragment = new QueryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        fragmentAction = (FragmentAction) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query, container, false);
        tvMoney = (TextView) view.findViewById(R.id.tv_money);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentAction.getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentAction.getBus().unregister(this);
    }

    public void onNewIntent() {
        fragmentAction.getNfcManager().writeApdus(new byte[][] {
                new byte[] {(byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x3F, (byte) 0x01},
                new byte[] {(byte) 0x80, (byte) 0x5C, (byte) 0x00, (byte) 0x02, (byte) 0x04}
        });
    }

    @Subscribe
    public void onNfcEvent(NfcManagerImpl.NfcResponseEvent nfcEvent) {
        Log.e("zwx:", nfcEvent.getResponseData());
        long l = Long.parseLong(nfcEvent.getResponseData().substring(0, 8), 16);
        tvMoney.setText(String.valueOf(l / 100f));
    }
}
