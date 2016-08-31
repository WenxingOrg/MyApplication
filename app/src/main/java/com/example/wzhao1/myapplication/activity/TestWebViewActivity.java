package com.example.wzhao1.myapplication.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.wzhao1.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestWebViewActivity extends Activity {

    private static final String TAG = "TestWebViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test_web_view);
        final WebView myWebView = (WebView) findViewById(R.id.myWebView);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl("file:///android_asset/myHtml.html");
        myWebView.addJavascriptInterface(new MyClass(), "myClass");

        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //                if(url.startsWith("shdr")) {
                //                    String methodName = "jsMethod";
                //                    String jsMethod = "";
                //                    jsMethod += "javascript:function jsMethod(){";
                //                    jsMethod += "window.myClass.method1();";
                //                    jsMethod += "}" + "" + methodName + "()";
                //                    myWebView.loadUrl(jsMethod);
                //                    return;
                //                } else {
                //
                //                }
                super.onPageStarted(view, url, favicon);
                Log.v(TAG, "onPageStarted : " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("shdr")) {
                    String param = url.substring(url.indexOf("=") + 1, url.length());
                    //                    myWebView.loadUrl("javascript: window.myClass.method1(" + param + ");");

                    InputStream mIs = null;
                    String wholeJS = null;
                    try {
                        mIs = getResources().getAssets().open("my.js");
                        if (mIs != null) {
                            byte buff[] = new byte[1024];
                            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
                            do {
                                int numread = 0;
                                numread = mIs.read(buff);
                                if (numread <= 0) {
                                    break;
                                }
                                fromFile.write(buff, 0, numread);
                            } while (true);
                            wholeJS = fromFile.toString();
                        } else {
                            Toast.makeText(TestWebViewActivity.this, "js加载失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myWebView.loadUrl("javascript: " + wholeJS);
                    myWebView.loadUrl("javascript: method1()");
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
        //        myWebView.loadUrl("javascript:jsMethod()");
    }


    class MyClass {
        @JavascriptInterface
        public void method1(String param) {
            String toastText = "javascript called native param =" + param;
            //            for (int i = 0; i < methods.length; i++) {
            //                if(i == methods.length - 1) {
            //                    toastText += methods[i] + "]";
            //                } else {
            //                    toastText += methods[i] + ", ";
            //                }
            //            }
            //            toastText += ", params=[";
            //            for (int i = 0; i < params.length; i++) {
            //                if(i == methods.length - 1) {
            //                    toastText += params[i] + "]";
            //                } else {
            //                    toastText += params[i] + ", ";
            //                }
            //            }
            Toast.makeText(TestWebViewActivity.this, toastText, Toast.LENGTH_LONG).show();
        }
    }
}
