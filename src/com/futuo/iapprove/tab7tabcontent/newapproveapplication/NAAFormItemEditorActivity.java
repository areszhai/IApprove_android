package com.futuo.iapprove.tab7tabcontent.newapproveapplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.form.FormItemType;
import com.futuo.iapprove.tab7tabcontent.newapproveapplication.NewApproveApplicationActivity.NewApproveApplicationExtraData;
import com.futuo.iapprove.utils.DateStringUtils;

public class NAAFormItemEditorActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = NAAFormItemEditorActivity.class
			.getCanonicalName();

	// new approve application form item editor form item type and id
	private FormItemType _mEditorFormItemType;
	private Long _mEditorFormItemId;

	// input method manager
	private InputMethodManager _mInputMethodManager;

	// new approve application form item editor form item text, number, multiple
	// line textEdit, date picker, date display textView and spinner listView
	private EditText _mEditorFormItemEditText;
	private DatePicker _mEditorFormItemDatePicker;
	private TextView _mEditorFormItemDateDisplayTextView;
	private ListView _mEditorFormItemSpinnerListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.naa_form_item_editor_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define the editor form item name, selector content infos and info
		// value
		String _editorFormItemName = "";
		List<String> _editorFormItemSelectorContentInfos = null;
		String _editorFormItemInfoValue = "";

		// check the data
		if (null != _extraData) {
			// get the editor form item name, type, selector content infos, info
			// value and id
			_editorFormItemName = _extraData
					.getString(NAAFormItemEditorExtraData.NAA_FROMITEM_NAME);
			_mEditorFormItemType = (FormItemType) _extraData
					.getSerializable(NAAFormItemEditorExtraData.NAA_FROMITEM_TYPE);
			_editorFormItemSelectorContentInfos = _extraData
					.getStringArrayList(NAAFormItemEditorExtraData.NAA_FROMITEM_SELECTORCONTENT);
			_editorFormItemInfoValue = _extraData
					.getString(NAAFormItemEditorExtraData.NAA_FROMITEM_INFOVALUE);
			_mEditorFormItemId = _extraData
					.getLong(NAAFormItemEditorExtraData.NAA_FROMITEM_ID);
		}

		// get input method manager
		_mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// set subViews
		// set title
		setTitle(_editorFormItemName);

		// check the editor form item type and set right bar button item if
		// needed
		if (FormItemType.SPINNER != _mEditorFormItemType) {
			// set save text form item info bar button item as right bar button
			// item
			setRightBarButtonItem(new IApproveBarButtonItem(this,
					R.string.naafie_saveInfo_barButtonItem_title,
					new SaveTextFormItemInfoBarBtnItemOnClickListener()));
		}

		// check the editor form item type again and initialize editor form item
		// text, number, multiple line textEdit, date picker, date display
		// textView and spinner listView
		switch (_mEditorFormItemType) {
		case TEXTEDIT_TEXT:
		case TEXTEDIT_NUMBER:
		case TEXTAREA:
			// check the editor form item type and get editor form item text or
			// number or multiple line textView
			_mEditorFormItemEditText = (EditText) findViewById(FormItemType.TEXTAREA == _mEditorFormItemType ? R.id.naafie_multipleLine_editText
					: R.id.naafie_text6number_editText);

			// set its visible
			_mEditorFormItemEditText.setVisibility(View.VISIBLE);

			// set its input type if needed
			if (FormItemType.TEXTEDIT_NUMBER == _mEditorFormItemType) {
				_mEditorFormItemEditText
						.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			}

			// check editor form item info value and set its text
			if (null != _editorFormItemInfoValue) {
				_mEditorFormItemEditText.setText(_editorFormItemInfoValue);
			}
			break;

		case DATE:
			// get editor form item date relativeLayout, date picker and date
			// display textView
			RelativeLayout _editorFormItemDateRelativeLayout = (RelativeLayout) findViewById(R.id.naafie_date_relativeLayout);
			_mEditorFormItemDatePicker = (DatePicker) findViewById(R.id.naafie_datePicker);
			_mEditorFormItemDateDisplayTextView = (TextView) findViewById(R.id.naafie_dateDisplay_textView);

			// set date relativeLayout visible
			_editorFormItemDateRelativeLayout.setVisibility(View.VISIBLE);

			// check editor form item info value and initialize calendar
			Calendar _calendar = Calendar.getInstance(Locale.getDefault());
			if (null != _editorFormItemInfoValue
					&& !"".equalsIgnoreCase(_editorFormItemInfoValue)) {
				// get, check editor form item info value date and set calendar
				// time in millis
				Date _editorFormItemInfoValueDate = DateStringUtils
						.shortDateString2Date(_editorFormItemInfoValue);
				if (null != _editorFormItemInfoValueDate) {
					_calendar.setTimeInMillis(_editorFormItemInfoValueDate
							.getTime());
				}
			}

			// initialize date picker
			_mEditorFormItemDatePicker
					.init(_calendar.get(Calendar.YEAR),
							_calendar.get(Calendar.MONTH),
							_calendar.get(Calendar.DAY_OF_MONTH),
							new NAAFormItemEditorFormItemDatePickerOnDateChangedListener());

			// set date display textView text first
			_mEditorFormItemDateDisplayTextView.setText(new SimpleDateFormat(
					"yyyy-MM-dd", Locale.getDefault()).format(_calendar
					.getTime()));
			break;

		case SPINNER:
			// get editor form item spinner listView
			_mEditorFormItemSpinnerListView = (ListView) findViewById(R.id.naafie_spinner_listView);

			// set its visible
			_mEditorFormItemSpinnerListView.setVisibility(View.VISIBLE);

			// check editor form item selector content infos
			if (null != _editorFormItemSelectorContentInfos) {
				// set its adapter
				_mEditorFormItemSpinnerListView
						.setAdapter(new NAAFormItemEditorFormItemSpinnerArrayAdapter(
								this,
								R.layout.naa_formitemeditor_spinner_item_layout,
								_editorFormItemSelectorContentInfos));

				// set its on item click listener
				_mEditorFormItemSpinnerListView
						.setOnItemClickListener(new NAAFormItemEditorFormItemSpinnerListViewOnItemClickListener());

				// set its choice mode
				_mEditorFormItemSpinnerListView
						.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				// check editor form item info value and set its default
				// selected item
				if (null != _editorFormItemInfoValue) {
					// get and check the index of editor form item info value in
					// selector content infos
					int _index = _editorFormItemSelectorContentInfos
							.indexOf(_editorFormItemInfoValue);
					if (-1 != _index) {
						// set the default selected item checked
						_mEditorFormItemSpinnerListView.setItemChecked(_index,
								true);
					}
				}
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// set new approve application form item editor form item text, number,
		// multiple line textEdit as focus
		if (null != _mEditorFormItemEditText) {
			// set input editText focusable
			_mEditorFormItemEditText.setFocusable(true);
			_mEditorFormItemEditText.setFocusableInTouchMode(true);
			_mEditorFormItemEditText.requestFocus();

			// show soft input after 250 milliseconds
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					_mInputMethodManager.showSoftInput(
							_mEditorFormItemEditText, 0);
				}

			}, 250);
		}
	}

	@Override
	protected void onBackBarButtonItemClick(View backBarBtnItem) {
		// hide soft input
		_mInputMethodManager.hideSoftInputFromWindow(
				backBarBtnItem.getWindowToken(), 0);

		super.onBackBarButtonItemClick(backBarBtnItem);
	}

	// inner class
	// new approve application form item editor extra data constant
	public static final class NAAFormItemEditorExtraData {

		// new approve application form item name, type, selector content, info
		// value and id
		public static final String NAA_FROMITEM_NAME = "naa_formitem_name";
		public static final String NAA_FROMITEM_TYPE = "naa_formitem_type";
		public static final String NAA_FROMITEM_SELECTORCONTENT = "naa_formitem_selectorcontent";
		public static final String NAA_FROMITEM_INFOVALUE = "naa_formitem_infovalue";
		public static final String NAA_FROMITEM_ID = "naa_formitem_id";

	}

	// save text form item info bar button item on click listener
	class SaveTextFormItemInfoBarBtnItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Save editor form item whose type = "
					+ _mEditorFormItemType + " edit info value");

			// hide soft input
			_mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

			// define new approve application extra data map
			Map<String, Object> _extraMap = new HashMap<String, Object>();

			// put editor form item need to update form item id and info value
			// to extra data map as param
			_extraMap
					.put(NewApproveApplicationExtraData.NAA_FORMITEM_EDITOR_NEED2UPDATEINFO_FORMITEM_ID,
							_mEditorFormItemId);
			_extraMap
					.put(NewApproveApplicationExtraData.NAA_FORMITEM_EDITOR_NEED2UPDATEINFO,
							FormItemType.DATE == _mEditorFormItemType ? _mEditorFormItemDateDisplayTextView
									.getText().toString()
									: _mEditorFormItemEditText.getText()
											.toString());

			// pop new approve application form item editor activity with result
			popActivityWithResult(RESULT_OK, _extraMap);
		}

	}

	// new approve application form item editor form item date picker on date
	// changed listener
	class NAAFormItemEditorFormItemDatePickerOnDateChangedListener implements
			OnDateChangedListener {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// update date display textView
			_mEditorFormItemDateDisplayTextView.setText(year + "-"
					+ String.format("%02d", monthOfYear + 1) + "-"
					+ String.format("%02d", dayOfMonth));
		}

	}

	// new approve application form item editor form item spinner array adapter
	class NAAFormItemEditorFormItemSpinnerArrayAdapter extends
			ArrayAdapter<String> {

		public NAAFormItemEditorFormItemSpinnerArrayAdapter(Context context,
				int resource, List<String> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// get convert view
			View _convertView = super.getView(position, convertView, parent);

			// check count, position and set convert view background
			if (1 == getCount()) {
				_convertView
						.setBackgroundResource(R.drawable.naa_formitemeditor_spinnerlistview_single_item_bg);
			} else if (1 < getCount()) {
				if (0 == position) {
					_convertView
							.setBackgroundResource(R.drawable.naa_formitemeditor_spinnerlistview_multipletop_item_bg);
				} else if (getCount() - 1 == position) {
					_convertView
							.setBackgroundResource(R.drawable.naa_formitemeditor_spinnerlistview_multiplebottom_item_bg);
				} else {
					_convertView
							.setBackgroundResource(R.drawable.naa_formitemeditor_spinnerlistview_multiplemiddle_item_bg);
				}
			}

			return _convertView;
		}

	}

	// new approve application form item editor form item spinner list view on
	// item click listener
	class NAAFormItemEditorFormItemSpinnerListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> spinnerListView,
				View checkedTextView, int position, long id) {
			// set the selected item checked
			((ListView) spinnerListView).setItemChecked(position, true);

			// define new approve application extra data map
			Map<String, Object> _extraMap = new HashMap<String, Object>();

			// put editor form item need to update form item id and info value
			// to extra data map as param
			_extraMap
					.put(NewApproveApplicationExtraData.NAA_FORMITEM_EDITOR_NEED2UPDATEINFO_FORMITEM_ID,
							_mEditorFormItemId);
			_extraMap
					.put(NewApproveApplicationExtraData.NAA_FORMITEM_EDITOR_NEED2UPDATEINFO,
							((CheckedTextView) checkedTextView).getText());

			// pop new approve application form item editor activity with result
			popActivityWithResult(RESULT_OK, _extraMap);
		}

	}

}
