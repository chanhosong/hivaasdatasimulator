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
 * hsbae			2015. 4. 28.		First Draft.
 */
package com.hhi.vaas.platform.vdm.parser.kv;

/**
 * KeyValueItem class
 * 
 * @author hsbae
 *
 */

public class KeyValueItem {
	private String key;
	private String value;
	
	public KeyValueItem(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
//end of KeyValueItem.java