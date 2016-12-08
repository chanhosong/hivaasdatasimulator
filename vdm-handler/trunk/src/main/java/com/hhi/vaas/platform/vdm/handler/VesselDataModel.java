/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hhi.vaas.platform.middleware.common.util.XMLUtil;
import com.hhi.vaas.platform.vdm.handler.struct.ItemData;
import com.hhi.vaas.platform.vdm.handler.struct.StructureCreator;
import com.hhi.vaas.platform.vdm.handler.struct.VDMPathDatas;
import com.hhi.vaas.platform.vdm.handler.validation.DataValidator;


/**
 * VDM Conf. XML load & search bType.
 * 
 * @author BongJin Kwon
 *
 */
public class VesselDataModel {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VesselDataModel.class);
	
	private Document xmlDocument;
	
	private DataValidator validator;
	
	private Map<String, Map<String, String>> enumMap = new HashMap<String, Map<String, String>>();
	
	/**
	 * vdmpath : VDMNode map(cache).
	 */
	private Map<String, VDMNode> vdmNodeMap = new HashMap<String, VDMNode>();
	
	
	public VesselDataModel(Document xmlDocument) {
		this.xmlDocument = xmlDocument;
		this.validator = new DataValidator(this);
		
		createEnumMap();
	}
	
	/**
	 * return VDM version.
	 * @return
	 */
	public String getVersion() {
		
		Node hNode = XMLUtil.getNode(xmlDocument, "/VCL/Header");
		
		return XMLUtil.getAttribute(hNode, "version") + "." + XMLUtil.getAttribute(hNode, "revision");
	}
	
	public String getId() {
		return XMLUtil.getStringValue(xmlDocument, "/VCL/Header/@id");
	}
	
	public NodeList getSystems(){
		
		return XMLUtil.getNodeList(xmlDocument, "/VCL/System");
	}
	
	/**
	 * return Vessel/EquipmentGroup/Equipment path
	 * @param systemName
	 * @param vdmpath
	 * @return
	 */
	public String getEquipmentPath(String systemName, String vdmpath) {
		
		if( StringUtils.isEmpty(systemName)){
			throw new IllegalArgumentException("systemName must not be null.");
		}
		
		if( StringUtils.isEmpty(vdmpath)){
			throw new IllegalArgumentException("vdmpath must not be null.");
		}
		
		VDMPath vdmPath = new VDMPath(systemName, vdmpath);
		
		//Node node = XMLUtil.getNode(xmlDocument, "/VCL/Vessel/EquipmentGroup/Equipment/ConnectivityNode[@systemName='"+systemName+"' and @ldInst='"+vdmPath.getlDeviceInst()+"']");
		Node node = XMLUtil.getNode(xmlDocument, "//ConnectivityNode[@systemName='"+systemName+"' and @ldInst='"+vdmPath.getlDeviceInst()+"']");
		
		Node parent1 = node.getParentNode();
		
		LOGGER.trace("parent1: {}", parent1.getNodeName());
		boolean hasComp = "Component".equals(parent1.getNodeName());
		
		String path = XMLUtil.getAttribute(parent1, VDMConstants.NODE_ATTR_NAME);
		
		Node parent2 = parent1.getParentNode();
		
		path = XMLUtil.getAttribute(parent2, VDMConstants.NODE_ATTR_NAME) + VDMConstants.DELIMITER1 + path;
		
		if(hasComp){
			path = XMLUtil.getAttribute(parent2.getParentNode(), VDMConstants.NODE_ATTR_NAME) + VDMConstants.DELIMITER1 + path;
		}
		
		return path;
	}
	
	/**
	 * return VDMNode of vdmpth
	 * @param systemName
	 * @param vdmpath
	 * @return
	 */
	public VDMNode getVDMNode(String systemName, String vdmpath) {
		
		String key = systemName + "_" + vdmpath;
		VDMNode vdmNode = vdmNodeMap.get(key);
		
		if(vdmNode == null){
			VDMPath vdmPath = new VDMPath(systemName, vdmpath);
			vdmNode = getVDMNode(vdmPath);
			vdmNodeMap.put(key, vdmNode);
		}
		
		return vdmNode;
	}
	
	private VDMNode getVDMNode(VDMPath vdmPath) {
		
		Node node = null;
		
		try {
			
			// hsbae_150510 : depth == 0 then it's systemnode
			if(vdmPath.getDepth() == 0) {
				node = getSystemNode(vdmPath.getSystemName());
				return createVDMNode(vdmPath, node);
			}
			
			node = getLDeviceNode(vdmPath.getSystemName(), vdmPath.getlDeviceInst());
			
			if(vdmPath.getDepth() == 1 ) {
				return createVDMNode(vdmPath, node);
			}
			
			node = getLnNode(vdmPath.getLnPath(), node.getChildNodes());
			
			if (vdmPath.getDepth() == 2) {
				
				return createVDMNode(vdmPath, node);
			}
			
			String lnType = XMLUtil.getAttribute(node, VDMConstants.NODE_ATTR_LNTYPE);
			
			node = getDONode(lnType, vdmPath.getDoName());
			
			if (vdmPath.getDepth() == 3) {
				
				return createVDMNode(vdmPath, node);
			}
			
			String dOTypeId = XMLUtil.getAttribute(node, VDMConstants.NODE_ATTR_TYPE);
			
			
			try {
				
				if(vdmPath.getSdoName() != null){
					node = getSDONode(dOTypeId, vdmPath.getSdoName());
					
					if (vdmPath.getDepth() == 4) {
						
						return createVDMNode(vdmPath, node);
					}
					
					dOTypeId = XMLUtil.getAttribute(node, VDMConstants.NODE_ATTR_TYPE);
				}
				
				node = getDANode(dOTypeId, vdmPath.getDaName());
				
				if (vdmPath.getDepth() == 4 || (vdmPath.getSdoName() != null && vdmPath.getDepth() == 5)) {
					
					return createVDMNode(vdmPath, node);
				}
				
			} catch (RuntimeException e) {
				
				LOGGER.info(e.getMessage() + ", vdmpath : " + vdmPath.getVdmpath());
				
				vdmPath.setSdoName(vdmPath.getDaName());
				node = getSDONode(dOTypeId, vdmPath.getSdoName());
				
				if (vdmPath.getDepth() == 4) {
					
					return createVDMNode(vdmPath, node);
				}
				
				
				dOTypeId = XMLUtil.getAttribute(node, VDMConstants.NODE_ATTR_TYPE);
				vdmPath.setDaName(vdmPath.getBdaName());
				node = getDANode(dOTypeId, vdmPath.getDaName());
				
				
				if (vdmPath.getDepth() == 5) {
					
					return createVDMNode(vdmPath, node);
				}
			}
			
			
			String dATypeId = XMLUtil.getAttribute(node, VDMConstants.NODE_ATTR_TYPE);
			
			node = getBDANode(dATypeId, vdmPath.getBdaName());
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new VDMException(e);
		}
		
		return createVDMNode(vdmPath, node);
	}
	
	/**
	 * return child nodes of vdmpath
	 * @param systemName
	 * @param vdmpath
	 * @return
	 */
	public List<VDMNode> getChildNodes(String systemName, String vdmpath) {
		
		VDMNode vdmNode = getVDMNode(systemName, vdmpath);
		
		return vdmNode.getChildNodes();
	}
	
	
	/**
	 * data validation.
	 * @param map
	 * @return
	 * @deprecated do not use. replaced by {@link #validate(String, String, String)}
	 */
	@Deprecated 
	public boolean validate(JsonNode jsonNode) {
		boolean validate = true;
		
		/*
		 * test code. will be removed.
		 */
		if (jsonNode.get("flag") != null) {
			validate = false;
		}
		
		return validate;
	}
	
	/**
	 * data validation
	 * 
	 * @param vdmpath
	 * @param value
	 * @param bType
	 * @return
	 */
	public String validate(VDMNode vdmNode, String value) {
		String result = DataValidator.VALID_RESULT_NA;
		
		if (validator.isMinMaxValidationTarget(vdmNode)) {
			
			String bType = vdmNode.getAttribute("bType");
			result = validator.validateMinMax(vdmNode.getVdmpath(), value, bType);
			
		} else if (validator.isMinMaxVdmpath(vdmNode.getVdmpath())) {
			
			//save to cache
			validator.saveMinMax(vdmNode.getVdmpath(), value);
		}
		
		return result;
	}
	
	/**
	 * equipment raw data type validate.
	 * 
	 * @param vdmpath
	 * @param value equipment raw data
	 * @param bType of VCD
	 * @return
	 */
	public Object convertType(VDMNode vdmNode, String value) {
		
		String bType = vdmNode.getAttribute("bType");
		
		if (validator.isEnumType(bType)) {
			return validator.convertEnumTyp(vdmNode, value, this);
		}
		
		return validator.convertType(vdmNode.getVdmpath(), value, bType);
	}
	
	
	protected void createEnumMap(){
		NodeList enumTypes = XMLUtil.getNodeList(this.xmlDocument, "/VCL/DataTypeTemplates/EnumType");
		
		for (int i = 0; i < enumTypes.getLength(); i++) {
			Node enumType = enumTypes.item(i);
			
			if(enumType.getNodeType() == Node.TEXT_NODE){
				continue;
			}
			
			Map<String, String> enumValMap = new HashMap<String, String>();
			
			NodeList enumVals = enumType.getChildNodes();
			
			for (int j = 0; j < enumVals.getLength(); j++) {
				Node enumVal = enumVals.item(j);
				
				if(enumVal.getNodeType() == Node.TEXT_NODE){
					continue;
				}
				
				String ord = XMLUtil.getAttribute(enumVal, "ord");
				String enumText = enumVal.getTextContent();
				
				if (StringUtils.isEmpty(enumText)) {
					enumText = ord;
				}
				
				enumValMap.put(ord, enumText);
			}
			
			enumMap.put(XMLUtil.getAttribute(enumType, "id"), enumValMap);
			
		} 
	}
	
	public Map<String, String> getEnumMap(String enumId){
		
		return enumMap.get(enumId);
	}
	
	
	/**
	 * <pre>
	 * LN Tag 에서 lnType value 가져오기.
	 * 
	 * &lt;System ...>
     *    &lt;LDevice ...>
     *       &lt;LN lnType="VDR/GPS_1" lnClass="GPS" inst="1" prefix="" desc="">
	 * </pre>
	 *
	 * @param vdmPath
	 * @param lnNodes &lt;LDevce /> 의 하위 노드들
	 * @return
	 */
	private Node getLnNode(String lnPath, NodeList lnNodes){
		
		Node node = null;
		int nodeLength = lnNodes.getLength();
		
		for(int i = 0; i < nodeLength; i++){
			Node lnNode = lnNodes.item(i);
			
			if(lnNode.getNodeType() == Node.TEXT_NODE){
				continue;
			}
			
			if(lnPath.equals(VDMPath.getLNPath(lnNode))){
				node = lnNode;
				break;
			}
		}
		
		if(node == null){
			throw new VDMException("<LN/> not found. lnPath : "+lnPath);
		}
		
		return node;
	}
	
	/**
	 * create VDMNode
	 * @param vdmpath
	 * @param node
	 * @return
	 */
	private VDMNode createVDMNode(VDMPath vdmPath, Node node){
		return new VDMNode(vdmPath, node, this);
	}
	
	// hsbae_150510 : add getSystemNode function
	private Node getSystemNode(String systemName) {
		return XMLUtil.getNode(xmlDocument, "VCL/System[@name='"+systemName+"']");
	}
	
	private Node getLDeviceNode(String systemName, String lDeviceInst){
		return XMLUtil.getNode(xmlDocument, "/VCL/System[@name='"+systemName+"']/LDevice[@inst='"+lDeviceInst+"']");
	}
	
	private Node getDONode(String lnType, String doName){
		return XMLUtil.getNode(xmlDocument, "/VCL/DataTypeTemplates/LNodeType[@id='"+lnType+"']/DO[@name='"+doName+"']");
	}
	
	private Node getDANode(String dOTypeId, String daName){
		return XMLUtil.getNode(xmlDocument, "/VCL/DataTypeTemplates/DOType[@id='"+dOTypeId+"']/DA[@name='"+daName+"']");
	}
	
	private Node getSDONode(String dOTypeId, String sdoName){
		return XMLUtil.getNode(xmlDocument, "/VCL/DataTypeTemplates/DOType[@id='"+dOTypeId+"']/SDO[@name='"+sdoName+"']");
	}
	
	protected Node getDA_SDONode(VDMPath vdmPath, String dOTypeId, String name, int curDepth){
		
		Node node = XMLUtil.getNode(xmlDocument, "/VCL/DataTypeTemplates/DOType[@id='"+dOTypeId+"']/*[@name='"+ name +"']");
		
		if(vdmPath.getDepth() == curDepth || "DA".equals(node.getNodeName())) {
			return node;
		
		} else {
			// SDO node & vdmPath.getDepth() > curDepth
			dOTypeId = XMLUtil.getAttribute(node, VDMConstants.NODE_ATTR_TYPE);
			return getDA_SDONode(vdmPath, dOTypeId, vdmPath.getPath(curDepth+1), curDepth+1);
		}
	}
	
	private Node getBDANode(String dATypeId, String bdaName){
		return XMLUtil.getNode(xmlDocument, "/VCL/DataTypeTemplates/DAType[@id='"+dATypeId+"']/BDA[@name='"+bdaName+"']");
	}
	
	
	/**
	 * return child nodes of System node
	 * @param systemName
	 * @return LDevice nodes
	 */
	public NodeList getLDevices(String systemName) {
		
		Node node = XMLUtil.getNode(xmlDocument, "/VCL/System[@name='"+systemName+"']");
		
		return node.getChildNodes();
	}
	
	/**
	 * return child nodes of LN node
	 * @param lnType
	 * @return DO nodes
	 */
	protected NodeList getDONodes(String lnType) {
		Node node = XMLUtil.getNode(xmlDocument, "/VCL/DataTypeTemplates/LNodeType[@id='"+lnType+"']");
		
		return node.getChildNodes();
	}
	
	/**
	 * return child nodes of DO node
	 * @param dOTypeId
	 * @return DA or SDO nodes
	 */
	protected NodeList getDASDONodes(String dOTypeId) {
		Node node = XMLUtil.getNode(xmlDocument, "/VCL/DataTypeTemplates/DOType[@id='"+dOTypeId+"']");
		
		return node.getChildNodes();
	}
	
	/**
	 * return child nodes of DA node
	 * @param dATypeId
	 * @return
	 */
	protected NodeList getBDANodes(String dATypeId) {
		Node node = XMLUtil.getNode(xmlDocument, "/VCL/DataTypeTemplates/DAType[@id='"+dATypeId+"']");
		
		return node.getChildNodes();
	}
	
	/**
	 * create structured full path (except data)
	 * @param filterPath must be equipment path of vdm full path
	 * @return
	 */
	public String createStructuredPath(String filterPath) throws Exception{
		
		ObjectMapper om = new ObjectMapper();
		//om.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		
		NodeList nodeList = XMLUtil.getNode(xmlDocument, "/VCL/Vessel").getChildNodes();
		
		ObjectNode rootNode = om.createObjectNode();
		ObjectNode child = om.createObjectNode();
		rootNode.put(getId() + "-" + getVersion(), child);
		
		addChild(child, nodeList, om, "", filterPath);
		
		//return om.writeValueAsString(rootNode);
		return rootNode.toString();
	}
	
	private void addChild(ObjectNode objNode, NodeList nodeList, ObjectMapper om, String prePath, String filterPath){
		
		String systemName = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String thisPath = null;
			String fullPath = null;
			
			if (node.getNodeType() == Node.TEXT_NODE) {
				continue;
			}
			
			ObjectNode oChild = om.createObjectNode();
			
			if (VDMConstants.NODE_NAME_CONNECTIVITY.equals(node.getNodeName())) {
				
				if (systemName == null) {
					systemName = XMLUtil.getAttribute(node, "systemName");
					
					/*
					 * skip systemName node.
					ObjectNode sChild = om.createObjectNode();
					objNode.put(systemName, sChild);
					objNode = sChild;
					*/
				}
				thisPath = XMLUtil.getAttribute(node, "ldInst");
				fullPath = prePath + VDMConstants.DELIMITER1 + thisPath;
				
				if(isMatch(fullPath, filterPath)){
					objNode.put(thisPath, oChild);
					
					List<VDMNode> children = null;
					try {
						children = getChildNodes(systemName, thisPath);
						
					} catch (Exception e) {
						LOGGER.warn(e.toString());
						LOGGER.warn("skip adding child of {}/{}", systemName, thisPath);
					}
					
					if(children != null){
						addChild(oChild, children, om);
					}
				}
				
				
			} else {
				thisPath = XMLUtil.getAttribute(node, VDMConstants.NODE_ATTR_NAME);
				fullPath = prePath + VDMConstants.DELIMITER1 + thisPath;
				
				if(isMatch(fullPath, filterPath)){
					objNode.put(thisPath, oChild);
				}
			}
			
			LOGGER.debug("fullPath: {}", fullPath);
			if(isMatch(fullPath, filterPath) && node.hasChildNodes()){
				addChild(oChild, node.getChildNodes(), om, fullPath, filterPath);
			}
			
		}
	}
	
	private boolean isMatch(String fullPath, String filterPath){
		return filterPath == null || filterPath.startsWith(fullPath) || fullPath.startsWith(filterPath);
	}
	
	private void addChild(ObjectNode objNode, List<VDMNode> children, ObjectMapper om){
		
		for (VDMNode vdmNode : children) {
			
			if (vdmNode.isLeaf()) {
				objNode.put(vdmNode.getLastPath(), om.createArrayNode());
				
			} else {
				
				ObjectNode oChild = om.createObjectNode();
				objNode.put(vdmNode.getLastPath(), oChild);
				addChild(oChild, vdmNode.getChildNodes(), om);
			}
		}
	}
	
	
	/**
	 * create structured json data (full vdm path)
	 * 
	 * @param jsonNode
	 * @return structured json data (full vdm path)
	 * @throws IOException
	 */
	public String createStructuredData(JsonNode jsonNode) throws IOException{
		if(jsonNode.isArray() == false){
			throw new VDMException("json is not array. "+ jsonNode.toString());
		}
		
		StructureCreator creator = new StructureCreator();
		VDMPathDatas vdmPathDatas = new VDMPathDatas();
		
		for (int i = 0; i < jsonNode.size(); i++) {
			
			ObjectNode objNode = (ObjectNode)jsonNode.get(i);
			
			//String vdmFullPath = getVDMFullPath(objNode);
			//String equipPath = getEquipmentPath(systemName, vdmpath);
			String vdmFullPath = objNode.get("vdmpath").asText();
			
			JsonNode timeNode = objNode.get("timestamp2");
			if(timeNode == null){
				timeNode = objNode.get("receivedTime");
			}
			long timestamp = timeNode.asLong();
			String value = objNode.get("value").asText();
			String valid = objNode.get("valid").asText();
			int iValid = 0;
			if ("true".compareTo(valid) == 0){
				iValid = 1;
			}
			
			vdmPathDatas.addData(vdmFullPath, new ItemData(timestamp, value, iValid));
			
		}
		
		JsonNode structured = creator.create(vdmPathDatas);
		
		return structured.toString();
		
	}
	
	private String getVDMFullPath(ObjectNode objNode){
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 1; i < 11; i++) {
			
			String vdmNode = objNode.get("vdmpath"+i).asText();
			
			if(StringUtils.isBlank(vdmNode)){
				continue;
			}
			
			if(i > 1){
				sb.append("/");
			}
			
			sb.append(vdmNode);
		}
		
		return sb.toString();
	}
	
	
	
	protected Map<String, Map<String, String>> getEnumMap() {
		return enumMap;
	}

	protected Map<String, VDMNode> getVdmNodeMap() {
		return vdmNodeMap;
	}

	public DataValidator getValidator() {
		return validator;
	}

	public static String getVDMPath(String vdmFullPath) {
		
		if (StringUtils.split(vdmFullPath, "/").length > 2) {
			
			int pos = vdmFullPath.lastIndexOf(VDMConstants.DELIMITER1);
			pos = vdmFullPath.lastIndexOf(VDMConstants.DELIMITER1, pos-1);
			
			return vdmFullPath.substring(pos+1);
		} else {
			
			// vdmFullPath is vdmpath.
			
			return vdmFullPath;
		}
		
		
	}
}
