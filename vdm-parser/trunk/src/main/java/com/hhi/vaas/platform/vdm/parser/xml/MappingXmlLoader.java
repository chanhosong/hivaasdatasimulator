package com.hhi.vaas.platform.vdm.parser.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.hhi.vaas.platform.vdm.handler.VDMConstants;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;

/**
 * <pre>
 * SAX parser based Mapping XML Loader 
 * </pre>
 * @author BongJin Kwon
 *
 */
public class MappingXmlLoader extends DefaultHandler {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MappingXmlLoader.class);
	//private static final String MAPPINGS_EL		= "Mappings";
	//private static final String ATTR_SYSTEMNAME	= "systemName";
	
	// <Header id="VDR_Mapping" version="0" revision="3">
	private static final String EL_HEADER = "Header";
	private static final String ATTR_ID = "id";
	private static final String ATTR_VERSION = "version";
	private static final String ATTR_REVISION = "revision";
	private static final String ATTR_EQUIPMENTID = "equipmentID";
	
	// <References>
	private static final String EL_REFERENCES = "References";		// Element group for Version control
	
	// <VDMFileVersion version="0" revision="3" />
	private static final String EL_VDMFILEVERSION = "VDMFileVersion";		// Element for VDM file version
	
	// <EQDFileVersion version="0" revision="3" />
	private static final String EL_EQDFILEVERSION = "EQDFileVersion";		// Element for Equipment data fileversion
	
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
	
	//private List<String> nmeaKeys;
	//private List<String> kvKeys;
	
	
	private Map<String, List<String>> keySubkeysMap = new HashMap<String, List<String>>();
	//private Map<String, List<String>> kvKeyMap = new HashMap<String, List<String>>();
	
	private Map<String, List<String>> vdmPathMap = new HashMap<String, List<String>>();
	
	private String systemName;
	
	private VesselDataModel vdm;
	
	private double mapVdmFileVersion = 0.0;
	private double mapEqdFileVersion = 0.0;
	private double mapFileVersion = 0.0;
	
	public MappingXmlLoader(VesselDataModel vdm) {
		this.vdm = vdm;
		mapVdmFileVersion = 0.0;
		mapEqdFileVersion = 0.0;
		mapFileVersion = 0.0;
	}
	

	public double getMapVdmFileVersion() {
		return mapVdmFileVersion;
	}

	public double getMapEqdFileVersion() {
		return mapEqdFileVersion;
	}

	public double getMapFileVersion() {
		return mapFileVersion;
	}

	/**
	 * parse Mapping XML 
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//System.out.println("Start Element: " + qName);

		
		if(EL_MAPPING.equals(qName)){
			
			String protocol = attributes.getValue(ATTR_PROTOCOL);
			String from = attributes.getValue(ATTR_FROM);
			String to = attributes.getValue(ATTR_TO);
			String param1 = attributes.getValue(ATTR_PARAM1);
			
			
			if(PROTOCOL_NMEA.equals(protocol)){
				
				String key = from + "_" + param1;
				
				if(!keySubkeysMap.containsKey(from)){
					
					List <String> keyList = new ArrayList<String>();
					
					keySubkeysMap.put(from, keyList);
					
				}
				
				keySubkeysMap.get(from).add(key);
				//nmeaKeys.add(key);
				
				if(!vdmPathMap.containsKey(key)) {
					List <String> VDMPathList = new ArrayList<String> ();
					
					vdmPathMap.put(key, VDMPathList);
				}
				
				String vdmPath = to.replace("VDMPath:", "");
				String vdmFullPath = null;
				try {
					vdmFullPath = vdm.getEquipmentPath(systemName, vdmPath) + VDMConstants.DELIMITER1 + vdmPath;
				} catch (Exception e) {
					
					LOGGER.warn(e.toString());
					vdmFullPath = vdmPath;
				}
				vdmPathMap.get(key).add(vdmFullPath);
				
			}
			else if(PROTOCOL_KV.equals(protocol)) {
				
				/*
				String key = from + param1;
				if(!keySubkeysMap.containsKey(from)) {
					List <String> keyList = new ArrayList<String> ();
					keySubkeysMap.put(from, keyList);
				}
				
				keySubkeysMap.get(from).add(key);
				
				if(!vdmPathMap.containsKey(key)) {
					List <String> VDMPathList = new ArrayList<String> ();
					
					vdmPathMap.put(key, VDMPathList);
				}
				
				vdmPathMap.get(key).add(to.replace("VDMPath:", ""));
				*/
				
				String key = from;
				
				if(!keySubkeysMap.containsKey(PROTOCOL_KV)) {
					List <String> keyList = new ArrayList<String> ();
					keySubkeysMap.put(PROTOCOL_KV, keyList);
				}
				
				keySubkeysMap.get(PROTOCOL_KV).add(key);
				
				if(!vdmPathMap.containsKey(key)) {
					List <String> VDMPathList = new ArrayList<String> ();
					
					vdmPathMap.put(key, VDMPathList);
				}
				
				String vdmPath = to.replace("VDMPath:", "");
				String vdmFullPath = null;
				try {
					vdmFullPath = vdm.getEquipmentPath(systemName, vdmPath) + VDMConstants.DELIMITER1 + vdmPath;
				} catch (Exception e) {
					
					LOGGER.warn("can not create vdm full path of {}:{}. Caused by: {}", key, vdmPath, e.toString());
					vdmFullPath = vdmPath;
				}
				vdmPathMap.get(key).add(vdmFullPath);
				
			}
			else {
				// other protocol
			}
		}
		else if(EL_HEADER.equals(qName)) {
			String version;
			
			systemName = attributes.getValue(ATTR_EQUIPMENTID);
			version = attributes.getValue(ATTR_VERSION) + "." + attributes.getValue(ATTR_REVISION);
			mapFileVersion = Double.parseDouble(version);
			
			LOGGER.debug("Map File Version = " + version);
			
		}
		else if(EL_VDMFILEVERSION.equals(qName)) {
			String version;
			version = attributes.getValue(ATTR_VERSION) + "." + attributes.getValue(ATTR_REVISION);
			mapVdmFileVersion = Double.parseDouble(version);
			
			LOGGER.debug("Map VDM File Version : " + version);
		}
		else if(EL_EQDFILEVERSION.equals(qName)) {
			String version;
			version = attributes.getValue(ATTR_VERSION) + "." + attributes.getValue(ATTR_REVISION);
			mapEqdFileVersion = Double.parseDouble(version);
			
			LOGGER.debug("Map EQD File Version : " + version);
		}
		else
		{
			
		}
		
	}

	public Map<String, List<String>> getKeySubkeysMap() {
		return keySubkeysMap;
	}

	public Map<String, List<String>> getVdmPathMap() {
		return vdmPathMap;
	}

	public String getSystemName() {
		return systemName;
	}
	
}