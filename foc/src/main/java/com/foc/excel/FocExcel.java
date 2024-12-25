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
package com.foc.excel;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class FocExcel extends FocObject {

  public FocExcel(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public String getExcelFileContents(){
    return getPropertyString(FocExcelDesc.FLD_EXCEL_FILE_CONTENTS);
  }
  
  public void setExcelFieldContents(String fileContents){
    setPropertyString(FocExcelDesc.FLD_EXCEL_FILE_CONTENTS, fileContents);
  }
  
  public String getTableName(){
    return getPropertyString(FocExcelDesc.FLD_TABLE_NAME);
  }
  
  public void setTableName(String tableName){
    setPropertyString(FocExcelDesc.FLD_TABLE_NAME, tableName);
  }
}
