package com.futuo.iapprove.tab7tabcontent;

import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.account.user.IAUserLocalStorageAttributes;
import com.futuo.iapprove.customwidget.IApproveTabContentActivity;
import com.futuo.iapprove.utils.HttpRequestParamUtils;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.DataStorageUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.StringUtils;

public class TodoListTabContentActivity extends IApproveTabContentActivity {

	private static final String LOG_TAG = TodoListTabContentActivity.class
			.getCanonicalName();

	// need to refresh to-do list flag
	private Boolean _mNeed2RefreshTodoList;

	// login user
	private UserBean _mLoginUser;

	// to-do list tab content view activity title view
	private TodoListTabContentViewActivityTitleView _mTitleView;

	// to-do list data fetching flag
	private Boolean _mDataFetching;

	// user enterprise to-do list change progress dialog
	private ProgressDialog _mUserEnterpriseTodoListChangeProgDlg;

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

		// refresh to-do list when on create
		refreshTodoList(false);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// check need to refresh to-do list flag
		if (null != _mNeed2RefreshTodoList && true == _mNeed2RefreshTodoList) {
			// refresh to-do list
			refreshTodoList(false);
		}
	}

	// refresh to-do list with changed user enterprise flag
	private void refreshTodoList(Boolean changeUserEnterprise) {
		// to-do list data fetching
		_mDataFetching = true;

		// check changed user enterprise flag
		if (changeUserEnterprise) {
			// show user enterprise to-do list change process dialog
			_mUserEnterpriseTodoListChangeProgDlg = ProgressDialog
					.show(TodoListTabContentActivity.this,
							null,
							getString(R.string.tdl_userEnterpriseChange_progDlg_message),
							true);
		} else {
			// update title
			setTitle(_mTitleView.generateTitleView());
		}

		// get to-do list
		// generate get to-do list post http request param
		Map<String, String> _getTodoListPostHttpReqParam = HttpRequestParamUtils
				.genUserSigHttpReqParam();

		// put get to-do list action, user enterprise id, state and page
		// number in
		_getTodoListPostHttpReqParam.put(
				getResources().getString(
						R.string.rbgServer_commonReqParam_action),
				getResources().getString(
						R.string.rbgServer_getTodoListReqParam_action));
		_getTodoListPostHttpReqParam
				.put(getResources()
						.getString(
								R.string.rbgServer_getIApproveListReqParam_enterpriseId),
						StringUtils.base64Encode(IAUserExtension
								.getUserLoginEnterpriseId(_mLoginUser)
								.toString()));
		_getTodoListPostHttpReqParam.put(
				getResources().getString(
						R.string.rbgServer_getIApproveListReqParam_state),
				getResources().getString(
						R.string.rbgServer_getTodoListReqParam_state));
		_getTodoListPostHttpReqParam.put(
				getResources().getString(
						R.string.rbgServer_getIApproveListReqParam_pageNum),
				"1");

		// send get to-do list post http request
		HttpUtils.postRequest(getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.get_todoList_url),
				PostRequestFormat.URLENCODED, _getTodoListPostHttpReqParam,
				null, HttpRequestType.ASYNCHRONOUS,
				new GetTodoListPostHttpRequestListener());
	}

	// close user enterprise to-do list change process dialog
	private void closeUserEnterpriseTodoListChangeProgDlg() {
		// check and dismiss user enterprise to-do list change process dialog
		if (null != _mUserEnterpriseTodoListChangeProgDlg) {
			_mUserEnterpriseTodoListChangeProgDlg.dismiss();
		}
	}

	// inner class
	// to-do list tab content view activity title view
	class TodoListTabContentViewActivityTitleView extends FrameLayout {

		// title enterprises spinner and data fetching relativeLayout
		private Spinner _mTitleEnterprisesSpinner;
		private RelativeLayout _mTitleDataFetchingRelativeLayout;

		// title enterprises spinner array adapter
		private ArrayAdapter<String> _mTitleEnterprisesSpinnerArrayAdapter;

		public TodoListTabContentViewActivityTitleView(Context context) {
			super(context);

			// inflate to-do list tab content activity title layout
			LayoutInflater.from(context).inflate(
					R.layout.todo_list_tab_content_activity_title_layout, this);

			// get title enterprises spinner and data fetching relativeLayout
			_mTitleEnterprisesSpinner = (Spinner) findViewById(R.id.tdlt_enterprises_spinner);
			_mTitleDataFetchingRelativeLayout = (RelativeLayout) findViewById(R.id.tdlt_fetchingData_relativeLayout);

			// init title enterprises spinner array adapter
			_mTitleEnterprisesSpinnerArrayAdapter = new ArrayAdapter<String>(
					context,
					R.layout.todo_list_tab_content_activity_title_spinner_item_layout,
					IAUserExtension.getUserEnterpriseNames());

			// set title enterprises spinner drop down view resource
			_mTitleEnterprisesSpinnerArrayAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// set title enterprises spinner adapter
			_mTitleEnterprisesSpinner
					.setAdapter(_mTitleEnterprisesSpinnerArrayAdapter);

			// set title enterprises spinner fake on item selected listener
			_mTitleEnterprisesSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
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
					.getUserLoginEnterpriseIndex(IAUserExtension
							.getUserLoginEnterpriseId(_mLoginUser)));
		}

		// generate title view
		public View generateTitleView() {
			// check to-do list data fetching
			if (null != _mDataFetching && true == _mDataFetching) {
				// hide title enterprises spinner and show title data fetching
				// relativeLayout
				_mTitleEnterprisesSpinner.setVisibility(View.GONE);
				_mTitleDataFetchingRelativeLayout.setVisibility(View.VISIBLE);
			} else {
				// show title enterprises spinner and hide title data fetching
				// relativeLayout
				_mTitleEnterprisesSpinner.setVisibility(View.VISIBLE);
				_mTitleDataFetchingRelativeLayout.setVisibility(View.GONE);
			}

			return this;
		}

		// inner class
		// title enterprises spinner on item selected listener
		class TitleEnterprisesSpinnerOnItemSelectedListener implements
				OnItemSelectedListener {

			@Override
			public void onItemSelected(AdapterView<?> spinner, View textView,
					int position, long id) {
				// get user selected enterprise id
				Long _selectedEnterpriseId = IAUserExtension
						.getUserEnterprises(_mLoginUser).get((int) id).getId();

				// save user selected enterprise id and save to local storage
				IAUserExtension.setUserLoginEnterpriseId(_mLoginUser,
						_selectedEnterpriseId);
				DataStorageUtils.putObject(
						IAUserLocalStorageAttributes.USER_LASTLOGINENTERPRISEID
								.name(), _selectedEnterpriseId);

				// refresh to-do list
				refreshTodoList(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> spinner) {
				// nothing to do
			}

		}

	}

	// get to-do list post http request listener
	class GetTodoListPostHttpRequestListener extends OnHttpRequestListener {

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// close user enterprise change process dialog
			closeUserEnterpriseTodoListChangeProgDlg();

			_mDataFetching = false;
			// update title
			setTitle(_mTitleView.generateTitleView());

			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send get to-do list post http request successful, response entity string = "
							+ _respEntityString);

			//
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			// close user enterprise change process dialog
			closeUserEnterpriseTodoListChangeProgDlg();

			_mDataFetching = false;
			// update title
			setTitle(_mTitleView.generateTitleView());

			Log.e(LOG_TAG, "Send get to-do list post http request failed");

			Toast.makeText(TodoListTabContentActivity.this,
					R.string.toast_request_exception, Toast.LENGTH_SHORT)
					.show();
		}

	}

}
