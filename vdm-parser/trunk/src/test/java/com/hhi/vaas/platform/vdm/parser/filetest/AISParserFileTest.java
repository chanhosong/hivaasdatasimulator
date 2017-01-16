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
 * hsbae			2015. 4. 6.			First Draft.
 */

package com.hhi.vaas.platform.vdm.parser.filetest;

import com.hhi.vaas.platform.vdm.parser.TestResources;
import com.hhi.vaas.platform.vdm.parser.ais.AISDecoder;
import com.hhi.vaas.platform.vdm.parser.nmea.NMEA0183Parser;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertNotNull;


/*
 * Note : 
 *   This test class is for developer testing.
 *   The developer or test can use this manually.
 */

public class AISParserFileTest {

	/*
	 * @ author : hsbae
	 *   - for testing big size file.
	 *   - for testing nmea parser coverage
	 *   - input : nmea sentence dump file
	 *   - output : nmea parser coverage (%)
	 */
	public static void main(String args[]) {
		// init nmea stream
		InputStream nmeaStream = DataParserFactoryFileTest.class.getResourceAsStream(TestResources.TEST_NMEA_AIS_FILE);
		DataInputStream dis = new DataInputStream(nmeaStream);
		
		assertNotNull("invalid resources : nmea stream", nmeaStream);
		assertNotNull("dis fail", dis);

		NMEA0183Parser parser;
		
		String nmeaData = "";
		String resultData = "";
		
		// for statistics
		HashMap<String, Integer> nmeaSuccessMap = new HashMap<>();
		HashMap<String, Integer> nmeaFailMap = new HashMap<>();
		HashMap<String, Integer> aisMap = new HashMap<String, Integer>();
		
		int lineLimit = 50000;
		int lineCount = 0;
		List<Integer> timedMMSI = new ArrayList<Integer>();
		
		int totalMsgCount = 0;
		int succeededMsgCount = 0;
		int failedMsgCount = 0;
		String prevAIS = "";
		
		while(true)
		{
			try {
				nmeaData = dis.readLine();
				if(nmeaData == null)
					break;
				lineCount++;
				
				parser = new NMEA0183Parser(nmeaData);
				
				String sentenceId = parser.getSentenceId();
				
				if("VDM".equals(sentenceId)) {
					
					int msgNum = parser.getInt(0);
					int seqNum = parser.getInt(1);
					String ais = "";
					
					if(msgNum == 2 && seqNum == 1) {
						prevAIS = parser.getString(4); 
						continue;
					}
					else if (msgNum == 2 && seqNum == 2) {
						ais = prevAIS + parser.getString(4);
					}
					else {
						ais = parser.getString(4);
					}
					
					AISDecoder decoder = new AISDecoder();
					List<String> subFields= null; // new ArrayList();
					
					subFields = decoder.decode(ais);
					
					String MMSI = subFields.get(2); 
					
					if(!aisMap.containsKey(MMSI)) {
						aisMap.put(MMSI,  0);
					}
					
					aisMap.put(MMSI, aisMap.get(MMSI) + 1);	
					
				}
				
				if(lineCount > lineLimit) {
					lineCount = 0;
					timedMMSI.add(aisMap.size());
					System.out.println("[" + timedMMSI.size() + "] =" + aisMap);
					aisMap.clear();
					
				}
				
				succeededMsgCount++;
				
			} catch (IllegalArgumentException e1) { 
				//System.out.println(">> ERR 1 : " + nmeaData);
				
				failedMsgCount ++;
				
			
			} catch (Exception e2) {
				System.out.println(">> ERR 2 : " + nmeaData);
				
				failedMsgCount ++;
				
			}
		}
		
		timedMMSI.add(aisMap.size());
		System.out.println("[" + timedMMSI.size() + "] =" + aisMap);
		aisMap.clear();
		
		int listCount = timedMMSI.size();
		System.out.println("=========================");
		System.out.println(timedMMSI);
		
		/*
		if(aisMap.size() > 0) {
			int shipCount = aisMap.size();
			System.out.println(aisMap);
			System.out.println("=========================");
			System.out.println("Total ship count = " + shipCount);
		}
		*/
		
	}
}
//end of DefaultNMEAParserProxyFileTest.java