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
 * Bongjin Kwon	2015. 6. 16.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.vcd;

import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.handler.crypto.CryptoException;
import com.hhi.vaas.platform.vdm.handler.crypto.CryptoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * 
 * VCD Handler
 * 
 * @author BongJin Kwon
 *
 */
public class VCDHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VCDHandler.class);

	private VCDHandler() {
	}
	
	/**
	 * get VCD InputStream  
	 * @param props
	 * @return
	 * @throws CryptoException
	 */
	public static InputStream getVCDInputStream(PropertyService props) throws CryptoException {
		
		byte[] descriptBytes = getVCDBytes(props);
		
		return new ByteArrayInputStream(descriptBytes);
	}
	
	
	public static String getVCDContents(PropertyService props) throws CryptoException, UnsupportedEncodingException {
		
		
		byte[] descriptBytes = getVCDBytes(props);
		
		return new String(descriptBytes, "UTF-8");
	}
	
	public static byte[] getVCDBytes(PropertyService props) throws CryptoException {
		
		LOGGER.debug("vaas.vcd.path is {}", props.getProperty("vaas.vcd.path"));
		URL url = VCDHandler.class.getResource(props.getProperty("vaas.vcd.path"));
		
		File file = new File(url.getFile());
		LOGGER.info("loading {}", file.getAbsolutePath());
		
		byte[] descriptBytes = CryptoUtils.decrypt(file);
		
		return descriptBytes;
	}
	
	

}
//end of VCDHandler.java