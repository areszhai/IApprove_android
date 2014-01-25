package com.futuo.iapprove.setting;

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
import com.futuo.iapprove.setting.UserProfileSettingActivity.UserProfileSettingExtraData;
import com.futuo.iapprove.utils.DateStringUtils;

public class UserProfileSettingFormItemEditorActivity extends
		IApproveNavigationActivity {

	private static final String LOG_TAG = UserProfileSettingFormItemEditorActivity.class
			.getCanonicalName();

	// user profile setting form item editor form item type
	private UserProfileSettingFormItemType _mEditorFormItemType;

	// input method manager
	private InputMethodManager _mInputMethodManager;

	// user profile setting form item editor form item text, number, email,
	// multiple line textEdit, date picker, date display textView and spinner
	// listView
	private EditText _mEditorFormItemEditText;
	private DatePicker _mEditorFormItemDatePicker;
	private TextView _mEditorFormItemDateDisplayTextView;
	private ListView _mEditorFormItemSpinnerListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.user_profile_setting_form_item_editor_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define the editor form item label, spinner content infos and info
		// value
		String _editorFormItemLabel = "";
		List<String> _editorFormItemSpinnerContentInfos = null;
		String _editorFormItemInfoValue = "";

		// check the data
		if (null != _extraData) {
			// get the editor form item label, type, spinner content infos and
			// info value
			_editorFormItemLabel = _extraData
					.getString(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_LABEL);
			_mEditorFormItemType = (UserProfileSettingFormItemType) _extraData
					.getSerializable(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_TYPE);
			_editorFormItemSpinnerContentInfos = _extraData
					.getStringArrayList(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_SPINNERCONTENT);
			_editorFormItemInfoValue = _extraData
					.getString(UserProfileSettingFormItemEditorExtraData.USERPROFILESETTING_FROMITEM_INFOVALUE);
		}

		// get input method manager
		_mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// set subViews
		// set title
		setTitle(_editorFormItemLabel);

		// check the editor form item type and set right bar button item if
		// needed
		if (UserProfileSettingFormItemType.SPINNER != _mEditorFormItemType) {
			// set save text form item info bar button item as right bar button
			// item
			setRightBarButtonItem(new IApproveBarButtonItem(this,
					R.string.upsfie_saveInfo_barButtonItem_title,
					new SaveTextFormItemInfoBarBtnItemOnClickListener()));
		}

		// check the editor form item type again and initialize editor form item
		// text, number, email, multiple line textEdit, date picker, date
		// display textView and spinner listView
		switch (_mEditorFormItemType) {
		case TEXTEDIT_TEXT:
		case TEXTEDIT_NUMBER:
		case TEXTEDIT_PHONE:
		case TEXTEDIT_EMAIL:
		case TEXTAREA:
			// check the editor form item type and get editor form item text or
			// number or email or multiple line textView
			_mEditorFormItemEditText = (EditText) findViewById(UserProfileSettingFormItemType.TEXTAREA == _mEditorFormItemType ? R.id.upsfie_multipleLine_editText
					: R.id.upsfie_text6number6email_editText);

			// set its visible
			_mEditorFormItemEditText.setVisibility(View.VISIBLE);

			// set its input type if needed
			if (UserProfileSettingFormItemType.TEXTEDIT_NUMBER == _mEditorFormItemType) {
				_mEditorFormItemEditText
						.setInputType(InputType.TYPE_CLASS_NUMBER
								| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			} else if (UserProfileSettingFormItemType.TEXTEDIT_PHONE == _mEditorFormItemType) {
				_mEditorFormItemEditText
						.setInputType(InputType.TYPE_CLASS_PHONE);
			} else if (UserProfileSettingFormItemType.TEXTEDIT_EMAIL == _mEditorFormItemType) {
				_mEditorFormItemEditText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			}

			// check editor form item info value and set its text
			if (null != _editorFormItemInfoValue) {
				_mEditorFormItemEditText.setText(_editorFormItemInfoValue);
			}
			break;

		case DATE:
			// get editor form item date relativeLayout, date picker and date
			// display textView
			RelativeLayout _editorFormItemDateRelativeLayout = (RelativeLayout) findViewById(R.id.upsfie_date_relativeLayout);
			_mEditorFormItemDatePicker = (DatePicker) findViewById(R.id.upsfie_datePicker);
			_mEditorFormItemDateDisplayTextView = (TextView) findViewById(R.id.upsfie_dateDisplay_textView);

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
							new UserProfileSettingFormItemEditorFormItemDatePickerOnDateChangedListener());

			// set date display textView text first
			_mEditorFormItemDateDisplayTextView.setText(new SimpleDateFormat(
					"yyyy-MM-dd", Locale.getDefault()).format(_calendar
					.getTime()));
			break;

		case SPINNER:
			// get editor form item spinner listView
			_mEditorFormItemSpinnerListView = (ListView) findViewById(R.id.upsfie_spinner_listView);

			// set its visible
			_mEditorFormItemSpinnerListView.setVisibility(View.VISIBLE);

			// check editor form item spinner content infos
			if (null != _editorFormItemSpinnerContentInfos) {
				// set its adapter
				_mEditorFormItemSpinnerListView
						.setAdapter(new UserProfileSettingFormItemEditorFormItemSpinnerArrayAdapter(
								this,
								R.layout.ups_formitemeditor_spinner_item_layout,
								_editorFormItemSpinnerContentInfos));

				// set its on item click listener
				_mEditorFormItemSpinnerListView
						.setOnItemClickListener(new UserProfileSettingFormItemEditorFormItemSpinnerListViewOnItemClickListener());

				// set its choice mode
				_mEditorFormItemSpinnerListView
						.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				// check editor form item info value and set its default
				// selected item
				if (null != _editorFormItemInfoValue) {
					// get and check the index of editor form item info value in
					// spinner content infos
					int _index = _editorFormItemSpinnerContentInfos
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

		// set user profile setting form item editor form item text, number,
		// email, multiple line textEdit as focus
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
	// user profile setting form item editor extra data constant
	public static final class UserProfileSettingFormItemEditorExtraData {

		// user profile setting form item label, type, spinner content and info
		// value
		public static final String USERPROFILESETTING_FROMITEM_LABEL = "userprofilesetting_formitem_label";
		public static final String USERPROFILESETTING_FROMITEM_TYPE = "userprofilesetting_formitem_type";
		public static final String USERPROFILESETTING_FROMITEM_SPINNERCONTENT = "userprofilesetting_formitem_spinnercontent";
		public static final String USERPROFILESETTING_FROMITEM_INFOVALUE = "userprofilesetting_formitem_infovalue";

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

			// define user profile setting extra data map
			Map<String, Object> _extraMap = new HashMap<String, Object>();

			// put editor form item need to update form item info value to extra
			// data map as param
			_extraMap
					.put(UserProfileSettingExtraData.USERPROFILESETTING_FORMITEM_EDITOR_NEED2UPDATEINFO,
							UserProfileSettingFormItemType.DATE == _mEditorFormItemType ? _mEditorFormItemDateDisplayTextView
									.getText().toString()
									: _mEditorFormItemEditText.getText()
											.toString());

			// pop user profile setting form item editor activity with result
			popActivityWithResult(RESULT_OK, _extraMap);
		}

	}

	// user profile setting form item editor form item date picker on date
	// changed listener
	class UserProfileSettingFormItemEditorFormItemDatePickerOnDateChangedListener
			implements OnDateChangedListener {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// update date display textView
			_mEditorFormItemDateDisplayTextView.setText(year + "-"
					+ String.format("%02d", monthOfYear + 1) + "-"
					+ String.format("%02d", dayOfMonth));
		}

	}

	// user profile setting form item editor form item spinner array adapter
	class UserProfileSettingFormItemEditorFormItemSpinnerArrayAdapter extends
			ArrayAdapter<String> {

		public UserProfileSettingFormItemEditorFormItemSpinnerArrayAdapter(
				Context context, int resource, List<String> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// get convert view
			View _convertView = super.getView(position, convertView, parent);

			// check count, position and set convert view background
			if (1 == getCount()) {
				_convertView
						.setBackgroundResource(R.drawable.ups_formitemeditor_spinnerlistview_single_item_bg);
			} else if (1 < getCount()) {
				if (0 == position) {
					_convertView
							.setBackgroundResource(R.drawable.ups_formitemeditor_spinnerlistview_multipletop_item_bg);
				} else if (getCount() - 1 == position) {
					_convertView
							.setBackgroundResource(R.drawable.ups_formitemeditor_spinnerlistview_multiplebottom_item_bg);
				} else {
					_convertView
							.setBackgroundResource(R.drawable.ups_formitemeditor_spinnerlistview_multiplemiddle_item_bg);
				}
			}

			return _convertView;
		}

	}

	// user profile setting form item editor form item spinner list view on item
	// click listener
	class UserProfileSettingFormItemEditorFormItemSpinnerListViewOnItemClickListener
			implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> spinnerListView,
				View checkedTextView, int position, long id) {
			// set the selected item checked
			((ListView) spinnerListView).setItemChecked(position, true);

			// define user profile setting extra data map
			Map<String, Object> _extraMap = new HashMap<String, Object>();

			// put editor form item need to update form item info value to extra
			// data map as param
			_extraMap
					.put(UserProfileSettingExtraData.USERPROFILESETTING_FORMITEM_EDITOR_NEED2UPDATEINFO,
							((CheckedTextView) checkedTextView).getText());

			// pop user profile setting form item editor activity with result
			popActivityWithResult(RESULT_OK, _extraMap);
		}

	}

}
