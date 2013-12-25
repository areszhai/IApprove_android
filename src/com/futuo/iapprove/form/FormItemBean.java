package com.futuo.iapprove.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems.FormItem;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.CommonUtils;
import com.richitec.commontoolkit.utils.JSONUtils;

public class FormItemBean implements Comparable<FormItemBean>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8949917272352104589L;

	private static final String LOG_TAG = FormItemBean.class.getCanonicalName();

	// row id, form item id, form item name, physical name, type, is must write
	// flag, is capital flag and formula
	private Long rowId;
	private Long itemId;
	private String itemName;
	private String itemPhysicalName;
	private FormItemType itemType;
	private Boolean mustWrite;
	private Boolean capital;
	private String formula;

	// selector content info list
	private List<String> selectorContentInfos;

	// local storage data dirty type
	private LocalStorageDataDirtyType lsDataDirtyType;

	public FormItemBean() {
		super();

		// set default type, must write flag, capital flag and
		itemType = FormItemType.TEXTEDIT_TEXT;
		mustWrite = true;
		capital = false;

		// initialized selector content info list
		selectorContentInfos = new ArrayList<String>();

		// set default local storage data dirty type
		lsDataDirtyType = LocalStorageDataDirtyType.NORMAL;
	}

	// constructor with JSON object
	public FormItemBean(JSONObject formItemJSONObject) {
		this();

		// check form item JSON object
		if (null != formItemJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// set form item attributes
			// form item id
			try {
				itemId = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										formItemJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseFormInfoReqResp_item_id)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG, "Get form item id error, exception message = "
						+ e.getMessage());

				e.printStackTrace();
			}

			// form item name
			itemName = JSONUtils
					.getStringFromJSONObject(
							formItemJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormInfoReqResp_item_name));

			// form item physical name
			itemPhysicalName = JSONUtils
					.getStringFromJSONObject(
							formItemJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormInfoReqResp_item_physicalName));

			// form item type
			itemType = FormItemType
					.getType(JSONUtils
							.getStringFromJSONObject(
									formItemJSONObject,
									_appContext
											.getResources()
											.getString(
													R.string.rbgServer_getEnterpriseFormInfoReqResp_item_type)));

			// form item must write flag
			try {
				// get item must write flag
				Integer _mustWriteFlag = Integer
						.parseInt(JSONUtils
								.getStringFromJSONObject(
										formItemJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseFormInfoReqResp_item_mustWriteFlag)));

				if (Integer
						.parseInt(_appContext
								.getResources()
								.getString(
										R.string.rbgServer_getEnterpriseFormInfoReqResp_item_mustWrite)) == _mustWriteFlag) {
					mustWrite = true;
				} else if (Integer
						.parseInt(_appContext
								.getResources()
								.getString(
										R.string.rbgServer_getEnterpriseFormInfoReqResp_item_mustnotWrite)) == _mustWriteFlag) {
					mustWrite = false;
				}
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get form item must write flag error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// form item capital flag
			try {
				// get item capital flag
				Integer _capitalFlag = Integer
						.parseInt(JSONUtils
								.getStringFromJSONObject(
										formItemJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getEnterpriseFormInfoReqResp_item_capitalFlag)));

				if (Integer
						.parseInt(_appContext
								.getResources()
								.getString(
										R.string.rbgServer_getEnterpriseFormInfoReqResp_item_capital)) == _capitalFlag) {
					capital = true;
				} else if (Integer
						.parseInt(_appContext
								.getResources()
								.getString(
										R.string.rbgServer_getEnterpriseFormInfoReqResp_item_notCapital)) == _capitalFlag) {
					capital = false;
				}
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get form item capital flag error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// get and check selector content infos value
			List<String> _selectorContentInfosValue = FormItemSelectorContentParser
					.parserFormItemSelectorContentList(formItemJSONObject);
			if (null != _selectorContentInfosValue) {
				selectorContentInfos.addAll(_selectorContentInfosValue);
			}
		} else {
			Log.e(LOG_TAG,
					"New form item with JSON object error, form JSON object = "
							+ formItemJSONObject);
		}
	}

	// constructor with cursor
	public FormItemBean(Cursor cursor) {
		this();

		// check the cursor
		if (null != cursor) {
			// set form item attributes
			// row id
			rowId = cursor.getLong(cursor.getColumnIndex(FormItem._ID));

			// form item id
			itemId = cursor.getLong(cursor.getColumnIndex(FormItem.ITEM_ID));

			// form item name
			itemName = cursor.getString(cursor.getColumnIndex(FormItem.NAME));

			// form item physical name
			itemPhysicalName = cursor.getString(cursor
					.getColumnIndex(FormItem.PHYSICALNAME));

			// form item type
			itemType = FormItemType.getType(cursor.getInt(cursor
					.getColumnIndex(FormItem.TYPE)));

			// form must write
			mustWrite = 0 == cursor.getShort(cursor
					.getColumnIndex(FormItem.MUSTWRITE_FLAG)) ? false : true;

			// form need capital
			capital = 0 == cursor.getShort(cursor
					.getColumnIndex(FormItem.CAPITAL_FLAG)) ? false : true;

			// form item formula
			formula = cursor.getString(cursor.getColumnIndex(FormItem.FORMULA));
		} else {
			Log.e(LOG_TAG, "New form item with cursor error, cursor = "
					+ cursor);
		}
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemPhysicalName() {
		return itemPhysicalName;
	}

	public void setItemPhysicalName(String itemPhysicalName) {
		this.itemPhysicalName = itemPhysicalName;
	}

	public FormItemType getItemType() {
		return itemType;
	}

	public void setItemType(FormItemType itemType) {
		this.itemType = itemType;
	}

	public Boolean mustWrite() {
		return mustWrite;
	}

	public void setMustWrite(Boolean mustWrite) {
		this.mustWrite = mustWrite;
	}

	public Boolean needCapital() {
		return capital;
	}

	public void setCapital(Boolean capital) {
		this.capital = capital;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public List<String> getSelectorContentInfos() {
		return selectorContentInfos;
	}

	public void setSelectorContentInfos(List<String> selectorContentInfos) {
		this.selectorContentInfos = selectorContentInfos;
	}

	public LocalStorageDataDirtyType getLocalStorageDataDirtyType() {
		return lsDataDirtyType;
	}

	public void setLocalStorageDataDirtyType(
			LocalStorageDataDirtyType lsDataDirtyType) {
		this.lsDataDirtyType = lsDataDirtyType;
	}

	@Override
	public int compareTo(FormItemBean another) {
		int _result = -1;

		// check form item name, type, is must write flag, is capital flag,
		// formula and selector content list
		if ((null == itemName && null == another.itemName)
				|| (null != itemName && null != another.itemName && itemName
						.equalsIgnoreCase(another.itemName))) {
			if ((null == itemType && null == another.itemType)
					|| (null != itemType && null != another.itemType && itemType == another.itemType)) {
				if ((null == mustWrite && null == another.mustWrite)
						|| (null != mustWrite && null != another.mustWrite && mustWrite
								.booleanValue() == another.mustWrite
								.booleanValue())) {
					if ((null == capital && null == another.capital)
							|| (null != capital && null != another.capital && capital
									.booleanValue() == another.capital
									.booleanValue())) {
						if ((null == formula && null == another.formula)
								|| (null != formula && null != another.formula && formula
										.equalsIgnoreCase(another.formula))) {
							if (CommonUtils.compareList(selectorContentInfos,
									another.selectorContentInfos)) {
								_result = 0;
							} else {
								Log.d(LOG_TAG,
										"Form item selector content info list not equals, self selector content info list = "
												+ selectorContentInfos
												+ " and another selector content info list = "
												+ another.selectorContentInfos);
							}
						} else {
							Log.d(LOG_TAG,
									"Form item formula not equals, self formula = "
											+ formula
											+ " and another formula = "
											+ another.formula);
						}
					} else {
						Log.d(LOG_TAG,
								"Form item capital flag not equals, self capital flag = "
										+ capital
										+ " and another capital flag = "
										+ another.capital);
					}
				} else {
					Log.d(LOG_TAG,
							"Form item must write flag not equals, self must write flag = "
									+ mustWrite
									+ " and another must write flag = "
									+ another.mustWrite);
				}
			} else {
				Log.d(LOG_TAG, "Form item type not equals, self type = "
						+ itemType + " and another type = " + another.itemType);
			}
		} else {
			Log.d(LOG_TAG, "Form item name not equals, self name = " + itemName
					+ " and another name = " + another.itemName);
		}

		return _result;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append enterprise form item id, name, physical name, type, must
		// write, need capital flag, formula and selector content list
		_description.append("Enterprise form item row id = ").append(rowId)
				.append(", ").append("form item id = ").append(itemId)
				.append(", ").append("form item name = ").append(itemName)
				.append(", ").append("form item physical name = ")
				.append(itemPhysicalName).append(", ")
				.append("form item type = ").append(itemType).append(", ")
				.append("must write = ").append(mustWrite).append(", ")
				.append("need capital = ").append(capital).append(", ")
				.append("formula = ").append(formula).append(" and ")
				.append("selector content list = ")
				.append(selectorContentInfos);

		return _description.toString();
	}

	// get form item bean with JSON object
	public static List<FormItemBean> getFormItems(JSONObject formInfoJSONObject) {
		List<FormItemBean> _formItems = null;

		// check form info JSON object
		if (null != formInfoJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// initialize return form item bean list
			_formItems = new ArrayList<FormItemBean>();

			// get form item formula map
			Map<Long, String> _formItemFormulaMap = FormFormulaParser
					.parserFormItemFormulaMap(formInfoJSONObject);

			// get and check form item json array
			JSONArray _formItemsJSONArray = JSONUtils
					.getJSONArrayFromJSONObject(
							formInfoJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormInfoReqResp_itemList));
			if (null != _formItemsJSONArray) {
				for (int i = 0; i < _formItemsJSONArray.length(); i++) {
					// get enterprise form item
					FormItemBean _formItem = new FormItemBean(
							JSONUtils.getJSONObjectFromJSONArray(
									_formItemsJSONArray, i));

					// set form item formula
					_formItem.setFormula(_formItemFormulaMap.get(_formItem
							.getItemId()));

					// add got enterprise form item to list
					_formItems.add(_formItem);
				}
			}
		} else {
			Log.e(LOG_TAG,
					"Get form item list with JSON object error, form info JSON object = "
							+ formInfoJSONObject);
		}

		return _formItems;
	}

}
