package com.futuo.iapprove.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class EnterpriseFormBroadcastReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = EnterpriseFormBroadcastReceiver.class
			.getCanonicalName();

	// broadcast actions
	public static final String A_FORMTYPECHANGE = "action_formTypeChange";
	public static final String A_FORMITEMCHANGE = "action_formItemChange";

	// message extra keys
	public static final String EK_FORMTYPECHANGED = "extraKey_formTypeChanged";
	public static final String EK_FORMITEMCHANGED = "extraKey_formItemChanged";
	public static final String EK_CHANGEDEDFORMTYPEID = "extraKey_changededFormTypeId";
	public static final String EK_CHANGEDEDFORMID = "extraKey_changededFormId";

	// enterprise form type changed
	public abstract void onEnterpriseFormTypeChange();

	// enterprise form item changed
	public abstract void onEnterpriseFormItemChange(Long formTypeId, Long formId);

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG,
				"Enterprise form broadcast receiver on receive, intent = "
						+ intent);

		// get and check enterprise form broadcast receiver action
		String _action = intent.getAction();
		if (A_FORMTYPECHANGE.equalsIgnoreCase(_action)) {
			// get and check enterprise form type changed flag
			boolean _enterpriseFormTypeChanged = intent.getBooleanExtra(
					EK_FORMTYPECHANGED, false);
			if (_enterpriseFormTypeChanged) {
				// enterprise form type changed
				onEnterpriseFormTypeChange();
			}
		} else if (A_FORMITEMCHANGE.equalsIgnoreCase(_action)) {
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
					"Unrecognize enterprise form broadcast receiver action = "
							+ _action);
		}
	}

}
