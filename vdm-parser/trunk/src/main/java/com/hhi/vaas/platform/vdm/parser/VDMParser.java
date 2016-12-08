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
 * Bongjin Kwon	2015. 3. 30.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser;

import com.hhi.vaas.platform.vdm.handler.VesselDataModel;


/**
 * Parser Interface
 * @author BongJin Kwon
 *
 */
public interface VDMParser {
	
	/**
	 * Check whether this parser supports the data
	 * @param rawData
	 * @return
	 */
	boolean supports(String rawData);

	/**
	 * raw data parse
	 * 
	 * @param rawData
	 * @param vdmMapping
	 * @param vdm
	 * @return
	 */
	<T> T parse(String rawData, VDMMapping vdmMapping, VesselDataModel vdm);
	
}
//end of VDMParser.java