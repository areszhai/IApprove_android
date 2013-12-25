package com.futuo.iapprove.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.utils.HttpRequestParamUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class GetEnterpriseABTask extends CoreServiceTask {

	private static final String LOG_TAG = GetEnterpriseABTask.class
			.getCanonicalName();

	// get enterprise address book post http request parameter
	private Map<String, String> _mGetEnterpriseABPostHttpReqParam;

	@Override
	protected void enterpriseChanged() {
		Log.d(LOG_TAG, "Query enterprise address book contacts count");

		// query enterprise address book contacts count in work thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				// get local storage address book contacts count
				Cursor _cursor = _mContentResolver.query(ContentUris
						.withAppendedId(Employee.ENTERPRISE_CONTENT_URI,
								_mEnterpriseId),
						new String[] { Employee._COUNT_PROJECTION }, null,
						null, null);

				// check cursor
				if (null != _cursor) {
					// get and check employee address book contacts count
					while (_cursor.moveToNext()) {
						if (0 == _cursor.getLong(_cursor
								.getColumnIndex(Employee._COUNT))) {
							// get enterprise address book immediately, not
							// update the next scheduled time
							execute();
						} else {
							Log.d(LOG_TAG,
									"Not need to get enterprise address book contact immediately, wait for next timer task period");
						}
					}

					// close cursor
					_cursor.close();
				}
			}

		}).start();
	}

	@Override
	protected void execute() {
		// send get enterprise address book post http request
		HttpUtils.postRequest(
				_mContext.getResources().getString(R.string.server_url)
						+ _mContext.getResources().getString(
								R.string.get_enterpriseAB_url),
				PostRequestFormat.URLENCODED,
				generateGetEnterpriseABPostHttpReqParam(), null,
				HttpRequestType.ASYNCHRONOUS,
				new GetEnterpriseABPostHttpRequestListener());
	}

	// generate get enterprise address book post http request param
	private Map<String, String> generateGetEnterpriseABPostHttpReqParam() {
		// check get enterprise address book post http request parameter
		if (null == _mGetEnterpriseABPostHttpReqParam) {
			_mGetEnterpriseABPostHttpReqParam = HttpRequestParamUtils
					.genUserSigHttpReqParam();

			// put get enterprise address book action in
			_mGetEnterpriseABPostHttpReqParam.put(
					_mContext.getResources().getString(
							R.string.rbgServer_commonReqParam_action),
					_mContext.getResources().getString(
							R.string.rbgServer_getEnterpriseABReqParam_action));
		}

		// put get enterprise address book user enterprise id in
		_mGetEnterpriseABPostHttpReqParam.put(
				_mContext.getResources().getString(
						R.string.rbgServer_getIApproveReqParam_enterpriseId),
				StringUtils.base64Encode(_mEnterpriseId.toString()));

		return _mGetEnterpriseABPostHttpReqParam;
	}

	// get local storage enterprise address book contacts user id as key and
	// bean as value map
	private Map<Long, ABContactBean> getLocalStorageEABContactsUserIdAndBeanMap(
			Cursor cursor) {
		Map<Long, ABContactBean> _localStorageEABContactsUserIdAndBeanMap = new HashMap<Long, ABContactBean>();

		// check the cursor
		if (null != cursor) {
			// set all local storage enterprise address book contact for
			// deleting
			while (cursor.moveToNext()) {
				// get for deleting address book contact
				ABContactBean _4deletingABContact = new ABContactBean(cursor);

				// set it for deleting
				_4deletingABContact
						.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

				// put address book contact use id and bean in
				_localStorageEABContactsUserIdAndBeanMap.put(
						_4deletingABContact.getUserId(), _4deletingABContact);
			}

			// close cursor
			cursor.close();
		}

		return _localStorageEABContactsUserIdAndBeanMap;
	}

	// inner class
	// get enterprise address book post http request listener
	class GetEnterpriseABPostHttpRequestListener extends OnHttpRequestListener {

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
						_mContext.getResources().getString(
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
				final JSONArray _enterpriseABJsonArray = JSONUtils
						.toJSONArray(_respEntityString);

				if (null != _enterpriseABJsonArray) {
					Log.d(LOG_TAG, "Get enterprise address book successful");

					// process enterprise address book contacts in work thread
					new Thread(new Runnable() {

						@Override
						public void run() {
							// get local storage enterprise address book
							// contacts user id as key and bean as value map
							Map<Long, ABContactBean> _localStorageEABContactsUserIdAndBeanMap = getLocalStorageEABContactsUserIdAndBeanMap(_mContentResolver
									.query(ContentUris.withAppendedId(
											Employee.ENTERPRISE_CONTENT_URI,
											_mEnterpriseId), null, null, null,
											null));

							// define the employee content values
							ContentValues _employeeContentValues = new ContentValues();

							for (int i = 0; i < _enterpriseABJsonArray.length(); i++) {
								// get enterprise each employee
								ABContactBean _employee = new ABContactBean(
										JSONUtils.getJSONObjectFromJSONArray(
												_enterpriseABJsonArray, i));

								// generate the employee content values with
								// employee user id, avatar, name, sex,
								// nickname, birthday, department and note
								_employeeContentValues.put(Employee.USER_ID,
										_employee.getUserId());
								_employeeContentValues.put(Employee.AVATAR,
										_employee.getAvatar());
								_employeeContentValues.put(Employee.NAME,
										_employee.getEmployeeName());
								_employeeContentValues.put(Employee.SEX,
										null != _employee.getSex() ? _employee
												.getSex().getValue() : null);
								_employeeContentValues.put(Employee.NICKNAME,
										_employee.getNickname());
								_employeeContentValues.put(Employee.BIRTHDAY,
										_employee.getBirthday());
								_employeeContentValues.put(Employee.DEPARTMENT,
										_employee.getDepartment());
								_employeeContentValues.put(
										Employee.APPROVE_NUMBER,
										_employee.getApproveNumber());
								_employeeContentValues.put(Employee.NOTE,
										_employee.getNote());

								// append mobile, office phone and email
								_employeeContentValues.put(
										Employee.MOBILE_PHONE,
										_employee.getMobilePhoneNumber());
								_employeeContentValues.put(
										Employee.OFFICE_PHONE,
										_employee.getOfficePhoneNumber());
								_employeeContentValues.put(Employee.EMAIL,
										_employee.getEmail());

								// append employee enterprise id
								_employeeContentValues.put(
										Employee.ENTERPRISE_ID, _mEnterpriseId);

								// insert the employee if not existed in local
								// storage database or update if existed
								if (!_localStorageEABContactsUserIdAndBeanMap
										.keySet().contains(
												_employee.getUserId())) {
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
									ABContactBean _4updatingABContact = _localStorageEABContactsUserIdAndBeanMap
											.get(_employee.getUserId());

									// update local storage enterprise address
									// book contact normal
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

										_mContentResolver.update(
												ContentUris
														.withAppendedId(
																Employee.EMPLOYEE_CONTENT_URI,
																_4updatingABContact
																		.getRowId()),
												_employeeContentValues, null,
												null);
									}
								}
							}

							// delete the local storage enterprise address book
							// contact for synchronizing
							for (Long _localStorageEABContactUseId : _localStorageEABContactsUserIdAndBeanMap
									.keySet()) {
								// get the for deleting address book contact
								ABContactBean _4deletingABContact = _localStorageEABContactsUserIdAndBeanMap
										.get(_localStorageEABContactUseId);

								// check its data dirty type
								if (LocalStorageDataDirtyType.DELETE == _4deletingABContact
										.getLocalStorageDataDirtyType()) {
									Log.d(LOG_TAG,
											"The employee whose id = "
													+ _4deletingABContact
															.getUserId()
													+ " will delete from local storage database");

									_mContentResolver.delete(
											ContentUris
													.withAppendedId(
															Employee.EMPLOYEE_CONTENT_URI,
															_4deletingABContact
																	.getRowId()),
											null, null);
								}
							}
						}

					}).start();
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
