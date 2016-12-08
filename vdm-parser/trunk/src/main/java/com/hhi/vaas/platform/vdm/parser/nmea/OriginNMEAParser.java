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
 * Bongjin Kwon	2015. 4. 1.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser.nmea;

/**
 * @author BongJin Kwon
 *
 */
public interface OriginNMEAParser {
	
	
	public int getItemCount();
	
	public String getString(int index);
	
	public String getSentenceId();

	
}
//end of NMEAParser.java