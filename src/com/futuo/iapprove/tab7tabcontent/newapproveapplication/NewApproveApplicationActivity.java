package com.futuo.iapprove.tab7tabcontent.newapproveapplication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.customwidget.CommonFormSeparator;
import com.futuo.iapprove.customwidget.EnterpriseFormItemFormItem;
import com.futuo.iapprove.customwidget.EnterpriseFormItemFormItem.EnterpriseFormItemInfoTextWatcher;
import com.futuo.iapprove.customwidget.IApproveImageBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.customwidget.NAAFormAttachmentFormItem;
import com.futuo.iapprove.customwidget.NAAFormAttachmentFormItem.NAAFormVoiceAttachmentInfoDataKeys;
import com.futuo.iapprove.customwidget.NAATDTSubmitContact;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormAttachmentType;
import com.futuo.iapprove.form.FormItemBean;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems.FormItem;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.Forms.Form;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.provider.UserEnterpriseTaskApprovingContentProvider.GeneratingNAATasks.GeneratingNAATask;
import com.futuo.iapprove.service.CoreService;
import com.futuo.iapprove.service.CoreService.LocalBinder;
import com.futuo.iapprove.tab7tabcontent.attachmentpresent.NAATaskTextImgAttachmentViewActivity;
import com.futuo.iapprove.tab7tabcontent.attachmentpresent.NAATaskTextImgAttachmentViewActivity.NAATaskTextImgAttachmentViewExtraData;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NAAFormItemEditorActivity.NAAFormItemEditorExtraData;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationActivity.NAAMorePlusInputListAdapter.NAAMorePlusInputListAdapterIconItemDataKey;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.futuo.iapprove.utils.AudioUtils;
import com.futuo.iapprove.utils.CalculateStringUtils;
import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.customcomponent.CTToast;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;

@SuppressWarnings("deprecation")
public class NewApproveApplicationActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = NewApproveApplicationActivity.class
			.getCanonicalName();

	// enterprise form type id and form id
	private Long _mFormTypeId;
	private Long _mFormId;

	// enterprise form name
	private String _mFormName;

	// new approve application submit contact list
	private List<ABContactBean> _mSubmitContactList;

	// enterprise form item form linearLayout
	private LinearLayout _mFormItemFormLinearLayout;

	// enterprise form item id(key) and form item form item view(value) map
	private Map<Long, EnterpriseFormItemFormItem> _mFormItemId7FormItemFormItemMap;

	// login user
	private UserBean _mLoginUser;

	// enterprise form attachment form parent frameLayout and form linearLayout
	private FrameLayout _mAttachmentFormParentFrameLayout;
	private LinearLayout _mAttachmentFormLinearLayout;

	// new approve application selected submit contacts gridLayout
	private GridLayout _mSelectedSubmitContactsGridLayout;

	// core service connection
	private CoreServiceConnection _mCoreServiceConnection;

	// new approve application submit contact list sliding drawer and its
	// listView
	private SlidingDrawer _mSubmitContactListSlidingDrawer;
	private ListView _mSubmitContactListView;

	// change to text and voice input mode image button
	private ImageButton _mChange2TextInputModeImageButton;
	private ImageButton _mChange2VoiceInputModeImageButton;

	// toggle audio recording button
	private Button _mToggleAudioRecordingButton;

	// note input editText and its with send button parent relativeLayout
	private EditText _mNoteInputEditText;
	private RelativeLayout _mNoteInputEditText7SendBtnParentRelativeLayout;

	// more plus input parent relativeLayout
	private RelativeLayout _mMorePlusInputParentRelativeLayout;

	// capture or select photo file path
	private String _mCaptureOrSelectPhotoFilePath;

	// input method manager
	private InputMethodManager _mInputMethodManager;

	// new approve application more plus input item icon image, its on click
	// listener and label array, the photos, camera and applications
	private final Object[][] NAA_MOREPLUS_INPUT_ITEMS = new Object[][] {
			{ R.drawable.img_naa_moreplus_photosinput_icon_imgbtn,
					new NAAMorePlusPhotosInputItemIconImgBtnOnClickListener(),
					R.string.naa_morePlus_photosInput_label },
			{ R.drawable.img_naa_moreplus_camerainput_icon_imgbtn,
					new NAAMorePlusCameraInputItemIconImgBtnOnClickListener(),
					R.string.naa_morePlus_cameraInput_label },
			{
					R.drawable.img_naa_moreplus_applicationsinput_icon_imgbtn,
					new NAAMorePlusApplicationsInputItemIconImgBtnOnClickListener(),
					R.string.naa_morePlus_applicationsInput_label } };

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.new_approve_application_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// initialize new approve application submit contact list
		_mSubmitContactList = new ArrayList<ABContactBean>();

		// check the data
		if (null != _extraData) {
			// get the user enterprise form type id, form id, name and the new
			// approve application submit contact
			_mFormTypeId = _extraData
					.getLong(NewApproveApplicationExtraData.ENTERPRISE_FROM_TYPE_ID);
			_mFormId = _extraData
					.getLong(NewApproveApplicationExtraData.ENTERPRISE_FROM_ID);
			_mFormName = _extraData
					.getString(NewApproveApplicationExtraData.ENTERPRISE_FROM_NAME);
			Object _submitContacts = _extraData
					.get(NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACTS);
			if (null != _submitContacts) {
				if (_submitContacts instanceof ABContactBean) {
					_mSubmitContactList.add((ABContactBean) _submitContacts);
				} else if (_submitContacts instanceof List) {
					_mSubmitContactList
							.addAll((Collection<? extends ABContactBean>) _submitContacts);
				}
			}
		}

		// get login user
		_mLoginUser = UserManager.getInstance().getUser();

		// get input method manager
		_mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// set subViews
		// set title
		setTitle(_mFormName);

		// set new approve application submit bar button item as right bar
		// button item
		setRightBarButtonItem(new IApproveImageBarButtonItem(this,
				R.drawable.img_naa6tdta_submitbarbtnitem,
				new NAASubmitBarBtnItemOnClickListener()));

		// submit contact
		// check submit contact list
		if (null != _mSubmitContactList && 0 < _mSubmitContactList.size()) {
			// set default submit contact
			refreshSubmitContacts();
		}

		// initialize enterprise form item id and form item form item view map
		_mFormItemId7FormItemFormItemMap = new HashMap<Long, EnterpriseFormItemFormItem>();

		// get form item form linearLayout
		_mFormItemFormLinearLayout = (LinearLayout) findViewById(R.id.naa_formItemForm_linearLayout);

		// refresh enterprise form item form
		refreshFormItemForm();

		// get enterprise form attachment form parent frameLayout and form
		// linearLayout
		_mAttachmentFormParentFrameLayout = (FrameLayout) findViewById(R.id.naa_attachmentForm_parent_frameLayout);
		_mAttachmentFormLinearLayout = (LinearLayout) findViewById(R.id.naa_attachmentForm_linearLayout);

		// bind add submit contact button on click listener
		((ImageButton) findViewById(R.id.naa_add_submitContact_imageButton))
				.setOnClickListener(new AddSubmitContactImgBtnOnClickListener());

		// get new approve application submit contact list sliding drawer and
		// its listView
		_mSubmitContactListSlidingDrawer = (SlidingDrawer) findViewById(R.id.naa_submitContactList_slidingDrawer);
		_mSubmitContactListView = (ListView) findViewById(R.id.naa_submitContact_listView);

		// bind cancel and done select new approve application submit contacts
		// imageButton, button on click listener
		((ImageButton) findViewById(R.id.naa_cancelSelect_submitContacts_imageButton))
				.setOnClickListener(new CancelSelectNAASubmitContactsImageButtonOnClickListener());
		((Button) findViewById(R.id.naa_doneSelect_submitContacts_button))
				.setOnClickListener(new DoneSelectNAASubmitContactsButtonOnClickListener());

		// set new approve application submit contact list cursor adapter
		_mSubmitContactListView
				.setAdapter(new NAASubmitContactListCursorAdapter());

		// set its choice mode
		_mSubmitContactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// set its on item click listener
		_mSubmitContactListView
				.setOnItemClickListener(new NAASubmitContactListViewOnItemClickListener());

		// get change to text input mode image button and bind its on click
		// listener
		_mChange2TextInputModeImageButton = (ImageButton) findViewById(R.id.naa_change2textInputMode_imageButton);
		_mChange2TextInputModeImageButton
				.setOnClickListener(new NAAChange2TextInputModeImgBtnOnClickListener());

		// get change to voice input mode image button and bind its on click
		// listener
		_mChange2VoiceInputModeImageButton = (ImageButton) findViewById(R.id.naa_change2voiceInputMode_imageButton);
		_mChange2VoiceInputModeImageButton
				.setOnClickListener(new NAAChange2VoiceInputModeImgBtnOnClickListener());

		// bind more plus input image button on click listener
		((ImageButton) findViewById(R.id.naa_morePlusInput_imageButton))
				.setOnClickListener(new NAAMorePlusInputImgBtnOnClickListener());

		// get toggle audio recording button and set its on touch listener
		_mToggleAudioRecordingButton = (Button) findViewById(R.id.naa_toggleAudioRecording_button);
		_mToggleAudioRecordingButton
				.setOnTouchListener(new NAAToggleAudioRecordingBtnOnTouchListener());

		// get note input editText and send button parent relativeLayout
		_mNoteInputEditText7SendBtnParentRelativeLayout = (RelativeLayout) findViewById(R.id.naa_note_parent_relativeLayout);

		// get note input editText
		_mNoteInputEditText = (EditText) findViewById(R.id.naa_note_editText);

		// set note input editText text changed listener
		_mNoteInputEditText
				.addTextChangedListener(new NAANoteInputEditTextWatcher());

		// bind its on touch listener
		_mNoteInputEditText
				.setOnTouchListener(new NAANoteInputEditTextOnTouchListener());

		// bind note send button on click listener
		((Button) findViewById(R.id.naa_note_send_button))
				.setOnClickListener(new NAANoteSendBtnOnClickListener());

		// get more plus input parent relativeLayout
		_mMorePlusInputParentRelativeLayout = (RelativeLayout) findViewById(R.id.naa_morePlusInput_parent_relativeLayout);

		// get more plus input gridView
		GridView _morePlusInputGridView = (GridView) findViewById(R.id.naa_morePlusInput_gridView);

		// generate more plus input item data list
		// define more plus input item data list
		List<Map<String, ?>> _morePlusInputItemDataList = new ArrayList<Map<String, ?>>();

		for (int i = 0; i < NAA_MOREPLUS_INPUT_ITEMS.length; i++) {
			// generate data
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// generate item icon value map
			Map<String, Object> _iconValueMap = new HashMap<String, Object>();
			// put icon image and on click listener in
			_iconValueMap
					.put(NAAMorePlusInputListAdapterIconItemDataKey.MOREPLUS_INPUT_ITEM_ICON_IMAGE,
							NAA_MOREPLUS_INPUT_ITEMS[i][0]);
			_iconValueMap
					.put(NAAMorePlusInputListAdapterIconItemDataKey.MOREPLUS_INPUT_ITEM_ICON_ONCLICKLISTENER,
							NAA_MOREPLUS_INPUT_ITEMS[i][1]);

			// put icon data map and label in
			_dataMap.put(NAAMorePlusInputListAdapter.MOREPLUS_INPUT_ITEM_ICON,
					_iconValueMap);
			_dataMap.put(
					NAAMorePlusInputListAdapter.MOREPLUS_INPUT_ITEM_LABEL,
					getResources().getString(
							(Integer) NAA_MOREPLUS_INPUT_ITEMS[i][2]));

			// add data map to data list
			_morePlusInputItemDataList.add(_dataMap);
		}

		// set more plus input list adapter
		_morePlusInputGridView
				.setAdapter(new NAAMorePlusInputListAdapter(
						this,
						_morePlusInputItemDataList,
						R.layout.naa_moreplus_input_item_layout,
						new String[] {
								NAAMorePlusInputListAdapter.MOREPLUS_INPUT_ITEM_ICON,
								NAAMorePlusInputListAdapter.MOREPLUS_INPUT_ITEM_LABEL },
						new int[] { R.id.naampii_icon_imageButton,
								R.id.naampii_label_textView }));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// check result code
		switch (resultCode) {
		case RESULT_OK:
			// check request code
			switch (requestCode) {
			case NewApproveApplicationRequestCode.NAA_FORMITEM_EDITOR_REQCODE:
				// check data
				if (null != data) {
					// get new approve application form item editor need to
					// update info value form item id and update info value
					Long _need2updateFormItemId = data
							.getExtras()
							.getLong(
									NewApproveApplicationExtraData.NAA_FORMITEM_EDITOR_NEED2UPDATEINFO_FORMITEM_ID);
					String _need2updateInfoValue = data
							.getExtras()
							.getString(
									NewApproveApplicationExtraData.NAA_FORMITEM_EDITOR_NEED2UPDATEINFO);

					// get the enterprise form item form item view
					EnterpriseFormItemFormItem _formItemView = _mFormItemId7FormItemFormItemMap
							.get(_need2updateFormItemId);

					// set the enterprise form item form item info
					_formItemView.setInfo(_need2updateInfoValue);
				}
				break;

			case NewApproveApplicationRequestCode.NAA_TAKEPHOTO_REQCODE:
			case NewApproveApplicationRequestCode.NAA_SELECTPHOTO_REQCODE:
				// check data
				if (null != data) {
					// get and check photo uri
					Uri _photoUri = data.getData();
					if (null != _photoUri) {
						// query photo from local storage
						Cursor _cursor = getContentResolver().query(_photoUri,
								new String[] { MediaStore.Images.Media.DATA },
								null, null, null);

						// check the cursor
						if (null != _cursor) {
							// move to first
							_cursor.moveToFirst();

							// get select photo file path
							_mCaptureOrSelectPhotoFilePath = _cursor
									.getString(_cursor
											.getColumnIndex(MediaStore.Images.Media.DATA));

							// close the cursor
							_cursor.close();
						}
					}
				}

				// define image scaled min
				int _imgScaledMin = 270;

				// define image attachment image bitmap
				Bitmap _imageAttachmentImgBitmap = null;

				// define bitmap factory options
				BitmapFactory.Options _options = new BitmapFactory.Options();

				// just get image bounds
				_options.inJustDecodeBounds = true;

				// get image width and height
				BitmapFactory.decodeFile(_mCaptureOrSelectPhotoFilePath,
						_options);
				int _imageWidth = _options.outWidth;
				int _imageHeight = _options.outHeight;

				// get real image bitmap
				_options.inJustDecodeBounds = false;
				_options.inSampleSize = Math.min(_imageWidth, _imageHeight)
						/ _imgScaledMin;

				// decode image file
				_imageAttachmentImgBitmap = BitmapFactory.decodeFile(
						_mCaptureOrSelectPhotoFilePath, _options);

				// check image width and height, then scaled the image bitmap
				if (_imageWidth <= _imageHeight) {
					_imageHeight = _imageHeight * _imgScaledMin / _imageWidth;
					_imageWidth = _imgScaledMin;
				} else {
					_imageWidth = _imageWidth * _imgScaledMin / _imageHeight;
					_imageHeight = _imgScaledMin;
				}

				// scaled the image bitmap
				_imageAttachmentImgBitmap = Bitmap.createScaledBitmap(
						_imageAttachmentImgBitmap, _imageWidth, _imageHeight,
						true);

				// generate image attachment info with file path and image
				Map<String, Object> _imageAttachmentInfo = new HashMap<String, Object>();
				_imageAttachmentInfo.put(
						NAAFormVoiceAttachmentInfoDataKeys.ATTACHMENT_FILEPATH,
						_mCaptureOrSelectPhotoFilePath);
				_imageAttachmentInfo
						.put(NAAFormVoiceAttachmentInfoDataKeys.IMAGEATTACHMENT_IMAGE_BITMAP,
								_imageAttachmentImgBitmap);

				// get the image and set as image attachment,
				// then add to form attachment form linearLayout
				addNAAFormAttachmentFormItem(
						TaskFormAttachmentType.IMAGE_ATTACHMENT,
						_imageAttachmentInfo,
						new NAAFormImageAttachmentFormItemOnClickListener());

				// hide more plus input parent relativeLayout if needed
				if (View.VISIBLE == _mMorePlusInputParentRelativeLayout
						.getVisibility()) {
					_mMorePlusInputParentRelativeLayout
							.setVisibility(View.GONE);
				}
				break;
			}
			break;

		default:
			// nothing to do
			break;
		}
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
		// query the enterprise form all items
		Cursor _cursor = getContentResolver().query(
				FormItem.FORMITEMS_CONTENT_URI,
				null,
				FormItem.ENTERPRISE_FORMITEMS_WITHFORMTYPEID7FORMID_CONDITION,
				new String[] {
						IAUserExtension.getUserLoginEnterpriseId(_mLoginUser)
								.toString(), _mFormTypeId.toString(),
						_mFormId.toString() }, null);

		// check the cursor
		if (null != _cursor) {
			while (_cursor.moveToNext()) {
				// get enterprise form item
				FormItemBean _formItem = new FormItemBean(_cursor);

				// define enterprise form item form item
				EnterpriseFormItemFormItem _formItemFormItem = null;

				// check form item id existed in enterprise form item id and
				// form item form item view map key set
				if (!_mFormItemId7FormItemFormItemMap.keySet().contains(
						_formItem.getItemId())) {
					// generate new enterprise form item form item
					_formItemFormItem = EnterpriseFormItemFormItem
							.generateEnterpriseFormItemFormItem(_formItem);

					// check enterprise form item formula
					if (null == _formItem.getFormula()) {
						// set on click listener
						_formItemFormItem
								.setOnClickListener(new NAAFormItemFormItemOnClickListener());
					}

					// get and check enterprise form item form linearLayout
					// subview count
					int _formItemFormItemCount = _mFormItemFormLinearLayout
							.getChildCount();
					if (0 == _formItemFormItemCount) {
						// single
						// check clickable, need to capital and set enterprise
						// form item form item background
						if (_formItemFormItem.isClickable()) {
							_formItemFormItem
									.setBackgroundResource(_formItem
											.needCapital() ? R.drawable.enterprise_form_item_form_notbottom_item_bg
											: R.drawable.enterprise_form_item_form_bottom_item_bg);
						}
					} else {
						// two and more
						// check clickable, need to capital and set enterprise
						// form item form item background
						if (_formItemFormItem.isClickable()) {
							_formItemFormItem
									.setBackgroundResource(_formItem
											.needCapital() ? R.drawable.enterprise_form_item_form_notbottom_item_bg
											: R.drawable.enterprise_form_item_form_bottom_item_bg);
						}

						for (int i = 0; i < _formItemFormItemCount; i++) {
							// trim separator
							if (!(_mFormItemFormLinearLayout.getChildAt(i) instanceof CommonFormSeparator)) {
								// get each form item form item existed
								EnterpriseFormItemFormItem _existedFormItemFormItem = (EnterpriseFormItemFormItem) _mFormItemFormLinearLayout
										.getChildAt(i);

								// check clickable and set enterprise form item
								// form item background
								if (_existedFormItemFormItem.isClickable()) {
									_existedFormItemFormItem
											.setBackgroundResource(R.drawable.enterprise_form_item_form_notbottom_item_bg);
								}
							}
						}
					}

					// add separator line and enterprise form item form item to
					// form item form
					_mFormItemFormLinearLayout.addView(new CommonFormSeparator(
							this), new LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					_mFormItemFormLinearLayout.addView(_formItemFormItem,
							new LayoutParams(LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT, 1));

					// check enterprise form item need to capital
					if (_formItem.needCapital()) {
						// clone enterprise form item copy
						FormItemBean _formItemCopy = new FormItemBean();
						_formItemCopy
								.setItemName(String
										.format(getResources()
												.getString(
														R.string.naa_formItem_infoLabel_capitalFormat),
												_formItem.getItemName()));
						_formItemCopy.setItemType(_formItem.getItemType());
						_formItemCopy.setMustWrite(false);

						// generate new generate enterprise form item capital
						// form item
						EnterpriseFormItemFormItem _formItemCapitalFormItem = EnterpriseFormItemFormItem
								.generateEnterpriseFormItemFormItem(_formItemCopy);

						// set new generate enterprise form item capital form
						// item as new enterprise form item form item tag
						_formItemFormItem.setTag(
								NAAFormItemTagKey.NEED_CAPITAL.hashCode(),
								_formItemCapitalFormItem);

						// add need capital enterprise form item form item text
						// changed listener
						_formItemFormItem
								.addTextChangedListener(new NAAFormItemCapitalFormItemInfoTextWatcher());

						// add separator line and new generate enterprise form
						// item capital form item to form item form
						_mFormItemFormLinearLayout.addView(
								new CommonFormSeparator(this),
								new LayoutParams(LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT));
						_mFormItemFormLinearLayout.addView(
								_formItemCapitalFormItem, new LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1));
					}
				} else {
					// get the existed enterprise form item form item and set
					// its form item
					(_formItemFormItem = _mFormItemId7FormItemFormItemMap
							.get(_formItem.getItemId())).setFormItem(_formItem);

					// check enterprise form item formula
					if (null == _formItem.getFormula()) {
						// set on click listener
						_formItemFormItem
								.setOnClickListener(new NAAFormItemFormItemOnClickListener());
					} else {
						// set unclickable
						_formItemFormItem.setClickable(false);
					}
				}

				// add enterprise form item form item to map
				_mFormItemId7FormItemFormItemMap.put(_formItem.getItemId(),
						_formItemFormItem);
			}

			// close cursor
			_cursor.close();
		}

		// traversal enterprise form item id(key) and form item form item
		// view(value) map
		for (EnterpriseFormItemFormItem _formItemFormItem : _mFormItemId7FormItemFormItemMap
				.values()) {
			// get and check enterprise form item formula
			String _formItemFormula = _formItemFormItem.getFormItem()
					.getFormula();
			if (null != _formItemFormula) {
				// define new approve application formula operand form item info
				// text watcher
				NAAFormulaOperandFormItemInfoTextWatcher _naaFormulaOperandFormItemInfoTextWatcher = new NAAFormulaOperandFormItemInfoTextWatcher();

				// traversal enterprise form item formula operand form item id
				// list
				for (Double _formItemIdDouble : CalculateStringUtils
						.getCalculateExpressionNumbers(_formItemFormula)) {
					// get form item formula operand form item
					EnterpriseFormItemFormItem _formItemFormulaOperandFormItem = _mFormItemId7FormItemFormItemMap
							.get(_formItemIdDouble.longValue());

					// set enterprise form item with formula as its operand form
					// item tag
					_formItemFormulaOperandFormItem.setTag(
							NAAFormItemTagKey.WITH_FORMULA.hashCode(),
							_formItemFormItem);

					// add formula operand enterprise form item form item text
					// changed listener
					_formItemFormulaOperandFormItem
							.addTextChangedListener(_naaFormulaOperandFormItemInfoTextWatcher);
				}
			}
		}
	}

	// add new approve application attachment(text, image, voice and
	// application) as attachment form item to attachment form linearLayout
	private void addNAAFormAttachmentFormItem(
			TaskFormAttachmentType attachmentType, Object attachmentInfo,
			OnClickListener attachmentOnClickListener) {
		// check enterprise form attachment form parent frameLayout visibility
		if (View.VISIBLE != _mAttachmentFormParentFrameLayout.getVisibility()) {
			// show enterprise form attachment form parent frameLayout
			_mAttachmentFormParentFrameLayout.setVisibility(View.VISIBLE);
		}

		// generate new added attachment
		NAAFormAttachmentFormItem _newAddedAttachmentFormItem = NAAFormAttachmentFormItem
				.generateNAAFormAttachmentFormItem(attachmentType,
						attachmentInfo);

		// check attachment on click listener and set new added attachment form
		// item on click listener
		if (null != attachmentOnClickListener) {
			_newAddedAttachmentFormItem
					.setOnClickListener(attachmentOnClickListener);
		}

		// set new added attachment form item on long click listener
		_newAddedAttachmentFormItem
				.setOnLongClickListener(new NAAFormAttachmentFormItemOnLongClickListener());

		// add new added attachment to attachment form linearLayout
		_mAttachmentFormLinearLayout.addView(_newAddedAttachmentFormItem,
				new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1));
	}

	// refresh new approve application submit contacts
	private void refreshSubmitContacts() {
		// check and initialize selected submit contacts gridLayout
		if (null == _mSelectedSubmitContactsGridLayout) {
			// get new approve application selected submit contacts gridLayout
			_mSelectedSubmitContactsGridLayout = (GridLayout) findViewById(R.id.naa_selectedSubmitContacts_gridLayout);
		}

		// check submit contact list again
		if (null != _mSubmitContactList && 0 < _mSubmitContactList.size()) {
			// show selected submit contacts gridLayout if needed
			if (View.VISIBLE != _mSelectedSubmitContactsGridLayout
					.getVisibility()) {
				_mSelectedSubmitContactsGridLayout.setVisibility(View.VISIBLE);
			}

			// define and initialize new approve application submit contact
			// list user id list
			List<Long> _naaSubmitContactListUserIdList = new ArrayList<Long>();
			for (ABContactBean _submitContact : _mSubmitContactList) {
				_naaSubmitContactListUserIdList.add(_submitContact.getUserId());
			}

			// traversal new approve application submit contacts gridLayout all
			// subviews
			int _submitContactGridLayoutSubviewIndex = 0;
			while (_submitContactGridLayoutSubviewIndex < _mSelectedSubmitContactsGridLayout
					.getChildCount()) {
				// get new approve application submit contacts gridLayout
				// subview submit contact user id
				Long _naaSubmitContactsGridLayoutSubviewSubmitContactUserId = ((NAATDTSubmitContact) _mSelectedSubmitContactsGridLayout
						.getChildAt(_submitContactGridLayoutSubviewIndex))
						.getSubmitContact().getUserId();
				if (_naaSubmitContactListUserIdList
						.contains(_naaSubmitContactsGridLayoutSubviewSubmitContactUserId)) {
					// set the new approve application submit contacts
					// gridLayout subview submit contact which contains in
					// submit contact list for delete
					_mSubmitContactList
							.get(_naaSubmitContactListUserIdList
									.indexOf(_naaSubmitContactsGridLayoutSubviewSubmitContactUserId))
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

			// traversal new approve application submit contact list, generate
			// the submit contact and add to submit contacts gridLayout
			for (ABContactBean _submitContact : _mSubmitContactList) {
				// check submit contact dirty type
				if (LocalStorageDataDirtyType.DELETE != _submitContact
						.getLocalStorageDataDirtyType()) {
					// generate new added submit contact
					NAATDTSubmitContact _newAddedSubmitContact = NAATDTSubmitContact
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
	// new approve application extra data constant
	public static final class NewApproveApplicationExtraData {

		// new approve application enterprise form type id, form id, name and
		// submit contact bean
		public static final String ENTERPRISE_FROM_TYPE_ID = "enterprise_form_type_id";
		public static final String ENTERPRISE_FROM_ID = "enterprise_form_id";
		public static final String ENTERPRISE_FROM_NAME = "enterprise_form_name";
		public static final String NEW_APPROVEAPPLICATION_SUBMIT_CONTACTS = "new_approveapplication_submit_contacts";

		// new approve application form item editor need to update info value
		// form item id and update info value
		public static final String NAA_FORMITEM_EDITOR_NEED2UPDATEINFO_FORMITEM_ID = "naa_formitem_editor_need2updateinfo_formitem_id";
		public static final String NAA_FORMITEM_EDITOR_NEED2UPDATEINFO = "naa_formitem_editor_need2updateinfo";

	}

	// new approve application request code
	static class NewApproveApplicationRequestCode {

		// new approve application form item editor request code
		private static final int NAA_FORMITEM_EDITOR_REQCODE = 400;

		// new approve application take and select photo request code
		private static final int NAA_TAKEPHOTO_REQCODE = 402;
		private static final int NAA_SELECTPHOTO_REQCODE = 403;

	}

	// new approve application submit bar button item on click listener
	class NAASubmitBarBtnItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// check submit contact list
			if (null != _mSubmitContactList && 0 < _mSubmitContactList.size()) {
				// define new approve application form item value string builder
				StringBuilder _naaFormItemValueStringBuilder = new StringBuilder();

				// get and check form item form linearLayout subviews count
				int _formItemFormLinearLayoutSubviewsCount = _mFormItemFormLinearLayout
						.getChildCount();
				if (1 < _formItemFormLinearLayoutSubviewsCount) {
					// traversal form item form linearLayout all subviews
					for (int i = 1; i < _formItemFormLinearLayoutSubviewsCount; i++) {
						// get and check new approve application form item form
						// linearLayout subview
						View _naaFormItemFormLinearLayoutSubview = _mFormItemFormLinearLayout
								.getChildAt(i);
						if (_naaFormItemFormLinearLayoutSubview instanceof EnterpriseFormItemFormItem) {
							// get new approve application form item form item
							EnterpriseFormItemFormItem _naaFormItemFormItem = (EnterpriseFormItemFormItem) _naaFormItemFormLinearLayoutSubview;

							// get new approve application form item form item
							// info
							String _naaFormItemFormItemInfo = _naaFormItemFormItem
									.getInfo();

							// check new approve application form item must
							// write flag and info
							if (_naaFormItemFormItem.getFormItem().mustWrite()
									&& (null == _naaFormItemFormItemInfo || ""
											.equalsIgnoreCase(_naaFormItemFormItemInfo))) {
								Log.d(LOG_TAG,
										"There is at least one must write form item not fill its content");

								// show there is at least one must write form
								// item not fill content toast
								Toast.makeText(
										NewApproveApplicationActivity.this,
										R.string.toast_fill_mustWriteFormItemContent,
										Toast.LENGTH_SHORT).show();

								// return immediately
								return;
							} else {
								// add enterprise form item physical name, value
								// and separate character to form item value
								// string builder
								_naaFormItemValueStringBuilder
										.append("[$zy$")
										.append(_naaFormItemFormItem
												.getFormItem()
												.getItemPhysicalName())
										.append(':')
										.append(_naaFormItemFormItemInfo)
										.append(']');
							}
						}
					}
				}

				// define and initialize submit contacts user id, approve number
				// string builder
				StringBuilder _submitContactsUserIdStringBuilder = new StringBuilder();
				StringBuilder _submitContactsApproveNumberStringBuilder = new StringBuilder();
				for (ABContactBean _submitContact : _mSubmitContactList) {
					_submitContactsUserIdStringBuilder.append(_submitContact
							.getUserId());
					_submitContactsApproveNumberStringBuilder
							.append(_submitContact.getApproveNumber());
					if (_mSubmitContactList.size() - 1 != _mSubmitContactList
							.indexOf(_submitContact)) {
						_submitContactsUserIdStringBuilder.append(',');
						_submitContactsApproveNumberStringBuilder.append(',');
					}
				}

				// define and initialize new approve application attachment path
				// string builder
				StringBuilder _naaAttachmentPathStringBuilder = new StringBuilder();
				if (null != _mAttachmentFormParentFrameLayout
						&& View.VISIBLE == _mAttachmentFormParentFrameLayout
								.getVisibility()
						&& null != _mAttachmentFormLinearLayout
						&& 1 < _mAttachmentFormLinearLayout.getChildCount()) {
					for (int i = 1; i < _mAttachmentFormLinearLayout
							.getChildCount(); i++) {
						// get and check new approve application attachment path
						String _naaAttachmentPath = ((NAAFormAttachmentFormItem) _mAttachmentFormLinearLayout
								.getChildAt(i)).getAttachmentPath();
						if (null != _naaAttachmentPath) {
							_naaAttachmentPathStringBuilder
									.append(_naaAttachmentPath);

							if (_mAttachmentFormLinearLayout.getChildCount() - 1 != i) {
								_naaAttachmentPathStringBuilder.append(',');
							}
						}
					}
				}

				// update form default submit contacts
				// define and initialize the form update content values
				ContentValues _updateContentValues = new ContentValues();
				_updateContentValues.put(Form.DEFAULT_SUBMITCONTACTS,
						_submitContactsUserIdStringBuilder.toString());

				// update the new approve application form data
				getContentResolver().update(
						ContentUris.withAppendedId(Form.FORM_CONTENT_URI,
								_mFormId),
						_updateContentValues,
						Form.ENTERPRISE_FORMS_WITHTYPEID_CONDITION,
						new String[] {
								IAUserExtension.getUserLoginEnterpriseId(
										UserManager.getInstance().getUser())
										.toString(), _mFormTypeId.toString() });

				// insert the new approve application for generating to local
				// storage
				// define and initialize the new approve application approving
				// for inserting content values
				ContentValues _insertContentValues = new ContentValues();
				_insertContentValues.put(GeneratingNAATask.FORM_ID,
						_mFormId.toString());
				_insertContentValues.put(GeneratingNAATask.ENTERPRISE_ID,
						IAUserExtension.getUserLoginEnterpriseId(_mLoginUser));
				_insertContentValues.put(GeneratingNAATask.APPROVE_NUMBER,
						_mLoginUser.getName());
				_insertContentValues.put(GeneratingNAATask.SUBMITCONTACTS,
						_submitContactsApproveNumberStringBuilder.toString());
				_insertContentValues.put(GeneratingNAATask.FORM_NAME,
						_mFormName);
				_insertContentValues.put(GeneratingNAATask.FORMITEM_VALUE,
						_naaFormItemValueStringBuilder.toString());
				_insertContentValues.put(GeneratingNAATask.FORM_ATTACHMENTPATH,
						_naaAttachmentPathStringBuilder.toString());

				// insert the new approve application for generating to local
				// storage
				getContentResolver().insert(
						GeneratingNAATask.GENERATINGNAATASKS_CONTENT_URI,
						_insertContentValues);

				// popup to-do task approve activity with result
				popActivityWithResult(RESULT_OK, null);
			} else {
				Log.d(LOG_TAG, "Please select at least one submit contact");

				// show select at least one submit contact toast
				Toast.makeText(NewApproveApplicationActivity.this,
						R.string.toast_select_submitContact, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	// new approve application form item form item on click listener
	class NAAFormItemFormItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View formItemFormItem) {
			// get new approve application form item object
			FormItemBean _formItem = ((EnterpriseFormItemFormItem) formItemFormItem)
					.getFormItem();

			// define new approve application form item editor extra data map
			Map<String, Object> _extraMap = new HashMap<String, Object>();

			// put new approve application form item name, type, selector
			// content, info value and id to extra data map as param
			_extraMap.put(NAAFormItemEditorExtraData.NAA_FROMITEM_NAME,
					_formItem.getItemName());
			_extraMap.put(NAAFormItemEditorExtraData.NAA_FROMITEM_TYPE,
					_formItem.getItemType());
			_extraMap.put(
					NAAFormItemEditorExtraData.NAA_FROMITEM_SELECTORCONTENT,
					_formItem.getSelectorContentInfos());
			_extraMap.put(NAAFormItemEditorExtraData.NAA_FROMITEM_INFOVALUE,
					((EnterpriseFormItemFormItem) formItemFormItem).getInfo());
			_extraMap.put(NAAFormItemEditorExtraData.NAA_FROMITEM_ID,
					_formItem.getItemId());

			// go to new approve application form item editor activity with
			// extra data map
			pushActivityForResult(
					NAAFormItemEditorActivity.class,
					_extraMap,
					NewApproveApplicationRequestCode.NAA_FORMITEM_EDITOR_REQCODE);
		}

	}

	// new approve application form item tag key
	enum NAAFormItemTagKey {

		// need capital and with formula form item
		NEED_CAPITAL, WITH_FORMULA

	}

	// new approve application form item need capital form item info text
	// watcher
	class NAAFormItemCapitalFormItemInfoTextWatcher implements
			EnterpriseFormItemInfoTextWatcher {

		@Override
		public void afterTextChanged(
				EnterpriseFormItemFormItem enterpriseFormItemFormItem,
				Editable s) {
			// get and check enterprise form item form item tag as form item
			// capital form item
			EnterpriseFormItemFormItem _formItemCapitalFormItem = (EnterpriseFormItemFormItem) enterpriseFormItemFormItem
					.getTag(NAAFormItemTagKey.NEED_CAPITAL.hashCode());
			if (null != _formItemCapitalFormItem) {
				// get and check editable info capital
				String _capitalString = CalculateStringUtils.calculateCapital(s
						.toString());
				if (null != _capitalString) {
					// set form item capital form item info
					_formItemCapitalFormItem.setInfo(_capitalString);
				} else {
					Log.d(LOG_TAG, "Calculate editable info error");

					// clear form item capital form item info
					_formItemCapitalFormItem.setInfo("");
				}
			} else {
				Log.e(LOG_TAG,
						"New approve application form item capital form item info text watcher error");
			}
		}

		@Override
		public void beforeTextChanged(
				EnterpriseFormItemFormItem enterpriseFormItemFormItem,
				CharSequence s, int start, int count, int after) {
			// nothing to do
		}

		@Override
		public void onTextChanged(
				EnterpriseFormItemFormItem enterpriseFormItemFormItem,
				CharSequence s, int start, int before, int count) {
			// nothing to do
		}

	}

	// new approve application formula operand form item info text watcher
	class NAAFormulaOperandFormItemInfoTextWatcher implements
			EnterpriseFormItemInfoTextWatcher {

		@Override
		public void afterTextChanged(
				EnterpriseFormItemFormItem enterpriseFormItemFormItem,
				Editable s) {
			// get and check formula operand form item tag as form item with
			// formula form item
			EnterpriseFormItemFormItem _withFormulaFormItem = (EnterpriseFormItemFormItem) enterpriseFormItemFormItem
					.getTag(NAAFormItemTagKey.WITH_FORMULA.hashCode());
			if (null != _withFormulaFormItem) {
				// get and check formula operand and operator list
				List<Object> _formulaOperandAndOperatorList = CalculateStringUtils
						.getCalculateExpressionTokens(_withFormulaFormItem
								.getFormItem().getFormula());
				for (Object _formulaToken : _formulaOperandAndOperatorList) {
					if (_formulaToken instanceof Double) {
						// get and check the formula operand form item info
						String _formulaOperandFormItemInfo = _mFormItemId7FormItemFormItemMap
								.get(((Double) _formulaToken).longValue())
								.getInfo();
						if (null == _formulaOperandFormItemInfo
								|| "".equalsIgnoreCase(_formulaOperandFormItemInfo)) {
							_formulaOperandAndOperatorList.set(
									_formulaOperandAndOperatorList
											.indexOf(_formulaToken), Double
											.valueOf(0));
						} else {
							_formulaOperandAndOperatorList
									.set(_formulaOperandAndOperatorList
											.indexOf(_formulaToken),
											Double.parseDouble(_formulaOperandFormItemInfo));
						}
					}
				}

				// set with formula form item info
				_withFormulaFormItem.setInfo(CalculateStringUtils
						.calculateExpression(_formulaOperandAndOperatorList));
			} else {
				Log.e(LOG_TAG,
						"New approve application formula operand form item info text watcher error");
			}
		}

		@Override
		public void beforeTextChanged(
				EnterpriseFormItemFormItem enterpriseFormItemFormItem,
				CharSequence s, int start, int count, int after) {
			// nothing to do
		}

		@Override
		public void onTextChanged(
				EnterpriseFormItemFormItem enterpriseFormItemFormItem,
				CharSequence s, int start, int before, int count) {
			// nothing to do
		}

	}

	// new approve application form text attachment form item on click listener
	class NAAFormTextAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// go to new approve application or to-do task text or image
			// attachment view activity
			// define new approve application or to-do task text or image
			// attachment view extra data map
			Map<String, String> _extraMap = new HashMap<String, String>();

			// put new approve application text attachment text to extra data
			// map as param
			_extraMap
					.put(NAATaskTextImgAttachmentViewExtraData.NAA_TASK_TEXT_IMAGE_ATTCHMENT_OBJECT,
							((TextView) v).getText().toString());

			// go to new approve application or iApprove task text or image
			// attachment view activity with extra data map
			pushActivity(NAATaskTextImgAttachmentViewActivity.class, _extraMap);
		}

	}

	// new approve application form image attachment form item on click listener
	class NAAFormImageAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// get and check tag of the imageView
			Object _imageViewTag = ((ImageView) v).getTag();
			if (null != _imageViewTag) {
				// go to new approve application or to-do task text or image
				// attachment view activity
				// define new approve application or to-do task text or image
				// attachment view extra data map
				Map<String, Bitmap> _extraMap = new HashMap<String, Bitmap>();

				// put new approve application image attachment image bitmap to
				// extra data map as param
				_extraMap
						.put(NAATaskTextImgAttachmentViewExtraData.NAA_TASK_TEXT_IMAGE_ATTCHMENT_OBJECT,
								(Bitmap) _imageViewTag);

				// go to new approve application or iApprove task text or image
				// attachment view activity with extra data map
				pushActivity(NAATaskTextImgAttachmentViewActivity.class,
						_extraMap);
			} else {
				Log.e(LOG_TAG,
						"Get new approve application form image attachment image view error");
			}
		}

	}

	// new approve application form voice attachment form item on click listener
	class NAAFormVoiceAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// get voice playing flag
			if ((Boolean) v.getTag()) {
				Log.d(LOG_TAG, "Play the voice");

				// play the voice
				AudioUtils
						.playRecorderAudio((String) v
								.getTag(NAAFormVoiceAttachmentInfoDataKeys.ATTACHMENT_FILEPATH
										.hashCode()));
			} else {
				Log.d(LOG_TAG, "Stop play the voice");

				// stop play the voice
				AudioUtils.stopPlayRecorderAudio();
			}
		}

	}

	// new approve application form attachment form item on long click listener
	class NAAFormAttachmentFormItemOnLongClickListener implements
			OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			// remove the click form attachment form item from attachment form
			// linearLayout
			_mAttachmentFormLinearLayout.removeView(v);

			// get and check form attachment form linearLayout subviews count
			if (1 == _mAttachmentFormLinearLayout.getChildCount()) {
				// hide enterprise form attachment form parent frameLayout
				_mAttachmentFormParentFrameLayout.setVisibility(View.GONE);
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
				if (_submitContact.getUserId().longValue() == ((NAATDTSubmitContact) v)
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
				// define new approve application submit contact list user id
				// list
				List<Long> _naaSubmitContactListUserIdList = new ArrayList<Long>();

				// get new approve application submit contact list cursor and
				// move to first
				Cursor _naaSubmitContactListCursor = ((NAASubmitContactListCursorAdapter) _mSubmitContactListView
						.getAdapter()).getCursor();
				_naaSubmitContactListCursor.moveToFirst();

				// traversal new approve application submit contacts and add
				// user id to list
				do {
					_naaSubmitContactListUserIdList.add(new ABContactBean(
							_naaSubmitContactListCursor).getUserId());
				} while (_naaSubmitContactListCursor.moveToNext());

				// traversal new approve application submit contact list
				for (ABContactBean _submitContact : _mSubmitContactList) {
					// get, check new approve application submit contact user id
					// and set the submit contact item checked
					Long _submitContactUserId = _submitContact.getUserId();
					if (_naaSubmitContactListUserIdList
							.contains(_submitContactUserId)) {
						_mSubmitContactListView.setItemChecked(
								_naaSubmitContactListUserIdList
										.indexOf(_submitContactUserId), true);
					}
				}
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

	// new approve application submit contact list cursor adapter
	class NAASubmitContactListCursorAdapter extends SimpleCursorAdapter {

		public NAASubmitContactListCursorAdapter() {
			super(
					NewApproveApplicationActivity.this,
					R.layout.naagenerating_tdtapproving_submitcontacts_select_layout,
					getContentResolver()
							.query(ContentUris
									.withAppendedId(
											Employee.ENTERPRISE_CONTENT_URI,
											IAUserExtension
													.getUserLoginEnterpriseId(_mLoginUser)),
									null, null, null, null),
					new String[] { Employee.NAME, Employee.APPROVE_NUMBER },
					new int[] { R.id.naagtdta_submitContactName_textView,
							R.id.naagtdta_submitContactApproveNumber_textView },
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		}

		@Override
		protected void onContentChanged() {
			// auto requery
			super.onContentChanged();

			// need to change new approve application submit contact query
			// cursor change
			// new approve application submit contact query cursor
			this.changeCursor(getContentResolver().query(
					ContentUris.withAppendedId(Employee.ENTERPRISE_CONTENT_URI,
							IAUserExtension
									.getUserLoginEnterpriseId(_mLoginUser)),
					null, null, null, null));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// get the got view
			View _gotView = super.getView(position, convertView, parent);

			// set checked text view checked
			((CheckedTextView) _gotView
					.findViewById(R.id.naagtdta_submitContactSelect_checkedTextView))
					.setChecked(((ListView) parent).isItemChecked(position));

			return _gotView;
		}

	}

	// new approve application submit contact list view on item click listener
	class NAASubmitContactListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// set checked text view checked
			((CheckedTextView) view
					.findViewById(R.id.naagtdta_submitContactSelect_checkedTextView))
					.setChecked(((ListView) parent).isItemChecked(position));
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

	// cancel select new approve application submit contacts imageButton on
	// click listener
	class CancelSelectNAASubmitContactsImageButtonOnClickListener implements
			OnClickListener {

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

	// done select new approve application submit contacts button on click
	// listener
	class DoneSelectNAASubmitContactsButtonOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// done select new approve application submit contacts
			// clear submit contact list
			_mSubmitContactList.clear();

			// get and check new approve application selected submit contact
			// item id array
			long[] _naaSelectedSubmitContactItemIds = _mSubmitContactListView
					.getCheckedItemIds();
			if (null != _naaSelectedSubmitContactItemIds
					&& 0 < _naaSelectedSubmitContactItemIds.length) {
				// get new approve application submit contact list cursor
				Cursor _naaSubmitContactListCursor = ((NAASubmitContactListCursorAdapter) _mSubmitContactListView
						.getAdapter()).getCursor();

				// traversal new approve application selected contact items
				for (int i = 0; i < _naaSelectedSubmitContactItemIds.length; i++) {
					// move the cursor to the position, get the selected address
					// book contact and add to submit contact list
					_naaSubmitContactListCursor
							.moveToPosition(((int) _naaSelectedSubmitContactItemIds[i]) - 1);
					_mSubmitContactList.add(new ABContactBean(
							_naaSubmitContactListCursor));
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

	// new approve application change to text input mode image button on click
	// listener
	class NAAChange2TextInputModeImgBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// change to text input mode
			// hide change to text input mode image button and show change to
			// voice input mode image button
			_mChange2TextInputModeImageButton.setVisibility(View.GONE);
			_mChange2VoiceInputModeImageButton.setVisibility(View.VISIBLE);

			// hide toggle audio recording button and show note input editText
			// and send button parent relativeLayout
			_mToggleAudioRecordingButton.setVisibility(View.GONE);
			_mNoteInputEditText7SendBtnParentRelativeLayout
					.setVisibility(View.VISIBLE);

			// show soft input
			_mInputMethodManager.showSoftInput(_mNoteInputEditText, 0);
		}

	}

	// new approve application change to voice input mode image button on click
	// listener
	class NAAChange2VoiceInputModeImgBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// change to voice input mode
			// hide change to voice input mode image button and show change to
			// text input mode image button
			_mChange2VoiceInputModeImageButton.setVisibility(View.GONE);
			_mChange2TextInputModeImageButton.setVisibility(View.VISIBLE);

			// hide note input editText and send button parent relativeLayout
			// and show toggle audio recording button
			_mNoteInputEditText7SendBtnParentRelativeLayout
					.setVisibility(View.GONE);
			_mToggleAudioRecordingButton.setVisibility(View.VISIBLE);

			// hide soft input
			_mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

			// hide more plus input parent relativeLayout if needed
			if (View.VISIBLE == _mMorePlusInputParentRelativeLayout
					.getVisibility()) {
				_mMorePlusInputParentRelativeLayout.setVisibility(View.GONE);
			}
		}

	}

	// new approve application more plus input image button on click listener
	class NAAMorePlusInputImgBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// more plus input
			// check more plus input parent relativeLayout visibility
			if (View.VISIBLE != _mMorePlusInputParentRelativeLayout
					.getVisibility()) {
				// hide change to text input mode image button and show change
				// to voice input mode image button
				_mChange2TextInputModeImageButton.setVisibility(View.GONE);
				_mChange2VoiceInputModeImageButton.setVisibility(View.VISIBLE);

				// hide toggle audio recording button and show note input
				// editText and send button parent relativeLayout
				_mToggleAudioRecordingButton.setVisibility(View.GONE);
				_mNoteInputEditText7SendBtnParentRelativeLayout
						.setVisibility(View.VISIBLE);

				// hide soft input
				_mInputMethodManager.hideSoftInputFromWindow(
						v.getWindowToken(), 0);

				// show more plus input parent relativeLayout
				_mMorePlusInputParentRelativeLayout.setVisibility(View.VISIBLE);
			}
		}

	}

	// new approve application toggle audio recording button on touch listener
	class NAAToggleAudioRecordingBtnOnTouchListener implements OnTouchListener {

		// audio recording timer
		private final Timer AUDIORECORDING_TIMER = new Timer();

		// milliseconds per second
		private final Long MILLISECONDS_PER_SECOND = 1000L;

		// voice record toast
		private final NAAVoiceAttachmentVoiceRecordToast VOICERECORD_TOAST = new NAAVoiceAttachmentVoiceRecordToast(
				NewApproveApplicationActivity.this);

		// update recording voice amplitude imageView image handle
		private final Handler UPDATE_RECORDINGVOICEAMP_IMGVIEW_IMAGE_HANDLE = new Handler();

		// audio recording chronometer and amplitude timer task
		private TimerTask _mAudioRecordingChronometerTimerTask;
		private TimerTask _mAudioRecordingAmplitudeTimerTask;

		// audio recording file path and duration
		private String _mAudioRecordingFilePath;
		private Integer _mAudioRecordingDuration;

		@Override
		public boolean onTouch(View toggleAudioRecordingButton,
				MotionEvent event) {
			// get axis y
			float _axisY = event.getY();

			// check motion event action
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// stop play all voice attachment voice
				for (int i = 1; i < _mAttachmentFormLinearLayout
						.getChildCount(); i++) {
					// get and check each voice attachment form item
					NAAFormAttachmentFormItem _formAttachmentFormItem = (NAAFormAttachmentFormItem) _mAttachmentFormLinearLayout
							.getChildAt(i);
					if (TaskFormAttachmentType.VOICE_ATTACHMENT == _formAttachmentFormItem
							.getAttachmentType()
							&& _formAttachmentFormItem.isVoicePlaying()) {
						// fake click voice attachment play imageView container
						// relaticeLayout
						_formAttachmentFormItem
								.fakeClickVoiceAttachmentPlayImgViewContainerRelativeLayout();

						// break immediately
						break;
					}
				}

				// begin to record voice
				Log.d(LOG_TAG, "Begin to record voice, move to cancel");

				// update toggle audio recording button title
				((Button) toggleAudioRecordingButton)
						.setText(R.string.naa_toggleAudioRecording_button_pressed_title);

				// clear audio recording duration
				_mAudioRecordingDuration = 0;

				// schedule audio recording chronometer timer task after 300
				// milliseconds and process every next one second
				AUDIORECORDING_TIMER.schedule(
						_mAudioRecordingChronometerTimerTask = new TimerTask() {

							@Override
							public void run() {
								// increase audio recording duration step
								_mAudioRecordingDuration++;
							}

						}, 300, MILLISECONDS_PER_SECOND);

				// schedule audio recording amplitude timer task immediately and
				// process every next 200 milliseconds
				AUDIORECORDING_TIMER.schedule(
						_mAudioRecordingAmplitudeTimerTask = new TimerTask() {

							@Override
							public void run() {
								// handle on UI thread with handle
								UPDATE_RECORDINGVOICEAMP_IMGVIEW_IMAGE_HANDLE
										.postDelayed(new Runnable() {

											@Override
											public void run() {
												// get and check recording audio
												// amplitude
												switch ((int) AudioUtils
														.getRecordingAmplitude()) {
												case 0:
												case 1:
													// step 1(default)
													VOICERECORD_TOAST
															.updateRecordingVoiceAmpImage(R.drawable.img_naa_voicerecord_recordingvoiceamp_1);
													break;

												case 2:
												case 3:
													// step 2
													VOICERECORD_TOAST
															.updateRecordingVoiceAmpImage(R.drawable.img_naa_voicerecord_recordingvoiceamp_2);
													break;

												case 4:
												case 5:
													// step 3
													VOICERECORD_TOAST
															.updateRecordingVoiceAmpImage(R.drawable.img_naa_voicerecord_recordingvoiceamp_3);
													break;

												case 6:
												case 7:
													// step 4
													VOICERECORD_TOAST
															.updateRecordingVoiceAmpImage(R.drawable.img_naa_voicerecord_recordingvoiceamp_4);
													break;

												case 8:
												case 9:
													// step 5
													VOICERECORD_TOAST
															.updateRecordingVoiceAmpImage(R.drawable.img_naa_voicerecord_recordingvoiceamp_5);
													break;

												case 10:
												case 11:
													// step 6
													VOICERECORD_TOAST
															.updateRecordingVoiceAmpImage(R.drawable.img_naa_voicerecord_recordingvoiceamp_6);
													break;

												default:
													// step 7 and more(max)
													VOICERECORD_TOAST
															.updateRecordingVoiceAmpImage(R.drawable.img_naa_voicerecord_recordingvoiceamp_7);
													break;
												}
											}

										}, 0);

							}

						}, 0, 200);

				// set voice record toast tip text and show
				VOICERECORD_TOAST
						.setTipText(R.string.naa_voiceRecord_recording)
						.showRecording();

				// start record voice
				_mAudioRecordingFilePath = AudioUtils
						.startRecordAudio(_mLoginUser.getName());
				break;

			case MotionEvent.ACTION_MOVE:
				// get and check axis y
				if (0 >= _axisY) {
					// update toggle audio recording button background image as
					// pressed
					toggleAudioRecordingButton
							.setBackgroundResource(R.drawable.img_naa_textvoiceinput_btn_pressed_bg);

					Log.d(LOG_TAG, "Will you release to cancel");

					// set voice record toast tip text and show
					VOICERECORD_TOAST.setTipText(
							R.string.naa_voiceRecord_cancelRecord)
							.showCancelRecord();
				} else {
					Log.d(LOG_TAG, "Continue recording, move to cancel");

					// set voice record toast tip text and show
					VOICERECORD_TOAST.setTipText(
							R.string.naa_voiceRecord_recording).showRecording();
				}
				break;

			case MotionEvent.ACTION_UP:
				// stop recording
				// recover toggle audio recording button title
				((Button) toggleAudioRecordingButton)
						.setText(R.string.naa_toggleAudioRecording_button_normal_title);

				// recover toggle audio recording button background image
				toggleAudioRecordingButton
						.setBackgroundResource(R.drawable.naa_textvoiceinput_btn_bg);

				// cancel audio recording chronometer and amplitude timer task
				_mAudioRecordingChronometerTimerTask.cancel();
				_mAudioRecordingAmplitudeTimerTask.cancel();

				// cancel voice record toast
				VOICERECORD_TOAST.cancel();

				// stop record voice
				AudioUtils.stopRecordAudio();

				// get and check axis y
				if (0 >= _axisY) {
					Log.d(LOG_TAG, "Voice recording canceled");

					// remove the voice record file
					//
				} else {
					Log.d(LOG_TAG, "Voice recording finish");

					// check audio recording duration
					if (0 == _mAudioRecordingDuration) {
						Log.w(LOG_TAG, "Audio recording duration too short");

						// set voice record toast tip text and show
						VOICERECORD_TOAST
								.setTipText(
										R.string.naa_voiceRecord_voiceDuration_tooShort)
								.showRecordTooShort();
					} else {
						// generate voice attachment info with file path and
						// duration
						Map<String, Object> _voiceAttachmentInfo = new HashMap<String, Object>();
						_voiceAttachmentInfo
								.put(NAAFormVoiceAttachmentInfoDataKeys.ATTACHMENT_FILEPATH,
										_mAudioRecordingFilePath);
						_voiceAttachmentInfo
								.put(NAAFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_DURATION,
										_mAudioRecordingDuration);

						// get the record voice and set as voice attachment,
						// then add to form attachment form linearLayout
						addNAAFormAttachmentFormItem(
								TaskFormAttachmentType.VOICE_ATTACHMENT,
								_voiceAttachmentInfo,
								new NAAFormVoiceAttachmentFormItemOnClickListener());
					}
				}
				break;
			}

			return false;
		}

		// inner class
		// new approve application voice attachment voice record toast
		class NAAVoiceAttachmentVoiceRecordToast extends CTToast {

			// new approve application voice attachment voice record tip
			// textView, cancel record, record too short imageView, recording
			// imageView parent linearLayout and recording voice amp imageView
			private TextView _mTipTextView;
			private ImageView _mCancelRecord6RecordTooShortImageView;
			private LinearLayout _mRecordingImgViewParentLinearLayout;
			private ImageView _mRecordingVoiceAmpImageView;

			public NAAVoiceAttachmentVoiceRecordToast(Context context) {
				super(context, R.layout.naa_voice_record_toast_layout);

				// get new approve application voice attachment voice record tip
				// textView, cancel record, record too short imageView,
				// recording imageView parent linearLayout and recording voice
				// amp imageView
				_mTipTextView = (TextView) _mContentView
						.findViewById(R.id.naavr_tip_textView);
				_mCancelRecord6RecordTooShortImageView = (ImageView) _mContentView
						.findViewById(R.id.naavr_cancelRecord6RecordTooShort_imageView);
				_mRecordingImgViewParentLinearLayout = (LinearLayout) _mContentView
						.findViewById(R.id.naavr_recordingImageView_parent_linearLayout);
				_mRecordingVoiceAmpImageView = (ImageView) _mContentView
						.findViewById(R.id.naavr_recordingVoiceAmp_imageView);

				// set duration(always) and gravity
				setDuration(Integer.MAX_VALUE);
				setGravity(Gravity.CENTER, 0, 0);
			}

			// set voice record tip textView text
			public NAAVoiceAttachmentVoiceRecordToast setTipText(int resId) {
				// set tip textView text
				_mTipTextView.setText(resId);

				return this;
			}

			// show voice record recording
			public void showRecording() {
				// update duration
				setDuration(Integer.MAX_VALUE);

				// update tip textView background
				_mTipTextView.setBackgroundColor(Color.TRANSPARENT);

				// show recording imageView parent linearLayout and hide cancel
				// record or record too short imageView if needed
				if (View.VISIBLE != _mRecordingImgViewParentLinearLayout
						.getVisibility()) {
					_mRecordingImgViewParentLinearLayout
							.setVisibility(View.VISIBLE);
				}
				if (View.VISIBLE == _mCancelRecord6RecordTooShortImageView
						.getVisibility()) {
					_mCancelRecord6RecordTooShortImageView
							.setVisibility(View.GONE);
				}

				// show voice record toast
				show();
			}

			// update recording voice amplitude image
			public void updateRecordingVoiceAmpImage(int resId) {
				// update recording voice amplitude imageView image
				_mRecordingVoiceAmpImageView.setImageResource(resId);
			}

			// show voice record cancel record
			public void showCancelRecord() {
				// update tip textView background
				_mTipTextView
						.setBackgroundResource(R.drawable.naa_voicerecord_cancelrecord_tip_textview_bg);

				// show cancel record or record too short image view and hide
				// recording imageView parent linearLayout if needed
				if (View.VISIBLE != _mCancelRecord6RecordTooShortImageView
						.getVisibility()) {
					_mCancelRecord6RecordTooShortImageView
							.setVisibility(View.VISIBLE);
				}
				if (View.VISIBLE == _mRecordingImgViewParentLinearLayout
						.getVisibility()) {
					_mRecordingImgViewParentLinearLayout
							.setVisibility(View.GONE);
				}

				// set cancel record imageView image
				_mCancelRecord6RecordTooShortImageView
						.setImageResource(R.drawable.img_naa_voicerecord_cancelrecord);

				// show voice record toast
				show();
			}

			// show voice record record too short
			public void showRecordTooShort() {
				// update duration
				setDuration(LENGTH_TRANSIENT);

				// update tip textView background
				_mTipTextView.setBackgroundColor(Color.TRANSPARENT);

				// show cancel record or record too short image view and hide
				// recording imageView parent linearLayout if needed
				if (View.VISIBLE != _mCancelRecord6RecordTooShortImageView
						.getVisibility()) {
					_mCancelRecord6RecordTooShortImageView
							.setVisibility(View.VISIBLE);
				}
				if (View.VISIBLE == _mRecordingImgViewParentLinearLayout
						.getVisibility()) {
					_mRecordingImgViewParentLinearLayout
							.setVisibility(View.GONE);
				}

				// set record too short imageView image
				_mCancelRecord6RecordTooShortImageView
						.setImageResource(R.drawable.img_naa_voicerecord_voicetooshort);

				// show voice record toast
				show();
			}

		}

	}

	// new approve application note input editText text watcher
	class NAANoteInputEditTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// get note send button
			Button _noteSendButton = (Button) findViewById(R.id.naa_note_send_button);

			// check editable and then enable or disable note send button
			if (null == s || "".equalsIgnoreCase(s.toString())) {
				_noteSendButton.setEnabled(false);
			} else {
				_noteSendButton.setEnabled(true);
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

	// new approve application note input editText on touch listener
	class NAANoteInputEditTextOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// check more plus input parent relativeLayout visibility
			if (View.VISIBLE == _mMorePlusInputParentRelativeLayout
					.getVisibility()) {
				// hide more plus input parent relativeLayout
				_mMorePlusInputParentRelativeLayout.setVisibility(View.GONE);
			}

			return false;
		}

	}

	// new approve application note send button on click listener
	class NAANoteSendBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// get the ready to send note and set as text attachment, then add
			// to form attachment form linearLayout
			addNAAFormAttachmentFormItem(
					TaskFormAttachmentType.TEXT_ATTACHMENT,
					_mNoteInputEditText.getText(),
					new NAAFormTextAttachmentFormItemOnClickListener());

			// clear note input editText text
			_mNoteInputEditText.setText("");
		}

	}

	// new approve application more plus input list adapter
	class NAAMorePlusInputListAdapter extends CTListAdapter {

		// new approve application more plus input list adapter keys
		private static final String MOREPLUS_INPUT_ITEM_LABEL = "moreplus_input_item_label";
		private static final String MOREPLUS_INPUT_ITEM_ICON = "moreplus_input_item_icon";

		public NAAMorePlusInputListAdapter(Context context,
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
			// image button
			if (view instanceof ImageButton) {
				try {
					// define item data map and convert item data to map
					@SuppressWarnings("unchecked")
					Map<String, Object> _itemDataMap = (Map<String, Object>) _itemData;

					// set image button attributes
					((ImageButton) view)
							.setImageResource((Integer) _itemDataMap
									.get(NAAMorePlusInputListAdapterIconItemDataKey.MOREPLUS_INPUT_ITEM_ICON_IMAGE));
					((ImageButton) view)
							.setOnClickListener((OnClickListener) _itemDataMap
									.get(NAAMorePlusInputListAdapterIconItemDataKey.MOREPLUS_INPUT_ITEM_ICON_ONCLICKLISTENER));
				} catch (Exception e) {
					e.printStackTrace();

					Log.e(LOG_TAG,
							"Convert item data to map error, item data = "
									+ _itemData);
				}
			}
			// text view
			else if (view instanceof TextView) {
				// set text view text
				((TextView) view).setText(_itemData.toString());
			}
		}

		// inner class
		// new approve application more plus input list adapter item icon data
		// keys
		class NAAMorePlusInputListAdapterIconItemDataKey {

			// more plus input item icon image and on click listener
			private static final String MOREPLUS_INPUT_ITEM_ICON_IMAGE = "moreplus_input_item_icon_image";
			private static final String MOREPLUS_INPUT_ITEM_ICON_ONCLICKLISTENER = "moreplus_input_item_icon_onclicklistener";

		}

	}

	// new approve application more plus photos input item icon image button on
	// click listener
	class NAAMorePlusPhotosInputItemIconImgBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// select photo
			// define get photo intent
			Intent _getPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);

			// set image file type and category
			_getPhotoIntent.setType("image/*");
			_getPhotoIntent.addCategory(Intent.CATEGORY_OPENABLE);

			// start get photo activity for photo selecting
			startActivityForResult(
					Intent.createChooser(
							_getPhotoIntent,
							getResources()
									.getString(
											R.string.naa_morePlus_photosInput_photosSelect_title)),
					NewApproveApplicationRequestCode.NAA_SELECTPHOTO_REQCODE);
		}

	}

	// new approve application more plus camera input item icon image button on
	// click listener
	class NAAMorePlusCameraInputItemIconImgBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// take photo
			try {
				// define and initialize the captured photo saved file name
				StringBuilder _capturedPhotoSavedFileName = new StringBuilder();
				_capturedPhotoSavedFileName.append(System.currentTimeMillis())
						.append(".jpg");

				// check external writeable environment
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Log.e(LOG_TAG,
							"SD card is not avaiable/writeable right now.");
				} else {
					String _filePathName = Environment
							.getExternalStorageDirectory().getPath()
							+ "/DCIM/iApprove/";
					File _filePath = new File(_filePathName);
					File _file = new File(_filePathName
							+ _capturedPhotoSavedFileName);
					if (!_filePath.exists()) {
						Log.d(LOG_TAG, "Create the path:" + _filePathName);

						// make dir
						_filePath.mkdir();
					}
					if (!_file.exists()) {
						Log.d(LOG_TAG, "Create the file:"
								+ _capturedPhotoSavedFileName);

						// create file
						_file.createNewFile();

						// get capture photo file path
						_mCaptureOrSelectPhotoFilePath = _file
								.getAbsolutePath();
					}

					// define capture photo intent
					Intent _capturePhotoIntent = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);

					// put captured photo output path
					_capturePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(_file));

					// start capture photo activity
					startActivityForResult(
							_capturePhotoIntent,
							NewApproveApplicationRequestCode.NAA_TAKEPHOTO_REQCODE);
				}
			} catch (IOException e) {
				Log.e(LOG_TAG,
						"Captured photo error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}

	}

	// new approve application more plus applications input item icon image
	// button on click listener
	class NAAMorePlusApplicationsInputItemIconImgBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG,
					"Click more plus applications input item image button = "
							+ v);

			//
		}

	}

}
