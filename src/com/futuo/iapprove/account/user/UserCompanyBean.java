package com.futuo.iapprove.account.user;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class UserCompanyBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7049603672369228031L;

	private static final String LOG_TAG = UserCompanyBean.class
			.getCanonicalName();

	// company id, name and employee name
	private Long id;
	private String name;
	private String employeeName;

	// Constructor
	public UserCompanyBean() {
		super();

		// nothing to do
	}

	public UserCompanyBean(JSONObject userCompanyJSONObejct) {
		super();

		// check user company json object
		if (null != userCompanyJSONObejct) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// set user company bean attributes
			// id
			id = JSONUtils
					.getLongFromJSONObject(
							userCompanyJSONObejct,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_accountLoginReqResp_companies_companyId));

			// name
			name = JSONUtils
					.getStringFromJSONObject(
							userCompanyJSONObejct,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_accountLoginReqResp_companies_companyName));

			// employee name
			employeeName = JSONUtils
					.getStringFromJSONObject(
							userCompanyJSONObejct,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_accountLoginReqResp_companies_employeeName));
		} else {
			Log.e(LOG_TAG,
					"Constructor user company bean with json object error, json object = "
							+ userCompanyJSONObejct);
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

		// append company id, name and employee name
		_description.append("Company id = ").append(id).append(", ")
				.append("name = ").append(name).append(" and ")
				.append("employee name = ").append(employeeName);

		return _description.toString();
	}

}
