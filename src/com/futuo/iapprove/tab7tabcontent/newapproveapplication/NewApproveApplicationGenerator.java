package com.futuo.iapprove.tab7tabcontent.newapproveapplication;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormTypes.FormType;
import com.futuo.iapprove.service.CoreService;
import com.futuo.iapprove.service.CoreService.LocalBinder;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationActivity.NewApproveApplicationExtraData;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.form.EnterpriseFormListActivity;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.form.EnterpriseFormListActivity.EnterpriseFormExtraData;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.form.EnterpriseFormTypeListActivity;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.user.UserManager;

public class NewApproveApplicationGenerator {

	private static final String LOG_TAG = NewApproveApplicationGenerator.class
			.getCanonicalName();

	// application context
	private Context _mAppContext;

	// core service connection
	private CoreServiceConnection _mCoreServiceConnection;

	public NewApproveApplicationGenerator() {
		super();

		// get application context
		_mAppContext = CTApplication.getContext();

		// bind core service
		boolean _result = _mAppContext.getApplicationContext().bindService(
				new Intent(_mAppContext, CoreService.class),
				_mCoreServiceConnection = new CoreServiceConnection(),
				Context.BIND_AUTO_CREATE);

		Log.d(LOG_TAG, "Bind core service complete, the result = " + _result
				+ " when on new approve application");
	}

	// generate new approve application
	public void genNewApproveApplication(final ABContactBean submitContact) {
		// get content resolver
		final ContentResolver _contentResolver = _mAppContext
				.getContentResolver();

		// delayed 0.8 second to querying
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// get local storage enterprise form type count, first type id
				// and name
				Cursor _cursor = _contentResolver.query(ContentUris
						.withAppendedId(FormType.ENTERPRISE_CONTENT_URI,
								IAUserExtension
										.getUserLoginEnterpriseId(UserManager
												.getInstance().getUser())),
						new String[] { FormType._COUNT_PROJECTION,
								FormType.TYPE_ID, FormType.NAME }, null, null,
						null);

				// check cursor
				if (null != _cursor) {
					while (_cursor.moveToNext()) {
						// get and check form types count
						long _formTypeCount = _cursor.getLong(_cursor
								.getColumnIndex(FormType._COUNT));
						if (0 == _formTypeCount) {
							Log.e(LOG_TAG,
									"There is no user login enterprise form type");

							// test by ares
							//
						} else if (1 == _formTypeCount) {
							// goto enterprise form list activity
							// define the enterprise form intent
							Intent _enterpriseFormIntent = new Intent(
									_mAppContext,
									EnterpriseFormListActivity.class);

							// set it as an new task
							_enterpriseFormIntent
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							// put the enterprise form list activity is new task
							// flag, the only one form type id, name and the new
							// approve application submit contact bean to extra
							// data map as param
							_enterpriseFormIntent
									.putExtra(
											EnterpriseFormExtraData.ENTERPRISE_FROMLISTACTIVITY_NEWTASK,
											true);
							_enterpriseFormIntent
									.putExtra(
											EnterpriseFormExtraData.ENTERPRISE_FROM_TYPE_ID,
											_cursor.getLong(_cursor
													.getColumnIndex(FormType.TYPE_ID)));
							_enterpriseFormIntent
									.putExtra(
											EnterpriseFormExtraData.ENTERPRISE_FROM_TYPE_NAME,
											_cursor.getString(_cursor
													.getColumnIndex(FormType.NAME)));
							_enterpriseFormIntent
									.putExtra(
											NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACT,
											submitContact);

							// start enterprise form list activity
							_mAppContext.startActivity(_enterpriseFormIntent);
						} else {
							// goto enterprise form type list activity
							// define the enterprise form type intent
							Intent _enterpriseFormTypeIntent = new Intent(
									_mAppContext,
									EnterpriseFormTypeListActivity.class);

							// set it as an new task
							_enterpriseFormTypeIntent
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							// put the new approve application submit contact
							// bean to extra data map as param
							_enterpriseFormTypeIntent
									.putExtra(
											NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACT,
											submitContact);

							// start enterprise form type list activity
							_mAppContext
									.startActivity(_enterpriseFormTypeIntent);
						}
					}

					// close cursor
					_cursor.close();
				}
			}

		}, 800);
	}

	// inner class
	// core service connection
	class CoreServiceConnection implements ServiceConnection {

		private final String LOG_TAG = CoreServiceConnection.class
				.getCanonicalName();

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			// get core service
			CoreService _coreService = ((LocalBinder) binder).getService();

			Log.d(LOG_TAG, "The core service = " + _coreService
					+ " is connected, component name = " + name
					+ " and the binder = " + binder);

			// start get user login enterprise form
			_coreService.startGetEnterpriseForm();

			// unbind core service
			_mAppContext.getApplicationContext().unbindService(
					_mCoreServiceConnection);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(LOG_TAG,
					"The core service is disconnected and the component name = "
							+ name);

			// nothing to do
		}

	}

}
