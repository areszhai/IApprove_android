package com.futuo.iapprove.tab7tabcontent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveTabContentActivity;

public class HistoryRecordTabContentActivity extends IApproveTabContentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.history_record_tab_content_activity_layout);

		// set history record title segment as title
		setTitle(generateHistoryRecordTitleSegment(this), null);

		//
	}

	// generate history record title segment
	private RadioGroup generateHistoryRecordTitleSegment(Context context) {
		RadioGroup _historyRecordTitleSegment = null;

		// inflater history record title segment layout
		View _inflateView = LayoutInflater.from(context).inflate(
				R.layout.history_record_title_segment_layout, null);

		// get history record title segment radioGroup
		_historyRecordTitleSegment = (RadioGroup) _inflateView
				.findViewById(R.id.hr_historyRecord_titleSegment_radioGroup);

		// set on checked changed listener
		_historyRecordTitleSegment
				.setOnCheckedChangeListener(new HistoryRecordTitleSegmentOnCheckedChangeListener());

		return _historyRecordTitleSegment;
	}

	// inner class
	// history record title segment on checked changed listener
	class HistoryRecordTitleSegmentOnCheckedChangeListener implements
			OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// check checked id
			switch (checkedId) {
			case R.id.hr_checked_segment_radioButton:
				Log.d("2", "checked segment be checked");
				break;

			case R.id.hr_myApplication_segment_radioButton:
			default:
				Log.d("1", "my application segment be checked");
				break;
			}
		}

	}

}
