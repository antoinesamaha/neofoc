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
package b01.officeLink.excel.synchronize;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.property.FProperty;

public class ExcelColumn extends FocObject {

  public ExcelColumn(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public String getFileHeader(){
  	return getPropertyString(ExcelColumnDesc.FLD_AUTOCAD_COL);
  }

  public void setFileHeader(String fileHeader){
  	setPropertyString(ExcelColumnDesc.FLD_AUTOCAD_COL, fileHeader);
  }

  public String getC3Header(){
  	return getPropertyString(ExcelColumnDesc.FLD_C3_COL);
  }

  public void setC3Header(String c3Header){
  	setPropertyString(ExcelColumnDesc.FLD_C3_COL, c3Header);
  }

  public int getFieldMode(){
  	return getPropertyMultiChoice(ExcelColumnDesc.FLD_FIELD_MODE);
  }

  public void setFieldMode(int choice){
  	setPropertyMultiChoice(ExcelColumnDesc.FLD_FIELD_MODE, choice);
  }

  public int getPosition(){
  	return getPropertyInteger(ExcelColumnDesc.FLD_POSITION);
  }
  
  public void setPosition(int position){
  	setPropertyInteger(ExcelColumnDesc.FLD_POSITION, position);
  }

  public boolean isMandatory(){
  	return getPropertyBoolean(ExcelColumnDesc.FLD_MANDATORY);
  }

  public void adjustPropertyLocks(){
  	boolean locked = getFieldMode() == ExcelColumnDesc.MODE_LEVEL || getFieldMode() == ExcelColumnDesc.MODE_REF;
  	FProperty prop = getFocProperty(ExcelColumnDesc.FLD_C3_COL);
  	if(prop != null){
  		prop.setValueLocked(locked);
  	}
  }
}
