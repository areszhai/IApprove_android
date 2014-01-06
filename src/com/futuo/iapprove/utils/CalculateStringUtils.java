package com.futuo.iapprove.utils;

import android.util.Log;

public class CalculateStringUtils {

	private static final String LOG_TAG = CalculateStringUtils.class
			.getCanonicalName();

	// get calculate string capital
	public static String calculateCapital(String calculateString) {
		StringBuilder _capitalStringBuilder = null;

		// digit, fraction and unit array
		String _digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		String _fraction[] = { "角", "分" };
		String _unit[][] = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } };

		// check calculate string
		if (null != calculateString) {
			// initialize capital string builder
			_capitalStringBuilder = new StringBuilder();

			// get calculate string capital
			try {
				// parse calculate string to double
				Double _calculateStringDouble = Double
						.parseDouble(calculateString);

				Log.d(LOG_TAG, "The calculate string double = "
						+ _calculateStringDouble);

				// get symbol as head and get calculate string double absolute
				// value
				String _headSymbol = _calculateStringDouble < 0 ? "负" : "";
				Double _calculateStringDoubleAbs = Math
						.abs(_calculateStringDouble);

				// append head symbol
				_capitalStringBuilder.append(_headSymbol);

				//
				String s = "";
				for (int i = 0; i < _fraction.length; i++) {
					s += (_digit[(int) (Math.floor(_calculateStringDoubleAbs
							* 10 * Math.pow(10, i)) % 10)] + _fraction[i])
							.replaceAll("(零.)+", "");
				}

				if (s.length() < 1) {
					s = "整";
				}

				//
				int integerPart = (int) Math.floor(_calculateStringDoubleAbs);

				for (int i = 0; i < _unit[0].length && integerPart > 0; i++) {
					String p = "";
					for (int j = 0; j < _unit[1].length
							&& _calculateStringDoubleAbs > 0; j++) {
						p = _digit[integerPart % 10] + _unit[1][j] + p;
						integerPart = integerPart / 10;
					}
					s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零")
							+ _unit[0][i] + s;
				}

				// append content
				_capitalStringBuilder.append(s.replaceAll("(零.)*零元", "元")
						.replaceFirst("(零.)+", "").replaceAll("(零.)+", "零")
						.replaceAll("^整$", "零元整"));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get calculate string capital error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		} else {
			Log.d(LOG_TAG,
					"Get calculate string capital error, the calculate string = "
							+ calculateString);
		}

		return _capitalStringBuilder.toString();
	}

}
