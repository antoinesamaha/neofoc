/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.focDataSourceDB.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.access.FocDataMap;
import com.foc.business.notifier.FocNotificationConst;
import com.foc.business.notifier.FocNotificationEvent;
import com.foc.business.notifier.FocNotificationManager;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.focDataSourceDB.db.connectionPooling.ConnectionCredentials;
import com.foc.focDataSourceDB.db.connectionPooling.ConnectionPool;
import com.foc.focDataSourceDB.db.connectionPooling.ConnectionWrapper;
import com.foc.focDataSourceDB.db.connectionPooling.CredentialsFileScanner;
import com.foc.focDataSourceDB.db.connectionPooling.StatementWrapper;

public class DBManagerServer {

	private ConnectionPool connectionPool = null;

  private String     dateRequestSQL      = null;
  private String     timeStampRequestSQL = null;

//  private boolean transaction_ShouldSurroundWithTransactionIfRequest = false;
//  private boolean transaction_Began                                  = false;
//  private Thread  transactionThread                                  = null ;
//  private int     nbrOfBeginTransactionsInTransactionThread          = 0    ;
  
  //Auxiliary Pools
  //---------------  
  private HashMap<String, ConnectionPool> auxiliaryPools = null;
  //---------------
  
	public DBManagerServer(){
		connectionPool = new ConnectionPool();
		Globals.logString("Before Callling the auxPools_LoadFiles");
		auxPools_LoadFiles();
	}
	
	public void dispose(){
		if(connectionPool != null){
			connectionPool.dispose();
			connectionPool = null;
		}
	}
	
	private ConnectionPool getConnectionPool(){
		return getConnectionPool(null);
	}
	
	public ConnectionPool getConnectionPool(String key){
		ConnectionPool pool = connectionPool;
		if(key != null){
			pool = auxPools_Get(key);
		}
		return pool;
	}
  	
  public StatementWrapper lockStatement() {
  	return lockStatement(null);
  }
  
  public StatementWrapper lockStatement(String dbSourceKey) {
  	StatementWrapper stm = null;
  	ConnectionPool cp = getConnectionPool(dbSourceKey);
  	if(cp == null){
  		Globals.logString("Could not getConnectionPool for key:"+dbSourceKey);
  	}else{
  		stm = cp.lockStatement();
  	}
    return stm;
  }
  
  public void unlockStatement(StatementWrapper stm) {
  	if(stm != null) stm.unlockStatement();
  }

  public Connection getConnection() {
  	return getConnectionPool() != null ? getConnectionPool().getConnection() : null;
  }
  
  public void releaseConnection(Connection connection) {
  	if(getConnectionPool() != null) getConnectionPool().releaseConnection(connection);
  }

  private String initializeDateRequest(){
    if(dateRequestSQL == null || timeStampRequestSQL == null){
      try{
      	Connection connection = getConnection();
        DatabaseMetaData dmt = connection.getMetaData();
        String fcts = dmt.getTimeDateFunctions();
        StringTokenizer tokenizer = new StringTokenizer(fcts, ",");
        Globals.logString(fcts);
        while(tokenizer.hasMoreTokens()){
          String tok = tokenizer.nextToken();        
          if(tok.compareTo("CURDATE") == 0){
            if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
              dateRequestSQL = "select sysdate from dual";
              break;
            }else{
              dateRequestSQL = "select CURDATE()";
              break;              
            }
          }else if(tok.compareTo("CURRENT_DATE") == 0){
            dateRequestSQL = "select CURRENT_DATE()";
            break;
          }else if(tok.compareTo("CURRENT_TIMESTAMP") == 0){
            timeStampRequestSQL = "select CURRENT_TIMESTAMP()";
          }
        }
        if(dateRequestSQL == null){
          dateRequestSQL = "";
        }
        if(timeStampRequestSQL == null){
          timeStampRequestSQL = "";
        }
        releaseConnection(connection);
      }catch (Exception e){
        Globals.logException(e);
      }
    }
    return dateRequestSQL;
  }
  
  public java.sql.Date getCurrentTimeStamp_AsTime(){
  	java.sql.Date timeStamp = null;
    try{
      initializeDateRequest();
      if(timeStampRequestSQL.compareTo("") == 0){
      	timeStamp = new java.sql.Date(System.currentTimeMillis());
      }else{
        StatementWrapper stm = lockStatement(null);
        
        stm = executeQuery_WithMultipleAttempts(stm, timeStampRequestSQL);
        ResultSet resSet = stm != null ? stm.getResultSet() : null;
        //ResultSet resSet = stm.executeQuery(timeStampRequestSQL);
        if(resSet != null && resSet.next()){
          //Globals.logString(resSet.getString(1));
          timeStamp = resSet.getDate(1);
        }
        
        if(resSet != null) resSet.close();
        unlockStatement(stm);
      }
    }catch (Exception e){
      Globals.logException(e);
    }
    return timeStamp;
  }

	public void transaction_BeginTransactionIfRequestIsToBeExecuted(){
		getConnectionPool().transaction_BeginTransactionIfRequestIsToBeExecuted();
//		if(transaction_ShouldSurroundWithTransactionIfRequest && !transaction_Began){
//			transaction_Began = true;
//			beginTransaction();
//		}
	}
	
	public void transaction_setShouldSurroundWithTransactionIfRequest(){
		getConnectionPool().transaction_setShouldSurroundWithTransactionIfRequest();
		/*
		if(nbrOfBeginTransactionsInTransactionThread == 0){
			transaction_Began = false;
		}
		nbrOfBeginTransactionsInTransactionThread++;
		this.transaction_ShouldSurroundWithTransactionIfRequest = true;
		*/
	}
	
	public void transaction_SeeIfShouldCommit(){
		getConnectionPool().transaction_SeeIfShouldCommit();
//    if(nbrOfBeginTransactionsInTransactionThread > 0){
//      nbrOfBeginTransactionsInTransactionThread--;
//    }
//    if(nbrOfBeginTransactionsInTransactionThread == 0){
//    	if(transaction_Began){
//    		commitTransaction();
//    	}
//    	this.transaction_ShouldSurroundWithTransactionIfRequest = false;
//    }
	}

  public Hashtable newAllRealTables(){
    Hashtable<String, String> tables = new Hashtable<String, String>();
    try {
    	if (Globals.getDBManager() != null && Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
    		//The JDBC way can be very slow on Oracle especially 12c with JDBC 8
    		if(Globals.getApp() != null && Globals.getApp().getDataSource() != null) {
	    		ArrayList<String> tablesArrayList = Globals.getApp().getDataSource().command_SelectRequest(new StringBuffer("SELECT table_name FROM all_tables"));
	    		if(tablesArrayList != null) {
		    		for(int i=0; i<tablesArrayList.size(); i++) {
		    			String tableName = tablesArrayList.get(i);
		    			tables.put(tableName, tableName);
		    		}
	    		}
    		}
    	} else {
    		Connection conn = getConnection();
	      DatabaseMetaData dmt = conn.getMetaData();
	      Globals.logString(dmt.getTimeDateFunctions());
	      if (dmt != null) {
	        ResultSet resultSet = dmt.getTables(null, null, null, new String[] { "TABLE" });
	        if (resultSet != null) {
	          while (resultSet.next()) {
	            String tableName = resultSet.getString(3);
	            tables.put(tableName, tableName);
	          }
	          resultSet.close();
	        }
	      }
	      releaseConnection(conn);
    	}
    } catch (Exception e) {
      Globals.logException(e);
    }
    return tables;
  }

  public static final int MAX_NUMBER_OF_ATTEMPTS = 3;
 
  public StatementWrapper executeQuery_WithMultipleAttempts(StatementWrapper stmt, String req){
  	return executeQuery_WithMultipleAttempts(stmt, req, SQLRequest.TYPE_SELECT, null);
  }
  
  public StatementWrapper executeQuery_WithMultipleAttempts(StatementWrapper stmt, String req, int queryType, FocObject focObject){
  	if(req != null){
  		if(ConfigInfo.isLogDBRequestActive() && (queryType != SQLRequest.TYPE_SELECT || ConfigInfo.isLogDBSelectActive())){
    		Globals.logString(req);
    	}
	  	
    	boolean successful    = false;
	  	int     attemptsCount = 0;
	  	while(attemptsCount < MAX_NUMBER_OF_ATTEMPTS && !successful){
	  		attemptsCount++;
		  	try{
		  		if(queryType != SQLRequest.TYPE_SELECT){
		  			FocDesc focDesc = focObject != null ? focObject.getThisFocDesc() : null;
		      	if(queryType == SQLRequest.TYPE_INSERT 
		      			&& focObject != null
		      			&& focDesc != null
		      			&& (
		      					   focDesc.getProvider() == DBManager.PROVIDER_MYSQL
		      					|| focDesc.getProvider() == DBManager.PROVIDER_MSSQL
		      					|| focDesc.getProvider() == DBManager.PROVIDER_H2
		      					|| focDesc.getProvider() == DBManager.PROVIDER_POSTGRES
		      					)
		      			){
		      		//We are here if we are MySQL and Insert
		      		Statement s = stmt != null ? stmt.getStatement() : null;
		      		if(s != null){
			    	  	s.executeUpdate(req, Statement.RETURN_GENERATED_KEYS);
			    	  	ResultSet rs = s.getGeneratedKeys();

			    	  	int indexOfREF = 1;
			    	  	if(focDesc.getProvider() == DBManager.PROVIDER_POSTGRES) {
//				    	  	ResultSetMetaData metaData = rs.getMetaData();
//				    	  	indexOfREF = metaData.getColumnCount();
				    	  	
			    	  		indexOfREF = -1;
				    	  	ResultSetMetaData metaData = rs.getMetaData();
				    	  	for(int i=1; i<=metaData.getColumnCount() && indexOfREF < 0; i++) {
				    	  		String colName = metaData.getColumnName(i);
				    	  		if(colName.equals(focDesc.getRefFieldName())) {
				    	  			indexOfREF = i;
				    	  		}
//				    	  		Globals.logString("Column["+i+"]="+colName);
				    	  	}
			    	  	}
			    	  	
			    	  	while(rs.next()){
			    	  		int ref = rs.getInt(indexOfREF);
	
			    	  		focObject.setReference_WithFocListRefHashMapAdjustment(ref);
			    	  	}
			    	  	if(rs != null) rs.close();
		      		}
		      	}else if(queryType == SQLRequest.TYPE_DELETE){
//		      		Globals.getApp().getDataSource().transaction_BeginTransactionIfRequestIsToBeExecuted();
			      	DBManagerServer.getInstance().transaction_BeginTransactionIfRequestIsToBeExecuted();
		      		Statement s = stmt != null ? stmt.getStatement() : null;
		      		if(s != null){
			      		s.executeUpdate(req);
		      		}
			      	Globals.getApp().getDataSource().transaction_SeeIfShouldCommit();
		      	}else{
		      		Statement s = stmt != null ? stmt.getStatement() : null;
		      		if(s != null){
		      			s.executeUpdate(req);
		      		}
		      	}
		      	
		  		}else{
	      		Statement s = stmt != null ? stmt.getStatement() : null;
	      		if(s != null){
	      			s.executeQuery(req);
	      		}
		  		}
		  		successful = true;
		  		
		  		if(focObject != null){
		  		  FocDataMap focDataMap = new FocDataMap(focObject);
  		  		focDataMap.putString("TABLE_NAME", focObject.getThisFocDesc().getStorageName());
		  		
  		  		switch(queryType){
  		  		case SQLRequest.TYPE_INSERT :
  	          FocNotificationManager.getInstance().fireEvent(new FocNotificationEvent(FocNotificationConst.EVT_TABLE_ADD, focDataMap));
  	          break;
  		  		case SQLRequest.TYPE_DELETE:
  	          FocNotificationManager.getInstance().fireEvent(new FocNotificationEvent(FocNotificationConst.EVT_TABLE_DELETE, focDataMap));
  	          break;
  		  		case SQLRequest.TYPE_UPDATE:
  	          FocNotificationManager.getInstance().fireEvent(new FocNotificationEvent(FocNotificationConst.EVT_TABLE_UPDATE, focDataMap));
  	          break;
  		  		default:
  		  		  break;
  		  		
  		  		}
		  		}		  		

		  	}catch(Exception e){
		  		if(attemptsCount < MAX_NUMBER_OF_ATTEMPTS){
		  			Globals.logExceptionWithoutPopup(e);
		  			Globals.logString("FOC will Attempt Again");
		  			
		  			stmt.unlockStatement();
		  			stmt = lockStatement(stmt.getDBSourceKey());
		  		}else{
		  			Globals.logException(e);
		  			if (ConfigInfo.isPopupExceptionDialog()) {
		  				Globals.showNotification("DB ERROR", ""+e.getMessage(), IFocEnvironment.TYPE_ERROR_MESSAGE);
		  			}
		  		}
		  	}
	  	}
  	}
    return stmt;
  }

  //---------------
  //Auxiliary Pools
  //---------------  

  private HashMap<String, ConnectionPool> auxPools_GetMap(boolean create){
  	if(auxiliaryPools == null && create){
  		auxiliaryPools = new HashMap<String, ConnectionPool>();
  	}
  	return auxiliaryPools;
  }
  
  public void auxPools_Put(String key, ConnectionCredentials cred){
  	ConnectionPool pool = new ConnectionPool(cred);
  	HashMap<String, ConnectionPool> map = auxPools_GetMap(true);
  	if(map != null){
  		map.put(key, pool);
  	}
  }
  
  private ConnectionPool auxPools_Get(String key){
  	ConnectionPool pool = null;
  	HashMap<String, ConnectionPool> map = auxPools_GetMap(false);
  	if(map != null){
  		pool = map.get(key);
  	}
  	return pool;
  } 
  
  public Iterator<ConnectionPool> auxPools_Iterator(){
  	Iterator<ConnectionPool> iter = null;
  	HashMap<String, ConnectionPool> map = auxPools_GetMap(false);
  	if(map != null && map.values() != null){
  		iter = map.values().iterator();
  	}
  	return iter;
  }

  private void auxPools_LoadFiles(){
		CredentialsFileScanner scanner = new CredentialsFileScanner("properties/db/");
		scanner.scanDirectory(this);
		scanner.dispose();
  }

  public StringBuffer getMonitoringText() {
  	StringBuffer buffer = new StringBuffer();
  	
  	if(getConnectionPool() != null) {
	  	StringBuffer poolBuffer = getConnectionPool().getMonitoringText();
	  	buffer.append(poolBuffer);
	  		
		  Iterator<ConnectionPool> iter = auxPools_Iterator();
		  while(iter != null && iter.hasNext()) {
		  	ConnectionPool pool = iter.next();
		  	
		  	if(pool != null) {
			  	poolBuffer = pool.getMonitoringText();
			  	buffer.append(poolBuffer);
		  	}
		  }
  	}
  	
  	return buffer;
  }
  
  //------------------------------------------------------------------
  // DBManagerServer
  //------------------------------------------------------------------
  
	public static DBManagerServer getInstance(){
		return (DBManagerServer) Globals.getApp().getDataSource().getDBManagerServer();
	}
}
