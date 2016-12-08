/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

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
