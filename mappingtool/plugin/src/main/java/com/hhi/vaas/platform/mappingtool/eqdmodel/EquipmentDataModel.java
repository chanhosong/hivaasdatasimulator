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
 * hsbae			2015. 4. 10.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.eqdmodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class EquipmentDataModel {
	public EquipmentDataModel parent;
	public List<EquipmentDataModel> child = new ArrayList<EquipmentDataModel>();
	
	private String 	name;
	private String 	desc;
	private String 	type;
	private Set 	linkSet = new HashSet<String>();
	private boolean bMapped = false;
	
	
	
	public EquipmentDataModel(EquipmentDataModel parent,  String name, String desc, String type) 
	{
		this.parent = parent;
	
		this.name = name;
		this.desc = desc;
		this.type = type;
	}
	
	public List<EquipmentDataModel> getChildren() {
		return child;
	}
	
	public EquipmentDataModel(EquipmentDataModel parent, String name, String desc)
	{
		this(parent, name, desc, "");
	}
	
	public EquipmentDataModel(EquipmentDataModel parent, String name) {
		this(parent, name, "", "");
	}
	
	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public String getType() {
		return type;
	}
	
	public String getLinkCount() {
		return "" + linkSet.size();
	}
	
	public String getDestination() {
		return linkSet.toString();
	}
	
	public boolean setName(String name) {
		this.name = name;
		return true;
	}
	
	public boolean setDesc(String desc) {
		this.desc = desc;
		return true;
	}
	
	public boolean setType(String type) {
		this.type = type;
		return true;
	}
	
	public boolean setDestination(String destination) {
		linkSet.add(destination);
		bMapped = true;
		return true;
	}
	
	public void clearDestination() {
		bMapped = false;
		linkSet.clear();
	}
	
	public boolean isMapped() {
		return bMapped;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
}
//end of EquipmentDataModel.java