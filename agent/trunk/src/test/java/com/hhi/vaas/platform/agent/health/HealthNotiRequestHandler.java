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
 * Bongjin Kwon	2015. 5. 11.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.health;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.IOException;

/**
 * @author BongJin Kwon
 *
 */
public class HealthNotiRequestHandler implements HttpRequestHandler {

	/**
	 * 
	 */
	public HealthNotiRequestHandler() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.apache.http.protocol.HttpRequestHandler#handle(org.apache.http.HttpRequest, org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		// TODO Auto-generated method stub

	}

}
//end of HealthNotiRequestHandler.java