package com.futuo.iapprove.tab7tabcontent.newapproveapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormAttachmentType;
import com.futuo.iapprove.form.FormItemBean;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems.FormItem;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NAAFormItemEditorActivity.NAAFormItemEditorExtraData;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationActivity.NAAMorePlusInputListAdapter.NAAMorePlusInputListAdapterIconItemDataKey;
import com.futuo.iapprove.utils.AudioUtils;
import com.futuo.iapprove.utils.CalculateStringUtils;
import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.customcomponent.CTToast;
import com.richitec.commontoolkit.user.UserManager;

public class NewApproveApplicationActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = NewApproveApplicationActivity.class
			.getCanonicalName();

	// enterprise form type id and form id
	private Long _mFormTypeId;
	private Long _mFormId;

	// new approve application submit contact
	private ABContactBean _mSubmitContact;

	// enterprise form item form linearLayout
	private LinearLayout _mFormItemFormLinearLayout;

	// enterprise form item id(key) and form item form item view(value) map
	private Map<Long, EnterpriseFormItemFormItem> _mFormItemId7FormItemFormItemMap;

	// enterprise form attachment form parent frameLayout and form linearLayout
	private FrameLayout _mAttachmentFormParentFrameLayout;
	private LinearLayout _mAttachmentFormLinearLayout;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.new_approve_application_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define the enterprise form name
		String _enterpriseFormName = "";

		// check the data
		if (null != _extraData) {
			// get the user enterprise form type id, form id, name and the new
			// approve application submit contact
			_mFormTypeId = _extraData
					.getLong(NewApproveApplicationExtraData.ENTERPRISE_FROM_TYPE_ID);
			_mFormId = _extraData
					.getLong(NewApproveApplicationExtraData.ENTERPRISE_FROM_ID);
			_enterpriseFormName = _extraData
					.getString(NewApproveApplicationExtraData.ENTERPRISE_FROM_NAME);
			_mSubmitContact = (ABContactBean) _extraData
					.getSerializable(NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACT);
		}

		// get input method manager
		_mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// set subViews
		// set title
		setTitle(_enterpriseFormName);

		// set new approve application submit bar button item as right bar
		// button item
		setRightBarButtonItem(new IApproveImageBarButtonItem(this,
				R.drawable.img_naa6tdta_submitbarbtnitem,
				new NAASubmitBarBtnItemOnClickListener()));

		// submit contact
		// check submit contact
		if (null != _mSubmitContact) {
			// set default submit contact
			//
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
			}
			break;

		default:
			// nothing to do
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// stop play recorder audio if needed
		AudioUtils.stopPlayRecorderAudio();
	}

	// refresh enterprise form item form
	private void refreshFormItemForm() {
		// query the enterprise form all items
		Cursor _cursor = getContentResolver()
				.query(FormItem.FORMITEMS_CONTENT_URI,
						null,
						FormItem.ENTERPRISE_FORMITEMS_WITHFORMTYPEID7FORMID_CONDITION,
						new String[] {
								IAUserExtension.getUserLoginEnterpriseId(
										UserManager.getInstance().getUser())
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
						_formItemFormItem.setTag(_formItemCapitalFormItem);

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

	// inner class
	// new approve application extra data constant
	public static final class NewApproveApplicationExtraData {

		// new approve application enterprise form type id, form id, name and
		// submit contact bean
		public static final String ENTERPRISE_FROM_TYPE_ID = "enterprise_form_type_id";
		public static final String ENTERPRISE_FROM_ID = "enterprise_form_id";
		public static final String ENTERPRISE_FROM_NAME = "enterprise_form_name";
		public static final String NEW_APPROVEAPPLICATION_SUBMIT_CONTACT = "new_approveapplication_submit_contact";

		// new approve application form item editor need to update info value
		// form item id and update info value
		public static final String NAA_FORMITEM_EDITOR_NEED2UPDATEINFO_FORMITEM_ID = "naa_formitem_editor_need2updateinfo_formitem_id";
		public static final String NAA_FORMITEM_EDITOR_NEED2UPDATEINFO = "naa_formitem_editor_need2updateinfo";

	}

	// new approve application request code
	static class NewApproveApplicationRequestCode {

		// new approve application form item editor request code
		private static final int NAA_FORMITEM_EDITOR_REQCODE = 200;

	}

	// new approve application submit bar button item on click listener
	class NAASubmitBarBtnItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Click submit new approve application");

			//
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
					.getTag();
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

	// new approve application form text attachment form item on click listener
	class NAAFormTextAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG,
					"Form text attachment form item on click listener, view = "
							+ v);

			//
		}

	}

	// new approve application form image attachment form item on click listener
	class NAAFormImageAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG,
					"Form image attachment form item on click listener, view = "
							+ v);

			//
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
								.getTag(NAAFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH
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

	// add submit contact image button on click listener
	class AddSubmitContactImgBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Click add submit contact");

			//
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
						.startRecordAudio(UserManager.getInstance().getUser()
								.getName());
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
								.put(NAAFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH,
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
			Log.d(LOG_TAG, "Click more plus photos input item image button = "
					+ v);

			//
		}

	}

	// new approve application more plus camera input item icon image button on
	// click listener
	class NAAMorePlusCameraInputItemIconImgBtnOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Click more plus camera input item image button = "
					+ v);

			//
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
