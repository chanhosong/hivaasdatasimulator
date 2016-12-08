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

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.hhi.vaas.platform.vdm.handler.VDMNode;

public class VdmTreeContentProvider implements ITreeContentProvider {

	private static final Logger LOGGER = Logger.getLogger(VdmTreeContentProvider.class);
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return getChildren(inputElement);
		
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		
		if (parentElement instanceof VDMNode) {
			Object[] childArray = ((VDMNode) parentElement).getChildNodes().toArray();
			if (childArray.length > 0) {
				return childArray;
			} else {
				return null;
			}
		} else {
			LOGGER.debug("ERR : invalid tree element");
			return null;
		}
		
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		if (element instanceof VDMNode) {
			return ((VDMNode) element).getParentNode();
		}
		
		return null;
	}

	@Override
	public boolean hasChildren(Object parentElement) {
		// TODO Auto-generated method stub
		if (parentElement instanceof VDMNode) {
			try {
				List<VDMNode> list = ((VDMNode) parentElement).getChildNodes();
				if (list != null) {
					return list.size() > 0;
				} else {
					return false;
				}
				 
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	/*
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	*/
	
}
//end of VDMTreeContentProvider.java