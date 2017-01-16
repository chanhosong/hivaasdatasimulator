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
 * Bongjin Kwon	2015. 3. 30.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser.nmea;

import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.parser.AbstractVDMParser;
import com.hhi.vaas.platform.vdm.parser.VDMMapping;
import com.hhi.vaas.platform.vdm.parser.exception.VDMMappingException;
import com.hhi.vaas.platform.vdm.parser.model.DefaultModel;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * This class is based on the NMEA 0183 parser
 * </pre>
 * @author BongJin Kwon
 *
 */
public class DefaultNMEAParserProxy extends AbstractVDMParser {

	private static final Logger logger = LoggerFactory.getLogger(DefaultNMEAParserProxy.class);
	
	public DefaultNMEAParserProxy() {
	}

	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.vdm.parser.VDMParser#supports(java.lang.String)
	 */
	@Override
	public boolean supports(String rawData) {
		String preHandleData = preHandle(rawData);
		return SentenceValidator.isValid(preHandleData);
	}
	
	/**
	 * nmea data pre handle method.
	 * 
	 * @param nmeaData
	 * @return
	 */
	protected String preHandle(String nmeaData){
		String trimmedData = trim(nmeaData);
		return trimmedData;
	}

	protected String trim(String rawNMEA) {
		String newNMEA;
		int indexOfDelimeter = 0;
		
		if(rawNMEA == null || "".equals(rawNMEA)) {
			return "";
		}
		
		if(rawNMEA.startsWith("$") || rawNMEA.startsWith("!")) {
			return rawNMEA;
		}

		if(rawNMEA.contains("$")) {
			indexOfDelimeter = rawNMEA.indexOf('$');
			newNMEA = rawNMEA.substring(indexOfDelimeter);
		
			return newNMEA;
		}
		else if(rawNMEA.contains("!")) {
			indexOfDelimeter = rawNMEA.indexOf('!');
			newNMEA = rawNMEA.substring(indexOfDelimeter);
		
			return newNMEA;
		}
		else
		{
			return "";
		}
	}
	
	/* (non-Javadoc)
	 * @see com.hhi.vaas.platform.vdm.parser.VDMParser#parse(java.lang.String, com.hhi.vaas.platform.vdm.parser.VDMMapping)
	 */
	@Override
	public List<DefaultModel> parse(String nmeaData, VDMMapping vdmMapping, VesselDataModel vdm) {
		
		String preHandleData = preHandle(nmeaData);
		
		OriginNMEAParser parser = getOriginNMEAParser(preHandleData);
		
		int fieldCount = parser.getItemCount();
		List<DefaultModel> models = new ArrayList<DefaultModel>();
		
		String key_bypass = getSentenceType(parser) + "_bypass";
		String systemName = vdmMapping.getSystemName();
			
		for (int i = 0; i < fieldCount; i++) {
			
			try {
				String key  = vdmMapping.getNMEAKey(getSentenceType(parser), i);
				
				if (key == null) {
					logger.debug("WARN : no mapping rules for " + getSentenceType(parser) + "[" + (i+1) + "]");
					continue;
				}
			
				// handle multiple mapping
				List <String> vdmPathList = getVdmFullPathList(vdmMapping, key, vdm);
				
				if(vdmPathList.isEmpty()) {
					logger.debug("WARN : mapping failed. key:{}", key); 
					continue;
				}
				

				for(int j=0; j<vdmPathList.size(); j++) {
					
					String vdmFullPath = vdmPathList.get(j);
					

					if (vdmFullPath.equals("") || vdmFullPath.isEmpty()) {
						// skip vdmFullPath == null case
						logger.debug("WARN : VDMPath is null. key:{}", key); 
					}
					else if (key_bypass.equals(key)) {
						
						models.add(new DefaultModel(systemName, vdmFullPath, key, preHandleData));
					}
					else
					{
						String value = parser.getString(i);
						Object valObj = convertType(systemName, vdmFullPath, value, vdm);
						String valid = validate(systemName, vdmFullPath, value, vdm);
						
						models.add(new DefaultModel(systemName, vdmFullPath, key, valObj, valid));
					}
				}
				
				if(key_bypass.equals(key)) {
					logger.debug("WARN : bypass sentence {}",  getSentenceType(parser));
					break;
				}
				
			} catch (VDMMappingException e) {
				logger.debug("WARN : {}", e.toString());
				break;
			}
		}
		
		if(models.isEmpty())
		{
			//throw new VDMMappingException("Mapping failed. "+  nmeaData);
			String vdmpath = "undefined/NMEA." + preHandleData.substring(1, preHandleData.indexOf(','));
			String key = getSentenceType(parser) + "_bypass";
			models.add(new DefaultModel(systemName, vdmpath, key, preHandleData));
			logger.debug("WARN : No mapping rule for [{}]", parser.getSentenceId());
		}
		
		
		return models;
	}
	
	/**
	 * get original nmea parser
	 * 
	 * @param nmea
	 * @return
	 */
	protected OriginNMEAParser getOriginNMEAParser(String nmea){
		return new NMEA0183Parser(nmea);
	}

	
	protected String getSentenceType(OriginNMEAParser parser){
		return parser.getSentenceId();
	}
	
}
//end of NMEAParserWrapper.java