
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
 * sehwan			2015. 7. 29.				First Draft.
 */
package com.hhi.vaas.platform.datasimulator;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.hhi.vaas.platform.datasimulator.actions.FileReading;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.udp.NicBinding;

public class ConsoleApp {
	private File file;
	private String ipAddress;
	private String bindIpAddress;
	private int portNbr;
	private int protocol;
	private int period;
	private int repeat;
	private LoggerSingletone LOGGER;
	
	public ConsoleApp() {
		LOGGER = LoggerSingletone.getInstance();
		LOGGER.setLogger(LOGGER.getLogger().getLogger(ConsoleApp.class.getName()));
		LOGGER.getLogger().info("Starting ConsoleAPP");
		
		this.file = null;
		this.ipAddress = "";
		this.portNbr = 0;
		this.protocol = 0;
		this.period = 0;
		this.repeat = 0;
	}
	
	public void Usage() {
		System.err.println("Usage: Console.exe [Protocol] [Period] [Repeat] [File] [IP Address:Port] [NIC IP Address]");
		System.err.println("Example: Console.exe u 100 r dump.txt 127.0.0.1:9999");
		System.err.println("Protocol:\n\tu\tUDP Unicast\n\tm\tUDP Multicast\n\tb\tUDP Broadcat\n\tz\tZeroMQ");
		System.err.println("Repeat:\n\tr\tTrun on repeat\n\tn\tTurn off repeat");
		System.exit(1);
	}
	
	public void setFile(String filePath) {
		if(filePath != null && !"".equals(filePath)) {
			this.file = new File(filePath);
			
			if(!this.file.exists()) {
				LOGGER.setLogger(LOGGER.getLogger().getLogger(ConsoleApp.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x File not found.", IDefineID.ERROR_FILE_NOT_FOUND));
				//System.err.println("File is not exists.");
				System.exit(1);
			}		
		} else {
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ConsoleApp.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Wrong file path.", IDefineID.ERROR_WRONG_FILE_PATH));
			//System.err.println("Wrong file path.");
			System.exit(1);
		}
	}
	
	public void setIpAddressAndPortNbr(String ipAddress) {
		StringTokenizer token = null;
		String strTmp = "";
		boolean isTrue = false;
		
		token = new StringTokenizer(ipAddress, ":");
		
		strTmp = new String(token.nextToken());
		isTrue = checkIpAddress(strTmp);		
		if(isTrue) {
			this.ipAddress = strTmp;
		}
		else {
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ConsoleApp.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Wrong IP Address.", IDefineID.ERROR_WRONG_IP_ADDRESS_REGEX));
			//System.err.println("Wrong IP Address.");
			this.Usage();
		}
		
		strTmp = new String(token.nextToken());
		isTrue = checkPortNbr(strTmp);
		if(isTrue) {
			this.portNbr = Integer.valueOf(strTmp);
		} else {
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ConsoleApp.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Wrong port number. 0~65535.", IDefineID.ERROR_WRONG_PORT_NUMBER_REGEX));
			//System.err.println("Wrong port number. 0~65535");
			this.Usage();
		}
	}
	
	public void setProtocol(String protocol) {
		if(protocol.equals("U")) {
			this.protocol = IDefineID.UDP_UNICAST;
		} else if(protocol.equals("M")) {
			this.protocol = IDefineID.UDP_MULTICAST;
		} else if(protocol.equals("B")) {
			this.protocol = IDefineID.UDP_BROADCAST;
		} else if(protocol.equals("Z")) {
			this.protocol = IDefineID.ZEROMQ_PUSH;
		} else {
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ConsoleApp.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Wrong protocol option.", IDefineID.ERROR_WRONG_PROTOCOL_REGEX));
			//System.err.println("Wrong protocol option.");
			this.Usage();
		}
	}
	
	public void setPeriod(String period) {
		boolean isTrue = false;
		
		isTrue = checkPeriod(period);
		if(isTrue) {
			this.period = Integer.valueOf(period);
		} else {
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ConsoleApp.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Wrong period. Period > 0.", IDefineID.ERROR_WRONG_PEROID_REGEX));
			//System.err.println("Wrong period. Period > 0");
			this.Usage();
		}
	}
	
	public void setRepeat(String repeat) {
		if(repeat.equals("R")) {
			this.repeat = IDefineID.FUNCTION_REPEAT;
		} else if(repeat.equals("N")) {
			this.repeat = IDefineID.FUNCTION_NO_REPEAT;
		} else {
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ConsoleApp.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Wrong repeat option. ", IDefineID.ERROR_WRONG_REPEAT_REGEX));
			//System.err.println("Wrong repeat option.");
			this.Usage();
		}
	}
	
	public boolean checkIpAddress(String ipAddress) {
		Pattern pattern;
		String regex = "((([0-9])|([1-9]\\d{1})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}(([0-9])|([1-9]\\d{1})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))";
		boolean isTrue = false;
		
		pattern = Pattern.compile(regex);
		
		isTrue = pattern.matcher(ipAddress).matches();
		
		return isTrue;
	}
	
	public boolean checkPortNbr(String portNbr) {
		Pattern pattern;
		String regex = "^(6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|[1-9][0-9]{1,3}|[0-9])$";
		boolean isTrue = false;
		
		pattern = Pattern.compile(regex);
		
		isTrue = pattern.matcher(portNbr).matches();
		
		return isTrue;
	}
	
	public boolean checkPeriod(String period) {
		Pattern pattern;
		String regex = "^[1-9][0-9]*$";
		boolean isTrue = false;
		
		pattern = Pattern.compile(regex);
		
		isTrue = pattern.matcher(period).matches();
		
		return isTrue;
	}
	
	public NetworkInterface findNetworkInterface() {
		NetworkInterface nic = null;

		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface nicTmp = en.nextElement();
				
				for (Enumeration<InetAddress> enumIpAddress = nicTmp.getInetAddresses(); enumIpAddress.hasMoreElements();) {
					String ipAddress = enumIpAddress.nextElement().toString();
					
					if (ipAddress.startsWith("/" + bindIpAddress)) {
						nic = nicTmp;
					}
				}
			}
		} catch (SocketException e) {
		}

		return nic;
	}
	
	public static void main(String[] args) {
		String filePath = "", ipAddress = "", bindIpAddress = "", protocol = "", period = "", repeat = "";		
		ConsoleApp console = new ConsoleApp();
		FileReading fileReading = null;
		NicBinding nicListBinding = new NicBinding();
		NetworkInterface nic;
		
		if(args.length < 6) {
			console.LOGGER.setLogger(console.LOGGER.getLogger().getLogger(ConsoleApp.class.getName()));
			console.LOGGER.getLogger().error(String.format("Error code : 0x%04x Wrong usage.", IDefineID.ERROR_WRONG_USAGE));
			//System.err.println("Error!! Need more arguments");
			console.Usage();
		} else {
			protocol = new String(args[0]);
			period = new String(args[1]);
			repeat = new String(args[2]);
			filePath = new String(args[3]);
			ipAddress = new String(args[4]);
			bindIpAddress = new String(args[5]);			
		}
		
		console.setProtocol(protocol.toUpperCase());
		console.setPeriod(period);
		console.setRepeat(repeat.toUpperCase());
		console.setFile(filePath);
		console.setIpAddressAndPortNbr(ipAddress);
		
		if(console.checkIpAddress(bindIpAddress)) {
			console.bindIpAddress = bindIpAddress;
		}
		
		nic = console.findNetworkInterface();
		
		fileReading = new FileReading(console.file, console.ipAddress, console.portNbr, console.protocol, console.bindIpAddress, console.period, console.repeat);
		fileReading.start();
	}
}
//end of Console.java