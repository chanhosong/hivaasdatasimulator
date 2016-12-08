
/* Copyright (C) eMarine Co. Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of eMarine Co. Ltd.
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with eMarine Co. Ltd.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * ddong			2015. 7. 13.				First Draft.
 */
package com.hhi.vaas.platform.datasimulator.zeromq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

public class SendZeroMqMsg {
	private ZMQ.Context context;
	private ZMQ.Socket requester;
	
	
	public SendZeroMqMsg(String ipAddress, int portNumber, int typeOfProtocol) {
		
		context = ZMQ.context(1);
		//requester = context.socket(ZMQ.PUSH);
		requester = context.socket(ZMQ.PUB);
		
		String connectionInfo = "tcp://" + ipAddress + ":" + String.valueOf(portNumber);
		//requester.bind("tcp://*:9000");
		requester.connect(connectionInfo);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendingMessage(String message) {
		requester.send(message.getBytes(), ZMQ.NOBLOCK);
		//requester.send(message);
		/*byte[] reply = requester.recv(0);
        System.out.println("Received " + new String(reply));*/
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ZMQ.Socket getRequester() {
		return requester;
	}

	public void setRequester(ZMQ.Socket requester) {
		this.requester = requester;
	}
}
//end of SendZeroMqMsg.java