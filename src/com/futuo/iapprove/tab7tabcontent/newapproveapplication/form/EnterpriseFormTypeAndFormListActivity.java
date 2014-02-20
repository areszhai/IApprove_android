package com.futuo.iapprove.tab7tabcontent.newapproveapplication.form;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.futuo.iapprove.R;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.richitec.commontoolkit.activityextension.NavigationActivity;

public class EnterpriseFormTypeAndFormListActivity extends NavigationActivity {

	// cancel bar button item on click listener
	protected OnClickListener _mCancelBarBtnItemOnClickListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialized cancel bar button item on click listener
		_mCancelBarBtnItemOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// finish the activity
				finish();
			}

		};
	}

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

}
