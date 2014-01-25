package com.futuo.iapprove.utils;

import android.content.Context;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.StringUtils;

public class IApproveTaskAttachmentFileUrlUtils {

	// iApprove application context
	private static final Context APP_CONTEXT = CTApplication.getContext();

	// get iApprove task image attachment file url with image name, original
	// name and suffix
	public static final String taskImageAttachmentFileUrl(String name,
			String originName, String suffix) {
		return APP_CONTEXT.getResources().getString(R.string.server_url)
				+ APP_CONTEXT.getResources().getString(R.string.imageFile_url)
				+ getTaskAttachmentFileUrlContent(name, originName, suffix);
	}

	// get iApprove task voice attachment file url with voice name, original
	// name and suffix
	public static final String taskVoiceAttachmentFileUrl(String name,
			String originName, String suffix) {
		return APP_CONTEXT.getResources().getString(R.string.server_url)
				+ APP_CONTEXT.getResources().getString(R.string.audioFile_url)
				+ getTaskAttachmentFileUrlContent(name, originName, suffix);
	}

	// get iApprove task attachment file url content with name, original name
	// and suffix
	private static final String getTaskAttachmentFileUrlContent(String name,
			String originName, String suffix) {
		StringBuilder _taskAttachmentFileUrlContent = new StringBuilder();

		// get login user info
		UserBean _loginUser = UserManager.getInstance().getUser();

		// append '?' get request url separator character '?'
		_taskAttachmentFileUrlContent.append('?');

		// append enterprise id, login user name, user key, file name, origin
		// name and suffix
		_taskAttachmentFileUrlContent
				.append(APP_CONTEXT.getResources().getString(
						R.string.rbgServer_fileReqParam_enterpriseId))
				.append('=')
				.append(StringUtils.base64Encode(IAUserExtension
						.getUserLoginEnterpriseId(_loginUser).toString()));
		_taskAttachmentFileUrlContent.append('&');
		_taskAttachmentFileUrlContent
				.append(APP_CONTEXT.getResources().getString(
						R.string.rbgServer_userSigReqParam_userName))
				.append('=')
				.append(StringUtils.base64Encode(_loginUser.getName()));
		_taskAttachmentFileUrlContent.append('&');
		_taskAttachmentFileUrlContent
				.append(APP_CONTEXT.getResources().getString(
						R.string.rbgServer_userSigReqParam_userKey))
				.append('=').append(_loginUser.getUserKey());
		_taskAttachmentFileUrlContent.append('&');
		_taskAttachmentFileUrlContent
				.append(APP_CONTEXT.getResources().getString(
						R.string.rbgServer_fileReqParam_name)).append('=')
				.append(StringUtils.base64Encode(name));
		_taskAttachmentFileUrlContent.append('&');
		_taskAttachmentFileUrlContent
				.append(APP_CONTEXT.getResources().getString(
						R.string.rbgServer_fileReqParam_originName))
				.append('=').append(StringUtils.base64Encode(originName));
		_taskAttachmentFileUrlContent.append('&');
		_taskAttachmentFileUrlContent
				.append(APP_CONTEXT.getResources().getString(
						R.string.rbgServer_fileReqParam_suffix)).append('=')
				.append(StringUtils.base64Encode(suffix));

		return _taskAttachmentFileUrlContent.toString();
	}

}
