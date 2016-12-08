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
 * Bongjin Kwon	2015. 5. 28.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author BongJin Kwon
 *
 */
public class VDMPathDatas extends HashMap<String, List<ItemData>> {

	public VDMPathDatas(){
		
	}
	
	public void addData(String vdmFullPath, ItemData data){
		
		List<ItemData> datas = null;
		if (containsKey(vdmFullPath)) {
			datas = get(vdmFullPath);
		}else{
			datas = new ArrayList<ItemData>();
			put(vdmFullPath, datas);
		}
		
		datas.add(data);
	}
	
	
}
//end of VDMPathDatas.java