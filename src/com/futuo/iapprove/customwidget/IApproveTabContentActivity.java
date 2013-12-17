package com.futuo.iapprove.customwidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.futuo.iapprove.R;
import com.futuo.iapprove.service.CoreService;
import com.futuo.iapprove.service.CoreService.LocalBinder;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class IApproveTabContentActivity extends NavigationActivity {

	private static final String LOG_TAG = IApproveTabContentActivity.class
			.getCanonicalName();

	// core service connection
	private CoreServiceConnection _mCoreServiceConnection;

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

	}

}
