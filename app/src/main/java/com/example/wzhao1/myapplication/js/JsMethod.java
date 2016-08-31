package com.example.wzhao1.myapplication.js;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by wzhao1 on 16/8/31.
 */
public class JsMethod implements Runnable{

    private final static String TAG = "JsMethod";

    private JsBridge jsBridge;

    public JsMethod(JsBridge jsBridge) {
        this.jsBridge = jsBridge;
    }

    @JavascriptInterface
    public void InterfaceA(String param, String callId) {
        //TODO java do something then give callback to js.
        Log.v(TAG, "call interfaceA successfully!! --> then callback");
        sendBackJsBridge();
    }

    @JavascriptInterface
    public void InterfaceB(String param, String callId) {
        //TODO java do something then give callback to js.
        Log.v(TAG, "call interfaceB successfully!! --> then callback");
        sendBackJsBridge();

    }

    private void sendBackJsBridge() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(this);
    }

    @Override
    public void run() {
        jsBridge.callbackFromMobile(true);
    }
}
