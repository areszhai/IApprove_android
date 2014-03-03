package com.futuo.iapprove.tab7tabcontent;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.account.user.UserEnterpriseProfileBean;
import com.futuo.iapprove.service.CoreService;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.richitec.commontoolkit.customcomponent.CTTabSpecIndicator;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

@SuppressWarnings("deprecation")
public class IApproveTabActivity extends TabActivity {

	private static final String LOG_TAG = IApproveTabActivity.class
			.getCanonicalName();

	// tab widget item and content class array, the to-do list, addressbook,
	// history record and settings
	private final Object[][] TAB_WIDGETITEMS7CONTENTCLS = new Object[][] {
			{ R.string.todoList_tab_tag, R.string.todoList_tab_title,
					R.drawable.todo_tab_icon, TodoListTabContentActivity.class },
			{ R.string.addressbook_tab_tag, R.string.addressbook_tab7nav_title,
					R.drawable.addressbook_tab_icon,
					AddressbookTabContentActivity.class },
			{ R.string.historyRecord_tab_tag, R.string.historyRecord_tab_title,
					R.drawable.historyrecord_tab_icon,
					HistoryRecordTabContentActivity.class },
			{ R.string.settings_tab_tag, R.string.settings_tab7nav_title,
					R.drawable.settings_tab_icon,
					SettingsTabContentActivity.class } };

	// tab host
	private TabHost _mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.iapprove_tab_activity_layout);

		// define need to get user enterprise profile flag
		Boolean _need2GetUserEnterpriseProfile = true;

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// check the data
		if (null != _extraData) {
			// get not need to get user enterprise profile flag
			Boolean _notNeed2GetUserEnterpriseProfileFlag = _extraData
					.getBoolean(IApproveTabExtraData.NOT_NEED2GET_USER_ENTERPRISEPROFILE_FLAG);

			// check account login user phone
			if (null != _notNeed2GetUserEnterpriseProfileFlag
					&& true == _notNeed2GetUserEnterpriseProfileFlag) {
				// not need to get user enterprise profile
				_need2GetUserEnterpriseProfile = false;
			}
		}

		// check if or not need to get user enterprise profile
		if (_need2GetUserEnterpriseProfile) {
			// get user enterprise profile
			// get login user
			UserBean _loginUser = UserManager.getInstance().getUser();

			// generate get user enterprise profile post http request param
			Map<String, String> _getUserEnterpriseProfilePostHttpReqParam = new HashMap<String, String>();

			// put get user enterprise profile action, user name and key in
			_getUserEnterpriseProfilePostHttpReqParam.put(
					getResources().getString(
							R.string.rbgServer_commonReqParam_action),
					getResources().getString(
							R.string.rbgServer_accountLoginReqParam_action));
			_getUserEnterpriseProfilePostHttpReqParam.put(getResources()
					.getString(R.string.rbgServer_accountLoginReqParam_phone),
					StringUtils.base64Encode(_loginUser.getName()));
			_getUserEnterpriseProfilePostHttpReqParam.put(
					getResources().getString(
							R.string.rbgServer_accountLoginReqParam_password),
					_loginUser.getUserKey());

			// send get user enterprise profile post http request
			HttpUtils.postRequest(getResources().getString(R.string.server_url)
					+ getResources().getString(R.string.account_login_url),
					PostRequestFormat.URLENCODED,
					_getUserEnterpriseProfilePostHttpReqParam, null,
					HttpRequestType.ASYNCHRONOUS,
					new GetUserEnterpriseProfilePostHttpRequestListener());
		}

		// set subViews
		// get tabHost
		_mTabHost = getTabHost();

		// set tab indicator and content
		for (int i = 0; i < TAB_WIDGETITEMS7CONTENTCLS.length; i++) {
			try {
				// get tab spec tag, indicator label, icon resource and content
				// class
				// tag
				String _tag = getResources().getString(
						(Integer) TAB_WIDGETITEMS7CONTENTCLS[i][0]);

				// label resource
				Integer _labelRes = (Integer) TAB_WIDGETITEMS7CONTENTCLS[i][1];

				// icon resource
				Integer _iconRes = (Integer) TAB_WIDGETITEMS7CONTENTCLS[i][2];

				// content class
				Class<?> _contentCls = (Class<?>) TAB_WIDGETITEMS7CONTENTCLS[i][3];

				// new tab spec and add to tab host
				TabSpec _tabSpec = _mTabHost
						.newTabSpec(_tag)
						.setIndicator(
								new CTTabSpecIndicator(this, _labelRes,
										_iconRes))
						.setContent(new Intent().setClass(this, _contentCls));

				_mTabHost.addTab(_tabSpec);
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"New tab spec error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		// start core service
		startService(new Intent(this, CoreService.class));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// stop core service
		stopService(new Intent(this, CoreService.class));
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		AppDataSaveRestoreUtils.onRestoreInstanceState(savedInstanceState);

		int currentTabIndex = savedInstanceState.getInt("current_tab");
		if (currentTabIndex != 0) {
			super.onRestoreInstanceState(savedInstanceState);
		} else {
			TabHost tabHost = getTabHost();
			tabHost.setCurrentTab(currentTabIndex);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtils.onSaveInstanceState(outState);

		super.onSaveInstanceState(outState);
		TabHost tabHost = getTabHost();
		int currentTabIndex = tabHost.getCurrentTab();
		outState.putInt("current_tab", currentTabIndex);
	}

	// inner class
	// iApprove tab extra data constant
	public static final class IApproveTabExtraData {

		// not need to get user enterprise profile flag
		public static final String NOT_NEED2GET_USER_ENTERPRISEPROFILE_FLAG = "not_need_to_get_user_enterpriseprofile_flag";

	}

	// get user enterprise profile post http request listener
	class GetUserEnterpriseProfilePostHttpRequestListener extends
			OnHttpRequestListener {

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send get user enterprise profile post http request successful, response entity string = "
							+ _respEntityString);

			// get and check http response entity string error json data
			JSONObject _respJsonData = JSONUtils
					.toJSONObject(_respEntityString);

			if (null == _respJsonData) {
				// get and check http response entity string error json data
				// again
				JSONArray _respJsonDataArray = JSONUtils
						.toJSONArray(_respEntityString);

				if (null != _respJsonDataArray) {
					Log.d(LOG_TAG, "Get user enterprise profile successful");

					// process user enterprise profile
					UserEnterpriseProfileBean
							.processUserEnterpriseProfile(_respJsonDataArray);

					// get login user
					UserBean _loginUser = UserManager.getInstance().getUser();

					// update account login info
					// get location
					// define my location latitude and longitude
					double _myLocationLatitude = 0, _myLocationLongitude = 0;

					LocationManager _locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					if (_locationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						Location location = _locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							_myLocationLatitude = location.getLatitude();
							_myLocationLongitude = location.getLongitude();
						}
					} else {
						Location location = _locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							_myLocationLatitude = location.getLatitude();
							_myLocationLongitude = location.getLongitude();
						}
					}

					// generate update account login info post http request
					// param
					Map<String, String> _updateAccountLoginInfoPostHttpReqParam = new HashMap<String, String>();

					// put update account login info action, user id, login
					// name, longitude, latitude, device name, platform and
					// version in
					_updateAccountLoginInfoPostHttpReqParam
							.put(getResources().getString(
									R.string.rbgServer_commonReqParam_action),
									getResources()
											.getString(
													R.string.rbgServer_updateAccountLoginInfoReqParam_action));
					_updateAccountLoginInfoPostHttpReqParam
							.put(getResources()
									.getString(
											R.string.rbgServer_userSigReqParam_userName),
									StringUtils.base64Encode(_loginUser
											.getName()));
					_updateAccountLoginInfoPostHttpReqParam
							.put(getResources()
									.getString(
											R.string.rbgServer_updateAccountLoginInfoReqParam_userId),
									StringUtils.base64Encode(IAUserExtension
											.getUserLoginUserId(_loginUser)
											.toString()));
					_updateAccountLoginInfoPostHttpReqParam
							.put(getResources()
									.getString(
											R.string.rbgServer_updateAccountLoginInfoReqParam_latitude),
									StringUtils.base64Encode(Double.valueOf(
											_myLocationLongitude).toString()));
					_updateAccountLoginInfoPostHttpReqParam
							.put(getResources()
									.getString(
											R.string.rbgServer_updateAccountLoginInfoReqParam_longitude),
									StringUtils.base64Encode(Double.valueOf(
											_myLocationLatitude).toString()));
					_updateAccountLoginInfoPostHttpReqParam
							.put(getResources()
									.getString(
											R.string.rbgServer_updateAccountLoginInfoReqParam_deviceName),
									StringUtils.base64Encode(Build.BRAND));
					_updateAccountLoginInfoPostHttpReqParam
							.put(getResources()
									.getString(
											R.string.rbgServer_updateAccountLoginInfoReqParam_devicePlatform),
									StringUtils.base64Encode("Android"));
					_updateAccountLoginInfoPostHttpReqParam
							.put(getResources()
									.getString(
											R.string.rbgServer_updateAccountLoginInfoReqParam_deviceVersion),
									StringUtils
											.base64Encode(Build.VERSION.RELEASE));

					// send account login post http request
					HttpUtils.postRequest(
							getResources().getString(R.string.server_url)
									+ getResources().getString(
											R.string.updateAccountLoginInfo),
							PostRequestFormat.URLENCODED,
							_updateAccountLoginInfoPostHttpReqParam, null,
							HttpRequestType.ASYNCHRONOUS, null);
				}
			} else {
				// get and check error message
				String _errorMsg = JSONUtils.getStringFromJSONObject(
						_respJsonData,
						getResources().getString(
								R.string.rbgServer_commonReqResp_error));

				if (null != _errorMsg) {
					Log.e(LOG_TAG,
							"Get user enterprise profile failed, response error message = "
									+ _errorMsg);
				} else {
					Log.e(LOG_TAG,
							"Get user enterprise profile failed, response error message unrecognized");
				}
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			Log.e(LOG_TAG,
					"Send get user enterprise profile post http request failed");
		}

	}

}
