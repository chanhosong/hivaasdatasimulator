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
 * hsbae			2015. 4. 6.		First Draft.
 */
package com.hhi.vaas.platform.vdm.parser.filetest;

import com.hhi.vaas.platform.vdm.handler.VDMLoader;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.parser.DataParserFactory;
import com.hhi.vaas.platform.vdm.parser.TestResources;
import com.hhi.vaas.platform.vdm.parser.VDMMapping;
import com.hhi.vaas.platform.vdm.parser.VDMParser;
import com.hhi.vaas.platform.vdm.parser.model.DefaultModel;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertNotNull;


/*
 * Note : 
 *   This test class is for developer testing.
 *   The developer or tester can use this manually.
 */

public class DataParserFactoryFileTest {

	private static VesselDataModel vdm;

	/*
	 * @ author : hsbae
	 *   - for testing big size file.
	 *   - for testing vdm mapping coverage
	 *   - input : nmea sentence dump file
	 *   - output : mapping coverage (%)
	 */
	public static void main(String args[]) {
		// init mapping stream
		InputStream mappingXmlStream = DataParserFactoryFileTest.class.getResourceAsStream(TestResources.TEST_MAP_VDR_FILE);
		
		vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TestResources.TEST_VCD_VDR_FILE));
		
		// init nmea stream
		//InputStream nmeaStream = DataParserFactoryFileTest.class.getResourceAsStream(TestResources.TEST_NMEA_MULTI_FILE);
		InputStream nmeaStream = DataParserFactoryFileTest.class.getResourceAsStream(TestResources.TEST_NMEA_JRC_FILE);
		DataInputStream dis = new DataInputStream(nmeaStream);
		
		List<DefaultModel> model;
		
		// create VDMMapping
		VDMMapping vdmMapping = new VDMMapping();
		try {
			vdmMapping.init(mappingXmlStream, vdm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull("invalid resources : mapping stream", mappingXmlStream);
		assertNotNull("invalid resources : nmea stream", nmeaStream);
		assertNotNull("dis fail", dis);

		VDMParser parser;
		String msg;
		String resultMsg;
		
		int totalMsgCount = 0;
		int succeededMsgCount = 0;
		int failedMsgCount = 0;
		
		while(true)
		{
			try {
				msg = dis.readLine();
				//System.out.println(msg);
				if(msg == null)
					break;
				
				totalMsgCount ++;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				break;
			}
			
			try {
				parser = DataParserFactory.getParser(msg);
				
				model = parser.parse(msg, vdmMapping, vdm);
				System.out.println(msg + ">> " + model);
				
				succeededMsgCount++;
				
			} catch (Exception e2) {
				System.out.println("ERR :" + msg);
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
	}
}
//end of DataParserFactoryFileTest.java