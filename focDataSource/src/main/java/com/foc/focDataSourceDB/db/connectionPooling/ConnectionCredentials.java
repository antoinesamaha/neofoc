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

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.StringTokenizer;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.api.IFocEncryptor;
import com.foc.db.DBManager;

public class ConnectionCredentials {
	private String dbSourceKey = null;
	
  private String drivers   = null;
  private String url       = null;
  private String username  = null;
  private String password  = null;
  private String xpassword = null;      
  private int    serverVersion = 0;
  private String namespace = null;
  
  private int    provider  = DBManager.PROVIDER_MYSQL;

  public ConnectionCredentials(){
    drivers   = null;
    url       = null;
    username  = null;
    password  = null;
    xpassword = null;
    serverVersion = 0;
    namespace = null;
  }
  
  public void dispose(){
    drivers   = null;
    url       = null;
    username  = null;
    password  = null;
    xpassword = null;
    namespace = null;
  }
  
  private void adjustProvider(){
  	if(url != null){
			if(url.startsWith("jdbc:sqlserver")){
				setProvider(DBManager.PROVIDER_MSSQL);
			}else if(url.startsWith("jdbc:oracle:thin")){
				setProvider(DBManager.PROVIDER_ORACLE);
			}else if(url.startsWith("jdbc:h2")){
				setProvider(DBManager.PROVIDER_H2);
			}else if(url.startsWith("jdbc:postgresql")){
				setProvider(DBManager.PROVIDER_POSTGRES);				
			}else{
				setProvider(DBManager.PROVIDER_MYSQL);
			}
  	}
  }
  
  public void fillCredentialsIfNeeded(){
  	setDrivers(ConfigInfo.getJdbcDrivers());
    setUrl(ConfigInfo.getJdbcURL());
    setUsername(ConfigInfo.getJdbcUserName());
    setPassword(ConfigInfo.getJdbcPassword());
    setXpassword(ConfigInfo.getJdbcXPassword());
    setServerVersion(ConfigInfo.getJdbcServerVersion());
    setNamespace(ConfigInfo.getJdbcNamespace());
    
    StringTokenizer tokenizer = url != null ? new StringTokenizer(url, ":", false) : null;
    for(int count = 0; count < 2 && tokenizer.hasMoreTokens(); count++){
      String token = tokenizer.nextToken();

      if(count == 1){
        if (token.equals("oracle")){
          setProvider(DBManager.PROVIDER_ORACLE);
        }else if (token.equals("mysql")){
          setProvider(DBManager.PROVIDER_MYSQL);
        }
      }
    }

    String dbHost = Globals.getApp().getCommandLineArgument("dbHost");
    if(dbHost != null && !dbHost.isEmpty()){
    	if(getProvider() == DBManager.PROVIDER_MYSQL){
    		String newURL  = url.substring(0, 13);
    		int    indexOf = url.indexOf(':', 14); 
    		newURL += dbHost;
    		newURL += url.substring(indexOf);
    		url = newURL;
    	}else if(getProvider() == DBManager.PROVIDER_ORACLE){
    		String newURL  = url.substring(0, 17);
    		int    indexOf = url.indexOf(':', 18); 
    		newURL += dbHost;
    		newURL += url.substring(indexOf);
    		url = newURL;
    	}
    }

    String dbPort = Globals.getApp().getCommandLineArgument("dbPort");
    if(dbPort != null && !dbPort.isEmpty()){
    	if(getProvider() == DBManager.PROVIDER_MYSQL){
    		int    indexOf2Points = url.indexOf(':', 14); 
    		int    indexOfSlash   = url.indexOf('/', indexOf2Points);
    		String newURL = url.substring(0, indexOf2Points+1);      		
    		newURL += dbPort.trim();
    		newURL += url.substring(indexOfSlash);
    		url = newURL;
    	}else if(getProvider() == DBManager.PROVIDER_ORACLE){
    		int    indexOf2Points = url.indexOf(':', 18);
    		int    indexOfSlash   = url.indexOf('/', indexOf2Points+1);
    		String newURL  = url.substring(0, indexOf2Points);
    		newURL += dbPort.trim();
    		newURL += url.substring(indexOfSlash);
    		url = newURL;
    	}
    }

    String dbSchema = Globals.getApp().getCommandLineArgument("dbSchema");
    if(dbSchema != null && !dbSchema.isEmpty()){
    	if(getProvider() == DBManager.PROVIDER_MYSQL){
    		int    lastSlash = url.lastIndexOf('/');
    		if(lastSlash > 0){
      		String newURL    = url.substring(0, lastSlash+1);
      		newURL += dbSchema;
      		url = newURL;
    		}
    	}else if(getProvider() == DBManager.PROVIDER_ORACLE){
    		int    lastSlash = url.lastIndexOf(':');
    		if(lastSlash > 0){
      		String newURL    = url.substring(0, lastSlash+1);
      		newURL += dbSchema;
      		url = newURL;
    		}
    	}
    }
    
    Globals.logString("Connecting to url : "+url+" user : "+username);
  }

	public int getProvider() {
		return provider;
	}

	public void setProvider(int provider) {
		this.provider = provider;
	}

	public String getDrivers() {
		return drivers;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		String returnedPassword = null;
		if(password == null) {
			returnedPassword = decrypt(xpassword);
		} else {
			returnedPassword = password;
			Globals.logString("PWD:["+encrypt(returnedPassword)+"]");
		}
		return returnedPassword;
	}

	public String getXpassword() {
		return xpassword;
	}

	public String getDbSourceKey() {
		return dbSourceKey;
	}

	public void setDbSourceKey(String dbSourceKey) {
		this.dbSourceKey = dbSourceKey;
	}

	public void setDrivers(String drivers) {
		this.drivers = drivers;
	}

	public void setUrl(String url) {
		this.url = url;
		adjustProvider();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setXpassword(String xpassword) {
		this.xpassword = xpassword;
	}

	public void setServerVersion(int serverVersion) {
		this.serverVersion = serverVersion;
	}
	
	public int getServerVersion() {
		return serverVersion;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	private String decrypt(String str) {
		String b2Str = null;
		IFocEncryptor fileEncryptor = Globals.getApp().getEncryptor();
		if(fileEncryptor != null) {
			byte[] b = str.getBytes(Charset.forName("UTF-8"));
			try {
				b = Base64.getDecoder().decode(b);
				byte[] b2 = fileEncryptor.decrypt(-1, b);
				b2Str = new String(b2, Charset.forName("UTF-8"));
			} catch (Exception e) {
				Globals.logException(e);
			}
		}
		return b2Str;
	}

	private String encrypt(String str) {
		String b2Str = null;
		IFocEncryptor fileEncryptor = Globals.getApp().getEncryptor();
		if(fileEncryptor != null) {
			byte[] b = str.getBytes(Charset.forName("UTF-8"));
			try {
				byte[] b2 = fileEncryptor.encrypt(-1, b);
				b2 = Base64.getEncoder().encode(b2);
				b2Str = new String(b2, Charset.forName("UTF-8"));
			} catch (Exception e) {
				Globals.logException(e);
			}
		}
		return b2Str;
	}
}
