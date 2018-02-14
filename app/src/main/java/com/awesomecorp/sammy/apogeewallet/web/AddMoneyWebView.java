package com.awesomecorp.sammy.apogeewallet.web;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by sammy on 12/2/18.
 */

public class AddMoneyWebView extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
