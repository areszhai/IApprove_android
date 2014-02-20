package com.futuo.iapprove.tab7tabcontent.task;

import android.app.Activity;
import android.os.Bundle;

import com.futuo.iapprove.R;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;

public class HistoryRecordTaskDetailInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.history_record_task_detail_info_activity_layout);

		//
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
