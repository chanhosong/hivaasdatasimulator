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
package com.hhi.vaas.platform.agent.enlist;

import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author BongJin Kwon
 *
 */
public class EnlistRequestHandler implements HttpRequestHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EnlistRequestHandler.class);

	private String resJson;
	
	private String agentId;
	private String mappingXML;
	private String requestParamStr;
	/**
	 * 
	 */
	public EnlistRequestHandler() {
	}

	/* (non-Javadoc)
	 * @see org.apache.http.protocol.HttpRequestHandler#handle(org.apache.http.HttpRequest, org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		
		
		HttpEntity requestEntity = ((HttpEntityEnclosingRequest) request).getEntity();
		
		requestParamStr = EntityUtils.toString(requestEntity);
		
		LOGGER.debug("entity: {}", requestParamStr);
		
		agentId = getParameter("agentId");
		mappingXML = getParameter("mappingXml");
		
		LOGGER.debug("agentId is {}", agentId);
		
		
		
		response.setEntity(new StringEntity(resJson, ContentType.APPLICATION_JSON));

	}

	public void setResponseJson(String resJson) {
		this.resJson = resJson;
	}

	public String getAgentId() {
		return agentId;
	}

	public String getMappingXML() {
		return mappingXML;
	}
	
	private String getParameter(String name){
		int pos = requestParamStr.indexOf(name);
		
		if( pos == -1){
			return null;
		}
		
		int endPos = requestParamStr.indexOf("&", pos);
		
		if(endPos == -1){
			endPos = requestParamStr.length();
		}
		
		return requestParamStr.substring(pos + name.length() + 1, endPos);
	}
	
}
//end of EnlistRequestHandler.java