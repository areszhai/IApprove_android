package com.futuo.iapprove.tab7tabcontent.newapproveapplication.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentUris;
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
import com.futuo.iapprove.form.FormTypeBean;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormTypes.FormType;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationActivity.NewApproveApplicationExtraData;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.form.EnterpriseFormListActivity.EnterpriseFormExtraData;
import com.richitec.commontoolkit.user.UserManager;

public class EnterpriseFormTypeListActivity extends
		EnterpriseFormTypeAndFormListActivity {

	// new approve application submit contact
	private ABContactBean _mSubmitContact;

	// enterprise form type list cursor adapter
	private EnterpriseFormTypeListCursorAdapter _mEnterpriseFormTypeListCursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.enterprise_form_type_list_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// check the data
		if (null != _extraData) {
			// get the new approve application submit contact
			_mSubmitContact = (ABContactBean) _extraData
					.getSerializable(NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACT);
		}

		// set subViews
		// set title
		setTitle(IAUserExtension.getUserLoginEnterpriseAbbreviation(UserManager
				.getInstance().getUser()));

		// set cancel bar button item as right bar button item
		setRightBarButtonItem(new IApproveBarButtonItem(this,
				R.string.eft6f_cancel_barButtonItem_title,
				_mCancelBarBtnItemOnClickListener));

		// get user enterprise form type listView
		ListView _enterpriseFormTypeListView = (ListView) findViewById(R.id.eft_listView);

		// set user enterprise form type list cursor adapter
		_enterpriseFormTypeListView
				.setAdapter(_mEnterpriseFormTypeListCursorAdapter = new EnterpriseFormTypeListCursorAdapter(
						this,
						R.layout.enterprise_form_type_layout,
						getContentResolver()
								.query(ContentUris
										.withAppendedId(
												FormType.ENTERPRISE_CONTENT_URI,
												IAUserExtension
														.getUserLoginEnterpriseId(UserManager
																.getInstance()
																.getUser())),
										null, null, null, null),
						new String[] { EnterpriseFormTypeListCursorAdapter.ENTERPRISE_FORMTYPE_NAME },
						new int[] { R.id.eft_name_textView }));

		// set user enterprise form type list on item click listener
		_enterpriseFormTypeListView
				.setOnItemClickListener(new EnterpriseFormTypeListOnItemClickListener());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// check result code
		switch (resultCode) {
		case RESULT_OK:
			// check request code
			switch (requestCode) {
			case EnterpriseFormTypeListRequestCode.ENTERPRISEFORMTYPELIST_ITEMONCLICK_REQCODE:
				// finish enterprise form type list activity
				finish();
				break;
			}
			break;

		default:
			// nothing to do
			break;
		}
	}

	// inner class
	// enterprise form type list request code
	static class EnterpriseFormTypeListRequestCode {

		// enterprise form type list item on click request code
		private static final int ENTERPRISEFORMTYPELIST_ITEMONCLICK_REQCODE = 200;

	}

	// enterprise form type list cursor adapter
	class EnterpriseFormTypeListCursorAdapter extends
			EnterpriseFormTypeAndFormListCursorAdapter {

		private final String LOG_TAG = EnterpriseFormTypeListCursorAdapter.class
				.getCanonicalName();

		// enterprise form type list cursor adapter data keys
		private static final String ENTERPRISE_FORMTYPE_NAME = "enterprise_formtype_name";

		public EnterpriseFormTypeListCursorAdapter(Context context,
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
				// get enterprise form type bean and append to data list
				data.add(new FormTypeBean(cursor));
			} else {
				Log.e(LOG_TAG,
						"Query user login enterprise all form types error, cursor = "
								+ cursor);
			}
		}

		@Override
		protected Map<String, ?> recombinationData(String dataKey,
				Object dataObject) {
			// define return data map and the data value for key in data object
			Map<String, String> _dataMap = new HashMap<String, String>();
			String _dataValue = null;

			// check data object and convert to enterprise form type object
			try {
				// convert data object to enterprise form type
				FormTypeBean _enterpriseFormTypeObject = (FormTypeBean) dataObject;

				// check data key and get data value for it
				if (ENTERPRISE_FORMTYPE_NAME.equalsIgnoreCase(dataKey)) {
					// enterprise form type name
					_dataValue = _enterpriseFormTypeObject.getTypeName();
				} else {
					Log.e(LOG_TAG, "Recombination data error, data key = "
							+ dataKey + " and data object = " + dataObject);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Convert data object to enterprise form type bean object error, data = "
								+ dataObject);

				e.printStackTrace();
			}

			// put data value to map and return
			_dataMap.put(dataKey, _dataValue);
			return _dataMap;
		}

	}

	// enterprise form type list on item click listener
	class EnterpriseFormTypeListOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> formTypeListView,
				View formTypeContentView, int position, long id) {
			// define enterprise form extra data map
			Map<String, Object> _extraMap = new HashMap<String, Object>();

			// get the clicked form type
			FormTypeBean _clickedFormType = (FormTypeBean) _mEnterpriseFormTypeListCursorAdapter
					.getDataList().get(position);

			// put enterprise form type id and name to extra data map as param
			_extraMap.put(EnterpriseFormExtraData.ENTERPRISE_FROM_TYPE_ID,
					_clickedFormType.getTypeId());
			_extraMap.put(EnterpriseFormExtraData.ENTERPRISE_FROM_TYPE_NAME,
					_clickedFormType.getTypeName());

			// put the new approve application submit contact bean to extra data
			// map as param if needed
			if (null != _mSubmitContact) {
				_extraMap
						.put(NewApproveApplicationExtraData.NEW_APPROVEAPPLICATION_SUBMIT_CONTACT,
								_mSubmitContact);
			}

			// go to user enterprise form list activity with extra data map
			pushActivityForResult(
					EnterpriseFormListActivity.class,
					_extraMap,
					EnterpriseFormTypeListRequestCode.ENTERPRISEFORMTYPELIST_ITEMONCLICK_REQCODE);
		}

	}

}
