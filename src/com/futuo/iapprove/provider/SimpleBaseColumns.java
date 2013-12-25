package com.futuo.iapprove.provider;

import android.provider.BaseColumns;

public interface SimpleBaseColumns extends BaseColumns {

	// selection "and" string
	public static final String _AND_SELECTION = " and ";

	// data count projection
	public static final String _COUNT_PROJECTION = "count(*) as " + _COUNT;

}
