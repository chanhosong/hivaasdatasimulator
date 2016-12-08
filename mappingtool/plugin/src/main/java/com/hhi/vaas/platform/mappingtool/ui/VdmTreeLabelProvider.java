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
 * hsbae			2015. 5. 7.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.hhi.vaas.platform.vdm.handler.VDMConstants;
import com.hhi.vaas.platform.vdm.handler.VDMNode;

public class VdmTreeLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(Object element) {
		// TODO Auto-generated method stub
		if (element instanceof VDMNode) {
			VDMNode vdmNode = (VDMNode) element;
			String nodeName = vdmNode.getNodeName(); 
			if (VDMConstants.NODE_NAME_LDEVICE.equals(nodeName)) {
				return vdmNode.getAttribute("inst");
			} else if (VDMConstants.NODE_NAME_LN.equals(nodeName)) {
				return vdmNode.getAttribute("lnClass") + "_" + vdmNode.getAttribute("inst");
			} else if (VDMConstants.NODE_NAME_DO.equals(nodeName) || VDMConstants.NODE_NAME_SDO.equals(nodeName)) {
				return vdmNode.getAttribute("name");
			} else if (VDMConstants.NODE_NAME_DA.equals(nodeName) || VDMConstants.NODE_NAME_BDA.equals(nodeName)) {
				return vdmNode.getAttribute("name");
			} else {
				return element.toString();
			}
				
		}
		
		
		return null;
	}

}
//end of VdmTreeLabelProvider.java