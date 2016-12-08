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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hhi.vaas.platform.mappingtool.ui.EquipmentDataView;

/**
 * @author hsbae
 */

public class EquipmentDataHeader {
	
	private static final Logger LOGGER = Logger.getLogger(EquipmentDataView.class);
	
	private String 	id = "";
	private String 	revision = "0";
	private String  version = "0.0";

	
	private List<HistoryItem> historyList = new ArrayList<HistoryItem>();
	
	public EquipmentDataHeader() {
	}
	
	public EquipmentDataHeader(String id, String revision, String version) {
		this.id = id;
		this.revision = revision;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public String getRevision() {
		return revision;
	}

	public String getVersion() {
		return version;
	}
	
	public double getFullVersion() {
		String fullVersion = version; // + "." + revision;
		double dVersion = 0.0;
		try {
			dVersion = Double.parseDouble(fullVersion);
		} catch (Exception e) {
			LOGGER.debug("[EQD] getVersion Error, set to 0.0");
			dVersion = 0.0;
		}
		
		return dVersion;
	}

	public List<HistoryItem> getHistoryList() {
		return historyList;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setRevision(String revision) {
		this.revision = revision;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void addHistoryItem(HistoryItem hitem) {
		historyList.add(hitem);
	}
	
	public void setHistoryList(List<HistoryItem> historyList) {
		this.historyList = historyList;
	}
}
//end of EquipmentDataHistory.java