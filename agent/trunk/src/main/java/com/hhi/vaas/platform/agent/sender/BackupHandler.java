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
 * Bongjin Kwon	2015. 4. 30.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.sender;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.hhi.vaas.platform.agent.model.BackupMessage;

/**
 * @author BongJin Kwon
 *
 */
public interface BackupHandler {
	
	int getBackupMsgTotalCount(Connection newConnction);
	
	void backupMessage(String routingKey, String body);
	
	List<BackupMessage> getMessages(Connection newConnction);
	
	void deleteMessage(Connection newConnction, List<BackupMessage> msgs);
	
	Connection newConnection()throws SQLException;
	
	void close();
	
}
//end of BackupHandler.java