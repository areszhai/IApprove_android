package com.futuo.iapprove.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.futuo.iapprove.account.user.IAUserExtension;
import com.richitec.commontoolkit.user.UserManager;

public abstract class UserEnterpriseBroadcastReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = UserEnterpriseBroadcastReceiver.class
			.getCanonicalName();

	// broadcast actions
	public static final String A_ENTERPRISECHANGE = "action_enterpriseChange";

	// message extra keys
	public static final String EK_ENTERPRISECHANGED = "extraKey_enterpriseChanged";

	// user enterprise changed
	public abstract void onEnterpriseChange(Long newEnterpriseId);

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG,
				"User enterprise broadcast receiver on receive, intent = "
						+ intent);

		// get and check user enterprise broadcast receiver action
		String _action = intent.getAction();
		if (A_ENTERPRISECHANGE.equalsIgnoreCase(_action)) {
			// get and check enterprise changed flag
			boolean _userEnterpriseChanged = intent.getBooleanExtra(
					EK_ENTERPRISECHANGED, false);
			if (_userEnterpriseChanged) {
				// enterprise changed
				onEnterpriseChange(IAUserExtension
						.getUserLoginEnterpriseId(UserManager.getInstance()
								.getUser()));
			}
		} else {
			Log.e(LOG_TAG,
					"Unrecognize user enterprise broadcast receiver action = "
							+ _action);
		}
	}

}
