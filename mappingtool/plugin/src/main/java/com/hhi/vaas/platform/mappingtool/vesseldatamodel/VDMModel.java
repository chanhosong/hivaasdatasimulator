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
 * hsbae			2015. 5. 8.		First Draft.
 */

package com.hhi.vaas.platform.mappingtool.vesseldatamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * VDMModel class
 *  - for display to the tree viewer.
 *  - will mapped with each vdm item;
 * @author hsbae
 *
 */
public class VDMModel {
	public VDMModel parent;
	public List<VDMModel> childList = new ArrayList<VDMModel>();
	
	private String name;
	private String vdmPath;
	private String desc;
	
	public VDMModel(VDMModel parent, String name, String vdmPath, String desc) {
		this.parent = parent;
		this.name = name;
		this.vdmPath = vdmPath;
		this.desc = desc;
	}

	public VDMModel(VDMModel parent, String name, String vdmPath) {
		this.parent = parent;
		this.name = name;
		this.vdmPath = vdmPath;
		this.desc = "";
	}
	
	@Override
	public String toString() {
		return name;
	}

}
//end of VDMModel.java