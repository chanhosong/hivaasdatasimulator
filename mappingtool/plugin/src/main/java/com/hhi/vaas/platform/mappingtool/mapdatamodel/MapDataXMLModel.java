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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.hhi.vaas.platform.mappingtool.ui.MappingTableView;

/**
 * @author hsbae
 * Class 	: MapDataXMLModel
 * Desc 	: Mapping XML Matching class
 */
public class MapDataXMLModel extends DefaultHandler {
	
	public static final Logger LOGGER = Logger.getLogger(MapDataXMLModel.class);
	
	// <Header id="VDR_Mapping" version="0" revision="3">
	private static final String EL_HEADER = "Header";
	private static final String ATTR_ID = "id";
	private static final String ATTR_VERSION = "version";
	private static final String ATTR_REVISION = "revision";
	private static final String ATTR_EQUIPMENTID = "equipmentID";
	
	private static final String EL_REFERENCES = "References";		// Element group for Version control
	private static final String EL_VDMFILEVERSION = "VDMFileVersion";		// Element for VDM file version
	private static final String EL_EQDFILEVERSION = "EQDFileVersion";		// Element for Equipment data fileversion
	
	// <Vitem>0.3</Vitem>
	//private static final String EL_VITEM = "Vitem";
	
	// <Hitem version="0" revision="2" when="2015/02/21" who="" what="" why="" />
	private static final String EL_HITEM = "Hitem";
	private static final String ATTR_WHEN = "when";
	private static final String ATTR_WHO = "who";
	private static final String ATTR_WHAT = "what";
	private static final String ATTR_WHY = "why";
	
	// <Mapping protocol="NMEA" function="" from="GGA" param1="field:07" param2="" to="" />
	private static final String EL_MAPPING = "Mapping";
	private static final String ATTR_PROTOCOL = "protocol";
	private static final String ATTR_FUNCTION = "function";
	private static final String ATTR_FROM = "from";
	private static final String ATTR_TO = "to";
	private static final String ATTR_PARAM1 = "param1";
	private static final String ATTR_PARAM2 = "param2";
	
	private static final String PROTOCOL_NMEA = "NMEA";
	private static final String PROTOCOL_KV = "KV";
	
	
	// 
	private MapDataXMLHeader mapDataHeader;
	private List<MapDataItem> mapDataList;
	private Set<String> mapDataVdmPathList;
	
	/**
	 * 
	 */
	public MapDataXMLModel() {
		mapDataHeader = new MapDataXMLHeader();
		mapDataList = new ArrayList<MapDataItem>();
		mapDataVdmPathList = new HashSet<String>();
	}

	/**
	 * Parse Mapping XML
	 * @param
	 * @return
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			 throws SAXException {
		
		if (EL_MAPPING.equals(qName)) {
			
			String protocol = attributes.getValue(ATTR_PROTOCOL);
			String function = attributes.getValue(ATTR_FUNCTION);
			String from = attributes.getValue(ATTR_FROM);
			String to = attributes.getValue(ATTR_TO);
			String param1 = attributes.getValue(ATTR_PARAM1);
			String param2 = attributes.getValue(ATTR_PARAM2);
			
			MapDataItem item = new MapDataItem(protocol, function, from, param1, param2, to);
			mapDataList.add(item);
			mapDataVdmPathList.add(to.replace("VDMPath:", ""));
			
		} else if (EL_HEADER.equals(qName)) {
			
			String id = attributes.getValue(ATTR_ID);
			String version = attributes.getValue(ATTR_VERSION);
			String revision = attributes.getValue(ATTR_REVISION);
			String equipmentid = attributes.getValue(ATTR_EQUIPMENTID);
			
			LOGGER.debug("device id = " + id);
			LOGGER.debug("version = " + version);
			LOGGER.debug("revision = " + revision);
			LOGGER.debug("equipmentId = " + equipmentid);
			
			if ("".equals(equipmentid)) {
				LOGGER.warn("[MAP] equipment id is blank");
			}
			
			mapDataHeader.setHeaderId(id);
			mapDataHeader.setVersion(version);
			mapDataHeader.setRevision(revision);
			mapDataHeader.setEquipmentId(equipmentid);
		} else if (EL_VDMFILEVERSION.equals(qName)) {
			String version = attributes.getValue(ATTR_VERSION);
			String revision = attributes.getValue(ATTR_REVISION);
			
			LOGGER.debug("[MAP] VDM File Version = " + version);
			LOGGER.debug("[MAP] VDM File Revision = " + revision);
			
			double vdmVersion = 0.0;
			try {
				vdmVersion = Double.parseDouble(version);
				
			} catch (Exception e) {
				LOGGER.debug("[MAP] vdm version load error, set to 0.0");
			}
			
			mapDataHeader.setVdmVersion(vdmVersion);
			
		} else if (EL_EQDFILEVERSION.equals(qName)) {
			String version = attributes.getValue(ATTR_VERSION);
			String revision = attributes.getValue(ATTR_REVISION);
			
			LOGGER.debug("[MAP] EQD File Version = " + version);
			LOGGER.debug("[MAP] EQD File Revision = " + revision);
			
			double eqdVersion = Double.parseDouble(version);
			
			mapDataHeader.setEqdVersion(eqdVersion);
			
		} else if (EL_HITEM.equals(qName)) {
			String version = attributes.getValue(ATTR_VERSION);
			String revision = attributes.getValue(ATTR_REVISION);
			String when = attributes.getValue(ATTR_WHEN);
			String who = attributes.getValue(ATTR_WHO);
			String what = attributes.getValue(ATTR_WHAT);
			String why = attributes.getValue(ATTR_WHY);
			
			MapDataHistoryItem hItem = new MapDataHistoryItem(version, revision, when, who, what, why);
			mapDataHeader.addHistoryItem(hItem);
			
		} 
	}
	
	/**
	 * 
	 * @return
	 */
	public List<MapDataItem> getMapDataList() {
		return mapDataList;
	}
	
	public boolean addMapData(MapDataItem mapItem) {
		boolean result = false;

		try {
			String vdmPath = mapItem.getTo().replace("VDMPath:", "");
			if (mapDataVdmPathList.contains(vdmPath)) {
				LOGGER.warn("[MAP] Duplicate VDMPath " + vdmPath);
			} else {
				mapDataVdmPathList.add(vdmPath);
				result = true;
			}
		} catch (Exception e) {
			LOGGER.warn("[MAP] Exception occured : " + mapItem);
			return false;
		}
		
		mapDataList.add(mapItem);
		
		return result;
	}
	
	public boolean removeMapDataByIndex(int index) {
		boolean result = false;
		
		try {
			MapDataItem mapItem = mapDataList.get(index);
			String vdmPath = mapItem.getTo().replace("VDMPath:", "");
			
			mapDataVdmPathList.remove(vdmPath);
			mapDataList.remove(index);
			
			result = true;
		} catch (Exception e) {
			LOGGER.warn("[MAP] Map item delete error");
			return false;
		}
		
		return result;
	}
	
	public boolean checkVdmPath(String vdmPath) {
		return mapDataVdmPathList.contains(vdmPath);
	}
	
	/**
	 * 
	 * @return
	 */
	public MapDataXMLHeader getMapDataHeader() {
		return mapDataHeader;
	}
	
	/**
	 * 
	 * @param mapDataHeader
	 */
	public void setMapDataHeader(MapDataXMLHeader mapDataHeader) {
		this.mapDataHeader = mapDataHeader;
	}
		
}
//end of MapDataXMLHandler.java