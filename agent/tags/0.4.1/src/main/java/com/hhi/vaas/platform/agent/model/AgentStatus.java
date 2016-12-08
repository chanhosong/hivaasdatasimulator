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
 * Bongjin Kwon	2015. 3. 24.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.model;

/**
 * agent stauts
 * @author BongJin Kwon
 *
 */
public class AgentStatus {

	private Authentication authentication;
	
	public AgentStatus() {
		// TODO Auto-generated constructor stub
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}
	
	public boolean isActivated(){
		return authentication != null;
	}

}
//end of AgentStatus.java