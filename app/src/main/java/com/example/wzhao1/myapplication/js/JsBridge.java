package com.example.wzhao1.myapplication.js;

import android.webkit.WebView;

/**
 * Created by wzhao1 on 16/8/31.
 */
public class JsBridge {

    private WebView webView;

    public JsBridge(WebView webView) {
        this.webView = webView;
    }

    public void fetchParameter(String method, String callId) {
        String jsScript = "javascript:";
        jsScript += "var androidCallId=\"" + callId + "\";";
        jsScript += "var androidParam = zshybrid.fetchParameter(androidCallId);";
        jsScript += "window.androidNative." + method + "(androidParam, androidCallId);";
        webView.loadUrl(jsScript);
    }

    public void callbackFromMobile(boolean successFlag) {
        webView.loadUrl("javascript:zshybrid.callbackFromMobile(androidCallId, androidParam, " + successFlag + ");");
    }

}
