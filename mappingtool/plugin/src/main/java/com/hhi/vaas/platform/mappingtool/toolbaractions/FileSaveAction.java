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

public class FileSaveAction extends Action implements IWorkbenchAction {
	private static final String ID = "com.hhi.vaas.platform.mappingtool.filesave";
	
	private static final Logger LOGGER = Logger.getLogger(FileSaveAction.class);
	
	private final IWorkbenchWindow window;
	private IFileSaveActionCallback actionCallback;
	
	private String [] fileFilters = {"*.*"}; 

	public FileSaveAction(IFileSaveActionCallback callback) {
		setId(ID);
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		actionCallback = callback;
		
		setText("&Save");
		setToolTipText("File Save");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, IImageKeys.FILE_SAVE));
	}
	
	public FileSaveAction(IWorkbenchWindow window, IFileSaveActionCallback callback) {
		// TODO Auto-generated constructor stub
		setId(ID);
		
		this.window = window;
		actionCallback = callback;
		
		setText("&Save");
		setToolTipText("File Save");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, IImageKeys.FILE_SAVE));
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
		String result;
		
		FileDialog fd = new FileDialog(sh, SWT.SAVE);
		fd.setFilterExtensions(fileFilters);
		result = fd.open();
		if (result != null) {
			String filePath = fd.getFilterPath();
			String fileName = fd.getFileName();
			String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
			String fileFullPath = filePath + "/" + fileName;

			LOGGER.debug("Selected file = " + fileFullPath);
			
			try {
				if (actionCallback.doPrecheckSaveCondition()) {
					actionCallback.doFileSaveAction(fileFullPath);	
				}
			} catch (Exception e) {
				LOGGER.debug("FileSaveError");
			}
		}
	}
}
//end of FileLoadAction.java