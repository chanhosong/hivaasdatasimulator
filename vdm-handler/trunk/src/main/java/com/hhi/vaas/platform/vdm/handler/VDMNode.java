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
 * Bongjin Kwon	2015. 4. 8.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hhi.vaas.platform.middleware.common.util.XMLUtil;

/**
 * Node of vdmpath
 * 
 * @author BongJin Kwon
 *
 */
public class VDMNode {
	
	private Node node;
	private VDMPath vdmPath;
	private String nodeName;		// to display nodename
	private String nodeDesc;		// to display node description
	
	private VesselDataModel vdm;
	private VDMNode	parentNode;		// hsbae_150510
	private List<VDMNode> childNodes;
	
	public VDMNode(VDMPath vdmPath, Node node, VesselDataModel vdm) {
		this.parentNode = null;
		this.vdmPath = vdmPath;
		this.node = node;
		
		this.nodeName = node.getNodeName();
		this.vdm = vdm;
	}
	
	public VDMNode(VDMNode parent, VDMPath vdmPath, Node node, VesselDataModel vdm) {
		this.parentNode = parent;
		this.vdmPath = vdmPath;
		this.node = node;
		
		this.nodeName = node.getNodeName();
		this.vdm = vdm;
	}

	public Node getNode() {
		return node;
	}
	
	public String getSystemName() {
		if (vdmPath == null) {
			return null;
		}
		return vdmPath.getSystemName();
	}

	/**
	 * return vdmpath of this node.
	 * @return vdmpath of this node.
	 */
	public String getVdmpath() {
		
		if (vdmPath == null){
			return null;
		}
		return vdmPath.getVdmpath();
	}
	
	/**
	 * return attribute value
	 * @param attrName
	 * @return attribute value
	 */
	public String getAttribute(String attrName){
		
		
		return XMLUtil.getAttribute(node, attrName);
	}
	
	/**
	 * The value of this node
	 * @return
	 */
	public String getNodeValue(){
		return node.getNodeValue();
	}
	
	public String getNodeName() {
		return nodeName;
	}

	public VDMNode getParentNode() {
		return parentNode;
	}
	
	public List<VDMNode> getChildNodes(){
		
		
		// hsbae_150510 : system node 추가
		if(VDMConstants.NODE_NAME_SYSTEM.equals(this.nodeName)){
			childNodes = createChildNodes(node.getChildNodes());
			
		}else if(VDMConstants.NODE_NAME_LDEVICE.equals(this.nodeName)){
			childNodes = createChildNodes(node.getChildNodes());
			
		}else if(VDMConstants.NODE_NAME_LN.equals(this.nodeName)){
			
			String lnType = XMLUtil.getAttribute(this.node, VDMConstants.NODE_ATTR_LNTYPE);
			
			childNodes = createChildNodes(vdm.getDONodes(lnType));
			
		}else if(VDMConstants.NODE_NAME_DO.equals(this.nodeName) || VDMConstants.NODE_NAME_SDO.equals(this.nodeName)){
			
			String dOTypeId = XMLUtil.getAttribute(this.node, VDMConstants.NODE_ATTR_TYPE);
			
			childNodes = createChildNodes(vdm.getDASDONodes(dOTypeId));
			
		}else if(VDMConstants.NODE_NAME_DA.equals(this.nodeName)){
			
			String dATypeId = XMLUtil.getAttribute(node, VDMConstants.NODE_ATTR_TYPE);
			
			childNodes = createChildNodes(vdm.getBDANodes(dATypeId));
			
		} 
		
		return childNodes;
	}
	
	public boolean isLeaf(){
		return VDMConstants.NODE_NAME_BDA.equals(this.nodeName) || isLeafDA();
	}
	
	private boolean isLeafDA(){
		
		if (VDMConstants.NODE_NAME_DA.equals(this.nodeName)) {
			
			String bType = XMLUtil.getAttribute(node, VDMConstants.NODE_ATTR_BTYPE);
			
			return VDMConstants.NODE_BTYPE_STRUCT.equals(bType) == false;
		}
		
		return false; 
	}
	
	private List<VDMNode> createChildNodes(NodeList nodeList){
		
		int nodeLen = nodeList.getLength();
		
		List<VDMNode> childNodes = new ArrayList<VDMNode>();
		
		for (int i = 0; i < nodeLen; i++) {
			Node childNode = nodeList.item(i);
			
			if(childNode.getNodeType() == Node.TEXT_NODE){
				continue;
			}
			
			// hsbae_150510 : when vdmpath==null it's system node
			if(this.vdmPath.getVdmpath() == null) {
				String childVdmpath = "" + XMLUtil.getAttribute(childNode, "inst");
				VDMPath childVdmPath = new VDMPath(this.vdmPath.getSystemName(), childVdmpath);
				
				childNodes.add( new VDMNode(this, childVdmPath, childNode, vdm) );
			}
			else
			{
				String childVdmpath = this.vdmPath.getVdmpath() + this.vdmPath.getNextDelimiter() + this.vdmPath.getNextPath(childNode);
				VDMPath childVdmPath = new VDMPath(this.vdmPath.getSystemName(), childVdmpath);
				
				childNodes.add( new VDMNode(this, childVdmPath, childNode, vdm) );
			}
		}
		
		return childNodes;
	}
	
	public String getLastPath(){
		if (vdmPath == null) {
			return null;
		}
		return vdmPath.getLastPath();
	}
	
	@Override
	public String toString(){
		
		return XMLUtil.nodeToString(node);
	}

}
//end of VDMNode.java