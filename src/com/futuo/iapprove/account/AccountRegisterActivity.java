package com.futuo.iapprove.account;

import android.os.Bundle;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;

public class AccountRegisterActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = AccountRegisterActivity.class
			.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.account_register_activity_layout);

		Log.d(LOG_TAG, "Account register activity on create");

		//
	}

}
