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
 * Sang-cheon Park	2015. 5. 7.		First Draft.
 */
package com.hhi.vaas.platform.vdm.handler;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * <pre>
 * 
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class ValidatorTest {

	private Validator validator;

	/**
	 * Test method for {@link com.hhi.vaas.platform.vdm.handler.Validator#validate(org.codehaus.jackson.JsonNode)}.
	 */
	@Test
	public void testValidate() {
		/**
		 * 1. initilize
		 */
		try {

			String contents = readFile(Validator.class.getResourceAsStream("/VCD_for_VDR_ver0.3.vcd"));
			validator = new Validator(contents);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		/**
		 * 2. Test & Verify
		 */
		String str = "{\"vdmpath\":\"SLOG/SLOG.SpeedThWaterN.val.f\",\"value\":\"10.0\",\"key\":\"VBW_field:01\"}";
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JsonNode jsonNode = mapper.readTree(str);
			boolean result = validator.validate(jsonNode);
			
			assertTrue("result must be equals with true.", result);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	private String readFile(String path) throws IOException {
		System.out.println(path);
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, Charset.defaultCharset());
	}
	
	private String readFile(InputStream is){
    	
    	StringBuffer sb = new StringBuffer();
    	BufferedReader br = null;
    	try{
	    	br = new BufferedReader(new InputStreamReader(is));
	    	
	    	String line = null;
	    	while ((line = br.readLine()) != null){
	    		if(sb.length() > 0){
	    			sb.append("\n");
	    		}
	    		sb.append(line);
	    	}
	    	
    	} catch (IOException e){
    		e.printStackTrace();
    		
    	} finally {
    		try{
	    		if( br != null){
	    			br.close();
	    		}
    		} catch (IOException e){
    			//ignore.
    		}
    	}
    	
    	return sb.toString();
    	
    }


}
//end of ValidatorTest.java