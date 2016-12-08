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
 * hsbae			2015. 4. 9.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.eqdmodel;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;


public final class EQDXMLHandler {

	private static EQDXMLModel newXMLModel;
	
	private EQDXMLHandler() {
		// won't be called
	}
	
	public static EQDXMLModel load(InputStream is) {
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
		    builder = builderFactory.newDocumentBuilder();
		    
		    Document xmlDocument = builder.parse(is);
		    
		    xmlDocument.normalize();
		    
		    newXMLModel = new EQDXMLModel(xmlDocument);
		    
		    return newXMLModel;
		    
		} catch (Exception e) {
		    throw new EQDXMLException(e);
		}
	}
	
	public static boolean save(OutputStream os, EquipmentDataHeader eqdHeader, EquipmentDataModel edmRoot) {
		
		try {

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			
			Document xmlNewDocument = null;
			
			EQDXMLModel xmlModel = new EQDXMLModel(null);
			//xmlNewDocument = xmlModel.buildWithTemplateHeader(builder.newDocument(), edmRoot);
			xmlNewDocument = xmlModel.buildWithHeader(builder.newDocument(), eqdHeader, edmRoot);
				
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			DOMSource source = new DOMSource(xmlNewDocument);
			StreamResult result = null;
			
			result = new StreamResult(os);
			transformer.transform(source, result);
			

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	
}
//end of EQDLoader.java