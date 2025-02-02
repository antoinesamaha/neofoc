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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.dbcp2.BasicDataSource;

import com.foc.ConfigInfo;
import com.foc.ConfigInfoWizardPanel;
import com.foc.Globals;
import com.foc.GuiConfigInfo;
import com.foc.performance.PerfManager;
import com.foc.util.Utils;

public class ConnectionWrapper {
	private ConnectionPool pool         = null; 
	//private Connection     connection   = null;
	private BasicDataSource apacheConnectionPool = null;
	private boolean        autoCommit   = true;
	private long           creationTime = 0;
	private long           expiryTime   = 0;
	
  private HashMap<StatementWrapper, StatementWrapper> busyStatements = null;
//  private ArrayList<StatementWrapper>                 freeStatements = null;

	public ConnectionWrapper(ConnectionPool pool){
		this.pool = pool;
    busyStatements = new HashMap<StatementWrapper, StatementWrapper>();
//    freeStatements = new ArrayList<StatementWrapper>();
    long duration = ConfigInfo.getDbConnectionDuration();
    if(duration > 0) {
    	creationTime = System.currentTimeMillis();
    	expiryTime   = creationTime + duration; 
    }
	}
	
	public void dispose(){
		if(busyStatements != null){
			for(int i=0; i>busyStatements.size(); i++){
				busyStatements.get(i).close();
			}
			busyStatements.clear();
			busyStatements = null;
		}
//		if(freeStatements != null){
//			for(int i=0; i>freeStatements.size(); i++){
//				freeStatements.get(i).close();
//			}
//			freeStatements.clear();
//			freeStatements = null;
//		}		
		closeConnection();
		pool = null;
	}
	
	public boolean hasBusyStatements() {
		return busyStatements != null && busyStatements.size() > 0; 
	}
	
	public boolean exceededExpiryTime() {
		return expiryTime > 0 && System.currentTimeMillis() > expiryTime;
	}
	
	public void releaseConnection(Connection connection){
		if(connection != null) {
			try{
				Globals.logString("  << << JDBC ACTIVE CONNECTIONS - Closing = "+apacheConnectionPool.getNumActive());
				connection.close();				
			}catch (Exception e){
				Globals.logException(e);
			}
		}
	}
	
	public Connection getConnection(){
  	if(apacheConnectionPool == null){
  		openConnection();
  		setAutoCommit(autoCommit);
  	}
  	Connection conn = null;
		try{
			String threadID = Thread.currentThread() != null ? String.valueOf(Thread.currentThread().getId()) : "--";
			Globals.logString(threadID+"  >> >> JDBC ACTIVE CONNECTIONS - Opening = "+apacheConnectionPool.getNumActive());
			conn = apacheConnectionPool.getConnection();
		}catch (Exception e){
			Globals.logException(e);
		}
		return conn;
	}
	
	public StringBuffer getMonitoringText() {
		StringBuffer buffer = new StringBuffer();
		String sourceKey = getDBSourceKey();
		
		if(Utils.isStringEmpty(sourceKey)) sourceKey = "Main";
//		if (freeStatements != null && busyStatements != null) {
//			buffer.append("DB pool: <b>" + sourceKey + "</b> locked: <b>" + busyStatements.size() + "</b> free: <b>" + freeStatements.size() + "</b><br>");
//		}
		return buffer;
	}
	
	public void setAutoCommit(boolean autoCommit){
		this.autoCommit = autoCommit;
		try{
			apacheConnectionPool.setDefaultAutoCommit(autoCommit);
			if(!autoCommit){
				getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			}
		}catch (SQLException e){
			Globals.logException(e);
		}
	}
	
	/*
	public boolean isAutoCommit(){
		try{
			return getConnection().getAutoCommit();
		}catch (SQLException e){
			Globals.logException(e);
		}
		return false;
	}
	*/
	
	public ConnectionPool getPool(){
		return pool;
	}
	
//	public DBManagerServer getDBManagerServer(){
//		return getPool() != null ? getPool().getDbManagerServer() : null;
//	}
	
	public int getProvider(){
		return getPool() != null ? getPool().getProvider() : 0;
	}
	
	public boolean isValid() {
		boolean valid = false;
		try {
			valid = getConnection().isValid(5);
		} catch(Exception e) {
			Globals.logException(e);
		}
		return valid;
	}
	
  public StatementWrapper lockStatement() {
  	StatementWrapper stmtWrapper = null;
//    if (freeStatements.size() > 0) {
//    	stmtWrapper = (StatementWrapper) freeStatements.get(0);
//      freeStatements.remove(0);
//    } else {
      try {
      	Connection conn = getConnection();
      	Statement stm = conn.createStatement();
      	stmtWrapper = new StatementWrapper(this, stm);
      	stmtWrapper.setConnection(conn);
      } catch (Exception e) {
        Globals.logException(e);
      }
//    }
//    if (stmtWrapper != null) {
//      busyStatements.put(stmtWrapper, stmtWrapper);
//    }
    
    return stmtWrapper;
  }
  
  public void unlockStatement(StatementWrapper stmtWrapper) {
    if (stmtWrapper != null) {
      busyStatements.remove(stmtWrapper);
//      if(freeStatements.size() < 0){
//      	freeStatements.add(stmtWrapper);	
//      }else{
      	try{
      		stmtWrapper.close();
      		Connection conn = stmtWrapper.getConnection();
      		releaseConnection(conn);
      	}catch(Exception e){
      		Globals.logException(e);
      	}
//      }
    }
  }
	
  private synchronized void openConnection() {
  	ConnectionCredentials credentials = getPool() != null ? getPool().getCredentials() : null;
    try {
      apacheConnectionPool = new BasicDataSource();

     	apacheConnectionPool.setUsername(credentials.getUsername());
     	apacheConnectionPool.setPassword(credentials.getPassword());
     	apacheConnectionPool.setConnectionProperties("characterEncoding=UTF-8;");//utf8

      apacheConnectionPool.setDriverClassName(credentials.getDrivers());
      apacheConnectionPool.setUrl(credentials.getUrl());
      apacheConnectionPool.setInitialSize(1);

      String maxStr = ConfigInfo.getProperty("jdbc.connections.maxtotal");
      int maxtotal = Utils.parseInteger(maxStr, 10);

      String maxIdleStr = ConfigInfo.getProperty("jdbc.connections.maxidle");
      int maxidle = Utils.parseInteger(maxIdleStr, 1);

      String maxWaitStr = ConfigInfo.getProperty("jdbc.connections.maxwaitmillis");
      int maxWait = Utils.parseInteger(maxWaitStr, -1);

      apacheConnectionPool.setMaxIdle(maxidle);
      apacheConnectionPool.setMaxTotal(maxtotal);
      if(maxWait > 0) {
      	apacheConnectionPool.setMaxWaitMillis(maxWait);
      }
      /*
      Globals.logString("drivers "+credentials.getDrivers());
      Class.forName(credentials.getDrivers()).newInstance();
      
      Properties props = new Properties();
      props.put("user", credentials.getUsername());
      props.put("password", credentials.getPassword());
      props.put("characterEncoding", "UTF-8");
      
      Globals.logString("Connection URL "+credentials.getUrl());
      connection = DriverManager.getConnection(credentials.getUrl(), props);
      */
      
      
      
    } catch (Exception e) {
    	String message = "Could not connect to Database: "+(credentials.getUrl() != null ? credentials.getUrl() : "")+"\nwith Username: "+(credentials.getUsername() != null ? credentials.getUsername() : ""); 
      if(Globals.getApp().isWithRegistry() && Globals.getDisplayManager() != null){
      	message += "\nYou will be redirected to change Environment";
      	Globals.getDisplayManager().popupMessage(message);
	      ConfigInfoWizardPanel panel = new ConfigInfoWizardPanel(new GuiConfigInfo(), ConfigInfoWizardPanel.STATE_DIRECTORY);
	      panel.setWithRestart(true);
	      Globals.getDisplayManager().popupDialog(panel, "", true);    
      }else{
	    	if(Globals.getDisplayManager() != null){
	    		Globals.getDisplayManager().popupMessage(message);
	    	}
	      Globals.logException(e);
	      System.exit(0);
      }
    }
  }

  private synchronized void closeConnection() {
    try {
    	if(apacheConnectionPool != null){
    		destroyBusyStatments();
//	      destroyFreeStatments();
	      apacheConnectionPool.close();
	      apacheConnectionPool = null;
    	}
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
	
  public void refreshConnection(){
  	closeConnection();
  	openConnection();
  }

	public void destroyBusyStatments(){
		if(busyStatements != null){
			Iterator<StatementWrapper> iter = (Iterator<StatementWrapper>) busyStatements.values().iterator();
			while(iter.hasNext()){
				StatementWrapper stmt = iter.next();
				if(stmt != null){
					stmt.close();
				}
			}
			busyStatements.clear();
		}
	}
	
//  public void destroyFreeStatments(){
//    if(freeStatements != null){
//      for(int i=freeStatements.size()-1; i>=0; i--){
//        Statement stm = (Statement) freeStatements.get(i);
//        try{
//          stm.close();
//          freeStatements.remove(i);
//        }catch(Exception e){
//          Globals.logException(e);
//        }
//      }
//    }
//  }

  public synchronized void commitTransaction(){
    try{
    	Globals.logString("Transaction Commit");
    	PerfManager.start("Commit_Transaction");
      getConnection().commit();
      PerfManager.end();
    }catch(Exception e){
      Globals.logException(e);
//      Globals.getApp().exit();
    }
  }

  /*
  public synchronized void rollbackTransaction(Savepoint savepoint){
    try{
      if(nbrOfBeginTransactionsInTransactionThread > 0 && transactionThread == Thread.currentThread()){
        nbrOfBeginTransactionsInTransactionThread--;
        if(savepoint != null){
        	getConnection().rollback(savepoint);
        }
      }else{
        while(getConnection().getAutoCommit()){
          wait();
        }      
        getConnection().rollback();
        getConnection().setAutoCommit(true);
        transactionThread = null;
        notifyAll();
      }
    }catch(Exception e){
      Globals.logException(e);
      Globals.getApp().exit();
    }
  }
  */
  
  public String getDBSourceKey(){
  	return pool != null ? pool.getDBSourceKey() : null;
  }
}
