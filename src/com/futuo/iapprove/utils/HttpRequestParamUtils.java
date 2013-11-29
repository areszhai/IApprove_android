package com.futuo.iapprove.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.StringUtils;

public class HttpRequestParamUtils {

	// generate user signature http request parameter
	public static Map<String, String> genUserSigHttpReqParam() {
		Map<String, String> _userSigHttpReqParam = new HashMap<String, String>();

		// get application context
		Context _appContext = CTApplication.getContext();

		// get login user info
		UserBean _loginUser = UserManager.getInstance().getUser();

		// put login user name and key in
		_userSigHttpReqParam.put(
				_appContext.getResources().getString(
						R.string.rbgServer_userSigReqParam_userName),
				StringUtils.base64Encode(_loginUser.getName()));
		_userSigHttpReqParam.put(
				_appContext.getResources().getString(
						R.string.rbgServer_userSigReqParam_userKey),
				_loginUser.getUserKey());

		return _userSigHttpReqParam;
	}

}
