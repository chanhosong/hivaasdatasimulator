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
 * Sang-cheon Park	2015. 4. 15.		First Draft.
 */
package com.hhi.vaas.platform.middleware.adapter;


/**
 * <pre>
 * Enum object for Adapter Status Type
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public enum AdapterStatusType {

	START, 
	STOP,
	PAUSE;
	
    public String value() {
        return name();
    }

    public static AdapterStatusType fromValue(String v) {
        return valueOf(v);
    }
}
//end of AdapterStatusType.java