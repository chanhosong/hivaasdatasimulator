/* Copyright (C) eMarine Co. Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of eMarine Co. Ltd.
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with eMarine Co. Ltd.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * ddong			2015. 7. 16.				First Draft.
 */
package com.hhi.vaas.platform.datasimulator.actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.hhi.vaas.platform.datasimulator.IDefineID;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.common.ViewMapSingleton;
import com.hhi.vaas.platform.datasimulator.udp.NicBinding;
import com.hhi.vaas.platform.datasimulator.ui.GeneratorView;

public class GeneratorViewRestore {
	private ViewMapSingleton map;
	private IWorkbenchWindow window;

	private LoggerSingletone LOGGER = LoggerSingletone.getInstance();

	public GeneratorViewRestore(IWorkbenchWindow window) {
		map = ViewMapSingleton.getInstance();
		this.window = window;
	}

	public void restore() {
		String ipAdrees = null, viewTitle = null, filePath = null, portNumber = null, typeOfProtocol = null, 
				functionCode = null, periodTime = null, nicName = null, device = null, dataType = null;
		Properties p = new Properties();
		Map<String, String> propertiesMap = new TreeMap<String, String>();

		try {
			FileInputStream in = new FileInputStream(IDefineID.PROPERTIES);
			p.load(in);
			in.close();
			
			if (!p.isEmpty()) {
				int viewCount = Integer.valueOf(p.getProperty(IDefineID.PROPERTIES_VIEW_COUNT));
				
				/*if (viewCount == -1) {
					window.getActivePage().showView(IDefineID.GENERATOR_VIEW_ID,
							Integer.toString(map.getNextGeneratorInstanceCount()), IWorkbenchPage.VIEW_ACTIVATE);
					map.getNextGeneratorInstanceMax();
				} else*/ if (viewCount > -1) {
					for (String key : p.stringPropertyNames()) {
						propertiesMap.put(key, p.getProperty(key));
						propertiesMap.remove(IDefineID.PROPERTIES_VIEW_COUNT);
					}

					for (int i = 0; i <= viewCount; i++) {
						for (String key : propertiesMap.keySet()) {
							StringTokenizer tok = new StringTokenizer(key);
							String keyTmp01 = tok.nextToken(".");
							String keyTmp02 = tok.nextToken(".");

							if (keyTmp01.equals(IDefineID.PROPERTIES_VIEW_TITLE) 
									&& keyTmp02.equals(String.valueOf(i))) {
								viewTitle = propertiesMap.get(key);
							} else if (keyTmp01.equals(IDefineID.PROPERTIES_TYPE_OF_PROTOCOL) 
									&& keyTmp02.equals(String.valueOf(i))) {
								typeOfProtocol = propertiesMap.get(key);
							} else if (keyTmp01.equals(IDefineID.PROPERTIES_IP_ADDRESS) 
									&& keyTmp02.equals(String.valueOf(i))) {
								ipAdrees = propertiesMap.get(key);
							} else if (keyTmp01.equals(IDefineID.PROPERTIES_PORT_NUMBER) 
									&& keyTmp02.equals(String.valueOf(i))) {
								portNumber = propertiesMap.get(key);
							} else if (keyTmp01.equals(IDefineID.PROPERTIES_PEROID) && keyTmp02.equals(String.valueOf(i))) {
								periodTime = propertiesMap.get(key);
							} else if (keyTmp01.equals(IDefineID.PROPERTIES_FUNCTION_CODE) 
									&& keyTmp02.equals(String.valueOf(i))) {
								functionCode = propertiesMap.get(key);
							} else if (keyTmp01.equals(IDefineID.PROPERTIES_FILE_PATH) 
									&& keyTmp02.equals(String.valueOf(i))) {
								filePath = propertiesMap.get(key);
							} else if(keyTmp01.equals(IDefineID.PROPERTIES_NIC)
									&& keyTmp02.equals(String.valueOf(i))) {
								nicName = propertiesMap.get(key);
							} else if (keyTmp01.equals(IDefineID.PROPERTIES_DEVICE) 
									&& keyTmp02.equals(String.valueOf(i))) {
								device = propertiesMap.get(key);
							} else if (keyTmp01.equals(IDefineID.PROPERTIES_DATA_TYPE) 
									&& keyTmp02.equals(String.valueOf(i))) {
								dataType = propertiesMap.get(key);
							}
						}

						window.getActivePage().showView(IDefineID.GENERATOR_VIEW_ID,
								Integer.toString(map.getNextGeneratorInstanceCount()), IWorkbenchPage.VIEW_ACTIVATE);
						map.getNextGeneratorInstanceMax();

						GeneratorView view = map.getMap().get(map.getGeneratorInstanceCount());

						view.setViewName(viewTitle);
						view.setPartName(viewTitle);
						view.setDevice(device);
						view.setDataType(dataType);

						view.setPeriodTime(Long.valueOf(periodTime));
						view.getTxtPeriod().setText(periodTime);

						view.setFunctionCode(Integer.valueOf(functionCode));
						view.repeat();

						view.setTypeofProtocol(Integer.valueOf(typeOfProtocol));
						view.setIpAdress(ipAdrees);
						view.setPortNumber(Integer.valueOf(portNumber));
						
						if(nicName != null && !nicName.equals("")) {
							NicBinding nicListBinding = new NicBinding();
							List<NetworkInterface> nicList = new ArrayList<NetworkInterface>();
							nicList = nicListBinding.findNetworkInterface();
							for(int j = 0; j < nicList.size(); j++){
								if(nicName.equals(nicList.get(j).getDisplayName()));
								view.setNic(nicList.get(j));
								view.getActionExecution().setNic(nicList.get(j));
							}
						} 
						
						view.getActionExecution().setTypeofProtocol(Integer.valueOf(typeOfProtocol));
						view.getActionExecution().setIpAdress(ipAdrees);
						view.getActionExecution().setPortNumber(Integer.valueOf(portNumber));

						view.setFilePath(filePath);
						if (filePath != null && !filePath.equals("")) {
							view.fileLoad(filePath);
							view.getStartingProcess().setEnabled(true);
						}
						//view.getSetting().setEnabled(false);
						view.setSettingOk(true);
					}
				}
			} /*else if (p.isEmpty()) {
				window.getActivePage().showView(IDefineID.GENERATOR_VIEW_ID,
						Integer.toString(map.getNextGeneratorInstanceCount()), IWorkbenchPage.VIEW_ACTIVATE);
				map.getNextGeneratorInstanceMax();
			}*/		
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorViewRestore.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x File not found.", IDefineID.ERROR_FILE_NOT_FOUND));
		} catch (IOException e) {
			//e.printStackTrace();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorViewRestore.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Property IO exception. ", IDefineID.ERROR_IO_EXCEPTION));
		} catch (PartInitException e) {
			//e.printStackTrace();
			LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorViewRestore.class.getName()));
			LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail creating part view. ", IDefineID.ERROR_PART_VIEW_FAIL_CREATING_VIEW));
		}
	}
}
// end of TestRestore.java