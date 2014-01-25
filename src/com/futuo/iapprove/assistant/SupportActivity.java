package com.futuo.iapprove.assistant;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;

public class SupportActivity extends IApproveNavigationActivity {

	// support webView
	private WebView _mSupportWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.support_activity_layout);

		// set subViews
		// set title
		setTitle(R.string.support_nav_title);

		// get support webView
		_mSupportWebView = (WebView) findViewById(R.id.support_webView);

		// load support url
		// test by ares
		_mSupportWebView.loadUrl("http://sync.walkwork.net");

		// add web chrome client for loading progress changed
		_mSupportWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);

				// set support loading progressBar progress
				((ProgressBar) findViewById(R.id.support_loading_progressBar))
						.setProgress(newProgress);

				// set support loading textView text
				((TextView) findViewById(R.id.support_loading_textView)).setText(String
						.format(getResources().getString(
								R.string.supportLoading_textView_text_format),
								newProgress));
			}

		});

		// add web view client for override url loading in support webView, page
		// started and finished
		_mSupportWebView.setWebViewClient(new WebViewClient() {

			// get support webView loading frameLayout
			FrameLayout _supportWebViewLoadingFrameLayout = (FrameLayout) findViewById(R.id.support_loading_frameLayout);

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);

				// show support webView loading frameLayout if needed
				if (View.VISIBLE != _supportWebViewLoadingFrameLayout
						.getVisibility()) {
					_supportWebViewLoadingFrameLayout
							.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);

				// hide support webView loading frameLayout
				_supportWebViewLoadingFrameLayout.setVisibility(View.GONE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// support webView load the url
				view.loadUrl(url);

				return true;
			}

		});
	}

	@Override
	public void onBackPressed() {
		// check the support webView can go back
		if (_mSupportWebView.canGoBack()) {
			// support webView go back
			_mSupportWebView.goBack();
		} else {
			super.onBackPressed();
		}
	}

}
