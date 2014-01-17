package com.futuo.iapprove.tab7tabcontent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.account.user.IAUserLocalStorageAttributes;
import com.futuo.iapprove.account.user.UserEnterpriseBean;
import com.futuo.iapprove.customwidget.IApproveImageBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveTabContentActivity;
import com.futuo.iapprove.customwidget.TodoTaskAdvice;
import com.futuo.iapprove.provider.UserEnterpriseProfileContentProvider.EnterpriseProfiles.EnterpriseProfile;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTasks.TodoTask;
import com.futuo.iapprove.service.CoreService;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationGenerator;
import com.futuo.iapprove.tab7tabcontent.task.TodoTaskApproveActivity;
import com.futuo.iapprove.tab7tabcontent.task.TodoTaskApproveActivity.TodoTaskApproveExtraData;
import com.futuo.iapprove.task.IApproveTaskAdviceBean;
import com.futuo.iapprove.task.TodoTaskBean;
import com.richitec.commontoolkit.customadapter.CTListCursorAdapter;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;

public class TodoListTabContentActivity extends IApproveTabContentActivity {

	private static final String LOG_TAG = TodoListTabContentActivity.class
			.getCanonicalName();

	// login user
	private UserBean _mLoginUser;

	// to-do list tab content view activity title view
	private TodoListTabContentViewActivityTitleView _mTitleView;

	// to-do list task list cursor adapter
	private TodoListTaskListAdapter _mTodoListTaskListCursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.todo_list_tab_content_activity_layout);

		// get login user
		_mLoginUser = UserManager.getInstance().getUser();

		// set subViews
		// set title
		setTitle((_mTitleView = new TodoListTabContentViewActivityTitleView(
				this)).generateTitleView());

		// set new approve application bar button item as right bar button item
		setRightBarButtonItem(new IApproveImageBarButtonItem(this,
				R.drawable.img_new_approveapplication_barbtnitem,
				new NewApproveApplicationBarBtnItemOnClickListener()));

		// get to-do list task listView
		ListView _todoListTaskListView = (ListView) findViewById(R.id.tdl_task_listView);

		// set to-do list task list cursor adapter
		_todoListTaskListView
				.setAdapter(_mTodoListTaskListCursorAdapter = new TodoListTaskListAdapter(
						this,
						R.layout.todo_list_task_layout,
						getContentResolver()
								.query(ContentUris
										.withAppendedId(
												TodoTask.ENTERPRISE_CONTENT_URI,
												IAUserExtension
														.getUserLoginEnterpriseId(_mLoginUser)),
										null,
										TodoTask.USER_ENTERPRISETODOLISTTASKS_WITHLOGINNAME_CONDITION,
										new String[] { _mLoginUser.getName() },
										null),
						new String[] {
								TodoListTaskListAdapter.TASKTITLE7APPLICANT_KEY,
								TodoListTaskListAdapter.TASKSUBMITTIMESTAMP_KEY,
								TodoListTaskListAdapter.TASKAPPROVEADVICES_KEY },
						new int[] { R.id.tdli_taskTitle7Applicant_textView,
								R.id.tdli_taskSubmitTimestamp_textView,
								R.id.tdli_taskAdvice_linearLayout }));

		// set to-do list task listView on item click listener
		_todoListTaskListView
				.setOnItemClickListener(new TodoListTaskListViewOnItemClickListener());
	}

	@Override
	protected boolean bindCoreServiceWhenOnResume() {
		// binder core service when on resume
		return true;
	}

	@Override
	protected void onCoreServiceConnected(CoreService coreService) {
		super.onCoreServiceConnected(coreService);

		// start get user login enterprise to-do list task
		coreService.startGetUserEnterpriseTodoListTask();
	}

	@Override
	protected void onUserEnterpriseChanged(Long newEnterpriseId) {
		super.onUserEnterpriseChanged(newEnterpriseId);

		// get and check core service
		CoreService _coreService = getCoreService();
		if (null != _coreService) {
			// force do on core service connected
			onCoreServiceConnected(_coreService);
		}

		// need to change user enterprise to-do list task query cursor change
		// user enterprise to-do list task query cursor
		_mTodoListTaskListCursorAdapter
				.changeCursor(getContentResolver()
						.query(ContentUris.withAppendedId(
								TodoTask.ENTERPRISE_CONTENT_URI,
								IAUserExtension
										.getUserLoginEnterpriseId(_mLoginUser)),
								null,
								TodoTask.USER_ENTERPRISETODOLISTTASKS_WITHLOGINNAME_CONDITION,
								new String[] { _mLoginUser.getName() }, null));
	}

	@Override
	protected void onCoreServiceDisconnected(CoreService coreService) {
		super.onCoreServiceDisconnected(coreService);

		// stop get user login enterprise to-do list task
		coreService.stopGetUserEnterpriseTodolistTask();
	}

	// inner class
	// to-do list tab content view activity title view
	class TodoListTabContentViewActivityTitleView extends FrameLayout {

		// title enterprises spinner and data fetching relativeLayout
		private Spinner _mTitleEnterprisesSpinner;
		private RelativeLayout _mTitleDataFetchingRelativeLayout;

		// title enterprises spinner cursor adapter
		private SimpleCursorAdapter _mTitleEnterprisesSpinnerCursorAdapter;

		public TodoListTabContentViewActivityTitleView(Context context) {
			super(context);

			// inflate to-do list tab content activity title layout
			LayoutInflater.from(context).inflate(
					R.layout.todo_list_tab_content_activity_title_layout, this);

			// get title enterprises spinner and data fetching relativeLayout
			_mTitleEnterprisesSpinner = (Spinner) findViewById(R.id.tdlt_enterprises_spinner);
			_mTitleDataFetchingRelativeLayout = (RelativeLayout) findViewById(R.id.tdlt_fetchingData_relativeLayout);

			// init title enterprises spinner cursor adapter
			_mTitleEnterprisesSpinnerCursorAdapter = new SimpleCursorAdapter(
					context,
					R.layout.todo_list_tab_content_activity_title_spinner_item_layout,
					context.getContentResolver()
							.query(EnterpriseProfile.ENTERPRISEPROFILES_CONTENT_URI,
									null,
									EnterpriseProfile.USER_ENTERPRISEPROFILES_WITHLOGINNAME_CONDITION,
									new String[] { _mLoginUser.getName() },
									null),
					new String[] { EnterpriseProfile.ENTERPRISE_ABBREVIATION },
					new int[] { android.R.id.text1 },
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

			// set title enterprises spinner drop down view resource
			_mTitleEnterprisesSpinnerCursorAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// set title enterprises spinner adapter
			_mTitleEnterprisesSpinner
					.setAdapter(_mTitleEnterprisesSpinnerCursorAdapter);

			// set title enterprises spinner fake on item selected listener
			_mTitleEnterprisesSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> spinner,
								View textView, int position, long id) {
							// set title enterprises spinner real on item
							// selected listener
							_mTitleEnterprisesSpinner
									.setOnItemSelectedListener(new TitleEnterprisesSpinnerOnItemSelectedListener());
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// nothing to do
						}

					});

			// set default selection
			_mTitleEnterprisesSpinner.setSelection(IAUserExtension
					.getUserLoginEnterpriseIndex(_mLoginUser));
		}

		// generate title view
		public View generateTitleView() {
			// show title enterprises spinner and hide title data fetching
			// relativeLayout
			_mTitleEnterprisesSpinner.setVisibility(View.VISIBLE);
			_mTitleDataFetchingRelativeLayout.setVisibility(View.GONE);

			return this;
		}

		// inner class
		// title enterprises spinner on item selected listener
		class TitleEnterprisesSpinnerOnItemSelectedListener implements
				OnItemSelectedListener {

			@Override
			public void onItemSelected(AdapterView<?> spinner, View textView,
					int position, long id) {
				// get title enterprise spinner cursor and move to position
				Cursor _titleEnterprisesSpinnerCursor = _mTitleEnterprisesSpinnerCursorAdapter
						.getCursor();
				if (true == _titleEnterprisesSpinnerCursor
						.moveToPosition(position)) {
					// get user selected enterprise id
					Long _selectedEnterpriseId = new UserEnterpriseBean(
							_titleEnterprisesSpinnerCursor).getEnterpriseId();

					// save user selected enterprise id and save to local
					// storage
					IAUserExtension.setUserLoginEnterpriseId(_mLoginUser,
							_selectedEnterpriseId);
					DataStorageUtils
							.putObject(
									IAUserLocalStorageAttributes.USER_LASTLOGINENTERPRISEID
											.name(), _selectedEnterpriseId);
				}

				// // refresh to-do list
				// refreshTodoList(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> spinner) {
				// nothing to do
			}

		}

	}

	// new approve application bar button item on click listener
	class NewApproveApplicationBarBtnItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// generate new approve application
			new NewApproveApplicationGenerator().genNewApproveApplication(null);
		}

	}

	// to-do list task list adapter
	class TodoListTaskListAdapter extends CTListCursorAdapter {

		// to-do task adapter keys
		public static final String TASKTITLE7APPLICANT_KEY = "task title and applicant key";
		public static final String TASKSUBMITTIMESTAMP_KEY = "task submit timestamp key";
		public static final String TASKAPPROVEADVICES_KEY = "task approve advices key";

		public TodoListTaskListAdapter(Context context, int itemsLayoutResId,
				Cursor c, String[] dataKeys, int[] itemsComponentResIds) {
			super(context, itemsLayoutResId, c, dataKeys, itemsComponentResIds,
					true);
		}

		@Override
		protected void onContentChanged() {
			// auto requery
			super.onContentChanged();

			//
		}

		@Override
		protected void appendCursorData(List<Object> data, Cursor cursor) {
			// check the cursor
			if (null != cursor) {
				// get to-do list task bean and append to data list
				data.add(new TodoTaskBean(cursor));
			} else {
				Log.e(LOG_TAG,
						"Query user login enterprise to-do list all tasks error, cursor = "
								+ cursor);
			}
		}

		@Override
		protected Map<String, ?> recombinationData(String dataKey,
				Object dataObject) {
			// define return data map and the data value for key in data object
			Map<String, Object> _dataMap = new HashMap<String, Object>();
			Object _dataValue = null;

			// check data object and convert to to-do list task object
			try {
				// convert data object to to-do list task
				TodoTaskBean _todoTaskObject = (TodoTaskBean) dataObject;

				// check data key and get data value for it
				if (TASKTITLE7APPLICANT_KEY.equalsIgnoreCase(dataKey)) {
					// title and applicant
					_dataValue = String
							.format(getResources()
									.getString(
											R.string.tdl_task_title7applicantName_format),
									_todoTaskObject.getTaskTitle(),
									_todoTaskObject.getApplicantName());
				} else if (TASKSUBMITTIMESTAMP_KEY.equalsIgnoreCase(dataKey)) {
					// submit timestamp
					_dataValue = formatTodoTaskSubmitTime(_todoTaskObject
							.getSubmitTimestamp());
				} else if (TASKAPPROVEADVICES_KEY.equalsIgnoreCase(dataKey)) {
					// submit timestamp
					_dataValue = _todoTaskObject.getAdvices();
				} else {
					Log.e(LOG_TAG, "Recombination data error, data key = "
							+ dataKey + " and data object = " + dataObject);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Convert data object to to-do list task bean object error, data = "
								+ dataObject);

				e.printStackTrace();
			}

			// put data value to map and return
			_dataMap.put(dataKey, _dataValue);
			return _dataMap;
		}

		@Override
		protected void bindView(View view, Map<String, ?> dataMap,
				String dataKey) {
			// get item data object
			Object _itemData = dataMap.get(dataKey);

			// check view type
			// textView
			if (view instanceof TextView) {
				// generate view text
				SpannableString _viewNewText = new SpannableString(
						null == _itemData ? "" : _itemData.toString());

				// check data class name
				if (_itemData instanceof SpannableString) {
					_viewNewText.setSpan(new ForegroundColorSpan(Color.RED), 0,
							_viewNewText.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				// set view text
				((TextView) view).setText(_viewNewText);
			} else if (view instanceof ImageView) {
				try {
					// define item data bitmap and convert item data to bitmap
					Bitmap _itemDataBitmap = (Bitmap) _itemData;

					// check and set imageView image
					if (null != _itemDataBitmap) {
						((ImageView) view).setImageBitmap(_itemDataBitmap);
					}
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item data to bitmap error, item data = "
									+ _itemData);
				}
			} else if (view instanceof LinearLayout) {
				try {
					// define item data list and convert item data to list
					@SuppressWarnings("unchecked")
					List<IApproveTaskAdviceBean> _itemDataList = (List<IApproveTaskAdviceBean>) _itemData;

					// check to-do list task advices
					if (null != _itemDataList && !_itemDataList.isEmpty()) {
						// show to-do list task advice parent view if needed
						ViewGroup _todoTaskAdviceParentView = (ViewGroup) ((LinearLayout) view)
								.getParent();
						if (View.VISIBLE != _todoTaskAdviceParentView
								.getVisibility()) {
							_todoTaskAdviceParentView
									.setVisibility(View.VISIBLE);
						}

						// remove all subviews from to-do list task advice
						// linearLayout
						((LinearLayout) view).removeAllViews();

						for (int i = 0; i < _itemDataList.size(); i++) {
							// generate new to-do list task advice
							TodoTaskAdvice _newTodoTaskAdvice = TodoTaskAdvice
									.generateTodoTaskAdvice(_itemDataList
											.get(i));

							// set it on click listener
							_newTodoTaskAdvice
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											Log.d(LOG_TAG, "@@, view = " + v);
										}

									});

							// add to to-do list task advice linearLayout
							((LinearLayout) view).addView(_newTodoTaskAdvice);

							// check index not the last
							if (i != _itemDataList.size() - 1) {
								// add to to-do list task advice separator
								// linearLayout
								((LinearLayout) view).addView(TodoTaskAdvice
										.generateTodoTaskAdviceSeparator(),
										new LayoutParams(
												LayoutParams.WRAP_CONTENT,
												LayoutParams.MATCH_PARENT));
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item data to list error, item data = "
									+ _itemData);
				}
			} else {
				Log.e(LOG_TAG, "Bind view error, view = " + view
						+ " not recognized, data key = " + dataKey
						+ " and data map = " + dataMap);
			}
		}

		// format to-do list task submit time
		private String formatTodoTaskSubmitTime(Long submitTimestamp) {
			// define return string builder
			StringBuilder _ret = new StringBuilder();

			// to-do list task submit time day and time format, format timeStamp
			final DateFormat _todoTaskSubmitTimeDayFormat = new SimpleDateFormat(
					"yy-MM-dd", Locale.getDefault());
			final DateFormat _todoTaskSubmitTimeTimeFormat = new SimpleDateFormat(
					"HH:mm", Locale.getDefault());

			// miliSceonds of day
			final Long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L;

			// get current system time
			Long _currentSystemTime = System.currentTimeMillis();

			// compare current system time and submit timestamp
			if (_currentSystemTime - submitTimestamp >= 0) {
				// get today zero o'clock calendar instance
				Calendar _todayZeroCalendarInstance = Calendar
						.getInstance(Locale.getDefault());
				_todayZeroCalendarInstance.set(Calendar.AM_PM, 0);
				_todayZeroCalendarInstance.set(Calendar.HOUR, 0);
				_todayZeroCalendarInstance.set(Calendar.MINUTE, 0);
				_todayZeroCalendarInstance.set(Calendar.SECOND, 0);
				_todayZeroCalendarInstance.set(Calendar.MILLISECOND, 0);

				// get to-do list task submit timestamp calendar instance
				Calendar _submitTimestampCalendarInstance = Calendar
						.getInstance(Locale.getDefault());
				_submitTimestampCalendarInstance
						.setTimeInMillis(submitTimestamp);

				// format day and time
				if (_submitTimestampCalendarInstance
						.before(_todayZeroCalendarInstance)) {
					// get today zero o'clock and submit timestamp time
					// different
					Long _today7submitTimestampCalendarTimeDifferent = _todayZeroCalendarInstance
							.getTimeInMillis()
							- _submitTimestampCalendarInstance
									.getTimeInMillis();

					// check time different
					if (_today7submitTimestampCalendarTimeDifferent <= MILLISECONDS_PER_DAY) {
						_ret.append(_mContext
								.getResources()
								.getString(
										R.string.tdl_task_yesterdaySubmit_submitTimestamp));
					} else {
						// get first day zero o'clock of week calendar instance
						Calendar _firstDayOfWeekZeroCalendarInstance = Calendar
								.getInstance(Locale.getDefault());
						_firstDayOfWeekZeroCalendarInstance
								.setTimeInMillis(_todayZeroCalendarInstance
										.getTimeInMillis());
						_firstDayOfWeekZeroCalendarInstance.set(
								Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

						if (_submitTimestampCalendarInstance
								.before(_firstDayOfWeekZeroCalendarInstance)) {
							_ret.append(_todoTaskSubmitTimeDayFormat
									.format(submitTimestamp));
						} else {
							_ret.append(_mContext
									.getResources()
									.getStringArray(
											R.array.tdl_task_submitTimestamp_daysOfWeek)[_submitTimestampCalendarInstance
									.get(Calendar.DAY_OF_WEEK) - 1]);
						}
					}
				} else {
					_ret.append(_todoTaskSubmitTimeTimeFormat
							.format(submitTimestamp));
				}
			} else {
				Log.e(LOG_TAG,
						"Format to-do list task submit time error, submit timestamp greater than current system time");
			}

			return _ret.toString();
		}

	}

	// to-do list task listView on item click listener
	class TodoListTaskListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> todoListTaskListView,
				View todoTaskItemContentView, int position, long id) {
			// define to-do list task approve extra data map
			Map<String, Object> _extraMap = new HashMap<String, Object>();

			// get the clicked to-do list task
			TodoTaskBean _clickedTodoTask = (TodoTaskBean) _mTodoListTaskListCursorAdapter
					.getDataList().get(position);

			// put user enterprise to-do list task title and sender fake id to
			// extra data map as param
			_extraMap.put(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKTITLE,
					_clickedTodoTask.getTaskTitle());
			_extraMap.put(
					TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKSENDERFAKEID,
					_clickedTodoTask.getSenderFakeId());

			// go to to-do list task approve activity with extra data map
			pushActivity(TodoTaskApproveActivity.class, _extraMap);
		}

	}

}
