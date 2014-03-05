package com.futuo.iapprove.task;

import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;

public enum IApproveTaskAttachmentType {

	// audio amr, wav and 3gp
	// image jpg, jpeg, png, bmp and gif
	// common file
	AUDIO_AMR(0), AUDIO_WAV(1), AUDIO_3GPP(2), IMAGE_JPG(4), IMAGE_JPEG(5), IMAGE_PNG(
			6), IMAGE_BMP(7), IMAGE_GIF(8), COMMON_TEXT(10), COMMON_FILE(12);

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
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_amrSuffix);
		} else if (1 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_wavSuffix);
		} else if (2 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_3gpSuffix);
		} else if (4 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_jpgSuffix);
		} else if (5 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_jpegSuffix);
		} else if (6 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_pngSuffix);
		} else if (7 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_bmpSuffix);
		} else if (8 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_gifSuffix);
		} else if (10 == value) {
			rbgServerRetValue = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_textSuffix);
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
			if (AUDIO_AMR.rbgServerRetValue.equalsIgnoreCase(rbgServerRetValue)) {
				// amr audio
				_type = AUDIO_AMR;
			} else if (AUDIO_WAV.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// wav audio
				_type = AUDIO_WAV;
			} else if (AUDIO_3GPP.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// 3gp audio
				_type = AUDIO_3GPP;
			} else if (IMAGE_JPG.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// jpg image
				_type = IMAGE_JPG;
			} else if (IMAGE_JPEG.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// jpeg image
				_type = IMAGE_JPEG;
			} else if (IMAGE_PNG.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// png image
				_type = IMAGE_PNG;
			} else if (IMAGE_BMP.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// bmp image
				_type = IMAGE_BMP;
			} else if (IMAGE_GIF.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// gif image
				_type = IMAGE_GIF;
			} else if (COMMON_TEXT.rbgServerRetValue
					.equalsIgnoreCase(rbgServerRetValue)) {
				// text
				_type = COMMON_TEXT;
			} else {
				// common file
				_type = COMMON_FILE;
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
			if (AUDIO_AMR.typeValue.intValue() == value.intValue()) {
				// amr audio
				_type = AUDIO_AMR;
			} else if (AUDIO_WAV.typeValue.intValue() == value.intValue()) {
				// wav audio
				_type = AUDIO_WAV;
			} else if (AUDIO_3GPP.typeValue.intValue() == value.intValue()) {
				// 3gp audio
				_type = AUDIO_3GPP;
			} else if (IMAGE_JPG.typeValue.intValue() == value.intValue()) {
				// jpg image
				_type = IMAGE_JPG;
			} else if (IMAGE_JPEG.typeValue.intValue() == value.intValue()) {
				// jpeg image
				_type = IMAGE_JPEG;
			} else if (IMAGE_PNG.typeValue.intValue() == value.intValue()) {
				// png image
				_type = IMAGE_PNG;
			} else if (IMAGE_BMP.typeValue.intValue() == value.intValue()) {
				// bmp image
				_type = IMAGE_BMP;
			} else if (IMAGE_BMP.typeValue.intValue() == value.intValue()) {
				// gif image
				_type = IMAGE_GIF;
			} else if (COMMON_TEXT.typeValue.intValue() == value.intValue()) {
				// text
				_type = COMMON_TEXT;
			} else if (COMMON_FILE.typeValue.intValue() == value.intValue()) {
				// common file
				_type = COMMON_FILE;
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
