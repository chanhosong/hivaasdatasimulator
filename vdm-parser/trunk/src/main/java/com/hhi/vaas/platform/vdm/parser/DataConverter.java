/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.hhi.vaas.platform.vdm.handler.VesselDataModel;

/**
 * Data Converter interface
 * 
 * @author Bong-Jin Kwon
 *
 */
public interface DataConverter {
	
	static String DATA_TYPE_ALARM = "alarm";
	
	/**
	 * convert equipment raw data to json
	 * 
	 * @param orginStr equipment raw data
	 * @return
	 * @throws IOException
	 */
	String convert(String orginStr) throws IOException;
	
	/**
	 * update mapping xml
	 * 
	 * @param mappingXMLStr
	 */
	void updateMapping(String mappingXMLStr) throws SAXException, ParserConfigurationException, IOException;
	
	/**
	 * update VesselDataModel
	 * @param vdm
	 */
	void updateVDM(VesselDataModel vdm);
	
	/**
	 * systemName getter
	 * @return
	 */
	String getSystemName();
	
	/**
	 * handle alarm data
	 * @param orginStr
	 * @param recevedTime
	 * @return
	 */
	String handleAlarmData(String orginStr, long recevedTime);
}
