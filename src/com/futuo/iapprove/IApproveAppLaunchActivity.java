package com.futuo.iapprove;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.futuo.iapprove.account.AccountSetting4FirstActivity;
import com.futuo.iapprove.tab7tabcontent.IApproveTabActivity;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;

public class IApproveAppLaunchActivity extends AppLaunchActivity {

	@Override
	public Drawable splashImg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Intent intentActivity() {
		// define default target intent activity, account setting activity
		Intent _targetIntentActivity = new Intent(this,
				AccountSetting4FirstActivity.class);

		// load login account
		AppDataSaveRestoreUtils.loadAccount();

		// get login user
		UserBean _loginUser = UserManager.getInstance().getUser();

		// check user name and user key
		if (null != _loginUser.getName()
				&& !"".equalsIgnoreCase(_loginUser.getName())
				&& null != _loginUser.getUserKey()
				&& !"".equalsIgnoreCase(_loginUser.getUserKey())) {
			// go to approve tab activity
			_targetIntentActivity = new Intent(this, IApproveTabActivity.class);
		}

		// go to target activity
		return _targetIntentActivity;
	}

	@Override
	public boolean didFinishLaunching() {
		// TODO Auto-generated method stub
		return false;
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
