package com.futuo.iapprove.addressbook.person;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.utils.DateStringUtils;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class PersonBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4169644680442963986L;

	private static final String LOG_TAG = PersonBean.class.getCanonicalName();

	// application context
	protected transient Context _mAppContext;

	// avater, avatar url, employee name, sex, birthday, department, approve
	// number, mobile phone, office phone, email and note
	protected byte[] avatar;
	protected String avatarUrl;
	protected String employeeName;
	protected PersonSex sex;
	protected Long birthday;
	protected String department;
	protected Long approveNumber;
	protected Long mobilePhone;
	protected Long officePhone;
	protected String email;
	protected String note;

	public PersonBean() {
		super();

		// get application context
		_mAppContext = CTApplication.getContext();

		// set default birthday
		birthday = 0L;
	}

	// constructor with JSON object
	public PersonBean(JSONObject personJSONObject) {
		this();

		// check person JSON object
		if (null != personJSONObject) {
			// set person attributes
			// avatar url
			avatarUrl = JSONUtils
					.getStringFromJSONObject(
							personJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_avatarUrl));

			// employee name
			employeeName = JSONUtils
					.getStringFromJSONObject(
							personJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_name));

			// sex
			try {
				sex = PersonSex
						.getSexWithRbgServerRetValue(Integer.parseInt(JSONUtils
								.getStringFromJSONObject(
										personJSONObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseABReqResp_employee_sex))));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get person sex error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// birthday
			// get and check birthday value
			Date _birthdayValue = DateStringUtils
					.shortDateString2Date(JSONUtils
							.getStringFromJSONObject(
									personJSONObject,
									_mAppContext
											.getResources()
											.getString(
													R.string.rbgServer_getEnterpriseABReqResp_employee_birthday)));
			if (null != _birthdayValue) {
				birthday = _birthdayValue.getTime();
			}

			// department
			// get and check department value
			String _departmentValue = JSONUtils
					.getStringFromJSONObject(
							personJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_department));
			if (null != _departmentValue
					&& !"null".equalsIgnoreCase(_departmentValue)) {
				department = _departmentValue;
			}

			// approve number
			try {
				approveNumber = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										personJSONObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseABReqResp_employee_approveNumber)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get person approve number error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// email
			// get and check email value
			String _emailValue = JSONUtils
					.getStringFromJSONObject(
							personJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_email));
			if (null != _emailValue && !"null".equalsIgnoreCase(_emailValue)) {
				email = _emailValue;
			}

			// note
			// get and check note value
			String _noteValue = JSONUtils
					.getStringFromJSONObject(
							personJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_note));
			if (null != _noteValue && !"null".equalsIgnoreCase(_noteValue)) {
				note = _noteValue;
			}
		} else {
			Log.e(LOG_TAG,
					"New person with JSON object error, contact JSON object = "
							+ personJSONObject);
		}
	}

	// constructor with cursor
	public PersonBean(Cursor cursor) {
		this();

		// check the cursor
		if (null != cursor) {
			// set person attributes
			// avatar
			avatar = cursor.getBlob(cursor
					.getColumnIndex(PersonConstants.AVATAR));

			// employee name
			employeeName = cursor.getString(cursor
					.getColumnIndex(PersonConstants.NAME));

			// sex
			// get and check sex column index
			int _sexColumnIndex = cursor.getColumnIndex(PersonConstants.SEX);
			if (!cursor.isNull(_sexColumnIndex)) {
				sex = PersonSex.getSex(cursor.getInt(_sexColumnIndex));
			}

			// birthday
			// get and check birthday column index
			int _birthdayColumnIndex = cursor
					.getColumnIndex(PersonConstants.BIRTHDAY);
			if (!cursor.isNull(_birthdayColumnIndex)) {
				birthday = cursor.getLong(_birthdayColumnIndex);
			}

			// department
			department = cursor.getString(cursor
					.getColumnIndex(PersonConstants.DEPARTMENT));

			// approve number
			approveNumber = cursor.getLong(cursor
					.getColumnIndex(PersonConstants.APPROVE_NUMBER));

			// email
			email = cursor.getString(cursor
					.getColumnIndex(PersonConstants.EMAIL));

			// note
			note = cursor
					.getString(cursor.getColumnIndex(PersonConstants.NOTE));
		} else {
			Log.e(LOG_TAG, "New person with cursor error, cursor = " + cursor);
		}
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public PersonSex getSex() {
		return sex;
	}

	public void setSex(PersonSex sex) {
		this.sex = sex;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Long getApproveNumber() {
		return approveNumber;
	}

	public void setApproveNumber(Long approveNumber) {
		this.approveNumber = approveNumber;
	}

	public Long getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(Long mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Long getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(Long officePhone) {
		this.officePhone = officePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	// comparable compare to
	public int compareTo(PersonBean another) {
		int _result = -1;

		// check person avatar url, employee name, sex, birthday, department,
		// mobile phone, office phone, email and note
		if ((null == employeeName && null == another.employeeName)
				|| (null != employeeName && null != another.employeeName && employeeName
						.equalsIgnoreCase(another.employeeName))) {
			if ((null == sex && null == another.sex)
					|| (null != sex && null != another.sex && sex == another.sex)) {
				if ((null == birthday && null == another.birthday)
						|| (null != birthday && null != birthday && birthday
								.longValue() == another.birthday.longValue())) {
					if ((null == department && null == another.department)
							|| (null != department
									&& null != another.department && department
										.equalsIgnoreCase(another.department))) {
						if ((null == mobilePhone && null == another.mobilePhone)
								|| (null != mobilePhone
										&& null != another.mobilePhone && mobilePhone
										.longValue() == another.mobilePhone
										.longValue())) {
							if ((null == officePhone && null == another.officePhone)
									|| (null != officePhone
											&& null != another.officePhone && officePhone
											.longValue() == another.officePhone
											.longValue())) {
								if ((null == email && null == another.email)
										|| (null != email
												&& null != another.email && email
													.equalsIgnoreCase(another.email))) {
									if ((null == note && null == another.note)
											|| (null != note
													&& null != another.note && note
														.equalsIgnoreCase(another.note))) {
										_result = 0;
									} else {
										Log.d(LOG_TAG,
												"Person note not equals, self note = "
														+ note
														+ " and another note = "
														+ another.note);
									}
								} else {
									Log.d(LOG_TAG,
											"Person email not equals, self email = "
													+ email
													+ " and another email = "
													+ another.email);
								}
							} else {
								Log.d(LOG_TAG,
										"Person office phone not equals, self office phone = "
												+ officePhone
												+ " and another office phone = "
												+ another.officePhone);
							}
						} else {
							Log.d(LOG_TAG,
									"Person mobile phone not equals, self mobile phone = "
											+ mobilePhone
											+ " and another mobile phone = "
											+ another.mobilePhone);
						}
					} else {
						Log.d(LOG_TAG,
								"Person department not equals, self department = "
										+ department
										+ " and another department = "
										+ another.department);
					}
				} else {
					Log.d(LOG_TAG,
							"Person birthday not equals, self birthday = "
									+ birthday + " and another birthday = "
									+ another.birthday);
				}
			} else {
				Log.d(LOG_TAG, "Person sex not equals, self sex = " + sex
						+ " and another sex = " + another.sex);
			}
		} else {
			Log.d(LOG_TAG,
					"Person employee name not equals, self employee name = "
							+ employeeName + " and another employee name = "
							+ another.employeeName);
		}

		return _result;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
