package com.futuo.iapprove.tab7tabcontent.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SlidingDrawer;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.person.PersonBean;
import com.futuo.iapprove.customwidget.CommonFormSeparator;
import com.futuo.iapprove.customwidget.IApproveImageBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.customwidget.TaskFormAdviceFormItem;
import com.futuo.iapprove.customwidget.TaskFormAdviceFormItem.TaskFormAdviceType;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormAttachmentType;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormVoiceAttachmentInfoDataKeys;
import com.futuo.iapprove.customwidget.TaskFormItemFormItem;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.futuo.iapprove.provider.UserEnterpriseProfileContentProvider.EnterpriseProfiles.EnterpriseProfile;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTaskAttachments.TodoTaskAttachment;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTaskFormItems.TodoTaskFormItem;
import com.futuo.iapprove.service.CoreService;
import com.futuo.iapprove.service.CoreService.LocalBinder;
import com.futuo.iapprove.task.IApproveTaskAdviceBean;
import com.futuo.iapprove.task.IApproveTaskAttachmentBean;
import com.futuo.iapprove.task.IApproveTaskFormItemBean;
import com.futuo.iapprove.utils.AudioUtils;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;

@SuppressWarnings("deprecation")
public class TodoTaskApproveActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = TodoTaskApproveActivity.class
			.getCanonicalName();

	// user enterprise to-do list task sender fake id
	private Long _mTodoTaskSenderFakeId;

	// to-do list task form item form linearLayout
	private LinearLayout _mFormItemFormLinearLayout;

	// to-do list task form item id(key) and form item form item view(value) map
	private Map<Long, TaskFormItemFormItem> _mFormItemId7FormItemFormItemMap;

	// login user
	private UserBean _mLoginUser;

	// to-do list task form attachment form parent frameLayout and form
	// linearLayout
	private FrameLayout _mAttachmentFormParentFrameLayout;
	private LinearLayout _mAttachmentFormLinearLayout;

	// to-do list task attachment id(key) and form attachment form item
	// view(value) map
	private Map<Long, TaskFormAttachmentFormItem> _mFormAttachmentId7FormAttachmentFormItemMap;

	// to-do list task form advice form parent frameLayout and form linearLayout
	private FrameLayout _mAdviceFormParentFrameLayout;
	private LinearLayout _mAdviceFormLinearLayout;

	// core service connection
	private CoreServiceConnection _mCoreServiceConnection;

	// to-do list task submit contact list sliding drawer and its listView
	private SlidingDrawer _mSubmitContactListSlidingDrawer;
	private ListView _mSubmitContactListView;

	// advice input editText
	private EditText _mAdviceInputEditText;

	// my advice advisor
	private PersonBean _mMyAdviceAdvisor;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.todo_task_approve_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define user enterprise to-do list task title and advice list
		String _todoTaskTitle = "";
		List<IApproveTaskAdviceBean> _todoTaskAdvices = new ArrayList<IApproveTaskAdviceBean>();

		// check the data
		if (null != _extraData) {
			// get to-do list task title, sender fake id and advice list
			_todoTaskTitle = _extraData
					.getString(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKTITLE);
			_mTodoTaskSenderFakeId = _extraData
					.getLong(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKSENDERFAKEID);
			_todoTaskAdvices
					.addAll((Collection<? extends IApproveTaskAdviceBean>) _extraData
							.getSerializable(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKADVICES));
		}

		// get login user
		_mLoginUser = UserManager.getInstance().getUser();

		// get and check my advice advisor cursor
		Cursor _cursor = getContentResolver()
				.query(EnterpriseProfile.ENTERPRISEPROFILES_CONTENT_URI,
						null,
						EnterpriseProfile.USER_ENTERPRISEPROFILE_WITHENTERPRISEID7LOGINNAME_CONDITION,
						new String[] {
								IAUserExtension.getUserLoginEnterpriseId(
										_mLoginUser).toString(),
								_mLoginUser.getName() }, null);
		if (null != _cursor) {
			while (_cursor.moveToNext()) {
				// get my advice advisor
				_mMyAdviceAdvisor = new PersonBean(_cursor);

				// break immediately
				break;
			}

			// close cursor
			_cursor.close();
		}

		// set subViews
		// set title
		setTitle(_todoTaskTitle);

		// define to-do list task submit bar button item
		IApproveImageBarButtonItem _todoTaskSubmitBarBtnItem;

		// set to-do list task submit bar button item as right bar button item
		setRightBarButtonItem(_todoTaskSubmitBarBtnItem = new IApproveImageBarButtonItem(
				this, R.drawable.img_naa6tdta_submitbarbtnitem,
				new TodoTaskApproveSubmitBarBtnItemOnClickListener()));

		// bind to-do list task submit bar button item on long click listener
		_todoTaskSubmitBarBtnItem
				.setOnLongClickListener(new TodoTaskApproveSubmitBarBtnItemOnLongClickListener());

		// initialize to-do list task form item id and form item form item view
		// map
		_mFormItemId7FormItemFormItemMap = new HashMap<Long, TaskFormItemFormItem>();

		// get to-do list task form item form linearLayout
		_mFormItemFormLinearLayout = (LinearLayout) findViewById(R.id.tdta_formItemForm_linearLayout);

		// refresh to-do list task form item form
		refreshFormItemForm();

		// initialize to-do list task form attachment id and form attachment
		// form item view map
		_mFormAttachmentId7FormAttachmentFormItemMap = new HashMap<Long, TaskFormAttachmentFormItem>();

		// get to-do list task form attachment form parent frameLayout and form
		// linearLayout
		_mAttachmentFormParentFrameLayout = (FrameLayout) findViewById(R.id.tdta_attachmentForm_parent_frameLayout);
		_mAttachmentFormLinearLayout = (LinearLayout) findViewById(R.id.tdta_attachmentForm_linearLayout);

		// refresh to-do list task form attachment form
		refreshFormAttachmentForm();

		// get to-do list task form advice form parent frameLayout and form
		// linearLayout
		_mAdviceFormParentFrameLayout = (FrameLayout) findViewById(R.id.tdta_adviceForm_parent_frameLayout);
		_mAdviceFormLinearLayout = (LinearLayout) findViewById(R.id.tdta_adviceForm_linearLayout);

		// check to-do list task advices, then generate to-do list task each
		// others advice and add to form advice form linearLayout
		for (IApproveTaskAdviceBean todoTaskAdvice : _todoTaskAdvices) {
			// test by ares
			PersonBean _othersAdvisor = new PersonBean();
			_othersAdvisor.setEmployeeName(todoTaskAdvice.getAdvisorName());

			addTodoTaskFormAdviceFormItem(TaskFormAdviceType.OTHERS_ADVICE,
					_othersAdvisor, todoTaskAdvice);
		}

		// bind add submit contact button on click listener
		((ImageButton) findViewById(R.id.tdta_add_submitContact_imageButton))
				.setOnClickListener(new AddSubmitContactImgBtnOnClickListener());

		// get to-do list task submit contact list sliding drawer and its
		// listView
		_mSubmitContactListSlidingDrawer = (SlidingDrawer) findViewById(R.id.tdta_submitContactList_slidingDrawer);
		_mSubmitContactListView = (ListView) findViewById(R.id.tdta_submitContact_listView);

		// bind cancel and done select to-do list task approve submit contacts
		// imageButton, button on click listener
		((ImageButton) findViewById(R.id.tdta_cancelSelect_submitContacts_imageButton))
				.setOnClickListener(new CancelSelectTodoTaskApproveSubmitContactsImageButtonOnClickListener());
		((Button) findViewById(R.id.tdta_doneSelect_submitContacts_button))
				.setOnClickListener(new DoneSelectTodoTaskApproveSubmitContactsButtonOnClickListener());

		// set to-do list task approve submit contact list cursor adapter
		_mSubmitContactListView
				.setAdapter(new SimpleCursorAdapter(
						this,
						android.R.layout.simple_list_item_multiple_choice,
						getContentResolver()
								.query(ContentUris
										.withAppendedId(
												Employee.ENTERPRISE_CONTENT_URI,
												IAUserExtension
														.getUserLoginEnterpriseId(_mLoginUser)),
										null, null, null, null),
						new String[] { Employee.NAME },
						new int[] { android.R.id.text1 },
						CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));

		// set its choice mode
		_mSubmitContactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// get advice input editText
		_mAdviceInputEditText = (EditText) findViewById(R.id.tdta_advice_editText);

		// set advice input editText text changed listener
		_mAdviceInputEditText
				.addTextChangedListener(new TodoTaskApproveAdviceInputEditTextWatcher());

		// bind advice send button on click listener
		((Button) findViewById(R.id.tdta_advice_send_button))
				.setOnClickListener(new TodoTaskApproveAdviceSendBtnOnClickListener());
	}

	@Override
	protected void onResume() {
		super.onResume();

		// bind core service
		boolean _result = getApplicationContext().bindService(
				new Intent(this, CoreService.class),
				_mCoreServiceConnection = new CoreServiceConnection(),
				Context.BIND_AUTO_CREATE);

		Log.d(LOG_TAG, "Bind core service complete, the result = " + _result
				+ " when on resume");
	}

	@Override
	protected void onPause() {
		super.onPause();

		// stop play recorder audio if needed
		AudioUtils.stopPlayRecorderAudio();
	}

	@Override
	public void onBackPressed() {
		// check submit contact list sliding drawer is opened and then hide it
		if (_mSubmitContactListSlidingDrawer.isOpened()) {
			// show navigation bar
			((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
					.setVisibility(View.VISIBLE);

			// close submit contact list sliding drawer
			_mSubmitContactListSlidingDrawer.animateClose();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// unbind core service
		getApplicationContext().unbindService(_mCoreServiceConnection);
	}

	// refresh enterprise form item form
	private void refreshFormItemForm() {
		// query the to-do list task form all items
		Cursor _cursor = getContentResolver()
				.query(TodoTaskFormItem.TODOTASKFORMITEMS_CONTENT_URI,
						null,
						TodoTaskFormItem.USER_ENTERPRISETODOLISTTASK_FORMITEMS_WITHSENDERFAKEID7LOGINNAME_CONDITION,
						new String[] {
								IAUserExtension.getUserLoginEnterpriseId(
										_mLoginUser).toString(),
								_mTodoTaskSenderFakeId.toString(),
								_mLoginUser.getName() }, null);

		// check the cursor
		if (null != _cursor) {
			while (_cursor.moveToNext()) {
				// get to-do list task form item
				IApproveTaskFormItemBean _todoTaskFormItem = IApproveTaskFormItemBean
						.getTaskFormItem(_cursor);

				// define to-do list task form item form item
				TaskFormItemFormItem _todoTaskFormItemFormItem = null;

				// check form item id existed in to-do list task form item id
				// and form item form item view map key set
				if (!_mFormItemId7FormItemFormItemMap.keySet().contains(
						_todoTaskFormItem.getItemId())) {
					// generate new to-do list task form item form item
					_todoTaskFormItemFormItem = TaskFormItemFormItem
							.generateTaskFormItemFormItem(_todoTaskFormItem);

					// add separator line and to-do list task form item form
					// item to form item form
					_mFormItemFormLinearLayout.addView(new CommonFormSeparator(
							this), new LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					_mFormItemFormLinearLayout.addView(
							_todoTaskFormItemFormItem, new LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT, 1));
				} else {
					// get the existed to-do list task form item form item and
					// set its form item
					(_todoTaskFormItemFormItem = _mFormItemId7FormItemFormItemMap
							.get(_todoTaskFormItem.getItemId()))
							.setFormItem(_todoTaskFormItem);
				}

				// add to-do list task form item form item to map
				_mFormItemId7FormItemFormItemMap.put(
						_todoTaskFormItem.getItemId(),
						_todoTaskFormItemFormItem);
			}

			// close cursor
			_cursor.close();
		}
	}

	// refresh enterprise form attachment form
	@SuppressWarnings("unchecked")
	private void refreshFormAttachmentForm() {
		// query the to-do list task form attachments
		Cursor _cursor = getContentResolver()
				.query(TodoTaskAttachment.TODOTASKATTACHMENTS_CONTENT_URI,
						null,
						TodoTaskAttachment.USER_ENTERPRISETODOLISTTASK_ATTACHMENTS_WITHSENDERFAKEID7LOGINNAME_CONDITION,
						new String[] {
								IAUserExtension.getUserLoginEnterpriseId(
										_mLoginUser).toString(),
								_mTodoTaskSenderFakeId.toString(),
								_mLoginUser.getName() }, null);

		// check the cursor
		if (null != _cursor) {
			while (_cursor.moveToNext()) {
				// get to-do list task attachment
				IApproveTaskAttachmentBean _todoTaskAttachment = IApproveTaskAttachmentBean
						.getTaskAttachment(_cursor);

				// define to-do list task form attachment form item
				TaskFormAttachmentFormItem _todoTaskFormAttachmentFormItem = null;

				// check attachment id existed in to-do list task attachment id
				// and form attachment form item view map key set
				if (!_mFormAttachmentId7FormAttachmentFormItemMap.keySet()
						.contains(_todoTaskAttachment.getAttachmentId())) {
					// define task form attachment info and on click listener
					Object _attachmentInfo = null;
					OnClickListener _attachmentOnClickListener = null;

					// get, check task form attachment type then initialize task
					// form attachment info and on click listener
					TaskFormAttachmentType _taskFormAttachmentType = TaskFormAttachmentType
							.getType(_todoTaskAttachment.getAttachmentType());
					switch (_taskFormAttachmentType) {
					case TEXT_ATTACHMENT:
						// text
						_attachmentInfo = _todoTaskAttachment
								.getAttachmentUrl();
						_attachmentOnClickListener = new TodoTaskFormTextAttachmentFormItemOnClickListener();
						break;

					case IMAGE_ATTACHMENT:
						// image
						// check image attachment url
						if (null != _todoTaskAttachment.getAttachmentUrl()) {
							_attachmentInfo = BitmapFactory
									.decodeFile(_todoTaskAttachment
											.getAttachmentUrl());
							_attachmentOnClickListener = new TodoTaskFormImageAttachmentFormItemOnClickListener();
						}
						break;

					case VOICE_ATTACHMENT:
						// voice
						// check voice attachment url
						if (null != _todoTaskAttachment.getAttachmentUrl()) {
							_attachmentInfo = new HashMap<String, Object>();
							((Map<String, Object>) _attachmentInfo)
									.put(TaskFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH,
											_todoTaskAttachment
													.getAttachmentUrl());
							((Map<String, Object>) _attachmentInfo)
									.put(TaskFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_DURATION,
											AudioUtils
													.getRecorderAudioDuration(_todoTaskAttachment
															.getAttachmentUrl()));
							_attachmentOnClickListener = new TodoTaskFormVoiceAttachmentFormItemOnClickListener();
						}
						break;

					case APPLICATION_ATTACHMENT:
						// nothing to do
						break;
					}

					// check attachment info
					if (null != _attachmentInfo) {
						// check to-do list task form attachment form parent
						// frameLayout visibility
						if (View.VISIBLE != _mAttachmentFormParentFrameLayout
								.getVisibility()) {
							// show to-do list task form attachment form parent
							// frameLayout
							_mAttachmentFormParentFrameLayout
									.setVisibility(View.VISIBLE);
						}

						// generate new to-do list task form attachment form
						// item
						_todoTaskFormAttachmentFormItem = TaskFormAttachmentFormItem
								.generateTaskFormAttachmentFormItem(
										_taskFormAttachmentType,
										_attachmentInfo);

						// check attachment on click listener and set new to-do
						// list task form attachment form item on click listener
						if (null != _attachmentOnClickListener) {
							_todoTaskFormAttachmentFormItem
									.setOnClickListener(_attachmentOnClickListener);
						}

						// add to-do list task form attachment form item to form
						// attachment form
						_mAttachmentFormLinearLayout.addView(
								_todoTaskFormAttachmentFormItem,
								new LayoutParams(LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1));

						// add to-do list task form attachment form item to map
						_mFormAttachmentId7FormAttachmentFormItemMap.put(
								_todoTaskAttachment.getAttachmentId(),
								_todoTaskFormAttachmentFormItem);
					}
				} else {
					// // get the existed to-do list task form attachment form
					// item and set its form attachment type and info
					// (_todoTaskFormAttachmentFormItem =
					// _mFormAttachmentId7FormAttachmentFormItemMap
					// .get(_todoTaskAttachment.getAttachmentId()))
					// .;

					// add to-do list task form attachment form item to map
					_mFormAttachmentId7FormAttachmentFormItemMap.put(
							_todoTaskAttachment.getAttachmentId(),
							_todoTaskFormAttachmentFormItem);
				}
			}

			// close cursor
			_cursor.close();
		}
	}

	// add to-do list task advice(my and others) as advice form item to advice
	// form linearLayout
	private void addTodoTaskFormAdviceFormItem(TaskFormAdviceType adviceType,
			PersonBean advisorInfo, IApproveTaskAdviceBean adviceInfo) {
		// check to-do list task form advice form parent frameLayout
		// visibility
		if (View.VISIBLE != _mAdviceFormParentFrameLayout.getVisibility()) {
			// show to-do list task form advice form parent frameLayout
			_mAdviceFormParentFrameLayout.setVisibility(View.VISIBLE);
		}

		// generate new added my advice form item
		TaskFormAdviceFormItem _newAddedMyAdviceFormItem = TaskFormAdviceFormItem
				.generateTaskFormAdviceFormItem(adviceType, advisorInfo,
						adviceInfo);

		// set its on click listener
		_newAddedMyAdviceFormItem
				.setOnClickListener(new TodoTaskFormAdviceFormItemOnClickListener());

		// check advice type and set its on long click listener
		if (null != adviceType && TaskFormAdviceType.MY_ADVICE == adviceType) {
			_newAddedMyAdviceFormItem
					.setOnLongClickListener(new TodoTaskFormAdviceFormItemOnLongClickListener());
		}

		// add new added my advice to advice form linearLayout
		_mAdviceFormLinearLayout.addView(_newAddedMyAdviceFormItem,
				new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1));
	}

	// inner class
	// to-do list task approve extra data constant
	public static final class TodoTaskApproveExtraData {

		// to-do list task title and sender fake id
		public static final String TODOTASK_APPROVE_TASKTITLE = "todo_task_approve_tasktitle";
		public static final String TODOTASK_APPROVE_TASKSENDERFAKEID = "todo_task_approve_tasksenderfakeid";
		public static final String TODOTASK_APPROVE_TASKADVICES = "todo_task_approve_taskadvices";

	}

	// to-do list task approve submit bar button item on click listener
	class TodoTaskApproveSubmitBarBtnItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Click submit the to-do list task approve");

			//
		}

	}

	// to-do list task approve submit bar button item on long click listener
	class TodoTaskApproveSubmitBarBtnItemOnLongClickListener implements
			OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			Log.d(LOG_TAG, "Long click end the to-do list task approve");

			//

			return true;
		}

	}

	// to-do list task form text attachment form item on click listener
	class TodoTaskFormTextAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG,
					"Form text attachment form item on click listener, view = "
							+ v);

			//
		}

	}

	// to-do list task form image attachment form item on click listener
	class TodoTaskFormImageAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG,
					"Form image attachment form item on click listener, view = "
							+ v);

			//
		}

	}

	// to-do list task form voice attachment form item on click listener
	class TodoTaskFormVoiceAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// get voice playing flag
			if ((Boolean) v.getTag()) {
				Log.d(LOG_TAG, "Play the voice");

				// play the voice
				AudioUtils
						.playRecorderAudio((String) v
								.getTag(TaskFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH
										.hashCode()));
			} else {
				Log.d(LOG_TAG, "Stop play the voice");

				// stop play the voice
				AudioUtils.stopPlayRecorderAudio();
			}
		}

	}

	// to-do list task form advice form item on click listener
	class TodoTaskFormAdviceFormItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG,
					"To-do list task Form advice form item on click listener, view = "
							+ v);

			//
		}

	}

	// to-do list task form advice form item on long click listener
	class TodoTaskFormAdviceFormItemOnLongClickListener implements
			OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			// remove the click form advice form item from advice form
			// linearLayout
			_mAdviceFormLinearLayout.removeView(v);

			// get and check form advice form linearLayout subviews count
			if (1 == _mAdviceFormLinearLayout.getChildCount()) {
				// hide to-do list task form advice form parent frameLayout
				_mAdviceFormParentFrameLayout.setVisibility(View.GONE);
			}

			return true;
		}

	}

	// add submit contact image button on click listener
	class AddSubmitContactImgBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Click add submit contact");

			//

			// open submit contact list sliding drawer
			_mSubmitContactListSlidingDrawer.animateOpen();

			// delayed 250 milliseconds to hide navigation bar
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
							.setVisibility(View.GONE);
				}

			}, 250);
		}

	}

	// core service connection
	class CoreServiceConnection implements ServiceConnection {

		private final String LOG_TAG = CoreServiceConnection.class
				.getCanonicalName();

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			// get core service
			CoreService _coreService = ((LocalBinder) binder).getService();

			Log.d(LOG_TAG, "The core service = " + _coreService
					+ " is connected, component name = " + name
					+ " and the binder = " + binder);

			// start get user login enterprise address book
			_coreService.startGetEnterpriseAddressbook();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(LOG_TAG,
					"The core service is disconnected and the component name = "
							+ name);

			// nothing to do
		}

	}

	// cancel select to-do list task approve submit contacts imageButton on
	// click listener
	class CancelSelectTodoTaskApproveSubmitContactsImageButtonOnClickListener
			implements OnClickListener {

		@Override
		public void onClick(View v) {
			// show navigation bar
			((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
					.setVisibility(View.VISIBLE);

			// close submit contact list sliding drawer
			_mSubmitContactListSlidingDrawer.animateClose();
		}

	}

	// done select to-do list task approve submit contacts button on click
	// listener
	class DoneSelectTodoTaskApproveSubmitContactsButtonOnClickListener
			implements OnClickListener {

		@Override
		public void onClick(View v) {
			// done select to-do list task approve submit contacts
			//

			// show navigation bar
			((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
					.setVisibility(View.VISIBLE);

			// close submit contact list sliding drawer
			_mSubmitContactListSlidingDrawer.animateClose();
		}

	}

	// to-do list task approve advice input editText text watcher
	class TodoTaskApproveAdviceInputEditTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// get advice send button
			Button _adviceSendButton = (Button) findViewById(R.id.tdta_advice_send_button);

			// check editable and then enable or disable advice send button
			if (null == s || "".equalsIgnoreCase(s.toString())) {
				_adviceSendButton.setEnabled(false);
			} else {
				_adviceSendButton.setEnabled(true);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// nothing to do
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// nothing to do
		}

	}

	// to-do list task approve advice send button on click listener
	class TodoTaskApproveAdviceSendBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// get the ready to send advice and set as advice, then add to form
			// advice form linearLayout
			// define my advice bean and set its attributes
			IApproveTaskAdviceBean _myAdvice = new IApproveTaskAdviceBean();
			_myAdvice.setAdvice(_mAdviceInputEditText.getText().toString());
			_myAdvice.setAdviceGivenTimestamp(System.currentTimeMillis());

			// add my advice to form advice form linearLayout
			addTodoTaskFormAdviceFormItem(TaskFormAdviceType.MY_ADVICE,
					_mMyAdviceAdvisor, _myAdvice);

			// clear advice input editText text
			_mAdviceInputEditText.setText("");
		}

	}

}
