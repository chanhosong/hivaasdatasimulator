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
 * Bongjin Kwon	2015. 4. 29.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.model;

import java.io.IOException;

import com.hhi.vaas.platform.middleware.common.util.JSONUtil;

/**
 * @author BongJin Kwon
 *
 */
public class BackupMessage {

	private String routingKey;
	private String body;
	private String regDt;
	
	public BackupMessage() {
		// TODO Auto-generated constructor stub
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getRegDt() {
		return regDt;
	}

	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}
	
	public String toString(){
		
		String str = null;
		try{
			str = JSONUtil.objToJson(this);
		}catch(IOException e){
			str = routingKey + "," + body + "," + regDt;
		}
		
		return str;
	}

}
//end of BackupMessage.java