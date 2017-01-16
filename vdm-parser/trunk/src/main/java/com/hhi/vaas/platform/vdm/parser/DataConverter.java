/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser;

import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

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
