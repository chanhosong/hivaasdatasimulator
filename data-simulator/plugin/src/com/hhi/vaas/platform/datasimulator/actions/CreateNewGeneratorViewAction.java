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
 * sehwan			2015. 6. 18.				First Draft.
 */

package com.hhi.vaas.platform.datasimulator.actions;

import java.net.NetworkInterface;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.hhi.vaas.platform.datasimulator.IDefineID;
import com.hhi.vaas.platform.datasimulator.common.CommonMethodes;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.common.ViewMapSingleton;
import com.hhi.vaas.platform.datasimulator.ui.GeneratorView;

public class CreateNewGeneratorViewAction extends Action {
	private final IWorkbenchWindow window;
	private ViewMapSingleton map;	
	private final String viewId;

	private LoggerSingletone LOGGER = LoggerSingletone.getInstance();

	public CreateNewGeneratorViewAction(IWorkbenchWindow window, String viewId) {
		this.window = window;
		this.viewId = viewId;
		map = ViewMapSingleton.getInstance();
	}

	public void run() {		
		if(map.getGeneratorInstanceMax() < 8) {
			ActionsExecution actionExecution = new ActionsExecution();
			boolean settingOk;
			
			settingOk = actionExecution.setting(window, "", "", "", "", -1, -1, null);
			
			if (settingOk) {
				try {
					window.getActivePage().showView(viewId, Integer.toString(map.getNextGeneratorInstanceCount()),
							IWorkbenchPage.VIEW_ACTIVATE);
					map.getNextGeneratorInstanceMax();

					GeneratorView view = map.getMap().get(map.getGeneratorInstanceCount());

					view.setSettingOk(settingOk);
					view.setViewName(actionExecution.getStrViewTitle());
					view.getActionExecution().setIpAdress(actionExecution.getStrIpAdress());
					view.getActionExecution().setPortNumber(actionExecution.getPortNumber());
					view.getActionExecution().setTypeofProtocol(actionExecution.getTypeofProtocol());
					view.setIpAdress(actionExecution.getStrIpAdress());
					view.setPortNumber(actionExecution.getPortNumber());
					view.setTypeofProtocol(actionExecution.getTypeofProtocol());
					view.getBtnSend().setEnabled(true);

					if (view.isLoadfileOk()) {
						view.getStartingProcess().setEnabled(true);
						//view.getSetting().setEnabled(false);
						view.getStartingProcess().setImageDescriptor(
								AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID
										, IDefineID.IMG_START_PROCESS));
					}
					view.setPartName(actionExecution.getStrViewTitle());

				} catch (PartInitException e) {
					//e.printStackTrace();
					LOGGER.setLogger(LOGGER.getLogger().getLogger(CreateNewGeneratorViewAction.class.getName()));
					LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail creating part view. ", IDefineID.ERROR_PART_VIEW_FAIL_CREATING_VIEW));
				}
			}
		} else {
			LOGGER.setLogger(LOGGER.getLogger().getLogger(CreateNewGeneratorViewAction.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Too many views. ", IDefineID.ERROR_PART_VIEW_TOO_MANY_VIEWS));
			CommonMethodes.warningMessageBox("Sorry, too many views...", window.getShell());
		}
	}
}
