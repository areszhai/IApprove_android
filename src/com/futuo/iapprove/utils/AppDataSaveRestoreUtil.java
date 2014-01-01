package com.futuo.iapprove.utils;

import android.os.Bundle;

import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.account.user.IAUserLocalStorageAttributes;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;

public class AppDataSaveRestoreUtil {
	public static void onSaveInstanceState(Bundle outState) {
		UserBean user = UserManager.getInstance().getUser();
		outState.putString(IAUserLocalStorageAttributes.USER_LOGINNAME.name(),
				user.getName());
	}

	public static void onRestoreInstanceState(Bundle savedInstanceState) {
		String userName = savedInstanceState
				.getString(IAUserLocalStorageAttributes.USER_LOGINNAME.name());

		UserBean user = UserManager.getInstance().getUser();
		if (userName == null || userName.equals("")) {
		} else if (user.getName() == null || user.getName().equals("")) {
			loadAccount();
		}

	}

	public static void loadAccount() {
		String userName = DataStorageUtils
				.getString(IAUserLocalStorageAttributes.USER_LOGINNAME.name());
		String userkey = DataStorageUtils
				.getString(IAUserLocalStorageAttributes.USER_LOGINKEY.name());
		Long _userLastLoginEnterpriseId = DataStorageUtils
				.getLong(IAUserLocalStorageAttributes.USER_LASTLOGINENTERPRISEID
						.name());

		UserBean user = new UserBean(userName, null, userkey);
		IAUserExtension.setUserLoginEnterpriseId(user,
				_userLastLoginEnterpriseId);

		UserManager.getInstance().setUser(user);
	}
}
