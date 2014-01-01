package com.futuo.iapprove.tab7tabcontent.newapproveapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.form.FormItemBean;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems.FormItem;
import com.richitec.commontoolkit.user.UserManager;

public class NewApproveApplicationActivity extends IApproveNavigationActivity {

	// new approve application submit contact
	private ABContactBean _mSubmitContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.new_approve_application_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define the enterprise form type id, form id and name
		Long _enterpriseFormTypeId = null;
		Long _enterpriseFormId = null;
		String _enterpriseFormName = "";

		// check the data
		if (null != _extraData) {
			// get the user enterprise form type id, form id, name and the new
			// approve application submit contact
			_enterpriseFormTypeId = _extraData
					.getLong(NewApproveApplicationExtraData.ENTERPRISE_FROM_TYPE_ID);
			_enterpriseFormId = _extraData
					.getLong(NewApproveApplicationExtraData.ENTERPRISE_FROM_ID);
			_enterpriseFormName = _extraData
					.getString(NewApproveApplicationExtraData.ENTERPRISE_FROM_NAME);
			_mSubmitContact = (ABContactBean) _extraData
					.getSerializable(NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACT);
		}

		// set subViews
		// set title
		setTitle(_enterpriseFormName);

		Log.d("@@", "New approve application enterprise form type id = "
				+ _enterpriseFormTypeId + ", form id = " + _enterpriseFormId
				+ " and submit contact = " + _mSubmitContact);

		Cursor _cursor = getContentResolver()
				.query(FormItem.FORMITEMS_CONTENT_URI,
						null,
						FormItem.ENTERPRISE_FORMITEMS_WITHFORMTYPEID7FORMID_CONDITION,
						new String[] {
								IAUserExtension.getUserLoginEnterpriseId(
										UserManager.getInstance().getUser())
										.toString(),
								_enterpriseFormTypeId.toString(),
								_enterpriseFormId.toString() }, null);
		if (null != _cursor) {
			while (_cursor.moveToNext()) {
				Log.d("@@", "Form items cursor = " + _cursor
						+ " and form item = " + new FormItemBean(_cursor));
			}

			_cursor.close();
		}

		//
	}

	// inner class
	// new approve application extra data constant
	public static final class NewApproveApplicationExtraData {

		// new approve application enterprise form type id, form id, name and
		// submit contact bean
		public static final String ENTERPRISE_FROM_TYPE_ID = "enterprise_form_type_id";
		public static final String ENTERPRISE_FROM_ID = "enterprise_form_id";
		public static final String ENTERPRISE_FROM_NAME = "enterprise_form_name";
		public static final String NEW_APPROVEAPPLICATION_SUBMIT_CONTACT = "new_approveapplication_submit_contact";

	}

}
