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
 * Bongjin Kwon	2015. 5. 28.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler.struct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author BongJin Kwon
 *
 */
public class StructureCreator {

	private static final Logger LOGGER = LoggerFactory.getLogger(StructureCreator.class);
	
	private ObjectMapper om = new ObjectMapper();
	
	/**
	 * 
	 */
	public StructureCreator() {
		// TODO Auto-generated constructor stub
	}
	
	public JsonNode create(VDMPathDatas vdmPathDatas){
		
		ObjectNode rootNode = om.createObjectNode();
		
		for (Entry<String, List<ItemData>> entry : vdmPathDatas.entrySet()) {
			
			setStructuredNode(rootNode, entry.getKey(), entry.getValue());
			
		} 
		
		return rootNode;
	}
	
	private void setStructuredNode(ObjectNode rootNode, String vdmFullPath, List<ItemData> datas) {
		LOGGER.debug("vdmFullPath: {}", vdmFullPath);
		
		String[] highTokens = StringUtils.split(vdmFullPath, "/");
		String[] lowTokens = StringUtils.split(highTokens[highTokens.length - 1], "."); // split highTokens last element (ESDR.DepthF.val.f)
		
		highTokens = (String[])ArrayUtils.remove(highTokens, highTokens.length - 1);// remove last element (ESDR.DepthF.val.f)
		
		String[] allTokens = (String[])ArrayUtils.addAll(highTokens, lowTokens);
		
		createChildren(rootNode, allTokens, 0, datas);
		
	}
	
	private void createChildren(ObjectNode parent, String[] allTokens, int tokenIndex, List<ItemData> datas) {
		
		LOGGER.debug("token: {}", allTokens[tokenIndex]);
		
		if (allTokens.length == (tokenIndex + 1)) {
			
			// leaf node
			parent.put(allTokens[tokenIndex], om.valueToTree( toArray(datas) ));
			return;
			
		} else {
			
			ObjectNode child = (ObjectNode)parent.get(allTokens[tokenIndex]);
			
			if(child == null){
				child = om.createObjectNode();
				parent.put(allTokens[tokenIndex], child);
			}
			
			createChildren(child, allTokens, tokenIndex + 1, datas);
		}
		
	}
	
	private Object[] toArray(List<ItemData> datas) {
		List<Object> list = new ArrayList<Object>();
		
		for (ItemData item : datas) {
			list.add( item.getTimestamp() );
			list.add( item.getValue() );
		}
		
		return list.toArray();
	}

}
//end of StructureCreator.java