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
package com.hhi.vaas.platform.agent.health;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.update.UpdateHandler;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;

/**
 * @author BongJin Kwon
 *
 */
public class HealthNotifier implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(HealthNotifier.class);
	
	private AgentStatus status;
	private PropertyService props;
	private UpdateHandler updateHandler;

	/**
	 * 
	 */
	public HealthNotifier(AgentStatus status, PropertyService props, UpdateHandler updateHandler) {
		this.status = status;
		this.props = props;
		this.updateHandler = updateHandler;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String managerHealthUri = props.getProperty("vaas.manager.host") + props.getProperty("vaas.manager.heath.uri");
		String equipmentId = props.getProperty("equipment.id");
		
		
		while(true){
			
			try{
				notifyHealth(managerHealthUri, equipmentId);
				
			}catch(Exception e){
				logger.error(e.toString(), e);
			}
		}

	}
	
	/**
	 * notify health for agent
	 * 
	 * @param managerHealthUri
	 * @param equipmentId
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private void notifyHealth(String managerHealthUri, String equipmentId) throws IOException, ClientProtocolException{
		
		Content returnContent = Request.Post(managerHealthUri).bodyForm(
				Form.form().add("equipmentId", equipmentId).build()
		).execute().returnContent();
		
		
		if(ContentType.APPLICATION_JSON.equals( returnContent.getType() )){
			
			//TODO 응답이 JSON 이면 인증정보임 (username, password). AgentStatus 에 Authentication 셋팅.
			
		}else if(ContentType.TEXT_XML.equals( returnContent.getType() )){
			
			//updating mapping xml.
			updateHandler.updateMapping(returnContent.asString());
			
			
		}else{
			// no response.
		}
	}

}
//end of HealthNotifier.java