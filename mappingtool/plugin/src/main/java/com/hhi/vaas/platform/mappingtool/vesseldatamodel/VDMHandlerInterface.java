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
package com.hhi.vaas.platform.mappingtool.vesseldatamodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hhi.vaas.platform.mappingtool.ui.VesselDataModelView;
import com.hhi.vaas.platform.middleware.common.util.XMLUtil;
import com.hhi.vaas.platform.vdm.handler.VDMLoader;
import com.hhi.vaas.platform.vdm.handler.VDMNode;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.handler.exception.VDMNodeNotFoundException;


/**
 * VDMHandlerInterface class
 *   - interface between UI and vdm-handler
 * @author hsbae
 *
 */

public class VDMHandlerInterface {
	
	private static final Logger LOGGER = Logger.getLogger(VesselDataModelView.class);
	
	private VesselDataModel vdm = null;
	
	public VDMHandlerInterface() {
		
	}
	
	public void load(String vdmFileName) {
		InputStream is;
		try {
			is = new FileInputStream(vdmFileName);
			vdm = VDMLoader.load(is);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			vdm = null;
			//throw (new VDMException("Vdm file not found"));
		}
	}
	
	public void load(InputStream is) {
		try {
			vdm = VDMLoader.load(is);
		} catch (Exception e) {
			vdm = null;
			//throw(new VDMException("Vdm load fail"));
		}
	}
	
	
	public List<String> getSystemList() {
		
		List<String> systemList = new ArrayList<String>();
		
		try {
			NodeList nodeList = vdm.getSystems();
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				
				String systemName = XMLUtil.getAttribute(nodeList.item(i), "name");
				
				//LOGGER.debug("system {}: {}", i, systemName );
				
				systemList.add(systemName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return systemList;
	}
	
	public List<String> getDeviceList(String system) {
		List<String> lDeviceList = new ArrayList<String>();
		
		try {
			NodeList lDevices = vdm.getLDevices(system);
			for (int i = 0; i < lDevices.getLength(); i++) {
				if (lDevices.item(i).getNodeType() == Node.TEXT_NODE) {
					continue;
				}
				String ldeviceName = XMLUtil.getAttribute(lDevices.item(i), "inst");
				lDeviceList.add(ldeviceName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return lDeviceList;
	}
	
	public VDMNode getSystemRoot(String systemName) {
		VDMNode vdmNode = null;
		try {
			vdmNode = vdm.getVDMNode(systemName, null);
		} catch (VDMNodeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vdmNode;
	}
	
	public List<VDMNode> getChildNode(VDMNode vdmNode) {
		return vdmNode.getChildNodes();
	}
	
	
	public String getFullVersion() {
		return vdm.getVersion();
	}

	
	public boolean checkVdmPath(String systemName, String vdmPath) {
		
		boolean bFound = false;
		
		try {
			
			//String vdmFullPath = vdm.getEquipmentPath(systemName, vdmPath) + VDMConstants.DELIMITER1 + vdmPath;
			
			//LOGGER.debug("vdmFullPath = " + vdmFullPath);
			
			VDMNode vdmNode = vdm.getVDMNode(systemName, vdmPath);
			
			if (vdmNode != null) {
				//vdmNode.setMapped(true);
				bFound = true;
			}
			
		} catch (Exception e) {
			LOGGER.warn("[VDM] not found : vdmPath = " + vdmPath);
			bFound = false;
		}
		
		return bFound;
	}
	
	/*
	public VDMModel getVdmRoot(String system) {
		
		VDMModel root = new VDMModel(null, system, null);
		
		try{
			NodeList lDevices = vdm.getLDevices(system);
			
			for (int j = 0; j < lDevices.getLength(); j++) {
				
				if(lDevices.item(j).getNodeType() == Node.TEXT_NODE){
					continue;
				}
				//LOGGER.debug("LDevice {}: {}", j, XMLUtil.getAttribute(lDevices.item(j), "inst") );
			}
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		return null;
	}
	*/
	
}
//end of VDMHandlerInterface.java