package com.futuo.iapprove.customwidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.futuo.iapprove.R;
import com.futuo.iapprove.receiver.UserEnterpriseBroadcastReceiver;
import com.futuo.iapprove.service.CoreService;
import com.futuo.iapprove.service.CoreService.LocalBinder;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;

public class IApproveTabContentActivity extends NavigationActivity {

	private static final String LOG_TAG = IApproveTabContentActivity.class
			.getCanonicalName();

	// core service connection
	private CoreServiceConnection _mCoreServiceConnection;

	// iApprove tab user enterprise broadcast receiver
	private IATabUserEnterpriseBroadcastReceiver _mUserEnterpriseBroadcastReceiver;

	// login user
	protected UserBean _mLoginUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get login user
		_mLoginUser = UserManager.getInstance().getUser();

		// register user enterprise broadcast receiver
		registerReceiver(
				_mUserEnterpriseBroadcastReceiver = new IATabUserEnterpriseBroadcastReceiver(),
				new IntentFilter(
						UserEnterpriseBroadcastReceiver.A_ENTERPRISECHANGE));
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_iapprove_navbar_bg);
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_iapprove_navbar_bg);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// bind core service if needed
		if (true == bindCoreServiceWhenOnResume()) {
			boolean _result = getApplicationContext().bindService(
					new Intent(this, CoreService.class),
					_mCoreServiceConnection = new CoreServiceConnection(),
					Context.BIND_AUTO_CREATE);

			Log.d(LOG_TAG, "Bind core service complete, the result = "
					+ _result + " when on resume");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// unbind core service if needed
		if (true == bindCoreServiceWhenOnResume()) {
			getApplicationContext().unbindService(_mCoreServiceConnection);
		}

		// release user enterprise broadcast receiver
		if (null != _mUserEnterpriseBroadcastReceiver) {
			unregisterReceiver(_mUserEnterpriseBroadcastReceiver);

			_mUserEnterpriseBroadcastReceiver = null;
		}
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

	// get core service
	public CoreService getCoreService() {
		return null != _mCoreServiceConnection ? _mCoreServiceConnection
				.getCoreService() : null;
	}

	// set bind core service or not when on resume
	protected boolean bindCoreServiceWhenOnResume() {
		// default not need to bind core service when on resume
		return false;
	}

	// core service on connected
	protected void onCoreServiceConnected(CoreService coreService) {
		Log.d(LOG_TAG, "Core service on connected");
	};

	// core service on disconnected
	protected void onCoreServiceDisconnected(CoreService coreService) {
		Log.d(LOG_TAG, "Core service on disconnected");
	};

	// on iApprove tab user enterprise changed
	protected void onUserEnterpriseChanged(Long newEnterpriseId) {
		Log.d(LOG_TAG, "On user enterprise changed");
	}

	// inner class
	// core service connection
	class CoreServiceConnection implements ServiceConnection {

		private final String LOG_TAG = CoreServiceConnection.class
				.getCanonicalName();

		// core service
		private CoreService _mCoreService;

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			// get core service
			_mCoreService = ((LocalBinder) binder).getService();

			Log.d(LOG_TAG, "The core service = " + _mCoreService
					+ " is connected, component name = " + name
					+ " and the binder = " + binder);

			// core service on connected
			onCoreServiceConnected(_mCoreService);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(LOG_TAG,
					"The core service is disconnected and the component name = "
							+ name);

			// core service on disconnected
			onCoreServiceDisconnected(_mCoreService);
		}

		// get core service
		public CoreService getCoreService() {
			return _mCoreService;
		}

	}

	// iApprove tab user enterprise broadcast receiver
	class IATabUserEnterpriseBroadcastReceiver extends
			UserEnterpriseBroadcastReceiver {

		@Override
		public void onEnterpriseChange(Long newEnterpriseId) {
			// on iApprove tab user enterprise changed
			onUserEnterpriseChanged(newEnterpriseId);
		}

	}

}
