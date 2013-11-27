package com.futuo.iapprove.account;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.AccountLoginActivity.AccountLoginExtraData;
import com.futuo.iapprove.tab7tabcontent.IApproveTabActivity;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class AccountSetting4FirstActivity extends NavigationActivity {

	private static final String LOG_TAG = AccountSetting4FirstActivity.class
			.getCanonicalName();

	// account login user phone
	private String _mAccountLoginUserPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.account_setting_for_first_activity_layout);

		// set subViews
		// bind account register button on click listener
		((Button) findViewById(R.id.as4f_accountRegister_button))
				.setOnClickListener(new AccountRegisterBtnOnClickListener());

		// bind account login button on click listener
		((Button) findViewById(R.id.as4f_accountLogin_button))
				.setOnClickListener(new AccountLoginBtnOnClickListener());
	}

	@Override
	protected boolean hideNavigationBarWhenOnCreated() {
		// hide navigation bar
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// check result code
		switch (resultCode) {
		case RESULT_OK:
			// check request code
			switch (requestCode) {
			case AccountSetting4FirstRequestCode.ACCOUNT_LOGIN_REQCODE:
				// finish account setting for first activity
				finish();

				// go to approve tab activity
				startActivity(new Intent(this, IApproveTabActivity.class));
				break;

			case AccountSetting4FirstRequestCode.ACCOUNT_REGISTER_REQCODE:
				//
				break;
			}
			break;

		default:
			// nothing to do
			break;
		}
	}

	// inner class
	// account setting for first request code
	static class AccountSetting4FirstRequestCode {
		// account register and login request code
		private static final int ACCOUNT_REGISTER_REQCODE = 100;
		private static final int ACCOUNT_LOGIN_REQCODE = 101;
	}

	// account register button on click listener
	class AccountRegisterBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Account register not implement, maybe don't need");

			// go to account register activity
			pushActivityForResult(AccountRegisterActivity.class,
					AccountSetting4FirstRequestCode.ACCOUNT_REGISTER_REQCODE);
		}

	}

	// account login button on click listener
	class AccountLoginBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// go to account login activity
			// check account login user phone
			if (null != _mAccountLoginUserPhone
					&& !"".equalsIgnoreCase(_mAccountLoginUserPhone)) {
				// define account login extra data map
				Map<String, String> _extraMap = new HashMap<String, String>();

				// put account login user phone to extra data map as param
				_extraMap.put(AccountLoginExtraData.ACCOUNT_LOGIN_USER_PHONE,
						_mAccountLoginUserPhone);

				// go to account login activity with extra data map
				pushActivityForResult(AccountLoginActivity.class, _extraMap,
						AccountSetting4FirstRequestCode.ACCOUNT_LOGIN_REQCODE);
			} else {
				// go to account login activity
				pushActivityForResult(AccountLoginActivity.class,
						AccountSetting4FirstRequestCode.ACCOUNT_LOGIN_REQCODE);
			}
		}

	}

}
