package com.futuo.iapprove.tab7tabcontent.task;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;

import com.richitec.commontoolkit.customadapter.CTListAdapter;

public class IApproveTaskAdapter extends CTListAdapter {

	public IApproveTaskAdapter(Context context, List<Map<String, ?>> data,
			int itemsLayoutResId, String[] dataKeys, int[] itemsComponentResIds) {
		super(context, data, itemsLayoutResId, dataKeys, itemsComponentResIds);
	}

	@Override
	protected void bindView(View view, Map<String, ?> dataMap, String dataKey) {
		// TODO Auto-generated method stub

	}

}
