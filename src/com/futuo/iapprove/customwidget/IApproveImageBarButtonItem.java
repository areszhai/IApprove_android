package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.customcomponent.ImageBarButtonItem;

public class IApproveImageBarButtonItem extends ImageBarButtonItem {

	public IApproveImageBarButtonItem(Context context) {
		super(context);
	}

	public IApproveImageBarButtonItem(Context context, int srcId,
			OnClickListener btnClickListener) {
		super(context, context.getResources().getDrawable(srcId),
				BarButtonItemStyle.RIGHT_GO, null,
				context.getResources().getDrawable(
						R.drawable.img_iapprove_rightbarbtnitem_touchdown_bg),
				btnClickListener);
	}

	@Override
	protected Drawable rightBarBtnItemNormalDrawable() {
		return null;
	}

}
