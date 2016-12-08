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
 * Sang-cheon Park	2015. 4. 7.			First Draft.
 */
package com.hhi.vaas.platform.middleware.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.Properties;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.middleware.metadata.KeyValueDomain;
import com.hhi.vaas.platform.middleware.metadata.MetadataQueryDomain;
import com.hhi.vaas.platform.middleware.metadata.MetadataService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * <pre>
 * Adapter manager class to manage udp adapters
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class AdapterManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdapterManager.class);
	private static final String AGENT_LIST_METADATA_KEY = "agent_list";
	
	private static AdapterManager manager;
	
	private Map<Integer, AdapterThread> agentMap = new ConcurrentHashMap<Integer, AdapterThread>();
	private MetadataService metadataService;
	
	private MBassador<AdapterEventMessage> eventBus;
	
	/**
	 * <pre>
	 * Default constructor
	 * </pre>
	 * @param propertyService propertyService
	 */
	private AdapterManager(PropertyService propertyService) {
		this.metadataService = new MetadataService(propertyService);
		
		/**
		 * Initialize event bus for adapter thread control
		 */
		IBusConfiguration configuration = new BusConfiguration()
	        .addFeature(Feature.SyncPubSub.Default())
	        .addFeature(Feature.AsynchronousHandlerInvocation.Default())
	        .addFeature(Feature.AsynchronousMessageDispatch.Default())
	        .setProperty(Properties.Handler.PublicationError, new AdapterPublicationErrorHandler());

		eventBus = new MBassador<AdapterEventMessage>(configuration);
	}
	//end of constructor()
	
	/**
	 * <pre>
	 * Get singleton instance
	 * </pre>
	 * @param propertyService propertyService
	 * @return
	 */
	public synchronized static AdapterManager getInstance(PropertyService propertyService) {
		if (manager == null) {
			manager = new AdapterManager(propertyService);
		}
		
		return manager;
	}
	//end of getInstance()
	
	/**
	 * <pre>
	 * Initializing Manager
	 * </pre>
	 * @param propertyService propertyService
	 * @throws VaasAdapterException 
	 */
	public void init() throws VaasAdapterException {		
		/**
		{
			"key" : "agent_list",
			"value" : {
				"agentType" : "adapter",
				"agentId" : "11111111",
				"equipmentId" : "equipment_1",
				"interfaceIp" : "192.168.0.71",
				"multicastIp" : "239.0.112.1",
				"port" : 5001,
				"status" : "[START | PAUSE | STOP]",
				"mappingXml" : "<?xml ...><Mappings>...</Mappings>",
				"regDt" : {
		            "$date": 1427442916000
		        }
			}
		}
		*/
		
		/**
		 * Get All Agent List 
		 */
		MetadataQueryDomain query = new MetadataQueryDomain();
		query.addQuery(new KeyValueDomain("value.agentType", "adapter"));
		List<DBObject> agentList = metadataService.select(AGENT_LIST_METADATA_KEY, query);
		
		String status = null;
		Map<String, Object> agentInfo = null;
		BasicDBObject obj = null;
		for (DBObject agent : agentList) {
			try {
				obj = (BasicDBObject) agent.get("value");
				
				status = (String) obj.get("status");
				
				/**
				 * Start UDP Adapter that has a START status in Metadata
				 */				
				if (status.equals(AdapterStatusType.START.toString())) {
					agentInfo = new HashMap<String, Object>();
					agentInfo.put("agentType", (String) obj.get("agentType"));
					agentInfo.put("agentId", (String) obj.get("agentId"));
					agentInfo.put("equipmentId", (String) obj.get("equipmentId"));
					agentInfo.put("processType", (String) agent.get("processType"));
					agentInfo.put("interfaceIp", (String) obj.get("interfaceIp"));
					agentInfo.put("multicastIp", (String) obj.get("multicastIp"));
					agentInfo.put("port", (Integer) obj.get("port"));
					agentInfo.put("status", status);
					agentInfo.put("mappingXml", (String) obj.get("mappingXml"));
					agentInfo.put("regDt", Calendar.getInstance().getTime());
					
					createAdapter(agentInfo);
				}
			} catch (Exception e) {
				// NullpointerException or IllegalArgumentException
				// nothing to do
				LOGGER.error("Unhandled exception has occurred during parsing metadata : ", e);
			}
		}
	}
	//end of init()
	
	/**
	 * <pre>
	 * Terminate all AdapterThreads
	 * </pre>
	 */
	public synchronized void stopAll() {
        List<Integer> portList = new ArrayList<Integer>(agentMap.keySet());
        
        AdapterEventMessage message = null;
        for (Integer port : portList) {
        	message = new AdapterEventMessage(port, AdapterStatusType.STOP);
            eventBus.publish(message);
            
            if (message.isHandled()) {
            	agentMap.remove(port);
            }
        }
	}
	//end of stopAll()
	
	/**
	 * <pre>
	 * Create a new AdpaterThread and run
	 * </pre>
	 * @param agentId
	 * @throws VaasAdapterException
	 */
	public void createAdapter(String agentId) throws VaasAdapterException {
		MetadataQueryDomain query = new MetadataQueryDomain();
		query.addQuery(new KeyValueDomain("value.agentType", "adapter"));
		query.addQuery(new KeyValueDomain("value.agentId", agentId));
		
		DBObject agent = null;
		
		try {
			agent = (DBObject) metadataService.selectOne(AGENT_LIST_METADATA_KEY, query);
			
			if (agent == null) {
	    		throw new Exception("Could not find the document.");
			}

			Map<String, Object> adapterInfo = null;
			adapterInfo = new HashMap<String, Object>();
			adapterInfo.put("agentType", (String) agent.get("agentType"));
			adapterInfo.put("agentId", agentId);
			adapterInfo.put("equipmentId", (String) agent.get("equipmentId"));
			adapterInfo.put("processType", (String) agent.get("processType"));
			adapterInfo.put("interfaceIp", (String) agent.get("interfaceIp"));
			adapterInfo.put("multicastIp", (String) agent.get("multicastIp"));
			adapterInfo.put("port", (Integer) agent.get("port"));
			adapterInfo.put("mappingXml", (String) agent.get("mappingXml"));
			adapterInfo.put("regDt", Calendar.getInstance().getTime());
					
			createAdapter(adapterInfo);
		} catch (Exception e) {
    		throw new VaasAdapterException(e.getMessage());
		}
	}
	//end of createAdapter()
	
	/**
	 * <pre>
	 * Create a new AdpaterThread and run
	 * </pre>
	 * @param agentInfo UDP Adapter Infor(listening port, equipment name, mapping xml, etc)
	 * @throws VaasAdapterException
	 */
	public synchronized void createAdapter(Map<String, Object> agentInfo) throws VaasAdapterException {
		String agentType = (String) agentInfo.get("agentType");
		String agentId = (String) agentInfo.get("agentId");
		String processType = (String) agentInfo.get("processType");
		String interfaceIp = (String) agentInfo.get("interfaceIp");
		String multicastIp = (String) agentInfo.get("multicastIp");
		Integer port = (Integer) agentInfo.get("port");
		String mappingXml = (String) agentInfo.get("mappingXml");
		
		if ("".equals(interfaceIp)) {
			interfaceIp = null;
		}
		if ("".equals(multicastIp)) {
			multicastIp = null;
		}
		
		/**
		 * agentType check
		 */
		if (!"adapter".equals(agentType)) {
			throw new VaasAdapterException("[" + agentType + "] is invalid agentType for adapter.");
		}
		
		/**
		 *  port duplication check
		 */
		if (agentMap.get(port) != null) {
			throw new VaasAdapterException("Port " + port + " already in use.");
		}
		
		/**
		 * Create a new AdapterThread and add to EventBus to handle the event.
		 */
		AdapterThread adapter = new AdapterThread(agentId, processType, interfaceIp, multicastIp, port, mappingXml, metadataService);
		eventBus.subscribe(adapter);
		
		/**
		 * Start a AdapterThread
		 */
		adapter.start();
		
		/**
		 * Put thread info to map
		 */
		agentMap.put(adapter.getPort(), adapter);
		
		/*
		// update agent's info to metadata (if doesn't exist, insert)
		MetadataQueryDomain query = new MetadataQueryDomain();
    	query.addQuery(new KeyValueDomain("value.agentId", agentId));
    	query.addQuery(new KeyValueDomain("value.agentType", "adapter"));
    	
    	agentInfo.put("status", AdapterStatusType.START.value());
    	
		metadataService.update(AGENT_LIST_METADATA_KEY, query, agentInfo, true, false);
		//*/
		
		updateStatus(port, AdapterStatusType.START);
		
        LOGGER.info("A new UDP adapter[{}] started on port [{}].", agentId, port);
	}
	//end of createAdapter()
	
	/**
	 * <pre>
	 * Update all agents
	 * </pre>
	 * @throws VaasAdapterException
	 */
	public void updateAll() throws VaasAdapterException {
		/**
		 * Get All Agents information 
		 */
		MetadataQueryDomain mqd = new MetadataQueryDomain();
		mqd.addQuery(new KeyValueDomain("value.agentType", "adapter"));
		List<DBObject> agentList = metadataService.select(AGENT_LIST_METADATA_KEY, mqd);
		
		String status = null;
		Map<String, Object> agentMap = null;
		BasicDBObject obj = null;
		for (DBObject agent : agentList) {
			try {
				obj = (BasicDBObject) agent.get("value");
				
				status = (String) obj.get("status");
				
				/**
				 * Start UDP Adapter that has a START or PAUSE status in Metadata
				 */				
				if (status.equals(AdapterStatusType.START.toString()) || status.equals(AdapterStatusType.PAUSE.toString())) {
					agentMap = new HashMap<String, Object>();
					agentMap.put("agentType", (String) obj.get("agentType"));
					agentMap.put("agentId", (String) obj.get("agentId"));
					agentMap.put("equipmentId", (String) obj.get("equipmentId"));
					agentMap.put("processType", (String) agent.get("processType"));
					agentMap.put("interfaceIp", (String) obj.get("interfaceIp"));
					agentMap.put("multicastIp", (String) obj.get("multicastIp"));
					agentMap.put("oldPort", (Integer) obj.get("port"));
					agentMap.put("port", (Integer) obj.get("port"));
					agentMap.put("status", status);
					agentMap.put("mappingXml", (String) obj.get("mappingXml"));
					agentMap.put("regDt", Calendar.getInstance().getTime());
					
					updateAdapter(agentMap);
				}
			} catch (Exception e) {
				// NullpointerException or IllegalArgumentException
				// nothing to do
				LOGGER.error("Unhandled exception has occurred during parsing metadata : ", e);
			}
		}
	}
	//end of updateAll()
	
	/**
	 * <pre>
	 * Update a agent information
	 * </pre>
	 * @param agentInfo UDP Adapter Info(equipment id, mapping xml, etc)
	 * @throws VaasAdapterException
	 */
	public void updateAdapter(int oldPort, String agentId) throws VaasAdapterException {
		MetadataQueryDomain query = new MetadataQueryDomain();
		query.addQuery(new KeyValueDomain("value.agentType", "adapter"));
		query.addQuery(new KeyValueDomain("value.agentId", agentId));
		
		DBObject agent = null;
		
		try {
			agent = (DBObject) metadataService.selectOne(AGENT_LIST_METADATA_KEY, query);
			
			if (agent == null) {
	    		throw new Exception("Could not find the document.");
			}
			
			int port = (Integer) agent.get("port");
			String status = (String) agent.get("status");
			
			/**
			 *  port duplication check
			 */
			if (oldPort != port && agentMap.get(port) != null) {
				throw new VaasAdapterException("Port " + port + " already in use.");
			}

			stop(oldPort);

			Map<String, Object> agentInfo = null;
			agentInfo = new HashMap<String, Object>();
			agentInfo.put("agentType", (String) agent.get("agentType"));
			agentInfo.put("agentId", agentId);
			agentInfo.put("equipmentId", (String) agent.get("equipmentId"));
			agentInfo.put("processType", (String) agent.get("processType"));
			agentInfo.put("interfaceIp", (String) agent.get("interfaceIp"));
			agentInfo.put("multicastIp", (String) agent.get("multicastIp"));
			agentInfo.put("port", (Integer) agent.get("port"));
			agentInfo.put("mappingXml", (String) agent.get("mappingXml"));
			agentInfo.put("regDt", Calendar.getInstance().getTime());
					
			createAdapter(agentInfo);
			
			if (status.equals(AdapterStatusType.PAUSE.value())) {
				pause(port);
			} else if (status.equals(AdapterStatusType.STOP.value())) {
				stop(port);
			} 
		} catch (Exception e) {
    		throw new VaasAdapterException(e.getMessage());
		}
	}
	//end of updateAdapter()
	
	/**
	 * <pre>
	 * Update a agent information
	 * </pre>
	 * @param agentInfo UDP Adapter Info(equipment id, mapping xml, etc)
	 * @throws VaasAdapterException
	 */
	public void updateAdapter(Map<String, Object> agentInfo) throws VaasAdapterException {
		Integer oldPort = (Integer) agentInfo.get("oldPort");
		Integer port = (Integer) agentInfo.get("port");
		
		/*
		 *  port duplication check
		 */
		if (!oldPort.equals(port) && agentMap.get(port) != null) {
			throw new VaasAdapterException("Port " + port + " already in use.");
		}
		
		String status = (String) agentInfo.get("status");
		
		stop(oldPort);
		createAdapter(agentInfo);
		
		if (status.equals(AdapterStatusType.PAUSE.value())) {
			pause(port);
		} else if (status.equals(AdapterStatusType.STOP.value())) {
			stop(port);
		} 
	}
	//end of updateAdapter()
	
	/**
	 * <pre>
	 * Pause a AdapterThread
	 * </pre>
	 * @param port
	 * @return whether the event processing successful 
	 * @throws VaasAdapterException
	 */
	public boolean pause(int port) throws VaasAdapterException {
		boolean result = sendEvent(port, AdapterStatusType.PAUSE);
    	LOGGER.debug("Send PAUSE Event to AdapterThread that listening on UDP Port [{}] and result is [{}].", port, result);

    	if (result) {
        	updateStatus(port, AdapterStatusType.PAUSE);
    	}
    	
        return result;
	}
	//end of pause()
	
	/**
	 * <pre>
	 * Resume a AdapterThread
	 * </pre>
	 * @param port
	 * @return whether the event processing successful 
	 * @throws VaasAdapterException
	 */
	public boolean resume(int port) throws VaasAdapterException {
		boolean result = sendEvent(port, AdapterStatusType.START);
    	LOGGER.debug("Send START Event to AdapterThread that listening on UDP Port [{}] and result is [{}].", port, result);

    	if (result) {
        	updateStatus(port, AdapterStatusType.START);
    	}
    	
        return result;
	}
	//end of resume()
	
	/**
	 * <pre>
	 * Terminate a AdapterThread
	 * </pre>
	 * @param port
	 * @return whether the event processing successful 
	 * @throws VaasAdapterException
	 */
	public synchronized boolean stop(int port) throws VaasAdapterException {
		boolean result = sendEvent(port, AdapterStatusType.STOP);
    	LOGGER.debug("Send STOP Event to AdapterThread that listening on UDP Port [{}] and result is [{}].", port, result);
    	
    	if (result) {
        	agentMap.remove(port);
        	updateStatus(port, AdapterStatusType.STOP);
    	}
    	
        return result;
	}
	//end of stop()
	
	/**
	 * <pre>
	 * Send Adapter Status change event to AdapterThread using Event Bus
	 * </pre>
	 * @param port UDP Listening Port
	 * @param status Adapter Status
	 * @return
	 * @throws VaasAdapterException
	 */
	private boolean sendEvent(int port, AdapterStatusType status) throws VaasAdapterException {
		if (agentMap.get(port) == null) {
			throw new VaasAdapterException("Port " + port + " is not listening state.");
		}
		
        AdapterEventMessage message = new AdapterEventMessage(port, status);
        eventBus.publish(message);
        
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// ignore
		}
        
        return message.isHandled();
	}
	//end of sendEvent()
	
	/**
	 * <pre>
	 * update AdapterThread's status to metadata
	 * </pre>
	 * @param port UDP Listening Port
	 * @param status Adapter Status
	 */
	private void updateStatus(int port, AdapterStatusType status) {
		MetadataQueryDomain query = new MetadataQueryDomain();
    	query.addQuery(new KeyValueDomain("value.port", port));
    	query.addQuery(new KeyValueDomain("value.agentType", "adapter"));
    	
    	KeyValueDomain update = new KeyValueDomain("$set", new KeyValueDomain("value.status", status.toString()));
    	
		metadataService.update(AGENT_LIST_METADATA_KEY, query, update);
	}
	//end of updateStatus()
}
//end of AdapterManager.java