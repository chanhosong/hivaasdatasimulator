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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.common.ViewMapSingleton;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private LoggerSingletone LOGGER = LoggerSingletone.getInstance();

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return IDefineID.PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		configurer.setSaveAndRestore(false);
	}

	@Override
	public boolean preShutdown() {
		LOGGER.setLogger(LOGGER.getLogger().getLogger(ApplicationWorkbenchAdvisor.class.getName()));
		LOGGER.getLogger().info("Saving properites.");
		
		ViewMapSingleton map = ViewMapSingleton.getInstance();
		Properties p = new Properties();
		int i = 0;
				
		for (int key : map.getMap().keySet()) {
			try {
				if (map.getMap().get(key).isSettingOk()) {
					p.setProperty(IDefineID.PROPERTIES_FILE_PATH + "." + String.valueOf(i),
							map.getMap().get(key).getFilePath());
					p.setProperty(IDefineID.PROPERTIES_FUNCTION_CODE + "." + String.valueOf(i),
							String.valueOf(map.getMap().get(key).getFunctionCode()));
					p.setProperty(IDefineID.PROPERTIES_PEROID + "." + String.valueOf(i),
							String.valueOf(map.getMap().get(key).getPeriodTime()));
					p.setProperty(IDefineID.PROPERTIES_PORT_NUMBER + "." + String.valueOf(i),
							String.valueOf(map.getMap().get(key).getPortNumber()));
					p.setProperty(IDefineID.PROPERTIES_IP_ADDRESS + "." + String.valueOf(i),
							map.getMap().get(key).getIpAdress());
					p.setProperty(IDefineID.PROPERTIES_TYPE_OF_PROTOCOL + "." + String.valueOf(i),
							String.valueOf(map.getMap().get(key).getTypeofProtocol()));
					p.setProperty(IDefineID.PROPERTIES_VIEW_TITLE + "." + String.valueOf(i),
							map.getMap().get(key).getViewName());					
					p.setProperty(IDefineID.PROPERTIES_DEVICE + "." + String.valueOf(i),
							map.getMap().get(key).getDevice());
					p.setProperty(IDefineID.PROPERTIES_DATA_TYPE + "." + String.valueOf(i),
							map.getMap().get(key).getDataType());					
					if(map.getMap().get(key).getNic() != null	) {
						p.setProperty(IDefineID.PROPERTIES_NIC + "." + String.valueOf(i),
								map.getMap().get(key).getNic().getDisplayName());
					} else {
						p.setProperty(IDefineID.PROPERTIES_NIC + "." + String.valueOf(i), "");
					}				
					i++;
				}
				p.setProperty(IDefineID.PROPERTIES_VIEW_COUNT, String.valueOf(i - 1));
			} catch(Exception e) {
				LOGGER.getLogger().info("Excepted properites.");
				continue;
			}
		}

		try {
			FileOutputStream out = new FileOutputStream(IDefineID.PROPERTIES);
			p.store(out, "Data Simulator Properties");
			out.close();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ApplicationWorkbenchAdvisor.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Property file not found. ", IDefineID.ERROR_FILE_NOT_FOUND));
		} catch (IOException e) {
			//e.printStackTrace();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(ApplicationWorkbenchAdvisor.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Property IO exception. ", IDefineID.ERROR_IO_EXCEPTION));
		}
		
		LOGGER.setLogger(LOGGER.getLogger().getLogger(ApplicationWorkbenchAdvisor.class.getName()));
		LOGGER.getLogger().info("Shutdown");
		
		return super.preShutdown();
	}
}
