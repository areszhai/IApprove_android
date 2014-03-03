package com.futuo.iapprove.account.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.addressbook.person.PersonBean;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.provider.UserEnterpriseProfileContentProvider.EnterpriseProfiles.EnterpriseProfile;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.JSONUtils;

public class UserEnterpriseProfileBean implements
		Comparable<UserEnterpriseProfileBean>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7049603672369228031L;

	private static final String LOG_TAG = UserEnterpriseProfileBean.class
			.getCanonicalName();

	// row id, user profile, enterprise id and abbreviation
	private Long rowId;
	private PersonBean userProfile;
	private UserEnterpriseBean userEnterprise;

	// local storage data dirty type
	private LocalStorageDataDirtyType lsDataDirtyType;

	public UserEnterpriseProfileBean() {
		super();

		// set default local storage data dirty type
		lsDataDirtyType = LocalStorageDataDirtyType.NORMAL;
	}

	// constructor with JSON object
	public UserEnterpriseProfileBean(JSONObject userEnterpriseProfileJSONObejct) {
		this();

		// check user enterprise profile json object
		if (null != userEnterpriseProfileJSONObejct) {
			// set user enterprise profile attributes
			// user profile
			userProfile = new PersonBean(userEnterpriseProfileJSONObejct);

			// user enterprise
			userEnterprise = new UserEnterpriseBean(
					userEnterpriseProfileJSONObejct);
		} else {
			Log.e(LOG_TAG,
					"New user enterprise profile with json object error, json object = "
							+ userEnterpriseProfileJSONObejct);
		}
	}

	// constructor with cursor
	public UserEnterpriseProfileBean(Cursor cursor) {
		super();

		// check the cursor
		if (null != cursor) {
			// set user enterprise profile attributes
			// row id
			rowId = cursor
					.getLong(cursor.getColumnIndex(EnterpriseProfile._ID));

			// user profile
			userProfile = new PersonBean(cursor);

			// user enterprise
			userEnterprise = new UserEnterpriseBean(cursor);
		} else {
			Log.e(LOG_TAG,
					"New user enterprise profile with cursor error, cursor = "
							+ cursor);
		}
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public PersonBean getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(PersonBean userProfile) {
		this.userProfile = userProfile;
	}

	public UserEnterpriseBean getUserEnterprise() {
		return userEnterprise;
	}

	public void setUserEnterprise(UserEnterpriseBean userEnterprise) {
		this.userEnterprise = userEnterprise;
	}

	public LocalStorageDataDirtyType getLocalStorageDataDirtyType() {
		return lsDataDirtyType;
	}

	public void setLocalStorageDataDirtyType(
			LocalStorageDataDirtyType lsDataDirtyType) {
		this.lsDataDirtyType = lsDataDirtyType;
	}

	@Override
	public int compareTo(UserEnterpriseProfileBean another) {
		// get and check person compare result
		int _result = userProfile.compareTo(another.userProfile);
		if (0 == _result) {
			_result = userEnterprise.compareTo(another.userEnterprise);
		}

		return _result;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append user profile, enterprise id and abbreviation
		_description.append("Enterprise profile row id = ").append(rowId)
				.append(", ").append("user profile = ").append(userProfile)
				.append(" and ").append("enterprise = ").append(userEnterprise);

		return _description.toString();
	}

	// process user enterprise profile
	public static void processUserEnterpriseProfile(
			JSONArray userEnterpriseProfileJSONArray) {
		// get login user
		final UserBean _loginUser = UserManager.getInstance().getUser();

		// define user login user id
		Long _userLoginUserId = null;

		// define user login, last login enterprise id and user enterprise
		// profile list
		Long _userLoginEnterpriseId = null;
		Long _userLastLoginEnterpriseId = IAUserExtension
				.getUserLoginEnterpriseId(_loginUser);
		final List<UserEnterpriseProfileBean> _userEnterpriseProfileList = new ArrayList<UserEnterpriseProfileBean>();

		for (int i = 0; i < userEnterpriseProfileJSONArray.length(); i++) {
			// get user enterprise profile bean
			UserEnterpriseProfileBean _userEnterpriseProfile = new UserEnterpriseProfileBean(
					JSONUtils.getJSONObjectFromJSONArray(
							userEnterpriseProfileJSONArray, i));

			// set the first enterprise id as user login enterprise id
			if (0 == i) {
				_userLoginUserId = _userEnterpriseProfile.getUserProfile()
						.getUserId();
				_userLoginEnterpriseId = _userEnterpriseProfile
						.getUserEnterprise().getEnterpriseId();
			}

			// add user enterprise profile to user enterprise profile list
			_userEnterpriseProfileList.add(_userEnterpriseProfile);

			// check user last login enterprise id and compare with user
			// enterprise bean id
			if (null != _userLastLoginEnterpriseId
					&& _userLastLoginEnterpriseId.longValue() == _userEnterpriseProfile
							.getUserEnterprise().getEnterpriseId().longValue()) {
				_userLoginEnterpriseId = _userLastLoginEnterpriseId;
			}
		}

		// set user login enterprise id to approve user extension
		if (null != _userLoginEnterpriseId) {
			IAUserExtension.setUserLoginUserId(_loginUser, _userLoginUserId);
			IAUserExtension.setUserLoginEnterpriseId(_loginUser,
					_userLoginEnterpriseId);

			// save user login enterprise id to local storage
			DataStorageUtils.putObject(
					IAUserLocalStorageAttributes.USER_LASTLOGINENTERPRISEID
							.name(), _userLoginEnterpriseId);
		}

		// process user enterprise profiles in work thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				// get content resolver
				ContentResolver _contentResolver = CTApplication.getContext()
						.getContentResolver();

				// get local storage user enterprise profiles enterprise id as
				// key and bean as value map
				Map<Long, UserEnterpriseProfileBean> _localStorageUEProfilesEnterpriseIdAndBeanMap = getLocalStorageUEProfilesEnterpriseIdAndBeanMap(_contentResolver
						.query(EnterpriseProfile.ENTERPRISEPROFILES_CONTENT_URI,
								null,
								EnterpriseProfile.USER_ENTERPRISEPROFILES_WITHLOGINNAME_CONDITION,
								new String[] { _loginUser.getName() }, null));

				// define the user enterprise profile content values
				ContentValues _userEnterpriseProfileContentValues = new ContentValues();

				for (UserEnterpriseProfileBean userEnterpriseProfile : _userEnterpriseProfileList) {
					// clear the user enterprise profile content values
					_userEnterpriseProfileContentValues.clear();

					// generate the user enterprise profile content values with
					// user avatar, name, sex, birthday, department, approve
					// number, mobile phone, office phone, email, note,
					// enterprise id and abbreviation
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.AVATAR, userEnterpriseProfile
									.getUserProfile().getAvatar());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.NAME, userEnterpriseProfile
									.getUserProfile().getEmployeeName());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.SEX,
							null != userEnterpriseProfile.getUserProfile()
									.getSex() ? userEnterpriseProfile
									.getUserProfile().getSex().getValue()
									: null);
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.BIRTHDAY, userEnterpriseProfile
									.getUserProfile().getBirthday());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.DEPARTMENT, userEnterpriseProfile
									.getUserProfile().getDepartment());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.APPROVE_NUMBER,
							userEnterpriseProfile.getUserProfile()
									.getApproveNumber());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.MOBILE_PHONE,
							userEnterpriseProfile.getUserProfile()
									.getMobilePhone());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.OFFICE_PHONE,
							userEnterpriseProfile.getUserProfile()
									.getOfficePhone());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.EMAIL, userEnterpriseProfile
									.getUserProfile().getEmail());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.NOTE, userEnterpriseProfile
									.getUserProfile().getNote());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.ENTERPRISE_ID,
							userEnterpriseProfile.getUserEnterprise()
									.getEnterpriseId());
					_userEnterpriseProfileContentValues.put(
							EnterpriseProfile.ENTERPRISE_ABBREVIATION,
							userEnterpriseProfile.getUserEnterprise()
									.getEnterpriseAbbreviation());

					// insert the user enterprise profile if not existed in
					// local storage database or update if existed
					if (!_localStorageUEProfilesEnterpriseIdAndBeanMap.keySet()
							.contains(
									userEnterpriseProfile.getUserEnterprise()
											.getEnterpriseId())) {
						Log.d(LOG_TAG,
								"The user enterprise profile = "
										+ userEnterpriseProfile
										+ " for inserting into local storage database, its content values = "
										+ _userEnterpriseProfileContentValues);

						_contentResolver
								.insert(EnterpriseProfile.ENTERPRISEPROFILE_CONTENT_URI,
										_userEnterpriseProfileContentValues);
					} else {
						// get for updating address book contact
						UserEnterpriseProfileBean _4updatingUEProfile = _localStorageUEProfilesEnterpriseIdAndBeanMap
								.get(userEnterpriseProfile.getUserEnterprise()
										.getEnterpriseId());

						// update local storage user enterprise profile normal
						_4updatingUEProfile
								.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.NORMAL);

						// compare the got user enterprise profile with the for
						// updating user enterprise profile
						if (0 != userEnterpriseProfile
								.compareTo(_4updatingUEProfile)) {
							Log.d(LOG_TAG,
									"The user enterprise profile whose enterprise id = "
											+ _4updatingUEProfile
													.getUserEnterprise()
													.getEnterpriseId()
											+ " for updating to local storage database, its content values = "
											+ _userEnterpriseProfileContentValues);

							_contentResolver.update(
									ContentUris
											.withAppendedId(
													EnterpriseProfile.ENTERPRISEPROFILE_CONTENT_URI,
													_4updatingUEProfile
															.getRowId()),
									_userEnterpriseProfileContentValues, null,
									null);
						}
					}
				}

				// delete the local storage user enterprise profile for
				// synchronizing
				for (Long _localStorageUEProfileEnterpriseId : _localStorageUEProfilesEnterpriseIdAndBeanMap
						.keySet()) {
					// get the for deleting user enterprise profile
					UserEnterpriseProfileBean _4deletingUEProfile = _localStorageUEProfilesEnterpriseIdAndBeanMap
							.get(_localStorageUEProfileEnterpriseId);

					// check its data dirty type
					if (LocalStorageDataDirtyType.DELETE == _4deletingUEProfile
							.getLocalStorageDataDirtyType()) {
						Log.d(LOG_TAG,
								"The user enterprise profile whose enterprise id = "
										+ _4deletingUEProfile
												.getUserEnterprise()
												.getEnterpriseId()
										+ " will delete from local storage database");

						_contentResolver.delete(
								ContentUris
										.withAppendedId(
												EnterpriseProfile.ENTERPRISEPROFILE_CONTENT_URI,
												_4deletingUEProfile.getRowId()),
								null, null);
					}
				}
			}

		}).start();
	}

	// get local storage user enterprise profile enterprise id as key and bean
	// as value map
	private static Map<Long, UserEnterpriseProfileBean> getLocalStorageUEProfilesEnterpriseIdAndBeanMap(
			Cursor cursor) {
		Map<Long, UserEnterpriseProfileBean> _localStorageUEProfilesEnterpriseIdAndBeanMap = new HashMap<Long, UserEnterpriseProfileBean>();

		// check the cursor
		if (null != cursor) {
			// set all local storage user enterprise profile for deleting
			while (cursor.moveToNext()) {
				// get for deleting user enterprise profile
				UserEnterpriseProfileBean _4deletingUEProfile = new UserEnterpriseProfileBean(
						cursor);

				// set it for deleting
				_4deletingUEProfile
						.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

				// put user enterprise profile enterprise id and bean in
				_localStorageUEProfilesEnterpriseIdAndBeanMap.put(
						_4deletingUEProfile.getUserEnterprise()
								.getEnterpriseId(), _4deletingUEProfile);
			}

			// close cursor
			cursor.close();
		}

		return _localStorageUEProfilesEnterpriseIdAndBeanMap;
	}

}
