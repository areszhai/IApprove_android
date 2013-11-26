package com.futuo.iapprove.account;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONArray;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.AccountLoginFormItem;
import com.futuo.iapprove.customwidget.AccountLoginFormItem.AccountLoginFormItemInputEditTextTextWatcher;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.richitec.commontoolkit.customcomponent.BarButtonItem;
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
					R.drawable.confirm_login_button_title_textcolor));

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

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// get http response entity string json data
			JSONArray _respJsonData = JSONUtils.toJSONArray(HttpUtils
					.getHttpResponseEntityString(response));

			Log.d(LOG_TAG,
					"Send account login post http request successful, response json data = "
							+ _respJsonData);

			//
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			Log.e(LOG_TAG, "Send account login post http request failed");

			//
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
