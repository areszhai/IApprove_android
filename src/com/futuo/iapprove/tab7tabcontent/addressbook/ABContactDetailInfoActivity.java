package com.futuo.iapprove.tab7tabcontent.addressbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.addressbook.person.PersonSex;
import com.futuo.iapprove.customwidget.ABContactInfoFormItem;
import com.futuo.iapprove.customwidget.CommonFormSeparator;
import com.futuo.iapprove.customwidget.IApproveImageBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationGenerator;

public class ABContactDetailInfoActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = ABContactDetailInfoActivity.class
			.getCanonicalName();

	// address book contact birthday date format string
	private static final String ABCONTACT_BIRTHDAY_DATEFORMATSTRING = "yyyy-MM-dd";

	// address book contact bean
	private ABContactBean _mABContactBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.abcontact_detail_info_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// check the data
		if (null != _extraData) {
			// get address book contact detail info contact bean
			_mABContactBean = (ABContactBean) _extraData
					.getSerializable(ABContactDetailInfoExtraData.ABCONTACT_DETAILINFO_CONTACT);
		}

		// set subViews
		// set title
		setTitle(R.string.abcontact_detailInfo_nav_title);

		// set more bar button item as right bar button item
		setRightBarButtonItem(new IApproveImageBarButtonItem(this,
				R.drawable.img_morebarbtnitem,
				new ABContactMoreButtonItemOnClickListener()));

		// avatar
		// get and check avatar data
		byte[] _avatarData = _mABContactBean.getAvatar();
		if (null != _avatarData) {
			try {
				// get avatar data stream
				InputStream _avatarDataStream = new ByteArrayInputStream(
						_avatarData);

				// set avatar
				((ImageView) findViewById(R.id.abcdi_avatar_imageView))
						.setImageBitmap(BitmapFactory
								.decodeStream(_avatarDataStream));

				// close photo data stream
				_avatarDataStream.close();
			} catch (IOException e) {
				Log.e(LOG_TAG,
						"Get contact avatar data stream error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

		// employee name
		((TextView) findViewById(R.id.abcdi_employeeName_textView))
				.setText(_mABContactBean.getEmployeeName());

		// sex
		// get and check sex value
		PersonSex _sexValue = _mABContactBean.getSex();
		if (null != _sexValue) {
			// set sex
			((ImageView) findViewById(R.id.abcdi_sex_imageView))
					.setImageDrawable(_mABContactBean.getSex().getIcon());
		} else {
			Log.e(LOG_TAG, "Address book contact = " + _mABContactBean
					+ " whoise sex = " + _mABContactBean.getSex());
		}

		// approve number
		((TextView) findViewById(R.id.abcdi_approveNumber_textView))
				.setText(String.format(
						getResources().getString(
								R.string.abcdi_approveNumber_text_format),
						_mABContactBean.getApproveNumber()));

		// nickname
		// get and check nickname value
		String _nicknameValue = _mABContactBean.getNickname();
		if (null != _nicknameValue && !"".equalsIgnoreCase(_nicknameValue)) {
			// set nickname
			((TextView) findViewById(R.id.abcdi_nickname_textView))
					.setText(_nicknameValue);
		}

		// get info form linearLayout
		LinearLayout _infoFormLinearLayout = (LinearLayout) findViewById(R.id.abcdi_infoForm_linearLayout);

		// birthday
		// get and check birthday value
		Long _birthdayValue = _mABContactBean.getBirthday();
		if (null != _birthdayValue && 0 != _birthdayValue) {
			// set birthday
			addABContactInfoFormItem(
					ABContactInfoFormItem.generateABContactInfoFormItem(
							getResources().getString(
									R.string.abcdi_birthday_infoFormItem_label),
							new SimpleDateFormat(
									ABCONTACT_BIRTHDAY_DATEFORMATSTRING, Locale
											.getDefault())
									.format(_birthdayValue)),
					_infoFormLinearLayout);
		}

		// department
		// get and check department value
		String _departmentValue = _mABContactBean.getDepartment();
		if (null != _departmentValue && !"".equalsIgnoreCase(_departmentValue)) {
			// set department
			addABContactInfoFormItem(
					ABContactInfoFormItem.generateABContactInfoFormItem(
							getResources()
									.getString(
											R.string.abcdi_department_infoFormItem_label),
							_departmentValue), _infoFormLinearLayout);
		}

		// mobile phone
		// get and check mobile phone value
		Long _mobilePhoneValue = _mABContactBean.getMobilePhone();
		if (null != _mobilePhoneValue) {
			// set mobile phone
			// generate mobile phone info form item
			ABContactInfoFormItem _mobilePhoneInfoFormItem = ABContactInfoFormItem
					.generateABContactInfoFormItem(
							String.format(
									getResources()
											.getString(
													R.string.abcdi_phone_infoFormItem_label_format),
									_mABContactBean.getMobilePhoneLabel()),
							_mobilePhoneValue.toString());

			// set on click listener
			_mobilePhoneInfoFormItem
					.setOnClickListener(new ABContactPhoneInfoFormItemOnClickListener());

			// add mobile phone info form item to linearLayout
			addABContactInfoFormItem(_mobilePhoneInfoFormItem,
					_infoFormLinearLayout);
		}

		// office phone
		// get and check office phone value
		Long _officePhoneValue = _mABContactBean.getOfficePhone();
		if (null != _officePhoneValue) {
			// set office phone
			// generate office phone info form item
			ABContactInfoFormItem _officePhoneInfoFormItem = ABContactInfoFormItem
					.generateABContactInfoFormItem(
							String.format(
									getResources()
											.getString(
													R.string.abcdi_phone_infoFormItem_label_format),
									_mABContactBean.getOfficePhoneLabel()),
							_officePhoneValue.toString());

			// set on click listener
			_officePhoneInfoFormItem
					.setOnClickListener(new ABContactPhoneInfoFormItemOnClickListener());

			// add office phone info form item to linearLayout
			addABContactInfoFormItem(_officePhoneInfoFormItem,
					_infoFormLinearLayout);
		}

		// email
		// get and check email value
		String _emailValue = _mABContactBean.getEmail();
		if (null != _emailValue && !"".equalsIgnoreCase(_emailValue)) {
			// set email
			// generate office phone info form item
			ABContactInfoFormItem _emailInfoFormItem = ABContactInfoFormItem
					.generateABContactInfoFormItem("邮件", _emailValue);

			// set on click listener
			_emailInfoFormItem
					.setOnClickListener(new ABContactEmailInfoFormItemOnClickListener());

			// add email info form item to linearLayout
			addABContactInfoFormItem(_emailInfoFormItem, _infoFormLinearLayout);
		}

		// bind new approve application button on click listener
		((Button) findViewById(R.id.abcdi_new_approveApplication_button))
				.setOnClickListener(new NewApproveApplicationBtnOnClickListener());
	}

	// add address book contact info(birthday, department, mobile phone, office
	// phone and email) as info form item to info form linearLayout
	private void addABContactInfoFormItem(ABContactInfoFormItem infoFormItem,
			LinearLayout infoFormLinearLayout) {
		// check address book contact info form item and its parent linearLayout
		if (null != infoFormItem && null != infoFormLinearLayout) {
			// show address book contact info form linearLayout if needed
			if (View.VISIBLE != infoFormLinearLayout.getVisibility()) {
				infoFormLinearLayout.setVisibility(View.VISIBLE);
			}

			// get and check address book contact info form linearLayout subview
			// count
			int _infoFormItemCount = infoFormLinearLayout.getChildCount();
			if (0 == _infoFormItemCount) {
				// single
				// check clickable and set address book contact info form item
				// background
				if (infoFormItem.isClickable()) {
					infoFormItem
							.setBackgroundResource(R.drawable.addressbook_contact_info_form_single_item_bg);
				}
			} else {
				// two and more
				// add separator line
				infoFormLinearLayout.addView(new CommonFormSeparator(this),
						new LayoutParams(LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));

				// check clickable and set address book contact info form item
				// background
				if (infoFormItem.isClickable()) {
					infoFormItem
							.setBackgroundResource(R.drawable.addressbook_contact_info_form_multiplebottom_item_bg);
				}

				for (int i = 0; i < _infoFormItemCount; i++) {
					// trim separator
					if (!(infoFormLinearLayout.getChildAt(i) instanceof CommonFormSeparator)) {
						// get each info form item existed
						ABContactInfoFormItem _existedInfoFormItem = (ABContactInfoFormItem) infoFormLinearLayout
								.getChildAt(i);

						// check clickable and set address book contact info
						// form item background
						if (_existedInfoFormItem.isClickable()) {
							// first
							if (0 == i) {
								_existedInfoFormItem
										.setBackgroundResource(R.drawable.addressbook_contact_info_form_multipletop_item_bg);
							} else {
								_existedInfoFormItem
										.setBackgroundResource(R.drawable.addressbook_contact_info_form_multiplemiddle_item_bg);
							}
						}
					}
				}
			}

			// add address book contact info form item to info form
			infoFormLinearLayout.addView(infoFormItem, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
		} else {
			Log.e(LOG_TAG, "Add address book contact info item = "
					+ infoFormItem + " to info form linearLayout = "
					+ infoFormLinearLayout + " error");
		}
	}

	// inner class
	// address book contact detail info extra data constant
	public static final class ABContactDetailInfoExtraData {

		// address book contact detail info contact bean
		public static final String ABCONTACT_DETAILINFO_CONTACT = "addressbook_contact_detailinfo_contact";

	}

	// address book contact more bar button item on click listener
	class ABContactMoreButtonItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View moreBarBtnItem) {
			Log.d(LOG_TAG, "More bar button item = " + moreBarBtnItem
					+ " on click listener");

			//
		}

	}

	// address book contact phone info form item on click listener
	class ABContactPhoneInfoFormItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View phoneInfoFormItem) {
			Log.d(LOG_TAG, "Click phone = "
					+ ((ABContactInfoFormItem) phoneInfoFormItem).getInfo());

			//
		}

	}

	// address book contact email info form item on click listener
	class ABContactEmailInfoFormItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View phoneInfoFormItem) {
			Log.d(LOG_TAG, "Click email = "
					+ ((ABContactInfoFormItem) phoneInfoFormItem).getInfo());

			//
		}

	}

	// new approve application button on click listener
	class NewApproveApplicationBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// generate new approve application
			new NewApproveApplicationGenerator()
					.genNewApproveApplication(_mABContactBean);
		}

	}

}
