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

package com.hhi.vaas.platform.datasimulator;

public interface IDefineID {	
	public static final String PLUGIN_ID = "com.hhi.vaas.platform.datasimulator";
	public static final String PERSPECTIVE_ID = "com.hhi.vaas.platform.datasimulator.perspective";	
	public static final String GENERATOR_VIEW_ID = "com.hhi.vaas.platform.datasimulator.views.GeneratorView";
	public static final String STATISTICS_VIEW_ID = "com.hhi.vaas.platform.datasimulator.StatisticsView";
	
	public static final String COMMAND_OPEN_FILE_ID = "com.hhi.vaas.platform.datasimulator.command.OpenFile";
	public static final String COMMAND_START_ID = "com.hhi.vaas.platform.datasimulator.command.Start";
	public static final String COMMAND_RELOAD_ID = "com.hhi.vaas.platform.datasimulator.command.Reload";
	public static final String COMMAND_SETTING_ID= "com.hhi.vaas.platform.datasimulator.command.Setting";
	
	public static final String IMG_FILE_LOAD = "icons/generator/file_load.png";
	public static final String IMG_START_PROCESS = "icons/generator/start_process.png";
	public static final String IMG_PAUSE_PROCESS = "icons/generator/pause_process.png";
	public static final String IMG_RELOAD_PROCESS = "icons/generator/reload_process.png";
	public static final String IMG_REPEAT_PROCESS = "icons/generator/repeat_process.png";
	public static final String IMG_REPEAT_PROCESS_FALSE = "icons/generator/repeat_process_false.png";
	public static final String IMG_SETTING = "icons/generator/setting.png";
	public static final String IMG_STATISTICS_PROCESSING = "icons/generator/processing.png";
	public static final String IMG_SHOW_MESSAGE = "icons/generator/show_message.png";
	public static final String IMG_HIDE_MESSAGE = "icons/generator/hide_message.png";
	
	public static final int STATE_NO_EXSIST = 0x1;
	public static final int STATE_INIT = 0x1 << 1;
	public static final int STATE_STARTED = 0x1 << 2;
	public static final int STATE_SUSPENDED = 0x1 << 3;
	public static final int STATE_STOPPED = 0x1 << 4;
	public static final int STATE_SHOW_MESSAGE = 0x1 << 7;
	public static final int STATE_HIDE_MESSAGE = 0x1 << 8;
	
	public static final int FUNCTION_REPEAT = 0x1 << 5;
	public static final int FUNCTION_NO_REPEAT = 0x1 << 6;
	
	public static final int UDP_UNICAST = 0x1 << 11;
	public static final int UDP_MULTICAST = 0x1 << 12;
	public static final int UDP_BROADCAST = 0x1 << 13;
	public static final int ZEROMQ_PUSH = 0x1 << 14;
	
	public static final String PROPERTIES = "./config/datasimulator.properties";
	public static final String PROPERTIES_DIRECTORY = "./config/";
	public static final String PROPERTIES_VIEW_TITLE = "viewtitle";
	public static final String PROPERTIES_TYPE_OF_PROTOCOL = "typeofprotocol";
	public static final String PROPERTIES_IP_ADDRESS = "ipaddress";
	public static final String PROPERTIES_PORT_NUMBER = "portnumber";
	public static final String PROPERTIES_PEROID = "period";
	public static final String PROPERTIES_FUNCTION_CODE = "functioncode";
	public static final String PROPERTIES_FILE_PATH = "filepath";
	public static final String PROPERTIES_VIEW_COUNT = "viewcount";
	public static final String PROPERTIES_NIC = "nic";
	public static final String PROPERTIES_DEVICE = "device";
	public static final String PROPERTIES_DATA_TYPE = "datatype";
	
	public static final String LOGGER_PROPERTIES = "./config/log4j.properties";
	public static final String LOGGER_PROPERTIES_DIRECTORY = "./config/";
	
	public static final int ERROR_WRONG_USAGE = 0x1;
	public static final int ERROR_WRONG_PROTOCOL_REGEX = 0x2;
	public static final int ERROR_WRONG_PEROID_REGEX = 0x3;
	public static final int ERROR_WRONG_REPEAT_REGEX = 0x4;
	public static final int ERROR_WRONG_IP_ADDRESS_REGEX = 0x5;
	public static final int ERROR_WRONG_PORT_NUMBER_REGEX = 0x6;
	
	public static final int ERROR_IO_EXCEPTION = 0x10;
	public static final int ERROR_FILE_NOT_FOUND = 0x11;
	public static final int ERROR_WRONG_FILE_PATH = 0x12;
	public static final int ERROR_Fail_Creating_DIRECTORY = 0x13;
	
	public static final int ERROR_NETWORK_UNKNOWN_HOST = 0x20;
	public static final int ERROR_NETWOKk_FAIL_CREATING_UDP_SOCKET = 0x21;
	
	public static final int ERROR_PART_VIEW_FAIL_CREATING_VIEW = 0x30;
	public static final int ERROR_PART_VIEW_TOO_MANY_VIEWS = 0x31;
	
	public static final int ERROR_THREAD_SLEEP = 0x41;
	public static final int ERROR_THREAD_INTERRUPTED_EXCEPTION = 0x43;
}
