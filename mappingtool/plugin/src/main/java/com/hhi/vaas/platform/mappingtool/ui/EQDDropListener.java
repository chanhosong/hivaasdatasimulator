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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.hhi.vaas.platform.mappingtool.eqdmodel.EquipmentDataModel;
import com.hhi.vaas.platform.mappingtool.eqdmodel.TemplateItem;
import com.hhi.vaas.platform.mappingtool.mapdatamodel.MapDataItem;

public class EQDDropListener extends ViewerDropAdapter {

	private final TreeViewer viewer;
	private TreeItem item;
	
	private static final Logger LOGGER = Logger.getLogger(EQDDropListener.class);
	
	
	public EQDDropListener(Viewer viewer) {
		super(viewer);
		this.viewer = (TreeViewer) viewer;
	}
	
	@Override
	public void drop(DropTargetEvent event) {
		int location = this.determineLocation(event);
		LOGGER.debug("Target : " + this.determineTarget(event));
		
		if (location == 3) {
			item = (TreeItem) event.item;
			super.drop(event);
			
		}
	}
	
	@Override
	public boolean performDrop(Object data) {
		// TODO Auto-generated method stub
		//ContentProviderTree.INSTANCE.getModel().add(data.toString());
		//viewer.setInput(ContentProviderTree.INSTANCE.getModel());
		
		boolean bUseTemplateMapping = true; // 추후 옵션으로 뺄 예정
		
		String link = data.toString().trim();
		EquipmentDataModel target = (EquipmentDataModel) getCurrentTarget();
		if (target != null && !"".equals(link)) {
			
			// add mapping rule into the mappingtableview
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewPart view = page.findView(MappingTableView.ID);
				MappingTableView mView = (MappingTableView) view;
				
				IViewPart view2 = page.findView(EquipmentDataView.ID);
				EquipmentDataView eqdView = (EquipmentDataView) view2;
				
				MapDataItem newItem = convEQDtoMap(target, link);
				
				if (newItem != null) {
					mView.handleAppend(newItem);
					
					LOGGER.debug("New Link : " + data.toString());
					target.setDestination("VDMPath:" + data.toString());
					
					// color
					Display display = Display.getCurrent();
					item.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));

					// template mapping 
					if (bUseTemplateMapping == true) {
						
						String linkSuffix = link.substring(link.lastIndexOf("."));
						
						LOGGER.debug("drop linkSuffix = " + linkSuffix);
	
						boolean hasTemplate = eqdView.templateList.containsKey(linkSuffix);
						
						if (hasTemplate == true) {
							TemplateItem templateItem = eqdView.templateList.get(linkSuffix);
							List<String> eqdKeyList = new ArrayList(templateItem.getKeyList());
									
							String baseKey = target.getName();
							String basePath = link.substring(0, link.lastIndexOf("."));
							
							LOGGER.debug("baseKey = " + baseKey);
							LOGGER.debug("basePath = " + basePath);
							
							EquipmentDataModel subTarget = null;
								
							for (int i = 0; i < eqdKeyList.size(); i++) {
								String subKey = baseKey + eqdKeyList.get(i);
								String subPath = basePath + templateItem.getVdmSuffix(eqdKeyList.get(i));
									
								LOGGER.trace("subKey = " + subKey);
								LOGGER.trace("subPath = " + subPath);

								subTarget = eqdView.searchEQD(target.parent, subKey);
								if (subTarget != null) {
									LOGGER.trace("subKey found");
									
									newItem = convEQDtoMap(subTarget, subPath);
									
									if (newItem != null) {
										mView.handleAppend(newItem);
										
										LOGGER.debug("New Link : " + subPath);
										target.setDestination("VDMPath:" + subPath);
										
										// color
										//display = Display.getCurrent();
										//item.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
									}
								}
							}
							
							mView.syncWithEQDView();
							mView.syncWithVDMView();
						} 
					} 
				}
				
				viewer.refresh();
				return true;
			} catch (Exception e) {
				LOGGER.debug("INF : Wrong position");
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		// TODO Auto-generated method stub
		return true;
	}
	
	private MapDataItem convEQDtoMap(EquipmentDataModel target, String destLink) {
		
		// skip root node
		if (target == null || target.parent == null) { 
			return null;
		} 
		
		EquipmentDataModel eqdRoot = (EquipmentDataModel) viewer.getInput();
		
		EquipmentDataModel eqdProtocol = eqdRoot.child.get(0);
		
		// skip protocol node
		if (eqdProtocol == target) {
			return null;
		}
		
		EquipmentDataModel eqdParent = target.parent;
		
		// skip group node
		if (eqdProtocol == eqdParent) {
			return null;
		}
		
		
		MapDataItem mapItem;
		
		if (eqdProtocol.getName().equals("NMEA")) {
			String protocol = eqdProtocol.getName();
			String function = "";
			String from = target.parent.getName();
			String param1 = target.getName();
			String param2 = "";
			String to = destLink;
			
			mapItem = new MapDataItem(protocol, function, from, param1, param2, to);
		} else if (eqdProtocol.getName().equals("KV")) {
			String protocol = eqdProtocol.getName();
			String function = "";
			String from = target.getName();
			String param1 = "";
			String param2 = "";
			String to = destLink;
			
			mapItem = new MapDataItem(protocol, function, from, param1, param2, to);
		} else {
			mapItem = null;
		}
		
		return mapItem;
	}

}
//end of EQDDropListener.java