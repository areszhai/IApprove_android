package com.futuo.iapprove.account;

import android.os.Bundle;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;

public class AccountRetrievePwdActivity extends IApproveNavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.account_retrieve_pwd_activity_layout);

		// set subViews
		// test by ares
		//
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

}
