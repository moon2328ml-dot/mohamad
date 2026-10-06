package com.nevo.life;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(6,61,64));
        getWindow().setNavigationBarColor(Color.rgb(247,251,250));
        WebView web = new WebView(this);
        web.setBackgroundColor(Color.rgb(247,251,250));
        web.setWebViewClient(new WebViewClient());
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.loadUrl("file:///android_asset/index.html");
        setContentView(web, new FrameLayout.LayoutParams(-1, -1));
    }
    @SuppressWarnings("deprecation")
    @Override public void onBackPressed() {
        View v = findViewById(android.R.id.content);
        if (v instanceof FrameLayout && ((FrameLayout)v).getChildCount() > 0 && ((FrameLayout)v).getChildAt(0) instanceof WebView) {
            WebView w = (WebView)((FrameLayout)v).getChildAt(0);
            w.evaluateJavascript("(typeof window.nevoBack === 'function' ? window.nevoBack() : false)", value -> {
                if (!"true".equals(value)) {
                    MainActivity.super.onBackPressed();
                }
            });
            return;
        }
        super.onBackPressed();
    }
}
