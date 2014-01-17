package com.futuo.iapprove.tab7tabcontent;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.account.user.IAUserLocalStorageAttributes;
import com.futuo.iapprove.addressbook.person.PersonBean;
import com.futuo.iapprove.assistant.AboutActivity;
import com.futuo.iapprove.assistant.FeedbackActivity;
import com.futuo.iapprove.assistant.SupportActivity;
import com.futuo.iapprove.customwidget.IApproveTabContentActivity;
import com.futuo.iapprove.customwidget.SettingFormItem;
import com.futuo.iapprove.provider.UserEnterpriseProfileContentProvider.EnterpriseProfiles.EnterpriseProfile;
import com.futuo.iapprove.setting.UserProfileSettingActivity;
import com.futuo.iapprove.setting.UserProfileSettingActivity.UserProfileSettingExtraData;
import com.richitec.commontoolkit.utils.DataStorageUtils;

public class SettingsTabContentActivity extends IApproveTabContentActivity {

	private static final String LOG_TAG = SettingsTabContentActivity.class
			.getCanonicalName();

	// user profile bean
	PersonBean _mUserProfile;

	// user profile setting avatar imageView, employee name and approve number
	// textView
	private ImageView _mAvatarImageView;
	private TextView _mEmployeeNameTextView;
	private TextView _mApproveNumberTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.settings_tab_content_activity_layout);

		// set subViews
		// set title
		setTitle(R.string.settings_tab7nav_title);

		// get user profile setting avatar imageView, employee name and approve
		// number textView
		_mAvatarImageView = (ImageView) findViewById(R.id.st_userProfileSetting_userAvatar_imageView);
		_mEmployeeNameTextView = (TextView) findViewById(R.id.st_userProfileSetting_userEmployeeName_textView);
		_mApproveNumberTextView = (TextView) findViewById(R.id.st_userProfileSetting_userApproveNumber_textView);

		// update user profile setting avatar, employee name and approve number
		updateUserProfileSettingFormItem();

		// bind user profile setting relativeLayout on click listener
		((RelativeLayout) findViewById(R.id.st_userProfileSetting_relativeLayout))
				.setOnClickListener(new UserProfileSettingRelativeLayoutOnClickListener());

		// bind rate, support, feedback, about and check for update setting form
		// item on click listener
		((SettingFormItem) findViewById(R.id.st_rate_settingFormItem))
				.setOnClickListener(new RateIApproveSettingFormItemOnClickListener());
		((SettingFormItem) findViewById(R.id.st_support_settingFormItem))
				.setOnClickListener(new SupportSettingFormItemOnClickListener());
		((SettingFormItem) findViewById(R.id.st_feedback_settingFormItem))
				.setOnClickListener(new FeedbackSettingFormItemOnClickListener());
		((SettingFormItem) findViewById(R.id.st_about_settingFormItem))
				.setOnClickListener(new AboutSettingFormItemOnClickListener());
		((SettingFormItem) findViewById(R.id.st_check4update_settingFormItem))
				.setOnClickListener(new Check4UpdateSettingFormItemOnClickListener());

		// bind user logout button on click listener
		((Button) findViewById(R.id.st_user_logout_button))
				.setOnClickListener(new UserLogoutBtnOnClickListener());
	}

	@Override
	protected void onUserEnterpriseChanged(Long newEnterpriseId) {
		super.onUserEnterpriseChanged(newEnterpriseId);

		// update user profile setting avatar, employee name and approve number
		updateUserProfileSettingFormItem();
	}

	// update user profile setting avatar, employee name and approve number
	private void updateUserProfileSettingFormItem() {
		// get and check user profile cursor
		Cursor _cursor = getContentResolver()
				.query(EnterpriseProfile.ENTERPRISEPROFILES_CONTENT_URI,
						null,
						EnterpriseProfile.USER_ENTERPRISEPROFILE_WITHENTERPRISEID7LOGINNAME_CONDITION,
						new String[] {
								IAUserExtension.getUserLoginEnterpriseId(
										_mLoginUser).toString(),
								_mLoginUser.getName() }, null);
		if (null != _cursor) {
			while (_cursor.moveToNext()) {
				// get user profile bean
				_mUserProfile = new PersonBean(_cursor);

				// break immediately
				break;
			}

			// close cursor
			_cursor.close();
		}

		// get user profile avatar and set as user profile setting user avatar
		// imageView image
		// test by ares
		_mAvatarImageView.setImageResource(R.drawable.img_dot_large_avatar);

		// get user profile employee name and set as user profile setting user
		// employee name textView text
		_mEmployeeNameTextView.setText(_mUserProfile.getEmployeeName());

		// get user profile approve number and set as user profile setting user
		// approve number textView text
		_mApproveNumberTextView
				.setText(String
						.format(getResources()
								.getString(
										R.string.st_userProfileSetting_userApproveNumber_format),
								_mUserProfile.getApproveNumber()));
	}

	// inner class
	// user profile setting relativeLayout on click listener
	class UserProfileSettingRelativeLayoutOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// define user profile setting extra data map
			Map<String, PersonBean> _extraMap = new HashMap<String, PersonBean>();

			// put user profile bean to extra data map as param
			_extraMap
					.put(UserProfileSettingExtraData.USERPROFILE_SETTING_USERPROFILE,
							_mUserProfile);

			// go to user profile setting activity with extra data map
			pushActivity(UserProfileSettingActivity.class, _extraMap);
		}

	}

	// rate iApprove setting form item on click listener
	class RateIApproveSettingFormItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Rate iApprove in android market");

			//
		}

	}

	// support setting form item on click listener
	class SupportSettingFormItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// go to support activity
			pushActivity(SupportActivity.class);
		}

	}

	// feedback setting form item on click listener
	class FeedbackSettingFormItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// go to feedback activity
			pushActivity(FeedbackActivity.class);
		}

	}

	// about setting form item on click listener
	class AboutSettingFormItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// go to about activity
			pushActivity(AboutActivity.class);
		}

	}

	// check for update setting form item on click listener
	class Check4UpdateSettingFormItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "IApprove check for update");

			//
		}

	}

	// user logout button on click listener
	class UserLogoutBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// show exit iApprove client alert dialog
			new AlertDialog.Builder(SettingsTabContentActivity.this)
					.setTitle(R.string.iApprove_exitAlertDialog_title)
					.setMessage(R.string.iApprove_exitAlertDialog_message)
					.setPositiveButton(
							R.string.iApprove_exitAlertDialog_exitButton_title,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									DataStorageUtils
											.putObject(
													IAUserLocalStorageAttributes.USER_LOGINKEY
															.name(), "");

									// exit iApprove project
									System.exit(0);
								}
							})
					.setNegativeButton(
							R.string.iApprove_exitAlertDialog_cancelButton_title,
							null).show();
		}

	}

}
