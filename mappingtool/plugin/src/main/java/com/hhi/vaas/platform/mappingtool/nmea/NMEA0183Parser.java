/**
 * 
 */
package com.hhi.vaas.platform.mappingtool.nmea;

import java.util.ArrayList;
import java.util.List;

import net.sf.marineapi.nmea.parser.DataNotAvailableException;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.sentence.TalkerId;
/**
 * @author BongJin Kwon
 *
 */
public class NMEA0183Parser implements OriginNMEAParser {

	private char beginChar;
	private String sentenceId;
	private List<String> fields;
	private TalkerId talkerId;
	private AdditionalTalkerId aTalkerId;
	
	/**
	 * 
	 */
	public NMEA0183Parser(String nmea) {

		if (!SentenceValidator.isValid(nmea)) {
			String msg = String.format("Invalid data [%s]", nmea);
			throw new IllegalArgumentException(msg);
		}

		beginChar = nmea.charAt(0);
		parseTalkerId(nmea);
		sentenceId = SentenceId.parseStr(nmea);

		// remove address field
		int begin = nmea.indexOf(Sentence.FIELD_DELIMITER);
		String temp = nmea.substring(begin + 1);

		// remove checksum
		if (temp.contains(String.valueOf(Sentence.CHECKSUM_DELIMITER))) {
			int end = temp.indexOf(Sentence.CHECKSUM_DELIMITER);
			temp = temp.substring(0, end);
		}

		// copy data fields to list
		String[] temp2 = temp.split(String.valueOf(Sentence.FIELD_DELIMITER), -1);
		fields = new ArrayList<String>(temp2.length);
		for (String s : temp2) {
			fields.add(s);
		}
	}
	
	protected void parseTalkerId(String nmea) {
		try {
			talkerId = TalkerId.parse(nmea);
		} catch (IllegalArgumentException e) {
			aTalkerId = AdditionalTalkerId.parse(nmea);
		}
	}
	
	public String getString(int index) {
		String value = fields.get(index);
		
		if (value == null) { // || "".equals(value)) {
			throw new DataNotAvailableException("Data not available");
		}
		
		
		return value;
	}
	
	public double getDouble(int index) {
		double value;
		try {
			value = Double.parseDouble(getString(index));
		} catch (NumberFormatException ex) {
			throw new ParserException("Field does not contain integer value", ex);
		}
		return value;
	}
	
	public int getInt(int index) {
		int value;
		try {
			value = Integer.parseInt(getString(index));
		} catch (NumberFormatException ex) {
			throw new ParserException("Field does not contain integer value", ex);
		}
		return value;
	}
	
	public boolean contains(int index) {
		boolean result = true;
		try {
			getString(index);
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	public int getItemCount() {
		if (fields == null) {
			return 0;
		}
		return fields.size();
	}
	
	public String getTalkerId() {
		
		if (talkerId == null) {
			return aTalkerId.toString();
		}
		
		return talkerId.toString();
		
	}

	public String getSentenceId() {

		return sentenceId;
	}
	
}
