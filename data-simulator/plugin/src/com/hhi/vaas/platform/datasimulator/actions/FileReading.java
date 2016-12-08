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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hhi.vaas.platform.datasimulator.IActionCallback;
import com.hhi.vaas.platform.datasimulator.IDefineID;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.udp.SendUdpMsg;
import com.hhi.vaas.platform.datasimulator.zeromq.SendZeroMqMsg;

public class FileReading implements Runnable {
    
	private File file;
	private IActionCallback fileReadcallback;
	
	private String message;
	private SendUdpMsg udpSending;
	private SendZeroMqMsg zeroMqPushSending;
	private FileInputStream stream;
	private BufferedReader fileIn;

	private Thread thisThread;
	private Thread speedThread;

	private double bytesPerSec = 0.0;
	private long linePerSec = 0;
	private long bytesTransferred = 0;
	private long lineCount = 0;
	private int typeOfProtocol;
	private volatile long period = 0;
	private volatile int stateCode = IDefineID.STATE_INIT;
	private volatile int isRepeat = IDefineID.FUNCTION_NO_REPEAT;
	private int stateCodeSpeed = IDefineID.STATE_STARTED;
	private volatile int stateCodeShowMsg = IDefineID.STATE_SHOW_MESSAGE;
	
	private LoggerSingletone LOGGER = LoggerSingletone.getInstance();
	
	private boolean isConsoleApp = false;
	private boolean checkTimeStamp;

	private NetworkInterface nic;
	private String bindIpAddress;

	public FileReading(File filepath, String ipAdress, int portNbrr, int typeOfProtocol, NetworkInterface nic, int period, int repeat) {
		this.file = filepath;
		this.typeOfProtocol = typeOfProtocol;
		this.nic = nic;
		this.isConsoleApp = true;
		this.period = period;
		this.isRepeat = repeat;
	
		setNetwork(ipAdress, portNbrr, typeOfProtocol, nic);
		
		speedThread = new SendingSpeedThread();
		speedThread.start();
	}

	public FileReading(File filepath, String ipAdress, int portNbrr, int typeOfProtocol, String bindIpAddress, int period, int repeat) {
		this.file = filepath;
		this.typeOfProtocol = typeOfProtocol;
		this.bindIpAddress = bindIpAddress;
		this.isConsoleApp = true;
		this.period = period;
		this.isRepeat = repeat;
	
		setNetwork(ipAdress, portNbrr, typeOfProtocol, bindIpAddress);
		
		speedThread = new SendingSpeedThread();
		speedThread.start();
	}

	public FileReading(File filepath, IActionCallback fileReadcallback, String ipAdress, int portNbrr, int typeOfProtocol, NetworkInterface nic, boolean checkTimeStamp) {
		this.file = filepath;
		this.fileReadcallback = fileReadcallback;
		this.typeOfProtocol = typeOfProtocol;
		this.nic = nic;
		this.checkTimeStamp = checkTimeStamp;
		
		setNetwork(ipAdress, portNbrr, typeOfProtocol, nic);
		
		speedThread = new SendingSpeedThread();
		speedThread.start();
	}
	
	private void setNetwork(String ipAdress, int portNumber, int typeOfProtocol, String bindIpAddress) {
		if (IDefineID.ZEROMQ_PUSH == typeOfProtocol) {
			zeroMqPushSending = new SendZeroMqMsg(ipAdress, portNumber, typeOfProtocol);
		} else {
			try {
				udpSending = new SendUdpMsg(ipAdress, portNumber, typeOfProtocol, bindIpAddress);
			} catch (UnknownHostException e) {
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Unknown host. ", IDefineID.ERROR_NETWORK_UNKNOWN_HOST));
			} catch (SocketException e) {
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail creating socket. ", IDefineID.ERROR_NETWOKk_FAIL_CREATING_UDP_SOCKET));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Property IO exception. ", IDefineID.ERROR_IO_EXCEPTION));
			}
		}
	}
	
	private void setNetwork(String ipAdress, int portNumber, int typeOfProtocol, NetworkInterface nic) {
		if (IDefineID.ZEROMQ_PUSH == typeOfProtocol) {
			zeroMqPushSending = new SendZeroMqMsg(ipAdress, portNumber, typeOfProtocol);
		} else {
			try {
				udpSending = new SendUdpMsg(ipAdress, portNumber, typeOfProtocol, nic);
			} catch (UnknownHostException e) {
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Unknown host. ", IDefineID.ERROR_NETWORK_UNKNOWN_HOST));
			} catch (SocketException e) {
				//System.out.println(ipAdress);
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail creating socket. ", IDefineID.ERROR_NETWOKk_FAIL_CREATING_UDP_SOCKET));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Property IO exception. ", IDefineID.ERROR_IO_EXCEPTION));
			}
		}
	}
	
	private void closingNetwork() {
		if (IDefineID.ZEROMQ_PUSH == typeOfProtocol) {
			zeroMqPushSending.getRequester().close();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
			LOGGER.getLogger().info("Closing ZeroMQ socket.");
		} else if (IDefineID.UDP_UNICAST == typeOfProtocol || IDefineID.UDP_BROADCAST == typeOfProtocol) {
			udpSending.getSocket().disconnect();
			udpSending.getSocket().close();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
			LOGGER.getLogger().info("Closing UDP socket.");
		} else {
			udpSending.getMulticastSocket().disconnect();
			udpSending.getMulticastSocket().close();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
			LOGGER.getLogger().info("Closing UDP socket.");
		}
	}
	
	private void suspendingProcess() {
		while (IDefineID.STATE_SUSPENDED == stateCode) {
			fileReadcallback.displayUpdate("", 0.0, 0.0, 0.0);
			try {
				Thread.sleep(24 * 60 * 60 * 1000);
			} catch (InterruptedException e) {
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail file thread sleep. %d", IDefineID.ERROR_THREAD_SLEEP, stateCode));
				if (IDefineID.STATE_SUSPENDED != stateCode) {
					LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
					LOGGER.getLogger().info("Wake up and stating send message.");
					break;
				}
			}
		}
	}

	private void fileHandler() {
		try {
			stream = new FileInputStream(file.getPath());
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x File not found.", IDefineID.ERROR_FILE_NOT_FOUND));
		}
		fileIn = new BufferedReader(new InputStreamReader(stream));
	}
	
	private void sendingMessage(String message) throws IOException {
		if (IDefineID.UDP_BROADCAST == typeOfProtocol || IDefineID.UDP_UNICAST == typeOfProtocol) {
			//udpSending.sendMsgBroadcast(message);
			udpSending.sendMsgUnicast(message);
		} else if (IDefineID.UDP_MULTICAST == typeOfProtocol) {
			udpSending.sendMsgMulticast(message);
		} else if (IDefineID.ZEROMQ_PUSH == typeOfProtocol) {
			zeroMqPushSending.sendingMessage(message);
		}
	}
	
	private void sendingMessageWithTimestamp(long timeStamp, String message) throws IOException {
		if (IDefineID.UDP_BROADCAST == typeOfProtocol || IDefineID.UDP_UNICAST == typeOfProtocol) {
			//udpSending.sendMsgBroadcast(message);
			udpSending.sendMsgUnicast(message);
		} else if (IDefineID.UDP_MULTICAST == typeOfProtocol) {
			udpSending.sendMsgMulticast(message);
		} else if (IDefineID.ZEROMQ_PUSH == typeOfProtocol) {
			zeroMqPushSending.sendingMessage(message);
		}
	}
	
	private void jobProcess() throws IOException, InterruptedException {
		while ((message = fileIn.readLine()) != null) {
			suspendingProcess();
	
			if (stateCode == IDefineID.STATE_STOPPED) {
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().info("Stopping send message.");
				break;
			}
			
			// timestamp trim 
			if(message.startsWith("TIM:")) {
				message = message.substring(17);
			}
			
			sendingMessage(message);
			
			bytesTransferred += message.length();
			lineCount++;
			
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail file thread sleep. ", IDefineID.ERROR_THREAD_SLEEP));
			}
		}
	}

	private void jobProcessWithTimeStamp() throws IOException, InterruptedException {
		
		long prevTime = 0;
		long curTime = 0;
		
		long delay = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String messageTime;
		Date messageDate;
		
		while ((message = fileIn.readLine()) != null) {
			suspendingProcess();
	
			if (stateCode == IDefineID.STATE_STOPPED) {
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().info("Stopping send message.");
				break;
			}
			
			// timestamp trim 
			if(message.startsWith("TIM:")) {
				messageTime = message.substring(4, 27);
				message = message.substring(28);
				
						
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().debug("Time : " + messageTime);
				
				if(checkTimeStamp == true) {					
					try {
						messageDate = sdf.parse(messageTime);
						curTime = messageDate.getTime();
						
						if(prevTime == 0) {
							String nextMessage, nextMessageTime;
							Date nextMessageDate;
							long nextTime;
							
							fileIn.mark(1024);
							
							nextMessage = fileIn.readLine();
							nextMessageTime = nextMessage.substring(4, 27);
							
							nextMessageDate = sdf.parse(nextMessageTime);
							nextTime = nextMessageDate.getTime();
							
							prevTime = curTime;
							curTime = nextTime;
							
							fileIn.reset();
						}
						
						if(/*prevTime == 0 || */curTime < prevTime) {
							 prevTime = curTime; 
						}
							 
							 delay = curTime - prevTime;
							 prevTime = curTime;
						
						LOGGER.getLogger().debug("Delay : " + delay);
						
					} catch (Exception e) {
						LOGGER.getLogger().debug("timestamp error, Delay : " + delay);
						
						delay = period;
					}
				} else {
					delay = period;
				}
			} else {
				delay = period;
			}
			
			sendingMessage(message);
			
			if(IDefineID.STATE_STARTED == stateCode) {
				if(isConsoleApp) {
					System.out.format("%s\t\t%.2f line/sec, %.2f kb/sec, %.2f kb transferred\n", message, Math.round(linePerSec * 100d) / 100d,
							Math.round(bytesPerSec * 100d) / 100d, Math.round(bytesTransferred / 1024 * 100d) / 100d);
				} else {
					/*fileReadcallback.displayUpdate(message, Math.round(linePerSec * 100d) / 100d,
							Math.round(bytesPerSec * 100d) / 100d, Math.round(bytesTransferred / 1024 * 100d) / 100d);*/
					if(IDefineID.STATE_SHOW_MESSAGE == stateCodeShowMsg) {
						fileReadcallback.displayTxtFiledescriptor(message);
					}					
				}
			}
			
			bytesTransferred += message.length();
			lineCount++;
			
			try {
				if(IDefineID.STATE_STARTED == stateCode) {
					Thread.sleep(delay);	
				}
			} catch (InterruptedException e) {
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail file thread sleep. ", IDefineID.ERROR_THREAD_SLEEP));
			}
		}
	}
	
	public void run() {
		LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
		LOGGER.getLogger().info("Thread start.");
		do {
			try {
				if (stateCode == IDefineID.STATE_STOPPED) {
					LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
					LOGGER.getLogger().info("Stopping send message.");
					break;
				}
				fileHandler();
				//jobProcess();
				jobProcessWithTimeStamp();
			} catch(IOException e) {
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Property IO exception. ", IDefineID.ERROR_IO_EXCEPTION));
				break;
			} catch(InterruptedException e) {
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Interrupted exception. ", IDefineID.ERROR_THREAD_INTERRUPTED_EXCEPTION));
			}
			finally {
				try {
					fileIn.close();
					stream.close();
				} catch (IOException e) {
					//e.printStackTrace();
					LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
					LOGGER.getLogger().error(String.format("Error code : 0x%04x Property IO exception. ", IDefineID.ERROR_IO_EXCEPTION));
				}
			}
		} while (isRepeat == IDefineID.FUNCTION_REPEAT);

		closingNetwork();
		stateCode = IDefineID.STATE_INIT;
		stateCodeSpeed = IDefineID.STATE_STOPPED;
		
		if(isConsoleApp) {
			//System.out.println("Done.");
			LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
			LOGGER.getLogger().info("Done.");
			System.exit(1);
		} else {
			fileReadcallback.playViewToolbarChange(true);
			fileReadcallback.displayTxtFiledescriptor("End of file.");
			fileReadcallback.setThreadStateCode(stateCode);
		}		
	}

	public void start() {
		synchronized (this) {
			if (IDefineID.STATE_INIT != stateCode) {
				throw new IllegalStateException("Already started");
			}

			thisThread = new Thread(this);
			thisThread.start();
			stateCode = IDefineID.STATE_STARTED;
			stateCodeSpeed = IDefineID.STATE_STARTED;
		}
	}

	public void resume() {
		synchronized (this) {
			if (IDefineID.STATE_STARTED == stateCode || IDefineID.STATE_INIT == stateCode) {
				return;
			}

			if (IDefineID.STATE_STOPPED == stateCode) {
				throw new IllegalStateException("Already stopped");
			}

			stateCode = IDefineID.STATE_STARTED;
			stateCodeSpeed = IDefineID.STATE_STARTED;
			thisThread.interrupt();
			speedThread.interrupt();
		}
	}

	public void suspend() {
		synchronized (this) {
			if (IDefineID.STATE_SUSPENDED == stateCode) {
				return;
			}

			if (IDefineID.STATE_INIT == stateCode) {
				throw new IllegalStateException("Not started yet");
			}

			if (IDefineID.STATE_STOPPED == stateCode) {
				throw new IllegalStateException("Already stoped");
			}

			stateCode = IDefineID.STATE_SUSPENDED;
			stateCodeSpeed = IDefineID.STATE_SUSPENDED;
		}
	}

	public void stop() {
		synchronized (this) {
			if (IDefineID.STATE_STOPPED == stateCode) {
				throw new IllegalStateException("Already stoped");
			}

			thisThread.interrupt();
			speedThread.interrupt();
			stateCode = IDefineID.STATE_STOPPED;
			stateCodeSpeed = IDefineID.STATE_STOPPED;
		}
	}

	public void join() {
	}

	public void repeat() {
		synchronized (this) {
			isRepeat = IDefineID.FUNCTION_REPEAT;
		}
	}

	public void noRepeat() {
		synchronized (this) {
			isRepeat = IDefineID.FUNCTION_NO_REPEAT;
		}
	}

	public SendUdpMsg getUdpSending() {
		synchronized (this) {
			return udpSending;
		}
	}

	public SendZeroMqMsg getZeroMqSending() {
		synchronized (this) {
			return zeroMqPushSending;
		}
	}

	public void setPeriodTime(long periodTime) {
		synchronized (this) {
			this.period = periodTime;
		}
	}

	public void setFunctionCode(int functionCode) {
		synchronized (this) {
			this.isRepeat = functionCode;
		}
	}

	public int getStateCode() {
		synchronized (this) {
			return stateCode;
		}
	}

	public int getFunctionCode() {
		synchronized (this) {
			return isRepeat;
		}
	}

	public int getStateCodeShowMsg() {
		return stateCodeShowMsg;
	}

	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}
	
	public void setCheckTimeStamp(boolean check) {
		this.checkTimeStamp = check;
	}
	
	public void setStateCodeShowMsg(int stateCodeShowMsg) {
		this.stateCodeShowMsg = stateCodeShowMsg;
	}
	
	private class SendingSpeedThread extends Thread {
		private long prevBytesTransferred = 0, prevLineCount = 0, down = 0, line = 0, linePerSec_t = 0;
		private double bytesPerSec_t = 0.0;
		
		public void run() {
			//speedThread = Thread.currentThread();
			
			while(true) {				
				while (IDefineID.STATE_SUSPENDED == stateCodeSpeed) {
					fileReadcallback.displaySpeedOfSendingMsg(0.0, 0.0, 0.0);
					try {
						Thread.sleep(24 * 60 * 60 * 1000);
					} catch (InterruptedException e) {
						LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
						LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail file thread sleep. %d", IDefineID.ERROR_THREAD_SLEEP, stateCode));
						if (IDefineID.STATE_SUSPENDED != stateCode) {
							LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
							LOGGER.getLogger().info("Wake up and stating send message.");
							break;
						}
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//e.printStackTrace();
					LOGGER.setLogger(LOGGER.getLogger().getLogger(FileReading.class.getName()));
					LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail file thread sleep. ", IDefineID.ERROR_THREAD_SLEEP));
				}
				
				down = bytesTransferred - prevBytesTransferred;
				if(down > 0) {
					bytesPerSec = bytesPerSec_t = down;
				}
				
				line = lineCount - prevLineCount;
				if(line > 0) {
					linePerSec = linePerSec_t = line;
				}							
				
				prevBytesTransferred = bytesTransferred;
				prevLineCount = lineCount;				
				
				fileReadcallback.displaySpeedOfSendingMsg(linePerSec, bytesPerSec, bytesTransferred);
				
				if(IDefineID.STATE_STOPPED == stateCodeSpeed) {
					//fileReadcallback.displaySpeedOfSendingMsg(0.0, 0.0, 0.0);
					break;
				}
			}
		}		
	}
}
