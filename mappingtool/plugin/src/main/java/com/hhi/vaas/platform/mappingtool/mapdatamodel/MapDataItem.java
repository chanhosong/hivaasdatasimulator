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
 * hsbae			2015. 4. 29.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.mapdatamodel;

/**
 * MapDataItem Class
 *   - Mapping table data
 * @author hsbae
 *
 */
public class MapDataItem {
	
	private String protocol = "";
	private String function = "";
	private String from = "";
	private String param1 = "";
	private String param2 = "";
	private String to = "";
	
	public MapDataItem(String protocol, String function, String from,
			 String param1, String param2, String to) {
		
		this.protocol = protocol;
		this.function = function;
		this.from = from;
		this.param1 = param1;
		this.param2 = param2;
		this.to = to;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	@Override
	public String toString() {
		return "MapDataItem [protocol=" + protocol + ", function=" + function
				+ ", from=" + from + ", param1=" + param1
				+ ", param2=" + param2 + ", to=" + to + "]";
	}
	
}
//end of MapTableModel.java