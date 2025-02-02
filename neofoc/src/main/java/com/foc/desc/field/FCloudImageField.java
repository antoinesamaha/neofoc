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
import java.sql.Types;

import com.fab.model.table.FieldDefinition;
import com.foc.desc.FocObject;
import com.foc.property.FCloudImageProperty;
import com.foc.property.FProperty;

public class FCloudImageField extends FCloudStorageField{

	public FCloudImageField(String name, String title, int id, boolean key) {
		super(name, title, id, key, FField.NO_FIELD_ID);
	}
	
	public static int SqlType() {
    return Types.BLOB;
  }

  public int getSqlType() {
    return SqlType();
  }
  
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_IMAGE;
  }
  
  @Override
	public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue) {
		return new FCloudImageProperty(masterObj, getID(), (Blob) defaultValue);
	}

}
