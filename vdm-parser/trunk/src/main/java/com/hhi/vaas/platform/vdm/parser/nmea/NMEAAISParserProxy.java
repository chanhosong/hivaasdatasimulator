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
 * Bongjin Kwon	2015. 4. 3.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser.nmea;

import com.hhi.vaas.platform.vdm.parser.ais.AISDecoder;
import com.hhi.vaas.platform.vdm.parser.exception.ParserException;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;

import java.io.IOException;
import java.util.List;

/**
 * @author BongJin Kwon
 *
 */
public class NMEAAISParserProxy extends DefaultNMEAParserProxy {
	
	
	private static final String AIVDM = "!AIVDM";
	private static final String AIVDO = "!AIVDO";
	private static final String FIELD_DELIMITER = String.valueOf(Sentence.FIELD_DELIMITER);

	
	/**
	 * for ParserProxy
	 */
	public NMEAAISParserProxy() {
	}
	
	/**
	 * for OriginNMEAParser
	 * @param nmea
	 */
	public NMEAAISParserProxy(String nmea) {
		
	}

	@Override
	public boolean supports(String rawData) {
		
		if(rawData == null){
			return false;
		}
		
		if (rawData.startsWith(AIVDM) || rawData.startsWith(AIVDO)) {
			return SentenceValidator.isValid(rawData);
		}
		else {
			return false;
		}
	}

	@Override
	protected String preHandle(String nmeaData) {
		
		// remove address field
		int begin = nmeaData.indexOf(FIELD_DELIMITER);
		
		String head = nmeaData.substring(0, begin);
		String temp = nmeaData.substring(begin + 1);


		// copy data fields to list
		String[] temp2 = temp.split(FIELD_DELIMITER, -1);
		
		
		AISDecoder decoder = new AISDecoder();
		List<String> subFields= null; // new ArrayList();
		try {
			subFields = decoder.decode(temp2[4]);
			
			temp2[4] = join(subFields);
			
		} catch (IOException e) {
			throw new ParserException(e);
			
			//e.printStackTrace(); 상위에서 에러 발생여부를 인지할수 있도록 해야 합니다.
		}
		
		// 표준 nmea 포맷으로 재 조립해서 반환.
		return head + FIELD_DELIMITER + join(temp2);
	}

	@Override
	protected OriginNMEAParser getOriginNMEAParser(String nmea) {
		
		return new OriginalNMEAAISParser(nmea);
	}
	
	
	@Override
	protected String getSentenceType(OriginNMEAParser parser) {
		
		return parser.getSentenceId() + "_" + parser.getString(4);
	}

	private String join(String[] strArray){
		
		
		String join = "";
		for (int i = 0; i < strArray.length; i++) {
			
			if(i > 0){
				join += FIELD_DELIMITER;
			}
			join += strArray[i];
		}
		return join;
	}
	
	private String join(List<String> strList){
		
		String join = "";
		int i = 0;
		for (String string : strList) {
			if(i > 0){
				join += FIELD_DELIMITER;
			}
			join += string;
			
			i++;
		}
		
		return join;
	}
	
	

}
//end of NMEAAISParser.java