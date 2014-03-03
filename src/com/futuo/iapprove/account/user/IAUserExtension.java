package com.futuo.iapprove.account.user;

import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.provider.UserEnterpriseProfileContentProvider.EnterpriseProfiles.EnterpriseProfile;
import com.futuo.iapprove.receiver.UserEnterpriseBroadcastReceiver;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.user.UserBean;

public class IAUserExtension {

	private static final String LOG_TAG = IAUserExtension.class
			.getCanonicalName();

	// get approve user login user id
	public static Long getUserLoginUserId(UserBean user) {
		return (Long) getUserExtAttr(user,
				IAUserExtensionAttributes.USER_LOGINUSERID);
	}

	// set approve user login user id
	public static void setUserLoginUserId(UserBean user, Long userLoginUserId) {
		setUserExtAttr(user, IAUserExtensionAttributes.USER_LOGINUSERID,
				userLoginUserId);
	}

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

		// send user enterprise changed broadcast
		// define user enterprise changed broadcast intent
		Intent _userEnterpriseChangedBroadcastIntent = new Intent(
				UserEnterpriseBroadcastReceiver.A_ENTERPRISECHANGE);

		// set user enterprise changed message
		_userEnterpriseChangedBroadcastIntent.putExtra(
				UserEnterpriseBroadcastReceiver.EK_ENTERPRISECHANGED, true);

		// send normal broadcast
		CTApplication.getContext().sendBroadcast(
				_userEnterpriseChangedBroadcastIntent);
	}

	// get approve user login enterprise index
	public static int getUserLoginEnterpriseIndex(UserBean user) {
		int _userLoginEnterpriseIndex = 0;

		// get user enterprise cursor
		Cursor _userEnterpriseCursor = CTApplication
				.getContext()
				.getContentResolver()
				.query(EnterpriseProfile.ENTERPRISEPROFILES_CONTENT_URI,
						null,
						EnterpriseProfile.USER_ENTERPRISEPROFILES_WITHLOGINNAME_CONDITION,
						new String[] { user.getName() }, null);

		// check the cursor
		if (null != _userEnterpriseCursor) {
			while (_userEnterpriseCursor.moveToNext()) {
				// get and check user login enterprise id
				if (IAUserExtension.getUserLoginEnterpriseId(user).longValue() == new UserEnterpriseBean(
						_userEnterpriseCursor).getEnterpriseId().longValue()) {
					_userLoginEnterpriseIndex = _userEnterpriseCursor
							.getPosition();
				}
			}

			// close the cursor
			_userEnterpriseCursor.close();
		}

		return _userLoginEnterpriseIndex;
	}

	// get approve user login enterprise abbreviation
	public static String getUserLoginEnterpriseAbbreviation(UserBean user) {
		String _userLoginEnterpriseAbbreviation = "";

		// get user enterprise cursor
		Cursor _userEnterpriseCursor = CTApplication
				.getContext()
				.getContentResolver()
				.query(EnterpriseProfile.ENTERPRISEPROFILES_CONTENT_URI,
						null,
						EnterpriseProfile.USER_ENTERPRISEPROFILES_WITHLOGINNAME_CONDITION,
						new String[] { user.getName() }, null);

		// check the cursor
		if (null != _userEnterpriseCursor) {
			while (_userEnterpriseCursor.moveToNext()) {
				// get user login enterprise
				UserEnterpriseBean _userEnterprise = new UserEnterpriseBean(
						_userEnterpriseCursor);

				// get and check user login enterprise id
				if (IAUserExtension.getUserLoginEnterpriseId(user).longValue() == _userEnterprise
						.getEnterpriseId().longValue()) {
					_userLoginEnterpriseAbbreviation = _userEnterprise
							.getEnterpriseAbbreviation();
				}
			}

			// close the cursor
			_userEnterpriseCursor.close();
		}

		return _userLoginEnterpriseAbbreviation;
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

		// user login user id and enterprise id
		USER_LOGINUSERID, USER_LOGINENTERPRISEID

	}

}
