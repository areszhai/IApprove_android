package com.futuo.iapprove.form;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormTypes.FormType;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class FormTypeBean implements Comparable<FormTypeBean>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3454578737016320127L;

	private static final String LOG_TAG = FormTypeBean.class.getCanonicalName();

	// row id, type id and name
	private Long rowId;
	private Long typeId;
	private String typeName;

	// local storage data dirty type
	private LocalStorageDataDirtyType lsDataDirtyType;

	public FormTypeBean() {
		super();

		// set default local storage data dirty type
		lsDataDirtyType = LocalStorageDataDirtyType.NORMAL;
	}

	// constructor with JSON object
	public FormTypeBean(JSONObject formTypeJSONObject) {
		this();

		// check form type JSON object
		if (null != formTypeJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// set form type attributes
			// type id
			try {
				typeId = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										formTypeJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseFormTypeReqResp_formType_id)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG, "Get form type id error, exception message = "
						+ e.getMessage());

				e.printStackTrace();
			}

			// type name
			typeName = JSONUtils
					.getStringFromJSONObject(
							formTypeJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormTypeReqResp_formType_name));
		} else {
			Log.e(LOG_TAG,
					"New form type with JSON object error, form type JSON object = "
							+ formTypeJSONObject);
		}
	}

	// constructor with cursor
	public FormTypeBean(Cursor cursor) {
		this();

		// check the cursor
		if (null != cursor) {
			// set form type attributes
			// row id
			rowId = cursor.getLong(cursor.getColumnIndex(FormType._ID));

			// type id
			typeId = cursor.getLong(cursor.getColumnIndex(FormType.TYPE_ID));

			// type name
			typeName = cursor.getString(cursor.getColumnIndex(FormType.NAME));
		} else {
			Log.e(LOG_TAG, "New form type with cursor error, cursor = "
					+ cursor);
		}
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public LocalStorageDataDirtyType getLocalStorageDataDirtyType() {
		return lsDataDirtyType;
	}

	public void setLocalStorageDataDirtyType(
			LocalStorageDataDirtyType lsDataDirtyType) {
		this.lsDataDirtyType = lsDataDirtyType;
	}

	@Override
	public int compareTo(FormTypeBean another) {
		int _result = -1;

		// check type name
		if ((null == typeName && null == another.typeName)
				|| (null != typeName && null != another.typeName && typeName
						.equalsIgnoreCase(another.typeName))) {
			_result = 0;
		} else {
			Log.d(LOG_TAG, "Form type name not equals, self name = " + typeName
					+ " and another name = " + another.typeName);
		}

		return _result;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append enterprise form type id and name
		_description.append("Enterprise form type row id = ").append(rowId)
				.append(", ").append("type id = ").append(typeId)
				.append(" and ").append("name = ").append(typeName);

		return _description.toString();
	}

}
