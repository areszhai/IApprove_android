package com.futuo.iapprove.customwidget;

import android.view.View;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class IApproveTabContentActivity extends NavigationActivity {

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_iapprove_navbar_bg);
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_iapprove_navbar_bg);
	}

}
