package com.futuo.iapprove.tab7tabcontent.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.addressbook.person.PersonBean;
import com.futuo.iapprove.customwidget.CommonFormSeparator;
import com.futuo.iapprove.customwidget.IApproveImageBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.customwidget.SubmitContact;
import com.futuo.iapprove.customwidget.TaskFormAdviceFormItem;
import com.futuo.iapprove.customwidget.TaskFormAdviceFormItem.TaskFormAdviceType;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormAttachmentType;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormVoiceAttachmentInfoDataKeys;
import com.futuo.iapprove.customwidget.TaskFormItemFormItem;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.provider.UserEnterpriseProfileContentProvider.EnterpriseProfiles.EnterpriseProfile;
import com.futuo.iapprove.provider.UserEnterpriseTaskApprovingContentProvider.ApprovingTodoTasks.ApprovingTodoTask;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTaskAttachments.TodoTaskAttachment;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTaskFormItems.TodoTaskFormItem;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTasks.TodoTask;
import com.futuo.iapprove.service.CoreService;
import com.futuo.iapprove.service.CoreService.LocalBinder;
import com.futuo.iapprove.task.IApproveTaskAdviceBean;
import com.futuo.iapprove.task.IApproveTaskAttachmentBean;
import com.futuo.iapprove.task.IApproveTaskFormItemBean;
import com.futuo.iapprove.task.TodoTaskStatus;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.futuo.iapprove.utils.AudioUtils;
import com.futuo.iapprove.utils.CalculateStringUtils;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;

@SuppressWarnings("deprecation")
public class TodoTaskApproveActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = TodoTaskApproveActivity.class
			.getCanonicalName();

	// user enterprise to-do list task id, sender fake id and status
	private Long _mTodoTaskId;
	private Long _mTodoTaskSenderFakeId;
	private TodoTaskStatus _mTodoTaskStatus;

	// to-do list task form item form parent frameLayout and form linearLayout
	private FrameLayout _mFormItemFormParentFrameLayout;
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

	// to-do task submit contact list
	private List<ABContactBean> _mSubmitContactList;

	// to-do task selected submit contacts gridLayout
	private GridLayout _mSelectedSubmitContactsGridLayout;

	// core service connection
	private CoreServiceConnection _mCoreServiceConnection;

	// core service
	private CoreService _mCoreService;

	// to-do list task submit contact list sliding drawer and its listView
	private SlidingDrawer _mSubmitContactListSlidingDrawer;
	private ListView _mSubmitContactListView;

	// advice input editText
	private EditText _mAdviceInputEditText;

	// my advice advisor
	private PersonBean _mMyAdviceAdvisor;

	// my advice judge
	private Boolean _mMyAdviceJudge;

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

		// initialize to-do task submit contact list
		_mSubmitContactList = new ArrayList<ABContactBean>();

		// check the data
		if (null != _extraData) {
			// get to-do list task id, title, sender fake id, status and advice
			// list
			_mTodoTaskId = _extraData
					.getLong(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKID);
			_todoTaskTitle = _extraData
					.getString(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKTITLE);
			_mTodoTaskSenderFakeId = _extraData
					.getLong(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKSENDERFAKEID);
			_mTodoTaskStatus = (TodoTaskStatus) _extraData
					.getSerializable(TodoTaskApproveExtraData.TODOTASK_APPROVE_TASKSTATUS);
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

		// get to-do list task form item form parent frameLayout and form
		// linearLayout
		_mFormItemFormParentFrameLayout = (FrameLayout) findViewById(R.id.tdta_formItemForm_parent_frameLayout);
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
				.setAdapter(new SubmitContactListCursorAdapter());

		// set its choice mode
		_mSubmitContactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// set my advice judge default value
		_mMyAdviceJudge = false;

		// set my advice switch toggle button on checked change listener
		((ToggleButton) findViewById(R.id.tdta_adviceSwitch_toggleButton))
				.setOnCheckedChangeListener(new TodoTaskApproveAdviceSwitchToggleBtnOnCheckedChangeListener());

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

			// clear choices
			_mSubmitContactListView.clearChoices();

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

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		AppDataSaveRestoreUtils.onRestoreInstanceState(savedInstanceState);

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtils.onSaveInstanceState(outState);

		super.onSaveInstanceState(outState);
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
					// check to-do list task form item form parent
					// frameLayout visibility
					if (View.VISIBLE != _mFormItemFormParentFrameLayout
							.getVisibility()) {
						// show to-do list task form item form parent
						// frameLayout
						_mFormItemFormParentFrameLayout
								.setVisibility(View.VISIBLE);
					}

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

					// need capital
					if (_todoTaskFormItem.itemNeedCapital()) {
						// clone to-do list task form item copy
						IApproveTaskFormItemBean _todoTaskFormItemCopy = new IApproveTaskFormItemBean();
						_todoTaskFormItemCopy
								.setItemName(String
										.format(getResources()
												.getString(
														R.string.task_formItem_infoLabel_capitalFormat),
												_todoTaskFormItem.getItemName()));
						_todoTaskFormItemCopy.setItemInfo(CalculateStringUtils
								.calculateCapital(_todoTaskFormItem
										.getItemInfo()));

						// generate new to-do list task capital form item form
						// item
						TaskFormItemFormItem _todoTaskFormItemCapitalFormItem = TaskFormItemFormItem
								.generateTaskFormItemFormItem(_todoTaskFormItemCopy);

						// add separator line and to-do list task form item form
						// item to form item form
						_mFormItemFormLinearLayout.addView(
								new CommonFormSeparator(this),
								new LayoutParams(LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT));
						_mFormItemFormLinearLayout.addView(
								_todoTaskFormItemCapitalFormItem,
								new LayoutParams(LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1));
					}
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
						Log.d(LOG_TAG,
								"@@@, _todoTaskAttachment.getAttachmentUrl() = "
										+ _todoTaskAttachment
												.getAttachmentUrl()
										+ ", origin name = "
										+ _todoTaskAttachment
												.getAttachmentOriginName()
										+ " and suffix = "
										+ _todoTaskAttachment
												.getAttachmentSuffix());
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

	// refresh to-do task submit contacts
	private void refreshSubmitContacts() {
		// check and initialize selected submit contacts gridLayout
		if (null == _mSelectedSubmitContactsGridLayout) {
			// get to-do task selected submit contacts gridLayout
			_mSelectedSubmitContactsGridLayout = (GridLayout) findViewById(R.id.tdta_selectedSubmitContacts_gridLayout);
		}

		// check submit contact list again
		if (null != _mSubmitContactList && 0 < _mSubmitContactList.size()) {
			// show selected submit contacts gridLayout if needed
			if (View.VISIBLE != _mSelectedSubmitContactsGridLayout
					.getVisibility()) {
				_mSelectedSubmitContactsGridLayout.setVisibility(View.VISIBLE);
			}

			// define and initialize to-do task submit contact list user id list
			List<Long> _tdtaSubmitContactListUserIdList = new ArrayList<Long>();
			for (ABContactBean _submitContact : _mSubmitContactList) {
				_tdtaSubmitContactListUserIdList
						.add(_submitContact.getUserId());
			}

			// traversal to-do task submit contacts gridLayout all subviews
			int _submitContactGridLayoutSubviewIndex = 0;
			while (_submitContactGridLayoutSubviewIndex < _mSelectedSubmitContactsGridLayout
					.getChildCount()) {
				// get to-do task submit contacts gridLayout subview submit
				// contact user id
				Long _tdtaSubmitContactsGridLayoutSubviewSubmitContactUserId = ((SubmitContact) _mSelectedSubmitContactsGridLayout
						.getChildAt(_submitContactGridLayoutSubviewIndex))
						.getSubmitContact().getUserId();
				if (_tdtaSubmitContactListUserIdList
						.contains(_tdtaSubmitContactsGridLayoutSubviewSubmitContactUserId)) {
					// set the to-do task submit contacts gridLayout subview
					// submit contact which contains in submit contact list for
					// delete
					_mSubmitContactList
							.get(_tdtaSubmitContactListUserIdList
									.indexOf(_tdtaSubmitContactsGridLayoutSubviewSubmitContactUserId))
							.setLocalStorageDataDirtyType(
									LocalStorageDataDirtyType.DELETE);

					// next subview
					_submitContactGridLayoutSubviewIndex++;
				} else {
					// remove the selected submit contact from submit contacts
					// gridLayout
					_mSelectedSubmitContactsGridLayout
							.removeViewAt(_submitContactGridLayoutSubviewIndex);
				}
			}

			// traversal to-do task submit contact list, generate the submit
			// contact and add to submit contacts gridLayout
			for (ABContactBean _submitContact : _mSubmitContactList) {
				// check submit contact dirty type
				if (LocalStorageDataDirtyType.DELETE != _submitContact
						.getLocalStorageDataDirtyType()) {
					// generate new added submit contact
					SubmitContact _newAddedSubmitContact = SubmitContact
							.generateNAA6TodoTaskSubmitContact(_submitContact);

					// set its on long click listener
					_newAddedSubmitContact
							.setOnLongClickListener(new SubmitContactOnLongClickListener());

					// add to submit contacts gridLayout
					_mSelectedSubmitContactsGridLayout
							.addView(_newAddedSubmitContact);
				}
			}
		} else {
			// remove selected submit contacts gridLayout all subviews and then
			// hide selected submit contacts gridLayout
			_mSelectedSubmitContactsGridLayout.removeAllViews();
			_mSelectedSubmitContactsGridLayout.setVisibility(View.GONE);
		}
	}

	// inner class
	// to-do list task approve extra data constant
	public static final class TodoTaskApproveExtraData {

		// to-do list task id, title, sender fake id, status and advice
		public static final String TODOTASK_APPROVE_TASKID = "todo_task_approve_taskid";
		public static final String TODOTASK_APPROVE_TASKTITLE = "todo_task_approve_tasktitle";
		public static final String TODOTASK_APPROVE_TASKSENDERFAKEID = "todo_task_approve_tasksenderfakeid";
		public static final String TODOTASK_APPROVE_TASKSTATUS = "todo_task_approve_taskstatus";
		public static final String TODOTASK_APPROVE_TASKADVICES = "todo_task_approve_taskadvices";

	}

	// to-do list task approve submit bar button item on click listener
	class TodoTaskApproveSubmitBarBtnItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// check submit contact list
			if (null != _mSubmitContactList && 0 < _mSubmitContactList.size()) {
				// get my advice
				// define my advice string builder
				StringBuilder _myAdviceStringBuilder = new StringBuilder();

				// get and check advice form linearLayout subviews count
				int _adviceFormLinearLayoutSubviewsCount = _mAdviceFormLinearLayout
						.getChildCount();
				if (1 < _adviceFormLinearLayoutSubviewsCount) {
					// traversal advice form linearLayout all subviews
					for (int i = 1; i < _adviceFormLinearLayoutSubviewsCount; i++) {
						// get to-do task advice form item
						TaskFormAdviceFormItem _todoTaskAdviceFormItem = (TaskFormAdviceFormItem) _mAdviceFormLinearLayout
								.getChildAt(i);

						// check to-do task advice type and get my advice
						if (TaskFormAdviceType.MY_ADVICE == _todoTaskAdviceFormItem
								.getAdviceType()) {
							// add my advice and separate character to my advice
							// string builder
							_myAdviceStringBuilder
									.append(_todoTaskAdviceFormItem
											.getAdviceInfo());
							if (_adviceFormLinearLayoutSubviewsCount - 1 != i) {
								_myAdviceStringBuilder.append("\n");
							}
						}
					}
				}

				// define and initialize submit contacts name string builder
				StringBuilder _submitContactsNameStringBuilder = new StringBuilder();
				for (ABContactBean _submitContact : _mSubmitContactList) {
					_submitContactsNameStringBuilder.append(_submitContact
							.getApproveNumber());
					if (_mSubmitContactList.size() - 1 != _mSubmitContactList
							.indexOf(_submitContact)) {
						_submitContactsNameStringBuilder.append(',');
					}
				}

				// hide the to-do task local storage
				// define and initialize the update content values
				ContentValues _updateContentValues = new ContentValues();
				_updateContentValues.put(TodoTask.TASK_STATUS,
						TodoTask.HIDDEN_STATUS.toString());

				// update user enterprise to-do list task local storage
				getContentResolver()
						.update(ContentUris.withAppendedId(
								TodoTask.ENTERPRISE_CONTENT_URI,
								IAUserExtension
										.getUserLoginEnterpriseId(_mLoginUser)),
								_updateContentValues,
								TodoTask.APPROVE_USER_ENTERPRISETODOLISTTASK_WITHSENDERFAKEID_CONDITION,
								new String[] { _mTodoTaskSenderFakeId
										.toString() });

				// insert the to-do task for approving to local storage
				// define and initialize the to-do task approving for inserting
				// content values
				ContentValues _insertContentValues = new ContentValues();
				_insertContentValues.put(ApprovingTodoTask.TASK_ID,
						_mTodoTaskId.toString());
				_insertContentValues.put(ApprovingTodoTask.ENTERPRISE_ID,
						IAUserExtension.getUserLoginEnterpriseId(_mLoginUser));
				_insertContentValues.put(ApprovingTodoTask.APPROVE_NUMBER,
						_mLoginUser.getName());
				_insertContentValues.put(ApprovingTodoTask.SUBMITCONTACTS,
						_submitContactsNameStringBuilder.toString());
				_insertContentValues
						.put(ApprovingTodoTask.JUDGE,
								_mMyAdviceJudge ? getResources()
										.getString(
												R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_myAgreedJudge)
										: getResources()
												.getString(
														R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_myDisAgreedJudge));
				_insertContentValues.put(ApprovingTodoTask.ADVICE_INFO,
						_myAdviceStringBuilder.toString());
				_insertContentValues.put(ApprovingTodoTask.SENDER_FAKEID,
						_mTodoTaskSenderFakeId.toString());
				_insertContentValues.put(ApprovingTodoTask.TASK_STATUS,
						_mTodoTaskStatus.getValue());
				_insertContentValues.put(ApprovingTodoTask.TASK_OPERATESTATE,
						ApprovingTodoTask.TASK_OPERATESTATE_NOTENDED);

				// insert the to-do task for approving to local storage
				getContentResolver().insert(
						ApprovingTodoTask.APPROVINGTODOTASKS_CONTENT_URI,
						_insertContentValues);

				// popup to-do task approve activity
				popActivity();
			} else {
				Log.d(LOG_TAG, "Please select at least one submit contact");

				// show select at least one submit contact toast
				Toast.makeText(TodoTaskApproveActivity.this,
						R.string.toast_select_submitContact, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	// to-do list task approve submit bar button item on long click listener
	class TodoTaskApproveSubmitBarBtnItemOnLongClickListener implements
			OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			// check to-do task status
			if (TodoTaskStatus.ENDED != _mTodoTaskStatus) {
				// get my advice
				// define my advice string builder
				StringBuilder _myAdviceStringBuilder = new StringBuilder();

				// get and check advice form linearLayout subviews count
				int _adviceFormLinearLayoutSubviewsCount = _mAdviceFormLinearLayout
						.getChildCount();
				if (1 < _adviceFormLinearLayoutSubviewsCount) {
					// traversal advice form linearLayout all subviews
					for (int i = 1; i < _adviceFormLinearLayoutSubviewsCount; i++) {
						// get to-do task advice form item
						TaskFormAdviceFormItem _todoTaskAdviceFormItem = (TaskFormAdviceFormItem) _mAdviceFormLinearLayout
								.getChildAt(i);

						// check to-do task advice type and get my advice
						if (TaskFormAdviceType.MY_ADVICE == _todoTaskAdviceFormItem
								.getAdviceType()) {
							// add my advice and separate character to my
							// advice string builder
							_myAdviceStringBuilder
									.append(_todoTaskAdviceFormItem
											.getAdviceInfo());
							if (_adviceFormLinearLayoutSubviewsCount - 1 != i) {
								_myAdviceStringBuilder.append("\n");
							}
						}
					}
				}

				// hide the to-do task local storage
				// define and initialize the update content values
				ContentValues _updateContentValues = new ContentValues();
				_updateContentValues.put(TodoTask.TASK_STATUS,
						TodoTask.HIDDEN_STATUS.toString());

				// update user enterprise to-do list task local storage
				getContentResolver()
						.update(ContentUris.withAppendedId(
								TodoTask.ENTERPRISE_CONTENT_URI,
								IAUserExtension
										.getUserLoginEnterpriseId(_mLoginUser)),
								_updateContentValues,
								TodoTask.APPROVE_USER_ENTERPRISETODOLISTTASK_WITHSENDERFAKEID_CONDITION,
								new String[] { _mTodoTaskSenderFakeId
										.toString() });

				// insert the to-do task for approving to local storage
				// define and initialize the to-do task approving for
				// inserting content values
				ContentValues _insertContentValues = new ContentValues();
				_insertContentValues.put(ApprovingTodoTask.TASK_ID,
						_mTodoTaskId.toString());
				_insertContentValues.put(ApprovingTodoTask.ENTERPRISE_ID,
						IAUserExtension.getUserLoginEnterpriseId(_mLoginUser));
				_insertContentValues.put(ApprovingTodoTask.APPROVE_NUMBER,
						_mLoginUser.getName());
				_insertContentValues
						.put(ApprovingTodoTask.JUDGE,
								_mMyAdviceJudge ? getResources()
										.getString(
												R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_myAgreedJudge)
										: getResources()
												.getString(
														R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_myDisAgreedJudge));
				_insertContentValues.put(ApprovingTodoTask.ADVICE_INFO,
						_myAdviceStringBuilder.toString());
				_insertContentValues.put(ApprovingTodoTask.SENDER_FAKEID,
						_mTodoTaskSenderFakeId.toString());
				_insertContentValues.put(ApprovingTodoTask.TASK_STATUS,
						_mTodoTaskStatus.getValue());
				_insertContentValues.put(ApprovingTodoTask.TASK_OPERATESTATE,
						ApprovingTodoTask.TASK_OPERATESTATE_ENDED);

				// insert the to-do task for approving to local storage
				getContentResolver().insert(
						ApprovingTodoTask.APPROVINGTODOTASKS_CONTENT_URI,
						_insertContentValues);

				// popup to-do task approve activity
				popActivity();
			}

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

	// submit contact on long click listener
	class SubmitContactOnLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			// remove the selected submit contact from submit contacts
			// gridLayout
			_mSelectedSubmitContactsGridLayout.removeView(v);

			// check submit contacts gridLayout subviews count and hide it if
			// needed
			if (0 == _mSelectedSubmitContactsGridLayout.getChildCount()) {
				_mSelectedSubmitContactsGridLayout.setVisibility(View.GONE);
			}

			// remove the selected submit contact's submit contact object from
			// submit contact list
			for (ABContactBean _submitContact : _mSubmitContactList) {
				// check the selected submit contact user id and delete the
				// selected submit contact from submit contact list
				if (_submitContact.getUserId().longValue() == ((SubmitContact) v)
						.getSubmitContact().getUserId().longValue()) {
					_mSubmitContactList.remove(_submitContact);

					// break immediately
					break;
				}
			}

			return false;
		}

	}

	// add submit contact image button on click listener
	class AddSubmitContactImgBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// check submit contact list
			if (null != _mSubmitContactList && 0 < _mSubmitContactList.size()) {
				// define to-do task submit contact list user id list
				List<Long> _tdtaSubmitContactListUserIdList = new ArrayList<Long>();

				// get to-do task submit contact list cursor and move to first
				Cursor _tdtaSubmitContactListCursor = ((SubmitContactListCursorAdapter) _mSubmitContactListView
						.getAdapter()).getCursor();
				_tdtaSubmitContactListCursor.moveToFirst();

				// traversal to-do task submit contacts and add user id to list
				do {
					_tdtaSubmitContactListUserIdList.add(new ABContactBean(
							_tdtaSubmitContactListCursor).getUserId());
				} while (_tdtaSubmitContactListCursor.moveToNext());

				// traversal to-do task submit contact list
				for (ABContactBean _submitContact : _mSubmitContactList) {
					// get, check to-do task submit contact user id and set the
					// submit contact item checked
					Long _submitContactUserId = _submitContact.getUserId();
					if (_tdtaSubmitContactListUserIdList
							.contains(_submitContactUserId)) {
						_mSubmitContactListView.setItemChecked(
								_tdtaSubmitContactListUserIdList
										.indexOf(_submitContactUserId), true);
					}
				}
			}

			// start get user login enterprise address book
			if (null != _mCoreService) {
				_mCoreService.startGetEnterpriseAddressbook();
			}

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

	// submit contact list cursor adapter
	class SubmitContactListCursorAdapter extends SimpleCursorAdapter {

		public SubmitContactListCursorAdapter() {
			super(
					TodoTaskApproveActivity.this,
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
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		}

		@Override
		protected void onContentChanged() {
			// auto requery
			super.onContentChanged();

			// need to change to-do task approve submit contact query cursor
			// change
			// to-do task approve submit contact query cursor
			this.changeCursor(getContentResolver().query(
					ContentUris.withAppendedId(Employee.ENTERPRISE_CONTENT_URI,
							IAUserExtension
									.getUserLoginEnterpriseId(_mLoginUser)),
					null, null, null, null));
		}

	}

	// core service connection
	class CoreServiceConnection implements ServiceConnection {

		private final String LOG_TAG = CoreServiceConnection.class
				.getCanonicalName();

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			// get core service
			_mCoreService = ((LocalBinder) binder).getService();

			Log.d(LOG_TAG, "The core service = " + _mCoreService
					+ " is connected, component name = " + name
					+ " and the binder = " + binder);
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

			// clear choices
			_mSubmitContactListView.clearChoices();

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
			// clear submit contact list
			_mSubmitContactList.clear();

			// get and check to-do task selected submit contact item id array
			long[] _tdtaSelectedSubmitContactItemIds = _mSubmitContactListView
					.getCheckedItemIds();
			if (null != _tdtaSelectedSubmitContactItemIds
					&& 0 < _tdtaSelectedSubmitContactItemIds.length) {
				// get to-do task submit contact list cursor
				Cursor _tdtaSubmitContactListCursor = ((SubmitContactListCursorAdapter) _mSubmitContactListView
						.getAdapter()).getCursor();

				// traversal to-do task selected contact items
				for (int i = 0; i < _tdtaSelectedSubmitContactItemIds.length; i++) {
					// move the cursor to the position, get the selected address
					// book contact and add to submit contact list
					_tdtaSubmitContactListCursor
							.moveToPosition(((int) _tdtaSelectedSubmitContactItemIds[i]) - 1);
					_mSubmitContactList.add(new ABContactBean(
							_tdtaSubmitContactListCursor));
				}
			}

			// refresh submit contacts
			refreshSubmitContacts();

			// show navigation bar
			((RelativeLayout) findViewById(R.id.navBar_relativeLayout))
					.setVisibility(View.VISIBLE);

			// clear choices
			_mSubmitContactListView.clearChoices();

			// close submit contact list sliding drawer
			_mSubmitContactListSlidingDrawer.animateClose();
		}

	}

	// to-do list task approve advice switch toggle button on checked change
	// listener
	class TodoTaskApproveAdviceSwitchToggleBtnOnCheckedChangeListener implements
			OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// check the checked flag and update my advice judge
			if (isChecked) {
				_mMyAdviceJudge = true;
			} else {
				_mMyAdviceJudge = false;
			}
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
