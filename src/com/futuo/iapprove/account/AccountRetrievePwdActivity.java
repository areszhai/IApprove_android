package com.futuo.iapprove.account;

import android.os.Bundle;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;

public class AccountRetrievePwdActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = AccountRetrievePwdActivity.class
			.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.account_retrieve_pwd_activity_layout);

		Log.d(LOG_TAG, "Account retrieve password activity on create");

		//
	}

}
