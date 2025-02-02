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
package com.foc.email;

@Deprecated
public class EMailAccount {

  public static final int ENCRYPTION_TYPE_NONE = 0;
  public static final int ENCRYPTION_TYPE_SSL  = 1;
  public static final int ENCRYPTION_TYPE_TLS  = 2;

  private int encryptionConnectionType = ENCRYPTION_TYPE_SSL;

  private String host = "email-smtp.us-east-1.amazonaws.com";
  // private int port = 587;
  private int port = 465;//  Outgoing server (SMTP) 25, 587, or 2587 (to connect using STARTTLS), or 465 or 2465 (to connect using TLS Wrapper).
  private String username = "AKIAIVMOV3G7F55RILIQ";
  private String password = "Ajmnx7ON3LoKYMtFdbx+LPqls3HnArYkGlJ96/42qFKu";
  private String sender   = "01barmaja@01barmaja.com";
  
  public EMailAccount(){
  }
  
	public int getEncryptionConnectionType() {
		return encryptionConnectionType;
	}
	
	public void setEncryptionConnectionType(int encryptionConnectionType) {
		this.encryptionConnectionType = encryptionConnectionType;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	private static EMailAccount account = null;
  public static EMailAccount getInstance(){
  	if(account == null){
  		account = new EMailAccount();
  	}
  	return account;
  }

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}
