package com.futuo.iapprove.tab7tabcontent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.customwidget.IApproveTabContentActivity;
import com.futuo.iapprove.tab7tabcontent.task.HistoryRecordTaskDetailInfoActivity;
import com.futuo.iapprove.tab7tabcontent.task.HistoryRecordTaskDetailInfoActivity.HistoryRecordTaskListTaskDetailInfoExtraData;
import com.futuo.iapprove.task.IApproveTaskBean;
import com.futuo.iapprove.utils.HttpRequestParamUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class HistoryRecordTabContentActivity extends IApproveTabContentActivity {

	private static final String LOG_TAG = HistoryRecordTabContentActivity.class
			.getCanonicalName();

	// history record title segment radioGroup
	private RadioGroup _mHistoryRecordTitleSegment;

	// history record checked and my application segment view
	private FrameLayout _mCheckedSegmentView;
	private FrameLayout _mMyApplicationSegmentView;

	// history record task list view and its pull to refresh list view
	private ListView _mHisrotyRecordTaskListView;
	private PullToRefreshListView _mHisrotyRecordTaskPull2RefreshListView;

	// history record task pull to refresh list view on refresh listener and on
	// last item visible listener
	private HistoryRecordTaskListPull2RefreshListViewOnRefreshListener _mHisrotyRecordTaskPull2RefreshListViewOnRefreshListener;
	private HistoryRecordTaskListPull2RefreshListViewOnLastItemVisibleListener _mHisrotyRecordTaskPull2RefreshListViewOnLastItemVisibleListener;

	// history record task list view on item click listener
	private HistoryRecordTaskListOnItemClickListener _mHisrotyRecordTaskListViewOnItemClickListener;

	// history record task list view's footer view
	private View _mHistoryRecordTaskListViewFooterView;

	// history record task list list adapter data list
	private List<IApproveTaskBean> _mHisrotyRecordTaskList = new ArrayList<IApproveTaskBean>();
	private List<Map<String, ?>> _mHisrotyRecordTaskListAdapterDataList = new ArrayList<Map<String, ?>>();

	// history record task list adapter
	private HistoryRecordTaskListAdapter _mHisrotyRecordTaskListAdapter;

	// history record task list has next page flag and page number
	private Boolean _mHistoryRecordTaskListHasNextPage;
	private Integer _mHistoryRecordTaskListPageNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.history_record_tab_content_activity_layout);

		// set subViews
		// set history record title segment as title
		setTitle(generateHistoryRecordTitleSegment(this), "");

		// get history record checked and my application segment view
		_mCheckedSegmentView = (FrameLayout) findViewById(R.id.hr_checked_segment_view);
		_mMyApplicationSegmentView = (FrameLayout) findViewById(R.id.hr_myApplication_segment_view);

		// get history record task list view and its pull to refresh list view
		_mHisrotyRecordTaskPull2RefreshListView = (PullToRefreshListView) _mCheckedSegmentView
				.findViewById(R.id.hr_segmentContent_listView);
		_mHisrotyRecordTaskListView = _mHisrotyRecordTaskPull2RefreshListView
				.getRefreshableView();

		// set history record task list pull to refresh listView on refresh
		// listener
		_mHisrotyRecordTaskPull2RefreshListView
				.setOnRefreshListener(_mHisrotyRecordTaskPull2RefreshListViewOnRefreshListener = new HistoryRecordTaskListPull2RefreshListViewOnRefreshListener());

		// set history record task list pull to refresh listView on last item
		// visible listener
		_mHisrotyRecordTaskPull2RefreshListView
				.setOnLastItemVisibleListener(_mHisrotyRecordTaskPull2RefreshListViewOnLastItemVisibleListener = new HistoryRecordTaskListPull2RefreshListViewOnLastItemVisibleListener());

		// set its on click listener
		_mHisrotyRecordTaskListView
				.setOnItemClickListener(_mHisrotyRecordTaskListViewOnItemClickListener = new HistoryRecordTaskListOnItemClickListener());

		// inflate history record task list view footer view
		_mHistoryRecordTaskListViewFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(
						R.layout.historyrecord_task_listview_footerview_layout,
						null);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// refresh history record task list
		getHistoryRecordTaskList(null);
	}

	// generate history record title segment
	private RadioGroup generateHistoryRecordTitleSegment(Context context) {
		// inflater history record title segment layout
		View _inflateView = LayoutInflater.from(context).inflate(
				R.layout.history_record_title_segment_layout, null);

		// get history record title segment radioGroup
		_mHistoryRecordTitleSegment = (RadioGroup) _inflateView
				.findViewById(R.id.hr_historyRecord_titleSegment_radioGroup);

		// set on checked changed listener
		_mHistoryRecordTitleSegment
				.setOnCheckedChangeListener(new HistoryRecordTitleSegmentOnCheckedChangeListener());

		return _mHistoryRecordTitleSegment;
	}

	// get history record task list with start page number
	private void getHistoryRecordTaskList(Integer pageNumber) {
		// get history record task list post http request url
		StringBuilder _getHistoryRecordTaskListPostHttpReqUrl = new StringBuilder(
				getResources().getString(R.string.server_url));

		// get history record task list post http request param
		Map<String, String> _getHistoryRecordTaskListPostHttpReqParam = HttpRequestParamUtils
				.genUserSigHttpReqParam();

		// put get user enterprise history record task list enterprise id and
		// start page in
		_getHistoryRecordTaskListPostHttpReqParam.put(
				getResources().getString(
						R.string.rbgServer_getIApproveReqParam_enterpriseId),
				StringUtils.base64Encode(IAUserExtension
						.getUserLoginEnterpriseId(_mLoginUser).toString()));
		_getHistoryRecordTaskListPostHttpReqParam.put(
				getResources().getString(
						R.string.rbgServer_getIApproveListReqParam_pageNum),
				null != pageNumber ? pageNumber.toString() : "1");

		// check history record title segment radioGroup checked id and put get
		// user enterprise history record task list action, state and start page
		// in
		switch (_mHistoryRecordTitleSegment.getCheckedRadioButtonId()) {
		case R.id.hr_checked_segment_radioButton:
			Log.d(LOG_TAG, "Get checked segment task list");

			_getHistoryRecordTaskListPostHttpReqParam.put(
					getResources().getString(
							R.string.rbgServer_commonReqParam_action),
					getResources().getString(
							R.string.rbgServer_getCheckedListReqParam_action));
			_getHistoryRecordTaskListPostHttpReqParam.put(
					getResources().getString(
							R.string.rbgServer_getIApproveReqParam_state),
					getResources().getString(
							R.string.rbgServer_getCheckedListReqParam_state));

			// update get checked history record task list post request url
			_getHistoryRecordTaskListPostHttpReqUrl.append(getResources()
					.getString(R.string.get_userEnterpriseCheckedTaskList_url));
			break;

		case R.id.hr_myApplication_segment_radioButton:
		default:
			Log.d(LOG_TAG, "Get my application segment task list");

			_getHistoryRecordTaskListPostHttpReqParam
					.put(getResources().getString(
							R.string.rbgServer_commonReqParam_action),
							getResources()
									.getString(
											R.string.rbgServer_getMyApplicationListReqParam_action));
			_getHistoryRecordTaskListPostHttpReqParam
					.put(getResources().getString(
							R.string.rbgServer_getIApproveReqParam_state),
							getResources()
									.getString(
											R.string.rbgServer_getMyApplicationListReqParam_state));

			// update get checked history record task list post request url
			_getHistoryRecordTaskListPostHttpReqUrl
					.append(getResources()
							.getString(
									R.string.get_userEnterpriseMyApplicationTaskList_url));
			break;
		}

		// send get history record task list post http request
		HttpUtils.postRequest(
				_getHistoryRecordTaskListPostHttpReqUrl.toString(),
				PostRequestFormat.URLENCODED,
				_getHistoryRecordTaskListPostHttpReqParam, null,
				HttpRequestType.ASYNCHRONOUS,
				new GetHistoryRecordTaskListOnHttpRequestListener(pageNumber));
	}

	// get history record task list view footer view
	private View getHistoryRecordTaskListViewFooterView(
			HistoryRecordTaskListViewFooterViewType footerViewType) {
		// get loading more and no more history record task footer view
		View _loadingMoreHistoryRecordTaskFooterView = _mHistoryRecordTaskListViewFooterView
				.findViewById(R.id.hrtfv_loadingMore_historyRecordTask_footerView);
		View _noMoreHistoryRecordTaskFooterView = _mHistoryRecordTaskListViewFooterView
				.findViewById(R.id.hrtfv_noMore_historyRecordTask_footerView);

		// check history record task list view footer view type
		switch (footerViewType) {
		case LOADINGMORE_HISTORYRECORDTASK:
			// show loading more history record task footer view and hide no
			// more history record task footer view
			_loadingMoreHistoryRecordTaskFooterView.setVisibility(View.VISIBLE);
			_noMoreHistoryRecordTaskFooterView.setVisibility(View.GONE);
			break;

		case NOMORE_HISTORYRECORDTASK:
		default:
			// hide loading more history record task footer view and show no
			// more history record task footer view
			_loadingMoreHistoryRecordTaskFooterView.setVisibility(View.GONE);
			_noMoreHistoryRecordTaskFooterView.setVisibility(View.VISIBLE);
			break;
		}

		return _mHistoryRecordTaskListViewFooterView;
	}

	// generate user enterprise history record task listView adapter data list
	private List<Map<String, ?>> generateHistoryRecordTaskListDataList(
			JSONArray historyRecordTaskListJsonArray) {
		// history record task list data list
		List<Map<String, ?>> _dataList = new ArrayList<Map<String, ?>>();

		for (int i = 0; i < historyRecordTaskListJsonArray.length(); i++) {
			// get user enterprise each history record list task
			IApproveTaskBean _historyRecordTask = new IApproveTaskBean(
					JSONUtils.getJSONObjectFromJSONArray(
							historyRecordTaskListJsonArray, i));

			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// set data
			_dataMap.put(
					HistoryRecordTaskListAdapter.TASKSTATE_KEY,
					_historyRecordTask.ended() ? BitmapFactory.decodeResource(
							getResources(), android.R.drawable.ic_media_pause)
							: BitmapFactory.decodeResource(getResources(),
									android.R.drawable.ic_media_play));
			_dataMap.put(
					HistoryRecordTaskListAdapter.TASKTITLE7APPLICANT_KEY,
					String.format(
							getResources()
									.getString(
											R.string.tdl_task_title7applicantName_format),
							_historyRecordTask.getTaskTitle(),
							_historyRecordTask.getApplicantName()));
			_dataMap.put(HistoryRecordTaskListAdapter.TASKSCREATETIMESTAMP_KEY,
					formatHistoryRecordTaskSubmitTime(_historyRecordTask
							.getCreateTimestamp()));
			_dataMap.put(HistoryRecordTaskListAdapter.TASKADVICES_KEY,
					_historyRecordTask.getAdvices());

			// add to data list
			_dataList.add(_dataMap);
		}

		return _dataList;
	}

	// format history record task list task submit time
	private String formatHistoryRecordTaskSubmitTime(Long submitTimestamp) {
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
			Calendar _todayZeroCalendarInstance = Calendar.getInstance(Locale
					.getDefault());
			_todayZeroCalendarInstance.set(Calendar.AM_PM, 0);
			_todayZeroCalendarInstance.set(Calendar.HOUR, 0);
			_todayZeroCalendarInstance.set(Calendar.MINUTE, 0);
			_todayZeroCalendarInstance.set(Calendar.SECOND, 0);
			_todayZeroCalendarInstance.set(Calendar.MILLISECOND, 0);

			// get to-do list task submit timestamp calendar instance
			Calendar _submitTimestampCalendarInstance = Calendar
					.getInstance(Locale.getDefault());
			_submitTimestampCalendarInstance.setTimeInMillis(submitTimestamp);

			// format day and time
			if (_submitTimestampCalendarInstance
					.before(_todayZeroCalendarInstance)) {
				// get today zero o'clock and submit timestamp time
				// different
				Long _today7submitTimestampCalendarTimeDifferent = _todayZeroCalendarInstance
						.getTimeInMillis()
						- _submitTimestampCalendarInstance.getTimeInMillis();

				// check time different
				if (_today7submitTimestampCalendarTimeDifferent <= MILLISECONDS_PER_DAY) {
					_ret.append(getResources().getString(
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
						_ret.append(getResources().getStringArray(
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

	// inner class
	// history record title segment on checked changed listener
	class HistoryRecordTitleSegmentOnCheckedChangeListener implements
			OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// check checked id
			switch (checkedId) {
			case R.id.hr_checked_segment_radioButton:
				// show checked segment view and hide my application segment
				// view
				_mCheckedSegmentView.setVisibility(View.VISIBLE);
				_mMyApplicationSegmentView.setVisibility(View.GONE);

				// reset history record task list and its pull to refresh list
				// view
				_mHisrotyRecordTaskPull2RefreshListView = (PullToRefreshListView) _mCheckedSegmentView
						.findViewById(R.id.hr_segmentContent_listView);
				_mHisrotyRecordTaskListView = _mHisrotyRecordTaskPull2RefreshListView
						.getRefreshableView();

				// reset history record task list pull to refresh listView on
				// refresh
				// listener and on last item
				// visible listener
				_mHisrotyRecordTaskPull2RefreshListView
						.setOnRefreshListener(_mHisrotyRecordTaskPull2RefreshListViewOnRefreshListener);
				_mHisrotyRecordTaskPull2RefreshListView
						.setOnLastItemVisibleListener(_mHisrotyRecordTaskPull2RefreshListViewOnLastItemVisibleListener);

				// reset history record task list item on click listener
				_mHisrotyRecordTaskListView
						.setOnItemClickListener(_mHisrotyRecordTaskListViewOnItemClickListener);

				// refresh history record task list
				getHistoryRecordTaskList(null);
				break;

			case R.id.hr_myApplication_segment_radioButton:
			default:
				// hide checked segment view and show my application segment
				// view
				_mCheckedSegmentView.setVisibility(View.GONE);
				_mMyApplicationSegmentView.setVisibility(View.VISIBLE);

				// reset history record task list and its pull to refresh list
				// view
				_mHisrotyRecordTaskPull2RefreshListView = (PullToRefreshListView) _mMyApplicationSegmentView
						.findViewById(R.id.hr_segmentContent_listView);
				_mHisrotyRecordTaskListView = _mHisrotyRecordTaskPull2RefreshListView
						.getRefreshableView();

				// reset history record task list pull to refresh listView on
				// refresh
				// listener and on last item
				// visible listener
				_mHisrotyRecordTaskPull2RefreshListView
						.setOnRefreshListener(_mHisrotyRecordTaskPull2RefreshListViewOnRefreshListener);
				_mHisrotyRecordTaskPull2RefreshListView
						.setOnLastItemVisibleListener(_mHisrotyRecordTaskPull2RefreshListViewOnLastItemVisibleListener);

				// reset history record task list item on click listener
				_mHisrotyRecordTaskListView
						.setOnItemClickListener(_mHisrotyRecordTaskListViewOnItemClickListener);

				// refresh history record task list
				getHistoryRecordTaskList(null);
				break;
			}
		}

	}

	// history record task list pull to refresh listView on refresh listener
	class HistoryRecordTaskListPull2RefreshListViewOnRefreshListener implements
			OnRefreshListener<ListView> {

		@Override
		public void onRefresh(PullToRefreshBase<ListView> refreshView) {
			// refresh history record task list
			getHistoryRecordTaskList(null);
		}

	}

	// history record task list view footer view type
	enum HistoryRecordTaskListViewFooterViewType {
		LOADINGMORE_HISTORYRECORDTASK, NOMORE_HISTORYRECORDTASK
	}

	// history record task list pull to refresh listView on last item visible
	// listener
	class HistoryRecordTaskListPull2RefreshListViewOnLastItemVisibleListener
			implements OnLastItemVisibleListener {

		@Override
		public void onLastItemVisible() {
			// get and check history record task list pager hasNext flag
			if (null != _mHistoryRecordTaskListHasNextPage
					&& true == _mHistoryRecordTaskListHasNextPage
							.booleanValue()) {
				// set loading more history record task footer view
				_mHisrotyRecordTaskListView
						.addFooterView(getHistoryRecordTaskListViewFooterView(HistoryRecordTaskListViewFooterViewType.LOADINGMORE_HISTORYRECORDTASK));

				// send get more history record task post http request
				getHistoryRecordTaskList(_mHistoryRecordTaskListPageNum);
			} else {
				// set no more history record task footer view
				_mHisrotyRecordTaskListView
						.addFooterView(getHistoryRecordTaskListViewFooterView(HistoryRecordTaskListViewFooterViewType.NOMORE_HISTORYRECORDTASK));
			}
		}
	}

	// history record task list adapter
	class HistoryRecordTaskListAdapter extends CTListAdapter {

		// history record task adapter keys
		public static final String TASKSTATE_KEY = "task state key";
		public static final String TASKTITLE7APPLICANT_KEY = "task title and applicant key";
		public static final String TASKSCREATETIMESTAMP_KEY = "task create timestamp key";
		public static final String TASKADVICES_KEY = "task advices key";

		public HistoryRecordTaskListAdapter(Context context,
				List<Map<String, ?>> data, int itemsLayoutResId,
				String[] dataKeys, int[] itemsComponentResIds) {
			super(context, data, itemsLayoutResId, dataKeys,
					itemsComponentResIds);
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
			} else {
				Log.e(LOG_TAG, "Bind view error, view = " + view
						+ " not recognized, data key = " + dataKey
						+ " and data map = " + dataMap);
			}
		}

	}

	// history record task list view on item click listener
	class HistoryRecordTaskListOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> hisrotyRecordTaskListView,
				View hisrotyRecordTaskItemContentView, int position, long id) {
			// define history record task list task detail info extra data map
			Map<String, Object> _extraMap = new HashMap<String, Object>();

			// get the clicked history record task data map
			IApproveTaskBean _clickedHistoryRecordTask = _mHisrotyRecordTaskList
					.get((int) id);

			// put user enterprise to-do list task id, title, sender fake id,
			// status and advice list to extra data map as param
			_extraMap
					.put(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKID,
							_clickedHistoryRecordTask.getTaskId());
			_extraMap
					.put(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKTITLE,
							_clickedHistoryRecordTask.getTaskTitle());
			_extraMap
					.put(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKSENDERFAKEID,
							_clickedHistoryRecordTask.getSenderFakeId());
			_extraMap
					.put(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKSTATUS,
							_clickedHistoryRecordTask.getTaskStatus());
			_extraMap
					.put(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKADVICES,
							_clickedHistoryRecordTask.getAdvices());

			// go to history record task list task detail info activity with
			// extra data map
			pushActivity(HistoryRecordTaskDetailInfoActivity.class, _extraMap);
		}

	}

	// get history record task list post http request listener
	class GetHistoryRecordTaskListOnHttpRequestListener extends
			OnHttpRequestListener {

		// history record task list page number
		private Integer _mPageNumber;

		public GetHistoryRecordTaskListOnHttpRequestListener(Integer pageNumber) {
			super();

			// save history record task list pager number
			_mPageNumber = pageNumber;
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// check page number
			if (null == _mPageNumber) {
				// initialize history record task list page number
				_mHistoryRecordTaskListPageNum = Integer.valueOf(1);

				// history record task pull to refresh listView refresh complete
				_mHisrotyRecordTaskPull2RefreshListView.onRefreshComplete();
			} else {
				// remove history record task list view footer view
				_mHisrotyRecordTaskListView
						.removeFooterView(_mHistoryRecordTaskListViewFooterView);
			}

			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send get user enterprise history record task list post http request successful, response entity string = "
							+ _respEntityString);

			// get and check http response entity string error json data
			JSONObject _respJsonData = JSONUtils
					.toJSONObject(_respEntityString);

			if (null != _respJsonData) {
				// get and check error message
				String _errorMsg = JSONUtils.getStringFromJSONObject(
						_respJsonData,
						getResources().getString(
								R.string.rbgServer_commonReqResp_error));

				if (null != _errorMsg) {
					Log.e(LOG_TAG,
							"Get user enterprise history record task list failed, response error message = "
									+ _errorMsg);

					Toast.makeText(HistoryRecordTabContentActivity.this,
							_errorMsg, Toast.LENGTH_SHORT).show();
				} else {
					Log.d(LOG_TAG,
							"Get user enterprise history record task list successful");

					// get has next page flag
					_mHistoryRecordTaskListHasNextPage = JSONUtils
							.getBooleanFromJSONObject(
									_respJsonData,
									getResources()
											.getString(
													R.string.rbgServer_getIApproveListReqResp_hasNext));
					if (null != _mHistoryRecordTaskListHasNextPage
							&& true == _mHistoryRecordTaskListHasNextPage
									.booleanValue()) {
						// increase history record task list page number
						_mHistoryRecordTaskListPageNum++;
					}

					// get and check user enterprise history record task list
					JSONArray _historyRecordTaskListJsonArray = JSONUtils
							.getJSONArrayFromJSONObject(
									_respJsonData,
									getResources()
											.getString(
													R.string.rbgServer_getIApproveListReqResp_taskList));
					if (null != _historyRecordTaskListJsonArray) {
						// check page number again
						if (null == _mPageNumber) {
							// clear history record task
							_mHisrotyRecordTaskList.clear();

							// set history record task listView adapter
							_mHisrotyRecordTaskListView
									.setAdapter(_mHisrotyRecordTaskListAdapter = new HistoryRecordTaskListAdapter(
											HistoryRecordTabContentActivity.this,
											_mHisrotyRecordTaskListAdapterDataList = generateHistoryRecordTaskListDataList(_historyRecordTaskListJsonArray),
											R.layout.historyrecord_list_task_layout,
											new String[] {
													HistoryRecordTaskListAdapter.TASKSTATE_KEY,
													HistoryRecordTaskListAdapter.TASKTITLE7APPLICANT_KEY,
													HistoryRecordTaskListAdapter.TASKSCREATETIMESTAMP_KEY },
											new int[] {
													R.id.hrli_taskState_imageView,
													R.id.hrli_taskTitle7Applicant_textView,
													R.id.hrli_taskSubmitTimestamp_textView }));
						} else {
							// generate more history record task data list and
							// add it to my talking
							// group adapter data list
							for (Map<String, ?> moreHistoryRecordTaskDataMap : generateHistoryRecordTaskListDataList(_historyRecordTaskListJsonArray)) {
								_mHisrotyRecordTaskListAdapterDataList
										.add(moreHistoryRecordTaskDataMap);
							}

							// notify my talking group adapter changed
							_mHisrotyRecordTaskListAdapter
									.notifyDataSetChanged();
						}

						for (int i = 0; i < _historyRecordTaskListJsonArray
								.length(); i++) {
							// get user enterprise each history record list task
							IApproveTaskBean _historyRecordTask = new IApproveTaskBean(
									JSONUtils.getJSONObjectFromJSONArray(
											_historyRecordTaskListJsonArray, i));

							// add history record task to list
							_mHisrotyRecordTaskList.add(_historyRecordTask);
						}
					}
				}
			} else {
				Log.e(LOG_TAG,
						"Get user enterprise history record task list failed, response error message unrecognized");

				Toast.makeText(HistoryRecordTabContentActivity.this,
						R.string.toast_requestResp_unrecognized,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			// check page number
			if (null == _mPageNumber) {
				// history record task pull to refresh listView refresh complete
				_mHisrotyRecordTaskPull2RefreshListView.onRefreshComplete();
			} else {
				// remove history record task list view footer view
				_mHisrotyRecordTaskListView
						.removeFooterView(_mHistoryRecordTaskListViewFooterView);
			}

			Log.e(LOG_TAG,
					"Send get user enterprise history record task list post http request failed");

			Toast.makeText(HistoryRecordTabContentActivity.this,
					R.string.toast_request_exception, Toast.LENGTH_SHORT)
					.show();
		}

	}

}
