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
 * Bongjin Kwon	2015. 6. 25.		First Draft.
 */
package com.hhi.vaas.platform.agent.receiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * 로그파일로 테스트 데이타 만들기.
 */
public class TestData {

	public TestData() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		String path = "C:/Users/Administrator/Downloads/ACONISRawData/";
		BufferedReader br = null;
		BufferedWriter bw = null;
    	try{
	    	br = new BufferedReader(new FileReader(path + "ZMQ.log"));
	    	bw = new BufferedWriter(new FileWriter(path + "test_aconis2.txt"));
	    	
	    	int cnt = 0;
	    	String line = null;
	    	while ((line = br.readLine()) != null){
	    		int pos = line.indexOf("{");
	    		
	    		if (pos == -1){
	    			continue;
	    		}
	    		
	    		if(line.indexOf("\"MC046") > 0 || line.indexOf("\"SP004") > 0 || line.indexOf("\"GC067") > 0 || line.indexOf("\"GA067") > 0 
	    				|| line.indexOf("\"GB067") > 0 || line.indexOf("\"GD067") > 0) {
	    			String data = line.substring(pos);
		    		System.out.println(data);
		    		bw.write(data);
		    		bw.write("\n");
		    		cnt++;
		    		/*
		    		if(cnt == 10000){
		    			break;
		    		}*/
	    		}
	    	}
	    	
    	} catch (IOException e){
    		e.printStackTrace();
    		
    	} finally {
    		try{
	    		if( br != null){
	    			br.close();
	    		}
	    		if( bw != null){
	    			bw.close();
	    		}
    		} catch (IOException e){
    			//ignore.
    		}
    		System.out.println("finished!!");
    	}


	}

}
//end of TestData.java