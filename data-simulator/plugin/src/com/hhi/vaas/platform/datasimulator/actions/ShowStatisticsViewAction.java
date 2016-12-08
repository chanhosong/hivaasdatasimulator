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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

public class ShowStatisticsViewAction extends Action {
	
	private final IWorkbenchWindow window;
	private final String viewId;

	public ShowStatisticsViewAction(IWorkbenchWindow window, String viewId) {
		this.window = window;
		this.viewId = viewId;
	}

	public void run() {
		try {
			window.getActivePage().showView(viewId);			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
//end of showStatisticsViewAction.java