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
 * Bongjin Kwon	2015. 4. 9.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler;

import com.hhi.vaas.platform.vdm.handler.validation.DataValidator;

/**
 * @author BongJin Kwon
 *
 */
public abstract class VDMConstants {

	public static final String NODE_NAME_CONNECTIVITY = "ConnectivityNode";
	public static final String NODE_NAME_SYSTEM = "System";		// hsbae_150510
	public static final String NODE_NAME_LDEVICE = "LDevice";
	public static final String NODE_NAME_LN = "LN";
	public static final String NODE_NAME_DO = "DO";
	public static final String NODE_NAME_DA = "DA";
	public static final String NODE_NAME_SDO = "SDO";
	public static final String NODE_NAME_BDA = "BDA";
	
	public static final String NODE_ATTR_INST = "inst";
	public static final String NODE_ATTR_LNTYPE = "lnType";
	public static final String NODE_ATTR_NAME = "name";
	public static final String NODE_ATTR_TYPE = "type";
	public static final String NODE_ATTR_BTYPE = "bType";
	
	/**
	 * The other bTypes is defined at DataValidator
	 * @see DataValidator
	 */
	public static final String NODE_BTYPE_STRUCT = "Struct";
	
	
	public static final String DELIMITER1 = "/";
	public static final String DELIMITER2 = ".";
	
}
//end of VDMConstants.java