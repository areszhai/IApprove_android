package com.futuo.iapprove.account.user;

import java.util.List;

import android.util.Log;

import com.richitec.commontoolkit.user.UserBean;

public class IAUserExtension {

	private static final String LOG_TAG = IAUserExtension.class
			.getCanonicalName();

	// get approve user login company id
	public static Long getUserLoginCompanyId(UserBean user) {
		return (Long) getUserExtAttr(user,
				IAUserExtensionAttributes.USER_LOGINCOMPANYID);
	}

	// set approve user login company id
	public static void setUserLoginCompanyId(UserBean user,
			Long userLoginCompanyId) {
		setUserExtAttr(user, IAUserExtensionAttributes.USER_LOGINCOMPANYID,
				userLoginCompanyId);
	}

	// get approve user company list
	@SuppressWarnings("unchecked")
	public static List<UserCompanyBean> getUserCompanies(UserBean user) {
		return (List<UserCompanyBean>) getUserExtAttr(user,
				IAUserExtensionAttributes.USER_COMPANIES);
	}

	// set approve user company list
	public static void setUserCompanies(UserBean user,
			List<UserCompanyBean> userCompanies) {
		setUserExtAttr(user, IAUserExtensionAttributes.USER_COMPANIES,
				userCompanies);
	}

	// get approve user extension attribute with key
	private static Object getUserExtAttr(UserBean user,
			IAUserExtensionAttributes extAttrKey) {
		Object _extAttrValue = null;

		// check user bean
		if (null != user) {
			_extAttrValue = user.getExtension().get(extAttrKey.name());
		} else {
			Log.e(LOG_TAG,
					"Get iApprove user extension attribute error, user = "
							+ user + " and extension attribute key = "
							+ extAttrKey);
		}

		return _extAttrValue;
	}

	// set approve user extension attribute with key and value
	private static void setUserExtAttr(UserBean user,
			IAUserExtensionAttributes extAttrKey, Object extAttrValue) {
		// check user bean
		if (null != user && null != extAttrValue) {
			user.getExtension().put(extAttrKey.name(), extAttrValue);
		} else {
			Log.e(LOG_TAG,
					"Set iApprove user extension attribute error, user = "
							+ user + ", extension attribute key = "
							+ extAttrKey + " and value = " + extAttrValue);
		}
	}

	// inner class
	// approve user extension attributes
	static enum IAUserExtensionAttributes {

		// user login company id and company list
		USER_LOGINCOMPANYID, USER_COMPANIES

	}

}
