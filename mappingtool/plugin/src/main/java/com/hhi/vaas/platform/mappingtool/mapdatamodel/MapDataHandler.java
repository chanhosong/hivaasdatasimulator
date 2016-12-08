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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.hhi.vaas.platform.mappingtool.Activator;
import com.hhi.vaas.platform.mappingtool.ui.MPTException;

/**
 * MapDataHandler Class
 *   Load/Save Mapping.xml
 * @author hsbae
 *
 */

public final class MapDataHandler {
	private static final String EL_MAPDATA = "MapData";
	private static final String EL_HEADER = "Header"; 
	private static final String EL_REFERENCES = "References";		// Element group for Version control
	private static final String EL_VDMFILEVERSION = "VDMFileVersion";		// Element for VDM file version
	private static final String EL_EQDFILEVERSION = "EQDFileVersion";		// Element for Equipment data fileversion
	
	//private static final String EL_VITEM = "Vitem";
	private static final String EL_HISTORY = "History";
	private static final String EL_HITEM = "Hitem";
	private static final String EL_MAPPINGS = "Mappings";
	private static final String EL_MAPPING = "Mapping";
	
	private static final String ATTR_ID = "id";
	private static final String ATTR_VERSION = "version";
	private static final String ATTR_REVISION = "revision";
	private static final String ATTR_EQUIPMENTID = "equipmentID";
	private static final String ATTR_WHEN = "when";
	private static final String ATTR_WHO = "who";
	private static final String ATTR_WHAT = "what";
	private static final String ATTR_WHY = "why";
	
	private static final String ATTR_PROTOCOL = "protocol";
	private static final String ATTR_FUNCTION = "function";
	private static final String ATTR_FROM = "from";
	private static final String ATTR_PARAM1 = "param1";
	private static final String ATTR_PARAM2 = "param2";
	private static final String ATTR_TO = "to";
	
	private static final Logger LOGGER = Logger.getLogger(MapDataHandler.class);
	
	
	private MapDataHandler() {
		// won't be called
	}
	
	/**
	 * 
	 * @param strMapXmlFilename
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static MapDataXMLModel load(String strMapXmlFilename) 
			throws ParserConfigurationException, SAXException, IOException {
		
		InputStream is = new FileInputStream(strMapXmlFilename);
		MapDataXMLModel xmlModel = load(is);
		
		// check for mapping.xml 
		//if (xmlModel.getMapDataHeader().gethItemList().isEmpty() 
		//		|| xmlModel.getMapDataList().isEmpty()) {
		//	throw new MPTException("Invalid file");
		//}
		
		if (xmlModel.getMapDataList().isEmpty()) {
			throw new MPTException("Invalid file.");
		}
		
		return xmlModel;
		
	}
	
	/**
	 * 
	 * @param mapXmlStream
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static MapDataXMLModel load(InputStream mapXmlStream) 
			throws ParserConfigurationException, SAXException, IOException {
		
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

		MapDataXMLModel mapDataModel = new MapDataXMLModel();
		
		parser.parse(mapXmlStream, mapDataModel);
		
		// check for mapping.xml 
		//if (mapDataModel.getMapDataHeader().gethItemList().isEmpty() 
		//		|| mapDataModel.getMapDataList().isEmpty()) {
		//	throw new MPTException("Invalid file");
		//}
		
		if (mapDataModel.getMapDataList().isEmpty()) {
			throw new MPTException("Invalid file.");
		}
		
		
		return mapDataModel;
	}
	
	public static String prettyFormat(String input, int indent) {
	    try {
	    	
	    	
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", indent);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}

	public static String prettyFormat(String input) {
	    return prettyFormat(input, 2);
	}
	
	public static void xmlFormatter(InputStream is, OutputStream os, int indent) {
		Source xmlInput = new StreamSource(is);
		StreamResult xmlOutput = new StreamResult(os);
		
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "" + indent);
			
			transformer.transform(xmlInput,  xmlOutput);
		} catch (Exception e) {
			
			LOGGER.debug("ERR : transform error");
		}
		
	}
	
	
	public static void save(MapDataXMLModel mapModel, String strFileName, String systemName) 
			throws XMLStreamException, FileNotFoundException {
		
		save(mapModel, strFileName, 0.0, 0.0, systemName);
	}
	
	public static void save(MapDataXMLModel mapModel, String strFileName, double vdmVersion, double eqdVersion, String systemName) 
			throws XMLStreamException, FileNotFoundException {
		
		String strTempFile = strFileName + "_";
		
		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			FileOutputStream fos = new FileOutputStream(strTempFile);
		    
			XMLStreamWriter writer = factory.createXMLStreamWriter(fos, "UTF-8");
			
			writer.writeStartDocument("UTF-8", "1.0");
			
			// <MapData>
			writer.writeStartElement(EL_MAPDATA);
			
			// <Header id="VDR_Mapping" version="0" revision="3">
			MapDataXMLHeader mapDataHeader = mapModel.getMapDataHeader();
			String id = mapDataHeader.getHeaderId();
			String version = mapDataHeader.getVersion();
			String revision = mapDataHeader.getRevision();
			String equipmentid = systemName;
			
			
			writer.writeStartElement(EL_HEADER);
			writer.writeAttribute(ATTR_ID, id);
			writer.writeAttribute(ATTR_VERSION,  version);
			writer.writeAttribute(ATTR_REVISION,  revision);
			writer.writeAttribute(ATTR_EQUIPMENTID, equipmentid);
			
			// <References>
			writer.writeStartElement(EL_REFERENCES);
			
			// <VDMFileVersion>
			writer.writeEmptyElement(EL_VDMFILEVERSION);
			
			// --> vdm version
			double mapVdmVersion = mapDataHeader.getVdmVersion();
			double saveVdmVersion = (mapVdmVersion >= vdmVersion)? mapVdmVersion : vdmVersion;
			
			String strVdmFullVersion = Double.toString(saveVdmVersion); 
			 
			writer.writeAttribute(ATTR_VERSION, strVdmFullVersion);
			writer.writeAttribute(ATTR_REVISION, "0");
			
			// <EQDFileVersion>
			writer.writeEmptyElement(EL_EQDFILEVERSION);
						
			// --> eqd version
			double mapEqdVersion = mapDataHeader.getEqdVersion();
			double saveEqdVersion = (mapEqdVersion >= eqdVersion)? mapEqdVersion : eqdVersion;
			
			String strEqdFullVersion = Double.toString(saveEqdVersion);
			
			writer.writeAttribute(ATTR_VERSION, strEqdFullVersion);
			writer.writeAttribute(ATTR_REVISION, "0");
			
			writer.writeEndElement();
			
			// <History>
			writer.writeStartElement(EL_HISTORY);
			
			// <Hitem version="0" revision="2" when="2015/02/21" who="" what="" why="" />
			List<MapDataHistoryItem> hItemList = mapDataHeader.gethItemList();
			int itemCount = hItemList.size();
			for (int i = 0; i < itemCount; i++) {
				MapDataHistoryItem hItem = hItemList.get(i);
				
				writer.writeEmptyElement(EL_HITEM);
				writer.writeAttribute(ATTR_VERSION, hItem.getVersion());
				writer.writeAttribute(ATTR_REVISION, hItem.getRevision());
				writer.writeAttribute(ATTR_WHEN, hItem.getWhen());
				writer.writeAttribute(ATTR_WHO, hItem.getWho());
				writer.writeAttribute(ATTR_WHAT, hItem.getWhat());
				writer.writeAttribute(ATTR_WHY, hItem.getWhy());
			}
			
			// </History>
			writer.writeEndElement();
			
			// </Header>
			writer.writeEndElement();
			
			// <Mappings>
			writer.writeStartElement(EL_MAPPINGS);
			
			// <Mapping protocol="NMEA" function="" from="GGA" param1="field:01" param2="" 
			//       to="VDMPath:GPS/GPS1.Time.utc" />
			List<MapDataItem> list = mapModel.getMapDataList();
			itemCount = list.size();
			for (int i = 0; i < itemCount; i++) {
				MapDataItem mItem = list.get(i);
				
				writer.writeEmptyElement(EL_MAPPING);
				writer.writeAttribute(ATTR_PROTOCOL, mItem.getProtocol());
				writer.writeAttribute(ATTR_FUNCTION, mItem.getFunction());
				writer.writeAttribute(ATTR_FROM, mItem.getFrom());
				writer.writeAttribute(ATTR_PARAM1, mItem.getParam1());
				writer.writeAttribute(ATTR_PARAM2, mItem.getParam2());
				writer.writeAttribute(ATTR_TO, mItem.getTo());
			}
			
			// </Mappings>
			writer.writeEndElement();
			
			// </MapData>
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			writer.close();
			fos.close();
			
			// formatting
			
			InputStream is = new FileInputStream(strTempFile);
			OutputStream os = new FileOutputStream(strFileName);
			try {
				xmlFormatter(is, os, 4);
				
				//File f = new File(strTempFile);
				//f.delete();
			} catch (Exception e) {
				LOGGER.debug("ERR : Formatting error");
				
			}
			
			is.close();
			os.close();
			
			// delete temp file 
			try {
				File fTemp = new File(strTempFile);
				if (fTemp.delete() == true) {
					LOGGER.debug("OK : " + strTempFile);
				} else {
					LOGGER.debug("NOK : " + strTempFile);
				}
			} catch (Exception e) {
				LOGGER.debug("ERR : " + strTempFile);
			}
			
		} catch (Exception e) {
			LOGGER.debug("ERR: MapData save error");
		}
	}
}
//end of MapDataHandler.java