package com.futuo.iapprove.tab7tabcontent.task;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveImageBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;

public class TodoTaskDetailInfoActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = TodoTaskDetailInfoActivity.class
			.getCanonicalName();

	// user enterprise to-do list task title and sender fake id
	private String _mTodoTaskTitle;
	private Long _mTodoTaskSenderFakeId;

	// input method manager
	private InputMethodManager _mInputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.todo_task_detail_info_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// check the data
		if (null != _extraData) {
			// get to-do list task title and sender fake id
			_mTodoTaskTitle = _extraData
					.getString(TodoTaskDetailInfoExtraData.TODOTASK_DETAILINFO_TASKTITLE);
			_mTodoTaskSenderFakeId = _extraData
					.getLong(TodoTaskDetailInfoExtraData.TODOTASK_DETAILINFO_TASKSENDERFAKEID);
		}

		// get input method manager
		_mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// set subViews
		// set title
		setTitle(_mTodoTaskTitle);

		// set to-do list task submit bar button item as right bar button item
		setRightBarButtonItem(new IApproveImageBarButtonItem(this,
				R.drawable.img_naa6tdta_submitbarbtnitem, null));

		//
	}

	// inner class
	// to-do list task detail info extra data constant
	public static final class TodoTaskDetailInfoExtraData {

		// to-do list task title and sender fake id
		public static final String TODOTASK_DETAILINFO_TASKTITLE = "todo_task_detailinfo_tasktitle";
		public static final String TODOTASK_DETAILINFO_TASKSENDERFAKEID = "todo_task_detailinfo_tasksenderfakeid";

	}

}
