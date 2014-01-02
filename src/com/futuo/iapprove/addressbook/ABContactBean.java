package com.futuo.iapprove.addressbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.addressbook.person.PersonBean;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.richitec.commontoolkit.utils.JSONUtils;

public class ABContactBean extends PersonBean implements
		Comparable<ABContactBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7615522783042300054L;

	private static final String LOG_TAG = ABContactBean.class
			.getCanonicalName();

	// row id, user id, nickname, phones and frequency
	private Long rowId;
	private Long userId;
	private String nickname;
	private List<ABContactPhoneBean> phones;
	private Long frequency;

	// local storage data dirty type
	private LocalStorageDataDirtyType lsDataDirtyType;

	// constructor with JSON object
	public ABContactBean(JSONObject contactJSONObject) {
		super(contactJSONObject);

		// initialize address book contact attributes
		initABContactAttrs();

		// check address book contact JSON object
		if (null != contactJSONObject) {
			// set address book contact attributes
			// user id
			try {
				userId = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										contactJSONObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseABReqResp_employee_id)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get address book contact user id error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// nickname
			// get and check nickname value
			String _nicknameValue = JSONUtils
					.getStringFromJSONObject(
							contactJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseABReqResp_employee_nickname));
			if (null != _nicknameValue
					&& !"null".equalsIgnoreCase(_nicknameValue)) {
				nickname = _nicknameValue;
			}

			// phones
			// get and check phones value
			List<ABContactPhoneBean> _phonesValue = ABContactPhoneBean
					.getContactPhones(contactJSONObject);
			if (null != _phonesValue) {
				phones.addAll(_phonesValue);
			}
		} else {
			Log.e(LOG_TAG,
					"New address book contact with JSON object error, contact JSON object = "
							+ contactJSONObject);
		}
	}

	// constructor with cursor
	public ABContactBean(Cursor cursor) {
		super(cursor);

		// initialize address book contact attributes
		initABContactAttrs();

		// check the cursor
		if (null != cursor) {
			// set address book contact attributes
			// row id
			rowId = cursor.getLong(cursor.getColumnIndex(Employee._ID));

			// user id
			userId = cursor.getLong(cursor.getColumnIndex(Employee.USER_ID));

			// nickname
			nickname = cursor.getString(cursor
					.getColumnIndex(Employee.NICKNAME));

			// phones
			// get and check phones value
			List<ABContactPhoneBean> _phonesValue = ABContactPhoneBean
					.getContactPhones(cursor);
			if (null != _phonesValue) {
				phones.addAll(_phonesValue);

				// update mobile and office phone
				mobilePhone = getMobilePhone();
				officePhone = getOfficePhone();
			}

			// frequency
			frequency = cursor.getLong(cursor
					.getColumnIndex(Employee.FREQUENCY));
		} else {
			Log.e(LOG_TAG,
					"New address book contact with cursor error, cursor = "
							+ cursor);
		}
	}

	// initialize address book contact attributes
	private void initABContactAttrs() {
		// set default frequency
		frequency = 0L;

		// initialize phones list
		phones = new ArrayList<ABContactPhoneBean>();

		// set default local storage data dirty type
		lsDataDirtyType = LocalStorageDataDirtyType.NORMAL;
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public List<ABContactPhoneBean> getPhones() {
		return phones;
	}

	// get phone with type
	public ABContactPhoneBean getPhone(ABContactPhoneType phoneType) {
		ABContactPhoneBean _phone = null;

		for (ABContactPhoneBean phone : phones) {
			// check phone type
			if (phoneType == phone.getType()) {
				// get phone
				_phone = phone;

				// break immediately
				break;
			}
		}

		return _phone;
	}

	@Override
	public Long getMobilePhone() {
		Long _mobilePhoneNumber = mobilePhone;

		// check mobile phone
		if (null == mobilePhone) {
			// get and check mobile phone
			ABContactPhoneBean _mobilePhone = getPhone(ABContactPhoneType.MOBILE);
			if (null != _mobilePhone) {
				// get mobile phone number
				_mobilePhoneNumber = mobilePhone = _mobilePhone.getNumber();
			}
		}

		return _mobilePhoneNumber;
	}

	// get mobile phone type label
	public String getMobilePhoneLabel() {
		String _mobilePhoneLabel = "";

		// get and check mobile phone
		ABContactPhoneBean _mobilePhone = getPhone(ABContactPhoneType.MOBILE);
		if (null != _mobilePhone) {
			// get mobile phone type label
			_mobilePhoneLabel = _mobilePhone.getTypeLabel();
		}

		return _mobilePhoneLabel;
	}

	@Override
	public Long getOfficePhone() {
		Long _officePhoneNumber = officePhone;

		// check office phone
		if (null == officePhone) {
			// get and check office phone
			ABContactPhoneBean _officePhone = getPhone(ABContactPhoneType.OFFICE);
			if (null != _officePhone) {
				// get office phone number
				_officePhoneNumber = officePhone = _officePhone.getNumber();
			}
		}

		return _officePhoneNumber;
	}

	// get office phone type label
	public String getOfficePhoneLabel() {
		String _officePhoneLabel = "";

		// get and check office phone
		ABContactPhoneBean _officePhone = getPhone(ABContactPhoneType.OFFICE);
		if (null != _officePhone) {
			// get office phone type label
			_officePhoneLabel = _officePhone.getTypeLabel();
		}

		return _officePhoneLabel;
	}

	public void setPhones(List<ABContactPhoneBean> phones) {
		// set mobile and office phone
		mobilePhone = getPhone(ABContactPhoneType.MOBILE).getNumber();
		officePhone = getPhone(ABContactPhoneType.OFFICE).getNumber();

		this.phones = phones;
	}

	public Long getFrequency() {
		return frequency;
	}

	public void setFrequency(Long frequency) {
		this.frequency = frequency;
	}

	public LocalStorageDataDirtyType getLocalStorageDataDirtyType() {
		return lsDataDirtyType;
	}

	public void setLocalStorageDataDirtyType(
			LocalStorageDataDirtyType lsDataDirtyType) {
		this.lsDataDirtyType = lsDataDirtyType;
	}

	@Override
	public int compareTo(ABContactBean another) {
		// get and check person compare result
		int _result = super.compareTo(another);
		if (0 == _result) {
			// address book compare
			// check address book contact nickname
			if ((null == nickname && null == another.nickname)
					|| (null != nickname && null != another.nickname && nickname
							.equalsIgnoreCase(another.nickname))) {
				_result = 0;
			} else {
				Log.d(LOG_TAG,
						"Address book contact nickname not equals, self nickname = "
								+ nickname + " and another nickname = "
								+ another.nickname);
			}
		}

		return _result;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append enterprise address book contact id, avatar url, employee name,
		// sex, nickname, birthday, department, approve number, phones, email,
		// note and frequency
		_description
				.append("Enterprise address book contact row id = ")
				.append(rowId)
				.append(", ")
				.append("user id = ")
				.append(userId)
				.append(", ")
				.append("avatar url = ")
				.append(avatarUrl)
				.append(", ")
				.append("employee name = ")
				.append(employeeName)
				.append(", ")
				.append("sex = ")
				.append(sex)
				.append(", ")
				.append("nickname = ")
				.append(nickname)
				.append(", ")
				.append("birthday = ")
				.append(new SimpleDateFormat("", Locale.getDefault())
						.format(new Date(birthday))).append(", ")
				.append("department = ").append(department).append(", ")
				.append("approve number = ").append(approveNumber).append(", ")
				.append("phones list = ").append(phones).append(", ")
				.append("email = ").append(email).append(", ")
				.append("note = ").append(note).append(" and ")
				.append("frequency = ").append(frequency);

		return _description.toString();
	}

}
