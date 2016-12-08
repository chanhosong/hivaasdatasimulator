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
 * hsbae			2015. 4. 30.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.mapdatamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * MapDataXMLHeader Class
 *   - mapping.xml header info
 * @author hsbae
 *
 */
public class MapDataXMLHeader {
	private String headerId;
	private String version;
	private String revision;
	private String equipmentId;
	private double vdmFileVersion;
	private double equipmentFileVersion;
	
	//private List<MapDataVersionItem> vItemList;
	private List<MapDataHistoryItem> hItemList;
	
	public MapDataXMLHeader() {
		headerId = "";
		version = "0";
		revision = "0";
		equipmentId = "";
		vdmFileVersion = 0.0;
		equipmentFileVersion = 0.0;
		
		//vItemList = new ArrayList<MapDataVersionItem>();
		hItemList = new ArrayList<MapDataHistoryItem>();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getHeaderId() {
		return headerId;
	}

	/**
	 * 
	 * @param headerId
	 */
	public void setHeaderId(String headerId) {
		this.headerId = headerId;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * 
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getRevision() {
		return revision;
	}
	
	/**
	 * 
	 * @param revision
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	public double getVdmVersion() {
		return vdmFileVersion;
	}
	
	public void setVdmVersion(double vdmVersion) {
		vdmFileVersion = vdmVersion;
	}
	
	public double getEqdVersion() {
		
		return equipmentFileVersion;
	}
	
	public void setEqdVersion(double eqdVersion) {
		equipmentFileVersion = eqdVersion;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEquipmentId() {
		return equipmentId;
	}
	
	/**
	 * 
	 * @param equpmentid
	 * @return
	 */
	public void setEquipmentId(String equipmentid) {
		this.equipmentId = equipmentid;
	}

	
	public void addHistoryItem(MapDataHistoryItem item) {
		hItemList.add(item);
	}
	
	public List<MapDataHistoryItem> gethItemList() {
		return hItemList;
	}

	public void sethItemList(List<MapDataHistoryItem> hItemList) {
		this.hItemList = hItemList;
	}
}
//end of MapDataXMLHeader.java