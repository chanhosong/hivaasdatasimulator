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
/**
 * 
 */
package com.hhi.vaas.platform.agent.sender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.agent.model.BackupMessage;
import com.hhi.vaas.platform.middleware.common.util.PropertyService;

/**
 * backup message Handler
 * 
 * @author BongJin Kwon
 *
 */
public class SQLiteBackupHandler implements BackupHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteBackupHandler.class);
	
	private static final String DEFAULT_BACKUP_DB = "backup.db";
	private static final String DEFAULT_SQL_CREATE_TABLE = "CREATE TABLE BAK_MSG (routing_key TEXT, body TEXT, reg_dt DATETIME)";
	private static final String DEFAULT_SQL_INSERT_MESSAGE = "INSERT INTO BAK_MSG VALUES(?, ?, datetime('now'))";
	private static final String DEFAULT_SQL_SELECT_COUNT = "SELECT COUNT(1) FROM BAK_MSG";
	private static final String DEFAULT_SQL_SELECT_MSGS = "SELECT * FROM BAK_MSG ORDER BY reg_dt LIMIT ";
	private static final String DEFAULT_SQL_DELETE = "DELETE FROM BAK_MSG WHERE reg_dt = datetime(?)";

	private Connection connection;
    private String dbFileName;
    private String sqlSelectMsgs;
    private boolean isOpened = false;
 
 
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch(Exception e) {
        	LOGGER.error(e.toString(), e);
        }
    }
    
	public SQLiteBackupHandler(PropertyService props) {
		
		this.dbFileName = props.getProperty("vaas.sqlite.backup.db", String.class, DEFAULT_BACKUP_DB);
		this.sqlSelectMsgs = DEFAULT_SQL_SELECT_MSGS + props.getProperty("vaas.mq.clent.resend.count", Integer.class, 10);
		
		try {
	        
	        this.connection = newConnection();
	        createTable();
	        
	        isOpened = true;
	    } catch(SQLException e) { 
	    	LOGGER.error(e.toString(), e);
	    	isOpened = false;
	    }
	 
	}
	
	public int getBackupMsgTotalCount(Connection newConnction){
		Statement statement = null;
		ResultSet rs = null;
		
		int count = 0;
		try{
			statement = newConnction.createStatement();
	
		    rs = statement.executeQuery(DEFAULT_SQL_SELECT_COUNT);
		    
		    if(rs.next()){
		    	count = rs.getInt(1);
		    }
		    
		}catch(SQLException e){
			LOGGER.error(e.toString(), e);
		}finally{
			if(statement != null){
				try{
					rs.close();
					statement.close();
				}catch(SQLException e){
					//ignore.
				}
			}
		}
		
		return count;
	}
	
	/**
	 * backup message
	 * @param routingKey
	 * @param body
	 */
	public void backupMessage(String routingKey, String body){
		
		if(isOpened){
			PreparedStatement statement = null;
			try{
				statement = connection.prepareStatement(DEFAULT_SQL_INSERT_MESSAGE);
				statement.setString(1, routingKey);
				statement.setString(2, body);
				
				statement.executeUpdate();
				
				LOGGER.debug("saved : {}, {}", routingKey, body);
				
			}catch(SQLException e){
				LOGGER.error(e.toString(), e);
			}finally{
				if(statement != null){
					try{
						statement.close();
					}catch(SQLException e){
						//ignore.
					}
				}
			}
			
		}else{
			LOGGER.warn("Not yet backup db connect.");
		}
		
	}
	
	/**
	 * 
	 * @param newConnction
	 * @return
	 */
	public List<BackupMessage> getMessages(Connection newConnction){
		
		Statement statement = null;
		ResultSet rs = null;
		
		List<BackupMessage> messages = new ArrayList<BackupMessage>();
		
		try{
			statement = newConnction.createStatement();
	
		    rs = statement.executeQuery(this.sqlSelectMsgs);
		    
		    while(rs.next()){
		    	BackupMessage msg = new BackupMessage();
		    	
		    	msg.setRoutingKey( rs.getString(1) );
		    	msg.setBody( rs.getString(2) );
		    	msg.setRegDt( rs.getString(3) );
		    	
		    	messages.add(msg);
		    }
		    
		}catch(SQLException e){
			LOGGER.error(e.toString(), e);
		}finally{
			if(statement != null){
				try{
					rs.close();
					statement.close();
				}catch(SQLException e){
					//ignore.
				}
			}
		}
		
		return messages;
	}
	
	public void deleteMessage(Connection newConnction, List<BackupMessage> msgs){
		
		
		PreparedStatement statement = null;
		try{
			if(newConnction.getAutoCommit()){
				newConnction.setAutoCommit(false);
			}
			
			statement = connection.prepareStatement(DEFAULT_SQL_DELETE);
			for (BackupMessage backupMessage : msgs) {
				
				statement.setString(1, backupMessage.getRegDt());
				statement.addBatch();
			}
			statement.executeBatch();
			newConnction.commit();
			
			LOGGER.debug("deleted count: {}", msgs.size());
			
		}catch(SQLException e){
			LOGGER.error(e.toString(), e);
			try{
				newConnction.rollback();
			}catch(SQLException ex){
				//ignore.
			}
		}finally{
			
			if(statement != null){
				try{
					statement.close();
				}catch(SQLException e){
					//ignore.
				}
			}
		}
	}
	
	/**
	 * create new db connection.
	 * @return
	 * @throws SQLException
	 */
	public Connection newConnection()throws SQLException{
		return DriverManager.getConnection("jdbc:sqlite:" + this.dbFileName);
	}
	
	/**
	 * create table.
	 */
	private void createTable(){
		
		Statement statement = null;
		try{
			statement = connection.createStatement();
	
		    statement.executeUpdate(DEFAULT_SQL_CREATE_TABLE);
		    
		    LOGGER.info("created {}", DEFAULT_SQL_CREATE_TABLE);
		    
		}catch(SQLException e){
			LOGGER.info(e.getMessage());
		}finally{
			if(statement != null){
				try{
					statement.close();
				}catch(SQLException e){
					//ignore.
				}
			}
		}
	}
	
	/**
	 * Releases this Connection object's database
	 */
	public void close() {
	    if(this.isOpened && this.connection != null) {
	    	try {
		        this.connection.close();
		    } catch(SQLException e) { 
		    	//ignore.
		    }
	    }
	}


}
//end of BackupHandler.java