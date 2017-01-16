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
 * Bongjin Kwon	2015. 7. 30.		First Draft.
 */

package com.hhi.vaas.platform.vdm.parser.config;

import com.hhi.vaas.platform.middleware.common.util.JSONUtil;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.parser.AbstractVDMParser;
import com.hhi.vaas.platform.vdm.parser.VDMMapping;
import com.hhi.vaas.platform.vdm.parser.exception.ParserException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * alarm & config parser
 * @author BongJin Kwon
 *
 */
public class DefaultConfigAlarmParser extends AbstractVDMParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigAlarmParser.class);
	
	private static String DEFAULT_VPATH_KEY = "key";
	private static String DEFAULT_VPATH_NM = "vdmpath";

	/**
	 * 
	 */
	public DefaultConfigAlarmParser() {
		
	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.vdm.parser.VDMParser#supports(java.lang.String)
	 */
	@Override
	public boolean supports(String rawData) {
		
		rawData = rawData.trim();
		if(rawData.startsWith("[{") && rawData.endsWith("}]")) {
			return true;
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.vdm.parser.VDMParser#parse(java.lang.String, com.hhi.vaas.platform.vdm.parser.VDMMapping, com.hhi.vaas.platform.vdm.handler.VesselDataModel)
	 */
	@Override
	public List<Map> parse(String rawData, VDMMapping vdmMapping, VesselDataModel vdm) {
		
		ArrayNode arrayNode = (ArrayNode)JSONUtil.readTree(rawData);
		List<Map> models = new ArrayList<Map>();
		
		
		for (JsonNode jsonNode : arrayNode) {
			models.add(convertMap(jsonNode, vdmMapping));
		}
		
		return models;
	}
	
	protected Map convertMap(JsonNode jsonNode, VDMMapping vdmMapping) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		Iterator <String> fieldNames =  jsonNode.getFieldNames();
		
		while(fieldNames.hasNext()) {
			String key = fieldNames.next();
			String value = jsonNode.get(key).asText();
			
			if (DEFAULT_VPATH_KEY.equals(key)) {
				map.put(key, value);
				
				map.put(DEFAULT_VPATH_NM, getVdmpath(value, vdmMapping));
			} else {
				try {
					map.put(changeKey(key, vdmMapping), value);
				} catch (ParserException e) {
					LOGGER.warn(e.getMessage() + " for " + key);
				}
				
			}
		}
		
		return map;
	}
	
	protected String changeKey(String key, VDMMapping vdmMapping) {
		List<String> vdmpaths = vdmMapping.getVDMPathList(key);
		
		if (vdmpaths == null || vdmpaths.size() == 0) {
			
			throw new ParserException("No mapping rule");
		}
		
		String vdmPath = vdmpaths.get(0);
		int pos = vdmPath.lastIndexOf(".");
		
		return vdmPath.substring(pos + 1);
	}
	
	protected String getVdmpath(String key, VDMMapping vdmMapping) {
		List<String> vdmpaths = vdmMapping.getVDMPathList(key);
		
		if (vdmpaths == null || vdmpaths.size() == 0) {
			
			throw new ParserException("No mapping rule");
		}
		
		return vdmpaths.get(0);
	}

}
//end of ConfigAlarmParser.java