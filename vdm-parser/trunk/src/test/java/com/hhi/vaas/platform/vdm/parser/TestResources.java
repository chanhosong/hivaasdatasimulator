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
 * hsbae			2015. 6. 11.		First Draft.
 */

package com.hhi.vaas.platform.vdm.parser;

public interface TestResources {
	
	// vcd files
	public static final String TEST_VCD_ACONIS_LC_VDR = "/VCD_for_ACONIS_LC_VDR_ver0.6.vcd";
	public static final String TEST_VCD_VDR_FILE = "/VCD_for_ACONIS_LC_VDR_ver0.6.vcd";   //"/VCD_for_JRC_VDR_v0.3_150611.vcd";
	public static final String TEST_VCD_LCS_FILE = "/VCD_for_ACONIS_LC_VDR_ver0.6.vcd";   //"/VCD_for_TM_LCS_v0.4_150601.vcd";
	
	// mapping files
	public static final String TEST_MAP_VDR_FILE = "/Mapping_for_JRC_VDR_v0.7_150726.xml";
	public static final String TEST_MAP_LCS_FILE = "/Mapping_for_TM_LCS_v0.6_150726.xml";
	
	// NMEA DATA
	public static final String TEST_NMEA_JRC_FILE = "/20150301000000_err.nma";
	public static final String TEST_NMEA_MULTI_FILE = "/20150301000000_multi.nma";
	public static final String TEST_NMEA_AIS_FILE = "/20150301000000.nma"; //"/20150301000000_mini.nma";
	
	// LCS KV DATA
	public static final String TEST_KV_LCS_FILE = "/LoadingDataSample.kv";
}
//end of TestFileSet.java