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

import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasExchangeMessageClient;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMQMessage;
import com.hhi.vaas.platform.middleware.common.rabbitmq.VaasMessageClient;
import com.hhi.vaas.platform.middleware.metadata.MetadataException;
import com.hhi.vaas.platform.middleware.metadata.MetadataService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import com.hhi.vaas.platform.vdm.parser.DataConverterFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

/**
 * <pre>
 * UDP Listener class to running individual threads
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class AdapterThread extends Thread {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdapterThread.class);
	
	public static final int DEFAULT_PORT = 5001;
	public static final int ALL_ADAPTER_THREAD = -1;
	private static final int MSG_SIZE = 1024 * 32;
	
	private static final String CRLF = "\r\n";

	private String agentId;
	private AdapterProcsssType processType;
	private String interfaceIp;
	private String multicastIp;
	private int port;
	private String mappingXml;
	private MetadataService metadataService;
	
	private AdapterStatusType status;

	private boolean isMulticast;
	private InetAddress group;
	private DatagramSocket socket;
	
	private DataConverter jsonConverter;
	private VaasMessageClient mqClient;
	
	private DB db;
	private DBCollection collection;
	
	private String exchangeName;
	private String routingKeyName;
	private String errRoutingKeyName;
	
	/**
	 * <pre>
	 * Constructor of AdapterThread for UNICAST
	 * </pre>
	 * @param agentId agentId
	 * @param port port
	 * @param mappingXml mappingXml
	 * @param metadataService metadataService
	 * @throws VaasAdapterException 
	 */
	public AdapterThread(String agentId, String processType, int port, String mappingXml, MetadataService metadataService) throws VaasAdapterException {
		this(agentId, processType, null, null, port, mappingXml, metadataService);
	}
	//end of constructor
	
	/**
	 * <pre>
	 * Constructor of AdapterThread for MULTICAST
	 * </pre>
	 * @param agentId agentId
	 * @param interfaceIp
	 * @param multicastIp
	 * @param port port
	 * @param mappingXml mappingXml
	 * @param metadataService metadataService
	 * @throws VaasAdapterException 
	 */
	public AdapterThread(String agentId, String processType, String interfaceIp, String multicastIp, int port, String mappingXml, MetadataService metadataService) throws VaasAdapterException {
		this.agentId = agentId;
		
		if (processType == null) {
			processType = AdapterProcsssType.NMEA.toString();
		}
		
		this.processType = AdapterProcsssType.fromValue(processType);
		this.interfaceIp = interfaceIp;
		this.multicastIp = multicastIp;
		this.port = port;
		this.mappingXml = mappingXml;
		this.metadataService = metadataService;
		
		if (interfaceIp != null && multicastIp != null) {
			isMulticast = true;
		}
		
		initialize();
	}
	//end of constructor
	
	/**
	 * <pre>
	 * AdapterEventMessage handler
	 * </pre>
	 * @param message
	 */
	@Handler
    public void handleEvent(AdapterEventMessage message) {
		if (port == message.getPort() || ALL_ADAPTER_THREAD == message.getPort()) {
			LOGGER.debug("AdapterEventMessage Received : [{}]", message.getStatus().toString());
			
			status = message.getStatus();
			
    		if (AdapterStatusType.STOP.equals(message.getStatus())) {
    			/**
    			 *  to get out of blocking status
    			 */
    			if (socket != null) {
    				socket.close();
    			}
    			
    	        this.interrupt();
    		}
    		
    		message.setHandled(true);
		}
	}
	//end of handleEvent()
	
	/**
	 * <pre>
	 * Initialize Adapter Thread
	 * </pre>
	 * @throws VaasAdapterException 
	 */
	public void initialize() throws VaasAdapterException {
		try {
			if (processType.equals(AdapterProcsssType.NMEA)) {
				String vcdXml = (String) metadataService.selectOne("vaas_dataservice_vcd");
				
				/**
				 *  json converter to convert NMEA data to json formatted string
				 */
				jsonConverter = DataConverterFactory.create(new ByteArrayInputStream(mappingXml.getBytes()), new ByteArrayInputStream(vcdXml.getBytes()));
	
				/**
				 *  exchange name
				 */
				exchangeName = (String) metadataService.selectOne("vaas_mq_exchangeName");
				
				LOGGER.info("exchangeName : [{}]", exchangeName);
				
				/**
				 *  normal message Queue
				 *  Use agent(adapter) id as a routing key
				 */
				routingKeyName = agentId;
				
				/**
				 *  error message Queue
				 */
				errRoutingKeyName = (String) metadataService.selectOne("vaas_mq_topic_name_exception");
				
				LOGGER.info("routingKeyName : [{}], errRoutingKeyName: [{}]", routingKeyName, errRoutingKeyName);
	
				DBObject obj = (DBObject) metadataService.selectOne("vaas_mq_connection_info");
				mqClient = new VaasExchangeMessageClient(obj);
				mqClient.declare(exchangeName);
			} else {
				/**
				 * Initialize mongoDB connection to save binary(radar image, voice record)
				 */
				db = metadataService.getDb();

				String collectionName = (String) metadataService.selectOne("vaas_mongo_collection_binary_data");
				if (collectionName == null || collectionName.equals("")) {
					collectionName = "BINARY_DATA";
				}
				
				collection = db.getCollection(collectionName);
			}
			
			if (isMulticast) {
				group = InetAddress.getByName(multicastIp);

				// Start Multicast UDP Port Listening
				socket = new MulticastSocket(port);
				((MulticastSocket) socket).setInterface(InetAddress.getByName(interfaceIp));
				((MulticastSocket) socket).joinGroup(group);
			} else {
				// Start UDP Port Listening
				socket = new DatagramSocket(port);
			}
		} catch (SocketException e) {
			LOGGER.error("SocketException has occurred : ", e);
			throw new VaasAdapterException(e);
		} catch (SAXException e) {
			LOGGER.error("SAXException has occurred : ", e);
			throw new VaasAdapterException(e);
		} catch (ParserConfigurationException e) {
			LOGGER.error("ParserConfigurationException has occurred : ", e);
			throw new VaasAdapterException(e);
		} catch (IOException e) {
			LOGGER.error("IOException has occurred : ", e);
			throw new VaasAdapterException(e);
		} catch (MetadataException e) {
			LOGGER.error("MetadataException has occurred : ", e);
			throw new VaasAdapterException(e);
		}
	}
	//end of initialize()

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	//end of getPort()

	/**
	 * @return the status
	 */
	public AdapterStatusType getStatus() {
		return status;
	}
	//end of getStatus()
	
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		DatagramPacket inPacket = null;
        byte[] inMsg = null;
        String sendRoutingName = null;
        String recvStr = null;
        String[] recvMsgs = null;
        VaasMQMessage message = null;
        
        status = AdapterStatusType.START;
        
		while (true) {
			try {				
				inMsg = new byte[MSG_SIZE];
				sendRoutingName = routingKeyName;
				
				// Create new datagram packet to receive a NMEA data
		        inPacket = new DatagramPacket(inMsg, inMsg.length);
		        
		        // reveice data from packet
		        socket.receive(inPacket);
		        
		        if (AdapterStatusType.PAUSE.equals(status)) {
			        LOGGER.debug("Current status is [PAUSE]. Skip the received message");
		        	continue;
		        }
		        
		        if (AdapterStatusType.STOP.equals(status)) {
			        LOGGER.debug("Current status is [STOP]. Stopping the agent thread.");
		        	break;
		        }

		        if (processType.equals(AdapterProcsssType.NMEA)) {
		        	/**
		        	 * Processing NMEA data
		        	 */
			        recvStr = new String(inPacket.getData()).trim();
			        
			        LOGGER.debug("[{}] Received Message : [{}]", port, recvStr);
			        
			        recvMsgs = recvStr.split(CRLF);
			        
			        for (String recvMsg : recvMsgs) {
				        String converted = null;
				        try {
				        	/**
				        	 *  convert to json
				        	 */
				        	converted = jsonConverter.convert(recvMsg);
				            LOGGER.debug("[{}] JSON Converted Message : [{}]", port, converted);
				        } catch (Exception e) {
				        	if(LOGGER.isDebugEnabled()){
				        		LOGGER.error(e.toString(), e);
				        	} else {
				        		LOGGER.info(e.toString());
				        	}
				        	
				        	converted = recvMsg;
				        	sendRoutingName = errRoutingKeyName;
				        }
				        
				        /**
				         * create queue message instance
				         */
				        message = new VaasMQMessage(exchangeName, sendRoutingName, converted);
				        
						/**
						 * send message to queue
						 */
						mqClient.basicPublish(message);
			        }
		        } else {
		        	/**
		        	 * Processing RADAR_IMAGE and VOICE_RECORD data
		        	 */
		        	try {
						Long timestamp = System.currentTimeMillis();
						
			        	DBObject doc = new BasicDBObject();
						doc.put("type", processType.toString());
						doc.put("length", inPacket.getData().length);
						doc.put("data", inPacket.getData());
						doc.put("timestamp", timestamp);
						doc.put("regDt", new Date(timestamp));
						
						LOGGER.debug("BIN_DATA will be inserted. [{}]", doc);
	
						collection.insert(doc);
		        	} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
		        	}
		        }
			} catch (Exception e) {
				if (e.getMessage().startsWith("Socket closed")) {
					break;
				}
				
				LOGGER.error(e.toString(), e);
			} 
		}
		
		if (mqClient != null) {
			mqClient.close();
		}

		if (socket != null) {
			try {
				if (isMulticast) {
					((MulticastSocket) socket).leaveGroup(group);
				}
				socket.close();
			} catch (Exception e) {
				// nothing to do
			}
		}
	}
	//end of run()
}
//end of AdapterThread.java