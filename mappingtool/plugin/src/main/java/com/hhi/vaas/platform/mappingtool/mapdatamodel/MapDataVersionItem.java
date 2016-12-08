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
 * hsbae			2015. 4. 30.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.mapdatamodel;

public class MapDataVersionItem {
	private String version;
	private String revision;
	
	public MapDataVersionItem(String version, String revision) {
		this.version = version;
		this.revision = revision;
	}

	public String getVersion() {
		return version;
	}

	public String getRevision() {
		return revision;
	}
}
//end of MapDataVDMVersionItem.java