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
 * Bongjin Kwon	2015. 3. 25.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.update;

import com.hhi.vaas.platform.vdm.parser.DataConverter;

/**
 * <pre>
 * 
 * updating agent
 *  - updating mapping xml
 * </pre>
 * @author BongJin Kwon
 *
 */
public class UpdateHandler {

	private DataConverter dataConverter;
	/**
	 * 
	 */
	public UpdateHandler(DataConverter dataConverter) {
		this.dataConverter = dataConverter;
	}
	
	public void updateMapping(String mappingXMLStr){
		
		
		//TODO dataConverter.updateMapping(vmdMapping);
		
	}

}
//end of UpdateHandler.java