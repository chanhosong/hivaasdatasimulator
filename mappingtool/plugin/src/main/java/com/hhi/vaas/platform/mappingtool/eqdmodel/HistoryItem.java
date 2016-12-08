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
 * hsbae			2015. 4. 20.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.eqdmodel;


public class HistoryItem {
	private String revision = "0";
	private String version = "0";
	private String what = "";
	private String when = "";
	private String who = "";
	private String why = "";
	
	public HistoryItem() {

	}
	
	public HistoryItem(String revision, String version) {

		this.revision = revision;
		this.version = version;
		
	}
	
	public HistoryItem(String revision, String version, String what, String when, String who, String why) {
		this.revision = revision;
		this.version = version;
		this.what = what;
		this.when = when;
		this.who = who;
		this.why = why;
	}
	
	public void reset() {
		revision = "0";
		version = "0";
		what = "";
		when = "";
		who = "";
		why = "";
	}

	public String getRevision() {
		return revision;
	}

	public String getVersion() {
		return version;
	}
	
	public String getWhat() {
		return what;
	}

	public String getWhen() {
		return when;
	}

	public String getWho() {
		return who;
	}

	public String getWhy() {
		return why;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public void setWhy(String why) {
		this.why = why;
	}
	
	
	
}
//end of HistoryItem.java