package com.futuo.iapprove.customwidget;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.addressbook.person.PersonBean;
import com.futuo.iapprove.task.IApproveTaskAdviceBean;
import com.richitec.commontoolkit.CTApplication;

public class TaskFormAdviceFormItem extends FrameLayout {

	private static final String LOG_TAG = TaskFormAdviceFormItem.class
			.getCanonicalName();

	// iApprove task form advice relativeLayout, advisor avatar imageView,
	// advisor name textView, advice info parent frameLayout and advice info
	// textView
	private RelativeLayout _mAdviceRelativeLayout;
	private ImageView _mAdvisorAvatarImageView;
	private TextView _mAdvisorNameTextView;
	private FrameLayout _mAdviceInfoParentFrameLayout;
	private TextView _mAdviceInfoTextView;

	// iApprove task form advice form item on click and long click listener
	private OnClickListener _mAdviceFormItemOnClickListener;
	private OnLongClickListener _mAdviceFormItemOnLongClickListener;

	private TaskFormAdviceFormItem(Context context) {
		super(context);

		// inflate iApprove task form advice form item layout
		LayoutInflater.from(context).inflate(
				R.layout.task_formadvice_form_item_layout, this);

		// get iApprove task form my advice relativeLayout, advisor avatar
		// imageView, advisor name textView, advice info parent frameLayout,
		// adviceinfo textView as default
		_mAdviceRelativeLayout = (RelativeLayout) findViewById(R.id.taskfafi_myAdvice_relativeLayout);
		_mAdvisorAvatarImageView = (ImageView) findViewById(R.id.taskfafi_myAdvice_userAvatar_imageView);
		_mAdvisorNameTextView = (TextView) findViewById(R.id.taskfafi_myAdvice_userName_textView);
		_mAdviceInfoParentFrameLayout = (FrameLayout) findViewById(R.id.taskfafi_myAdvice_parent_frameLayout);
		_mAdviceInfoTextView = (TextView) findViewById(R.id.taskfafi_myAdvice_textView);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		// save advice on click listener
		_mAdviceFormItemOnClickListener = l;

		// set iApprove task form advice info parent frameLayout on click
		// listener
		_mAdviceInfoParentFrameLayout
				.setOnClickListener(new TaskFormAdviceParentFrameLayoutOnClickListener());
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		// save advice on long click listener
		_mAdviceFormItemOnLongClickListener = l;

		// set iApprove task form advice info parent frameLayout on long click
		// listener
		_mAdviceInfoParentFrameLayout
				.setOnLongClickListener(new TaskFormAdviceParentFrameLayoutOnLongClickListener());
	}

	private void setAdviceType(TaskFormAdviceType adviceType) {
		// check advice type and set iApprove task form advice type
		if (null != adviceType) {
			switch (adviceType) {
			case MY_ADVICE:
				// get iApprove task form my advice relativeLayout, advisor
				// avatar imageView, advisor name textView, advice info parent
				// frameLayout and advice info textView
				_mAdviceRelativeLayout = (RelativeLayout) findViewById(R.id.taskfafi_myAdvice_relativeLayout);
				_mAdvisorAvatarImageView = (ImageView) findViewById(R.id.taskfafi_myAdvice_userAvatar_imageView);
				_mAdvisorNameTextView = (TextView) findViewById(R.id.taskfafi_myAdvice_userName_textView);
				_mAdviceInfoParentFrameLayout = (FrameLayout) findViewById(R.id.taskfafi_myAdvice_parent_frameLayout);
				_mAdviceInfoTextView = (TextView) findViewById(R.id.taskfafi_myAdvice_textView);
				break;

			case OTHERS_ADVICE:
				// get iApprove task form others advice relativeLayout, advisor
				// avatar imageView, advisor name textView, advice info parent
				// frameLayout and advice info textView
				_mAdviceRelativeLayout = (RelativeLayout) findViewById(R.id.taskfafi_othersAdvice_relativeLayout);
				_mAdvisorAvatarImageView = (ImageView) findViewById(R.id.taskfafi_othersAdvice_userAvatar_imageView);
				_mAdvisorNameTextView = (TextView) findViewById(R.id.taskfafi_othersAdvice_userName_textView);
				_mAdviceInfoParentFrameLayout = (FrameLayout) findViewById(R.id.taskfafi_othersAdvice_parent_frameLayout);
				_mAdviceInfoTextView = (TextView) findViewById(R.id.taskfafi_othersAdvice_textView);
				break;
			}
		}
	}

	private void setAdvisorInfo(PersonBean advisor) {
		// check advisor object
		if (null != advisor) {
			// get advisor avatar data and set iApprove task form advice form
			// item advisor avatar imageView image
			byte[] _avatarData = advisor.getAvatar();
			if (null != _avatarData) {
				try {
					// get avatar data stream
					InputStream _avatarDataStream = new ByteArrayInputStream(
							_avatarData);

					// set avatar
					_mAdvisorAvatarImageView.setImageBitmap(BitmapFactory
							.decodeStream(_avatarDataStream));

					// close photo data stream
					_avatarDataStream.close();
				} catch (IOException e) {
					Log.e(LOG_TAG,
							"Get advisor avatar data stream error, exception message = "
									+ e.getMessage());

					e.printStackTrace();
				}
			}

			// set advisor name textView text
			_mAdvisorNameTextView.setText(advisor.getEmployeeName());
		}
	}

	// generate iApprove task form advice form item with type, advisor and info
	public static TaskFormAdviceFormItem generateTaskFormAdviceFormItem(
			TaskFormAdviceType type, PersonBean advisor,
			IApproveTaskAdviceBean advice) {
		// get application context
		Context _appContext = CTApplication.getContext();

		// new iApprove task form advice form item
		TaskFormAdviceFormItem _newTaskFormAdviceFormItem = new TaskFormAdviceFormItem(
				_appContext);

		// check iApprove task form advice type and set it
		if (null != type && TaskFormAdviceType.MY_ADVICE != type) {
			_newTaskFormAdviceFormItem.setAdviceType(type);
		}

		// show iApprove task advice relativeLayout
		_newTaskFormAdviceFormItem._mAdviceRelativeLayout
				.setVisibility(View.VISIBLE);

		// get, check advisor and set iApprove task advisor info
		if (null != advisor) {
			_newTaskFormAdviceFormItem.setAdvisorInfo(advisor);
		}

		// get, check advice info then set iApprove task advice info parent
		// frameLayout background and textView text
		if (null != advice) {
			// check iApprove task form advice type again
			if (null != type && TaskFormAdviceType.MY_ADVICE != type) {
				// check others advice info agreed
				if (advice.agreed()) {
					_newTaskFormAdviceFormItem._mAdviceInfoParentFrameLayout
							.setBackgroundResource(R.drawable.task_formadvice_formitem_othersagreeadvice_bg);
				} else {
					_newTaskFormAdviceFormItem._mAdviceInfoParentFrameLayout
							.setBackgroundResource(R.drawable.task_formadvice_formitem_othersdisagreeadvice_bg);
				}
			}
			_newTaskFormAdviceFormItem._mAdviceInfoTextView.setText(advice
					.getAdvice());
		}

		return _newTaskFormAdviceFormItem;
	}

	// inner class
	// iApprove task form advice type
	public static enum TaskFormAdviceType {

		// my and others advice
		MY_ADVICE, OTHERS_ADVICE

	}

	// iApprove task form advice parent frameLayout on click listener
	class TaskFormAdviceParentFrameLayoutOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// set advice info textView advice form item on click response view
			_mAdviceFormItemOnClickListener.onClick(_mAdviceInfoTextView);
		}

	}

	// iApprove task form advice parent frameLayout on long click listener
	class TaskFormAdviceParentFrameLayoutOnLongClickListener implements
			OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			// set advice form item as advice parent frameLayout on long click
			// response view
			return _mAdviceFormItemOnLongClickListener
					.onLongClick(TaskFormAdviceFormItem.this);
		}

	}

}
