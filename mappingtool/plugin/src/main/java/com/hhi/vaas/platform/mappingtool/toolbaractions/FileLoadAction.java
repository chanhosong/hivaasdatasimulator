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

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.hhi.vaas.platform.mappingtool.Application;


/**
 * @author hsbae
 * 
 */
public class FileLoadAction extends Action implements IWorkbenchAction {
	private static final String ID = "com.hhi.vaas.platform.mappingtool.fileload";
	
	private static final Logger LOGGER = Logger.getLogger(FileLoadAction.class);
	
	private final IWorkbenchWindow window;
	private IFileLoadActionCallback actionCallback;
	
	private String [] fileFilters = {"*.*"}; 

	public FileLoadAction(IFileLoadActionCallback callback) {
		setId(ID);
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		actionCallback = callback;
		
		setText("&Load");
		setToolTipText("File Load");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, IImageKeys.FILE_LOAD));
	}
	
	public FileLoadAction(IWorkbenchWindow window, IFileLoadActionCallback callback) {
		// TODO Auto-generated constructor stub
		setId(ID);
		
		this.window = window;
		actionCallback = callback;
		
		setText("&Load");
		setToolTipText("File Load");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, IImageKeys.FILE_LOAD));
	}
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	
	public void setFileFilters(String[] fileFilters) {
		this.fileFilters = fileFilters;
	}
	
	public void run() {
		
		Shell sh = window.getShell();
		
		if (actionCallback.doPrecheckLoadCondition()) {

			FileDialog fd = new FileDialog(sh, SWT.OPEN);
			fd.setFilterExtensions(fileFilters);
			
			if (fd.open() != null) {
				
				
				String filePath = fd.getFilterPath();
				String fileName = fd.getFileName();
				String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
				String fileFullPath = filePath + "/" + fileName;
		
				LOGGER.debug("Selected file = " + fileFullPath);
				
				try {
					actionCallback.doFileLoadAction(fileFullPath);	
				} catch (Exception e) {
					
				}
			}
		}
	}

}
//end of FileLoadAction.java