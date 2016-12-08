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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.sender.RabbitMQSender;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;



/**
 * <pre>
 * UDP Server Class
 * </pre>
 * @author BongJin Kwon
 */
public class UDPServer extends AbstractDataReceiver{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UDPServer.class);
	
	private int port;
	private DatagramSocket socket;
	
	
	public UDPServer(DataConverter dataConverter, RabbitMQSender sender, PropertyService props) {
		super(dataConverter, sender, props);
		this.port = props.getProperty("vaas.udp.listen.port", Integer.class, new Integer(5001));;
	}
	
	
	@Override
	protected void preStart() {
		
		try{
			socket = new DatagramSocket(port);
		}catch(SocketException e){
			// Will be handled by the parent class.
			throw new RuntimeException(e);
		}
		
	}



	@Override
	protected String receiveData() {
		
		byte[] inMsg = new byte[1024 * 5];
		
		// 데이터를 수신하기 위한 패킷을 생성
		DatagramPacket inPacket = new DatagramPacket(inMsg, inMsg.length);
		
		try{
			// 패킷을 통해 데이터를 수신한다.
	        socket.receive(inPacket);
		}catch(IOException e){
			throw new RuntimeException(e);
		}

        String recvStr = new String(inPacket.getData()).trim();
        
        if(!recvStr.startsWith("$") && !recvStr.startsWith("!")){
        	//String shipID = recvStr.substring(0, 10);
        	recvStr = recvStr.substring(10);
        }
        
        
		return recvStr;
	}


	@Override
	public void stop() {
		
		if (socket != null) {
			socket.close();
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