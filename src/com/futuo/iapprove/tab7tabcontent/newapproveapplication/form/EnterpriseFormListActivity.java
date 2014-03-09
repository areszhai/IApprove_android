package com.futuo.iapprove.tab7tabcontent.newapproveapplication.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.futuo.iapprove.customwidget.IApproveBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveImageBarButtonItem;
import com.futuo.iapprove.form.FormBean;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.Forms.Form;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationActivity;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationActivity.NewApproveApplicationExtraData;
import com.richitec.commontoolkit.user.UserManager;

public class EnterpriseFormListActivity extends
		EnterpriseFormTypeAndFormListActivity {

	// form type id
	private Long _mFormTypeId;

	// new approve application submit contact
	private ABContactBean _mSubmitContact;

	// enterprise form list cursor adapter
	private EnterpriseFormListCursorAdapter _mEnterpriseFormListCursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.enterprise_form_list_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define the enterprise form list activity is new task flag, enterprise
		// form type name
		boolean _enterpriseFormListActivityNewTask = false;
		String _enterpriseFormTypeName = "";

		// check the data
		if (null != _extraData) {
			// get the enterprise form list activity is new task flag, user
			// enterprise form type id, name and the new approve application
			// submit contact
			_enterpriseFormListActivityNewTask = _extraData
					.getBoolean(EnterpriseFormExtraData.ENTERPRISE_FROMLISTACTIVITY_NEWTASK);
			_mFormTypeId = _extraData
					.getLong(EnterpriseFormExtraData.ENTERPRISE_FROM_TYPE_ID);
			_enterpriseFormTypeName = _extraData
					.getString(EnterpriseFormExtraData.ENTERPRISE_FROM_TYPE_NAME);
			_mSubmitContact = (ABContactBean) _extraData
					.getSerializable(NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACTS);
		}

		// set subViews
		// set title
		setTitle(_enterpriseFormTypeName);

		// check the enterprise form list activity is new task flag and set left
		// or right bar button item
		if (_enterpriseFormListActivityNewTask) {
			// set cancel bar button item as right bar button item
			setRightBarButtonItem(new IApproveBarButtonItem(this,
					R.string.eft6f_cancel_barButtonItem_title,
					_mCancelBarBtnItemOnClickListener));
		} else {
			// set navigation back image bar button item as left image bar
			// button item
			setLeftBarButtonItem(new IApproveImageBarButtonItem(this,
					R.drawable.img_nav_backbarbtnitem,
					_mBackBarBtnItemOnClickListener));
		}

		// get user enterprise form listView
		ListView _enterpriseFormListView = (ListView) findViewById(R.id.ef_listView);

		// set user enterprise form list cursor adapter
		_enterpriseFormListView
				.setAdapter(_mEnterpriseFormListCursorAdapter = new EnterpriseFormListCursorAdapter(
						this,
						R.layout.enterprise_form_layout,
						getContentResolver().query(
								Form.FORMS_CONTENT_URI,
								null,
								Form.ENTERPRISE_FORMS_WITHTYPEID_CONDITION,
								new String[] {
										IAUserExtension
												.getUserLoginEnterpriseId(
														UserManager
																.getInstance()
																.getUser())
												.toString(),
										_mFormTypeId.toString() }, null),
						new String[] { EnterpriseFormListCursorAdapter.ENTERPRISE_FORM_NAME },
						new int[] { R.id.ef_name_textView }));

		// set user enterprise form list on item click listener
		_enterpriseFormListView
				.setOnItemClickListener(new EnterpriseFormListOnItemClickListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// check result code
		switch (resultCode) {
		case RESULT_OK:
			// check request code
			switch (requestCode) {
			case EnterpriseFormListRequestCode.ENTERPRISEFORMLIST_ITEMONCLICK_REQCODE:
				// popup enterprise form list activity with result
				popActivityWithResult(RESULT_OK, null);
				break;
			}
			break;

		default:
			// nothing to do
			break;
		}
	}

	// inner class
	// enterprise form extra data constant
	public static final class EnterpriseFormExtraData {

		// enterprise form list activity new task flag
		public static final String ENTERPRISE_FROMLISTACTIVITY_NEWTASK = "enterprise_formlistactivity_newtask";

		// enterprise form type id and name
		public static final String ENTERPRISE_FROM_TYPE_ID = "enterprise_form_type_id";
		public static final String ENTERPRISE_FROM_TYPE_NAME = "enterprise_form_type_name";

	}

	// enterprise form list request code
	static class EnterpriseFormListRequestCode {

		// enterprise form list item on click request code
		private static final int ENTERPRISEFORMLIST_ITEMONCLICK_REQCODE = 300;

	}

	// enterprise form list cursor adapter
	class EnterpriseFormListCursorAdapter extends
			EnterpriseFormTypeAndFormListCursorAdapter {

		private final String LOG_TAG = EnterpriseFormListCursorAdapter.class
				.getCanonicalName();

		// enterprise form list cursor adapter data keys
		private static final String ENTERPRISE_FORM_NAME = "enterprise_form_name";

		public EnterpriseFormListCursorAdapter(Context context,
				int itemsLayoutResId, Cursor c, String[] dataKeys,
				int[] itemsComponentResIds) {
			super(context, itemsLayoutResId, c, dataKeys, itemsComponentResIds);
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
				// get enterprise form bean and append to data list
				data.add(new FormBean(cursor));
			} else {
				Log.e(LOG_TAG,
						"Query user login enterprise all forms error, cursor = "
								+ cursor);
			}
		}

		@Override
		protected Map<String, ?> recombinationData(String dataKey,
				Object dataObject) {
			// define return data map and the data value for key in data object
			Map<String, String> _dataMap = new HashMap<String, String>();
			String _dataValue = null;

			// check data object and convert to enterprise form object
			try {
				// convert data object to enterprise form
				FormBean _enterpriseFormObject = (FormBean) dataObject;

				// check data key and get data value for it
				if (ENTERPRISE_FORM_NAME.equalsIgnoreCase(dataKey)) {
					// enterprise form name
					_dataValue = _enterpriseFormObject.getFormName();
				} else {
					Log.e(LOG_TAG, "Recombination data error, data key = "
							+ dataKey + " and data object = " + dataObject);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Convert data object to enterprise form bean object error, data = "
								+ dataObject);

				e.printStackTrace();
			}

			// put data value to map and return
			_dataMap.put(dataKey, _dataValue);
			return _dataMap;
		}

	}

	// enterprise form list on item click listener
	class EnterpriseFormListOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> formListView,
				View formContentView, int position, long id) {
			// define new approve application extra data map
			Map<String, Object> _extraMap = new HashMap<String, Object>();

			// get the clicked form
			FormBean _clickedForm = (FormBean) _mEnterpriseFormListCursorAdapter
					.getDataList().get(position);

			// put enterprise form type id, form id and name to extra data map
			// as param
			_extraMap.put(
					NewApproveApplicationExtraData.ENTERPRISE_FROM_TYPE_ID,
					_mFormTypeId);
			_extraMap.put(NewApproveApplicationExtraData.ENTERPRISE_FROM_ID,
					_clickedForm.getFormId());
			_extraMap.put(NewApproveApplicationExtraData.ENTERPRISE_FROM_NAME,
					_clickedForm.getFormName());

			// put the new approve application submit contact bean to extra data
			// map as param if needed
			if (null != _mSubmitContact) {
				_extraMap
						.put(NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACTS,
								_mSubmitContact);
			} else {
				_extraMap
						.put(NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACTS,
								_clickedForm.getFormDefaultSubmitContacts());
			}

			// go to new approve application activity with extra data map
			pushActivityForResult(
					NewApproveApplicationActivity.class,
					_extraMap,
					EnterpriseFormListRequestCode.ENTERPRISEFORMLIST_ITEMONCLICK_REQCODE);
		}

	}

}
