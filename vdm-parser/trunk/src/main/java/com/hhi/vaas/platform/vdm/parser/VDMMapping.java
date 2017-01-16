/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser;

import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.parser.exception.VDMMappingException;
import com.hhi.vaas.platform.vdm.parser.xml.MappingXmlLoader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 
 * Mapping XML 을 로드(Parsing) 해서 nmea key 정보 및 vdmpath 정보를 제공한다.
 * 
 * @author BongJin Kwon
 *
 */
public class VDMMapping {

	private Map<String, List<String>> keySubkeysMap ;
	
	private Map<String, List<String>> vdmPathMap ;
	
	private String systemName;
	
	public VDMMapping() {
		
	}
	
	/**
	 * Mapping xml 을 load 해서 nmeaKeyMap, vdmPathMap 를 초기화 한다.
	 * 
	 * @param mappingXmlStream Mapping XML InputStream
	 * @throws Exception
	 */
	public void init(InputStream mappingXmlStream, VesselDataModel vdm) throws SAXException, ParserConfigurationException, IOException{
		
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

		
		MappingXmlLoader mappingParser = new MappingXmlLoader(vdm);
		
		parser.parse(mappingXmlStream, mappingParser);
		
		
		initProperties(mappingParser);
		
	}
	
	
	/**
	 * Mapping xml 을 load 해서 nmeaKeyMap, vdmPathMap 를 초기화 한다.
	 * 
	 * @param mappingXmlPath Mapping XML 파일 경로.
	 * @throws Exception
	 */
	public void init(String mappingXmlPath, VesselDataModel vdm) throws SAXException, ParserConfigurationException, IOException{
		
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

		File xmlFile = new File(mappingXmlPath);
		
		MappingXmlLoader mappingParser = new MappingXmlLoader(vdm);
		
		parser.parse(xmlFile, mappingParser);
		
		
		initProperties(mappingParser);
	}
	
	private void initProperties(MappingXmlLoader mappingParser){
		keySubkeysMap = mappingParser.getKeySubkeysMap();
		vdmPathMap = mappingParser.getVdmPathMap();
		systemName = mappingParser.getSystemName();
	}
	
	public List<String> getVDMPathList(String key){
		
		return vdmPathMap.get(key);
	}
	
	/**
	 * NMEA Key 값을 반환한다.
	 * @param sentenceType
	 * @param index NMEA field index ('0' base)
	 * @return
	 */
	public String getNMEAKey(String sentenceType, int index){
		
		List<String> nmeaKeys = keySubkeysMap.get(sentenceType);
		String compareKey = String.format("%s_field:%02d", sentenceType, index+1);
		
		if( nmeaKeys == null){
			throw new VDMMappingException(sentenceType + " is not found in Mapping XML File.");
		}
		
		if( nmeaKeys.contains(sentenceType + "_bypass")) {
			return sentenceType + "_bypass";
		}
		else if( nmeaKeys.contains(compareKey)) {
			return compareKey;
		}
		else {
			//throw new VDMMappingException(sentenceType + ", " + index +" field is not found in Mapping XML File. mapping size : "+ nmeaKeys.size());
			return null;
		}
	}
	
	/**
	 * get systemName from mapping xml.
	 * @return
	 */
	public String getSystemName(){
		return systemName;
	}

}
