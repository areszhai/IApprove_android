package com.futuo.iapprove.customwidget;

import android.view.View;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class IApproveNavigationActivity extends NavigationActivity {

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_iapprove_navbar_bg);

		// set navigation back image bar button item as left image bar button
		// item
		setLeftBarButtonItem(new IApproveImageBarButtonItem(this,
				R.drawable.img_nav_backbarbtnitem,
				_mBackBarBtnItemOnClickListener));
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_iapprove_navbar_bg);

		// set navigation back image bar button item as left image bar button
		// item
		setLeftBarButtonItem(new IApproveImageBarButtonItem(this,
				R.drawable.img_nav_backbarbtnitem,
				_mBackBarBtnItemOnClickListener));
	}

}
