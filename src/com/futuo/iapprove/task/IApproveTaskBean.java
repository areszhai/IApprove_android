package com.futuo.iapprove.task;

import java.io.Serializable;

public class IApproveTaskBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8661391503415942906L;

	// task id, title, applicant name, submit timestamp, status
	private Long id;
	private String title;
	private String applicantName;
	private Long submitTimestamp;

}
