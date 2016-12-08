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

package com.hhi.vaas.platform.datasimulator.provider;

import java.util.ArrayList;
import java.util.List;

import com.hhi.vaas.platform.datasimulator.IDefineID;
import com.hhi.vaas.platform.datasimulator.IStatisticsCallback;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.common.ViewMapSingleton;

public class UpdateStatistics implements Runnable {
	private Thread thisThread;
	private volatile int stateCode;
	private List<StatisticsData> statisticsData;
	private IStatisticsCallback statisticsCallback;
	private ViewMapSingleton map;
	
	private LoggerSingletone LOGGER = LoggerSingletone.getInstance();

	public UpdateStatistics(IStatisticsCallback statisticsCallback) {
		this.statisticsCallback = statisticsCallback;
		stateCode = IDefineID.STATE_INIT;
		map = ViewMapSingleton.getInstance();
	}
	
	public void setSpeedDataToStatisticsView() {
		double linesPerSec =  0, bytesPerSec =  0, bytesTransferred =  0, totalLinesPerSec = 0,
				totalBytesPerSec = 0, totalBytesTransferred = 0;
		long periodTime;
		boolean processing = false;
		setStatisticsData(new ArrayList<StatisticsData>());
			
		for(int key : map.getMap().keySet()) {
			if(map.getMap().get(key).getStateCode() == IDefineID.STATE_STARTED) {
				processing = true;
			} else {
				processing = false;
			}
			
			linesPerSec = map.getMap().get(key).getLinesPerSec();
			bytesPerSec = map.getMap().get(key).getBytesPerSec();
			bytesTransferred = map.getMap().get(key).getBytesTransferred();
			periodTime = map.getMap().get(key).getPeriodTime();
			statisticsData.add(new StatisticsData(map.getMap().get(key).getPartName(), linesPerSec, bytesPerSec, bytesTransferred, processing, periodTime));			
		}
	}
	
	public void setStatisticsData(List<StatisticsData> statisticsData) {
		this.statisticsData = statisticsData;
	}

	public void run() {
		while(true) {
			while (stateCode == IDefineID.STATE_SUSPENDED) {
				try {
					Thread.sleep(24 * 60 * 60 * 1000);
				} catch (InterruptedException e) {
					LOGGER.setLogger(LOGGER.getLogger().getLogger(UpdateStatistics.class.getName()));
					LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail file thread sleep. ", IDefineID.ERROR_THREAD_SLEEP));
					if (stateCode != IDefineID.STATE_SUSPENDED) {
						LOGGER.setLogger(LOGGER.getLogger().getLogger(UpdateStatistics.class.getName()));
						LOGGER.getLogger().info("Wake up and stating send message.");
						break;
					}
				}
			}

			if (stateCode == IDefineID.STATE_STOPPED) {
				LOGGER.setLogger(LOGGER.getLogger().getLogger(UpdateStatistics.class.getName()));
				LOGGER.getLogger().info("Stopping send message.");
				break;
			}

			setSpeedDataToStatisticsView();
			statisticsCallback.displaySpeedOfTransfer(statisticsData);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(UpdateStatistics.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Fail file thread sleep. ", IDefineID.ERROR_THREAD_SLEEP));
			}
		}

		stateCode = IDefineID.STATE_INIT;
	}

	public int getStateCode() {
		return stateCode;
	}

	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}

	public void start() {
		synchronized (this) {
			if (stateCode != IDefineID.STATE_INIT) {
				throw new IllegalStateException("Already started");
			}

			thisThread = new Thread(this);
			thisThread.start();
			stateCode = IDefineID.STATE_STARTED;
		}
	}

	public void resume() {
		synchronized (this) {
			if (stateCode == IDefineID.STATE_STARTED
					|| stateCode == IDefineID.STATE_INIT) {
				return;
			}

			if (stateCode == IDefineID.STATE_STOPPED) {
				throw new IllegalStateException("Already stopped");
			}

			stateCode = IDefineID.STATE_STARTED;
			thisThread.interrupt();
		}
	}

	public void suspend() {
		synchronized (this) {
			if (stateCode == IDefineID.STATE_SUSPENDED) {
				return;
			}

			if (stateCode == IDefineID.STATE_INIT) {
				throw new IllegalStateException("Not started yet");
			}

			if (stateCode == IDefineID.STATE_STOPPED) {
				throw new IllegalStateException("Already stoped");
			}

			stateCode = IDefineID.STATE_SUSPENDED;
		}
	}

	public void stop() {
		synchronized (this) {
			if (stateCode == IDefineID.STATE_STOPPED) {
				throw new IllegalStateException("Already stoped");
			}

			thisThread.interrupt();
			stateCode = IDefineID.STATE_STOPPED;
		}
	}

}
//end of UpdateStatistics.java