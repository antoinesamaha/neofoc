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
package com.foc.focDataSourceDB.db.connectionPooling;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.util.Utils;

public class ConnectionPool {
	private ConnectionCredentials        credentials     = null;
	
	private ConnectionWrapper            defaultConnectionWrapper = null;
	
//	private ArrayList<ConnectionWrapper> busyConnections = null;//These Connections are autocommit=false
//	private ArrayList<ConnectionWrapper> freeConnections = null;
	
	private ArrayList<ConnectionWrapper> connectionsQueuedForDispose = null;

	public ConnectionPoolThreadLocal threadLocal_Connections = null;
//	public ThreadLocal<ThreadDBTransactionData> threadLocal_TransactionData = null;
	
	public ConnectionPool(){
		this(null);
	}
	
	public ConnectionPool(ConnectionCredentials credentials){
		this.credentials = credentials;
	
		threadLocal_Connections = new ConnectionPoolThreadLocal();
//		threadLocal_TransactionData = new ThreadLocal<ThreadDBTransactionData>();
		
//		busyConnections = new ArrayList<ConnectionWrapper>();
//		freeConnections = new ArrayList<ConnectionWrapper>();
		
		connectionsQueuedForDispose = null;
	}
	
	public void dispose(){
		/*
		if(busyConnections != null){
			for(int i=0; i<busyConnections.size(); i++){
				busyConnections.get(i).dispose();
			}
			busyConnections.clear();
			busyConnections = null;
		}
		
		if(freeConnections != null){
			for(int i=0; i<freeConnections.size(); i++){
				freeConnections.get(i).dispose();
			}
			freeConnections.clear();
			freeConnections = null;
		}
		*/
		
		dispose_QueuedConnections_Dispose(false);
		
		if(credentials != null){
			credentials.dispose();
			credentials = null;
		}

		if(threadLocal_Connections != null){
			//TODO Should dispose this ThreadLocal local like it deserves to be disposed
			threadLocal_Connections = null;
		}
		
//		if(threadLocal_TransactionData != null){
//			threadLocal_TransactionData = null;
//		}
		
		dispose_DefaultConnectionWrapper();
	}
	
	public void dispose_DefaultConnectionWrapper(){
		if(defaultConnectionWrapper != null){
			defaultConnectionWrapper.dispose();
			defaultConnectionWrapper = null;
		}
	}

	public synchronized void dispose_QueuedConnections_Add() {
		if(defaultConnectionWrapper != null) {
			if(connectionsQueuedForDispose == null) {
				connectionsQueuedForDispose = new ArrayList<ConnectionWrapper>();
			}			
			connectionsQueuedForDispose.add(defaultConnectionWrapper);
			if (credentials != null) {
				Globals.logString(" DB CONNECTION QUEUED FOR DISPOSE. Total: "+connectionsQueuedForDispose.size()+" key="+credentials.getDbSourceKey());
			} else {
				Globals.logString(" DB CONNECTION QUEUED FOR DISPOSE. Total: "+connectionsQueuedForDispose.size());
			}
			defaultConnectionWrapper = null;
		}
	}

	public synchronized void dispose_QueuedConnections_Dispose(boolean checkForPendingStatements) {
		if(connectionsQueuedForDispose != null) {
			for(int i=connectionsQueuedForDispose.size()-1; i>=0; i--) {
				ConnectionWrapper cw = connectionsQueuedForDispose.get(i);
				if (cw != null && (!checkForPendingStatements || !cw.hasBusyStatements())) {
					cw.dispose();
					connectionsQueuedForDispose.remove(cw);
					
					if (credentials != null) {
						Globals.logString(" DB CONNECTION QUEUED IS ACTUALLY DISPOSED. Total: "+connectionsQueuedForDispose.size()+" Key="+credentials.getDbSourceKey());
					} else {
						Globals.logString(" DB CONNECTION QUEUED IS ACTUALLY DISPOSED. Total: "+connectionsQueuedForDispose.size());
					}
				}
			}
			if (connectionsQueuedForDispose.size() == 0) {
				//In this case we would have removed all connections wrapped
				connectionsQueuedForDispose = null;
			}
		}
	}

	public StringBuffer getMonitoringText() {
		ConnectionWrapper wrapper = getConnectionWrapper();
		return wrapper != null ? wrapper.getMonitoringText() : null;
	}
	
	/*
  private synchronized ConnectionWrapper lockConnectionWrapper() {
    ConnectionWrapper connection = null;
    if (freeConnections.size() > 0) {
      connection = (ConnectionWrapper) freeConnections.get(0);
      freeConnections.remove(0);
    } else {
    	connection = newConnectionWrapper();
    	connection.setAutoCommit(false);//All connections n the pool are not autocomit
    }
    
    if (connection != null) {
    	threadLocal_Connections.set(connection);
      busyConnections.add(connection);
    }
    return connection;
  }
  
  public synchronized void unlockConnectionWrapper(ConnectionWrapper connection) {
    if (connection != null) {
      busyConnections.remove(connection);
      if(freeConnections.size() < 5){
      	freeConnections.add(connection);	
      }else{
      	try{
      		connection.dispose();
      	}catch(Exception e){
      		Globals.logException(e);
      	}
      }
    	threadLocal_Connections.set(null);
    }
  }
  */
	
	public int getProvider(){
		return getCredentials() != null ? getCredentials().getProvider() : 0;
	}
	
	public int getServerVersion(){
		return getCredentials() != null ? getCredentials().getServerVersion() : 0;
	}
	
	/*
	public DBManagerServer getDbManagerServer() {
		return dbManagerServer;
	}

	public void setDbManagerServer(DBManagerServer dbManagerServer) {
		this.dbManagerServer = dbManagerServer;
	}
*/
	public ConnectionCredentials getCredentials() {
		if(credentials == null){
			credentials = new ConnectionCredentials();
			credentials.fillCredentialsIfNeeded();
		}
		return credentials;
	}

	public void setCredentials(ConnectionCredentials credentials) {
		this.credentials = credentials;
	}

	/**
	 * Checks if the current thread has already a Connection related to it and returns it.
	 * If not it will return the AUTOCOMMIT connection
	 * 
	 * @return {@link ConnectionWrapper}
	 */
	public ConnectionWrapper getConnectionWrapper(){
		return getConnectionWrapper(false); 
	}
	
	/*
	public void closeReopenConnection(){
		if(getConnectionWrapper() == null || !getConnectionWrapper().isValid()) {
			getConnectionWrapper(true);
		}
	}
	*/

	private ConnectionWrapper recreateConnectionWrapper(ConnectionWrapper connectionWrapper){
		if(connectionWrapper != null){
			connectionWrapper.dispose();
			connectionWrapper = null;
			connectionWrapper = newConnectionWrapper();
		}
		return connectionWrapper;
	}

	private ConnectionWrapper getConnectionWrapper_IfDedicated(){
		ConnectionWrapper connectionWrapper = threadLocal_Connections.get();
		return connectionWrapper;
	}
	
	private ConnectionWrapper getConnectionWrapper(boolean closeReopenBecauseExpired){
		ConnectionWrapper connectionWrapper = getConnectionWrapper_IfDedicated();
		if(closeReopenBecauseExpired && connectionWrapper != null){
			threadLocal_Connections.set(recreateConnectionWrapper(connectionWrapper));
		}
		if(connectionWrapper == null){
			connectionWrapper = getDefaultConnectionWrapper();
			if(closeReopenBecauseExpired && connectionWrapper != null){
				dispose_DefaultConnectionWrapper();
				connectionWrapper = getDefaultConnectionWrapper();
			} else if(connectionWrapper != null && connectionWrapper.exceededExpiryTime()){
				dispose_QueuedConnections_Add();
				dispose_QueuedConnections_Dispose(true);
				connectionWrapper = getDefaultConnectionWrapper();
			}
		}
		return connectionWrapper;
	}
		
	public Connection getConnection(){
		ConnectionWrapper cw = getConnectionWrapper();
		return cw != null ? cw.getConnection() : null;
	}

	public void releaseConnection(Connection connection){
		ConnectionWrapper cw = getConnectionWrapper();
		if(cw != null) cw.releaseConnection(connection);
	}
	
	private ConnectionWrapper newConnectionWrapper(){
		return new ConnectionWrapper(this);
	}
	
	private ConnectionWrapper getDefaultConnectionWrapper(){
		if(defaultConnectionWrapper == null){
			defaultConnectionWrapper = newConnectionWrapper();
		}
		return defaultConnectionWrapper; 
	}
	
	public StatementWrapper lockStatement(){
		return getConnectionWrapper() != null ? getConnectionWrapper().lockStatement() : null;
	}
	
	public void unlockStatement(StatementWrapper statement){
		if(getConnectionWrapper() != null){
			getConnectionWrapper().unlockStatement(statement);
		}
	}
	
	//--------------------------------------------------
	// Transaction
	//--------------------------------------------------
	
	public void transaction_BeginTransactionIfRequestIsToBeExecuted(){
		/*
		ThreadDBTransactionData transData = threadLocal_TransactionData.get();
		if(transData != null && transData.transaction_BeginTransactionIfRequestIsToBeExecuted()){
			Globals.logString("transaction_BeginTransactionIfRequestIsToBeExecuted");
			Globals.logString("FREE CONNECTIONS:"+freeConnections.size());
			Globals.logString("BUSY CONNECTIONS:"+busyConnections.size());

			//Make sure I have a dedicated transactional connection
			ConnectionWrapper connWrap = getConnectionWrapper_IfDedicated();
			if(connWrap == null){
				connWrap = lockConnectionWrapper();
			}
			
			Globals.logString("FREE CONNECTIONS:"+freeConnections.size());
			Globals.logString("BUSY CONNECTIONS:"+busyConnections.size());		
			
//		if(transaction_ShouldSurroundWithTransactionIfRequest && !transaction_Began){
//	    transaction_Began = true;
//    	beginTransaction();
//    }
		}
		*/
	}
	
	public void transaction_setShouldSurroundWithTransactionIfRequest(){
		/*
		ThreadDBTransactionData data = threadLocal_TransactionData.get();
		if(data == null){
			data = new ThreadDBTransactionData(this);
			threadLocal_TransactionData.set(data);
		}
		data.transaction_setShouldSurroundWithTransactionIfRequest();
		*/
	}
	
	public void transaction_SeeIfShouldCommit(){
		/*
		ThreadDBTransactionData transData = threadLocal_TransactionData.get();
//		Globals.logString("transaction_SeeIfShouldCommit - Point1");
		if(transData != null){
//			Globals.logString("transaction_SeeIfShouldCommit - Point2");
			if(transData.transaction_SeeIfShouldCommit()){
				Globals.logString("transaction_SeeIfShouldCommit");		
				Globals.logString("FREE CONNECTIONS:"+freeConnections.size());
				Globals.logString("BUSY CONNECTIONS:"+busyConnections.size());		
				
				getConnectionWrapper().commitTransaction();
				unlockConnectionWrapper(getConnectionWrapper());
				transData.dispose();
				threadLocal_TransactionData.remove();
//				commitTransaction();
				
				Globals.logString("FREE CONNECTIONS:"+freeConnections.size());
				Globals.logString("BUSY CONNECTIONS:"+busyConnections.size());
			}
		}
		*/
	}
	
	public String getDBSourceKey(){
		return getCredentials() != null ? getCredentials().getDbSourceKey() : null;
	}
	
	
	
	
	
  public Hashtable<String, String> newActualTables(Hashtable<String, String> exeTablesMap){
  	Hashtable<String, String> allTables = new Hashtable<String, String>();
    try {
    	Connection connection = getConnection();
    	if(connection != null){
	      DatabaseMetaData dmt = connection.getMetaData();
	      if (dmt != null) {
	        ResultSet resultSet = null;
	        if(getProvider() == DBManager.PROVIDER_ORACLE){
	        	//Instead of Username we should use the TABLESPACE Name
	        	resultSet = dmt.getTables(null, getCredentials().getUsername(), null, new String[] { "TABLE" });
	        }else{
	        	resultSet = dmt.getTables(null, null, null, new String[] { "TABLE" });
	        }
	        
	        if (resultSet != null) {
	          while (resultSet.next()) {
	            String tableName = resultSet.getString(3);
	            String namespace = resultSet.getString(2);
	            
	            Globals.logString(" TABLE : "+resultSet.getString(1)+" | "+namespace+" | "+tableName);
	          	
	            ConnectionCredentials credencials = getCredentials();
	          	String credentialsNamespace = credencials != null ? credencials.getNamespace() : null;
	          	if (Utils.isStringEmpty(credentialsNamespace) || namespace.equals(credentialsNamespace)) {
		            allTables.put(tableName, tableName);
		            if(exeTablesMap != null) exeTablesMap.put(tableName, tableName);
	          	}
	          }
	          resultSet.close();
	        }
	      }
	      releaseConnection(connection);
    	}
    } catch (Exception e) {
      Globals.logException(e);
    }
    return allTables;
  }

	
	
	
	public class ConnectionPoolThreadLocal extends ThreadLocal<ConnectionWrapper> {
		@Override
		public void remove() {
			super.remove();
		}
	}
}
