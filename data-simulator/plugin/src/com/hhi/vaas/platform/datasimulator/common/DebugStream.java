
/* Copyright (C) eMarine Co. Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of eMarine Co. Ltd.
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with eMarine Co. Ltd.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * ddong			2015. 8. 12.				First Draft.
 */
package com.hhi.vaas.platform.datasimulator.common;

import java.io.PrintStream;
import java.text.MessageFormat;

public class DebugStream extends PrintStream {
	private static final DebugStream INSTANCE = new DebugStream();
	 
	public static void activate() {
		System.setOut(INSTANCE);
	}
 
	private DebugStream() {
		super(System.out);
	}
 
	@Override
	public void println(Object x) {
		showLocation();
		super.println(x);
	}
 
	@Override
	public void println(String x) {
		showLocation();
		super.println(x);
	}
 
	private void showLocation() {
		StackTraceElement element = Thread.currentThread().getStackTrace()[3];
		super.print(MessageFormat.format("({0}:{1, number,#}) : ", element.getFileName(), element.getLineNumber()));
	}
}
//end of DebugStream.java