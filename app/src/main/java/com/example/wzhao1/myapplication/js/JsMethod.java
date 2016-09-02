package com.example.wzhao1.myapplication.js;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Defines native method for js.
 *
 * Created by wzhao1 on 16/8/31.
 */
public class JsMethod implements Runnable{

    private final static String TAG = "JsMethod";

    private JsBridge jsBridge;

    public JsMethod(JsBridge jsBridge) {
        this.jsBridge = jsBridge;
    }

    @JavascriptInterface
    public void callNativeMethod(String param, String method) throws NoSuchMethodException {
        //TODO java do something then give callback to js.
        switch (method) {
            case "InterfaceA":
                Log.v(TAG, "call interfaceA successfully!! --> then callback");
                sendBackJsBridge();
                break;
            case "InterfaceB":
                Log.v(TAG, "call interfaceB successfully!! --> then callback");
                sendBackJsBridge();
                break;
            default:
                throw new NoSuchMethodException("Method not defined");
        }
    }

    /**
     * Retrieve javathread into main thread.
     */
    private void sendBackJsBridge() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(this);
    }

    @Override
    public void run() {
        jsBridge.callbackFromMobile(true);
    }
}
