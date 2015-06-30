package com.candeo.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.candeo.app.R;

/**
 * Created by partho on 29/6/15.
 */
public class CandeoTermsView extends DialogFragment {

    private WebView candeoTermsWebView;
    private Button accept,cancel;
    private View dialog;
    private Context mContext;
    private CandeoTermsInterface mCandeoTermsInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext=getActivity();
        dialog=inflater.inflate(R.layout.fragment_candeo_terms, container, false);
        initWidgets();
        return dialog;
    }

    public void setmCandeoTermsInterface(CandeoTermsInterface mCandeoTermsInterface)
    {
        this.mCandeoTermsInterface=mCandeoTermsInterface;
    }

    private void initWidgets()
    {
        candeoTermsWebView = (WebView)dialog.findViewById(R.id.candeo_terms_web);
        candeoTermsWebView.setWebChromeClient(new WebChromeClient());
        candeoTermsWebView.getSettings().setJavaScriptEnabled(true);
        candeoTermsWebView.loadUrl("http://www.candeoapp.com/terms/");
        accept=(Button)dialog.findViewById(R.id.candeo_terms_accept);
        cancel=(Button)dialog.findViewById(R.id.candeo_terms_cancel);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mCandeoTermsInterface.onTermsSuccess();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mCandeoTermsInterface.onTermsFailure();
            }
        });
        setCancelable(false);

    }




}
