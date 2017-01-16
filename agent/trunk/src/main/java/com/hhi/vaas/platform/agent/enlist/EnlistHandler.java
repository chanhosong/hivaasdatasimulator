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

import com.hhi.vaas.platform.agent.AgentMain;
import com.hhi.vaas.platform.agent.health.Activator;
import com.hhi.vaas.platform.agent.http.HttpHandler;
import com.hhi.vaas.platform.agent.model.Authentication;
import com.hhi.vaas.platform.agent.vcd.VCDHandler;
import com.hhi.vaas.platform.middleware.common.util.JSONUtil;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.handler.crypto.CryptoException;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.conn.HttpHostConnectException;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * <pre>
 *  Agent Enlist Handler
 * </pre>
 * 
 * @author BongJin Kwon
 *
 */
public class EnlistHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EnlistHandler.class);

	private HttpHandler httpHandler = new HttpHandler();
	
	private Activator activator;
	private PropertyService props;
	

	public EnlistHandler(Activator activator, PropertyService props) {
		this.activator = activator;
		this.props = props;
	}
	
	public void setHttpHandler(HttpHandler httpHandler) {
		this.httpHandler = httpHandler;
	}


	/**
	 *  Enlisting to manager
	 * @param status
	 * @param props
	 */
	public void process()throws IOException, ClientProtocolException, CryptoException {
		
		String managerEnlistUri = props.getProperty("vaas.manager.host") + props.getProperty("vaas.manager.enlist.uri");
		String equipmentId = props.getProperty(AgentMain.PROP_KEY_EQUIP_ID);
		String agentId = props.getProperty(AgentMain.PROP_KEY_AGENT_ID);
		String mappingXml = loadMappingXml();
		String vcdXml = VCDHandler.getVCDContents(props);
		String agentUpdateNoti = getAgentUpdateNoti();
		
		/*
		 * call rest api 
		 */
		Content returnContent = null;
		try {
			returnContent = httpHandler.requestEnlistPost(
					managerEnlistUri, 
					Form.form()
					.add("equipmentId", equipmentId)
					.add("agentId", agentId)
					.add("mappingXml", mappingXml)
					.add("vcd", vcdXml)
					.add("agentUpdateNoti", agentUpdateNoti)
					.build()
					);
		} catch (HttpHostConnectException e) {
			LOGGER.error(e.toString(), e);
			throw new RuntimeException("Platform is not running. must be running.");
		}
		
		/*
		 * parsing json response
		 */
		String json = returnContent.asString();
		LOGGER.info("json : {}", json);
		
		JsonNode jsonObj = JSONUtil.readTree(json);
		
		/*
		 * check response
		 */
		if ("ok".equals(jsonObj.get("result").asText())) {
			
			LOGGER.debug("enlist ok.");
			
			JsonNode activation = jsonObj.get("activation");
			if (activation != null) {
				activator.activate(new Authentication(activation.get("username").asText(), activation.get("passwd").asText()), props);
			}
			
		} else {
			throw new RuntimeException("Agent Enlisting is fail. caused by manager server error.");
		}
		
	}
	
	/**
	 * loading and return Mapping XML file
	 * @return Mapping XML String
	 */
	public String loadMappingXml() throws IOException {
		
		String mappingXMLPath = props.getProperty("mapping.xml.path");
		
		LOGGER.info("mappingXMLPath : {}", mappingXMLPath);
		
		File mxFile = FileUtils.toFile(EnlistHandler.class.getResource(mappingXMLPath));
		
		return FileUtils.readFileToString(mxFile);
	}
	
	protected String getAgentUpdateNoti(){
		File updateFile = new File(System.getProperty("user.dir") + File.separator + "updateSuccess");
		if (updateFile.exists()) {
			
			LOGGER.info("agentUpdateNoti=Y");
			FileUtils.deleteQuietly(updateFile);
			
			return "Y";
		} else {
			return "N";
		}
	}

}
//end of EnlistHandler.java