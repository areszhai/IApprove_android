package com.futuo.iapprove.tab7tabcontent.newapproveapplication;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.customwidget.CommonFormSeparator;
import com.futuo.iapprove.customwidget.EnterpriseFormItemFormItem;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.form.FormItemBean;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems.FormItem;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NAAFormItemEditorActivity.NAAFormItemEditorExtraData;
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

		// set subViews
		// set title
		setTitle(_enterpriseFormName);

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

		// bind add submit contact button on click listener
		((ImageButton) findViewById(R.id.naa_add_submitContact_imageButton))
				.setOnClickListener(new AddSubmitContactImgBtnOnClickListener());

		//
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

	// refresh enterprise form item form
	private void refreshFormItemForm() {
		// // remove the enterprise form item form linearLayout all subViews
		// _mFormItemFormLinearLayout.removeAllViews();

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
						// check clickable and set enterprise form item form
						// item background
						if (_formItemFormItem.isClickable()) {
							_formItemFormItem
									.setBackgroundResource(R.drawable.enterprise_form_item_form_bottom_item_bg);
						}
					} else {
						// two and more
						// check clickable and set enterprise form item form
						// item background
						if (_formItemFormItem.isClickable()) {
							_formItemFormItem
									.setBackgroundResource(R.drawable.enterprise_form_item_form_bottom_item_bg);
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

	// add submit contact image button on click listener
	class AddSubmitContactImgBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Click add submit contact");

			//
		}

	}

}
