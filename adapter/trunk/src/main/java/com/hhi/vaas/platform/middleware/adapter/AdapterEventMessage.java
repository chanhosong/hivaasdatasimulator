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
 * Event message class to control adapter thread
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class AdapterEventMessage {
    
	/** Adapter's UDP Listen Port */
	private int port;
	/** Adapter Status Type as command */
	private AdapterStatusType status;
	/** Check event message is handled by adapter */
	private boolean handled;
	
	public AdapterEventMessage(int port, AdapterStatusType status) {
		this.port = port;
		this.status = status;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the status
	 */
	public AdapterStatusType getStatus() {
		return status;
	}

	/**
	 * @return the handled
	 */
	public boolean isHandled() {
		return handled;
	}

	/**
	 * @param handled the handled to set
	 */
	public void setHandled(boolean handled) {
		this.handled = handled;
	}
}
//end of AdapterEventMessage.java