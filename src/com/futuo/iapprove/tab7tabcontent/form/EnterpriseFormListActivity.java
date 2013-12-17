package com.futuo.iapprove.tab7tabcontent.form;

import android.os.Bundle;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.user.UserManager;

public class EnterpriseFormListActivity extends NavigationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.enterprise_form_list_activity_layout);

		// set subViews
		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_iapprove_navbar_bg);

		// set title
		setTitle(String.format(
				getResources().getString(
						R.string.enterprise_formList_nav_title_format),
				IAUserExtension.getUserEnterpriseNames()[IAUserExtension
						.getUserLoginEnterpriseIndex(IAUserExtension
								.getUserLoginEnterpriseId(UserManager
										.getInstance().getUser()))]));

		//
	}

}
