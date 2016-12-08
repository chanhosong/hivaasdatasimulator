/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser.model;

import java.util.HashMap;

/**
 * json 으로 변환될 기본 모델 클래스.
 * 
 * @author BongJin Kwon
 *
 */
public class DefaultModel extends HashMap<String, Object> {
	
	/**
	 * validation is default (true).
	 * 
	 * @param vdmpath
	 * @param key
	 * @param value
	 */
	public DefaultModel(String systemName, String vdmpath, String key, Object value) {

       this(systemName, vdmpath, key, value, "true");
	}
	
	/**
	 * 
	 */
	public DefaultModel(String systemName, String vdmpath, String key, Object value, String validResult) {

	   put("systemName", systemName);
       put("vdmpath", vdmpath);
       put("key", key);
       put("value", value);
       put("valid", validResult);
	}
}
