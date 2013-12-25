package com.futuo.iapprove.form;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class FormFormulaParser {

	private static final String LOG_TAG = FormFormulaParser.class
			.getCanonicalName();

	// parser form item formula map(key is form item id and value is formula)
	// with JSON object
	public static Map<Long, String> parserFormItemFormulaMap(
			JSONObject formInfoJSONObject) {
		Map<Long, String> _formItemFormulaMap = new HashMap<Long, String>();

		// check form info JSON object
		if (null != formInfoJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// get and check form item formula json array
			JSONArray _formItemFormulasJSONArray = JSONUtils
					.getJSONArrayFromJSONObject(
							formInfoJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getEnterpriseFormInfoReqResp_formulaList));
			if (null != _formItemFormulasJSONArray) {
				for (int i = 0; i < _formItemFormulasJSONArray.length(); i++) {
					// get enterprise form item formula bean
					FormItemFormulaBean _formItemFormula = new FormItemFormulaBean(
							JSONUtils.getJSONObjectFromJSONArray(
									_formItemFormulasJSONArray, i));

					// add got enterprise form item formula to map
					_formItemFormulaMap.put(
							_formItemFormula.getFormulaTotalId(),
							_formItemFormula.getFormula());
				}
			}
		} else {
			Log.e(LOG_TAG,
					"Get form item formula map with JSON object error, form info JSON object = "
							+ formInfoJSONObject);
		}

		return _formItemFormulaMap;
	}

	// inner class
	// form item formula bean
	static class FormItemFormulaBean {

		// form item formula id, total id and formula
		private Long formulaId;
		private Long formulaTotalId;
		private String formula;

		// constructor with JSON object
		public FormItemFormulaBean(JSONObject formItemFormulaJSONObject) {
			super();

			// check form item formula JSON object
			if (null != formItemFormulaJSONObject) {
				// get application context
				Context _appContext = CTApplication.getContext();

				// set form item formula attributes
				// form item formula id
				try {
					formulaId = Long
							.parseLong(JSONUtils
									.getStringFromJSONObject(
											formItemFormulaJSONObject,
											_appContext
													.getResources()
													.getString(
															R.string.rbgServer_getEnterpriseFormInfoReqResp_formula_id)));
				} catch (NumberFormatException e) {
					Log.e(LOG_TAG,
							"Get form item formula id error, exception message = "
									+ e.getMessage());

					e.printStackTrace();
				}

				try {
					// get form item formula form id
					Long _formulaFormId = Long
							.parseLong(JSONUtils
									.getStringFromJSONObject(
											formItemFormulaJSONObject,
											_appContext
													.getResources()
													.getString(
															R.string.rbgServer_getEnterpriseFormInfoReqResp_formula_formId)));

					// get form origin formula
					String _originFormula = JSONUtils
							.getStringFromJSONObject(
									formItemFormulaJSONObject,
									_appContext
											.getResources()
											.getString(
													R.string.rbgServer_getEnterpriseFormInfoReqResp_formula_formula));

					// get formula total id string and formula map
					Map<String, String> _formulaTotalIdAndFormulaMap = getFormulaTotalIdAndFormulaMap(
							_formulaFormId, _originFormula);

					// form item formula total id
					try {
						formulaTotalId = Long
								.parseLong(_formulaTotalIdAndFormulaMap
										.get(FormulaTotalIdAndFormulaKeys.FORMULA_TOTALID_KEY));
					} catch (NumberFormatException e) {
						Log.e(LOG_TAG,
								"Get form item formula total id error, exception message = "
										+ e.getMessage());

						e.printStackTrace();
					}

					// form item formula
					formula = _formulaTotalIdAndFormulaMap
							.get(FormulaTotalIdAndFormulaKeys.FORMULA_FORMULA_KEY);
				} catch (NumberFormatException e) {
					Log.e(LOG_TAG,
							"Get form item formula form id error, exception message = "
									+ e.getMessage());

					e.printStackTrace();
				}
			} else {
				Log.e(LOG_TAG,
						"New form item formula with JSON object error, form JSON object = "
								+ formItemFormulaJSONObject);
			}
		}

		public Long getFormulaId() {
			return formulaId;
		}

		public void setFormulaId(Long formulaId) {
			this.formulaId = formulaId;
		}

		public Long getFormulaTotalId() {
			return formulaTotalId;
		}

		public void setFormulaTotalId(Long formulaTotalId) {
			this.formulaTotalId = formulaTotalId;
		}

		public String getFormula() {
			return formula;
		}

		public void setFormula(String formula) {
			this.formula = formula;
		}

		@Override
		public String toString() {
			// define description
			StringBuilder _description = new StringBuilder();

			// append enterprise form item formula id, total id and formula
			_description.append("Enterprise form item formula id = ")
					.append(formulaId).append(", ")
					.append("form item formula total id = ")
					.append(formulaTotalId).append(" and ")
					.append("form item formula = ").append(formula);

			return _description.toString();
		}

		// get form item formula total id and formula map with form id and
		// origin formula
		private Map<String, String> getFormulaTotalIdAndFormulaMap(Long formId,
				String originFormula) {
			Map<String, String> _formulaTotalIdAndFormulaMap = new HashMap<String, String>();

			// check formula form id and origin formula
			if (null != formId && null != originFormula
					&& !"".equalsIgnoreCase(originFormula)) {
				// get and check origin formula total and formula array
				String[] _originFormulaTotalAndFormulaArray = StringUtils
						.split(originFormula, "=");
				if (null != _originFormulaTotalAndFormulaArray
						&& 2 == _originFormulaTotalAndFormulaArray.length) {
					// get and check origin formula total
					String _originFormulaTotal = StringUtils.split(
							_originFormulaTotalAndFormulaArray[0], "[", "]")[0];
					if (_originFormulaTotal.startsWith(formId.toString())) {
						// put formula total id string to map
						_formulaTotalIdAndFormulaMap
								.put(FormulaTotalIdAndFormulaKeys.FORMULA_TOTALID_KEY,
										_originFormulaTotal.substring(formId
												.toString().length()));
					}

					// put formula to map
					_formulaTotalIdAndFormulaMap.put(
							FormulaTotalIdAndFormulaKeys.FORMULA_FORMULA_KEY,
							StringUtils.trim(
									_originFormulaTotalAndFormulaArray[1]
											.replace("[" + formId, ""), "[]"));
				} else {
					Log.e(LOG_TAG,
							"Get formula total id and formula map error, origin formula total and formula array = "
									+ _originFormulaTotalAndFormulaArray);
				}
			}

			return _formulaTotalIdAndFormulaMap;
		}

		// inner class
		// formula total id and formula keys
		class FormulaTotalIdAndFormulaKeys {

			// formula total id and formula keys
			private static final String FORMULA_TOTALID_KEY = "formula_totalid";
			private static final String FORMULA_FORMULA_KEY = "formula_formula";

		}

	}

}
