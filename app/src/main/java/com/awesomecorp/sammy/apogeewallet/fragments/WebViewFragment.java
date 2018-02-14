package com.awesomecorp.sammy.apogeewallet.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.WalletActivity;
import com.awesomecorp.sammy.apogeewallet.listners.TransactionCompleteListener;
import com.awesomecorp.sammy.apogeewallet.web.AddMoneyWebView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WebViewFragment extends Fragment {

    DatabaseReference databaseReference;
    String url;
    private ProgressDialog progress;
    TransactionCompleteListener listener;
    Activity activity;
    public WebViewFragment() {
    }

    public static WebViewFragment newInstance(String url) {
        WebViewFragment fragment = new WebViewFragment();
        fragment.url = url;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_web_view, container, false);
        WebView webView = v.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String walletID = getActivity().getApplicationContext().getSharedPreferences("details",
                Context.MODE_PRIVATE).getString("walletID","");

        final String balance =getActivity().getApplicationContext().getSharedPreferences("details",
                Context.MODE_PRIVATE).getString("balance","");

        databaseReference.child("wallet").child(walletID).child("wallet").child("curr_balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!balance.equals(dataSnapshot.getValue().toString())){
                    if (listener!=null){
                        listener.transactionComplete();
                    }
                    else {
                        ((WalletActivity)activity).transactionDone();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);

        progress = ProgressDialog.show(getActivity(), "Loading.....",
                "Please wait.", true);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                if (progress != null)
                    progress.dismiss();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.w("WebActivity", "Error loading page " + description);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        webView.loadUrl(url);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (TransactionCompleteListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException("implement properly");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}