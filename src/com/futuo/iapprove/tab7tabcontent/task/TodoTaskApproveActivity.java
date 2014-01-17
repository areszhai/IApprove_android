package com.futuo.iapprove.tab7tabcontent.task;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveImageBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;

public class TodoTaskApproveActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = TodoTaskApproveActivity.class
			.getCanonicalName();

	// user enterprise to-do list task title and sender fake id
	private String _mTodoTaskTitle;
	private Long _mTodoTaskSenderFakeId;

	// to-do list task form item form linearLayout
	private LinearLayout _mFormItemFormLinearLayout;

	// to-do list task form attachment form parent frameLayout and form
	// linearLayout
	private FrameLayout _mAttachmentFormParentFrameLayout;
	private LinearLayout _mAttachmentFormLinearLayout;

	// to-do list task form advice form parent frameLayout and form linearLayout
	private FrameLayout _mAdviceFormParentFrameLayout;
	private LinearLayout _mAdviceFormLinearLayout;

	// advice input editText
	private EditText _mAdviceInputEditText;

	// input method manager
	private InputMethodManager _mInputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.todo_task_approve_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// check the data
		if (null != _extraData) {
			// get to-do list task title and sender fake id
			_mTodoTaskTitle = _extraData
					.getString(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKTITLE);
			_mTodoTaskSenderFakeId = _extraData
					.getLong(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKSENDERFAKEID);
		}

		// get input method manager
		_mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// set subViews
		// set title
		setTitle(_mTodoTaskTitle);

		// set to-do list task submit bar button item as right bar button item
		setRightBarButtonItem(new IApproveImageBarButtonItem(this,
				R.drawable.img_naa6tdta_submitbarbtnitem, null));

		// get to-do list task form item form linearLayout
		_mFormItemFormLinearLayout = (LinearLayout) findViewById(R.id.tdta_formItemForm_linearLayout);

		// refresh to-do list task form item form
		// refreshFormItemForm();

		// get to-do list task form attachment form parent frameLayout and form
		// linearLayout
		_mAttachmentFormParentFrameLayout = (FrameLayout) findViewById(R.id.tdta_attachmentForm_parent_frameLayout);
		_mAttachmentFormLinearLayout = (LinearLayout) findViewById(R.id.tdta_attachmentForm_linearLayout);

		// get to-do list task form advice form parent frameLayout and form
		// linearLayout
		_mAttachmentFormParentFrameLayout = (FrameLayout) findViewById(R.id.tdta_adviceForm_parent_frameLayout);
		_mAttachmentFormLinearLayout = (LinearLayout) findViewById(R.id.tdta_adviceForm_linearLayout);

		// bind add submit contact button on click listener
		((ImageButton) findViewById(R.id.tdta_add_submitContact_imageButton))
				.setOnClickListener(null);

		// get advice input editText
		_mAdviceInputEditText = (EditText) findViewById(R.id.tdta_advice_editText);

		// set advice input editText text changed listener
		_mAdviceInputEditText.addTextChangedListener(null);

		// bind its on touch listener
		_mAdviceInputEditText.setOnTouchListener(null);

		// bind advice send button on click listener
		((Button) findViewById(R.id.tdta_advice_send_button))
				.setOnClickListener(null);
	}

	// inner class
	// to-do list task approve extra data constant
	public static final class TodoTaskApproveExtraData {

		// to-do list task title and sender fake id
		public static final String TODOTASK_APPROVE_TASKTITLE = "todo_task_approve_tasktitle";
		public static final String TODOTASK_APPROVE_TASKSENDERFAKEID = "todo_task_approve_tasksenderfakeid";

	}

}
