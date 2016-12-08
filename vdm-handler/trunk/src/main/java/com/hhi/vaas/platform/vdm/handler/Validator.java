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
 * Sang-cheon Park	2015. 5. 7.			First Draft.
 */
package com.hhi.vaas.platform.vdm.handler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.codehaus.jackson.JsonNode;

/**
 * <pre>
 * Validator class to check NMEA parsing data is validate or not
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class Validator {

	private VesselDataModel vdm;
	
	/**
	 * <pre>
	 * Constructor for String
	 * </pre>
	 * @param contents contents of Vessel Configuration Description
	 */
	public Validator(String contents) {
		this(contents.getBytes());
	}
	//end of Constructor()
	
	/**
	 * <pre>
	 * Constructor for Byte array
	 * </pre>
	 * @param contents contents of Vessel Configuration Description
	 */
	public Validator(byte[] contents) {
		this(new ByteArrayInputStream(contents));
	}
	//end of Constructor()
	
	/**
	 * <pre>
	 * Constructor for InputStrem
	 * </pre>
	 * @param is contents of Vessel Configuration Description
	 */
	public Validator(InputStream is) {
		vdm = VDMLoader.load(is);
	}
	//end of Constructor()
	
	/**
	 * <pre>
	 * validation check
	 * </pre>
	 * @param jsonNode jsonNode
	 * @return
	 */
	public boolean validate(JsonNode jsonNode) {
		return vdm.validate(jsonNode);
	}
	//end of validate()
	
}
//end of Validator.java