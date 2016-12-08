/* Copyright (C) eMarine Co. Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of eMarine Co. Ltd.
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with eMarine Co. Ltd.
 *
 * Revision History
 * Author			Date				Description
 * ---------	-----------		----------------	------------
 * sehwan			2015. 8. 6.		First Draft.
 */
package com.hhi.vaas.platform.datasimulator.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.hhi.vaas.platform.datasimulator.IDefineID;

public class LoggerSingletone {
	private static volatile LoggerSingletone loggerInstance;
	private Logger LOGGER = Logger.getLogger(LoggerSingletone.class.getName());
	
	public LoggerSingletone() { 
		try {
			initLog4j();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeToFile(File file) throws IOException {
		FileWriter fw = new FileWriter(file);				
		BufferedWriter bufferWritter = new BufferedWriter(fw);
		
    bufferWritter.write("# Root logger option");
    bufferWritter.newLine();
    bufferWritter.write("log4j.rootLogger=DEBUG, file, stdout");
    bufferWritter.newLine();
    bufferWritter.write("# Direct log messages to a log file");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.file=org.apache.log4j.RollingFileAppender");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.file.File=./log/logging.log");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.file.MaxFileSize=10MB");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.file.MaxBackupIndex=10");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.file.layout=org.apache.log4j.PatternLayout");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
    bufferWritter.newLine();
    bufferWritter.write("# Direct log messages to stdout");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.stdout=org.apache.log4j.ConsoleAppender");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.stdout.Target=System.out");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.stdout.layout=org.apache.log4j.PatternLayout");
    bufferWritter.newLine();
    bufferWritter.write("log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
    bufferWritter.newLine();
    bufferWritter.close();
	}

	private void initLog4j() throws IOException {
		boolean isDir = CommonMethodes.directoryMake(IDefineID.LOGGER_PROPERTIES_DIRECTORY);
		String filePath = null;
		File file = null;
		
		if(isDir) {
			file = new File(IDefineID.LOGGER_PROPERTIES);
			
			if(!file.exists() || file.length() < 1) {
				file.createNewFile();
			}
			
			writeToFile(file);
			filePath = file.getCanonicalPath();
			
			PropertyConfigurator.configure(filePath);

			LOGGER.debug("Log4j initialized with " + filePath);
			LOGGER.info("Log file location : " + System.getProperty("user.dir") + "/log/");
		}		
	}
	
	public static LoggerSingletone getInstance() {
		if (loggerInstance == null) {
			synchronized (LoggerSingletone.class) {
				if (loggerInstance == null) {
					loggerInstance = new LoggerSingletone();
				}
			}
		}
		return loggerInstance;
	}
	
	public Logger getLogger() {
		return LOGGER;
	}
	
	public void setLogger(Logger logger) {
		this.LOGGER = logger;
	}
}
//end of LoggerSingletone.java