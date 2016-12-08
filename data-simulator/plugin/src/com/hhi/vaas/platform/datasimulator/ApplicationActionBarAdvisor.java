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

package com.hhi.vaas.platform.datasimulator;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.hhi.vaas.platform.datasimulator.actions.CreateNewGeneratorViewAction;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
	private IAction createNewGeneratorViewAction;
	private IWorkbenchAction preferencesAction;
	//private RetargetAction fileLoadAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

    protected void makeActions(IWorkbenchWindow window) {
		// Exit
    	exitAction = ActionFactory.QUIT.create(window);
    	register(exitAction);
		
		// about
    	aboutAction = ActionFactory.ABOUT.create(window);
    	register(aboutAction);
    	
		// Install and Update
		
		// Preferences
		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);

    	// Create a new generator view
    	createNewGeneratorViewAction = new CreateNewGeneratorViewAction(window, IDefineID.GENERATOR_VIEW_ID);
    	createNewGeneratorViewAction.setText("&New");
    	
    	// File load
		/*
		 * fileLoadAction = new RetargetAction("FileLoadAction", "&Open File...");
		 * fileLoadAction.setToolTipText("Open File");
		 * window.getPartService().addPartListener(fileLoadAction);
		 * register(fileLoadAction);
		 */
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	IMenuManager fileMenu = new MenuManager("&File", "File");
    	menuBar.add(fileMenu);
    	fileMenu.add(createNewGeneratorViewAction);
		fileMenu.add(exitAction);
		// fileMenu.add(fileLoadAction);
		/*IMenuManager helpMenu = new MenuManager("&Help", "Help");*/
		IMenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
    	menuBar.add(helpMenu);
		helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		helpMenu.add(new Separator());
		helpMenu.add(preferencesAction);
		helpMenu.add(new Separator());
    	helpMenu.add(aboutAction);
		helpMenu.add(new Separator());
    }    
}
