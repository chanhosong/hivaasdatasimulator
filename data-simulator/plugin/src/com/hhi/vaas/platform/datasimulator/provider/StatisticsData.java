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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StatisticsData {
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private String viewTitle;
	private double linesPerSec;
	private double bytesPerSec;
	private double bytesTransferred;
	private boolean processing;
	private long periodTime;

	public StatisticsData() {}
	
	public StatisticsData(String viewTitle, double linesPerSec, double bytesPerSec, double bytesTransferred,
			boolean processing, long periodTime) {
		this.viewTitle = viewTitle;
		this.linesPerSec = linesPerSec;
		this.bytesPerSec = bytesPerSec;
		this.bytesTransferred = bytesTransferred;
		this.processing = processing;
		this.periodTime = periodTime;
	}
	
	public String getViewTitle() {
		return viewTitle;
	}

	public void setViewTitle(String viewTitle) {
		this.viewTitle = viewTitle;
	}

	public double getLinesPerSec() {
		return linesPerSec;
	}

	public void setLinesPerSec(double linesPerSec) {
		this.linesPerSec = linesPerSec;
	}

	public double getBytesPerSec() {
		return bytesPerSec;
	}

	public void setBytesPerSec(double bytesPerSec) {
		this.bytesPerSec = bytesPerSec;
	}

	public double getBytesTransferred() {
		return bytesTransferred;
	}

	public void setBytesTransferred(double bytesTransferred) {
		this.bytesTransferred = bytesTransferred;
	}
	
	public boolean isProcessing() {
		return processing;
	}

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	public void addPropertyChangeListener(String propertyName,	PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public long getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(long periodTime) {
		this.periodTime = periodTime;
	}
}
//end of StatisticsData.java