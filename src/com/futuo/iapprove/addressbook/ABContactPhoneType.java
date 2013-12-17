package com.futuo.iapprove.addressbook;

public enum ABContactPhoneType {

	// mobile, office and others
	MOBILE(0), OFFICE(1), OTHERS(3);

	// phone type value and label
	private Integer phoneTypeValue;
	private String phoneTypeLabel;

	// private constructor
	private ABContactPhoneType(Integer value) {
		// save phone type value and and initialize label
		phoneTypeValue = value;
		phoneTypeLabel = null;
	}

	public Integer getPhoneTypeValue() {
		return phoneTypeValue;
	}

	public String getPhoneTypeLabel() {
		return phoneTypeLabel;
	}

}
