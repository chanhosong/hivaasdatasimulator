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

import java.util.ArrayList;
import java.util.List;

import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TalkerId;

/**
 * @author BongJin Kwon
 *
 */
public class OriginalNMEAAISParser implements OriginNMEAParser {
	
	private String sentenceId;
	
	private List<String> fields;

	/**
	 * @param nmea
	 */
	public OriginalNMEAAISParser(String nmea) {

		sentenceId =  SentenceId.parseStr(nmea);

		// remove address field
		int begin = nmea.indexOf(Sentence.FIELD_DELIMITER);
		String temp = nmea.substring(begin + 1);

		// remove checksum
		if (temp.contains(String.valueOf(Sentence.CHECKSUM_DELIMITER))) {
			int end = temp.indexOf(Sentence.CHECKSUM_DELIMITER);
			temp = temp.substring(0, end );  // hsbae_150406 : add -2 (to remove last field)
		}

		// copy data fields to list
		String[] temp2 = temp.split(String.valueOf(Sentence.FIELD_DELIMITER), -1);
		fields = new ArrayList<String>(temp2.length);
		for (String s : temp2) {
			fields.add(s);
		}
	}

	@Override
	public int getItemCount() {
		
		return fields.size();
	}

	@Override
	public String getString(int index) {
		
		return fields.get(index);
	}

	@Override
	public String getSentenceId() {
		
		return sentenceId;
	}

}
//end of OrininalNMEAAISParser.java