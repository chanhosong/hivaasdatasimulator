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
 * Bongjin Kwon	2015. 6. 2.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser;

import com.hhi.vaas.platform.vdm.handler.VDMNode;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.handler.exception.VDMNodeNotFoundException;
import com.hhi.vaas.platform.vdm.handler.validation.DataValidator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author BongJin Kwon
 *
 */
public abstract class AbstractVDMParser implements VDMParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVDMParser.class);
	
	/**
	 * 
	 */
	
	
	public AbstractVDMParser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * get VDM Full Path. if vdm is null then return vdmpath.
	 * 
	 * @param vdmMapping
	 * @param key
	 * @param vdm
	 * @return if systemName is null and vdm is null then return vdmpath 
	 */
	protected List<String> getVdmFullPathList(VDMMapping vdmMapping, String key, VesselDataModel vdm) {
		
		List<String> vdmPathList = vdmMapping.getVDMPathList(key);
		return vdmPathList;
		/*
		List<String> vdmFullPathList = new ArrayList<String>();
		
		if(vdmPathList == null)
			return vdmFullPathList;
		
		if (vdmMapping.getSystemName() != null && vdm != null) {
			
			for (int i=0; i<vdmPathList.size(); i++) {
				String vdmPath = vdmPathList.get(i);
				String vdmFullPath = vdm.getEquipmentPath(vdmMapping.getSystemName(), vdmPath) + VDMConstants.DELIMITER1 + vdmPath;
				vdmFullPathList.add(vdmFullPath);
			}
		}
		
		return vdmFullPathList;
		*/
	}
	
	/**
	 *  convert value type
	 * @param systemName
	 * @param vdmFullPath
	 * @param value
	 * @param vdm
	 * @return
	 */
	public Object convertType(String systemName, String vdmFullPath, String value, VesselDataModel vdm) {
		
		LOGGER.debug("{}, {}, {}", systemName, vdmFullPath, value);
		
		if (StringUtils.isNotEmpty(systemName) && StringUtils.isNotEmpty(vdmFullPath) && vdm != null ) {
			String vdmpath = VesselDataModel.getVDMPath(vdmFullPath);

			VDMNode vdmNode = null;
			try {
				vdmNode = vdm.getVDMNode(systemName, vdmpath);
			} catch (VDMNodeNotFoundException e) {
				e.printStackTrace();
			}

			return vdm.convertType(vdmNode, value);
		}
		
		return value;
	}
	
	/**
	 * validate data.
	 * @param systemName
	 * @param vdmpath
	 * @param value
	 * @param vdm
	 * @return
	 */
	protected String validate(String systemName, String vdmFullPath, String value, VesselDataModel vdm){
		
		LOGGER.debug("{}, {}, {}", systemName, vdmFullPath, value);
		if(StringUtils.isNotEmpty(systemName) && StringUtils.isNotEmpty(vdmFullPath) && vdm != null ){
			
			String vdmpath = VesselDataModel.getVDMPath(vdmFullPath);

			VDMNode vdmNode = null;
			try {
				vdmNode = vdm.getVDMNode(systemName, vdmpath);
			} catch (VDMNodeNotFoundException e) {
				e.printStackTrace();
			}

			return vdm.validate(vdmNode, value);
		}
		
		return DataValidator.VALID_RESULT_DOUBT;
	}
	
	
}
//end of AbstractVDMParser.java