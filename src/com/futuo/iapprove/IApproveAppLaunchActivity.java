package com.futuo.iapprove;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.futuo.iapprove.account.AccountSetting4FirstActivity;
import com.futuo.iapprove.tab7tabcontent.IApproveTabActivity;
import com.richitec.commontoolkit.activityextension.AppLaunchActivity;

public class IApproveAppLaunchActivity extends AppLaunchActivity {

	@Override
	public Drawable splashImg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Intent intentActivity() {
		// define default target intent activity, approve tab activity
		Intent _targetIntentActivity = new Intent(this,
				IApproveTabActivity.class);

		// go to account setting activity
		_targetIntentActivity = new Intent(this,
				AccountSetting4FirstActivity.class);

		// go to target activity
		return _targetIntentActivity;
	}

	@Override
	public boolean didFinishLaunching() {
		// TODO Auto-generated method stub
		return false;
	}

}
