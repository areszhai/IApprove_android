package com.futuo.iapprove.assistant;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.richitec.commontoolkit.utils.VersionUtils;

public class AboutActivity extends IApproveNavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.about_activity_layout);

		// set subViews
		// set title
		setTitle(R.string.about_nav_title);

		// set product version name
		((TextView) findViewById(R.id.ab_productVersionName_textView))
				.setText(VersionUtils.versionName());

		// bind terms and privacy button on click listener
		((Button) findViewById(R.id.ab_termsAndPrivacy_button))
				.setOnClickListener(new TermsAndPrivacyBtnOnClickListener());
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
	// terms and privacy button on click listener
	class TermsAndPrivacyBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// go to privacy activity
			pushActivity(PrivacyActivity.class);
		}

	}

}
