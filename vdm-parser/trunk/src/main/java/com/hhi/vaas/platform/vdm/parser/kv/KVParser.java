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
 * hsbae			2015. 4. 27.		First Draft.
 */
package com.hhi.vaas.platform.vdm.parser.kv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.parser.AbstractVDMParser;
import com.hhi.vaas.platform.vdm.parser.VDMMapping;
import com.hhi.vaas.platform.vdm.parser.VDMParser;
import com.hhi.vaas.platform.vdm.parser.exception.ParserException;
import com.hhi.vaas.platform.vdm.parser.exception.VDMMappingException;
import com.hhi.vaas.platform.vdm.parser.model.DefaultModel;


/**
 * KVParser class
 * - key value data & alarm data (json format) parser. (support ACONIST, LoadingComputer)
 * @author hsbae
 *
 */
public class KVParser extends AbstractVDMParser {

	private static final Logger logger = LoggerFactory.getLogger(KVParser.class);
	
	private static ObjectMapper om =  new ObjectMapper();
	
	public KVParser() {
	}
	
	
	@Override
	public boolean supports(String rawData) {
		
		rawData = rawData.trim();
		if(rawData.startsWith("{") && rawData.endsWith("}")) {
			return true;
		}
		else {
			return false;
		}

	}

	@Override
	public List<DefaultModel> parse(String KVData, VDMMapping vdmMapping, VesselDataModel vdm){
		
		List<DefaultModel> models = new ArrayList<DefaultModel>();
		
		List <KeyValueItem> kvItems = divideAll(KVData);
		
		String systemName = vdmMapping.getSystemName();
		int fieldCount = kvItems.size();
		
		for(int i = 0; i < fieldCount; i++){
			
			
			String key = kvItems.get(i).getKey();
			String value 	= kvItems.get(i).getValue();
			
			// handle multiple mapping
			List <String> vdmPathList = getVdmFullPathList(vdmMapping, key, vdm);
			
			if(vdmPathList == null || vdmPathList.isEmpty()) {
				//logger.warn("mapping failed. key:{}" , key); 
				String vdmFullPath = "undefined/KV." + key + "_bypass"; // 추후 _bypass 를 빼야 할듯 
				models.add(new DefaultModel(systemName, vdmFullPath, key, value, "true"));
				logger.debug("WARN : No mapping rule for [{}]", key);
				continue;
			}
			
			for(int j=0; j<vdmPathList.size(); j++) {
				String vdmFullPath = vdmPathList.get(j);
				Object valObj = convertType(systemName, vdmFullPath, value, vdm);
				String valid = validate(systemName, vdmFullPath, value, vdm);
				
				models.add(new DefaultModel(systemName, vdmFullPath, key, valObj, valid));
			}
			
			// handling bypass
			//if(key_bypass.equals(key)) {
			//	logger.debug("WARN : bypass keyvalue {}",  key);
			//	break;
			//}
			
			
		}
		/*
		
		if(models.isEmpty())
		{
			
			String vdmFullPath = "undefined/KV." + key + "_bypass";
			models.add(new DefaultModel(systemName, vdmFullPath, key, value, "true"));
			logger.debug("WARN : No mapping rule for [{}]", key);

		}
		*/
		
		return models;
	}
	

	protected List<KeyValueItem> divideAll(String KVData) {

		List<KeyValueItem> itemList = new ArrayList<KeyValueItem>();
		
		
		JsonNode newDoc = null;
		Iterator <JsonNode> jsonItems = null;
		 
		try {
			
			JsonNode node = om.readTree(KVData);
			Iterator <String> fieldNames =  node.getFieldNames();
			
			while(fieldNames.hasNext()) {
				String key = fieldNames.next();
				String value = node.get(key).asText();
				
				if(value != null && !"".equals(value)) {
					itemList.add(new KeyValueItem(key,value));
				}
				
				// System.out.println("=> key:" + name + ", value:" + value);
			}
			
		} catch (JsonProcessingException e) {
			
			throw new ParserException("Invalid JSON Format: "+KVData, e);
		} catch (IOException e) {
			
			throw new ParserException(e);
		}
		
		return itemList;
	}
	
}
//end of KVParser.java