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
 * Bongjin Kwon	2015. 7. 8.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BongJin Kwon
 *
 */
public class CommandUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CommandUtil.class);
	
	public static final String STOPPED = "STOPPED";
	public static final String START_PENDING = "START_PENDING";
	public static final String RUNNING = "RUNNING";
	
	public static void startAgent(String serviceName){
		
		
		List<String> cmds = new ArrayList<String>();
		
		if (MasterMain.isSystemWindows()) {
			cmds.add("sc");
			cmds.add("start");
			cmds.add(serviceName);
		} else {
			cmds.add(System.getProperty("user.dir") + File.separator + "startup.sh");
			cmds.add("notail");
		}
		
		CommandUtil.exec(cmds);
	}
	
	public static void stopAgent(String serviceName){
		List<String> cmds = new ArrayList<String>();
		
		if (MasterMain.isSystemWindows()) {
			cmds.add("sc");
			cmds.add("stop");
			cmds.add(serviceName);
		} else {
			cmds.add(System.getProperty("user.dir") + File.separator + "kill.sh");
		}
		
		CommandUtil.exec(cmds);
	}
	
	public static String checkAgentStatus(String serviceName){
		
		String status = null;
		List<String> cmds = new ArrayList<String>();
		
		if (MasterMain.isSystemWindows()) {
			cmds.add("sc");
			cmds.add("query");
			cmds.add(serviceName);
		} else {
			cmds.add(System.getProperty("user.dir") + File.separator + "status.sh");
		}
		
		ProcessBuilder bld = new ProcessBuilder(cmds);
		BufferedReader br = null;
		Process p = null;
		try {
			
			p = bld.start();
			
			/*
			 * log 출력.
			 */
			//InputStream is = p.getErrorStream();
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			String line;
			while ((line = br.readLine()) != null) {
				//LOGGER.debug(line);
				
				if (line.indexOf(RUNNING) > -1) {
					LOGGER.debug("{} {}", serviceName, RUNNING);
					status = RUNNING;
					break;
					
				} else if (line.indexOf(START_PENDING) > -1) {
					LOGGER.debug("{} {}", serviceName, START_PENDING);
					status = START_PENDING;
					break;
					
				} else if (line.indexOf(STOPPED) > -1) {
					LOGGER.info("{} {}", serviceName, STOPPED);
					status = STOPPED;
					break;
				}
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(br != null){
				try{ br.close(); }catch(IOException e){}
			}
			
			if(p != null){
				p.destroy();
			}
		}
		
		return status;
	}
	
	public static void exec(List<String> cmds){
		
		ProcessBuilder bld = new ProcessBuilder(cmds);
		BufferedReader br = null;
		Process p = null;
		try {
			
			p = bld.start();
			
			/*
			 * log 출력.
			 */
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null) {
				LOGGER.info(line);
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if(br != null){
				try{ br.close(); }catch(IOException e){}
			}
			
			if(p != null){
				p.destroy();
			}
		}
	}
}
//end of CommandUtil.java