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
 * Bongjin Kwon	2015. 7. 6.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author BongJin Kwon
 *
 */
public class UpdateFileWatcher {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateFileWatcher.class);

	private final String watchDir;
	private long fileCheckInterval = 3000;
	/**
	 * 
	 */
	public UpdateFileWatcher(String watchDir, long fileCheckInterval) throws IOException {

		this.watchDir = watchDir;
		this.fileCheckInterval = fileCheckInterval;
	}
	
	public void startWatch(UpdateHandler updateHandler) throws IOException {
		
		
		File watchDirFile = new File(watchDir);
		LOGGER.info("watching : {}", watchDir);
		
		while (MasterMain.isStarted()) {
			
			try {
				
				Thread.sleep(fileCheckInterval);
				
				if (watchDirFile.exists() == false) {
			    	if (watchDirFile.mkdirs()) {
			    		LOGGER.debug("maked {}", watchDir);
					} else {
						LOGGER.warn("make failed!! {}", watchDir);
					}
				}
				
				File[] zipFiles = watchDirFile.listFiles(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".zip");
					}
				});
				
				if (zipFiles.length == 0) {
					LOGGER.debug("Empty dir: {}", watchDir);
					continue;
				}
				
				/*
				 * updating
				 */
				updateHandler.update(zipFiles[0]);
				
				
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			
		}
		
		LOGGER.info("stopped!!");
	}
	
	
	
	

}
//end of UpdateFileWatcher.java