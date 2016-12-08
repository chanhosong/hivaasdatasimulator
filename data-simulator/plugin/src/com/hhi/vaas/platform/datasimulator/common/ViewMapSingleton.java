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

package com.hhi.vaas.platform.datasimulator.common;

import java.util.HashMap;
import java.util.Map;

import com.hhi.vaas.platform.datasimulator.ui.GeneratorView;

public class ViewMapSingleton {
	private static volatile ViewMapSingleton map;
	private Map<Integer, GeneratorView> generaotrViewMap = new HashMap<Integer, GeneratorView>();
	private int generatorInstanceCount = 0, generatorInstanceMax = 0;

	public ViewMapSingleton() {}
	
	public static ViewMapSingleton getInstance() {
		if(map == null) {
			synchronized (ViewMapSingleton.class) {
				if(map == null) {
					map = new ViewMapSingleton();
				}
			}
		}
		return map;
	}
	
	public Map<Integer, GeneratorView> getMap() {
		return generaotrViewMap;
	}
	
	public int getGeneratorInstanceCount() {
		return generatorInstanceCount;
	}
	
	public int getNextGeneratorInstanceCount() {
		return ++generatorInstanceCount;
	}
	
	public int getGeneratorInstanceMax() {
		return generatorInstanceMax;
	}
	
	public int getNextGeneratorInstanceMax() {
		return ++generatorInstanceMax;
	}
	
	public void discountGeneratorInstanceMax() {
		generatorInstanceMax--;
	}
}
//end of ViewMap.java