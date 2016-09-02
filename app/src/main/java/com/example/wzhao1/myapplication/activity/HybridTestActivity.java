package com.example.wzhao1.myapplication.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.wzhao1.myapplication.R;
import com.example.wzhao1.myapplication.js.JsBridge;
import com.example.wzhao1.myapplication.js.JsMethod;

public class HybridTestActivity extends Activity {

    private static final String TAG = "TestWebViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test_web_view);
        final WebView myWebView = (WebView) findViewById(R.id.myWebView);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl("file:///android_asset/index.html");
        final JsBridge jsBridge = new JsBridge(myWebView);
        myWebView.addJavascriptInterface(new JsMethod(jsBridge), "androidNative");

        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.v(TAG, "onPageStarted : " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Intercept the scheme then call native methods.
                if (url.startsWith("hybridsample")) {
                    int indexDoubleSlash = url.indexOf("//") + 2;
                    int indexSlashQustionMark = url.indexOf("/?");
                    int indexCallIdParam = url.indexOf("callID") + 7;
                    String methodName = url.substring(indexDoubleSlash, indexSlashQustionMark);
                    String callId = url.substring(indexCallIdParam);
                    jsBridge.callNativeMethod(methodName, callId);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v(TAG, "onPageFinished : " + url);

                super.onPageFinished(view, url);
            }
        });
    }

}
