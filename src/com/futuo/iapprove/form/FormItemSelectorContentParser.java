package com.futuo.iapprove.form;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class FormItemSelectorContentParser {

	private static final String LOG_TAG = FormItemSelectorContentParser.class
			.getCanonicalName();

	// parser form item selector content info list with JSON object
	public static List<String> parserFormItemSelectorContentList(
			JSONObject formItemJSONObject) {
		List<String> _formItemSelectorContentInfoList = null;

		// check form item JSON object
		if (null != formItemJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// initialized form item selector content info list
			_formItemSelectorContentInfoList = new ArrayList<String>();

			// get and check form item selector content json array
			JSONArray _formItemSelectorContentsJSONArray = JSONUtils
					.getJSONArrayFromJSONObject(
							formItemJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormInfoReqResp_item_selectorContentList));
			if (null != _formItemSelectorContentsJSONArray) {
				for (int i = 0; i < _formItemSelectorContentsJSONArray.length(); i++) {
					// add got enterprise form item selector content info to
					// list
					_formItemSelectorContentInfoList
							.add(new FormItemSelectorContentBean(JSONUtils
									.getJSONObjectFromJSONArray(
											_formItemSelectorContentsJSONArray,
											i)).getInfo());
				}
			}
		} else {
			Log.e(LOG_TAG,
					"Get form item selector content list with JSON object error, form info JSON object = "
							+ formItemJSONObject);
		}

		return _formItemSelectorContentInfoList;
	}

	// inner class
	// form item selector content bean
	static class FormItemSelectorContentBean {

		// form item selector content id and info
		private Long selectorContentId;
		private String info;

		// constructor with JSON object
		public FormItemSelectorContentBean(
				JSONObject formItemSelectorContentJSONObject) {
			super();

			// check form item selector content JSON object
			if (null != formItemSelectorContentJSONObject) {
				// get application context
				Context _appContext = CTApplication.getContext();

				// set form item selector content attributes
				// form item selector content id
				try {
					selectorContentId = Long
							.parseLong(JSONUtils
									.getStringFromJSONObject(
											formItemSelectorContentJSONObject,
											_appContext
													.getResources()
													.getString(
															R.string.rbgServer_getEnterpriseFormInfoReqResp_item_selectorContent_id)));
				} catch (NumberFormatException e) {
					Log.e(LOG_TAG,
							"Get form item selector content id error, exception message = "
									+ e.getMessage());

					e.printStackTrace();
				}

				// form item selector content info
				info = JSONUtils
						.getStringFromJSONObject(
								formItemSelectorContentJSONObject,
								_appContext
										.getResources()
										.getString(
												R.string.rbgServer_getEnterpriseFormInfoReqResp_item_selectorContent_info));
			} else {
				Log.e(LOG_TAG,
						"New form item selector content with JSON object error, form JSON object = "
								+ formItemSelectorContentJSONObject);
			}
		}

		public Long getSelectorContentId() {
			return selectorContentId;
		}

		public void setSelectorContentId(Long selectorContentId) {
			this.selectorContentId = selectorContentId;
		}

		public String getInfo() {
			return info;
		}

		public void setInfo(String info) {
			this.info = info;
		}

		@Override
		public String toString() {
			// define description
			StringBuilder _description = new StringBuilder();

			// append enterprise form item selector content id and info
			_description.append("Enterprise form item selector content id = ")
					.append(selectorContentId).append(" and ")
					.append("form item selector content info = ").append(info);

			return _description.toString();
		}

	}

}
