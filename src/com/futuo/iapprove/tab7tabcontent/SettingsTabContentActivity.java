package com.futuo.iapprove.tab7tabcontent;

import android.os.Bundle;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveTabContentActivity;
import com.futuo.iapprove.service.CoreService;

public class SettingsTabContentActivity extends IApproveTabContentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.settings_tab_content_activity_layout);

		// set subViews
		// set title
		setTitle(R.string.settings_tab7nav_title);

		//
	}

	@Override
	protected boolean bindCoreServiceWhenOnResume() {
		// binder core service when on resume
		return true;
	}

	@Override
	protected void onCoreServiceConnected(CoreService coreService) {
		super.onCoreServiceConnected(coreService);

		// start get user login enterprise form
		coreService.startGetEnterpriseForm();
	}

	@Override
	protected void onCoreServiceDisconnected(CoreService coreService) {
		super.onCoreServiceDisconnected(coreService);

		// stop get user login enterprise form
		coreService.stopGetEnterpriseForm();
	}

}
