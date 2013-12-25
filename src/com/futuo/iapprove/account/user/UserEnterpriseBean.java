package com.futuo.iapprove.account.user;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class UserEnterpriseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7049603672369228031L;

	private static final String LOG_TAG = UserEnterpriseBean.class
			.getCanonicalName();

	// enterprise id, name and employee name
	private Long id;
	private String name;
	private String employeeName;

	// Constructor
	public UserEnterpriseBean() {
		super();

		// nothing to do
	}

	public UserEnterpriseBean(JSONObject userEnterpriseJSONObejct) {
		super();

		// check user enterprise json object
		if (null != userEnterpriseJSONObejct) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// set user enterprise bean attributes
			// id
			id = JSONUtils
					.getLongFromJSONObject(
							userEnterpriseJSONObejct,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_accountLoginReqResp_enterprise_id));

			// name
			name = JSONUtils
					.getStringFromJSONObject(
							userEnterpriseJSONObejct,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_accountLoginReqResp_enterprise_name));

			// employee name
			employeeName = JSONUtils
					.getStringFromJSONObject(
							userEnterpriseJSONObejct,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_accountLoginReqResp_enterprise_employeeName));
		} else {
			Log.e(LOG_TAG,
					"Constructor user enterprise bean with json object error, json object = "
							+ userEnterpriseJSONObejct);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append enterprise id, name and employee name
		_description.append("Enterprise id = ").append(id).append(", ")
				.append("name = ").append(name).append(" and ")
				.append("employee name = ").append(employeeName);

		return _description.toString();
	}

}
