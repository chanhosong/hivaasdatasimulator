/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser;

import com.hhi.vaas.platform.middleware.common.util.JSONUtil;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.handler.exception.VDMException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author BongJin Kwon
 *
 */
public class JSONConverter implements DataConverter {
	
	private VDMMapping vmdMapping;
	
	private VesselDataModel vdm;
	
	/**
	 * constructor
	 * @param vmdMapping
	 */
	public JSONConverter(VDMMapping vmdMapping) {
		this.vmdMapping = vmdMapping;
	}
	
	/**
	 * update vdm
	 * @param vcdContents
	 */
	public void updateVDM(VesselDataModel vdm) throws VDMException {

		this.vdm = vdm;
	}
	
	/**
	 * data parsing and contert to json
	 */
	@Override
	public String convert(String orginStr) throws IOException {
		
		long recevedTime = System.currentTimeMillis();
		
		VDMParser parser = DataParserFactory.getParser(orginStr);
		
		List<Map> list = parser.parse(orginStr, vmdMapping, vdm);
		
		for (Map map : list) {
			map.put("receivedTime", recevedTime);
		}
		
		return objToJson(list);
	}
	
	private String objToJson(Object obj) throws IOException {
		
		return JSONUtil.objToJson(obj);
	}

	@Override
	public void updateMapping(String mappingXMLStr) throws SAXException, ParserConfigurationException, IOException{
		
		ByteArrayInputStream bais = new ByteArrayInputStream(mappingXMLStr.getBytes());
		
		VDMMapping vdmMapping = new VDMMapping();
		
		vdmMapping.init(bais, vdm);
		
		this.vmdMapping = vdmMapping;
	}

	@Override
	public String getSystemName() {
		// TODO Auto-generated method stub
		return vmdMapping.getSystemName();
	}

	@Override
	public String handleAlarmData(String orginStr, long recevedTime) {
		// TODO Auto-generated method stub
		return null;
	}

}
