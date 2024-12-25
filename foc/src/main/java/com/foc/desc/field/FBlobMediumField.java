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
package com.foc.desc.field;

import java.sql.Blob;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;
import com.foc.property.FBlobMediumProperty;
import com.foc.property.FProperty;

public class FBlobMediumField extends FBlobField{

  public FBlobMediumField(String name, String title, int id, boolean key) {
    super(name, title, id, key);
    setIncludeInDBRequests(false);
  }
  
  @Override
  public String getCreationString(String name) {
    if (getProvider()== DBManager.PROVIDER_ORACLE){
      return " \"" + name + "\" BLOB";
    }else if (getProvider()== DBManager.PROVIDER_POSTGRES){
      return " \"" + name + "\" BYTEA";
    }else if (getProvider()== DBManager.PROVIDER_MSSQL){
    	return " " + name + " varbinary(max)";
    }else{
      return " " + name + " MEDIUMBLOB";
    }
  }
  
  @Override
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue) {
    return new FBlobMediumProperty(masterObj, getID(), (Blob)defaultValue);
  }
  
  @Override
  public FProperty newProperty_ToImplement(FocObject masterObj) {
    return newProperty(masterObj, null);
  }

  /*
  @Override
  public FProperty newProperty(FocObject masterObj, Object defaultValue) {
    return new FBlobMediumProperty(masterObj, getID(), (Blob)defaultValue);
  }
  */
}
