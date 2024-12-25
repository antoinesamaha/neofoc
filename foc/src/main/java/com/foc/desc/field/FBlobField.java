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

import java.awt.Component;
import java.sql.Blob;
import java.sql.Types;

import com.fab.model.table.FieldDefinition;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.list.filter.FilterCondition;
import com.foc.property.FBlobProperty;
import com.foc.property.FProperty;

public class FBlobField extends FField{
  
  public FBlobField(String name, String title, int id, boolean key) {
    super(name, title, id, key, 0, 0);
    setIncludeInDBRequests(false);
  }

  @Override
 	public int getFabType() {
 	  return FieldDefinition.SQL_TYPE_ID_BLOB_FILE;//The image field will override to make it more specific
 	}

  public static int SqlType() {
    return Types.BLOB;
  }
  
  public int getSqlType() {
    return SqlType();
  }

  public String getCreationString(String name) {
    if (getProvider()== DBManager.PROVIDER_ORACLE){
      return " " + name + " BLOB";
    }else if(getProvider()== DBManager.PROVIDER_POSTGRES) {
    	return " " + name + " BYTEA";
    }else if (getProvider()== DBManager.PROVIDER_MSSQL){
    	return " " + name + " varbinary";
    }else{
      return " " + name + " BLOB";
    }
  }
 
  public Component getGuiComponent(FProperty prop){
    return null;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    return null;
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }
  
  @Override
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
    return null;
  }
  
  @Override
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue) {
    return new FBlobProperty(masterObj, getID(), (Blob)defaultValue);
  }
  
  @Override
  public FProperty newProperty_ToImplement(FocObject masterObj) {
    return newProperty(masterObj, null);
  }
}
