/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser;

import com.hhi.vaas.platform.vdm.handler.VDMLoader;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * @author Administrator
 *
 */
public class DataConverterFactory {

	/**
	 * create DataConverter by mappingXmlURI
	 * @param mappingXmlURI classpath resource name (e.g. /com/osc/resource/Mapping.xml)
	 * @return
	 * @throws Exception
	 */
	public static DataConverter create(String mappingXmlURI, InputStream vcdInputStream) throws SAXException, ParserConfigurationException, IOException {
		
		VDMMapping vdmMapping = new VDMMapping();
		VesselDataModel vdm = VDMLoader.load(vcdInputStream);
		
		URL url = DataConverterFactory.class.getResource( mappingXmlURI );
		
		vdmMapping.init( url.getFile(), vdm);
		
		DataConverter dataConverter = new JSONConverter(vdmMapping);
		dataConverter.updateVDM(vdm);
		
		return dataConverter;
	}
	
	/**
	 * create DataConverter by mappingXmlStream
	 * @param mappingXmlStream
	 * @return
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static DataConverter create(InputStream mappingXmlStream, InputStream vcdInputStream) throws SAXException, ParserConfigurationException, IOException {
		
		VDMMapping vdmMapping = new VDMMapping();
		VesselDataModel vdm = VDMLoader.load(vcdInputStream);
		
		vdmMapping.init(mappingXmlStream, vdm);
		
		DataConverter dataConverter = new JSONConverter(vdmMapping);
		dataConverter.updateVDM(vdm);
		
		return dataConverter;
	}

}
