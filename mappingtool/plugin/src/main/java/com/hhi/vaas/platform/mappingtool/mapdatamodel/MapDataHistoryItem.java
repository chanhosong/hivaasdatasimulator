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

/**
 * MapDataHistoryItem
 *   - <Hitem version="0" revision="2" when="2015/02/21" who="" what="" why="" />
 * @author hsbae
 *
 */
public class MapDataHistoryItem {
	
		private String version;
		private String revision;
		private String when;
		private String who;
		private String what;
		private String why;
		
		public MapDataHistoryItem(String version, String revision, String when, String who, String what, String why) {
			this.version = version;
			this.revision = revision;
			this.when = when;
			this.who = who;
			this.what = what;
			this.why = why;
		}
		
		public String getVersion() {
			return version;
		}
		
		public String getRevision() {
			return revision;
		}
		
		public String getWhen() {
			return when;
		}
		
		public String getWho() {
			return who;
		}
		
		public String getWhat() {
			return what;
		}
		
		public String getWhy() {
			return why;
		}
	
}
//end of MapDataHistoryItem.java