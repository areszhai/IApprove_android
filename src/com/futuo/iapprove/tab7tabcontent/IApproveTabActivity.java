package com.futuo.iapprove.tab7tabcontent;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.customcomponent.CTTabSpecIndicator;

@SuppressWarnings("deprecation")
public class IApproveTabActivity extends TabActivity {

	private static final String LOG_TAG = IApproveTabActivity.class
			.getCanonicalName();

	// tab widget item and content class array, the to-do list, addressbook,
	// history record and settings
	private final Object[][] TAB_WIDGETITEMS7CONTENTCLS = new Object[][] {
			{ R.string.todoList_tab_tag, R.string.todoList_tab7nav_title,
					R.drawable.todo_tab_icon, TodoListTabContentActivity.class },
			{ R.string.addressbook_tab_tag, R.string.addressbook_tab7nav_title,
					R.drawable.addressbook_tab_icon,
					AddressbookTabContentActivity.class },
			{ R.string.historyRecord_tab_tag, R.string.historyRecord_tab_title,
					R.drawable.historyrecord_tab_icon,
					HistoryRecordTabContentActivity.class },
			{ R.string.settings_tab_tag, R.string.settings_tab7nav_title,
					R.drawable.settings_tab_icon,
					SettingsTabContentActivity.class } };

	// tab host
	private TabHost _mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.iapprove_tab_activity_layout);

		// set subViews
		// get tabHost
		_mTabHost = getTabHost();

		// set tab indicator and content
		for (int i = 0; i < TAB_WIDGETITEMS7CONTENTCLS.length; i++) {
			try {
				// get tab spec tag, indicator label, icon resource and content
				// class
				// tag
				String _tag = getResources().getString(
						(Integer) TAB_WIDGETITEMS7CONTENTCLS[i][0]);

				// label resource
				Integer _labelRes = (Integer) TAB_WIDGETITEMS7CONTENTCLS[i][1];

				// icon resource
				Integer _iconRes = (Integer) TAB_WIDGETITEMS7CONTENTCLS[i][2];

				// content class
				Class<?> _contentCls = (Class<?>) TAB_WIDGETITEMS7CONTENTCLS[i][3];

				Log.d(LOG_TAG, "IApprove tab spec tag = " + _tag
						+ ", indicator label resource = " + _labelRes
						+ ", icon resource = " + _iconRes
						+ " and content class = " + _contentCls);

				// new tab spec and add to tab host
				TabSpec _tabSpec = _mTabHost
						.newTabSpec(_tag)
						.setIndicator(
								new CTTabSpecIndicator(this, _labelRes,
										_iconRes))
						.setContent(new Intent().setClass(this, _contentCls));

				_mTabHost.addTab(_tabSpec);
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"New tab spec error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		}
	}

}
