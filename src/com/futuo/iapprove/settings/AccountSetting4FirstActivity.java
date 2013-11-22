package com.futuo.iapprove.settings;

import android.os.Bundle;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class AccountSetting4FirstActivity extends NavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.account_setting_for_first_activity_layout);

		//
	}

	@Override
	protected boolean hideNavigationBarWhenOnCreated() {
		// hide navigation bar
		return true;
	}

}
