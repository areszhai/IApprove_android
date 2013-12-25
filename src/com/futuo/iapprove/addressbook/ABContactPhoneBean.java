package com.futuo.iapprove.addressbook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class ABContactPhoneBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5187085950151498057L;

	private static final String LOG_TAG = ABContactPhoneBean.class
			.getCanonicalName();

	// address book contact phone type and number
	private ABContactPhoneType type;
	private Long number;

	public ABContactPhoneBean(ABContactPhoneType type, Long number) {
		super();

		// save address book contact phone type and number
		this.type = type;
		this.number = number;
	}

	public ABContactPhoneType getType() {
		return type;
	}

	public void setType(ABContactPhoneType type) {
		this.type = type;
	}

	// get phone type label
	public String getTypeLabel() {
		return type.getPhoneTypeLabel();
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	// get address book contact phone list with JSON object
	public static List<ABContactPhoneBean> getContactPhones(
			JSONObject contactJSONObject) {
		List<ABContactPhoneBean> _contactPhones = null;

		// check address book contact JSON object
		if (null != contactJSONObject) {
			// initialize contact phone list
			_contactPhones = new ArrayList<ABContactPhoneBean>();

			// get application context
			Context _appContext = CTApplication.getContext();

			// mobile phone
			try {
				// get and check mobile phone value
				Long _mobilePhoneValue = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										contactJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseABReqResp_employee_mobilePhone)));
				if (null != _mobilePhoneValue) {
					_contactPhones.add(new ABContactPhoneBean(
							ABContactPhoneType.MOBILE, _mobilePhoneValue));
				}
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get employee mobile phone error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// office phone
			try {
				// get and check office phone value
				Long _officePhoneValue = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										contactJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseABReqResp_employee_officePhone)));
				if (null != _officePhoneValue) {
					_contactPhones.add(new ABContactPhoneBean(
							ABContactPhoneType.OFFICE, _officePhoneValue));
				}
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get employee office phone error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		return _contactPhones;
	}

	// get address book contact phone list with cursor
	public static List<ABContactPhoneBean> getContactPhones(Cursor cursor) {
		List<ABContactPhoneBean> _contactPhones = null;

		// check the cursor
		if (null != cursor) {
			// initialize contact phone list
			_contactPhones = new ArrayList<ABContactPhoneBean>();

			// mobile phone
			try {
				// get and check mobile phone value
				Long _mobilePhoneValue = Long.parseLong(cursor.getString(cursor
						.getColumnIndex(Employee.MOBILE_PHONE)));
				if (null != _mobilePhoneValue) {
					_contactPhones.add(new ABContactPhoneBean(
							ABContactPhoneType.MOBILE, _mobilePhoneValue));
				}
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get employee mobile phone error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// office phone
			try {
				// get and check office phone value
				Long _officePhoneValue = Long.parseLong(cursor.getString(cursor
						.getColumnIndex(Employee.OFFICE_PHONE)));
				if (null != _officePhoneValue) {
					_contactPhones.add(new ABContactPhoneBean(
							ABContactPhoneType.OFFICE, _officePhoneValue));
				}
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get employee office phone error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		return _contactPhones;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append enterprise address book contact phone type and number
		_description.append("Enterprise address book contact phone type = ")
				.append(type.getPhoneTypeLabel()).append(" and ")
				.append("number = ").append(number);

		return _description.toString();
	}

}
