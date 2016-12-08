/* Copyright (C) 2015~ Hyundai Heavy Industries. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Hyundai Heavy Industries
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with Hyundai Heavy Industries.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * sehwan			2015. 6. 18.				First Draft.
 */

package com.hhi.vaas.platform.datasimulator.common;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class CommonMethodes {
	
	public CommonMethodes() { }
	
	public static boolean isLongValue(String s) {
		try {
			Long.parseLong(s);
			return true;
		} 	catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static void warningMessageBox(String message, Shell shell) {
		MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.ICON_WARNING);  
        dialog.setText("Warning!");			          
        dialog.setMessage(message);  
        dialog.open();
	}

	public static boolean directoryMake(String path) {
		File dir = new File(path);
		
		if (!dir.isDirectory()) {
			if (!dir.mkdirs()) {
				return false;
			}
		}		
		return true;
	}
	
	public static boolean fileMake(String path) throws IOException {
		File file = new File(path);
		
		if(!file.exists()) {
			if(!file.createNewFile()) {
				return false;
			}
		}		
		return true;
	}
	
	public static boolean checkIpAddress(String ipAddress) {
		Pattern pattern;
		String regex = "((([0-9])|([1-9]\\d{1})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}(([0-9])|([1-9]\\d{1})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))";
		boolean isTrue = false;
		
		pattern = Pattern.compile(regex);
		
		isTrue = pattern.matcher(ipAddress).matches();
		
		return isTrue;
	}
	
	public static boolean checkPortNbr(String portNbr) {
		Pattern pattern;
		String regex = "^(6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|[1-9][0-9]{1,3}|[0-9])$";
		boolean isTrue = false;
		
		pattern = Pattern.compile(regex);
		
		isTrue = pattern.matcher(portNbr).matches();
		
		return isTrue;
	}
	
	public static boolean checkPeriod(String period) {
		Pattern pattern;
		String regex = "^[1-9][0-9]*$";
		boolean isTrue = false;
		
		pattern = Pattern.compile(regex);
		
		isTrue = pattern.matcher(period).matches();
		
		return isTrue;
	}
}
//end of CommonMethodes.java