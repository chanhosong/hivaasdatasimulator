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
 * sehwan			2015. 6. 18.				First Draft.
 */

package com.hhi.vaas.platform.datasimulator.actions;
  
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;

import com.hhi.vaas.platform.datasimulator.IActionCallback;
import com.hhi.vaas.platform.datasimulator.IDefineID;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.udp.SendUdpMsg;
import com.hhi.vaas.platform.datasimulator.ui.DlgSetting;
import com.hhi.vaas.platform.datasimulator.zeromq.SendZeroMqMsg;

public class ActionsExecution {
	private FileReading fileReading;
	private SendUdpMsg sendingUDP;
	private SendZeroMqMsg sendingZMQ;

	private String strViewTitle;
	private String strDeviceName;

	private String strDataTypde;
	private String ipAddress;
	private int portNumber;
	private int typeofProtocol;
	private NetworkInterface nic;
	
	private LoggerSingletone logger;
	
	public ActionsExecution() {
		logger = LoggerSingletone.getInstance();
	}
	
	public void startProcess(File loadedFile, IActionCallback actionCallback, boolean checkTimeStamp) throws FileNotFoundException, InterruptedException {		
		
		fileReading = new FileReading(loadedFile, actionCallback, ipAddress, portNumber, typeofProtocol, nic, checkTimeStamp);
	}
	
	public void manualSignal() {
		if(IDefineID.ZEROMQ_PUSH == typeofProtocol) {
			sendingZMQ = new SendZeroMqMsg(ipAddress, portNumber, typeofProtocol);
		} else {
			try {
				sendingUDP = new SendUdpMsg(ipAddress, portNumber, typeofProtocol, nic);
			} catch (UnknownHostException e) {
				logger.setLogger(logger.getLogger().getLogger(ActionsExecution.class.getName()));
				logger.getLogger().error(String.format("Error code : 0x%04x Unknown host. ", IDefineID.ERROR_NETWORK_UNKNOWN_HOST));
				logger.getLogger().error(e);
			} catch (SocketException e) {
				logger.setLogger(logger.getLogger().getLogger(ActionsExecution.class.getName()));
				logger.getLogger().error(String.format("Error code : 0x%04x Fail creating socket. ", IDefineID.ERROR_NETWOKk_FAIL_CREATING_UDP_SOCKET));
				logger.getLogger().error(e);
			} catch (IOException e) {
				logger.setLogger(logger.getLogger().getLogger(ActionsExecution.class.getName()));
				logger.getLogger().error(String.format("Error code : 0x%04x Property IO exception. ", IDefineID.ERROR_IO_EXCEPTION));
				logger.getLogger().error(e);
			}
		}
	}
	
	public Boolean setting(IWorkbenchWindow window, String viewName, String device, String dataType,
			String ipAdress, int portNumber, int typeofProtocol, NetworkInterface nic) {
		DlgSetting setting = new DlgSetting(window.getShell(), viewName, device, dataType, ipAdress, portNumber, typeofProtocol, nic);

		if (setting.open() == Window.OK) {
			strViewTitle = setting.getDeviceName() + " : " + setting.getDeviceOptionName() + " - " + setting.getIPAdress() + ":" + setting.getPortNumbr() + " "
					+ setting.getProtocol();
			this.ipAddress = setting.getIPAdress();
			this.portNumber = setting.getPortNumbr();
			this.typeofProtocol = setting.getTypeofProtocol();
			this.nic = setting.getNic();
			this.strDeviceName = setting.getDeviceName();
			this.strDataTypde = setting.getDeviceOptionName();			
		} else {
			return false;
		}
		return true;
	}
	
	public void deleteSendMsg() {
		sendingUDP = null;
		sendingZMQ = null;
	}
	
	public void closingSocket() {
		if (!fileReading.getUdpSending().getSocket().isClosed()) {
			fileReading.getUdpSending().getSocket().close();
		}
	}

	public FileReading getFileReading() {
		return fileReading;
	}
	
	public SendUdpMsg getUdpSending() {
		return sendingUDP;
	}
	
	public SendZeroMqMsg getZMQSending() {
		return sendingZMQ;
	}

	public void setIpAdress(String ipAdress) {
		this.ipAddress = ipAdress;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public void setTypeofProtocol(int typeofProtocol) {
		this.typeofProtocol = typeofProtocol;
	}

	public void setNic(NetworkInterface nic) {
		this.nic = nic;
	}

	public int getTypeofProtocol() {
		return typeofProtocol;
	}

	public String getStrViewTitle() {
		return strViewTitle;
	}

	public String getStrIpAdress() {
		return ipAddress;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public NetworkInterface getNic() {
		return nic;
	}
	
	public String getStrDeviceName() {
		return strDeviceName;
	}

	public String getStrDataTypde() {
		return strDataTypde;
	}
}
