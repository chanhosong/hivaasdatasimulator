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
package com.hhi.vaas.platform.agent.model;

/**
 * <pre>
 * authentication from middleware (maybe rabbmq authentication) 
 * </pre>
 * @author BongJin Kwon
 *
 */
public class Authentication {

	private String username;
	private String passwordd;
	
	public Authentication() {
		// TODO Auto-generated constructor stub
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordd() {
		return passwordd;
	}

	public void setPasswordd(String passwordd) {
		this.passwordd = passwordd;
	}

}
//end of Authentication.java