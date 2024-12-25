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
package com.foc.pivot;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.property.FDouble;
import com.foc.property.FInt;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;

@SuppressWarnings("serial")
public class FPivotRow extends FocObject implements FPivotConst {
  
  public FPivotRow(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public void dispose() {
    super.dispose();
  }

  public String getTitle(){
    return getPropertyString(FLD_PVT_ROW_TITLE);
  }
  
  public void setTitle(String title){
    setPropertyString(FLD_PVT_ROW_TITLE, title);
  }

  public String getFullTitle(){
    return getPropertyString(FLD_PVT_ROW_FULL_TITLE);
  }
  
  public void setFullTitle(String title){
    setPropertyString(FLD_PVT_ROW_FULL_TITLE, title);
  }

  public String getGroupByValue(){
    return getPropertyString(FLD_PVT_ROW_GROUP_BY);
  }
  
  public void setGroupByValue(String title){
    setPropertyString(FLD_PVT_ROW_GROUP_BY, title);
  }
  
  public String getSortByValue(){
    return getPropertyString(FLD_PVT_ROW_SORT_BY);
  }
  
  public void setSortByValue(String title){
    setPropertyString(FLD_PVT_ROW_SORT_BY, title);
  }
  
  public FocObject getPivotRowObject() {
    return getPropertyObject(FLD_PVT_ROW_OBJECT);
  }

  public void setPivotRowObject(FocObject object) {
    setPropertyObject(FLD_PVT_ROW_OBJECT, object);
  }
  
  public FPivotBreakdown getPivotBreakdownStart() {
    return (FPivotBreakdown) getPropertyObject(FLD_PVT_ROW_START_BKDN);
  }
  
  public void setPivotBreakdownStart(FPivotBreakdown startBkdn){
    setPropertyObject(FLD_PVT_ROW_START_BKDN, startBkdn);
  }
  
  public FPivotBreakdown getPivotBreakdownEnd() {
    return (FPivotBreakdown) getPropertyObject(FLD_PVT_ROW_END_BKDN);
  }
  
  public void setPivotBreakdownEnd(FPivotBreakdown endBkdn){
    setPropertyObject(FLD_PVT_ROW_END_BKDN, endBkdn);
  }
  
  public boolean isRoot(){
  	return getFatherObject() == null;
  }
  
  public FProperty getPropertyForColumn(String dataPath){
    FProperty property = null;
    IFocData data = iFocData_getDataByPath(dataPath);
    if(data instanceof FProperty){
      property = (FProperty) data;
    }
    return property;
  }
  
  public double getDoubleforColumn(FPivotValue column){
  	double value = 0;
    String dataPath = column.getDatapath();
    
    FProperty currentProperty = getPropertyForColumn(dataPath);
    if(currentProperty == null){
    	String formula = column.getFormula();
			Object obj = computeFormula(formula);
			
			if(obj instanceof Double){
				value = ((Double)obj).doubleValue(); 
			}else if(obj instanceof Integer){
				value = ((Integer)obj).doubleValue();
			}
    }else if (currentProperty instanceof FDouble || currentProperty instanceof FInt) {
    	value = currentProperty.getDouble();
    }
    return value;
  }
  
  public void addDoubleInColumn(String dataPath, Double value){
    FProperty temp = getPropertyForColumn(dataPath);
    if(temp != null){
      Double tempDouble = temp.getDouble();
      tempDouble += value;
      temp.setDouble(tempDouble);
    }
  }
  
  public void setStringInColumn(String dataPath, String value){
    FProperty temp = getPropertyForColumn(dataPath);
    if(temp != null){
      temp.setString(value);
    }
  }
  
  public boolean isBreakdown_Descendent(String groupBy){
  	boolean isDescendent = false;
		FPivotRow currNode = this;
		while(!isDescendent && currNode != null){
			if(currNode.isBreakdown(groupBy)){
				isDescendent = true;
			}
			currNode = (FPivotRow) currNode.getFatherObject();
		}
		return isDescendent;
  }
  
  public boolean isBreakdown(String groupBy){
  	boolean isDescendent = false;
		FPivotRow curr = this;
		if(curr != null && curr.getPivotBreakdownEnd() != null && curr.getPivotBreakdownEnd().getGroupBy().equals(groupBy)){
			isDescendent = true;
		}
		return isDescendent;
  }
}
