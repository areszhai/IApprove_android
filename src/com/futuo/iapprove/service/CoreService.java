package com.futuo.iapprove.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.addressbook.AddressbookContactBean;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.utils.HttpRequestParamUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class CoreService extends Service {

	private static final String LOG_TAG = CoreService.class.getCanonicalName();

	// core service local binder
	private IBinder _mLocalBinder;

	// core service work timer
	private final Timer CORESERVICE_WORK_TIMER = new Timer();

	// milliseconds per second
	private final Long MILLISECONDS_PER_SECOND = 1000L;

	// get all to-do list tasks, enterprise address book timer task
	private GetTodoListTimerTask _mGetAllTodoListTasksTimerTask;
	private GetEnterpriseAddressbookTimerTask _mGetEnterpriseAddressbookTimerTask;

	// content resolver
	private ContentResolver _mContentResolver;

	@Override
	public void onCreate() {
		super.onCreate();

		// initialize core service local binder and content resolver
		_mLocalBinder = new LocalBinder();
		_mContentResolver = getContentResolver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// start get all to-do list tasks timer task, get immediately and then
		// get again every 60s
		CORESERVICE_WORK_TIMER.schedule(
				_mGetAllTodoListTasksTimerTask = new GetTodoListTimerTask(), 0,
				60 * MILLISECONDS_PER_SECOND);

		// start get enterprise address book timer task, get immediately and
		// then get again every 120s
		CORESERVICE_WORK_TIMER
				.schedule(
						_mGetEnterpriseAddressbookTimerTask = new GetEnterpriseAddressbookTimerTask(),
						0, 120 * MILLISECONDS_PER_SECOND);

		Log.d(LOG_TAG,
				"Start get all to-do list tasks and enterprise address book timer");

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "Bind core service using local binder");

		return _mLocalBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);

		Log.d(LOG_TAG, "Rebind core service");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(LOG_TAG, "Unbind core service");

		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// free core service local binder
		_mLocalBinder = null;

		// check and cancel the get all to-do list, enterprise address book
		// tasks timer task
		if (null != _mGetAllTodoListTasksTimerTask) {
			_mGetAllTodoListTasksTimerTask.cancel();
		}
		if (null != _mGetEnterpriseAddressbookTimerTask) {
			_mGetEnterpriseAddressbookTimerTask.cancel();
		}

		// cancel work timer
		CORESERVICE_WORK_TIMER.cancel();
	}

	// start get enterprise address book with enterprise id
	public void startGetEnterpriseAddressbook(Long enterpriseId) {
		Log.d(LOG_TAG,
				"Start get enterprise address book with enterprise id = "
						+ enterpriseId);

		// check get enterprise address book timer task and set enterprise id
		if (null != _mGetEnterpriseAddressbookTimerTask) {
			_mGetEnterpriseAddressbookTimerTask.setEnterpriseId(enterpriseId);

			// run immediately, not update the next scheduled time
			_mGetEnterpriseAddressbookTimerTask.run();
		}
	}

	// stop get enterprise address book
	public void stopGetEnterpriseAddressbook() {
		Log.d(LOG_TAG, "Stop get enterprise address book");

		// check get enterprise address book timer task and clear enterprise id
		if (null != _mGetEnterpriseAddressbookTimerTask) {
			_mGetEnterpriseAddressbookTimerTask.clearEnterpriseId();
		}
	}

	// inner class
	// local binder
	public class LocalBinder extends Binder {

		// get service
		public CoreService getService() {
			return CoreService.this;
		}

	}

	// get all to-do list tasks timer task
	class GetTodoListTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

	}

	// get enterprise address book timer task
	class GetEnterpriseAddressbookTimerTask extends TimerTask {

		private final String LOG_TAG = GetEnterpriseAddressbookTimerTask.class
				.getCanonicalName();

		// enterprise id
		private Long _mEnterpriseId;

		// get enterprise address book post http request parameter
		private Map<String, String> _mGetEnterpriseABPostHttpReqParam = null;

		@Override
		public void run() {
			// check enterprise id
			if (null != _mEnterpriseId) {
				// send get enterprise address book post http request
				HttpUtils.postRequest(
						getResources().getString(R.string.server_url)
								+ getResources().getString(
										R.string.get_enterpriseAB_url),
						PostRequestFormat.URLENCODED,
						generateGetEnterpriseABPostHttpReqParam(), null,
						HttpRequestType.ASYNCHRONOUS,
						new GetEnterpriseABPostHttpRequestListener());
			} else {
				Log.d(LOG_TAG, "Get enterprise address book timer task idling");
			}
		}

		// set enterprise id
		public void setEnterpriseId(Long enterpriseId) {
			// check enterprise id
			if (null != enterpriseId) {
				_mEnterpriseId = enterpriseId;
			} else {
				Log.e(LOG_TAG,
						"Set the enterprise id for getting its address book error, enterprise id = "
								+ enterpriseId);
			}
		}

		// clear enterprise id
		public void clearEnterpriseId() {
			_mEnterpriseId = null;
		}

		// generate get enterprise address book post http request param
		private Map<String, String> generateGetEnterpriseABPostHttpReqParam() {
			// check get enterprise address book post http request parameter
			if (null == _mGetEnterpriseABPostHttpReqParam) {
				_mGetEnterpriseABPostHttpReqParam = HttpRequestParamUtils
						.genUserSigHttpReqParam();

				// put get enterprise address book action in
				_mGetEnterpriseABPostHttpReqParam
						.put(getResources().getString(
								R.string.rbgServer_commonReqParam_action),
								getResources()
										.getString(
												R.string.rbgServer_getEnterpriseABReqParam_action));
			}

			// put get enterprise address book user enterprise id in
			_mGetEnterpriseABPostHttpReqParam
					.put(getResources()
							.getString(
									R.string.rbgServer_getIApproveReqParam_enterpriseId),
							StringUtils.base64Encode(_mEnterpriseId.toString()));

			return _mGetEnterpriseABPostHttpReqParam;
		}

		// get local storage enterprise address book contacts user id as key and
		// bean as value map
		private Map<Long, AddressbookContactBean> getLocalStorageEABContactsUserIdAndBeanMap(
				Cursor cursor) {
			Map<Long, AddressbookContactBean> _localStorageEABContactsUserIdAndBeanMap = new HashMap<Long, AddressbookContactBean>();

			// set all local storage enterprise address book contact for
			// deleting
			while (cursor.moveToNext()) {
				// get for deleting address book contact
				AddressbookContactBean _4deletingABContact = new AddressbookContactBean(
						cursor);

				// set it for deleting
				_4deletingABContact
						.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

				// put address book contact use id and bean in
				_localStorageEABContactsUserIdAndBeanMap.put(
						_4deletingABContact.getUserId(), _4deletingABContact);
			}

			// close cursor
			cursor.close();

			return _localStorageEABContactsUserIdAndBeanMap;
		}

		// inner class
		// get enterprise address book post http request listener
		class GetEnterpriseABPostHttpRequestListener extends
				OnHttpRequestListener {

			@Override
			public void onFinished(HttpRequest request, HttpResponse response) {
				// get http response entity string
				String _respEntityString = HttpUtils
						.getHttpResponseEntityString(response);

				Log.d(LOG_TAG,
						"Send get enterprise address book post http request successful, response entity string = "
								+ _respEntityString);

				// get and check http response entity string error json data
				JSONObject _respJsonData = JSONUtils
						.toJSONObject(_respEntityString);

				if (null != _respJsonData) {
					// get and check error message
					String _errorMsg = JSONUtils.getStringFromJSONObject(
							_respJsonData,
							getResources().getString(
									R.string.rbgServer_commonReqResp_error));

					if (null != _errorMsg) {
						Log.e(LOG_TAG,
								"Get enterprise address book failed, response error message = "
										+ _errorMsg);
					} else {
						Log.e(LOG_TAG,
								"Get enterprise address book failed, response entity unrecognized");
					}
				} else {
					// get and check the enterprise address book
					JSONArray _enterpriseABJsonArray = JSONUtils
							.toJSONArray(_respEntityString);

					if (null != _enterpriseABJsonArray) {
						Log.d(LOG_TAG, "Get enterprise address book successful");

						// get local storage enterprise address book contacts
						// user id as key and bean as value map
						Map<Long, AddressbookContactBean> _localStorageEABContactsUserIdAndBeanMap = getLocalStorageEABContactsUserIdAndBeanMap(_mContentResolver
								.query(ContentUris.withAppendedId(
										Employee.ENTERPRISE_CONTENT_URI,
										_mEnterpriseId), null, null, null, null));

						// define the employee content values
						ContentValues _employeeContentValues = new ContentValues();

						for (int i = 0; i < _enterpriseABJsonArray.length(); i++) {
							// get enterprise each employee
							AddressbookContactBean _employee = new AddressbookContactBean(
									JSONUtils.getJSONObjectFromJSONArray(
											_enterpriseABJsonArray, i));

							// generate the employee content values
							// employee user id, avatar, name, sex, nickname,
							// birthday, department and note
							_employeeContentValues.put(Employee.USER_ID,
									_employee.getUserId());
							_employeeContentValues.put(Employee.AVATAR,
									_employee.getAvatar());
							_employeeContentValues.put(Employee.NAME,
									_employee.getEmployeeName());
							_employeeContentValues.put(Employee.SEX, _employee
									.getSex().getValue());
							_employeeContentValues.put(Employee.NICKNAME,
									_employee.getNickname());
							_employeeContentValues.put(Employee.BIRTHDAY,
									_employee.getBirthday());
							_employeeContentValues.put(Employee.DEPARTMENT,
									_employee.getDepartment());
							_employeeContentValues.put(Employee.NOTE,
									_employee.getNote());

							// mobile, office phone and email
							_employeeContentValues.put(Employee.MOBILEPHONE,
									_employee.getMobilePhoneNumber());
							_employeeContentValues.put(Employee.OFFICEPHONE,
									_employee.getOfficePhoneNumber());
							_employeeContentValues.put(Employee.EMAIL,
									_employee.getEmail());

							// employee enterprise id
							_employeeContentValues.put(Employee.ENTERPRISE_ID,
									_mEnterpriseId);

							// insert the employee if not existed in local
							// storage database or update if existed
							if (!_localStorageEABContactsUserIdAndBeanMap
									.keySet().contains(_employee.getUserId())) {
								Log.d(LOG_TAG,
										"The employee = "
												+ _employee
												+ " for inserting into local storage database, its content values = "
												+ _employeeContentValues);

								_mContentResolver.insert(
										Employee.EMPLOYEE_CONTENT_URI,
										_employeeContentValues);
							} else {
								// get for updating address book contact
								AddressbookContactBean _4updatingABContact = _localStorageEABContactsUserIdAndBeanMap
										.get(_employee.getUserId());

								// update local storage enterprise address book
								// contact normal
								_4updatingABContact
										.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.NORMAL);

								// compare the got employee with the for
								// updating address book contact
								if (0 != _employee
										.compareTo(_4updatingABContact)) {
									Log.d(LOG_TAG,
											"The employee whose id = "
													+ _4updatingABContact
															.getUserId()
													+ " for updating to local storage database, its content values = "
													+ _employeeContentValues);

									_mContentResolver
											.update(ContentUris
													.withAppendedId(
															Employee.EMPLOYEE_CONTENT_URI,
															_4updatingABContact
																	.getRowId()),
													_employeeContentValues,
													null, null);
								}
							}
						}

						// delete the local storage enterprise address book
						// contact for synchronizing
						for (Long _localStorageEABContactUseId : _localStorageEABContactsUserIdAndBeanMap
								.keySet()) {
							// get the for deleting address book contact
							AddressbookContactBean _4deletingABContact = _localStorageEABContactsUserIdAndBeanMap
									.get(_localStorageEABContactUseId);

							// check its data dirty type
							if (LocalStorageDataDirtyType.DELETE == _4deletingABContact
									.getLocalStorageDataDirtyType()) {
								Log.d(LOG_TAG,
										"The employee whose id = "
												+ _4deletingABContact
														.getUserId()
												+ " will delete from local storage database");

								_mContentResolver
										.delete(ContentUris.withAppendedId(
												Employee.EMPLOYEE_CONTENT_URI,
												_4deletingABContact.getRowId()),
												null, null);
							}
						}
					} else {
						Log.e(LOG_TAG,
								"Get enterprise address book failed, response entity unrecognized");
					}
				}
			}

			@Override
			public void onFailed(HttpRequest request, HttpResponse response) {
				Log.e(LOG_TAG,
						"Send get enterprise address book post http request failed");
			}

		}

	}

}
