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
 * hsbae			2015. 4. 10.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.eqdmodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EQDXMLModel {
	
	private static final Logger LOGGER = Logger.getLogger(EQDXMLModel.class);
	
	private Document xmlDocument;

	private EquipmentDataHeader eqdHeader;
	private EquipmentDataMappingTemplate eqdMapTemplate;

	private String deviceName;
	private String vendorName;
	
	private String protocolName;
	
	public EQDXMLModel(Document xmlDocument) {
		this.xmlDocument = xmlDocument;
		eqdHeader = new EquipmentDataHeader();
		
	}
	
	public EquipmentDataModel parse() {
		EquipmentDataModel edmRoot; // = new EquipmentDataModel (null, "root");
		EquipmentDataModel edmProtocol;
		
		LOGGER.trace("==> EQDXMLModel.parse()");
		
		try {
			
			parserHeader();
			
			Element xmlRoot = (Element) xmlDocument.getDocumentElement();
			
			// parse device name and vendor
			Element device = (Element) xmlDocument.getElementsByTagName("device").item(0);
			
			deviceName = device.getAttribute("name");
			vendorName = device.getAttribute("vendor");
			
			// make a root node for treeview;
			edmRoot = new EquipmentDataModel(null, deviceName);
			
			// parse protocol name
			NodeList protocolList = xmlRoot.getElementsByTagName("protocol");
			Element protocol = (Element) protocolList.item(0);
			
			protocolName = protocol.getAttribute("name");
			
			// add protocol node to root node;
			edmProtocol = new EquipmentDataModel(edmRoot, protocolName);
			edmRoot.child.add(edmProtocol);
			
			// add group nodes to protocol node;
			NodeList groupList = parseSubItems(edmProtocol, protocol, "group");
			
			// add record nodes to group node;
			for (int i = 0; i < groupList.getLength(); i++) {
				NodeList recordList = parseSubItems(edmProtocol.child.get(i), (Element) groupList.item(i), "record");
			}
			
			parseTemplateInfo();
			
			return edmRoot;
		} catch (Exception e) {
			throw new EQDXMLException("Invalid File");
		}
	}
	
	public Document buildWithHeader(Document docSave, EquipmentDataHeader eqdHeader, EquipmentDataModel edmRoot) {
		Element xmlRoot = docSave.createElement("equipmentData");
		docSave.appendChild(xmlRoot);
		
		Element xmlHeader = docSave.createElement("header");
		String id = eqdHeader.getId();
		String revision = eqdHeader.getRevision();
		String version = eqdHeader.getVersion();
		
		if ("".equals(version)) {
			version = "0.0";
		}
		
		if ("".equals(revision)) {
			revision = "0";
		}
		
		xmlHeader.setAttribute("id",  id);
		xmlHeader.setAttribute("revision", revision);
		xmlHeader.setAttribute("version", version);
		xmlRoot.appendChild(xmlHeader);
		
		Element xmlHistory = docSave.createElement("history");
		xmlHeader.appendChild(xmlHistory);
		
		List<HistoryItem> historyList = eqdHeader.getHistoryList();
		for (int i = 0; i < historyList.size(); i++) {
			Element item = docSave.createElement("item");
			item.setAttribute("revision", historyList.get(i).getRevision());
			item.setAttribute("version", historyList.get(i).getVersion());
			item.setAttribute("what", historyList.get(i).getWhat());
			item.setAttribute("when", historyList.get(i).getWhen());
			item.setAttribute("who", historyList.get(i).getWho());
			item.setAttribute("why", historyList.get(i).getWhy());
			xmlHistory.appendChild(item);
		}
		
		Element deviceNode = convToElement(docSave, "device", edmRoot);
		xmlRoot.appendChild(deviceNode);
		
		for (int i = 0; i < edmRoot.child.size(); i++) {
			EquipmentDataModel edmProtocol = edmRoot.child.get(i);
			Element protocolNode = convToElement(docSave, "protocol", edmProtocol);
			deviceNode.appendChild(protocolNode);
			
			for (int j = 0; j < edmProtocol.child.size(); j++) {
				EquipmentDataModel edmGroup = edmProtocol.child.get(j);
				Element groupNode = convToElement(docSave, "group", edmGroup);
				protocolNode.appendChild(groupNode);
				
				for (int k = 0; k < edmGroup.child.size(); k++) {
					EquipmentDataModel edmRecord = edmGroup.child.get(k);
					Element recordNode = convToElement(docSave, "record", edmRecord);
					groupNode.appendChild(recordNode);
				}
			}
		}
		
		return docSave;
	}
	
	/*
	public Document buildWithTemplateHeader(Document docSave, EquipmentDataModel edmRoot) {
		Element xmlRoot = docSave.createElement("equipmentData");
		docSave.appendChild(xmlRoot);
		
		
		Element xmlHeader = docSave.createElement("header");
		xmlRoot.appendChild(xmlHeader);
		
		Element xmlHistory = docSave.createElement("history");
		xmlHeader.appendChild(xmlHistory);
		
		Element item = docSave.createElement("item");
		xmlHistory.appendChild(item);
		
		Element deviceNode = convToElement(docSave, "device", edmRoot);
		xmlRoot.appendChild(deviceNode);
		
		for(int i=0; i<edmRoot.child.size(); i++) {
			EquipmentDataModel edmProtocol = edmRoot.child.get(i);
			Element protocolNode = convToElement(docSave, "protocol", edmProtocol);
			deviceNode.appendChild(protocolNode);
			
			for(int j=0; j<edmProtocol.child.size(); j++) {
				EquipmentDataModel edmGroup = edmProtocol.child.get(j);
				Element groupNode = convToElement(docSave, "group", edmGroup);
				protocolNode.appendChild(groupNode);
				
				for(int k=0; k<edmGroup.child.size(); k++) {
					EquipmentDataModel edmRecord = edmGroup.child.get(k);
					Element recordNode = convToElement(docSave, "record", edmRecord);
					groupNode.appendChild(recordNode);
				}
			}
		}
		
		return docSave;
	}
	*/
	
	/*
	public Document build(Document docSave, EquipmentDataModel edmRoot)
	{
		try {
			Element srcXmlRoot = (Element) xmlDocument.getDocumentElement();
			
			Element targetXmlRoot = docSave.createElement(srcXmlRoot.getNodeName());
			docSave.appendChild(targetXmlRoot);
			
			// copy header block
			Element srcHeader = (Element) srcXmlRoot.getElementsByTagName("header").item(0);
			targetXmlRoot.appendChild(docSave.adoptNode(srcHeader));
			
			// build device block
			Element deviceNode = convToElement(docSave, "device", edmRoot);
			targetXmlRoot.appendChild(deviceNode);
			
			for(int i=0; i<edmRoot.child.size(); i++) {
				EquipmentDataModel edmProtocol = edmRoot.child.get(i);
				Element protocolNode = convToElement(docSave, "protocol", edmProtocol);
				deviceNode.appendChild(protocolNode);
				
				for(int j=0; j<edmProtocol.child.size(); j++) {
					EquipmentDataModel edmGroup = edmProtocol.child.get(j);
					Element groupNode = convToElement(docSave, "group", edmGroup);
					protocolNode.appendChild(groupNode);
					
					for(int k=0; k<edmGroup.child.size(); k++) {
						EquipmentDataModel edmRecord = edmGroup.child.get(k);
						Element recordNode = convToElement(docSave, "record", edmRecord);
						groupNode.appendChild(recordNode);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			docSave = null;
		}
		
		return docSave;
	}
	*/

	
	private Element convToElement(Document doc, String tagName, EquipmentDataModel edmItem) 
	{
		Element element = doc.createElement(tagName);
		element.setAttribute("name", edmItem.getName());
		element.setAttribute("desc", edmItem.getDesc());
		element.setAttribute("type", edmItem.getType());
		
		return element;
	}
	private NodeList parseSubItems(EquipmentDataModel edmParent, Element parent,  String targetTag) {
		NodeList nodeList = parent.getElementsByTagName(targetTag);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element node = (Element) nodeList.item(i);
			String name = node.getAttribute("name");
			String desc = node.getAttribute("desc");
			String type = node.getAttribute("type");
			
			EquipmentDataModel edmItem = new EquipmentDataModel(edmParent, name, desc, type);
			edmParent.child.add(edmItem);
		}
		
		return nodeList;
	}
	
	private boolean parserHeader() {
		
		List<HistoryItem> historyItemList = new ArrayList<HistoryItem>();
		
		NodeList headerList = xmlDocument.getElementsByTagName("header");
		Element header = (Element) headerList.item(0);
		
		String id = header.getAttribute("id");
		eqdHeader.setId(id);
		
		String revision = header.getAttribute("revision");
		eqdHeader.setRevision(revision);
		
		String version = header.getAttribute("version");
		eqdHeader.setVersion(version);
		
		// parse history 
		NodeList historyList = header.getElementsByTagName("history"); 
		Element history = (Element) historyList.item(0);
		
		NodeList hitemList = history.getElementsByTagName("item");
		for (int i = 0; i < hitemList.getLength(); i++) {
			
			Element hitem = (Element) hitemList.item(i);
			String hrevision = hitem.getAttribute("revision");
			String hversion = hitem.getAttribute("version");
			String hwhat = hitem.getAttribute("what");
			String hwhen = hitem.getAttribute("when");
			String hwho = hitem.getAttribute("who");
			String hwhy = hitem.getAttribute("why");
			
			HistoryItem newItem = new HistoryItem(hrevision, hversion, hwhat, hwhen, hwho, hwhy);
			historyItemList.add(newItem);
		}
		
		eqdHeader.setHistoryList(historyItemList);
		
		return true;
	}
	
	private boolean parseTemplateInfo() {
		
		LOGGER.trace("==> EQDXMLModel.parseTemplateInfo()");
		
		EquipmentDataMappingTemplate templateInfo = new EquipmentDataMappingTemplate();
		
		NodeList nodeTemplateList = xmlDocument.getElementsByTagName("template");
	
		for (int i = 0; i < nodeTemplateList.getLength(); i++) {
			Element elTemplate = (Element)  nodeTemplateList.item(i);
			
			String basePath = elTemplate.getAttribute("basepath");
			LOGGER.trace("template[" + i + "]= " + basePath);
			
			TemplateItem templateMapItem = new TemplateItem(basePath);
			
			NodeList nodeRuleList = elTemplate.getElementsByTagName("rule");
			for (int j = 0; j < nodeRuleList.getLength(); j++) {
				Element elRule = (Element) nodeRuleList.item(j);
				String attrKey = elRule.getAttribute("key");
				String attrPath = elRule.getAttribute("path");
			
				LOGGER.trace("template[" + i + "].rule[" + j + "]= " + attrKey + " : " + attrPath);
				
				templateMapItem.addRule(attrKey, attrPath);
			}
			
			templateInfo.addTemplate(basePath, templateMapItem);
			
		}
		
		eqdMapTemplate = templateInfo;
		
		return true;
	}
	
	
	public EQDXMLModel getInstance() {
		return this;
	}
	
	public Element getRoot() {
		return xmlDocument.getDocumentElement();
	}
	
	public String getDocumentId() {
		return eqdHeader.getId();
	}
	
	public String getDocumentVersion() {
		return eqdHeader.getVersion();
	}
	
	public String getDocumentRevision() {
		return eqdHeader.getRevision();
	}

	public String getDeviceName() {
		return deviceName;
	}
	
	public String getProtocolName() {
		return protocolName;
	}
	
	public String getVendorName() {
		return vendorName;
	}
	
	public EquipmentDataHeader getEqdHeader() {
		return eqdHeader;
	}
	
	public EquipmentDataMappingTemplate getTemplateInfo()  {
		return eqdMapTemplate;
	}
	
}
//end of EQDXMLModel.java