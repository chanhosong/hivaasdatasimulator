/* Copyright (c) 2015 OpenSourceConsulting, Inc.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of OpenSourceConsulting
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with OpenSourceConsulting.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * Sang-cheon Park	2015. 2. 10.		First Draft.
 */
package com.hhi.vaas.platform.agent.receiver;

import com.hhi.vaas.platform.agent.model.AgentStatus;
import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;



/**
 * <pre>
 * UDP Server Class
 * </pre>
 * @author BongJin Kwon
 */
public class UDPReceiver extends AbstractDataReceiver{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UDPReceiver.class);
	
	private static final int MSG_SIZE = 1024 * 32;
	
	private static final String CRLF = "\r\n";
	
	private String interfaceIp;
	private String multicastIp;
	private int port;
	private DatagramSocket socket;
	
	private boolean isMulticast;
	private InetAddress group;
	
	private String[] recvMsgs;
	private int dataIndex;
	
	
	public UDPReceiver(DataConverter dataConverter, RabbitMQSender sender, PropertyService props, AgentStatus status) {
		super(dataConverter, sender, props, status);
		this.port = props.getProperty("vaas.agent.listen.port", Integer.class, new Integer(5001));
		this.interfaceIp = props.getProperty("vaas.udp.if.ip");
		this.multicastIp = props.getProperty("vaas.udp.mc.ip");
		
		if (interfaceIp != null && multicastIp != null) {
			isMulticast = true;
		}
	}
	
	
	@Override
	protected void preStart() {
		
		try {
			if (isMulticast) {
				group = InetAddress.getByName(multicastIp);

				// Start Multicast UDP Port Listening
				socket = new MulticastSocket(port);
				((MulticastSocket) socket).setInterface(InetAddress.getByName(interfaceIp));
				((MulticastSocket) socket).joinGroup(group);
				
				LOGGER.info("listening multicast, port : {}", port);
			} else {
				// Start UDP Port Listening
				socket = new DatagramSocket(port);
				
				LOGGER.info("listening unicast, port : {}", port);
			}
		} catch(SocketException e) {
			// Will be handled by the parent class.
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}



	@Override
	protected String receiveData() {
		
		if(recvMsgs == null || recvMsgs.length == dataIndex){
			
			dataIndex = 0;
			byte[] inMsg = new byte[MSG_SIZE];
			
			// 데이터를 수신하기 위한 패킷을 생성
			DatagramPacket inPacket = new DatagramPacket(inMsg, inMsg.length);
			
			try {
				// 패킷을 통해 데이터를 수신한다.
		        socket.receive(inPacket);
			} catch(IOException e) {
				dataIndex = 0;
				recvMsgs = null;
				throw new RuntimeException(e);
			}

	        String recvStr = new String(inPacket.getData()).trim();
	        
	        recvMsgs = recvStr.split(CRLF);
	        LOGGER.debug("recvMsgs.length : {}", recvMsgs.length);
		}

		return recvMsgs[dataIndex++];
	}


	@Override
	public void stop() {
		
		if (socket != null) {
			if (isMulticast) {
				try {
					((MulticastSocket) socket).leaveGroup(group);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socket.close();
			socket = null;
			LOGGER.info("socket closed.");
		} else {
			LOGGER.info("socket is null.");
		}
		
	}//end of stop()

	
    /**
     * <pre>
     * main() method to run UDP Server in standalone mode
     * </pre>
     * @param args
     * @throws Exception
     */
	/*
    public static void main(String[] args) throws Exception {
    	PropertyService propertyService = new PropertyService("");
		
		// create json converter
		DataConverter jsonConverter = DataConverterFactory.create(propertyService.getResourceStreamFromKey("mapping.xml.path"));
		
		
		// normal message Queue
        String queueName = propertyService.getProperty("vaas.mq.topic.name.default");
        
        
        LOGGER.info("Queue is {}", queueName);
        
        VaasQueueClient mqClient = new VaasQueueClient(propertyService, queueName);
		
        UDPServer server = new UDPServer(jsonConverter, mqClient, propertyService);
        server.start();
        
    }//end of main()
*/
    
}
//end of UDPServer.java