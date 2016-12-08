package com.hhi.vaas.platform.datasimulator.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

import com.hhi.vaas.platform.datasimulator.IDefineID;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;

public class SendUdpMsg {

	private DatagramSocket socket;
	private MulticastSocket multicastSocket;
	
	private InetAddress ipAddress;
	private int portNumber;

	private LoggerSingletone LOGGER = LoggerSingletone.getInstance();
	
	public SendUdpMsg(String ipAddress, int portNumber, int typeOfProtocol, String bindIpAddress) throws IOException {
		if (typeOfProtocol == IDefineID.UDP_UNICAST) {
			this.ipAddress = InetAddress.getByName(ipAddress);		
			this.portNumber = portNumber;
			socket = new DatagramSocket();
		} else if (typeOfProtocol == IDefineID.UDP_MULTICAST) {
			this.ipAddress = InetAddress.getByName(ipAddress);
			this.portNumber = portNumber;
				multicastSocket = new MulticastSocket(portNumber);
			multicastSocket.setInterface(InetAddress.getByName(bindIpAddress));
			multicastSocket.joinGroup(this.ipAddress);
		} else if (typeOfProtocol == IDefineID.UDP_BROADCAST) {
			this.ipAddress = InetAddress.getByName(ipAddress);
			this.portNumber = portNumber;
			socket = new DatagramSocket();
		}
	}
	
	public SendUdpMsg(String ipAddress, int portNumber, int typeOfProtocol, NetworkInterface nic) throws IOException {
		if (typeOfProtocol == IDefineID.UDP_UNICAST) {
			this.ipAddress = InetAddress.getByName(ipAddress);		
			this.portNumber = portNumber;
			socket = new DatagramSocket();
		} else if (typeOfProtocol == IDefineID.UDP_MULTICAST) {
			this.ipAddress = InetAddress.getByName(ipAddress);
			this.portNumber = portNumber;
			multicastSocket = new MulticastSocket(portNumber);
			multicastSocket.setNetworkInterface(nic);
			multicastSocket.joinGroup(this.ipAddress);
		} else if (typeOfProtocol == IDefineID.UDP_BROADCAST) {
			this.ipAddress = InetAddress.getByName(ipAddress);
			this.portNumber = portNumber;
			socket = new DatagramSocket();
		}
	}
	
	public void sendMsgUnicast(String message) throws IOException {		
		byte[] sendData = message.getBytes();
				
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portNumber);		
		socket.send(sendPacket);
	}

	public void sendMsgMulticast(String message) {
		try {
		byte[] sendData = message.getBytes();
			DatagramPacket packet = new DatagramPacket(sendData, sendData.length, ipAddress, portNumber);

			multicastSocket.send(packet);
		} catch (IOException e) {
			//e.printStackTrace();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(SendUdpMsg.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Property IO exception. ", IDefineID.ERROR_IO_EXCEPTION));
		}
	}
	
	public void sendMsgBroadcast(String message) throws IOException {		
		socket.setBroadcast(true);		
		byte[] sendData = message.getBytes();
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portNumber);
		socket.send(sendPacket);
	}
	
	public DatagramSocket getSocket() {
		return socket;
	}
	
	public MulticastSocket getMulticastSocket() {
		return multicastSocket;
	}
}
