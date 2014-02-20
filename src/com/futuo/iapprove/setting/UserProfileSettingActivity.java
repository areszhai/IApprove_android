package com.futuo.iapprove.setting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.futuo.iapprove.R;
import com.futuo.iapprove.addressbook.ABContactPhoneType;
import com.futuo.iapprove.addressbook.person.PersonBean;
import com.futuo.iapprove.addressbook.person.PersonSex;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.customwidget.UserProfileSettingFormItem;
import com.futuo.iapprove.setting.UserProfileSettingFormItemEditorActivity.UserProfileSettingFormItemEditorExtraData;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.richitec.commontoolkit.customcomponent.CTPopupWindow;
import com.richitec.commontoolkit.utils.CommonUtils;

public class UserProfileSettingActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = UserProfileSettingActivity.class
			.getCanonicalName();

	// user profile birthday date format string
	private static final String USERPROFILE_BIRTHDAY_DATEFORMATSTRING = "yyyy-MM-dd";

	// user profile bean
	private PersonBean _mUserProfileBean;

	// user profile avatar imageView
	private ImageView _mAvatarImageView;

	// user profile sex, birthday, department, mobile phone, office phone, email
	// and note user profile setting form item
	private UserProfileSettingFormItem _mSexUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mBirthdayUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mDepartmentUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mMobilePhoneUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mOfficePhoneUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mEmailUserProfileSettingFormItem;
	private UserProfileSettingFormItem _mNoteUserProfileSettingFormItem;

	// user avatar setting photo source select popup window
	private final UserAvatarSettingPhotoSourceSelectPopupWindow USERAVATARSETTING_PHOTOSOURCE_SELECT_POPUPWINDOW = new UserAvatarSettingPhotoSourceSelectPopupWindow(
			R.layout.useravatarsetting_photosource_select_popupwindow_layout,
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

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

		// bind avatar setting relativeLayout on click listener
		((RelativeLayout) findViewById(R.id.upst_avatarSetting_relativeLayout))
				.setOnClickListener(new UserAvatarSettingRelativeLayoutOnClickListener());

		// get user profile avatar imageView
		_mAvatarImageView = (ImageView) findViewById(R.id.upst_avatarSetting_userAvatar_imageView);

		// get user profile avatar data and set as avatar setting user avatar
		// imageView image
		byte[] _avatarData = _mUserProfileBean.getAvatar();
		if (null != _avatarData) {
			try {
				// get avatar data stream
				InputStream _avatarDataStream = new ByteArrayInputStream(
						_avatarData);

				// set avatar
				_mAvatarImageView.setImageBitmap(BitmapFactory
						.decodeStream(_avatarDataStream));

				// close photo data stream
				_avatarDataStream.close();
			} catch (IOException e) {
				Log.e(LOG_TAG,
						"Get user profile avatar data stream error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

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
			_mSexUserProfileSettingFormItem.setInfo(_sexValue.getName());
		}

		// bind its on click listener
		_mSexUserProfileSettingFormItem
				.setOnClickListener(new UserSexProfileSettingFormItemOnClickListener());

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
		_mBirthdayUserProfileSettingFormItem
				.setOnClickListener(new UserBirthdayProfileSettingFormItemOnClickListener());

		// get department user profile setting form item
		_mDepartmentUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_department_userProfileSettingFormItem);

		// set its info
		// get and check user department value
		String _departmentValue = _mUserProfileBean.getDepartment();
		if (null != _departmentValue) {
			_mDepartmentUserProfileSettingFormItem.setInfo(_departmentValue);
		}

		// bind its on click listener
		_mDepartmentUserProfileSettingFormItem
				.setOnClickListener(new UserDepartmentProfileSettingFormItemOnClickListener());

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
		_mMobilePhoneUserProfileSettingFormItem
				.setOnClickListener(new UserPhoneProfileSettingFormItemOnClickListener(
						ABContactPhoneType.MOBILE));

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
		_mOfficePhoneUserProfileSettingFormItem
				.setOnClickListener(new UserPhoneProfileSettingFormItemOnClickListener(
						ABContactPhoneType.OFFICE));

		// get email user profile setting form item
		_mEmailUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_email_userProfileSettingFormItem);

		// set its info
		// get and check user email value
		String _emailValue = _mUserProfileBean.getEmail();
		if (null != _emailValue) {
			_mEmailUserProfileSettingFormItem.setInfo(_emailValue);
		}

		// bind its on click listener
		_mEmailUserProfileSettingFormItem
				.setOnClickListener(new UserEmailProfileSettingFormItemOnClickListener());

		// get note user profile setting form item
		_mNoteUserProfileSettingFormItem = (UserProfileSettingFormItem) findViewById(R.id.upst_note_userProfileSettingFormItem);

		// set its info
		// get and check user note value
		String _noteValue = _mUserProfileBean.getNote();
		if (null != _noteValue) {
			_mNoteUserProfileSettingFormItem.setInfo(_noteValue);
		}

		// bind its on click listener
		_mNoteUserProfileSettingFormItem
				.setOnClickListener(new UserNoteProfileSettingFormItemOnClickListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// check result code
		switch (resultCode) {
		case RESULT_OK:
			// check request code
			switch (requestCode) {
			case UserProfileSettingRequestCode.USER_SEX_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
			case UserProfileSettingRequestCode.USER_BIRTHDAY_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
			case UserProfileSettingRequestCode.USER_DEPARTMENT_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
			case UserProfileSettingRequestCode.USER_MOBILEPHONE_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
			case UserProfileSettingRequestCode.USER_OFFICEPHONE_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
			case UserProfileSettingRequestCode.USER_EMAIL_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
			case UserProfileSettingRequestCode.USER_NOTE_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
				// check data
				if (null != data) {
					// get user profile setting form item editor need to update
					// info value
					String _need2updateInfoValue = data
							.getExtras()
							.getString(
									UserProfileSettingExtraData.USERPROFILESETTING_FORMITEM_EDITOR_NEED2UPDATEINFO);

					// check request code again and set the user profile setting
					// form item form item info
					switch (requestCode) {
					case UserProfileSettingRequestCode.USER_SEX_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
						_mSexUserProfileSettingFormItem
								.setInfo(_need2updateInfoValue);
						break;

					case UserProfileSettingRequestCode.USER_BIRTHDAY_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
						_mBirthdayUserProfileSettingFormItem
								.setInfo(_need2updateInfoValue);
						break;

					case UserProfileSettingRequestCode.USER_DEPARTMENT_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
						_mDepartmentUserProfileSettingFormItem
								.setInfo(_need2updateInfoValue);
						break;

					case UserProfileSettingRequestCode.USER_MOBILEPHONE_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
						_mMobilePhoneUserProfileSettingFormItem
								.setInfo(_need2updateInfoValue);
						break;

					case UserProfileSettingRequestCode.USER_OFFICEPHONE_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
						_mOfficePhoneUserProfileSettingFormItem
								.setInfo(_need2updateInfoValue);
						break;

					case UserProfileSettingRequestCode.USER_EMAIL_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
						_mEmailUserProfileSettingFormItem
								.setInfo(_need2updateInfoValue);
						break;

					case UserProfileSettingRequestCode.USER_NOTE_PROFILESETTING_FORMITEM_EDITOR_REQCODE:
						_mNoteUserProfileSettingFormItem
								.setInfo(_need2updateInfoValue);
						break;
					}
				}
				break;
			}
			break;

		default:
			// nothing to do
			break;
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		AppDataSaveRestoreUtils.onRestoreInstanceState(savedInstanceState);

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtils.onSaveInstanceState(outState);

		super.onSaveInstanceState(outState);
	}

	// generate user profile setting form item editor extra data map with user
	// profile setting form item
	private Map<String, Object> generateUserProfileSettingFormItemEditorExtraDataMap(
			UserProfileSettingFormItem userProfileSettingFormItem) {
		// define user profile setting form item editor extra data map
		Map<String, Object> _extraMap = new HashMap<String, Object>();

		// put user profile setting form item label and info value to extra data
		// map as param
		_extraMap
				.put(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_LABEL,
						userProfileSettingFormItem.getLabel());
		_extraMap
				.put(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_INFOVALUE,
						userProfileSettingFormItem.getInfo());

		return _extraMap;
	}

	// inner class
	// user profile setting extra data constant
	public static final class UserProfileSettingExtraData {

		// user profile setting user profile bean
		public static final String USERPROFILE_SETTING_USERPROFILE = "userprofile_setting_userprofile";

		// user profile setting form item editor need to update info value
		public static final String USERPROFILESETTING_FORMITEM_EDITOR_NEED2UPDATEINFO = "userprofilesetting_formitem_editor_need2updateinfo";

	}

	// user profile setting request code
	static class UserProfileSettingRequestCode {

		// user sex, birthday, department, mobile phone, office phone, email and
		// note profile setting form item editor request code
		private static final int USER_SEX_PROFILESETTING_FORMITEM_EDITOR_REQCODE = 500;
		private static final int USER_BIRTHDAY_PROFILESETTING_FORMITEM_EDITOR_REQCODE = 501;
		private static final int USER_DEPARTMENT_PROFILESETTING_FORMITEM_EDITOR_REQCODE = 502;
		private static final int USER_MOBILEPHONE_PROFILESETTING_FORMITEM_EDITOR_REQCODE = 503;
		private static final int USER_OFFICEPHONE_PROFILESETTING_FORMITEM_EDITOR_REQCODE = 504;
		private static final int USER_EMAIL_PROFILESETTING_FORMITEM_EDITOR_REQCODE = 505;
		private static final int USER_NOTE_PROFILESETTING_FORMITEM_EDITOR_REQCODE = 506;

	}

	// user avatar setting relativeLayout on click listener
	class UserAvatarSettingRelativeLayoutOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// show user avatar setting photo source select popup window
			USERAVATARSETTING_PHOTOSOURCE_SELECT_POPUPWINDOW.showAtLocation(v,
					Gravity.CENTER, 0, 0);
		}

	}

	// user avatar setting photo source select popup window
	class UserAvatarSettingPhotoSourceSelectPopupWindow extends CTPopupWindow {

		public UserAvatarSettingPhotoSourceSelectPopupWindow(int resource,
				int width, int height) {
			super(resource, width, height);
		}

		@Override
		protected void bindPopupWindowComponentsListener() {
			// bind user avatar setting photo source select take, choose photo
			// and cancel button click listener
			((Button) getContentView().findViewById(
					R.id.uaspsspw_photoSourceSelect_takePhoto_button))
					.setOnClickListener(new UserAvatarSettingPhotoSourceSelectTakePhotoBtnOnClickListener());
			((Button) getContentView().findViewById(
					R.id.uaspsspw_photoSourceSelect_choosePhoto_button))
					.setOnClickListener(new UserAvatarSettingPhotoSourceSelectChoosePhotoBtnOnClickListener());
			((Button) getContentView().findViewById(
					R.id.uaspsspw_photoSourceSelect_cancel_button))
					.setOnClickListener(new UserAvatarSettingPhotoSourceSelectCancelBtnOnClickListener());
		}

		@Override
		protected void resetPopupWindow() {
			// nothing to do
		}

		// inner class
		// user avatar setting photo source select take photo button on click
		// listener
		class UserAvatarSettingPhotoSourceSelectTakePhotoBtnOnClickListener
				implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss user avatar setting photo source select popup window
				dismiss();

				// take photo
				// test by ares
				//
			}

		}

		// user avatar setting photo source select choose photo button on click
		// listener
		class UserAvatarSettingPhotoSourceSelectChoosePhotoBtnOnClickListener
				implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss user avatar setting photo source select popup window
				dismiss();

				// choose photo
				// test by ares
				//
			}

		}

		// user avatar setting photo source select cancel button on click
		// listener
		class UserAvatarSettingPhotoSourceSelectCancelBtnOnClickListener
				implements OnClickListener {

			@Override
			public void onClick(View v) {
				// dismiss user avatar setting photo source select popup window
				dismiss();
			}

		}

	}

	// user sex profile setting form item on click listener
	class UserSexProfileSettingFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// generate user sex profile setting form item editor extra data map
			Map<String, Object> _extraMap = generateUserProfileSettingFormItemEditorExtraDataMap(_mSexUserProfileSettingFormItem);

			// put user profile setting form item type and spinner content to
			// extra data map as param
			_extraMap
					.put(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_TYPE,
							UserProfileSettingFormItemType.SPINNER);
			_extraMap
					.put(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_SPINNERCONTENT,
							CommonUtils.array2List(getResources()
									.getStringArray(R.array.abc_sex_names)));

			// go to user profile setting form item editor activity with extra
			// data map
			pushActivityForResult(
					UserProfileSettingFormItemEditorActivity.class,
					_extraMap,
					UserProfileSettingRequestCode.USER_SEX_PROFILESETTING_FORMITEM_EDITOR_REQCODE);
		}

	}

	// user birthday profile setting form item on click listener
	class UserBirthdayProfileSettingFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// generate user birthday profile setting form item editor extra
			// data map
			Map<String, Object> _extraMap = generateUserProfileSettingFormItemEditorExtraDataMap(_mBirthdayUserProfileSettingFormItem);

			// put user profile setting form item type to extra data map as
			// param
			_extraMap
					.put(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_TYPE,
							UserProfileSettingFormItemType.DATE);

			// go to user profile setting form item editor activity with extra
			// data map
			pushActivityForResult(
					UserProfileSettingFormItemEditorActivity.class,
					_extraMap,
					UserProfileSettingRequestCode.USER_BIRTHDAY_PROFILESETTING_FORMITEM_EDITOR_REQCODE);
		}

	}

	// user department profile setting form item on click listener
	class UserDepartmentProfileSettingFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// generate user department profile setting form item editor extra
			// data map
			Map<String, Object> _extraMap = generateUserProfileSettingFormItemEditorExtraDataMap(_mDepartmentUserProfileSettingFormItem);

			// put user profile setting form item type to extra data map as
			// param
			_extraMap
					.put(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_TYPE,
							UserProfileSettingFormItemType.TEXTEDIT_TEXT);

			// go to user profile setting form item editor activity with extra
			// data map
			pushActivityForResult(
					UserProfileSettingFormItemEditorActivity.class,
					_extraMap,
					UserProfileSettingRequestCode.USER_DEPARTMENT_PROFILESETTING_FORMITEM_EDITOR_REQCODE);
		}

	}

	// user phone profile setting form item on click listener
	class UserPhoneProfileSettingFormItemOnClickListener implements
			OnClickListener {

		// user phone type
		private ABContactPhoneType _mUserPhoneType;

		public UserPhoneProfileSettingFormItemOnClickListener(
				ABContactPhoneType userPhoneType) {
			super();

			// save user phone type
			_mUserPhoneType = userPhoneType;
		}

		@Override
		public void onClick(View v) {
			// define user phone profile setting form item and request code
			UserProfileSettingFormItem _userPhoneProfileSettingFormItem = null;
			int _userPhoneProfileSettingFormItemEditorReqCode = 0;

			// check user phone type then get user phone profile setting form
			// item and request code
			switch (_mUserPhoneType) {
			case MOBILE:
				_userPhoneProfileSettingFormItem = _mMobilePhoneUserProfileSettingFormItem;
				_userPhoneProfileSettingFormItemEditorReqCode = UserProfileSettingRequestCode.USER_MOBILEPHONE_PROFILESETTING_FORMITEM_EDITOR_REQCODE;
				break;

			case OFFICE:
				_userPhoneProfileSettingFormItem = _mOfficePhoneUserProfileSettingFormItem;
				_userPhoneProfileSettingFormItemEditorReqCode = UserProfileSettingRequestCode.USER_OFFICEPHONE_PROFILESETTING_FORMITEM_EDITOR_REQCODE;
				break;

			case OTHERS:
			default:
				// nothing to do
				return;
			}

			// generate user phone profile setting form item editor extra data
			// map
			Map<String, Object> _extraMap = generateUserProfileSettingFormItemEditorExtraDataMap(_userPhoneProfileSettingFormItem);

			// put user profile setting form item type to extra data map as
			// param
			_extraMap
					.put(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_TYPE,
							UserProfileSettingFormItemType.TEXTEDIT_PHONE);

			// go to user profile setting form item editor activity with extra
			// data map
			pushActivityForResult(
					UserProfileSettingFormItemEditorActivity.class, _extraMap,
					_userPhoneProfileSettingFormItemEditorReqCode);
		}

	}

	// user email profile setting form item on click listener
	class UserEmailProfileSettingFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// generate user phone profile setting form item editor extra data
			// map
			Map<String, Object> _extraMap = generateUserProfileSettingFormItemEditorExtraDataMap(_mEmailUserProfileSettingFormItem);

			// put user profile setting form item type to extra data map as
			// param
			_extraMap
					.put(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_TYPE,
							UserProfileSettingFormItemType.TEXTEDIT_EMAIL);

			// go to user profile setting form item editor activity with extra
			// data map
			pushActivityForResult(
					UserProfileSettingFormItemEditorActivity.class,
					_extraMap,
					UserProfileSettingRequestCode.USER_EMAIL_PROFILESETTING_FORMITEM_EDITOR_REQCODE);
		}

	}

	// user note profile setting form item on click listener
	class UserNoteProfileSettingFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// generate user phone profile setting form item editor extra data
			// map
			Map<String, Object> _extraMap = generateUserProfileSettingFormItemEditorExtraDataMap(_mNoteUserProfileSettingFormItem);

			// put user profile setting form item type to extra data map as
			// param
			_extraMap
					.put(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_TYPE,
							UserProfileSettingFormItemType.TEXTAREA);

			// go to user profile setting form item editor activity with extra
			// data map
			pushActivityForResult(
					UserProfileSettingFormItemEditorActivity.class,
					_extraMap,
					UserProfileSettingRequestCode.USER_NOTE_PROFILESETTING_FORMITEM_EDITOR_REQCODE);
		}

	}

}
