package com.futuo.iapprove.tab7tabcontent;

import android.os.Bundle;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveTabContentActivity;
import com.richitec.commontoolkit.user.UserManager;

public class TodoListTabContentActivity extends IApproveTabContentActivity {

	private static final String LOG_TAG = TodoListTabContentActivity.class
			.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.todo_list_tab_content_activity_layout);

		Log.d(LOG_TAG, "@@ = " + UserManager.getInstance().getUser());

		// set subViews
		// set title
		setTitle(R.string.todoList_tab7nav_title);

		//
	}

}
