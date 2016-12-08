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
 * hsbae			2015. 4. 20.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.eqdmodel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hsbae
 */

public class EquipmentDataMappingTemplate {
	
	private Map<String, TemplateItem> templateList = null;
	
	
	public EquipmentDataMappingTemplate() {
		templateList = new HashMap<String, TemplateItem>();
	}
	
	public boolean addTemplate(String basePath, TemplateItem newTemplate) {
		
		templateList.put(basePath, newTemplate);
		
		return true;
	}

	public TemplateItem getTemplate(String basePath) {
		
		return templateList.get(basePath);
	}
	
	public Map<String, TemplateItem> getTemplateList() {
		return templateList;
	}
	
	
}
//end of EquipmentDataHistory.java