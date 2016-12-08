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
 * hsbae			2015. 5. 10.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.hhi.vaas.platform.mappingtool.vesseldatamodel.VDMHandlerInterface;
import com.hhi.vaas.platform.vdm.handler.VDMNode;

public final class VDMHandlerInterfaceTest {
	
	//public static final String TEST_VDM_FILE = "/VCD_for_VDR_ver0.3.vcd";//in classpath
	public static final String TEST_VDM_FILE = "/VCD_for_VDR_ver0.3_new.vcd"; //in classpath
	
	private static final Logger LOGGER = Logger.getLogger(VDMHandlerInterfaceTest.class);
	
	private static VDMHandlerInterface vdmh = null;
	
	private VDMHandlerInterfaceTest() {
		
	}
	
	public static void main(String[] args) {
		
		vdmh = new VDMHandlerInterface();
		
		try {
			InputStream is = VDMHandlerInterface.class.getResourceAsStream(TEST_VDM_FILE);
			vdmh.load(is);
		} catch (Exception e) {
			LOGGER.debug(e);
			return;
		}
		
		List<String> systemList = vdmh.getSystemList();
		
		LOGGER.debug(systemList);
		
		List<String> ldeviceList = vdmh.getDeviceList(systemList.get(0));
		LOGGER.debug(ldeviceList);
		
		
		VDMNode root = vdmh.getSystemRoot(systemList.get(0));
		LOGGER.debug(root);
		
	}
}
//end of VDMHandlerInterfaceTest.java