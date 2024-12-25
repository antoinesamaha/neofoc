/*
 * Created on 17 feb. 2004
 */
package com.foc.db;

import java.util.*;

import com.foc.*;
import com.foc.db.sync.SyncManager;
import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class DBManager {

	public static final String BACKUP_FILE_EXTENSION = "fbk";
	
	private SyncManager          syncManager   = null;
    
  private ArrayList<FocObject> lockedObjects = null;

//  private int provider = 1;

  public static final int PROVIDER_NONE       = 0;
  public static final int PROVIDER_MYSQL      = 1;
  public static final int PROVIDER_HSQLDB     = 2;
  public static final int PROVIDER_ORACLE     = 3;
  public static final int PROVIDER_MSSQL      = 4;
  public static final int PROVIDER_ORACLE_OLD = 5;
  public static final int PROVIDER_H2         = 6;
  public static final int PROVIDER_POSTGRES   = 7;

  private String INDEX_NAME_IDENTIFIER = "IDENTIFIER";
  
  private int syncMode = 0;
  
  public static final int SYNC_MODE_NONE   = 0;
  public static final int SYNC_MODE_SERVER = 1;
  public static final int SYNC_MODE_REMOTE = 2;
  
  public DBManager(){
  	init();
  }
  
  public java.sql.Date getCurrentDate(){
  	return getCurrentTimeStamp_AsTime();
  }

  public java.sql.Date getCurrentTimeStamp_AsTime(){
  	java.sql.Date date = null;
  	if(Globals.getApp() != null && Globals.getApp().isWebServer()){
  		date = Globals.getApp().getSystemDate();
  	}else{
  		date = Globals.getApp().getDataSource().command_GetCurrentTimeStamp();
  	}
  	return date;
  }

  public void exit(){
    unlockAll();
  }
  
  private void init(){
  	String sMode = ConfigInfo.getSyncMode();
  	if(sMode.toUpperCase().startsWith("N")){
  		sync_setMode(SYNC_MODE_NONE);
  	}else if(sMode.toUpperCase().startsWith("S")){
  		sync_setMode(SYNC_MODE_SERVER);
  	}else if(sMode.toUpperCase().startsWith("R")){
  		sync_setMode(SYNC_MODE_REMOTE);
  	}
  	
//  	if(ConfigInfo.getJdbcURL().startsWith("jdbc:sqlserver")){
//  		setProvider(PROVIDER_MSSQL);
//  	}else if(ConfigInfo.getJdbcURL().startsWith("jdbc:oracle:thin")){
//  		setProvider(PROVIDER_ORACLE);
//  	}else if(ConfigInfo.getJdbcURL().startsWith("jdbc:h2")){
//  		setProvider(PROVIDER_H2);
//  	}
  }
  
  public boolean isProviderSupportNullValues(){
  	return getProvider() == PROVIDER_MSSQL;
  }
  
  public boolean adaptTable(FocDesc focDesc){
  	return Globals.getApp().getDataSource().command_AdaptDataModel_SingleTable(focDesc);
  }

  public void addLockedObject(FocObject obj){
    if(lockedObjects == null){
      lockedObjects = new ArrayList<FocObject>();
    }
    lockedObjects.add(obj);
  }

  public void removeLockedObject(FocObject obj){
    if(lockedObjects != null){
      lockedObjects.remove(obj);
    }
  }
  
  public boolean isObjectLockedByThisSession(FocObject obj){
    return lockedObjects.contains(obj);
  }

  public void unlockAll(){
    if(lockedObjects != null){
      for(int i=lockedObjects.size()-1; i>=0; i--){
        FocObject obj = (FocObject) lockedObjects.get(i);
        obj.unlock();
      }
    }
  }

	public int getProvider(String dbSourceKey) {
		return Globals.getApp().getDataSource().getProvider(dbSourceKey);
	}
	
	public int getProvider() {
		return getProvider(null);
	}

	public int getServerVersion(String dbSourceKey) {
		return Globals.getApp().getDataSource().getServerVersion(dbSourceKey);
	}
	
	public int getServerVersion() {
		return getServerVersion(null);
	}
	
//	public void setProvider(int provider) {
//		this.provider = provider;
//		if(provider == PROVIDER_ORACLE || provider == PROVIDER_H2){
//			setINDEX_NAME_IDENTIFIER("ID");
//		}
//	}
	
	public SyncManager getSyncManager(){
		if(syncManager == null){
			syncManager = new SyncManager();
		}
		return syncManager;
	}
	
	// ------------------------------------------------
	// ------------------------------------------------
	// SYNC
	// ------------------------------------------------
	// ------------------------------------------------
	private int sync_getMode(){
		return syncMode;
	}

	public void sync_setMode(int mode){
		syncMode = mode;
	}

	public boolean sync_isServer(){
		return sync_getMode() == SYNC_MODE_SERVER;
	}

	public boolean sync_isRemote(){
		return sync_getMode() == SYNC_MODE_REMOTE;
	}

	public boolean sync_isNone(){
		return sync_getMode() == SYNC_MODE_NONE;
	}

	public String getINDEX_NAME_IDENTIFIER() {
		return INDEX_NAME_IDENTIFIER;
	}

	public void setINDEX_NAME_IDENTIFIER(String iNDEX_NAME_IDENTIFIER) {
		INDEX_NAME_IDENTIFIER = iNDEX_NAME_IDENTIFIER;
	}
	// ------------------------------------------------
	// ------------------------------------------------
	
	public static boolean provider_TableNamesBetweenSpeachmarks(int provider){
		return provider == DBManager.PROVIDER_ORACLE 
				|| provider == DBManager.PROVIDER_H2
				|| provider == DBManager.PROVIDER_POSTGRES
				; 
	}
	
	public static boolean provider_FieldNamesBetweenSpeachmarks(int provider){
		return provider == DBManager.PROVIDER_ORACLE 
				|| provider == DBManager.PROVIDER_H2 
				|| provider == DBManager.PROVIDER_POSTGRES;
	}
	
	public static String provider_ConvertFieldName(int provider, String fieldName){
		String newFieldName = fieldName;
	  if(DBManager.provider_TableNamesBetweenSpeachmarks(provider)){
	  	if(fieldName.contains(".")){
	  		int idx = fieldName.indexOf(".");
	  		if(idx > 0){
	  			newFieldName = fieldName.substring(0, idx+1) +"\""+ fieldName.substring(idx+1) +"\"";
	  		}
	  	}else{
	  		newFieldName = "\"" + fieldName + "\"";
	  	}
		}
	  return newFieldName;
	}
}
