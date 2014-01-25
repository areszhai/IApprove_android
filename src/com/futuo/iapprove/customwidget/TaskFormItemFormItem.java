package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.task.IApproveTaskFormItemBean;
import com.richitec.commontoolkit.CTApplication;

public class TaskFormItemFormItem extends LinearLayout {

	private static final String LOG_TAG = TaskFormItemFormItem.class
			.getCanonicalName();

	// iApprove task form item form item label and info textView
	private TextView _mLabelTextView;
	private TextView _mInfoTextView;

	private TaskFormItemFormItem(Context context) {
		super(context);

		// inflate iApprove task form item form item layout
		LayoutInflater.from(context).inflate(
				R.layout.task_formitem_form_item_layout, this);

		// get iApprove task form item form item label and info textView
		_mLabelTextView = (TextView) findViewById(R.id.tfifi_label_textView);
		_mInfoTextView = (TextView) findViewById(R.id.tfifi_info_textView);
	}

	public void setFormItem(IApproveTaskFormItemBean formItem) {
		// set iApprove task form item form item label and info string
		_mLabelTextView.setText(formItem.getItemName());
		_mInfoTextView.setText(formItem.getItemInfo());
	}

	// generate iApprove task form item form item with form item object
	public static TaskFormItemFormItem generateTaskFormItemFormItem(
			IApproveTaskFormItemBean formItem) {
		// define iApprove task form item form item
		TaskFormItemFormItem _newTaskFormItemFormItem = null;

		// check form item
		if (null != formItem) {
			// new iApprove task form item form item
			_newTaskFormItemFormItem = new TaskFormItemFormItem(
					CTApplication.getContext());

			// set iApprove task form item form item label and info string
			_newTaskFormItemFormItem._mLabelTextView.setText(formItem
					.getItemName());
			_newTaskFormItemFormItem._mInfoTextView.setText(formItem
					.getItemInfo());
		} else {
			Log.d(LOG_TAG,
					"Generate new iApprove task form item form item error, form item = "
							+ formItem);
		}

		return _newTaskFormItemFormItem;
	}

}
