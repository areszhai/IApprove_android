package com.futuo.iapprove.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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

	// get calculate expression operand and operator list
	public static List<Object> getCalculateExpressionTokens(
			String expressionString) {
		List<Object> _expressionTokens = new ArrayList<Object>();

		// check expression string
		if (null != expressionString) {
			// get and check the calculate expression number list
			@SuppressWarnings("unchecked")
			List<Object> _tokensList = new Calculator()
					.getTokens(expressionString);
			if (null != _tokensList) {
				_expressionTokens.addAll(_tokensList);
			}
		} else {
			Log.d(LOG_TAG,
					"get calculate expression operand and operator list error, the expression string = "
							+ expressionString);
		}

		return _expressionTokens;
	}

	// get calculate expression numbers
	public static List<Double> getCalculateExpressionNumbers(
			String expressionString) {
		List<Double> _expressionNumbers = new ArrayList<Double>();

		// check expression string
		if (null != expressionString) {
			// get and check the calculate expression number list
			List<Double> _numberList = new Calculator()
					.getNumbers(expressionString);
			if (null != _numberList) {
				_expressionNumbers.addAll(_numberList);
			}
		} else {
			Log.d(LOG_TAG,
					"get calculate expression number list error, the expression string = "
							+ expressionString);
		}

		return _expressionNumbers;
	}

	// calculate string expression
	public static String calculateExpression(String expressionString) {
		StringBuilder _expressionResult = null;

		// check expression string
		if (null != expressionString) {
			// initialize expression result string builder
			_expressionResult = new StringBuilder();

			// get and check the expression calculate result
			String _result = new Calculator().exec(expressionString);
			if (null != _result) {
				_expressionResult.append(_result);
			}
		} else {
			Log.d(LOG_TAG,
					"Calculate string expression error, the expression string = "
							+ expressionString);
		}

		return _expressionResult.toString();
	}

	// calculate expression operand and operator list
	public static String calculateExpression(List<Object> expressionTokens) {
		StringBuilder _expressionResult = null;

		// check expression operand and operator list
		if (null != expressionTokens) {
			// initialize expression result string builder
			_expressionResult = new StringBuilder();

			// get and check the expression calculate result
			String _result = new Calculator().exec(expressionTokens);
			if (null != _result) {
				_expressionResult.append(_result);
			}
		} else {
			Log.d(LOG_TAG,
					"Calculate expression operand and operator list error, the expression operand and operator list = "
							+ expressionTokens);
		}

		return _expressionResult.toString();
	}

	// inner class
	static class Calculator {

		private final Stack<Double> numStack = new Stack<Double>();
		private final Stack<Character> opStack = new Stack<Character>();

		private char currentOperator;
		private char opStackTop;

		private int i;
		private String expression;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public List getTokens(String expression) {
			List _tokens = new ArrayList();

			try {
				clean();
				if (expression == null || expression.isEmpty()) {
					throw new IllegalArgumentException("Blank Expression!");
				}
				this.expression = expression;
				opStack.push(TERMINATE_TOKENS.START_END_MARK);
				_tokens.addAll(TOKENIZER.exec(expression
						+ TERMINATE_TOKENS.START_END_MARK));
			} catch (Throwable e) {
				Log.e(LOG_TAG, String.format(
						"Incorret Expression: %s\nError: %s", expression,
						e.getMessage()));

				e.printStackTrace();
			}

			return _tokens;
		}

		@SuppressWarnings("rawtypes")
		public List<Double> getNumbers(String expression) {
			List<Double> _numbers = new ArrayList<Double>();

			// get and check tokens
			List _tokens = getTokens(expression);
			for (; i < _tokens.size(); i++) {
				final Object token = _tokens.get(i);
				if (token instanceof Double) {
					_numbers.add((Double) token);
				}
			}

			return _numbers;
		}

		@SuppressWarnings("rawtypes")
		public String exec(String expression) {
			String _result = null;

			try {
				clean();
				if (expression == null || expression.isEmpty()) {
					throw new IllegalArgumentException("Blank Expression!");
				}
				this.expression = expression;
				opStack.push(TERMINATE_TOKENS.START_END_MARK);
				List tokens = TOKENIZER.exec(expression
						+ TERMINATE_TOKENS.START_END_MARK);
				for (; i < tokens.size(); i++) {
					final Object token = tokens.get(i);
					if (token instanceof Double) {
						processOperand((Double) token);
					} else {
						processOperator((Character) token);
					}
				}

				// get the calculate result
				_result = numStack.peek().toString();
			} catch (Throwable e) {
				Log.e(LOG_TAG, String.format(
						"Incorret Expression: %s\nError: %s", expression,
						e.getMessage()));

				e.printStackTrace();
			}

			return _result;
		}

		@SuppressWarnings("rawtypes")
		public String exec(List tokens) {
			String _result = null;

			// check the tokens
			if (null != tokens) {
				clean();
				opStack.push(TERMINATE_TOKENS.START_END_MARK);
				for (; i < tokens.size(); i++) {
					final Object token = tokens.get(i);
					if (token instanceof Double) {
						processOperand((Double) token);
					} else {
						processOperator((Character) token);
					}
				}

				// get the calculate result
				_result = numStack.peek().toString();
			} else {
				Log.e(LOG_TAG, "Incorret Tokens: " + tokens + "\n");
			}

			return _result;
		}

		private void processOperand(final double operand) {
			numStack.push(operand);
		}

		private void processOperator(final char currentOperator) {
			this.currentOperator = currentOperator;
			this.opStackTop = opStack.peek();
			char calMode = CALCULATE_MODE.getRule(currentOperator, opStackTop);
			switch (calMode) {
			case '>':
				processStackHigerPriorityOperator();
				break;
			case '<':
				processStackLowerPriorityOperator();
				break;
			case '=':
				processStackEqualPriorityOperator();
				break;
			default:
				break;
			}
		}

		private void processStackLowerPriorityOperator() {
			opStack.push(currentOperator);
		}

		private void processStackHigerPriorityOperator() {
			numStack.push(CALCULATE.exec(opStack.pop(), numStack.pop(),
					numStack.pop()));
			--i; // pointer back to the previous operator.
		}

		private void processStackEqualPriorityOperator() {
			if (TERMINATE_TOKENS.START_END_MARK == currentOperator) {
				Log.d(LOG_TAG, expression + " = " + numStack.peek());
			} else if (')' == currentOperator) {
				opStack.pop();
			}
		}

		public void clean() {
			numStack.clear();
			opStack.clear();
			i = 0;
		}

		// inner class
		enum CALCULATE {
			INSTANCE;

			public static double exec(final char operator, final double right,
					final double left) {
				switch (operator) {
				case '+':
					return left + right;
				case '-':
					return left - right;
				case '*':
					return left * right;
				case '/':
					return left / right;
				default:
					throw new IllegalArgumentException("Unsupported operator: "
							+ operator);
				}
			}
		}

		enum TERMINATE_TOKENS {
			INSTANCE;

			public static final char START_END_MARK = '#';
			private static final Map<Character, Integer> TOKENs = new HashMap<Character, Integer>();

			static {
				// token, token id
				TOKENs.put('+', 0);
				TOKENs.put('-', 1);
				TOKENs.put('*', 2);
				TOKENs.put('/', 3);
				TOKENs.put('(', 4);
				TOKENs.put(')', 5);
				TOKENs.put(START_END_MARK, 6);
			}

			private static Set<Character> NEGATIVE_NUM_SENSITIVE = new HashSet<Character>();

			public static synchronized Set<Character> getNegativeNumSensitiveToken() {
				if (NEGATIVE_NUM_SENSITIVE.size() == 0) {
					NEGATIVE_NUM_SENSITIVE.addAll(TOKENs.keySet());
					NEGATIVE_NUM_SENSITIVE.remove(')');
				}
				return NEGATIVE_NUM_SENSITIVE;
			}

			public static boolean isTerminateToken(final char token) {
				Set<Character> keys = TOKENs.keySet();
				return keys.contains(token);
			}

			public static int getTokenId(final char token) {
				return TOKENs.get(token) == null ? -1 : TOKENs.get(token);
			}

			public static int getTokenSize() {
				return TOKENs.size();
			}

		}

		enum CALCULATE_MODE {
			INSTANCE;

			private static char[][] RULES = {
					// + - * / ( ) #
					{ '>', '>', '<', '<', '<', '>', '>' }, // +
					{ '>', '>', '<', '<', '<', '>', '>' }, // -
					{ '>', '>', '>', '>', '<', '>', '>' }, // *
					{ '>', '>', '>', '>', '<', '>', '>' }, // /
					{ '<', '<', '<', '<', '<', '=', 'o' }, // (
					{ '>', '>', '>', '>', 'o', '>', '>' }, // )
					{ '<', '<', '<', '<', '<', 'o', '=' }, // #
			};

			static {
				if (RULES.length != TERMINATE_TOKENS.getTokenSize()
						|| RULES.length < 1
						|| RULES[0].length != TERMINATE_TOKENS.getTokenSize()) {
					throw new IllegalArgumentException(
							"Rules matrix is incorrect!");
				}
			}

			public static char getRule(final char currentOperator,
					final char opStackTop) {
				try {
					return RULES[TERMINATE_TOKENS.getTokenId(opStackTop)][TERMINATE_TOKENS
							.getTokenId(currentOperator)];
				} catch (Throwable e) {
					throw new RuntimeException(
							"No rules were defined for some token!");
				}
			}
		}

		enum TOKENIZER {
			INSTANCE;

			private static final StringBuilder BUFFER = new StringBuilder();

			private static String clearExpression(String expression) {
				return expression.replaceAll(" ", "");
			}

			private static Character PREVIOUS_CHAR;

			private static void clean() {
				BUFFER.delete(0, BUFFER.length());
				PREVIOUS_CHAR = null;
			}

			private static boolean processNegativeNumbers(final String exp,
					final int index) {
				char c = exp.charAt(index);
				if (('+' == c || '-' == c)
						&& (PREVIOUS_CHAR == null || TERMINATE_TOKENS
								.getNegativeNumSensitiveToken().contains(
										PREVIOUS_CHAR))
						&& !TERMINATE_TOKENS.isTerminateToken(exp
								.charAt(index + 1))) {
					BUFFER.append(c);
					return true;
				}
				return false;
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public static List<?> exec(final String expression) {
				clean();
				String exp = clearExpression(expression);
				List result = new LinkedList();
				for (int i = 0; i < exp.length(); i++) {
					char c = exp.charAt(i);
					if (TERMINATE_TOKENS.isTerminateToken(c)) {
						if (processNegativeNumbers(exp, i))
							continue;
						if (BUFFER.length() > 0) {
							result.add(Double.valueOf(BUFFER.toString()));
							BUFFER.delete(0, BUFFER.length());
						}
						result.add(c);
					} else {
						BUFFER.append(c);
					}
					PREVIOUS_CHAR = c;
				}
				return Collections.unmodifiableList(result);
			}
		}

	}

}
