package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.customcomponent.BarButtonItem;

public class IApproveBarButtonItem extends BarButtonItem {

	public IApproveBarButtonItem(Context context) {
		super(context);
	}

	public IApproveBarButtonItem(Context context, int titleId,
			OnClickListener btnClickListener) {
		super(context, context.getResources().getString(titleId),
				BarButtonItemStyle.RIGHT_GO, null,
				context.getResources().getDrawable(
						R.drawable.img_iapprove_rightbarbtnitem_touchdown_bg),
				btnClickListener);

		// set text color
		setTextColor(getResources().getColor(R.color.ocean_blue));
	}

	@Override
	protected Drawable rightBarBtnItemNormalDrawable() {
		return null;
	}

}
