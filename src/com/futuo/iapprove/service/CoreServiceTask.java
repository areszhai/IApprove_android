package com.futuo.iapprove.service;

import java.util.TimerTask;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.account.user.IAUserExtension;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.user.UserManager;

public abstract class CoreServiceTask extends TimerTask {

	private static final String LOG_TAG = CoreServiceTask.class
			.getCanonicalName();

	// context
	protected Context _mContext;

	// content resolver
	protected ContentResolver _mContentResolver;

	// enterprise id
	protected Long _mEnterpriseId;

	public CoreServiceTask() {
		super();

		// save context
		_mContext = CTApplication.getContext();

		// initialize content resolver
		_mContentResolver = _mContext.getContentResolver();
	}

	@Override
	public void run() {
		// check enterprise id
		if (null != _mEnterpriseId) {
			// core service task execute
			execute();
		} else {
			Log.d(LOG_TAG, "Core Service task = " + this + " idling");
		}
	}

	// set enterprise id
	public void setEnterpriseId() {
		_mEnterpriseId = IAUserExtension.getUserLoginEnterpriseId(UserManager
				.getInstance().getUser());

		// user enterprise changed
		enterpriseChanged();
	}

	// clear enterprise id
	public void clearEnterpriseId() {
		_mEnterpriseId = null;
	}

	// enterprise changed
	protected abstract void enterpriseChanged();

	// core service task execute
	protected abstract void execute();

}
