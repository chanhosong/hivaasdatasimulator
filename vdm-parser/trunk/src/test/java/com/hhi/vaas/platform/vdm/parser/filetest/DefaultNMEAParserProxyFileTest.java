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

import static org.junit.Assert.assertNotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.hhi.vaas.platform.vdm.parser.nmea.NMEA0183Parser;


/*
 * Note : 
 *   This test class is for developer testing.
 *   The developer or test can use this manually.
 */

public class DefaultNMEAParserProxyFileTest {

	//public static final String TEST_NMEA_FILE = "/20150301000000.nma";//in classpath
	//public static final String TEST_NMEA_FILE = "/20150301000000_mini.nma";//in classpath

	public static final String TEST_NMEA_FILE = "/20150301000000_err.nma";//in classpath
	//public static final String TEST_NMEA_FILE = "/20150301000000_jrc_cf.nma";//in classpath
	/*
	 * @ author : hsbae
	 *   - for testing big size file.
	 *   - for testing nmea parser coverage
	 *   - input : nmea sentence dump file
	 *   - output : nmea parser coverage (%)
	 */
	public static void main(String args[]) {
		// init nmea stream
		InputStream nmeaStream = DataParserFactoryFileTest.class.getResourceAsStream(TEST_NMEA_FILE);
		DataInputStream dis = new DataInputStream(nmeaStream);
		
		assertNotNull("invalid resources : nmea stream", nmeaStream);
		assertNotNull("dis fail", dis);

		NMEA0183Parser parser;
		
		String nmeaData;
		String resultData;
		
		// for statistics
		HashMap<String, Integer> nmeaSuccessMap = new HashMap<>();
		HashMap<String, Integer> nmeaFailMap = new HashMap<>();
		
		int totalMsgCount = 0;
		int succeededMsgCount = 0;
		int failedMsgCount = 0;
		
		while(true)
		{
			try {
				nmeaData = dis.readLine();
				//System.out.println(nmeaData);
				if(nmeaData == null)
					break;
				
				totalMsgCount ++;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				break;
			}
			
			try {
				parser = new NMEA0183Parser(nmeaData);
				
				String sentenceId = parser.getSentenceId();
				if(!nmeaSuccessMap.containsKey(sentenceId)) {
					nmeaSuccessMap.put(sentenceId, 0);
				}
				else {
					//nmeaSuccessMap.put(parser.getSentenceId(), nmeaSuccessMap.get(parser.getSentenceId()) + 1);
					nmeaSuccessMap.put(sentenceId, nmeaSuccessMap.get(sentenceId) + 1);
				}
				succeededMsgCount++;
				
			} catch (IllegalArgumentException e1) { 
				System.out.println(">> ERR 1 : " + nmeaData);
				
				failedMsgCount ++;
			
			} catch (Exception e2) {
				System.out.println(">> ERR 2 : " + nmeaData);
				
				failedMsgCount ++;
			}
		}
		
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		
		// make report
		System.out.println("===================================================");
		System.out.println("Total msg count \t = " + totalMsgCount);
		System.out.println("Succeeded msg count \t = " + succeededMsgCount);
		System.out.println("Failed msg count \t = " + failedMsgCount);
		System.out.println("Success Rate \t\t = " + String.format("%.2f", (double)(succeededMsgCount*1.0/totalMsgCount)*100) + "%");
		System.out.println("===================================================");
		System.out.println(nmeaSuccessMap);
		System.out.println("===================================================");
	}
}
//end of DefaultNMEAParserProxyFileTest.java