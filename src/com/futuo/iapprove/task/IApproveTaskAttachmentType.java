package com.futuo.iapprove.task;

import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;

public enum IApproveTaskAttachmentType {

	// audio arm, wav and image jpg
	AUDIO_ARM(0), AUDIO_WAV(1), IMAGE_JPG(5);

	private static final String LOG_TAG = IApproveTaskAttachmentType.class
			.getCanonicalName();

	// type rbgServer return value and value
	private String rbgServerRetValue;
	private Integer typeValue;

	// private constructor
	private IApproveTaskAttachmentType(Integer value) {
		// get application context
		Context _appContext = CTApplication.getContext();

		// save type rbgServer return value and value
		typeValue = value;
		if (0 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_armSuffix);
		} else if (1 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_wavSuffix);
		} else if (5 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_jpgSuffix);
		}
	}

	public Integer getValue() {
		return typeValue;
	}

	// get user enterprise to-do list task attachment type with rbgServer return
	// value
	public static final IApproveTaskAttachmentType getType(
			String rbgServerRetValue) {
		IApproveTaskAttachmentType _type = null;

		// check type rbgServer return value
		if (null != rbgServerRetValue) {
			if (AUDIO_ARM.rbgServerRetValue.equalsIgnoreCase(rbgServerRetValue)) {
				// arm audio
				_type = AUDIO_ARM;
			} else if (AUDIO_WAV.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// wav audio
				_type = AUDIO_WAV;
			} else if (IMAGE_JPG.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// jpg image
				_type = IMAGE_JPG;
			} else {
				Log.e(LOG_TAG,
						"Unrecognized user enterprise to-do list task attachment type remote background server return value = "
								+ rbgServerRetValue);
			}
		} else {
			Log.e(LOG_TAG,
					"Can't init user enterprise to-do list task attachment type with remote background server return value = "
							+ rbgServerRetValue);
		}

		return _type;
	}

	// get user enterprise to-do list task attachment type with value
	public static final IApproveTaskAttachmentType getType(Integer value) {
		IApproveTaskAttachmentType _type = null;

		// check type value
		if (null != value) {
			if (AUDIO_ARM.typeValue.intValue() == value.intValue()) {
				// arm audio
				_type = AUDIO_ARM;
			} else if (AUDIO_WAV.typeValue.intValue() == value.intValue()) {
				// wav audio
				_type = AUDIO_WAV;
			} else if (IMAGE_JPG.typeValue.intValue() == value.intValue()) {
				// jpg image
				_type = IMAGE_JPG;
			} else {
				Log.e(LOG_TAG,
						"Unrecognized user enterprise to-do list task attachment type value = "
								+ value);
			}
		} else {
			Log.e(LOG_TAG,
					"Can't init user enterprise to-do list task attachment type with value = "
							+ value);
		}

		return _type;
	}

}
