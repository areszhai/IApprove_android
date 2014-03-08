package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.task.IApproveTaskAdviceBean;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.DisplayScreenUtils;

public class TodoTaskAdvice extends FrameLayout {

	private static final String LOG_TAG = TodoTaskAdvice.class
			.getCanonicalName();

	// to-do list task advice
	private IApproveTaskAdviceBean _mTodoTaskAdvice;

	// to-do list task advisor name textView
	private TextView _mTodoTaskAdvisorNameTextView;

	// to-do task advice on click listener
	private OnClickListener _mTodoTaskAdviceOnClickListener;

	private TodoTaskAdvice(Context context) {
		super(context);

		// inflate to-do list task advice layout
		LayoutInflater.from(context).inflate(R.layout.todo_task_advice_layout,
				this);

		// get to-do list task advisor name textView
		_mTodoTaskAdvisorNameTextView = (TextView) findViewById(R.id.tdlta_advisorName_textView);

		// set its on long click listener
		_mTodoTaskAdvisorNameTextView
				.setOnClickListener(new TodoTaskAdvisorNameTextViewOnClickListener());
	}

	private TodoTaskAdvice(Context context,
			IApproveTaskAdviceBean todoTaskAdvice) {
		this(context);

		// save to-do list task advice
		_mTodoTaskAdvice = todoTaskAdvice;

		// set to-do list task advisor name textView text
		_mTodoTaskAdvisorNameTextView.setText(todoTaskAdvice.getAdvisorName());

		// check to-do list task advice is agreed, modified flag and set to-do
		// list task advice background
		if (todoTaskAdvice.modified()) {
			// modified
			_mTodoTaskAdvisorNameTextView
					.setBackgroundResource(R.drawable.todo_task_modifiedadvice_bg);
		} else {
			if (todoTaskAdvice.agreed()) {
				// agreed
				_mTodoTaskAdvisorNameTextView
						.setBackgroundResource(R.drawable.todo_task_agreedadvice_bg);
			} else {
				// disagreed
				_mTodoTaskAdvisorNameTextView
						.setBackgroundResource(R.drawable.todo_task_disagreedadvice_bg);
			}
		}
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		// save to-do task advice on click listener
		_mTodoTaskAdviceOnClickListener = l;
	}

	public IApproveTaskAdviceBean getAdvice() {
		return _mTodoTaskAdvice;
	}

	public void setAdvice(IApproveTaskAdviceBean todoTaskAdvice) {
		_mTodoTaskAdvice = todoTaskAdvice;

		// set to-do list task advisor name textView text
		_mTodoTaskAdvisorNameTextView.setText(todoTaskAdvice.getAdvisorName());

		// check to-do list task advice is agreed, modified flag and set to-do
		// list task advice background
		if (todoTaskAdvice.modified()) {
			// modified
			_mTodoTaskAdvisorNameTextView
					.setBackgroundResource(R.drawable.todo_task_modifiedadvice_bg);
		} else {
			if (todoTaskAdvice.agreed()) {
				// agreed
				_mTodoTaskAdvisorNameTextView
						.setBackgroundResource(R.drawable.todo_task_agreedadvice_bg);
			} else {
				// disagreed
				_mTodoTaskAdvisorNameTextView
						.setBackgroundResource(R.drawable.todo_task_disagreedadvice_bg);
			}
		}
	}

	// generate to-do list task advice with to-do list task advice object
	public static TodoTaskAdvice generateTodoTaskAdvice(
			IApproveTaskAdviceBean todoTaskAdvice) {
		// define to-do list task advice
		TodoTaskAdvice _todoTaskAdvice = null;

		// check to-do list task advice object
		if (null != todoTaskAdvice) {
			// new to-do list task advice
			_todoTaskAdvice = new TodoTaskAdvice(CTApplication.getContext(),
					todoTaskAdvice);
		} else {
			Log.d(LOG_TAG,
					"Generate new to-do list task advice error, to-do list task advice = "
							+ todoTaskAdvice);
		}

		return _todoTaskAdvice;
	}

	// generate to-do list task advice separator
	public static TodoTaskAdviceSeparator generateTodoTaskAdviceSeparator() {
		return new TodoTaskAdviceSeparator(CTApplication.getContext());
	}

	// inner class
	// to-do list task advisor name textView on click listener
	class TodoTaskAdvisorNameTextViewOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// check to-do task advice on click listener
			if (null != _mTodoTaskAdviceOnClickListener) {
				_mTodoTaskAdviceOnClickListener.onClick(TodoTaskAdvice.this);
			}
		}

	}

	// to-do list task advice separator
	static class TodoTaskAdviceSeparator extends View {

		public TodoTaskAdviceSeparator(Context context, AttributeSet attrs,
				int defStyleAttr) {
			super(context, attrs, defStyleAttr);
		}

		public TodoTaskAdviceSeparator(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public TodoTaskAdviceSeparator(Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			// set width(8dp) and height
			setMeasuredDimension(DisplayScreenUtils.dp2pix(8.0f),
					heightMeasureSpec);
		}

	}

}
