package com.futuo.iapprove.task;

import android.util.Log;

public enum IApproveTaskAttachmentDownloadStatus {

	// normal, downloading and download failed
	NORMAL(0), DOWNLOADING(1), DOWNLOAD_FAILED(2);

	private static final String LOG_TAG = IApproveTaskAttachmentDownloadStatus.class
			.getCanonicalName();

	// attachment download status value
	private Integer downloadStatusValue;

	// private constructor
	private IApproveTaskAttachmentDownloadStatus(Integer value) {
		// save attachment download status value
		downloadStatusValue = value;
	}

	public Integer getValue() {
		return downloadStatusValue;
	}

	// get user enterprise to-do list task attachment type with value
	public static final IApproveTaskAttachmentDownloadStatus getDownloadStatus(
			Integer value) {
		IApproveTaskAttachmentDownloadStatus _downloadStatus = null;

		// check download status value
		if (null != value) {
			if (NORMAL.downloadStatusValue.intValue() == value.intValue()) {
				// normal
				_downloadStatus = NORMAL;
			} else if (DOWNLOADING.downloadStatusValue.intValue() == value
					.intValue()) {
				// downloading
				_downloadStatus = DOWNLOADING;
			} else if (DOWNLOAD_FAILED.downloadStatusValue.intValue() == value
					.intValue()) {
				// download failed
				_downloadStatus = DOWNLOAD_FAILED;
			} else {
				Log.e(LOG_TAG,
						"Unrecognized user enterprise to-do list task attachment download status value = "
								+ value);
			}
		} else {
			Log.e(LOG_TAG,
					"Can't init user enterprise to-do list task attachment download status with value = "
							+ value);
		}

		return _downloadStatus;
	}

}
