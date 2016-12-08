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
 * hsbae			2015. 4. 17.		First Draft.
 */

package com.hhi.vaas.platform.mappingtool.toolbaractions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.hhi.vaas.platform.mappingtool.Application;


/**
 * @author hsbae
 * 
 */
public class ItemInsertAction extends Action implements IWorkbenchAction {
	private static final String ID = "com.hhi.vaas.platform.mappingtool.iteminsert";
	private final IWorkbenchWindow window;
	private IItemInsertActionCallback actionCallback;
	
	public ItemInsertAction(IItemInsertActionCallback callback) {
		setId(ID);
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		actionCallback = callback;
		
		setText("&Insert");
		setToolTipText("Item Insert");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, IImageKeys.ITEM_INSERT));
		
	}
	
	public ItemInsertAction(IWorkbenchWindow window, IItemInsertActionCallback callback) {
		// TODO Auto-generated constructor stub
		setId(ID);
		
		this.window = window;
		actionCallback = callback;
		
		setText("&Insert");
		setToolTipText("Item insert");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, IImageKeys.ITEM_INSERT));
	}
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	
	
	public void run() {
		
		//Shell sh = window.getShell();
		
		try {
			actionCallback.doItemInsertAction();
		} catch (Exception e) {
			
		}
	
	}

}
//end of FileLoadAction.java