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
 * hsbae			2015. 4. 10.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.hhi.vaas.platform.mappingtool.eqdmodel.EquipmentDataModel;

public class EquipmentDataTreeContentProvider implements ITreeContentProvider {

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	
	@Override
	public void dispose() {
		
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}
	
	@Override 
	public Object[] getChildren(Object parentElement) {
		return ((EquipmentDataModel) parentElement).child.toArray();
	}
	
	@Override
	public Object getParent(Object element) {
		if (element == null) {
			return null;
		}
		return ((EquipmentDataModel) element).parent;
	}
	
	@Override
	public boolean hasChildren(Object element) {
		return ((EquipmentDataModel) element).child.size() > 0;
	}
	
}

//end of EquipmentDataTreeContentProvider.java