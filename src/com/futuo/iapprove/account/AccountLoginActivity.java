package com.futuo.iapprove.account;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.account.user.IAUserLocalStorageAttributes;
import com.futuo.iapprove.account.user.UserEnterpriseProfileBean;
import com.futuo.iapprove.customwidget.AccountLoginFormItem;
import com.futuo.iapprove.customwidget.AccountLoginFormItem.AccountLoginFormItemInputEditTextTextWatcher;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.tab7tabcontent.IApproveTabActivity.IApproveTabExtraData;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.richitec.commontoolkit.customcomponent.BarButtonItem;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class AccountLoginActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = AccountLoginActivity.class
			.getCanonicalName();

	// account confirm login bar button item
	private BarButtonItem _mConfirmLoginBarButtonItem;

	// account login user phone and password item
	private AccountLoginFormItem _mAccountLoginUserPhoneItem;
	private AccountLoginFormItem _mAccountLoginUserPwdItem;

	// input method manager
	private InputMethodManager _mInputMethodManager;

	// account login asynchronous post http request progress dialog
	private ProgressDialog _mAccountLoginAsyncHttpReqProgDlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.account_login_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// check the data
		if (null != _extraData) {
			// get account login user phone
			String _accountLoginUserPhone = _extraData
					.getString(AccountLoginExtraData.ACCOUNT_LOGIN_USER_PHONE);

			// check account login user phone
			if (null != _accountLoginUserPhone) {
				// get account login user phone item
				_mAccountLoginUserPhoneItem = (AccountLoginFormItem) findViewById(R.id.al_userLoginPhone);

				// set account login user phone item input editText text
				_mAccountLoginUserPhoneItem
						.setInputEditText(_accountLoginUserPhone);
			}
		}

		// set subViews
		// set title
		setTitle(R.string.account_login_nav_title_text);

		// init account confirm login bar button item and set it as right bar
		// button item
		setRightBarButtonItem(_mConfirmLoginBarButtonItem = new AccountConfirmLoginBarButtonItem(
				this));

		// get account login user phone and password item
		if (null == _mAccountLoginUserPhoneItem) {
			_mAccountLoginUserPhoneItem = (AccountLoginFormItem) findViewById(R.id.al_userLoginPhone);
		}
		_mAccountLoginUserPwdItem = (AccountLoginFormItem) findViewById(R.id.al_userLoginPwd);

		// add account login user phone and password editText text changed
		// watcher
		AccountLoginUserPhone7PwdEditTextTextWatch _accountLoginUserPhone7PwdEditTextTextWatcher = new AccountLoginUserPhone7PwdEditTextTextWatch();
		_mAccountLoginUserPhoneItem
				.addTextChangedListener(_accountLoginUserPhone7PwdEditTextTextWatcher);
		_mAccountLoginUserPwdItem
				.addTextChangedListener(_accountLoginUserPhone7PwdEditTextTextWatcher);

		// bind forget password button on click listener
		((Button) findViewById(R.id.al_forgetPwd_button))
				.setOnClickListener(new ForgetPwdBtnOnClickListener());
	}

	@Override
	protected void onResume() {
		super.onResume();

		// set account login user phone item as focus
		_mAccountLoginUserPhoneItem.setAsFocus();
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

	// set soft input visibility
	private void setSoftInputVisibility(boolean visible) {
		// check and initialize input method manager
		if (null == _mInputMethodManager) {
			_mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		}

		// check visible
		if (visible) {
			// show soft input
			_mInputMethodManager.toggleSoftInputFromWindow(
					_mConfirmLoginBarButtonItem.getWindowToken(), 0,
					InputMethodManager.HIDE_NOT_ALWAYS);
		} else {
			// hide soft input
			_mInputMethodManager.hideSoftInputFromWindow(
					_mConfirmLoginBarButtonItem.getWindowToken(), 0);
		}
	}

	// inner class
	// account login extra data constant
	public static final class AccountLoginExtraData {

		// account login user phone
		public static final String ACCOUNT_LOGIN_USER_PHONE = "account_login_user_phone";

	}

	// account confirm login bar button item
	class AccountConfirmLoginBarButtonItem extends BarButtonItem {

		public AccountConfirmLoginBarButtonItem(Context context) {
			super(context, R.string.al_confirmLogin_button_title,
					R.drawable.img_confirm_login_button_normal_bg,
					R.drawable.img_confirm_login_button_pressed_bg,
					R.drawable.img_confirm_login_button_disable_bg,
					new AccountConfirmLoginBtnOnClickListener());

			// set text color
			setTextColor(getResources().getColorStateList(
					R.color.confirm_login_button_title_textcolor));

			// set text size
			setTextSize(15.0f);

			// set it disable first
			setEnabled(false);
		}

	}

	// account login user phone and password editText text changed watcher
	class AccountLoginUserPhone7PwdEditTextTextWatch implements
			AccountLoginFormItemInputEditTextTextWatcher {

		@Override
		public void afterTextChanged(AccountLoginFormItem accountLoginFormItem,
				Editable s) {
			// check editable
			if (null == s || "".equalsIgnoreCase(s.toString())) {
				_mConfirmLoginBarButtonItem.setEnabled(false);
			} else {
				// get and check account login form item another input editText
				// text
				String _anotherAccountLoginFormItemInputEditTextText = (_mAccountLoginUserPhoneItem == accountLoginFormItem ? _mAccountLoginUserPwdItem
						: _mAccountLoginUserPhoneItem).getInputEditText();
				if (null == _anotherAccountLoginFormItemInputEditTextText
						|| "".equalsIgnoreCase(_anotherAccountLoginFormItemInputEditTextText)) {
					_mConfirmLoginBarButtonItem.setEnabled(false);
				} else {
					_mConfirmLoginBarButtonItem.setEnabled(true);
				}
			}
		}

		@Override
		public void beforeTextChanged(
				AccountLoginFormItem accountLoginFormItem, CharSequence s,
				int start, int count, int after) {
			// nothing to do
		}

		@Override
		public void onTextChanged(AccountLoginFormItem accountLoginFormItem,
				CharSequence s, int start, int before, int count) {
			// nothing to do
		}

	}

	// account confirm login button on click listener
	class AccountConfirmLoginBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// account login
			// hide soft input
			setSoftInputVisibility(false);

			// show account login asynchronous post http request process dialog
			_mAccountLoginAsyncHttpReqProgDlg = ProgressDialog.show(
					AccountLoginActivity.this, null,
					getString(R.string.al_loginAsyncHttpReq_progDlg_message),
					true);

			// get account login user phone and password
			String _userLoginPhone = _mAccountLoginUserPhoneItem
					.getInputEditText();
			String _userLoginPwd = _mAccountLoginUserPwdItem.getInputEditText();

			Log.d(LOG_TAG, "Account login, login user phone = "
					+ _userLoginPhone + " and password = " + _userLoginPwd);

			// generate account login post http request param
			Map<String, String> _accountLoginPostHttpReqParam = new HashMap<String, String>();

			// put account login action, phone and password in
			_accountLoginPostHttpReqParam.put(
					getResources().getString(
							R.string.rbgServer_commonReqParam_action),
					getResources().getString(
							R.string.rbgServer_accountLoginReqParam_action));
			_accountLoginPostHttpReqParam.put(
					getResources().getString(
							R.string.rbgServer_accountLoginReqParam_phone),
					StringUtils.base64Encode(_userLoginPhone));
			_accountLoginPostHttpReqParam.put(
					getResources().getString(
							R.string.rbgServer_accountLoginReqParam_password),
					StringUtils.base64Encode(_userLoginPwd));

			// send account login post http request
			HttpUtils.postRequest(getResources().getString(R.string.server_url)
					+ getResources().getString(R.string.account_login_url),
					PostRequestFormat.URLENCODED,
					_accountLoginPostHttpReqParam, null,
					HttpRequestType.ASYNCHRONOUS,
					new AccountLoginPostHttpRequestListener());
		}

	}

	// account login post http request listener
	class AccountLoginPostHttpRequestListener extends OnHttpRequestListener {

		// done account login
		private void doneAccountLogin() {
			// check and dismiss account login asynchronous post http request
			// process dialog
			if (null != _mAccountLoginAsyncHttpReqProgDlg) {
				_mAccountLoginAsyncHttpReqProgDlg.dismiss();
			}
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// done account login
			doneAccountLogin();

			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send account login post http request successful, response entity string = "
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
					Log.d(LOG_TAG, "Account login successful");

					// get account login user phone and password base64 encode
					// string
					String _accountLoginUserPhone = _mAccountLoginUserPhoneItem
							.getInputEditText();
					String _accountLoginUserPwdBase64Encode = StringUtils
							.base64Encode(_mAccountLoginUserPwdItem
									.getInputEditText());

					// generate account login user bean with username and
					// userkey
					UserBean _loginUserBean = new UserBean(
							_accountLoginUserPhone, null,
							_accountLoginUserPwdBase64Encode);

					// get user last login enterprise id from local storage as
					// default and put to login user extension
					Long _userLastLoginEnterpriseId = DataStorageUtils
							.getLong(IAUserLocalStorageAttributes.USER_LASTLOGINENTERPRISEID
									.name());
					IAUserExtension.setUserLoginEnterpriseId(_loginUserBean,
							_userLastLoginEnterpriseId);

					// add account login user bean to user manager
					UserManager.getInstance().setUser(_loginUserBean);

					// process user enterprise profile
					UserEnterpriseProfileBean
							.processUserEnterpriseProfile(_respJsonDataArray);

					// save account login user name and key to local storage
					DataStorageUtils.putObject(
							IAUserLocalStorageAttributes.USER_LOGINNAME.name(),
							_accountLoginUserPhone);
					DataStorageUtils.putObject(
							IAUserLocalStorageAttributes.USER_LOGINKEY.name(),
							_accountLoginUserPwdBase64Encode);

					// define iApprove tab extra data map
					Map<String, Boolean> _extraMap = new HashMap<String, Boolean>();

					// put not need to get user enterprise profile flag to extra
					// data map as param
					_extraMap
							.put(IApproveTabExtraData.NOT_NEED2GET_USER_ENTERPRISEPROFILE_FLAG,
									true);

					// pop account login activity with result
					popActivityWithResult(RESULT_OK, _extraMap);
				} else {
					// show soft input
					setSoftInputVisibility(true);

					Log.e(LOG_TAG,
							"Account login failed, response entity unrecognized");

					Toast.makeText(AccountLoginActivity.this,
							R.string.toast_requestResp_unrecognized,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				// show soft input
				setSoftInputVisibility(true);

				// get and check error message
				String _errorMsg = JSONUtils.getStringFromJSONObject(
						_respJsonData,
						getResources().getString(
								R.string.rbgServer_commonReqResp_error));

				if (null != _errorMsg) {
					Log.e(LOG_TAG,
							"Account login failed, response error message = "
									+ _errorMsg);

					Toast.makeText(AccountLoginActivity.this, _errorMsg,
							Toast.LENGTH_SHORT).show();
				} else {
					Log.e(LOG_TAG,
							"Account login failed, response error message unrecognized");

					Toast.makeText(AccountLoginActivity.this,
							R.string.toast_requestResp_unrecognized,
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			// done account login
			doneAccountLogin();

			// show soft input
			setSoftInputVisibility(true);

			Log.e(LOG_TAG, "Send account login post http request failed");

			Toast.makeText(AccountLoginActivity.this,
					R.string.toast_request_exception, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onTimeout(HttpRequest request) {
			// done account login
			doneAccountLogin();

			// show soft input
			setSoftInputVisibility(true);

			Log.e(LOG_TAG, "Send account login post http request timeout");

			Toast.makeText(AccountLoginActivity.this,
					R.string.toast_request_timeout, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onUnknownHost(HttpRequest request) {
			// done account login
			doneAccountLogin();

			// show soft input
			setSoftInputVisibility(true);

			Log.e(LOG_TAG, "Your network not reachability");

			Toast.makeText(AccountLoginActivity.this,
					R.string.toast_network_notReachability, Toast.LENGTH_SHORT)
					.show();
		}

	}

	// forget password button on click listener
	class ForgetPwdBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// go to retrieve password activity
			pushActivity(AccountRetrievePwdActivity.class);
		}

	}

}
