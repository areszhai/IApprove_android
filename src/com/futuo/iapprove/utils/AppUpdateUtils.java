package com.futuo.iapprove.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;
import com.richitec.commontoolkit.utils.VersionUtils;
import com.richitec.commontoolkit.utils.VersionUtils.APPUPGRADEMODE;
import com.richitec.commontoolkit.utils.VersionUtils.VersionCompareException;

public class AppUpdateUtils {

	private static final String LOG_TAG = AppUpdateUtils.class
			.getCanonicalName();

	// check application current version and upgrade the application
	public static void upgradeApp(Context activityContext,
			APPUPGRADEMODE upgradeMode) {
		// check activity context
		if (activityContext instanceof Activity) {
			// get the application latest version
			// generate get the application latest version post http request
			// param
			Map<String, String> _getLatestVersionPostHttpReqParam = new HashMap<String, String>();

			// put get the application latest version action, device type,
			// current version and state in
			_getLatestVersionPostHttpReqParam
					.put(activityContext.getResources().getString(
							R.string.rbgServer_commonReqParam_action),
							activityContext
									.getResources()
									.getString(
											R.string.rbgServer_versionCheck4UpdateReqParam_action));
			_getLatestVersionPostHttpReqParam
					.put(activityContext
							.getResources()
							.getString(
									R.string.rbgServer_versionCheck4UpdateReqParam_deviceType),
							StringUtils
									.base64Encode(activityContext
											.getResources()
											.getString(
													R.string.rbgServer_versionCheck4UpdateReqParam_androidDeviceType)));
			_getLatestVersionPostHttpReqParam
					.put(activityContext
							.getResources()
							.getString(
									R.string.rbgServer_versionCheck4UpdateReqParam_version),
							StringUtils.base64Encode(VersionUtils.versionName()));
			_getLatestVersionPostHttpReqParam
					.put(activityContext
							.getResources()
							.getString(
									R.string.rbgServer_versionCheck4UpdateReqParam_state),
							StringUtils
									.base64Encode(activityContext
											.getResources()
											.getString(
													R.string.rbgServer_versionCheck4UpdateReqParam_currentState)));

			// send get the application latest version post http request
			HttpUtils.postRequest(
					activityContext.getResources().getString(
							R.string.server_url)
							+ activityContext.getResources().getString(
									R.string.check4update_url),
					PostRequestFormat.URLENCODED,
					_getLatestVersionPostHttpReqParam, null,
					HttpRequestType.ASYNCHRONOUS,
					new GetLatestVersionHttpRequestListener(activityContext,
							upgradeMode));
		} else {
			Log.e(LOG_TAG,
					"Unable to upgrade the application, because there is no activity context to builder upgrade alert dialog");
		}
	}

	public static void upgradeApp(Context activityContext) {
		upgradeApp(activityContext, APPUPGRADEMODE.AUTO);
	}

	// inner class
	// get the application latest version http request listener
	static class GetLatestVersionHttpRequestListener extends
			OnHttpRequestListener {

		// dependent activity context
		private Context _mDependentActivityContext;

		// application update mode
		private APPUPGRADEMODE _mUpgradeMode;

		// new version application download url
		private String _mNewVersionAppDownloadUrl;

		public GetLatestVersionHttpRequestListener(
				Context dependentActivityContext, APPUPGRADEMODE upgradeMode) {
			super();

			// save dependent activity context and application upgrade mode
			_mDependentActivityContext = dependentActivityContext;
			_mUpgradeMode = upgradeMode;
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send get applicate latest version post http request successful, response entity string = "
							+ _respEntityString);

			// get and check http response entity string error json data
			JSONObject _respJsonData = JSONUtils
					.toJSONObject(_respEntityString);

			// get and check error message
			String _errorMsg = JSONUtils.getStringFromJSONObject(
					_respJsonData,
					_mDependentActivityContext.getResources().getString(
							R.string.rbgServer_commonReqResp_error));

			if (null != _errorMsg) {
				Log.e(LOG_TAG,
						"Get application latest version failed, response error message = "
								+ _errorMsg);
			} else {
				Log.e(LOG_TAG, "Get application latest version successful");

				// get the application current version
				String _currentVersion = VersionUtils.versionName();

				// get and check the application latest version
				String _latestVersion = JSONUtils
						.getStringFromJSONObject(
								_respJsonData,
								_mDependentActivityContext
										.getResources()
										.getString(
												R.string.rbgServer_versionCheck4UpdateReqResp_latestVersion));
				try {
					if (0 <= VersionUtils.compareVersionName(_latestVersion,
							_currentVersion)) {
						// check the application upgrade mode
						if (APPUPGRADEMODE.MANUAL == _mUpgradeMode) {
							// show your application version is the latest toast
							// center
							Toast _latestVersionToast = Toast.makeText(
									_mDependentActivityContext, "当前已是最新版本",
									Toast.LENGTH_LONG);
							_latestVersionToast
									.setGravity(Gravity.CENTER, 0, 0);
							_latestVersionToast.show();
						}
					} else {
						// get the application new version description and
						// download url
						String _newVersionDescription = JSONUtils
								.getStringFromJSONObject(
										_respJsonData,
										_mDependentActivityContext
												.getResources()
												.getString(
														R.string.rbgServer_versionCheck4UpdateReqResp_latestVersionDescription));
						_mNewVersionAppDownloadUrl = JSONUtils
								.getStringFromJSONObject(
										_respJsonData,
										_mDependentActivityContext
												.getResources()
												.getString(
														R.string.rbgServer_versionCheck4UpdateReqResp_downloadUrl));

						// define a alertDialog builder
						final Builder ALERTDIALOG_BUILDER = new AlertDialog.Builder(
								_mDependentActivityContext);

						// show there is new version alter dialog
						ALERTDIALOG_BUILDER
								.setTitle(
										R.string.ct_appUpgrade6theLatestApp_alertDialog_title)
								.setMessage(
										_mDependentActivityContext
												.getResources()
												.getString(
														R.string.ct_appUpgrade_alertDialog_message)
												.replaceFirst("\\*\\*\\*",
														_latestVersion)
												.replace("***",
														_newVersionDescription))
								.setPositiveButton(
										R.string.ct_appUpgrade_alertDialog_upgradeButton_title,
										new ApplicationUpgradeButtonOnClickListener())
								.setNegativeButton(
										R.string.ct_appUpgrade_alertDialog_remindLaterButton_title,
										null).show();
					}
				} catch (VersionCompareException e) {
					Log.e(LOG_TAG, "Compare the application current version = "
							+ _currentVersion
							+ " with the server latest version = "
							+ _latestVersion + " error, exception message = "
							+ e.getMessage());

					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			// nothing to do
		}

		// inner class
		// application upgrade button on click listener
		class ApplicationUpgradeButtonOnClickListener implements
				OnClickListener {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// update the application upgrade info
				// generate update the application upgrade info post http
				// request param
				Map<String, String> _updateAppUpgradePostHttpReqParam = new HashMap<String, String>();

				// get login user
				UserBean _loginUser = UserManager.getInstance().getUser();

				// put update the application upgrade info action, user id,
				// login name and version in
				_updateAppUpgradePostHttpReqParam
						.put(_mDependentActivityContext
								.getResources()
								.getString(
										R.string.rbgServer_commonReqParam_action),
								_mDependentActivityContext
										.getResources()
										.getString(
												R.string.rbgServer_updateNewVersionDownloadInfoReqParam_action));
				_updateAppUpgradePostHttpReqParam
						.put(_mDependentActivityContext
								.getResources()
								.getString(
										R.string.rbgServer_updateNewVersionDownloadInfoReqParam_userId),
								StringUtils.base64Encode(IAUserExtension
										.getUserLoginUserId(_loginUser)
										.toString()));
				_updateAppUpgradePostHttpReqParam.put(
						_mDependentActivityContext.getResources().getString(
								R.string.rbgServer_userSigReqParam_userName),
						StringUtils.base64Encode(_loginUser.getName()));
				_updateAppUpgradePostHttpReqParam
						.put(_mDependentActivityContext
								.getResources()
								.getString(
										R.string.rbgServer_updateNewVersionDownloadInfoReqParam_version),
								StringUtils
										.base64Encode(_mDependentActivityContext
												.getResources()
												.getString(
														R.string.rbgServer_updateNewVersionDownloadInfoReqParam_currentVersion)));

				// send update the application upgrade info post http request
				HttpUtils
						.postRequest(
								_mDependentActivityContext.getResources()
										.getString(R.string.server_url)
										+ _mDependentActivityContext
												.getResources()
												.getString(
														R.string.updateVersionDownloadInfo),
								PostRequestFormat.URLENCODED,
								_updateAppUpgradePostHttpReqParam, null,
								HttpRequestType.ASYNCHRONOUS, null);

				// download the latest application from its version center
				_mDependentActivityContext.startActivity(new Intent(
						Intent.ACTION_VIEW, Uri
								.parse(_mNewVersionAppDownloadUrl)));
			}

		}

	}

}
