package com.futuo.iapprove.customwidget;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

	// iApprove task form advice relativeLayout, advice given timestamp
	// textView, advisor avatar imageView, advisor name textView, advice info
	// parent frameLayout and advice info textView
	private RelativeLayout _mAdviceRelativeLayout;
	private TextView _mAdviceGivenTimestampTextView;
	private ImageView _mAdvisorAvatarImageView;
	private TextView _mAdvisorNameTextView;
	private FrameLayout _mAdviceInfoParentFrameLayout;
	private TextView _mAdviceInfoTextView;

	// iApprove task form advice type
	private TaskFormAdviceType _mAdviceType;

	// iApprove task form advice given timestamp
	private Long _mAdviceGivenTimestamp;

	// iApprove task form advice form item on click and long click listener
	private OnClickListener _mAdviceFormItemOnClickListener;
	private OnLongClickListener _mAdviceFormItemOnLongClickListener;

	private TaskFormAdviceFormItem(Context context) {
		super(context);

		// inflate iApprove task form advice form item layout
		LayoutInflater.from(context).inflate(
				R.layout.task_formadvice_form_item_layout, this);

		// set iApprove task form advice type default value
		_mAdviceType = TaskFormAdviceType.MY_ADVICE;

		// get iApprove task form my advice relativeLayout, advice given
		// timestamp textView, advisor avatar imageView, advisor name textView,
		// advice info parent frameLayout, advice info textView as default
		_mAdviceRelativeLayout = (RelativeLayout) findViewById(R.id.taskfafi_myAdvice_relativeLayout);
		_mAdviceGivenTimestampTextView = (TextView) findViewById(R.id.taskfafi_myAdvice_givenTimestamp_textView);
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
			// save iApprove task form advice type
			_mAdviceType = adviceType;

			switch (adviceType) {
			case MY_ADVICE:
				// get iApprove task form my advice relativeLayout, advice given
				// timestamp textView, advisor avatar imageView, advisor name
				// textView, advice info parent frameLayout and advice info
				// textView
				_mAdviceRelativeLayout = (RelativeLayout) findViewById(R.id.taskfafi_myAdvice_relativeLayout);
				_mAdviceGivenTimestampTextView = (TextView) findViewById(R.id.taskfafi_myAdvice_givenTimestamp_textView);
				_mAdvisorAvatarImageView = (ImageView) findViewById(R.id.taskfafi_myAdvice_userAvatar_imageView);
				_mAdvisorNameTextView = (TextView) findViewById(R.id.taskfafi_myAdvice_userName_textView);
				_mAdviceInfoParentFrameLayout = (FrameLayout) findViewById(R.id.taskfafi_myAdvice_parent_frameLayout);
				_mAdviceInfoTextView = (TextView) findViewById(R.id.taskfafi_myAdvice_textView);
				break;

			case OTHERS_ADVICE:
				// get iApprove task form others advice relativeLayout, advice
				// given timestamp textView, advisor avatar imageView, advisor
				// name textView, advice info parent frameLayout and advice info
				// textView
				_mAdviceRelativeLayout = (RelativeLayout) findViewById(R.id.taskfafi_othersAdvice_relativeLayout);
				_mAdviceGivenTimestampTextView = (TextView) findViewById(R.id.taskfafi_othersAdvice_givenTimestamp_textView);
				_mAdvisorAvatarImageView = (ImageView) findViewById(R.id.taskfafi_othersAdvice_userAvatar_imageView);
				_mAdvisorNameTextView = (TextView) findViewById(R.id.taskfafi_othersAdvice_userName_textView);
				_mAdviceInfoParentFrameLayout = (FrameLayout) findViewById(R.id.taskfafi_othersAdvice_parent_frameLayout);
				_mAdviceInfoTextView = (TextView) findViewById(R.id.taskfafi_othersAdvice_textView);
				break;
			}
		}
	}

	public TaskFormAdviceType getAdviceType() {
		return _mAdviceType;
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

	public String getAdviceInfo() {
		return (String) _mAdviceInfoTextView.getText();
	}

	public Long getAdviceGivenTimestamp() {
		return _mAdviceGivenTimestamp;
	}

	private void setAdviceGivenTimestamp(Long adviceGivenTimestamp) {
		// task advice given time day and time format, format timeStamp
		final DateFormat _taskAdviceGivenTimeDayFormat = new SimpleDateFormat(
				"yy-MM-dd HH:mm:ss", Locale.getDefault());
		final DateFormat _taskAdviceGivenTimeTimeFormat = new SimpleDateFormat(
				"HH:mm:ss", Locale.getDefault());

		// miliSceonds of day
		final Long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L;

		// define task advice given time string
		String _taskAdviceGivenTime = "";

		// get current system time
		Long _currentSystemTime = System.currentTimeMillis();

		// compare current system time and submit timestamp
		if (_currentSystemTime - adviceGivenTimestamp >= 0) {
			// get today zero o'clock calendar instance
			Calendar _todayZeroCalendarInstance = Calendar.getInstance(Locale
					.getDefault());
			_todayZeroCalendarInstance.set(Calendar.AM_PM, 0);
			_todayZeroCalendarInstance.set(Calendar.HOUR, 0);
			_todayZeroCalendarInstance.set(Calendar.MINUTE, 0);
			_todayZeroCalendarInstance.set(Calendar.SECOND, 0);
			_todayZeroCalendarInstance.set(Calendar.MILLISECOND, 0);

			// get task advice given timestamp calendar instance
			Calendar _adviceGivenTimestampCalendarInstance = Calendar
					.getInstance(Locale.getDefault());
			_adviceGivenTimestampCalendarInstance
					.setTimeInMillis(adviceGivenTimestamp);

			// format day and time
			if (_adviceGivenTimestampCalendarInstance
					.before(_todayZeroCalendarInstance)) {
				// get today zero o'clock and advice given timestamp time
				// different
				Long _today7adviceGivenTimestampCalendarTimeDifferent = _todayZeroCalendarInstance
						.getTimeInMillis()
						- _adviceGivenTimestampCalendarInstance
								.getTimeInMillis();

				// check time different
				if (_today7adviceGivenTimestampCalendarTimeDifferent <= MILLISECONDS_PER_DAY) {
					// yesterday
					_taskAdviceGivenTime = getResources().getString(
							R.string.taskAdvice_yesterdayGiven_givenTimestamp)
							+ _taskAdviceGivenTimeTimeFormat
									.format(adviceGivenTimestamp);
				} else {
					// get first day zero o'clock of week calendar instance
					Calendar _firstDayOfWeekZeroCalendarInstance = Calendar
							.getInstance(Locale.getDefault());
					_firstDayOfWeekZeroCalendarInstance
							.setTimeInMillis(_todayZeroCalendarInstance
									.getTimeInMillis());
					_firstDayOfWeekZeroCalendarInstance.set(
							Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

					if (_adviceGivenTimestampCalendarInstance
							.before(_firstDayOfWeekZeroCalendarInstance)) {
						// one day before days of one week
						_taskAdviceGivenTime = _taskAdviceGivenTimeDayFormat
								.format(adviceGivenTimestamp);
					} else {
						// in the week
						_taskAdviceGivenTime = getResources().getStringArray(
								R.array.taskAdvice_givenTimestamp_daysOfWeek)[_adviceGivenTimestampCalendarInstance
								.get(Calendar.DAY_OF_WEEK) - 1]
								+ _taskAdviceGivenTimeTimeFormat
										.format(adviceGivenTimestamp);
					}
				}
			} else {
				// today
				_taskAdviceGivenTime = _taskAdviceGivenTimeTimeFormat
						.format(adviceGivenTimestamp);
			}
		} else {
			Log.e(LOG_TAG,
					"Format task advice given time error, task advice given timestamp greater than current system time");

			_taskAdviceGivenTime = _taskAdviceGivenTimeTimeFormat
					.format(adviceGivenTimestamp);
		}

		// set advice given timestamp and set it as advice given timestamp
		// textView text
		_mAdviceGivenTimestamp = adviceGivenTimestamp;
		_mAdviceGivenTimestampTextView.setText(_taskAdviceGivenTime);
	}

	public void showAdviceGivenTimestampTextView() {
		_mAdviceGivenTimestampTextView.setVisibility(View.VISIBLE);
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
			// set advice given timestamp
			_newTaskFormAdviceFormItem.setAdviceGivenTimestamp(advice
					.getAdviceGivenTimestamp());

			// check iApprove task form advice type again
			if (null != type && TaskFormAdviceType.MY_ADVICE != type) {
				// check others advice info agreed and modified
				if (advice.modified()) {
					_newTaskFormAdviceFormItem._mAdviceInfoParentFrameLayout
							.setBackgroundResource(R.drawable.task_formadvice_formitem_othersmodifyadvice_bg);
				} else {
					if (advice.agreed()) {
						_newTaskFormAdviceFormItem._mAdviceInfoParentFrameLayout
								.setBackgroundResource(R.drawable.task_formadvice_formitem_othersagreeadvice_bg);
					} else {
						_newTaskFormAdviceFormItem._mAdviceInfoParentFrameLayout
								.setBackgroundResource(R.drawable.task_formadvice_formitem_othersdisagreeadvice_bg);
					}
				}
			}
			String _adviceInfo = advice.getAdvice();
			if (null != _adviceInfo && "~!@#$".equalsIgnoreCase(_adviceInfo)) {
				_adviceInfo = "";
			}
			_newTaskFormAdviceFormItem._mAdviceInfoTextView
					.setText(null != _adviceInfo
							&& !"".equalsIgnoreCase(_adviceInfo) ? _adviceInfo
							: (advice.modified() ? _newTaskFormAdviceFormItem
									.getResources()
									.getString(
											R.string.task_adviceItem_modify_adviceInfo)
									: (advice.agreed() ? _newTaskFormAdviceFormItem
											.getResources()
											.getString(
													R.string.tdta_adviceSwitch_onText)
											: _newTaskFormAdviceFormItem
													.getResources()
													.getString(
															R.string.tdta_adviceSwitch_offText))));
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
