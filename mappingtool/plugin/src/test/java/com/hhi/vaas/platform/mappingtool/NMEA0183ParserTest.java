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
package com.hhi.vaas.platform.mappingtool;

import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hhi.vaas.platform.mappingtool.nmea.NMEA0183Parser;


public class NMEA0183ParserTest {

	private static final Logger LOGGER = Logger.getLogger(NMEA0183ParserTest.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNMEA0183Parser() {
		
		String nmeaData = "$AGHTD,V,2.0,R,S,N,15.0,10.0,0.000,0.0,84.0,0.000,0.0,T,A,A,,84.1*17";
		
		try {
			NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
			
			LOGGER.debug("Message ID = " + parser.getSentenceId());
			LOGGER.debug("Field count = " + parser.getItemCount());
			
			for (int i = 0; i < parser.getItemCount(); i++) {
				LOGGER.debug("[" + i + "]= " + parser.getString(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
}
//end of NMEA0183ParserTest.java