package com.futuo.iapprove.tab7tabcontent.task;

import android.os.Bundle;
import android.webkit.WebView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;

public class TaskApplicationAttachmentViewActivity extends
		IApproveNavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.task_application_attachment_view_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define task application attachment name and open url
		String _taskApplicationAttachmentName = "";
		String _taskApplicationAttachmentOpenUrl = "";

		// check the data
		if (null != _extraData) {
			// get task application attachment name and open url
			_taskApplicationAttachmentName = _extraData
					.getString(TaskApplicationAttachmentViewExtraData.TASK_APPLICATIONATTCHMENT_NAME);
			_taskApplicationAttachmentOpenUrl = _extraData
					.getString(TaskApplicationAttachmentViewExtraData.TASK_APPLICATIONATTCHMENT_OPEN_URL);
		}

		// set subViews
		// set title
		setTitle(_taskApplicationAttachmentName);

		// get task application attachment present webView
		WebView _taskApplicationAttachmentPresentWebView = (WebView) findViewById(R.id.task_applicationAttachment_present_webView);

		// load task application attachment open url
		_taskApplicationAttachmentPresentWebView
				.loadUrl(_taskApplicationAttachmentOpenUrl);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		AppDataSaveRestoreUtils.onRestoreInstanceState(savedInstanceState);

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtils.onSaveInstanceState(outState);

		super.onSaveInstanceState(outState);
	}

	// inner class
	// task application attachment view extra data constant
	public static final class TaskApplicationAttachmentViewExtraData {

		// task application attachment name and open url
		public static final String TASK_APPLICATIONATTCHMENT_NAME = "task_applicationattachment_name";
		public static final String TASK_APPLICATIONATTCHMENT_OPEN_URL = "task_applicationattachment_openurl";

	}

}
