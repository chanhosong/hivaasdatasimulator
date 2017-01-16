/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * @author BongJin Kwon
 *
 */
public abstract class VDMLoader {

	
	public static VesselDataModel load(InputStream is){
			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
			    builder = builderFactory.newDocumentBuilder();
			    
			    Document xmlDocument = builder.parse(is);
			    
			    return new VesselDataModel(xmlDocument);
			    
			} catch (Exception e) {
			    throw new VDMException(e);
			}
			
	}
	
}
