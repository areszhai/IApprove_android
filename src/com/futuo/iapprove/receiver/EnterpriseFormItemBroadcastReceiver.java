package com.futuo.iapprove.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class EnterpriseFormItemBroadcastReceiver extends
		BroadcastReceiver {

	private static final String LOG_TAG = EnterpriseFormItemBroadcastReceiver.class
			.getCanonicalName();

	// broadcast actions
	public static final String A_FORMITEMCHANGE = "action_formItemChange";

	// message extra keys
	public static final String EK_FORMITEMCHANGED = "extraKey_formItemChanged";
	public static final String EK_CHANGEDEDFORMTYPEID = "extraKey_changededFormTypeId";
	public static final String EK_CHANGEDEDFORMID = "extraKey_changededFormId";

	// enterprise form item changed
	public abstract void onEnterpriseFormItemChange(Long formTypeId, Long formId);

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG,
				"Enterprise form item broadcast receiver on receive, intent = "
						+ intent);

		// get and check enterprise form item broadcast receiver action
		String _action = intent.getAction();
		if (A_FORMITEMCHANGE.equalsIgnoreCase(_action)) {
			// get and check enterprise form item changed flag
			boolean _enterpriseFormItemChanged = intent.getBooleanExtra(
					EK_FORMITEMCHANGED, false);
			if (_enterpriseFormItemChanged) {
				// get enterprise changed form type id and form id
				Long _enterpriseChangedFormTypeId = intent.getLongExtra(
						EK_CHANGEDEDFORMTYPEID, 0);
				Long _enterpriseChangedFormId = intent.getLongExtra(
						EK_CHANGEDEDFORMID, 0);

				// enterprise form item changed
				onEnterpriseFormItemChange(_enterpriseChangedFormTypeId,
						_enterpriseChangedFormId);
			}
		} else {
			Log.e(LOG_TAG,
					"Unrecognize enterprise form item broadcast receiver action = "
							+ _action);
		}
	}

}
