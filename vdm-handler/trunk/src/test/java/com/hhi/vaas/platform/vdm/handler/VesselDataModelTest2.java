package com.hhi.vaas.platform.vdm.handler;

import com.hhi.vaas.platform.middleware.common.util.XMLUtil;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.*;

public class VesselDataModelTest2 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VesselDataModelTest2.class);
	
	private static VesselDataModel vdm;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		vdm = VDMLoader.load(VesselDataModel.class.getResourceAsStream("/VCD_for_VDR_ver0.3.vcd"));
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
	public void testGetVDMNode_System() {
		/*
		 * test VesselDataModel.getVDMNode.
		 */
		try{
			VDMNode vdmNode = vdm.getVDMNode("VDR", "");
			
			LOGGER.debug("vdmNode: {}", vdmNode);
			
			assertNotNull(vdmNode);
			
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void testGetVDMNode_depth0() {
		/*
		 * test VesselDataModel.getVDMNode.
		 */
		try{
			VDMNode vdmNode = vdm.getVDMNode("VDR", null);
			
			LOGGER.debug("vdmNode: {}", vdmNode);
			
			assertNotNull(vdmNode);
			
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testGetVDMNode_depth1() {
		/*
		 * test VesselDataModel.getVDMNode.
		 */
		try{
			VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS");
			
			LOGGER.debug("vdmNode: {}", vdmNode);
			
			assertNotNull(vdmNode);
			
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testGetVDMNode_depth2() {
		/*
		 * test VesselDataModel.getVDMNode.
		 */
		try{
			VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1");
			
			LOGGER.debug("vdmNode: {}", vdmNode);
			
			assertNotNull(vdmNode);
			
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	@Test
	public void testGetVDMNode_depth3() {
		/*
		 * test VesselDataModel.getVDMNode.
		 */
		try{
			VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1.Time");
			
			LOGGER.debug("vdmNode: {}", vdmNode);
			
			assertNotNull(vdmNode);
			
			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	
}
