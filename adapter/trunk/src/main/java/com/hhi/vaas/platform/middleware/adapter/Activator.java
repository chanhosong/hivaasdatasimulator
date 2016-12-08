/* Copyright (C) 1999~2015 Hyundai Heavy Industries. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Hyundai Heavy Industries
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with Hyundai Heavy Industries.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * Sang-cheon Park	2015. 4. 7.			First Draft.
 */
package com.hhi.vaas.platform.middleware.adapter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.middleware.adapter.AdapterManager;
import com.hhi.vaas.platform.middleware.common.logging.LogbackInitializer;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;

/**
 * <pre>
 * Bundle Activator Class to run UDPServer
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class Activator implements BundleActivator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);
	
	private AdapterManager manager;
	private PropertyService propertyService;
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext ctx) throws Exception {
		LOGGER.info("Starting Adapter...");
		
		propertyService = new PropertyService(ctx);
		LogbackInitializer.initLogging("UDP_ADAPTER", propertyService.getResourceStreamFromPath("/logback.xml"));
		
		manager = AdapterManager.getInstance(propertyService);
		manager.init();
	}
	//end of start()

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext ctx) throws Exception {
		LOGGER.info("Terminated Adapter...");
		
		if (manager != null) {
			manager.stopAll();
		}
	}
	//end of stop()
}
//end of Activator.java