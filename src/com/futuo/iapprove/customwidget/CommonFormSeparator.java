package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.utils.DisplayScreenUtils;

public class CommonFormSeparator extends View {

	public CommonFormSeparator(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// init common form separator
		init(context, attrs);
	}

	public CommonFormSeparator(Context context, AttributeSet attrs) {
		super(context, attrs);

		// init common form separator
		init(context, attrs);
	}

	public CommonFormSeparator(Context context) {
		super(context);

		// init common form separator
		init(context, null);
	}

	// initialize common form separator
	private void init(Context context, AttributeSet attrs) {
		// set width and height(1.5dp)
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				DisplayScreenUtils.dp2pix(1.5f)));

		// set background color
		setBackgroundColor(getResources().getColor(R.color.light_middle_gray));
	}

}
