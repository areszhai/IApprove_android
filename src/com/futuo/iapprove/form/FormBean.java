package com.futuo.iapprove.form;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.Forms.Form;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class FormBean implements Comparable<FormBean>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1676248583447532056L;

	private static final String LOG_TAG = FormBean.class.getCanonicalName();

	// row id, form id, form type id and name
	private Long rowId;
	private Long formId;
	private String formName;

	// local storage data dirty type
	private LocalStorageDataDirtyType lsDataDirtyType;

	public FormBean() {
		super();

		// set default local storage data dirty type
		lsDataDirtyType = LocalStorageDataDirtyType.NORMAL;
	}

	// constructor with JSON object
	public FormBean(JSONObject formJSONObject) {
		this();

		// check form JSON object
		if (null != formJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// set form attributes
			// form id
			try {
				formId = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										formJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseFormReqResp_form_id)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get form id error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// form name
			formName = JSONUtils
					.getStringFromJSONObject(
							formJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormReqResp_form_name));
		} else {
			Log.e(LOG_TAG,
					"New form with JSON object error, form JSON object = "
							+ formJSONObject);
		}
	}

	// constructor with cursor
	public FormBean(Cursor cursor) {
		this();

		// check the cursor
		if (null != cursor) {
			// set form attributes
			// row id
			rowId = cursor.getLong(cursor.getColumnIndex(Form._ID));

			// form id
			formId = cursor.getLong(cursor.getColumnIndex(Form.FORM_ID));

			// form name
			formName = cursor.getString(cursor.getColumnIndex(Form.NAME));
		} else {
			Log.e(LOG_TAG, "New form with cursor error, cursor = " + cursor);
		}
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public LocalStorageDataDirtyType getLocalStorageDataDirtyType() {
		return lsDataDirtyType;
	}

	public void setLocalStorageDataDirtyType(
			LocalStorageDataDirtyType lsDataDirtyType) {
		this.lsDataDirtyType = lsDataDirtyType;
	}

	@Override
	public int compareTo(FormBean another) {
		int _result = -1;

		// check form name
		if ((null == formName && null == another.formName)
				|| (null != formName && null != another.formName && formName
						.equalsIgnoreCase(another.formName))) {
			_result = 0;
		} else {
			Log.d(LOG_TAG, "Form name not equals, self name = " + formName
					+ " and another name = " + another.formName);
		}

		return _result;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append enterprise form id and name
		_description.append("Enterprise form row id = ").append(rowId)
				.append(", ").append("form id = ").append(formId)
				.append(" and ").append("name = ").append(formName);

		return _description.toString();
	}

}
