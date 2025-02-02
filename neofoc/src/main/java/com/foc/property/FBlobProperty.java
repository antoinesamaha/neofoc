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
package com.foc.property;

import java.io.File;
import java.io.InputStream;
import java.sql.Blob;

import com.foc.Globals;
import com.foc.db.DBManager;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class FBlobProperty extends FProperty{

	private InputStream inputStream = null;
  private File file = null;
  
  public FBlobProperty(FocObject focObj, int fieldID, Blob defaultValue) {
    super(focObj, fieldID);
  }
  
  public void setFile(File file){
    try{
      this.file = file;
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  public long getFileLength(){
    return file.length();
  }
  
  @Override
  public String getSqlString() {
  	if(getProvider() == DBManager.PROVIDER_MSSQL){
  		String sqlStr = new String(getString() != null ? getString() : "");
  		sqlStr = "CAST('" + sqlStr + "\' AS VARBINARY(MAX))";//CAST('wahid' AS VARBINARY(MAX))
  		return sqlStr;
  	}else if(			getProvider() == DBManager.PROVIDER_ORACLE
  						||  getProvider() == DBManager.PROVIDER_H2
  						||  getProvider() == DBManager.PROVIDER_POSTGRES){
  		return "\'" + getString() + "\'";
  	}else{
  		return "\"" + getString() + "\"";
  	}
  }

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
  
}
