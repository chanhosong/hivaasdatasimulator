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
 * Bongjin Kwon	2015. 6. 1.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler.validation;

import com.hhi.vaas.platform.vdm.handler.VDMException;
import com.hhi.vaas.platform.vdm.handler.VDMNode;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * equipment raw data validator.
 * 
 * @author BongJin Kwon
 *
 */
public class DataValidator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataValidator.class);
	
	public static final String VALID_RESULT_TRUE = "true";
	public static final String VALID_RESULT_FALSE = "false";
	public static final String VALID_RESULT_DOUBT = "doubt";
	public static final String VALID_RESULT_NA = "NA";
	
	private static final String VALID_TYPE_BOOLEAN = "Boolean";
	private static final String VALID_TYPE_INT = "INT";
	private static final String VALID_TYPE_LONG = "LONG";
	private static final String VALID_TYPE_FLOAT = "FLOAT";
	private static final String VALID_TYPE_DOUBLE = "DOUBLE";
	private static final String VALID_TYPE_STRING = "String";
	private static final String VALID_TYPE_TIMESTAMP = "Timestamp";
	private static final String VALID_TYPE_ENUM = "Enum";
	
	private static final String PATH_MIN = ".rangeCfg.min";
	private static final String PATH_MAX = ".rangeCfg.max";
	
	
	private VesselDataModel vdm;
	private Map<String, String> minMaxMap = new HashMap<String, String>();
	private Map<String, Boolean> rangeCfgMap = new HashMap<String, Boolean>();

	/**
	 * 
	 */
	public DataValidator(VesselDataModel vdm) {
		this.vdm = vdm;
	}
	
	public boolean isEnumType(String bType){
		return VALID_TYPE_ENUM.equals(bType);
	}
	
	/**
	 * equipment raw data type validate.
	 * 
	 * @param systemName
	 * @param vdmpath
	 * @param value equipment raw data
	 * @return
	 */
	public Object convertType(String vdmpath, String value, String bType) {
		Object converted = null;
		
		LOGGER.debug("{}: {}, bType = {}", vdmpath, value, bType);
		
		try {
			converted = convertType(bType, value);
			
		} catch (Exception e) {
			
			LOGGER.warn(e.toString());
			converted = value;
		}
		
		return converted;
	}
	
	public String convertEnumTyp(VDMNode vdmNode, String value, VesselDataModel vdm) {
		
		Map<String, String> enumMap = vdm.getEnumMap(vdmNode.getAttribute("type"));
		
		String enumVal = enumMap.get(value);
		
		if(enumVal == null){
			LOGGER.warn("enum value not found. return original value.");
			return value;
		}
		
		return enumVal;
	}
	
	/**
	 * 
	 * @param bType
	 * @param value
	 */
	protected Object convertType(String bType, String value) {
		
		if (VALID_TYPE_BOOLEAN.equals(bType)) {
			if (value.equalsIgnoreCase("true") == false && value.equalsIgnoreCase("false") == false) {
				throw new RuntimeException("Invalid Boolean Type : "+ value);
			}else{
				return Boolean.parseBoolean(value);
			}
			
		} else if (VALID_TYPE_INT.equals(bType)) {
			return Integer.parseInt(value);
			
		} else if (VALID_TYPE_LONG.equals(bType)) {
			return Long.parseLong(value);
			
		} else if (VALID_TYPE_FLOAT.equals(bType)) {
			return Float.parseFloat(value);
			
		} else if (VALID_TYPE_DOUBLE.equals(bType)) {
			return Double.parseDouble(value);
			
		} else if (VALID_TYPE_STRING.equals(bType)) {
			return value;
		} else if (VALID_TYPE_TIMESTAMP.equals(bType)) {
			return value;
		} else {
			throw new IllegalArgumentException("Unsupported bType : "+ bType);
		}
		
	}
	
	/**
	 * check min max validation target.
	 * 
	 * @param vdmpath
	 * @return
	 */
	public boolean isMinMaxValidationTarget(VDMNode vdmNode) {
		
		String vdmpath = vdmNode.getVdmpath();
		
		if (vdmpath.endsWith(".val") == false) {
			return false;
		}
		String key = vdmNode.getSystemName() + "_" + vdmpath;
		
		Boolean hasRange = rangeCfgMap.get(key);
		
		if (hasRange == null) {
			String parentPath = vdmpath.replaceAll(".val", "");
			
			VDMNode minNode = null;
			VDMNode maxNode = null;
					
			try {
				minNode = vdm.getVDMNode(vdmNode.getSystemName(), parentPath + PATH_MIN);
				maxNode = vdm.getVDMNode(vdmNode.getSystemName(), parentPath + PATH_MAX);
			} catch (VDMException e) {
				// ignore: node not found.
			}
			
			hasRange = minNode != null && maxNode != null;
			rangeCfgMap.put(key, hasRange);
			
		}
		
		return hasRange.booleanValue();
	}
	
	/**
	 * min max validation.
	 * @param vdmpath
	 * @param value
	 * @param bType
	 * @return
	 */
	public String validateMinMax(String vdmpath, String value, String bType){
		
		String[] minMax = getMinMax(vdmpath);
		
		if ( minMax[0] == null || minMax[1] == null ) {
			return VALID_RESULT_DOUBT;
		}
		
		try {
			return doValidateMinMax(value, minMax[0], minMax[1], bType);
			
		} catch (Exception e) {
			
			LOGGER.warn(e.toString());
			return VALID_RESULT_DOUBT;
		}
		
	}
	
	/**
	 * check min max vdmpath.
	 * @param vdmpath
	 * @return
	 */
	public boolean isMinMaxVdmpath(String vdmpath){
		return vdmpath.endsWith(PATH_MIN) || vdmpath.endsWith(PATH_MAX);
	}
	
	/**
	 * save min, max to cache
	 * @param vdmpath
	 * @param value
	 */
	public void saveMinMax(String vdmpath, String value){
		minMaxMap.put(vdmpath, value);
	}
	
	private String[] getMinMax(String vdmpath){
		
		String[] minMax = new String[2];
		
		String parentPath = vdmpath.replaceAll(".val", "");
		
		LOGGER.debug("parentPath : {}", parentPath);
		
		minMax[0] = minMaxMap.get(parentPath + PATH_MIN);
		minMax[1] = minMaxMap.get(parentPath + PATH_MAX);
		
		return minMax;
	}
	
	protected String doValidateMinMax(String value, String min, String max, String bType){
		String result = VALID_RESULT_TRUE;
		
		if (VALID_TYPE_INT.equals(bType)) {
			
			int val = Integer.parseInt(value);
			if (Integer.parseInt(min) > val || val > Integer.parseInt(max)) {
				result = VALID_RESULT_FALSE;
			}
			
		}else if (VALID_TYPE_LONG.equals(bType)) {
			
			long val = Long.parseLong(value);
			if (Long.parseLong(min) > val || val > Long.parseLong(max)) {
				result = VALID_RESULT_FALSE;
			}
			
		}else if (VALID_TYPE_FLOAT.equals(bType)) {
			
			float val = Float.parseFloat(value);
			if (Float.parseFloat(min) > val || val > Float.parseFloat(max)) {
				result = VALID_RESULT_FALSE;
			}
			
		}else{
			LOGGER.warn("Validation Doubt : {}, {}, {}, {}", value, min, max, bType);
			result = VALID_RESULT_DOUBT;
		}
		
		return result;
	}

	protected Map<String, String> getMinMaxMap() {
		return minMaxMap;
	}

	protected Map<String, Boolean> getRangeCfgMap() {
		return rangeCfgMap;
	}

}
//end of Validator.java