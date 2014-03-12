package com.futuo.iapprove.tab7tabcontent.newapproveapplication;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormTypes.FormType;
import com.futuo.iapprove.receiver.EnterpriseFormBroadcastReceiver;
import com.futuo.iapprove.service.CoreService;
import com.futuo.iapprove.service.CoreService.LocalBinder;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationActivity.NewApproveApplicationExtraData;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.form.EnterpriseFormListActivity;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.form.EnterpriseFormListActivity.EnterpriseFormExtraData;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.form.EnterpriseFormTypeListActivity;
import com.richitec.commontoolkit.user.UserManager;

public class NewApproveApplicationGenerator {

	private static final String LOG_TAG = NewApproveApplicationGenerator.class
			.getCanonicalName();

	// dependent context
	private Context _mDependentContext;

	// default submit contact
	private ABContactBean _mDefaultSubmitContact;

	// generate new approve application loading enterprise form progress dialog
	private ProgressDialog _mGenNAALoadingEnterpriseFormProgDlg;

	// core service connection
	private CoreServiceConnection _mCoreServiceConnection;

	// enterprise form type broadcast receiver
	private EnterpriseFormTypeBroadcastReceiver _mEnterpriseFormTypeBroadcastReceiver;

	public NewApproveApplicationGenerator(Context dependentContext) {
		super();

		// save dependent context
		_mDependentContext = dependentContext;
	}

	// generate new approve application
	public void genNewApproveApplication(final ABContactBean submitContact) {
		// show new approve application loading enterprise form progress dialog
		_mGenNAALoadingEnterpriseFormProgDlg = ProgressDialog
				.show(_mDependentContext,
						null,
						_mDependentContext
								.getResources()
								.getString(
										R.string.naaGenerate_loadingEnterpriseForm_progDlg_message),
						true);

		// save default submit contact
		_mDefaultSubmitContact = submitContact;

		// get local storage enterprise form type count, first type id and name
		Cursor _cursor = _mDependentContext.getContentResolver().query(
				ContentUris.withAppendedId(FormType.ENTERPRISE_CONTENT_URI,
						IAUserExtension.getUserLoginEnterpriseId(UserManager
								.getInstance().getUser())),
				new String[] { FormType._COUNT_PROJECTION, FormType.TYPE_ID,
						FormType.NAME }, null, null, null);

		// check cursor
		if (null != _cursor) {
			while (_cursor.moveToNext()) {
				// get and check form types count
				long _formTypeCount = _cursor.getLong(_cursor
						.getColumnIndex(FormType._COUNT));
				if (0 == _formTypeCount) {
					Log.w(LOG_TAG,
							"There is no user login enterprise form type");

					// register enterprise form type broadcast receiver
					_mDependentContext
							.registerReceiver(
									_mEnterpriseFormTypeBroadcastReceiver = new EnterpriseFormTypeBroadcastReceiver(),
									new IntentFilter(
											EnterpriseFormBroadcastReceiver.A_FORMTYPECHANGE));

					// bind core service
					boolean _result = _mDependentContext
							.getApplicationContext()
							.bindService(
									new Intent(_mDependentContext,
											CoreService.class),
									_mCoreServiceConnection = new CoreServiceConnection(),
									Context.BIND_AUTO_CREATE);

					Log.d(LOG_TAG, "Bind core service complete, the result = "
							+ _result + " when on new approve application");
				} else if (1 == _formTypeCount) {
					// done loading enterprise form
					doneLoadingEnterpriseForm();

					// goto enterprise form list activity
					// define the enterprise form intent
					Intent _enterpriseFormIntent = new Intent(
							_mDependentContext,
							EnterpriseFormListActivity.class);

					// put the enterprise form list activity is new task flag,
					// the only one form type id, name and the new approve
					// application submit contact bean to extra data map as
					// param
					_enterpriseFormIntent
							.putExtra(
									EnterpriseFormExtraData.ENTERPRISE_FROMLISTACTIVITY_NEWTASK,
									true);
					_enterpriseFormIntent.putExtra(
							EnterpriseFormExtraData.ENTERPRISE_FROM_TYPE_ID,
							_cursor.getLong(_cursor
									.getColumnIndex(FormType.TYPE_ID)));
					_enterpriseFormIntent.putExtra(
							EnterpriseFormExtraData.ENTERPRISE_FROM_TYPE_NAME,
							_cursor.getString(_cursor
									.getColumnIndex(FormType.NAME)));
					_enterpriseFormIntent
							.putExtra(
									NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACTS,
									_mDefaultSubmitContact);

					// start enterprise form list activity
					_mDependentContext.startActivity(_enterpriseFormIntent);
				} else {
					// done loading enterprise form
					doneLoadingEnterpriseForm();

					// goto enterprise form type list activity
					// define the enterprise form type intent
					Intent _enterpriseFormTypeIntent = new Intent(
							_mDependentContext,
							EnterpriseFormTypeListActivity.class);

					// put the new approve application submit contact bean to
					// extra data map as param
					_enterpriseFormTypeIntent
							.putExtra(
									NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACTS,
									_mDefaultSubmitContact);

					// start enterprise form type list activity
					_mDependentContext.startActivity(_enterpriseFormTypeIntent);
				}
			}

			// close cursor
			_cursor.close();
		}
	}

	// dismiss generate new approve application loading enterprise form process
	// dialog
	private void doneLoadingEnterpriseForm() {
		// check and dismiss generate new approve application loading enterprise
		// form process dialog
		if (null != _mGenNAALoadingEnterpriseFormProgDlg) {
			_mGenNAALoadingEnterpriseFormProgDlg.dismiss();
		}
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
			_mDependentContext.getApplicationContext().unbindService(
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

	// enterprise form type broadcast receiver
	class EnterpriseFormTypeBroadcastReceiver extends
			EnterpriseFormBroadcastReceiver {

		@Override
		public void onEnterpriseFormTypeChange() {
			// done loading enterprise form
			doneLoadingEnterpriseForm();

			// get local storage enterprise form type count, first type id and
			// name
			Cursor _cursor = _mDependentContext.getContentResolver()
					.query(ContentUris.withAppendedId(
							FormType.ENTERPRISE_CONTENT_URI, IAUserExtension
									.getUserLoginEnterpriseId(UserManager
											.getInstance().getUser())),
							new String[] { FormType._COUNT_PROJECTION,
									FormType.TYPE_ID, FormType.NAME }, null,
							null, null);

			// check cursor
			if (null != _cursor) {
				while (_cursor.moveToNext()) {
					// get and check form types count
					long _formTypeCount = _cursor.getLong(_cursor
							.getColumnIndex(FormType._COUNT));
					if (0 == _formTypeCount) {
						Log.e(LOG_TAG,
								"There is no user login enterprise form type");

						Toast.makeText(_mDependentContext,
								R.string.toast_no_enterpriseForm,
								Toast.LENGTH_SHORT).show();
					} else if (1 == _formTypeCount) {
						// goto enterprise form list activity
						// define the enterprise form intent
						Intent _enterpriseFormIntent = new Intent(
								_mDependentContext,
								EnterpriseFormListActivity.class);

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
										NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACTS,
										_mDefaultSubmitContact);

						// start enterprise form list activity
						_mDependentContext.startActivity(_enterpriseFormIntent);
					} else {
						// goto enterprise form type list activity
						// define the enterprise form type intent
						Intent _enterpriseFormTypeIntent = new Intent(
								_mDependentContext,
								EnterpriseFormTypeListActivity.class);

						// put the new approve application submit contact
						// bean to extra data map as param
						_enterpriseFormTypeIntent
								.putExtra(
										NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACTS,
										_mDefaultSubmitContact);

						// start enterprise form type list activity
						_mDependentContext
								.startActivity(_enterpriseFormTypeIntent);
					}
				}

				// close cursor
				_cursor.close();
			}

			// release enterprise form type broadcast receiver
			if (null != _mEnterpriseFormTypeBroadcastReceiver) {
				_mDependentContext
						.unregisterReceiver(_mEnterpriseFormTypeBroadcastReceiver);

				_mEnterpriseFormTypeBroadcastReceiver = null;
			}
		}

		@Override
		public void onEnterpriseFormItemChange(Long formTypeId, Long formId) {
			// nothing to do
		}

	}

}
