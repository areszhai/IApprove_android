package com.futuo.iapprove.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class EnterpriseTodoTaskBroadcastReceiver extends
		BroadcastReceiver {

	private static final String LOG_TAG = EnterpriseTodoTaskBroadcastReceiver.class
			.getCanonicalName();

	// broadcast actions
	public static final String A_FORMITEMCHANGE = "action_formItemChange";
	public static final String A_FORMATTACHMENTCHANGE = "action_formAttachmentChange";

	// message extra keys
	public static final String EK_FORMITEM6ATTACHMENTCHANGED = "extraKey_formItemOrAttachmentChanged";
	public static final String EK_CHANGEDEDFORMSENDERFAKEID = "extraKey_changededFormSenderFakeId";
	public static final String EK_CHANGEDEDFORMATTACHMENTID = "extraKey_changededFormAttachmentId";

	// enterprise to-do task form item changed
	public abstract void onEnterpriseTodoTaskFormItemChange(
			Long formSenderFakeId);

	// enterprise to-do task form attachment changed
	public abstract void onEnterpriseTodoTaskFormAttachmentChange(
			Long formSenderFakeId, Long formAttachmentId);

	@Override
	public void onReceive(Context content, Intent intent) {
		Log.d(LOG_TAG,
				"Enterprise to-do task form broadcast receiver on receive, intent = "
						+ intent);

		// get and check enterprise to-do task form broadcast receiver action
		String _action = intent.getAction();
		if (A_FORMITEMCHANGE.equalsIgnoreCase(_action)
				|| A_FORMATTACHMENTCHANGE.equalsIgnoreCase(_action)) {
			// get and check enterprise to-do task form item or attachment
			// changed flag
			boolean _enterpriseTodoTaskFormItemOrAttachmentChanged = intent
					.getBooleanExtra(EK_FORMITEM6ATTACHMENTCHANGED, false);
			if (_enterpriseTodoTaskFormItemOrAttachmentChanged) {
				// get enterprise to-do task changed form sender fake id
				Long _enterpriseTodoTaskChangedFormSenderFakeId = intent
						.getLongExtra(EK_CHANGEDEDFORMSENDERFAKEID, 0);

				// check enterprise to-do task form broadcast receiver action
				// again
				if (A_FORMITEMCHANGE.equalsIgnoreCase(_action)) {
					// enterprise to-do task form item changed
					onEnterpriseTodoTaskFormItemChange(_enterpriseTodoTaskChangedFormSenderFakeId);
				} else if (A_FORMATTACHMENTCHANGE.equalsIgnoreCase(_action)) {
					// get enterprise to-do task changed form attachment id
					Long _enterpriseTodoTaskChangedFormAttachmentId = intent
							.getLongExtra(EK_CHANGEDEDFORMATTACHMENTID, 0);

					// enterprise to-do task form attachment changed
					onEnterpriseTodoTaskFormAttachmentChange(
							_enterpriseTodoTaskChangedFormSenderFakeId,
							_enterpriseTodoTaskChangedFormAttachmentId);
				}
			}
		} else {
			Log.e(LOG_TAG,
					"Unrecognize enterprise to-do task form broadcast receiver action = "
							+ _action);
		}
	}

}
