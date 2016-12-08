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
 * Sang-cheon Park	2015. 4. 15.		First Draft.
 */
package com.hhi.vaas.platform.middleware.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

/**
 * <pre>
 * Publication error handlers are provided with a publication error every time an
 * error occurs during message publication.
 * A handler might fail with an exception, not be accessible because of the presence
 * of a security manager or other reasons might lead to failures during the message publication process.
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class AdapterPublicationErrorHandler implements IPublicationErrorHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdapterPublicationErrorHandler.class);

	/* (non-Javadoc)
	 * @see net.engio.mbassy.bus.error.IPublicationErrorHandler#handleError(net.engio.mbassy.bus.error.PublicationError)
	 */
	@Override
	public void handleError(PublicationError error) {
		LOGGER.error("AdapterEventMessage publication error. ", error.getCause());
	}
	//end of handleError()
}
//end of AdapterPublicationErrorHandler.java