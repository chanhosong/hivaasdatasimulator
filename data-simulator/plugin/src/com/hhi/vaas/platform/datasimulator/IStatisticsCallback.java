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

import java.util.List;

import com.hhi.vaas.platform.datasimulator.provider.StatisticsData;

public interface IStatisticsCallback {
	void displaySpeedOfTransfer(List<StatisticsData> statisticsData);
}
//end of IStatisticsCallback.java