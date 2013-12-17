package com.futuo.iapprove.tab7tabcontent.form;

import android.os.Bundle;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;

public class EnterpriseFormDetailInfoActivity extends
		IApproveNavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.enterprise_form_detail_info_activity_layout);

		// set subViews
		// set title
		setTitle(R.string.settings_tab7nav_title);

		//
	}

	// new approve application extra data constant
	public static final class NewApproveApplicationExtraData {

		// new approve application submit contact bean
		public static final String NEW_APPROVEAPPLICATION_SUBMIT_CONTACT = "new_approveapplication_submit_contact";

	}

}
