
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
 * sehwan			2015. 9. 1.				First Draft.
 */
package com.hhi.vaas.platform.datasimulator.udp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.ui.GeneratorView;

public class NicBinding {	
	private LoggerSingletone LOGGER;	
	
	public NicBinding() {
		LOGGER = LoggerSingletone.getInstance();
	}

	public List<NetworkInterface> findNetworkInterface() {
		List<NetworkInterface> nicList = new ArrayList<NetworkInterface>();

		LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
		LOGGER.getLogger().info("Find Netowork Interface");

		try {			
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface nic = en.nextElement();
				Enumeration<InetAddress> nicAddresses = nic.getInetAddresses();
				
				if(!nicAddresses.hasMoreElements()) {
					continue;
				} else {
					nicList.add(nic);
					String ipAddr = nicAddresses.nextElement().toString();
					LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
					LOGGER.getLogger().info("Full list of Network Interfaces : " + nic.getName() + " " + nic.getDisplayName() + " : " + ipAddr);
				}
			}
		} catch (SocketException e) {
			LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
			LOGGER.getLogger().info("error retrieving network interface list");
		}
		
		return nicList;
	}
}
// end of NicBinding.java