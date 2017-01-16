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

import com.hhi.vaas.platform.agent.AgentMain;
import com.hhi.vaas.platform.agent.http.HttpHandler;
import com.hhi.vaas.platform.agent.model.Authentication;
import com.hhi.vaas.platform.agent.receiver.AbstractDataReceiver;
import com.hhi.vaas.platform.agent.update.UpdateHandler;
import com.hhi.vaas.platform.agent.update.UpdateResultModel;
import com.hhi.vaas.platform.middleware.common.util.JSONUtil;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author BongJin Kwon
 *
 */
public class HealthNotifier implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HealthNotifier.class);
	
	private HttpHandler httpHandler = new HttpHandler();
	private Activator activator;
	private PropertyService props;
	private UpdateHandler updateHandler;
	
	private String managerAddr;
	private String managerHealthUri;
	private String agentId;
	private String downPath;
	
	/**
	 * 
	 */
	public HealthNotifier(Activator activator, PropertyService props, UpdateHandler updateHandler) {
		this.activator = activator;
		this.props = props;
		this.updateHandler = updateHandler;
		this.updateHandler.setProps(props);
		
		this.managerAddr = props.getProperty("vaas.manager.host");
		this.managerHealthUri = managerAddr + props.getProperty("vaas.manager.heath.uri");
		this.agentId = props.getProperty(AgentMain.PROP_KEY_AGENT_ID);
		
		if(AgentMain.isSystemWindows()){
			this.downPath = "C:/Temp" + props.getProperty("vaas.agent.down.path", String.class, "/agentUpdate");
		} else {
			this.downPath = "/tmp" + props.getProperty("vaas.agent.down.path", String.class, "/agentUpdate");
		}
	}
	

	public void setUpdateHandler(UpdateHandler updateHandler) {
		this.updateHandler = updateHandler;
	}



	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		long notiSleep = props.getProperty("vaas.agent.noti.sleep", Long.class, 30000L);
		
		while (AbstractDataReceiver.isStoped() == false) {
			
			try {
				Thread.sleep(notiSleep);
				
				notifyHealth(managerHealthUri, agentId);
				
			} catch (Exception e) {
				LOGGER.error(e.toString(), e);
			}
		}
		LOGGER.info("stopped.");
	}
	
	/**
	 * notify health for agent
	 * 
	 * @param managerHealthUri
	 * @param agentId
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	protected void notifyHealth(String managerHealthUri, String agentId) throws IOException, ClientProtocolException{
		
		long agentTime = System.currentTimeMillis();
		
		Content returnContent = httpHandler.requestNotiPost(
				managerHealthUri, 
				Form.form().add("agentId", agentId).add("agentTime", String.valueOf(agentTime)).build()
				);
		String mimeType = returnContent.getType().getMimeType();
		
		
		if (ContentType.APPLICATION_JSON.getMimeType().equals(mimeType)) {
			
			String json = returnContent.asString();
			LOGGER.info("json : {}", json);
			
			JsonNode activation = JSONUtil.readTree(json);
			if (activation != null && activation.get("username") != null) {
				activator.activate(new Authentication(activation.get("username").asText(), activation.get("passwd").asText()), props);
				
			} else if (activation.get("downloadUrl") != null) {
				
				String downURL = activation.get("downloadUrl").asText();
				String downFilePath = downPath + downURL.substring(downURL.lastIndexOf("/"));
				
				if (new File(downFilePath).exists()) {
					LOGGER.warn("{} is already downloaded.");
				} else {
					httpHandler.download(managerAddr + downURL, downFilePath);
				}
			}
			
		} else if (ContentType.TEXT_XML.getMimeType().equals(mimeType)) {
			
			String xml = returnContent.asString();
			
			LOGGER.info("xml : {}", xml);
			UpdateResultModel result = null;
			
			if (xml.indexOf("<MapData>") > -1) {
				//updating mapping xml.
				result = updateHandler.updateMapping(xml);
			} else {
				result = updateHandler.updateVCD(xml);
			}
			
			httpHandler.requestUpdateResultPost(managerHealthUri, updateResultParams(agentId, result));
			
		} else {
			LOGGER.info("res : {}, {}", mimeType, returnContent.asString());
		}
	}
	
	private List<NameValuePair> updateResultParams(String agentId, UpdateResultModel result){
		
		return Form.form()
				.add("agentId", agentId)
				.add("updateSuccess", Boolean.toString( result.isSuccess() ))
				.add("updateErrorMessage", result.getErrorMessage())
				.build();
	}

}
//end of HealthNotifier.java