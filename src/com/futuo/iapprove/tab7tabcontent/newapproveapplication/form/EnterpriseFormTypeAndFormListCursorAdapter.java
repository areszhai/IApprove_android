package com.futuo.iapprove.tab7tabcontent.newapproveapplication.form;

import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.richitec.commontoolkit.customadapter.CTListCursorAdapter;

public abstract class EnterpriseFormTypeAndFormListCursorAdapter extends
		CTListCursorAdapter {

	private final String LOG_TAG = EnterpriseFormTypeAndFormListCursorAdapter.class
			.getCanonicalName();

	public EnterpriseFormTypeAndFormListCursorAdapter(Context context,
			int itemsLayoutResId, Cursor c, String[] dataKeys,
			int[] itemsComponentResIds) {
		super(context, itemsLayoutResId, c, dataKeys, itemsComponentResIds,
				true);
	}

	@Override
	protected void bindView(View view, Map<String, ?> dataMap, String dataKey) {
		// get item data object
		Object _itemData = dataMap.get(dataKey);

		// check view type
		// textView
		if (view instanceof TextView) {
			// generate view text
			SpannableString _viewNewText = new SpannableString(
					null == _itemData ? "" : _itemData.toString());

			// check data class name
			if (_itemData instanceof SpannableString) {
				_viewNewText
						.setSpan(new ForegroundColorSpan(Color.RED), 0,
								_viewNewText.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			// set view text
			((TextView) view).setText(_viewNewText);
		} else {
			Log.e(LOG_TAG, "Bind view error, view = " + view
					+ " not recognized, data key = " + dataKey
					+ " and data map = " + dataMap);
		}
	}

}
