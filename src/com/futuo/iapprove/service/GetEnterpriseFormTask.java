package com.futuo.iapprove.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.form.FormBean;
import com.futuo.iapprove.form.FormItemBean;
import com.futuo.iapprove.form.FormTypeBean;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems.FormItem;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormTypes.FormType;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.Forms.Form;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.utils.HttpRequestParamUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class GetEnterpriseFormTask extends CoreServiceTask {

	private static final String LOG_TAG = GetEnterpriseFormTask.class
			.getCanonicalName();

	// get enterprise form type, form and form info post http request parameter
	private Map<String, String> _mGetEnterpriseFormTypePostHttpReqParam;
	private Map<String, String> _mGetEnterpriseFormPostHttpReqParam;
	private Map<String, String> _mGetEnterpriseFormInfoPostHttpReqParam;

	@Override
	protected void enterpriseChanged() {
		Log.d(LOG_TAG, "Query enterprise form types count");

		// query enterprise form types count in work thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				// get local storage enterprise form type count
				Cursor _cursor = _mContentResolver.query(ContentUris
						.withAppendedId(FormType.ENTERPRISE_CONTENT_URI,
								_mEnterpriseId),
						new String[] { FormType._COUNT_PROJECTION }, null,
						null, null);

				// check cursor
				if (null != _cursor) {
					// get and check form types count
					while (_cursor.moveToNext()) {
						if (0 == _cursor.getLong(_cursor
								.getColumnIndex(FormType._COUNT))) {
							// get nested enterprise form immediately, not
							// update the next scheduled time
							execute();
						} else {
							// get enterprise form type immediately only
							getNestedEnterpriseForm(false);
						}
					}

					// close cursor
					_cursor.close();
				}
			}

		}).start();
	}

	@Override
	protected void execute() {
		// get nested enterprise form
		getNestedEnterpriseForm(true);
	}

	// get enterprise form with type id
	public void getEnterpriseFormWithTypeId(Long formTypeId) {
		// check form type id
		if (null != formTypeId) {
			// send get enterprise form post http request
			HttpUtils.postRequest(
					_mContext.getResources().getString(R.string.server_url)
							+ _mContext.getResources().getString(
									R.string.get_enterpriseForm_url),
					PostRequestFormat.URLENCODED,
					generateGetEnterpriseFormPostHttpReqParam(formTypeId),
					null, HttpRequestType.ASYNCHRONOUS,
					new GetEnterpriseFormPostHttpRequestListener(formTypeId));
		} else {
			Log.e(LOG_TAG, "Get enterprise form error, form type id = "
					+ formTypeId);
		}
	}

	// get nested enterprise form with flag
	private void getNestedEnterpriseForm(Boolean isGetFormAndInfo) {
		// send get enterprise form type post http request
		HttpUtils.postRequest(
				_mContext.getResources().getString(R.string.server_url)
						+ _mContext.getResources().getString(
								R.string.get_enterpriseFormType_url),
				PostRequestFormat.URLENCODED,
				generateGetEnterpriseFormTypePostHttpReqParam(), null,
				HttpRequestType.ASYNCHRONOUS,
				new GetEnterpriseFormTypePostHttpRequestListener(
						isGetFormAndInfo));
	}

	// generate get enterprise form type post http request param
	private Map<String, String> generateGetEnterpriseFormTypePostHttpReqParam() {
		// check get enterprise form type post http request parameter
		if (null == _mGetEnterpriseFormTypePostHttpReqParam) {
			_mGetEnterpriseFormTypePostHttpReqParam = HttpRequestParamUtils
					.genUserSigHttpReqParam();

			// put get enterprise form type action and state in
			_mGetEnterpriseFormTypePostHttpReqParam
					.put(_mContext.getResources().getString(
							R.string.rbgServer_commonReqParam_action),
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormTypeReqParam_action));
			_mGetEnterpriseFormTypePostHttpReqParam
					.put(_mContext.getResources().getString(
							R.string.rbgServer_getIApproveReqParam_state),
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormTypeReqParam_state));
		}

		// put get enterprise form type user enterprise id in
		_mGetEnterpriseFormTypePostHttpReqParam.put(
				_mContext.getResources().getString(
						R.string.rbgServer_getIApproveReqParam_enterpriseId),
				StringUtils.base64Encode(_mEnterpriseId.toString()));

		return _mGetEnterpriseFormTypePostHttpReqParam;
	}

	// get local storage enterprise form types(form type id as key and bean as
	// value map) or forms(form id as key and bean as value map) with cursor and
	// bean class
	private Map<Long, ?> getLocalStorageEFT6FSIdAndBeanMap(Cursor cursor,
			Class<?> beanCls) {
		Map<Long, Object> _localStorageEFT6FSIdAndBeanMap = new HashMap<Long, Object>();

		// check the cursor
		if (null != cursor) {
			// set all local storage enterprise form types or forms for deleting
			while (cursor.moveToNext()) {
				// check bean class
				if (FormTypeBean.class == beanCls) {
					// get for deleting form type
					FormTypeBean _4deletingFormType = new FormTypeBean(cursor);

					// set it for deleting
					_4deletingFormType
							.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

					// put form type id and bean in
					_localStorageEFT6FSIdAndBeanMap.put(
							_4deletingFormType.getTypeId(), _4deletingFormType);
				} else if (FormBean.class == beanCls) {
					// get for deleting form
					FormBean _4deletingForm = new FormBean(cursor);

					// set it for deleting
					_4deletingForm
							.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

					// put form id and bean in
					_localStorageEFT6FSIdAndBeanMap.put(
							_4deletingForm.getFormId(), _4deletingForm);
				} else if (FormItemBean.class == beanCls) {
					// get for deleting form item
					FormItemBean _4deletingFormItem = new FormItemBean(cursor);

					// set it for deleting
					_4deletingFormItem
							.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

					// put form item id and bean in
					_localStorageEFT6FSIdAndBeanMap.put(
							_4deletingFormItem.getItemId(), _4deletingFormItem);
				} else {
					Log.e(LOG_TAG, "Unrecognized bean class = " + beanCls);

					// break immediately
					break;
				}
			}

			// close cursor
			cursor.close();
		}

		return _localStorageEFT6FSIdAndBeanMap;
	}

	// generate get enterprise form post http request param with form type id
	private Map<String, String> generateGetEnterpriseFormPostHttpReqParam(
			Long formTypeId) {
		// check get enterprise form post http request parameter
		if (null == _mGetEnterpriseFormPostHttpReqParam) {
			_mGetEnterpriseFormPostHttpReqParam = HttpRequestParamUtils
					.genUserSigHttpReqParam();

			// put get enterprise form action in
			_mGetEnterpriseFormPostHttpReqParam
					.put(_mContext.getResources().getString(
							R.string.rbgServer_commonReqParam_action),
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormReqParam_action));
		}

		// put get enterprise form user enterprise id and form type id in
		_mGetEnterpriseFormPostHttpReqParam.put(
				_mContext.getResources().getString(
						R.string.rbgServer_getIApproveReqParam_enterpriseId),
				StringUtils.base64Encode(_mEnterpriseId.toString()));
		_mGetEnterpriseFormPostHttpReqParam
				.put(_mContext
						.getResources()
						.getString(
								R.string.rbgServer_getEnterpriseFormReqParam_formTypeId),
						StringUtils.base64Encode(formTypeId.toString()));

		return _mGetEnterpriseFormPostHttpReqParam;
	}

	// get enterprise form info with form id and its type id
	private void getEnterpriseFormInfoWithFormId7TypeId(Long formId,
			Long formTypeId) {
		// check form id and its type id
		if (null != formId && null != formTypeId) {
			// send get enterprise form info post http request
			HttpUtils.postRequest(
					_mContext.getResources().getString(R.string.server_url)
							+ _mContext.getResources().getString(
									R.string.get_enterpriseFormInfo_url),
					PostRequestFormat.URLENCODED,
					generateGetEnterpriseFormInfoPostHttpReqParam(formId),
					null, HttpRequestType.ASYNCHRONOUS,
					new GetEnterpriseFormInfoPostHttpRequestListener(formId,
							formTypeId));
		} else {
			Log.e(LOG_TAG, "Get enterprise form info error, form id = "
					+ formId);
		}
	}

	// generate get enterprise form info post http request param with form id
	private Map<String, String> generateGetEnterpriseFormInfoPostHttpReqParam(
			Long formId) {
		// check get enterprise form info post http request parameter
		if (null == _mGetEnterpriseFormInfoPostHttpReqParam) {
			_mGetEnterpriseFormInfoPostHttpReqParam = HttpRequestParamUtils
					.genUserSigHttpReqParam();

			// put get enterprise form info action in
			_mGetEnterpriseFormInfoPostHttpReqParam
					.put(_mContext.getResources().getString(
							R.string.rbgServer_commonReqParam_action),
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormInfoReqParam_action));
		}

		// put get enterprise form info user enterprise id and form id in
		_mGetEnterpriseFormInfoPostHttpReqParam.put(
				_mContext.getResources().getString(
						R.string.rbgServer_getIApproveReqParam_enterpriseId),
				StringUtils.base64Encode(_mEnterpriseId.toString()));
		_mGetEnterpriseFormInfoPostHttpReqParam
				.put(_mContext
						.getResources()
						.getString(
								R.string.rbgServer_getEnterpriseFormInfoReqParam_formId),
						StringUtils.base64Encode(formId.toString()));

		return _mGetEnterpriseFormInfoPostHttpReqParam;
	}

	// inner class
	// get enterprise form type post http request listener
	class GetEnterpriseFormTypePostHttpRequestListener extends
			OnHttpRequestListener {

		// is get form and its info flag
		private Boolean _mIsGetFormAndInfo;

		public GetEnterpriseFormTypePostHttpRequestListener(
				Boolean isGetFormAndInfo) {
			super();

			// save is get form and its info flag
			_mIsGetFormAndInfo = isGetFormAndInfo;
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send get enterprise form type post http request successful, response entity string = "
							+ _respEntityString);

			// get and check http response entity string error json data
			JSONObject _respJsonData = JSONUtils
					.toJSONObject(_respEntityString);

			if (null != _respJsonData) {
				// get and check error message
				String _errorMsg = JSONUtils.getStringFromJSONObject(
						_respJsonData,
						_mContext.getResources().getString(
								R.string.rbgServer_commonReqResp_error));

				if (null != _errorMsg) {
					Log.e(LOG_TAG,
							"Get enterprise form type failed, response error message = "
									+ _errorMsg);
				} else {
					Log.e(LOG_TAG,
							"Get enterprise form type failed, response entity unrecognized");
				}
			} else {
				// get and check the enterprise form type array
				final JSONArray _enterpriseFormTypeJsonArray = JSONUtils
						.toJSONArray(_respEntityString);

				if (null != _enterpriseFormTypeJsonArray) {
					Log.d(LOG_TAG, "Get enterprise form type successful");

					// process enterprise form type list in work thread
					new Thread(new Runnable() {

						@Override
						public void run() {
							// get local storage enterprise form types form type
							// id as key and bean as value map
							@SuppressWarnings("unchecked")
							Map<Long, FormTypeBean> _localStorageEFTSFormTypeIdAndBeanMap = (Map<Long, FormTypeBean>) getLocalStorageEFT6FSIdAndBeanMap(
									_mContentResolver.query(
											ContentUris
													.withAppendedId(
															FormType.ENTERPRISE_CONTENT_URI,
															_mEnterpriseId),
											null, null, null, null),
									FormTypeBean.class);

							// define the form type content values
							ContentValues _formTypeContentValues = new ContentValues();

							for (int i = 0; i < _enterpriseFormTypeJsonArray
									.length(); i++) {
								// clear the form type content values
								_formTypeContentValues.clear();

								// get enterprise each form type
								FormTypeBean _formType = new FormTypeBean(
										JSONUtils
												.getJSONObjectFromJSONArray(
														_enterpriseFormTypeJsonArray,
														i));

								// generate the form type content values with
								// form type id and name
								_formTypeContentValues.put(FormType.TYPE_ID,
										_formType.getTypeId());
								_formTypeContentValues.put(FormType.NAME,
										_formType.getTypeName());

								// append form type enterprise id
								_formTypeContentValues.put(
										FormType.ENTERPRISE_ID, _mEnterpriseId);

								// insert the form type if not existed in local
								// storage database or update if existed
								if (!_localStorageEFTSFormTypeIdAndBeanMap
										.keySet().contains(
												_formType.getTypeId())) {
									Log.d(LOG_TAG,
											"The form type = "
													+ _formType
													+ " for inserting into local storage database, its content values = "
													+ _formTypeContentValues);

									_mContentResolver.insert(
											FormType.FORMTYPE_CONTENT_URI,
											_formTypeContentValues);
								} else {
									// get for updating form type
									FormTypeBean _4updatingFormType = _localStorageEFTSFormTypeIdAndBeanMap
											.get(_formType.getTypeId());

									// update local storage enterprise form type
									// normal
									_4updatingFormType
											.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.NORMAL);

									// compare the got form type with the for
									// updating form type
									if (0 != _formType
											.compareTo(_4updatingFormType)) {
										Log.d(LOG_TAG,
												"The form type whose id = "
														+ _4updatingFormType
																.getTypeId()
														+ " for updating to local storage database, its content values = "
														+ _formTypeContentValues);

										_mContentResolver.update(
												ContentUris
														.withAppendedId(
																FormType.FORMTYPE_CONTENT_URI,
																_4updatingFormType
																		.getRowId()),
												_formTypeContentValues, null,
												null);
									}
								}
							}

							// delete the local storage enterprise form type for
							// synchronizing
							for (Long _localStorageEFTSFormTypeId : _localStorageEFTSFormTypeIdAndBeanMap
									.keySet()) {
								// get the for deleting form type
								FormTypeBean _4deletingFormType = _localStorageEFTSFormTypeIdAndBeanMap
										.get(_localStorageEFTSFormTypeId);

								// check its data dirty type
								if (LocalStorageDataDirtyType.DELETE == _4deletingFormType
										.getLocalStorageDataDirtyType()) {
									Log.d(LOG_TAG,
											"The form type whose id = "
													+ _4deletingFormType
															.getTypeId()
													+ " will delete from local storage database");

									_mContentResolver.delete(
											ContentUris
													.withAppendedId(
															FormType.FORMTYPE_CONTENT_URI,
															_4deletingFormType
																	.getRowId()),
											null, null);
								}
							}
						}

					}).start();

					// check is get form and its info flag
					if (null != _mIsGetFormAndInfo
							&& true == _mIsGetFormAndInfo) {
						// get enterprise form with type id
						for (int i = 0; i < _enterpriseFormTypeJsonArray
								.length(); i++) {
							getEnterpriseFormWithTypeId(new FormTypeBean(
									JSONUtils.getJSONObjectFromJSONArray(
											_enterpriseFormTypeJsonArray, i))
									.getTypeId());
						}
					}
				} else {
					Log.e(LOG_TAG,
							"Get enterprise form type failed, response entity unrecognized");
				}
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			Log.e(LOG_TAG,
					"Send get enterprise form type post http request failed");
		}

	}

	// get enterprise form post http request listener
	class GetEnterpriseFormPostHttpRequestListener extends
			OnHttpRequestListener {

		// enterprise form type id
		private Long _mFormTypeId;

		public GetEnterpriseFormPostHttpRequestListener(Long formTypeId) {
			super();

			// save enterprise form type id
			_mFormTypeId = formTypeId;
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send get enterprise form post http request successful, response entity string = "
							+ _respEntityString);

			// get and check http response entity string error json data
			JSONObject _respJsonData = JSONUtils
					.toJSONObject(_respEntityString);

			if (null != _respJsonData) {
				// get and check error message
				String _errorMsg = JSONUtils.getStringFromJSONObject(
						_respJsonData,
						_mContext.getResources().getString(
								R.string.rbgServer_commonReqResp_error));

				if (null != _errorMsg) {
					Log.e(LOG_TAG,
							"Get enterprise form failed, response error message = "
									+ _errorMsg);
				} else {
					Log.e(LOG_TAG,
							"Get enterprise form failed, response entity unrecognized");
				}
			} else {
				// get and check the enterprise form array
				final JSONArray _enterpriseFormJsonArray = JSONUtils
						.toJSONArray(_respEntityString);

				if (null != _enterpriseFormJsonArray) {
					Log.d(LOG_TAG, "Get enterprise form successful");

					// process enterprise form list in work thread
					new Thread(new Runnable() {

						@Override
						public void run() {
							// get local storage enterprise forms form id as key
							// and bean as value map
							@SuppressWarnings("unchecked")
							Map<Long, FormBean> _localStorageEFSFormIdAndBeanMap = (Map<Long, FormBean>) getLocalStorageEFT6FSIdAndBeanMap(
									_mContentResolver
											.query(Form.FORMS_CONTENT_URI,
													null,
													Form.ENTERPRISE_FORMS_WITHTYPEID_CONDITION,
													new String[] {
															_mEnterpriseId
																	.toString(),
															_mFormTypeId
																	.toString() },
													null), FormBean.class);

							// define the form content values
							ContentValues _formContentValues = new ContentValues();

							for (int i = 0; i < _enterpriseFormJsonArray
									.length(); i++) {
								// clear the form content values
								_formContentValues.clear();

								// get enterprise each form
								FormBean _form = new FormBean(JSONUtils
										.getJSONObjectFromJSONArray(
												_enterpriseFormJsonArray, i));

								// generate the form content values with form id
								// and name
								_formContentValues.put(Form.FORM_ID,
										_form.getFormId());
								_formContentValues.put(Form.NAME,
										_form.getFormName());

								// append form type id and enterprise id
								_formContentValues.put(Form.FORMTYPE_ID,
										_mFormTypeId);
								_formContentValues.put(Form.ENTERPRISE_ID,
										_mEnterpriseId);

								// insert the form if not existed in local
								// storage database or update if existed
								if (!_localStorageEFSFormIdAndBeanMap.keySet()
										.contains(_form.getFormId())) {
									Log.d(LOG_TAG,
											"The form = "
													+ _form
													+ " for inserting into local storage database, its content values = "
													+ _formContentValues);

									_mContentResolver.insert(
											Form.FORM_CONTENT_URI,
											_formContentValues);
								} else {
									// get for updating form
									FormBean _4updatingForm = _localStorageEFSFormIdAndBeanMap
											.get(_form.getFormId());

									// update local storage enterprise form
									// normal
									_4updatingForm
											.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.NORMAL);

									// compare the got form with the for
									// updating form
									if (0 != _form.compareTo(_4updatingForm)) {
										Log.d(LOG_TAG,
												"The form whose id = "
														+ _4updatingForm
																.getFormId()
														+ " for updating to local storage database, its content values = "
														+ _formContentValues);

										_mContentResolver.update(ContentUris
												.withAppendedId(
														Form.FORM_CONTENT_URI,
														_4updatingForm
																.getRowId()),
												_formContentValues, null, null);
									}
								}
							}

							// delete the local storage enterprise form for
							// synchronizing
							for (Long _localStorageEFSFormId : _localStorageEFSFormIdAndBeanMap
									.keySet()) {
								// get the for deleting form
								FormBean _4deletingForm = _localStorageEFSFormIdAndBeanMap
										.get(_localStorageEFSFormId);

								// check its data dirty type
								if (LocalStorageDataDirtyType.DELETE == _4deletingForm
										.getLocalStorageDataDirtyType()) {
									Log.d(LOG_TAG,
											"The form whose id = "
													+ _4deletingForm
															.getFormId()
													+ " will delete from local storage database");

									_mContentResolver.delete(ContentUris
											.withAppendedId(
													Form.FORM_CONTENT_URI,
													_4deletingForm.getRowId()),
											null, null);
								}
							}
						}

					}).start();

					// get enterprise form into with id
					for (int i = 0; i < _enterpriseFormJsonArray.length(); i++) {
						// get enterprise form info with form id and its type id
						getEnterpriseFormInfoWithFormId7TypeId(
								new FormBean(JSONUtils
										.getJSONObjectFromJSONArray(
												_enterpriseFormJsonArray, i))
										.getFormId(),
								_mFormTypeId);
					}
				} else {
					Log.e(LOG_TAG,
							"Get enterprise form failed, response entity unrecognized");
				}
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			Log.e(LOG_TAG, "Send get enterprise form post http request failed");
		}

	}

	// get enterprise form info post http request listener
	class GetEnterpriseFormInfoPostHttpRequestListener extends
			OnHttpRequestListener {

		// enterprise form id and its type id
		private Long _mFormId;
		private Long _mFormTypeId;

		public GetEnterpriseFormInfoPostHttpRequestListener(Long formId,
				Long formTypeId) {
			super();

			// save enterprise form id and its type id
			_mFormId = formId;
			_mFormTypeId = formTypeId;
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send get enterprise form info post http request successful, response entity string = "
							+ _respEntityString);

			// get and check http response entity string error json data
			final JSONObject _respJsonData = JSONUtils
					.toJSONObject(_respEntityString);

			if (null != _respJsonData) {
				// get and check error message
				String _errorMsg = JSONUtils.getStringFromJSONObject(
						_respJsonData,
						_mContext.getResources().getString(
								R.string.rbgServer_commonReqResp_error));

				if (null != _errorMsg) {
					Log.e(LOG_TAG,
							"Get enterprise form info failed, response error message = "
									+ _errorMsg);
				} else {
					Log.d(LOG_TAG, "Get enterprise form info successful");

					// process enterprise form info in work thread
					new Thread(new Runnable() {

						@Override
						public void run() {
							// get local storage enterprise form items form item
							// id as key and bean as value map
							@SuppressWarnings("unchecked")
							Map<Long, FormItemBean> _localStorageEFISFormItemIdAndBeanMap = (Map<Long, FormItemBean>) getLocalStorageEFT6FSIdAndBeanMap(
									_mContentResolver
											.query(FormItem.FORMITEMS_CONTENT_URI,
													null,
													FormItem.ENTERPRISE_FORMITEMS_WITHFORMTYPEID7FORMID_CONDITION,
													new String[] {
															_mEnterpriseId
																	.toString(),
															_mFormTypeId
																	.toString(),
															_mFormId.toString() },
													null), FormItemBean.class);

							// define the form item content values
							ContentValues _formItemContentValues = new ContentValues();

							// get and process form items
							for (FormItemBean formItem : FormItemBean
									.getFormItems(_respJsonData)) {
								// clear the form item content values
								_formItemContentValues.clear();

								// generate the form item content values with
								// item id, name, physical name, type, must
								// write flag and capital flag
								_formItemContentValues.put(FormItem.ITEM_ID,
										formItem.getItemId());
								_formItemContentValues.put(FormItem.NAME,
										formItem.getItemName());
								_formItemContentValues.put(
										FormItem.PHYSICALNAME,
										formItem.getItemPhysicalName());
								_formItemContentValues.put(FormItem.TYPE,
										formItem.getItemType().getValue());
								_formItemContentValues.put(
										FormItem.MUSTWRITE_FLAG,
										formItem.mustWrite());
								_formItemContentValues.put(
										FormItem.CAPITAL_FLAG,
										formItem.needCapital());

								// append form item formula
								_formItemContentValues.put(FormItem.FORMULA,
										formItem.getFormula());

								// append form item selector content infos
								for (int i = 0; i < formItem
										.getSelectorContentInfos().size(); i++) {
									_formItemContentValues.put(
											String.format(
													FormItem.SELECTORCONTENT_INFO_FORMAT,
													i), formItem
													.getSelectorContentInfos()
													.get(i));
								}

								// append form id, its type id and enterprise id
								_formItemContentValues.put(FormItem.FORM_ID,
										_mFormId);
								_formItemContentValues.put(
										FormItem.FORMTYPE_ID, _mFormTypeId);
								_formItemContentValues.put(
										FormItem.ENTERPRISE_ID, _mEnterpriseId);

								// insert the form item if not existed in local
								// storage database or update if existed
								if (!_localStorageEFISFormItemIdAndBeanMap
										.keySet()
										.contains(formItem.getItemId())) {
									Log.d(LOG_TAG,
											"The form item = "
													+ formItem
													+ " for inserting into local storage database, its content values = "
													+ _formItemContentValues);

									_mContentResolver.insert(
											FormItem.FORMITEM_CONTENT_URI,
											_formItemContentValues);
								} else {
									// get for updating form item
									FormItemBean _4updatingFormItem = _localStorageEFISFormItemIdAndBeanMap
											.get(formItem.getItemId());

									// update local storage enterprise form item
									// normal
									_4updatingFormItem
											.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.NORMAL);

									// compare the got form item with the for
									// updating form item
									if (0 != formItem
											.compareTo(_4updatingFormItem)) {
										Log.d(LOG_TAG,
												"The form item whose id = "
														+ _4updatingFormItem
																.getItemId()
														+ " for updating to local storage database, its content values = "
														+ _formItemContentValues);

										_mContentResolver.update(
												ContentUris
														.withAppendedId(
																FormItem.FORMITEM_CONTENT_URI,
																_4updatingFormItem
																		.getRowId()),
												_formItemContentValues, null,
												null);
									}
								}
							}

							// delete the local storage enterprise form item for
							// synchronizing
							for (Long _localStorageEFISFormItemId : _localStorageEFISFormItemIdAndBeanMap
									.keySet()) {
								// get the for deleting form item
								FormItemBean _4deletingFormItem = _localStorageEFISFormItemIdAndBeanMap
										.get(_localStorageEFISFormItemId);

								// check its data dirty type
								if (LocalStorageDataDirtyType.DELETE == _4deletingFormItem
										.getLocalStorageDataDirtyType()) {
									Log.d(LOG_TAG,
											"The form item whose id = "
													+ _4deletingFormItem
															.getItemId()
													+ " will delete from local storage database");

									_mContentResolver.delete(
											ContentUris
													.withAppendedId(
															FormItem.FORMITEM_CONTENT_URI,
															_4deletingFormItem
																	.getRowId()),
											null, null);
								}
							}
						}

					}).start();
				}
			} else {
				Log.e(LOG_TAG,
						"Get enterprise form info failed, response entity unrecognized");
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			Log.e(LOG_TAG,
					"Send get enterprise form info post http request failed");
		}

	}

}
