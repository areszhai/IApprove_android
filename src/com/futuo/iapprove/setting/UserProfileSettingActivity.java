package com.futuo.iapprove.setting;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.futuo.iapprove.R;
import com.futuo.iapprove.addressbook.person.PersonBean;
import com.futuo.iapprove.addressbook.person.PersonSex;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.customwidget.UserProfileSettingFormItem;

public class UserProfileSettingActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = UserProfileSettingActivity.class
			.getCanonicalName();

	// user profile birthday date format string
	private static final String USERPROFILE_BIRTHDAY_DATEFORMATSTRING = "yyyy-MM-dd";

	// user profile bean
	private PersonBean _mUserProfileBean;

	// user profile sex, birthday, department, mobile phone, office phone, email
	// and note user profile setting form item
	private UserProfileSettingFormItem _mSexUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mBirthdayUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mDepartmentUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mMobilePhoneUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mOfficePhoneUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mEmailUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mNoteUserProfileSettingFormItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.user_profile_setting_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// check the data
		if (null != _extraData) {
			// get user profile setting user profile bean
			_mUserProfileBean = (PersonBean) _extraData
					.getSerializable(UserProfileSettingExtraData.USERPROFILE_SETTING_USERPROFILE);
		}

		// set subViews
		// set title
		setTitle(R.string.userProfile_setting_nav_title);

		// get user profile avatar and set as avatar setting user avatar
		// imageView image
		//

		// bind avatar setting relativeLayout on click listener
		((RelativeLayout) findViewById(R.id.upst_avatarSetting_relativeLayout))
				.setOnClickListener(null);

		// set approve number and employee name user profile setting form item
		// info
		((UserProfileSettingFormItem) findViewById(R.id.upst_approveNumber_userProfileSettingFormItem))
				.setInfo(_mUserProfileBean.getApproveNumber().toString());
		((UserProfileSettingFormItem) findViewById(R.id.upst_employeeName_userProfileSettingFormItem))
				.setInfo(_mUserProfileBean.getEmployeeName());

		// get sex user profile setting form item
		_mSexUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_sex_userProfileSettingFormItem);

		// set its info
		// get and check user sex value
		PersonSex _sexValue = _mUserProfileBean.getSex();
		if (null != _sexValue) {
			_mSexUserProfileSettingFormItem.setInfo(_sexValue.name());
		}

		// bind its on click listener
		_mSexUserProfileSettingFormItem.setOnClickListener(null);

		// get birthday user profile setting form item
		_mBirthdayUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_birthday_userProfileSettingFormItem);

		// set its info
		// get and check user birthday value
		Long _birthdayValue = _mUserProfileBean.getBirthday();
		if (null != _birthdayValue && 0 != _birthdayValue) {
			_mBirthdayUserProfileSettingFormItem.setInfo(new SimpleDateFormat(
					USERPROFILE_BIRTHDAY_DATEFORMATSTRING, Locale.getDefault())
					.format(_birthdayValue));
		}

		// bind its on click listener
		_mBirthdayUserProfileSettingFormItem.setOnClickListener(null);

		// get department user profile setting form item
		_mDepartmentUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_department_userProfileSettingFormItem);

		// set its info
		// get and check user department value
		String _departmentValue = _mUserProfileBean.getDepartment();
		if (null != _departmentValue) {
			_mDepartmentUserProfileSettingFormItem.setInfo(_departmentValue);
		}

		// bind its on click listener
		_mDepartmentUserProfileSettingFormItem.setOnClickListener(null);

		// get mobile phone user profile setting form item
		_mMobilePhoneUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_mobilePhone_userProfileSettingFormItem);

		// set its info
		// get and check user mobile phone value
		Long _mobilePhoneValue = _mUserProfileBean.getMobilePhone();
		if (null != _mobilePhoneValue) {
			_mMobilePhoneUserProfileSettingFormItem.setInfo(_mobilePhoneValue
					.toString());
		}

		// bind its on click listener
		_mMobilePhoneUserProfileSettingFormItem.setOnClickListener(null);

		// get office phone user profile setting form item
		_mOfficePhoneUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_officePhone_userProfileSettingFormItem);

		// set its info
		// get and check user office phone value
		Long _officePhoneValue = _mUserProfileBean.getOfficePhone();
		if (null != _officePhoneValue) {
			_mOfficePhoneUserProfileSettingFormItem.setInfo(_officePhoneValue
					.toString());
		}

		// bind its on click listener
		_mOfficePhoneUserProfileSettingFormItem.setOnClickListener(null);

		// get email user profile setting form item
		_mEmailUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_email_userProfileSettingFormItem);

		// set its info
		// get and check user email value
		String _emailValue = _mUserProfileBean.getEmail();
		if (null != _emailValue) {
			_mEmailUserProfileSettingFormItem.setInfo(_emailValue);
		}

		// bind its on click listener
		_mEmailUserProfileSettingFormItem.setOnClickListener(null);

		// get note user profile setting form item
		_mNoteUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_note_userProfileSettingFormItem);

		// set its info
		// get and check user note value
		String _noteValue = _mUserProfileBean.getNote();
		if (null != _noteValue) {
			_mNoteUserProfileSettingFormItem.setInfo(_noteValue);
		}

		// bind its on click listener
		_mNoteUserProfileSettingFormItem.setOnClickListener(null);
	}

	// inner class
	// user profile setting extra data constant
	public static final class UserProfileSettingExtraData {

		// user profile setting user profile bean
		public static final String USERPROFILE_SETTING_USERPROFILE = "userprofile_setting_userprofile";

	}

}
