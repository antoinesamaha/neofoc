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

import com.foc.desc.FocObject;
import com.foc.property.FCloudStorageProperty;
import com.foc.property.FProperty;

public class FCloudStorageField extends FBlobMediumField {

	private int    fileNameFieldID   = FField.NO_FIELD_ID;
	private String fileNameFieldName = null;
	
	public FCloudStorageField(String name, String title, int id, boolean key, int fileNameFieldID) {
		super(name, title, id, key);
		setDBResident(false);
		
		this.fileNameFieldID = fileNameFieldID;
	}
	
	public FCloudStorageField(String name, String title, int id, boolean key, String fileNameFieldString) {
		super(name, title, id, key);
		setDBResident(false);
		
		this.fileNameFieldName = fileNameFieldString;
	}
	
	public int getFileNameFieldID(){
		return fileNameFieldID;
	}
	
	public String getFileNameFieldName(){
		return fileNameFieldName;
	}
		
	@Override
	public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue) {
		return new FCloudStorageProperty(masterObj, getID(), (Blob) defaultValue);
	}
	
}
