package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.customcomponent.ImageBarButtonItem;

public class IApproveNavigationActivity extends NavigationActivity {

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_iapprove_navbar_bg);

		// set navigation back image bar button item as left image bar button
		// item
		setLeftBarButtonItem(new NavBackImgBarButtonItem(this,
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
		setLeftBarButtonItem(new NavBackImgBarButtonItem(this,
				R.drawable.img_nav_backbarbtnitem,
				_mBackBarBtnItemOnClickListener));
	}

	// inner class
	// navigation back image bar button item
	class NavBackImgBarButtonItem extends ImageBarButtonItem {

		public NavBackImgBarButtonItem(Context context, int srcId,
				OnClickListener btnClickListener) {
			super(
					context,
					context.getResources().getDrawable(srcId),
					BarButtonItemStyle.RIGHT_GO,
					null,
					context.getResources()
							.getDrawable(
									R.drawable.img_iapprove_rightbarbtnitem_touchdown_bg),
					btnClickListener);
		}

		@Override
		protected Drawable rightBarBtnItemNormalDrawable() {
			return null;
		}

	}

}
