package com.hhi.vaas.platform.vdm.handler;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class VesselDataModelChildNodeTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VesselDataModelChildNodeTest.class);
	
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
	public void testGetChildNode_depth0() {
		VDMNode vdmNode = vdm.getVDMNode("VDR", null);
		
		List<VDMNode> childList = vdmNode.getChildNodes();
		System.out.println("===>");
		System.out.println(childList);
		System.out.println("<===");
	}
	
	@Test
	public void testGetChildNode_depth1() {
		VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS");
		
		List<VDMNode> childList = vdmNode.getChildNodes();
		System.out.println("===>");
		System.out.println(childList);
		System.out.println("<===");
	}
	
	@Test
	public void testGetChildNode_depth2() {
		VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1");
		
		List<VDMNode> childList = vdmNode.getChildNodes();
		System.out.println("===>");
		System.out.println(childList);
		System.out.println("<===");
	}
	
	@Test
	public void testGetChildNode_depth3() {
		VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1.Pos");
		
		List<VDMNode> childList = vdmNode.getChildNodes();
		System.out.println("===>");
		System.out.println(childList);
		System.out.println("<===");
	}
	
	@Test
	public void testGetChildNode_depth4() {
		VDMNode vdmNode = vdm.getVDMNode("VDR", "GPS/GPS1.Pos.latitude");
		
		List<VDMNode> childList = vdmNode.getChildNodes();
		System.out.println("===>");
		System.out.println(childList);
		System.out.println("<===");
	}
}
