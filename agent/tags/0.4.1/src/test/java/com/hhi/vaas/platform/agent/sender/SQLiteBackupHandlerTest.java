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
 * Bongjin Kwon	2015. 4. 29.		First Draft.
 */
package com.hhi.vaas.platform.agent.sender;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hhi.vaas.platform.agent.model.BackupMessage;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;

public class SQLiteBackupHandlerTest {
	
	private SQLiteBackupHandler backHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		backHandler = new SQLiteBackupHandler(new PropertyService(""));
	}

	@After
	public void tearDown() throws Exception {
		backHandler.close();
	}

	@Test
	public void testBackupMessage() {
		
		/*
		 * test for backupMessage(..)
		 */
		for (int i = 0; i < 5; i++) {
			backHandler.backupMessage("routingKey"+i, "body"+i);
		}
		
		Connection newConn = null;
		try{
			newConn = backHandler.newConnection();
			/*
			 * test for getBackupMsgTotalCount() & getAllMessages()
			 */
			int count = backHandler.getBackupMsgTotalCount(newConn);
			
			List<BackupMessage> messages = backHandler.getMessages(newConn);
			
			System.out.println(messages.toString());
			
			assertEquals(5, count);
			assertEquals(count, messages.size());
		
		
			/*
			 * test for newConnection() & deleteMessage();
			 */
			backHandler.deleteMessage(newConn, messages);
			
			count = backHandler.getBackupMsgTotalCount(newConn);
			
			assertEquals(0, count);
		}catch(SQLException e){
			fail(e.toString());
		}finally{
			try{
				if(newConn != null){
					newConn.close();
				}
			}catch(SQLException ex){
				//ignore.
			}
		}
	}

}
//end of BackupHandlerTest.java