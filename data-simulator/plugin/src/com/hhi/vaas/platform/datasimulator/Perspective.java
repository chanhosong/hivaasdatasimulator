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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;

import com.hhi.vaas.platform.datasimulator.common.CommonMethodes;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;

public class Perspective implements IPerspectiveFactory {
	private LoggerSingletone LOGGER = LoggerSingletone.getInstance();

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		IFolderLayout top = layout.createFolder("statisticsLayout", IPageLayout.TOP, 0.3f, layout.getEditorArea());
		top.addView(IDefineID.STATISTICS_VIEW_ID);
		IViewLayout statisticsLayout = layout.getViewLayout(IDefineID.STATISTICS_VIEW_ID);
		statisticsLayout.setMoveable(false);
		statisticsLayout.setCloseable(false);
		
		Properties p = new Properties();
		
		try {
			boolean isFile = CommonMethodes.fileMake(IDefineID.PROPERTIES);
			FileInputStream in = new FileInputStream(IDefineID.PROPERTIES);
			
			p.load(in);
			in.close();
		
			if (p.isEmpty()) {
				layout.addView(IDefineID.GENERATOR_VIEW_ID, IPageLayout.BOTTOM, 0.7f, layout.getEditorArea());
			} else {
				int viewCount = Integer.valueOf(p.getProperty(IDefineID.PROPERTIES_VIEW_COUNT));
				
				if (viewCount == -1) {
					layout.addView(IDefineID.GENERATOR_VIEW_ID, IPageLayout.BOTTOM, 0.7f, layout.getEditorArea());
				}
			}
		} catch (FileNotFoundException e) {
			layout.addView(IDefineID.GENERATOR_VIEW_ID, IPageLayout.BOTTOM, 0.7f, layout.getEditorArea());
			//e.printStackTrace();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ApplicationWorkbenchAdvisor.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Property file not found. ", IDefineID.ERROR_FILE_NOT_FOUND));
		} catch (IOException e) {
			layout.addView(IDefineID.GENERATOR_VIEW_ID, IPageLayout.BOTTOM, 0.7f, layout.getEditorArea());
			//e.printStackTrace();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ApplicationWorkbenchAdvisor.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Property file not found. ", IDefineID.ERROR_FILE_NOT_FOUND));
		}
	}
}
