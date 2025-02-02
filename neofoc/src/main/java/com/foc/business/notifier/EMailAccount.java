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
package com.foc.business.notifier;

import com.foc.annotations.model.FocChoice;
import com.foc.annotations.model.FocEntity;
import com.foc.annotations.model.fields.FocBoolean;
import com.foc.annotations.model.fields.FocInteger;
import com.foc.annotations.model.fields.FocMultipleChoice;
import com.foc.annotations.model.fields.FocPassword;
import com.foc.annotations.model.fields.FocString;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.parsers.ParsedFocDesc;
import com.foc.desc.parsers.pojo.PojoFocObject;
import com.foc.list.FocList;

@FocEntity
public class EMailAccount extends PojoFocObject {

  public static final int ENCRYPTION_TYPE_NONE = 0;
  public static final int ENCRYPTION_TYPE_SSL  = 1;
  public static final int ENCRYPTION_TYPE_TLS  = 2;

  public static final String DBNAME = "EMailAccount";
  
	@FocMultipleChoice(size = 2, choices= {
			@FocChoice(id=ENCRYPTION_TYPE_NONE, title="None"),
			@FocChoice(id=ENCRYPTION_TYPE_SSL, title="SSL"),
			@FocChoice(id=ENCRYPTION_TYPE_TLS, title="TLS")
	})
	public static final String FIELD_EncryptionType = "EncryptionType";
	
	@FocString(size = 200)
	public static final String FIELD_Host = "Host";
	
	@FocInteger(size = 10)
	public static final String FIELD_Port = "Port";
	
	@FocString(size = 200)
	public static final String FIELD_Username = "Username";
	
	@FocPassword(size = 200)
	public static final String FIELD_Password = "Password";
	
	@FocString(size = 250)
	public static final String FIELD_Sender = "Sender";
	
	@FocBoolean()
	public static final String FIELD_Default = "Default";

  public EMailAccount(FocConstructor constr) {
		super(constr);
  }
  
	public static ParsedFocDesc getFocDesc() {
		return ParsedFocDesc.getInstance(DBNAME);
	}
	
	public int getEncryptionType() {
		return getPropertyInteger(FIELD_EncryptionType);
	}

	public void setEncryptionType(int value) {
		setPropertyInteger(FIELD_EncryptionType, value);
	}

	public String getHost() {
		return getPropertyString(FIELD_Host);
	}

	public void setHost(String value) {
		setPropertyString(FIELD_Host, value);
	}

	public int getPort() {
		return getPropertyInteger(FIELD_Port);
	}

	public void setPort(int value) {
		setPropertyInteger(FIELD_Port, value);
	}

	public String getUsername() {
		return getPropertyString(FIELD_Username);
	}

	public void setUsername(String value) {
		setPropertyString(FIELD_Username, value);
	}

	public String getPassword() {
		return getPropertyString(FIELD_Password);
	}

	public void setPassword(String value) {
		setPropertyString(FIELD_Password, value);
	}

	public String getSender() {
		return getPropertyString(FIELD_Sender);
	}

	public void setSender(String value) {
		setPropertyString(FIELD_Sender, value);
	}
	
	public boolean isDefault() {
		return getPropertyBoolean(FIELD_Default);
	}

	public void setDefault(boolean value) {
		setPropertyBoolean(FIELD_Default, value);
	}

  public static EMailAccount getInstance(){
  	EMailAccount account = null;
  	FocDesc focDesc = getFocDesc();
  	if(focDesc != null) {
  		FocList list = focDesc.getFocList();
  		if(list != null) {
  			list.loadIfNotLoadedFromDB();
  			account = (EMailAccount) list.searchByPropertyBooleanValue(FIELD_Default, true);
  		}
  	}
  	return account;
  }
}
