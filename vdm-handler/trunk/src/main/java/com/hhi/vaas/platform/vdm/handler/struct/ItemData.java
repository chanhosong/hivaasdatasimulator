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
 * Bongjin Kwon	2015. 5. 28.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler.struct;

/**
 * @author BongJin Kwon
 *
 */
public class ItemData {

	private long timestamp;
	private String value;
	private int valid; // 1: true, 0: false or daubt or NA
	
	public ItemData(long timestamp, String value, int valid) {
		this.timestamp = timestamp;
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
//end of VDMPathDatas.java