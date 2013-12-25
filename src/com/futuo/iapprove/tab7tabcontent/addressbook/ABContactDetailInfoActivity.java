package com.futuo.iapprove.tab7tabcontent.addressbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.addressbook.ABContactSex;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.form.EnterpriseFormListActivity.NewApproveApplicationExtraData;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.form.EnterpriseFormTypeListActivity;

public class ABContactDetailInfoActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = ABContactDetailInfoActivity.class
			.getCanonicalName();

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
		ABContactSex _sexValue = _mABContactBean.getSex();
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
		// get and check nickname data
		String _nickname = _mABContactBean.getNickname();
		if (null != _nickname) {
			// set nickname
			((TextView) findViewById(R.id.abcdi_nickname_textView))
					.setText(_nickname);
		}

		//

		// bind new approve application button on click listener
		((Button) findViewById(R.id.abcdi_new_approveApplication_button))
				.setOnClickListener(new NewApproveApplicationBtnOnClickListener());
	}

	// inner class
	// address book contact detail info extra data constant
	public static final class ABContactDetailInfoExtraData {

		// address book contact detail info contact bean
		public static final String ABCONTACT_DETAILINFO_CONTACT = "addressbook_contact_detailinfo_contact";

	}

	// new approve application button on click listener
	class NewApproveApplicationBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// goto enterprise form list activity
			// define the target intent
			Intent _targetIntent = new Intent(ABContactDetailInfoActivity.this,
					EnterpriseFormTypeListActivity.class);

			// put the contact bean to extra data map as param
			_targetIntent
					.putExtra(
							NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACT,
							_mABContactBean);

			// start enterprise form list activity
			startActivity(_targetIntent);
		}

	}

}
