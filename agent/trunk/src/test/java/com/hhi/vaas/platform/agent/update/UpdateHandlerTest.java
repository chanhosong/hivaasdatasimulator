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
 * Bongjin Kwon	2015. 4. 7.		First Draft.
 */
package com.hhi.vaas.platform.agent.update;

import com.hhi.vaas.platform.agent.vcd.VCDHandler;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import com.hhi.vaas.platform.vdm.parser.DataConverterFactory;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.taskdefs.optional.ReplaceRegExp;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static org.junit.Assert.*;

public class UpdateHandlerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateHandlerTest.class);
	
	private String configFilePath = "./target/test-classes/context.properties";

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
		
		/*
		 * 이후 test case 에서 정상 사용을 위해 원래 설정으로 원복.
		 */
		File configFile = new File(configFilePath);
		
		ReplaceRegExp task = new ReplaceRegExp();
		
		task.setFile(configFile);
		task.setFlags("gs");
		task.setByLine(true);
		task.setMatch("mapping.xml.path=/Mapping(.*).xml");
		task.setReplace("mapping.xml.path=/Mapping_20150409.xml");
		
		task.execute();
		
		task = new ReplaceRegExp();
		
		task.setFile(configFile);
		task.setFlags("gs");
		task.setByLine(true);
		task.setMatch("vaas.vcd.path=/VCD(.*).vcd");
		task.setReplace("vaas.vcd.path=/VCD_for_TM_LCS_v0.4_150601.vcd");
		
		task.execute();
		
	}

	@Test
	public void testUpdateMapping() {
		PropertyService props = new PropertyService("");
		String nmea = "$GPGGA,121033.70,3606.3557,N,13001.6626,E,2,11,01,+0054,M,+025,M,06,0732*43";
		try{
			
			InputStream mInputStream = UpdateHandlerTest.class.getResourceAsStream("/Mapping_old.xml");
			InputStream vInputStream = VCDHandler.getVCDInputStream(props);
			DataConverter dataConverter = DataConverterFactory.create(mInputStream, vInputStream);
			
			UpdateHandler handler = new UpdateHandler(dataConverter);
			handler.setProps(props);
			handler.setConfigFilePath(configFilePath);// for junit test.
			
			try{
				/*
				 * before update.
				 */
				String result = dataConverter.convert(nmea);
				assertTrue(result.indexOf("_bypass") > -1);
				
			}catch(Exception e){
				e.printStackTrace();
				fail(e.toString());
			}
			
			/*
			 * update.
			 */
			String newMappingXml = getUpdateMappingXML(UpdateHandlerTest.class.getResourceAsStream("/Mapping.xml"));
			UpdateResultModel updateResult = handler.updateMapping(newMappingXml);
			assertTrue("update result must be success.", updateResult.isSuccess());
			
			
			/*
			 * after update.
			 */
			String result = dataConverter.convert(nmea);
			LOGGER.debug(result);
			
			/*
			 * json validation.
			 */
			ObjectMapper om = new ObjectMapper();
			JsonNode jsonObj = om.readTree(result);
			assertNotNull(jsonObj);
			
			/*
			 * check context.properties changing.
			 */
			String preProperty = props.getProperty("mapping.xml.path");
			LOGGER.debug("changed property : {}", preProperty);
			
			PropertyService newProps = new PropertyService("");
			assertEquals(preProperty, newProps.getProperty("mapping.xml.path"));
			
		}catch(Exception e){
			fail(e.toString());
		}
	}
	
	
	@Test
	public void testUpdateVCD() {
		PropertyService props = new PropertyService("");
		
		String vcdPath = props.getProperty("vaas.vcd.path");
		
		try {
			InputStream mInputStream = props.getResourceStreamFromKey("mapping.xml.path");
			InputStream vInputStream = VCDHandler.getVCDInputStream(props);
			
			DataConverter dataConverter = DataConverterFactory.create(mInputStream, vInputStream);
			
			UpdateHandler handler = new UpdateHandler(dataConverter);
			handler.setProps(props);
			handler.setConfigFilePath(configFilePath);// for junit test.
			
			
			String vcdFile = "./target/test-classes/VCD_for_ACONIS_LC_VDR_ver0.6.vcd";
			LOGGER.debug("loading {}", vcdFile);
			String vcdContents = FileUtils.readFileToString(new File(vcdFile));
			UpdateResultModel updateResult = handler.updateVCD(vcdContents);
			assertTrue("update result must be success.", updateResult.isSuccess());
			
			/*
			 * after update
			 */
			String updatedVCDPath = props.getProperty("vaas.vcd.path");
			
			assertTrue(!vcdPath.equals(updatedVCDPath));
			
			File updatedFile = new File("./conf" + updatedVCDPath);
			
			assertTrue(updatedFile.exists());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	private String getUpdateMappingXML(InputStream is){
    	
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
//end of UpdateHandlerTest.java