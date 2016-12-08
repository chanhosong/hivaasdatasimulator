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
 * hsbae			2015. 5. 12.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.ui;


import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

import com.hhi.vaas.platform.vdm.handler.VDMNode;


public class VDMDragListener implements DragSourceListener {

	private final TreeViewer treeViewer;
	
	private static final Logger LOGGER = Logger.getLogger(VDMDragListener.class);
	
	public VDMDragListener(TreeViewer srcViewer) {
		treeViewer = srcViewer;
		
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		// TODO Auto-generated method stub
		LOGGER.debug("Start Drag : " + event.data);
		
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		// TODO Auto-generated method stub
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		VDMNode selectedNode = (VDMNode) selection.getFirstElement();
		
		try {
			/*
			if(selectedNode.isLeaf()) {
				if(TextTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = selectedNode.getVdmpath();
				}
			}
			*/
			
			if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
				// leaf node checking
				if (selectedNode.isLeaf()) {
					event.data = selectedNode.getVdmpath();
					//LOGGER.debug("Drag text : " + event.data);
				} else {
					LOGGER.debug("[VDM] WRN : this item is not leaf node");
					event.data = "";
				}
			}
			
		} catch (SWTException se) {
			LOGGER.debug("invalid data");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		// TODO Auto-generated method stub
		LOGGER.debug("Finish Drag : " + event.data);
	}
	
	
	

}
//end of VDMDragListener.java