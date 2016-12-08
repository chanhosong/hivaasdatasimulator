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
package com.hhi.vaas.platform.agent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.enlist.EnlistHandler;
import com.hhi.vaas.platform.agent.health.Activator;
import com.hhi.vaas.platform.agent.health.HealthNotifier;
import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.receiver.AbstractDataReceiver;
import com.hhi.vaas.platform.agent.receiver.DataReceiver;
import com.hhi.vaas.platform.agent.receiver.DataReceiverFactory;
import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.agent.update.UpdateHandler;
import com.hhi.vaas.platform.agent.vcd.VCDHandler;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.handler.crypto.CryptoException;
import com.hhi.vaas.platform.vdm.handler.crypto.CryptoUtils;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import com.hhi.vaas.platform.vdm.parser.DataConverterFactory;

/**
 * <pre>
 * agent execution class
 * </pre>
 * 
 * @author BongJin Kwon
 * 
 */
public class AgentMain {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AgentMain.class);
	public static final String PROP_KEY_AGENT_ID = "vaas.agent.id";
	public static final String PROP_KEY_EQUIP_ID = "vaas.agent.equipment.id";

	/**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * The system separator character.
     */
    private static final char SYSTEM_SEPARATOR = File.separatorChar;
	
	/**
	 * start agent.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 0 || "start".equals(args[0])) {
            start(args);
        } else if ("stop".equals(args[0])) {
            stop(args);
        }
		
	}
	
	public static void start(String[] args) {
		
		System.out.println("========================");
		System.out.println("  starting VDIP Agent.");
		System.out.println("========================");
		
		PropertyService props = new PropertyService("");
		
		
		boolean enlistEnable = props.getProperty("vaas.agent.enlist.enable", Boolean.class, false);
		boolean healthNotiEnable = props.getProperty("vaas.agent.health.enable", Boolean.class, false);
		boolean forceActivate = props.getProperty("vaas.agent.force.activate", Boolean.class, false);
		
		
		try {
			
			props.put(PROP_KEY_AGENT_ID, createAgentId("agentId", props));
			LOGGER.info("agent id is {}", props.getProperty("vaas.agent.id"));
			
			RabbitMQSender sender = new RabbitMQSender(props);
			AgentStatus status = new AgentStatus(forceActivate);
			
			Activator activator = new Activator(sender, status);
			
			InputStream mInputStream = props.getResourceStreamFromKey("mapping.xml.path");
			InputStream vInputStream = VCDHandler.getVCDInputStream(props);
			DataConverter dataConverter = DataConverterFactory.create(mInputStream, vInputStream);
			
			/*
			 * 1. Enlisting
			 */
			props.put(PROP_KEY_EQUIP_ID, dataConverter.getSystemName());
			if(enlistEnable){
				new EnlistHandler(activator, props).process();
			}
	
			/*
			 * 2. Health noti
			 */
			if(healthNotiEnable){
				new Thread(new HealthNotifier(activator, props, new UpdateHandler(dataConverter))).start();
			}
	
			
			/*
			 * 3. transfer to middleware
			 */
			
			String recvType = props.getProperty("vaas.agent.type", String.class, "zmq");
			
			DataReceiver dataReceiver = DataReceiverFactory.create(recvType, dataConverter, sender, props, status);
			dataReceiver.start();
			
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
			System.exit(1);
		}
	}
	
	public static void stop(String[] args) {
    	LOGGER.info("stopping service.");
        AbstractDataReceiver.stopAgent();
    }
	
	/**
	 * create agent id.
	 * @return
	 */
	protected static String createAgentId(String idFilePrefix, PropertyService props) throws IOException {
		
		File agentIdFile = getAgentIdFile(idFilePrefix, props);
		
		String agentId = null;
		
		if (agentIdFile.exists()) {
			agentId = FileUtils.readFileToString(agentIdFile);
		} else {
			
			agentId = UUID.randomUUID().toString();
			agentIdFile.getParentFile().mkdir();
			FileUtils.writeStringToFile(agentIdFile, agentId);
		}
		
		return agentId;
	}
	
	protected static File getAgentIdFile(String idFilePrefix, PropertyService props) {
		String agentIdPath = System.getProperty("user.home") + File.separator + ".vdipAgent";
		System.out.println(agentIdPath);
		
		int listenPort = props.getProperty("vaas.agent.listen.port", Integer.class, 5558);
		File agentIdFile = new File(agentIdPath + File.separator + idFilePrefix + "_" + listenPort);
		
		return agentIdFile;
	}
	
	public static boolean isSystemWindows() {
        return SYSTEM_SEPARATOR == WINDOWS_SEPARATOR;
    }
	
}
// end of AgentMain.java