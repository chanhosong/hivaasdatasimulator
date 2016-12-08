package com.hhi.vaas.platform.vdm.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.hhi.vaas.platform.middleware.common.util.JSONUtil;
import com.hhi.vaas.platform.middleware.common.util.XMLUtil;
import com.hhi.vaas.platform.vdm.handler.validation.DataValidator;

public class VesselDataModelTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VesselDataModelTest.class);
	private static final String TEST_VCD_FILE = "/VCD_for_ACONIS_LC_VDR_ver0.6.vcd";
	private static final String VDM_ID = "VDM_for_ACONIS_LC_VDR";
	
	private static VesselDataModel vdm;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream("/VCD_for_VDR_ver0.3.vcd"));
		vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TEST_VCD_FILE));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		assertNotNull("vdm must not be null.", vdm);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetSystems() {
		
		/*
		 * test VesselDataModel.getSystems & getLDevices
		 */
		try{
			NodeList nodeList = vdm.getSystems();
			
			/*
			 * check not null, length
			 */
			assertNotNull(nodeList);
			assertTrue(nodeList.getLength() > 0);
			
			for (int i = 0; i < nodeList.getLength(); i++) {
				
				String systemName = XMLUtil.getAttribute(nodeList.item(i), "name");
				
				LOGGER.debug("system {}: {}", i, systemName );
				
				NodeList lDevices = vdm.getLDevices(systemName);
				
				/*
				 * check not null, length
				 */
				assertNotNull(lDevices);
				assertTrue(lDevices.getLength() > 0);
				
				for (int j = 0; j < lDevices.getLength(); j++) {
					
					if(lDevices.item(j).getNodeType() == Node.TEXT_NODE){
						continue;
					}
					LOGGER.debug("LDevice {}: {}", j, XMLUtil.getAttribute(lDevices.item(j), "inst") );
				}
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
	

	@Test
	public void testGetVDMNode() {
		/*
		 * test VesselDataModel.getVDMNode.
		 */
		try{
			//VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1.Pos.latitude.deg");//
			VDMNode vdmNode = vdm.getVDMNode("VDR", "PositioningDevice/GPSSensor1.Time.UTCTime.utc");
			
			LOGGER.debug("vdmNode: {}", vdmNode);
			
			/*
			 * check not null, BDA node 
			 */
			assertNotNull(vdmNode);
			//assertEquals(VDMConstants.NODE_NAME_BDA, vdmNode.getNodeName());
			assertEquals(VDMConstants.NODE_NAME_DA, vdmNode.getNodeName());
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testGetChildNodes(){
		
		/*
		 * test VesselDataModel.getChildNodes.
		 */
		try{
			List<VDMNode> vdmNodes = vdm.getChildNodes("VDR", "PositioningDevice/GPSSensor1.Time");

			
			/*
			 * check not null, size 
			 */
			assertNotNull(vdmNodes);
			assertTrue(vdmNodes.size() > 1);
			
			LOGGER.debug("vdmNodes.size : {}", vdmNodes.size());
			
			
			/**
			 * check child nodes
			 */
			for (VDMNode vdmNode : vdmNodes) {
				LOGGER.debug(vdmNode.toString());
				//assertEquals(VDMConstants.NODE_NAME_BDA, vdmNode.getNodeName());
				assertEquals(VDMConstants.NODE_NAME_SDO, vdmNode.getNodeName());
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testGetVersion(){
		/*
		 * test VesselDataModel.getVersion.
		 */
		try{
			String version = vdm.getVersion();
			
			LOGGER.debug("vdm version: {}", version);
			
			assertEquals("0.6", version);
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testGetId(){
		/*
		 * test VesselDataModel.getId.
		 */
		try{
			String id = vdm.getId();
			
			LOGGER.debug("vdm id: {}", id);
			
			assertEquals(VDM_ID, id);
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testGetEquipmentPath() {
		
		/*
		 * test VesselDataModel.getEquipmentPath.
		 */
		try{
			String path = vdm.getEquipmentPath("VDR", "CollisionAvoidanceDevice");
			
			LOGGER.debug("equipmentPath: {}", path);
			
			assertEquals("Navigational/Measurement", path);
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
	}
	
	
	
	@Test
	public void testXSDValidate() {
		
		/*
		 * XSD Validation test
		 * - xsdValidate must be true.
		 */
		boolean xsdValidate = false;
		
		String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

		try{
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				
				dbf.setNamespaceAware(true);
				dbf.setValidating(xsdValidate);
				
				if (xsdValidate) {
				    try {
				        dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
				    }
				    catch (IllegalArgumentException x) {
				        System.err.println("Error: JAXP DocumentBuilderFactory attribute not recognized: " + JAXP_SCHEMA_LANGUAGE);
				        System.err.println("Check to see if parser conforms to JAXP spec.");
				        System.exit(1);
				    }
				}

				
			    builder = dbf.newDocumentBuilder();
			    
			    builder.setErrorHandler(new ErrorHandler(){

					@Override
					public void warning(SAXParseException exception)
							throws SAXException {
						exception.printStackTrace();
						
					}

					@Override
					public void error(SAXParseException exception)
							throws SAXException {
						exception.printStackTrace();
						
					}

					@Override
					public void fatalError(SAXParseException exception)
							throws SAXException {
						exception.printStackTrace();
						
					}
			    	
			    });
			    
			    
			    Document xmlDocument = builder.parse(VesselDataModel.class.getResourceAsStream("/dispatcher-servlet.xml"));
			    //Document xmlDocument = builder.parse(VesselDataModel.class.getResourceAsStream("/VCD_for_GPS_rev1.1.vcd"));
			    
			    assertNotNull("xmlDocument must not be null.", xmlDocument);
			    
			    
			    
			} catch (Exception e) {
			    throw new VDMException(e);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
		
		
	}
	
	@Test
	public void testCreateStructuredPath(){
		try {
			String structuredJson = vdm.createStructuredPath(null);
			//String structuredJson = vdm.createStructuredPath("/Mechanical/Machinery");
			
			LOGGER.debug("structuredJson : {}", structuredJson);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testCreateStructuredData(){
		
		
		try {
			String sourceJson = FileUtils.readFileToString(new File("./target/test-classes/structured_source.json"));
			
			JsonNode jsonNode = JSONUtil.readTree(sourceJson);
			
			String structuredJson = vdm.createStructuredData(jsonNode);
			
			LOGGER.debug("structuredJson : {}", structuredJson);
			
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}
	
	@Test
	public void testValidate(){
		
		VesselDataModel vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream(TEST_VCD_FILE));
		
		try {
			String sysName = "ACONIS";
			String prePath = "MainEngine/CommonSensor.RPM.Response";
			
			VDMNode vdmNode = vdm.getVDMNode(sysName, prePath + ".val");
			LOGGER.debug("vdmNode : {}", vdmNode);
			
			/*
			 * check doubt validation
			 */
			String result = vdm.validate(vdmNode, "1.24");
			LOGGER.debug("result: {}", result);
			
			assertEquals(DataValidator.VALID_RESULT_DOUBT, result);
			
			/*
			 * caching min, max
			 */
			VDMNode minNode = vdm.getVDMNode(sysName, prePath + ".rangeCfg.min");
			vdm.validate(minNode, "1.00");
			
			VDMNode maxNode = vdm.getVDMNode(sysName, prePath + ".rangeCfg.max");
			vdm.validate(maxNode, "2.00");
			
			/*
			 * check true validation
			 */
			result = vdm.validate(vdmNode, "1.24");
			LOGGER.debug("result: {}", result);
			
			assertEquals(DataValidator.VALID_RESULT_TRUE, result);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}
	
	@Test
	public void testCreateEnumMap(){
		
		VesselDataModel vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream("/VCD_for_ACONIS_ME_ver0.5.vcd"));
		System.out.println(vdm.getEnumMap());
	}
	
	@Test
	public void testXpath(){
		
		InputStream is = VesselDataModel.class.getResourceAsStream(TEST_VCD_FILE);
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
		    builder = builderFactory.newDocumentBuilder();
		    
		    Document xmlDocument = builder.parse(is);
		    
		    
		    Node node = XMLUtil.getNode(xmlDocument, "//DOType[@id='VDR/Order_1']/*[@name='Response']");
		    
		    assertNotNull(node);
		    
		    LOGGER.debug("xpath result: {}", node.getNodeName());
		    LOGGER.debug("xpath result: {}", XMLUtil.nodeToString(node));
		    
		} catch (Exception e) {
		    e.printStackTrace();
		    fail(e.toString());
		}
	}
	
	@Test
	public void testGetDA_SDONode(){
		
		String vdmpath = "MainEngine/CommonSensor.RPM.Response";
		VDMPath vdmPath = new VDMPath("ACONIS", vdmpath);
		
		try {
			int curDepth = 4;
			String dOTypeId = "VDR/Order_1";
			String name = vdmPath.getPath(curDepth);// name is Response
			
			Node node = vdm.getDA_SDONode(vdmPath, dOTypeId, name, curDepth);
			
			assertEquals("SDO", node.getNodeName());
			
			LOGGER.debug("result: {}", XMLUtil.nodeToString(node));
			
			
			vdmPath = new VDMPath("ACONIS", vdmpath + ".rangeCfg");
			node = vdm.getDA_SDONode(vdmPath, dOTypeId, name, curDepth);
			
			assertEquals("DA", node.getNodeName());
			
			LOGGER.debug("result: {}", XMLUtil.nodeToString(node));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}

}
