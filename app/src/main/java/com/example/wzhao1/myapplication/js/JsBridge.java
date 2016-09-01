package com.example.wzhao1.myapplication.js;

import android.webkit.WebView;
import android.widget.Toast;

/**
 * Js bridge is used for when native needs communication with js. Either nativeToJs or JsToNative will be called in this class.
 *
 * Created by wzhao1 on 16/8/31.
 */
public class JsBridge {

    private WebView webView;

    public JsBridge(WebView webView) {
        this.webView = webView;
    }

    /**
     * Fetches parameter from js, then calls corresponding native method.
     *
     * @param method
     * @param callId
     */
    public void fetchParameter(String method, String callId) {
        String jsScript = "javascript:";
        jsScript += "var androidCallId=\"" + callId + "\";";
        jsScript += "var androidParam = zshybrid.fetchParameter(androidCallId);";
        jsScript += "window.androidNative." + method + "(androidParam, androidCallId);";
        webView.loadUrl(jsScript);
    }

    /**
     * When native method completes, gives callback to js.
     *
     * @param successFlag
     */
    public void callbackFromMobile(boolean successFlag) {
        if(successFlag) {
            Toast.makeText(webView.getContext(), "Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(webView.getContext(), "Fail", Toast.LENGTH_SHORT).show();
        }
        webView.loadUrl("javascript:zshybrid.callbackFromMobile(androidCallId, androidParam, " + successFlag + ");");
    }

}
