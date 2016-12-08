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
package com.hhi.vaas.platform.agent.enlist;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;

/**
 * <pre>
 *  Agent Enlist Handler
 * </pre>
 * 
 * @author BongJin Kwon
 *
 */
public class EnlistHandler {


	/**
	 *  Enlisting to manager
	 * @param status
	 * @param props
	 */
	public static void process(AgentStatus status, PropertyService props)throws IOException, ClientProtocolException{
		
		String managerHost = props.getProperty("vaas.manager.host");
		String equipmentId = props.getProperty("equipment.id");
		String mappingXml = loadMappingXml();
		
		/*
		 * call rest api 
		 */
		String responseStr = Request.Post(managerHost + props.getProperty("vaas.manager.enlist.uri")).bodyForm(
				Form.form().add("equipmentId", equipmentId).add("mappingXml", mappingXml).build()
		).execute().returnContent().asString();
		
		
		//TODO responseStr 를 JSON Object 로 변환하고 (use JSONUtil in common-lib)
		
		//TODO Authentication 이 있는지 확인. 있으면 AgentStatus 에 setting.
		
	}
	
	/**
	 * loading and return Mapping XML file
	 * @return Mapping XML String
	 */
	public static String loadMappingXml(){
		
		
		
		return "";
	}

}
//end of EnlistHandler.java