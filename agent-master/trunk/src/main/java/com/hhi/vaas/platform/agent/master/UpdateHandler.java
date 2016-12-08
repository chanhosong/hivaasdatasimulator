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

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BongJin Kwon
 *
 */
public class UpdateHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateHandler.class);

	private String downRootPath;
	private String updateTargetPath;
	private String serviceName;
	private boolean updating;
	/**
	 * 
	 */
	public UpdateHandler(String downRootPath, String updateTargetPath, String serviceName) {
		this.downRootPath = downRootPath;
		this.updateTargetPath = updateTargetPath;
		this.serviceName = serviceName;
	}
	
	/**
	 * updat agent (unzip --> stop agent --> file copy --> start agent)
	 * 
	 * @param zipFile
	 */
	public void update(File zipFile) throws IOException {
		updating = true;
		try {
			
			String realUnzipPath = unzip(zipFile);
			
			CommandUtil.stopAgent(serviceName);
			
			/*
			 * wait until agent stopped.
			 */
			waitStoppedAgent();
			
			copyFiles(realUnzipPath, updateTargetPath);
			
			CommandUtil.startAgent(serviceName);
			
			deleteUnzippedFiles();
			
			LOGGER.info("#### UPDATE SUCCESS ####");
			
		} catch (IOException e) {
			throw e;
		} finally {
			updating = false;
		}
	}

	protected String unzip(File zipFile) throws IOException {
		
		String realUnzipPath = null;
		String unzipPath = null;
    	
		int errorCount = 0;
    	while (realUnzipPath == null) {
    		
    		UnzipUtility unzipper = new UnzipUtility();
	    	
            try {
            	unzipPath = zipFile.getAbsolutePath().replaceAll(".zip", "");
                unzipper.unzip(zipFile, unzipPath);
                
                realUnzipPath = getRealUnzipPath(unzipPath);
                LOGGER.debug("unzipped: {}", realUnzipPath);
                
            } catch (FileNotFoundException ex) {
            	LOGGER.error(ex.getMessage(), ex);
            	errorCount++;
            	
            	if (errorCount == 30) {
					break;
				}
            }
		}
    	
    	if (realUnzipPath != null) {
    		
    		if (zipFile.renameTo(new File(zipFile.getAbsolutePath().replaceAll(".zip", ".zip." + System.currentTimeMillis())))) {
    			LOGGER.debug("renamed {}", zipFile.getAbsolutePath());
			} else {
				LOGGER.warn("renamed fail!! {}", zipFile.getAbsolutePath());
			}
		} 
    	
    	
    	return realUnzipPath;
	}
	
	protected String getRealUnzipPath(String unzipPath){
		String realUnzipPath = null;
		File path = new File(unzipPath);
		
		File[] files = path.listFiles();
		
		if (files.length == 1) {
			realUnzipPath = files[0].getAbsolutePath();
		} else if (files.length > 1) {
			realUnzipPath = unzipPath;
		} else {
			throw new RuntimeException("The unzipped path is empty.");
		}
		
		return realUnzipPath;
	}
	
	
	protected void copyFiles(String srcPath, String destPath)throws IOException{
		
		String libPath = File.separator + "lib";
		
		File libDest = new File(destPath + libPath);
		
		int errCount = 0;
		while (true) {
			try {
				
				if (errCount > 0){
					Thread.sleep(500);
				}
				
				FileUtils.deleteDirectory(libDest);
				break;
				
			} catch (InterruptedException e) {
				// ignore.
			} catch (IOException e) {
				LOGGER.error(e.toString());
				errCount++;
				
				if (errCount == 30) {
					throw e;
				}
			}
		}
		
		FileUtils.copyDirectory(new File(srcPath + libPath), libDest);
		FileUtils.writeStringToFile(new File(destPath + File.separator + "updateSuccess"), "Y");// for agentUpdateNoti=Y
	}
	
	protected void deleteUnzippedFiles() throws IOException{
		File downRoot = new File(downRootPath);
		
		File[] files = downRoot.listFiles();
		
		for (File file : files) {
			
			if(file.isDirectory()){
				FileUtils.deleteDirectory(file);
			}
		}
	}
	
	private void waitStoppedAgent(){
		while (true) {
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				//ignore.
			}
			
			String status = CommandUtil.checkAgentStatus(serviceName);
			
			if (CommandUtil.STOPPED.equals(status)) {
				break;
			}
		}
	}

	public boolean isUpdating() {
		return updating;
	}
	
}
//end of UpdateHandler.java