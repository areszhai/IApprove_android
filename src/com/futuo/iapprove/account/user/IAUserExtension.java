package com.futuo.iapprove.account.user;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.richitec.commontoolkit.user.UserBean;

public class IAUserExtension {

	private static final String LOG_TAG = IAUserExtension.class
			.getCanonicalName();

	// approve user enterprise map, key is enterprise id and value is enterprise
	// name
	private static Map<Long, String> _mUserEnterpriseMap;

	// get approve user login enterprise id
	public static Long getUserLoginEnterpriseId(UserBean user) {
		return (Long) getUserExtAttr(user,
				IAUserExtensionAttributes.USER_LOGINENTERPRISEID);
	}

	// set approve user login enterprise id
	public static void setUserLoginEnterpriseId(UserBean user,
			Long userLoginEnterpriseId) {
		setUserExtAttr(user, IAUserExtensionAttributes.USER_LOGINENTERPRISEID,
				userLoginEnterpriseId);
	}

	// get approve user enterprise list
	@SuppressWarnings("unchecked")
	public static List<UserEnterpriseBean> getUserEnterprises(UserBean user) {
		return (List<UserEnterpriseBean>) getUserExtAttr(user,
				IAUserExtensionAttributes.USER_ENTERPRISES);
	}

	// set approve user enterprise list
	public static void setUserEnterprises(UserBean user,
			List<UserEnterpriseBean> userEnterprises) {
		// init approve user enterprise map
		_mUserEnterpriseMap = new LinkedHashMap<Long, String>();

		for (UserEnterpriseBean userEnterprise : userEnterprises) {
			_mUserEnterpriseMap.put(userEnterprise.getId(),
					userEnterprise.getName());
		}

		setUserExtAttr(user, IAUserExtensionAttributes.USER_ENTERPRISES,
				userEnterprises);
	}

	// get approve user login enterprise index
	public static final Integer getUserLoginEnterpriseIndex(
			Long userLoginEnterpriseId) {
		Integer _userLoginEnterpriseIndex = null;

		// get user enterprise id array
		Long[] _userEnterpriseIds = _mUserEnterpriseMap.keySet().toArray(
				new Long[] {});

		for (int i = 0; i < _userEnterpriseIds.length; i++) {
			// compare user login enterprise id with each user enterprise id
			if (userLoginEnterpriseId.longValue() == _userEnterpriseIds[i]
					.longValue()) {
				_userLoginEnterpriseIndex = i;

				// break immediately
				break;
			}
		}

		return _userLoginEnterpriseIndex;
	}

	// get approve user enterprise name array
	public static final String[] getUserEnterpriseNames() {
		return _mUserEnterpriseMap.values().toArray(new String[] {});
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

		// user login enterprise id and enterprise list
		USER_LOGINENTERPRISEID, USER_ENTERPRISES

	}

}
