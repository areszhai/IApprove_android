package com.futuo.iapprove.account.user;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.UserEnterpriseProfileContentProvider.EnterpriseProfiles.EnterpriseProfile;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class UserEnterpriseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7091686271058774997L;

	private static final String LOG_TAG = UserEnterpriseBean.class
			.getCanonicalName();

	// user enterprise id and abbreviation
	private Long enterpriseId;
	private String enterpriseAbbreviation;

	// constructor with JSON object
	public UserEnterpriseBean(JSONObject enterpriseJSONObject) {
		super();

		// check user enterprise JSON object
		if (null != enterpriseJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// set user enterprise attributes
			// enterprise id
			try {
				enterpriseId = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										enterpriseJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_accountLoginReqResp_enterprise_id)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get user enterprise id error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// enterprise abbreviation
			enterpriseAbbreviation = JSONUtils
					.getStringFromJSONObject(
							enterpriseJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_accountLoginReqResp_enterprise_abbreviation));
		} else {
			Log.e(LOG_TAG,
					"New user enterprise with JSON object error, contact JSON object = "
							+ enterpriseJSONObject);
		}
	}

	// constructor with cursor
	public UserEnterpriseBean(Cursor cursor) {
		super();

		// check the cursor
		if (null != cursor) {
			// set user enterprise attributes
			// enterprise id
			enterpriseId = cursor.getLong(cursor
					.getColumnIndex(EnterpriseProfile.ENTERPRISE_ID));

			// enterprise abbreviation
			enterpriseAbbreviation = cursor.getString(cursor
					.getColumnIndex(EnterpriseProfile.ENTERPRISE_ABBREVIATION));
		} else {
			Log.e(LOG_TAG, "New user enterprise with cursor error, cursor = "
					+ cursor);
		}
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getEnterpriseAbbreviation() {
		return enterpriseAbbreviation;
	}

	public void setEnterpriseAbbreviation(String enterpriseAbbreviation) {
		this.enterpriseAbbreviation = enterpriseAbbreviation;
	}

	public int compareTo(UserEnterpriseBean another) {
		int _result = -1;

		// check user enterprise abbreviation
		if ((null == enterpriseAbbreviation && null == another.enterpriseAbbreviation)
				|| (null != enterpriseAbbreviation
						&& null != another.enterpriseAbbreviation && enterpriseAbbreviation
							.equalsIgnoreCase(another.enterpriseAbbreviation))) {
			_result = 0;
		} else {
			Log.d(LOG_TAG,
					"User enterprise abbreviation not equals, self abbreviation = "
							+ enterpriseAbbreviation
							+ " and another abbreviation = "
							+ another.enterpriseAbbreviation);
		}

		return _result;
	}

}
