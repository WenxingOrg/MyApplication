package com.example.wzhao1.myapplication.fragment;

import com.example.wzhao1.myapplication.manager.NfcManager;
import com.squareup.otto.Bus;

/**
 * Created by wzhao1 on 16/5/9.
 */
public interface FragmentAction {

    Bus getBus();

    NfcManager getNfcManager();
}
