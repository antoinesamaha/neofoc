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

@SuppressWarnings("serial")
public class FPivotValue extends FocObject implements FPivotConst {
	
	public FPivotValue(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public FPivotValue(FocConstructor constr, String title, String dataPath, String computeLevel) {
	  super(constr);
	  newFocProperties();
	  
	  setTitle(title);
	  setDatapath(dataPath);
	  setComputeLevel(computeLevel);
	}
	
	public void dispose(){
		super.dispose();
	}
	
	public String getTitle(){
	  return getPropertyString(FLD_VALUE_TITLE);
	}
	
	public void setTitle(String title){
	  setPropertyString(FLD_VALUE_TITLE, title);
	}
	
	public String getDatapath(){
	  return getPropertyString(FLD_VALUE_DATAPATH);
	}
	
	public void setDatapath(String datapath){
	  setPropertyString(FLD_VALUE_DATAPATH, datapath);
	}
	
	public String getComputeLevel(){
	  return getPropertyString(FLD_VALUE_COMPUTE_LEVEL);
	}
	
	public void setComputeLevel(String computeLevel){
	  setPropertyString(FLD_VALUE_COMPUTE_LEVEL, computeLevel);
	}
	
	public String getFormula(){
	  return getPropertyString(FLD_VALUE_FORMULA);
	}
	
	public void setFormula(String formula){
	  setPropertyString(FLD_VALUE_FORMULA, formula);
	}

	public int getAggregationFormula(){
		return getPropertyInteger(FLD_VALUE_AGGREGATION_FORMULA);
	}
	
	public void setAggregationFormula(int formula){
		setPropertyInteger(FLD_VALUE_AGGREGATION_FORMULA, formula);
	}
	
	public void setAggregationFormula(String formula){
		setPropertyString(FLD_VALUE_AGGREGATION_FORMULA, formula);
	}
}
