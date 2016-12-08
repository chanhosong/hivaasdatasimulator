package com.hhi.vaas.platform.mappingtool.eqdmodel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemplateItem {
	
	private String basePath;
	private String eqd;
	
	private Map<String, String> templateRules = new HashMap<String, String>();
	
	TemplateItem(String basePath) {
		this.basePath = basePath;
	}
	
	public boolean addRule(String key, String path) {
		templateRules.put(key, path);
		
		return true;
	}
	
	public String getVdmSuffix(String key) {
		return templateRules.get(key);
	}
	
	public Set<String> getKeyList() {
		return templateRules.keySet();
	}
}
