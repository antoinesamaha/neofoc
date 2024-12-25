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

import java.sql.Date;

import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FDate;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class FPivotView extends FocObject implements FPivotConst {
  
	public FPivotView(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public void dispose(){
		super.dispose();
	}

	public FPivotBreakdown addBreakdown(FPivotRowTree pivotTree, String name, String dataPath, String isEntireTreeShown, String captionProperty, String descriptionProperty, String dateStart, String dateEnd, String hideWhenAlone, String titleWhenEmptyProperty, String descriptionWhenEmptyProperty){
    FPivotBreakdown breakdown = (FPivotBreakdown) getBreakdownList().newEmptyItem();
    breakdown.setPivotTree(pivotTree);

    if(dataPath != null && !dataPath.isEmpty()){
      breakdown.setGroupBy(dataPath);
      
      if(name != null && !name.isEmpty()){
      	breakdown.setName(name);
      }else{
      	breakdown.setName(dataPath);
      }
      
      if(isEntireTreeShown != null && isEntireTreeShown.equalsIgnoreCase("true")){        
        breakdown.setTreeShown(true);
      }
      else{
        breakdown.setTreeShown(false);
      }
      
      if(!Utils.isStringEmpty(captionProperty)){
        breakdown.setTitleCaption(captionProperty);
      }

      if(!Utils.isStringEmpty(descriptionProperty)){
        breakdown.setDescriptionCaption(descriptionProperty);
      }
      
      if(!Utils.isStringEmpty(titleWhenEmptyProperty)){
        breakdown.setTitleWhenEmpty(titleWhenEmptyProperty);
      }
      
      if(!Utils.isStringEmpty(descriptionWhenEmptyProperty)){
        breakdown.setDescriptionWhenEmpty(descriptionWhenEmptyProperty);
      }
      
      if(!Utils.isStringEmpty(dateStart)){
        Date temp = null;
        
        try{
        	temp = new Date(FDate.convertDisplayStringToDate(dateStart).getTime());
//          temp = Date.valueOf(dateStart);
        }
        catch(IllegalArgumentException e){
          Globals.logException(e);
        }
        
        breakdown.setDateStart(temp);
      }
      
      if(!Utils.isStringEmpty(dateEnd)){
        Date temp = null;
        
        try{
        	temp = new Date(FDate.convertDisplayStringToDate(dateEnd).getTime());
//          temp = Date.valueOf(dateEnd);
        }
        catch(IllegalArgumentException e){
          Globals.logException(e);
        }
        
        breakdown.setDateEnd(temp);
      }
      
      if(!Utils.isStringEmpty(hideWhenAlone) && hideWhenAlone.equalsIgnoreCase("true")){
      	breakdown.setHideWhenOnlyChild(true);
      }else{
      	breakdown.setHideWhenOnlyChild(false);
      }

      getBreakdownList().add(breakdown);
    }
    return breakdown;
	}
	
	public void addValue(String title, String datapath){
		addValue(title, datapath, null, null, null);
	}
	
	public FPivotValue addValue(String title, String datapath, String computeLevel, String aggregationFormula, String formula) {
	  FPivotValue value = (FPivotValue) getValueList().newEmptyItem();
	  value.setTitle(title);
	  value.setComputeLevel(computeLevel);
	  value.setDatapath(datapath);
	  if(aggregationFormula != null){
	  	value.setAggregationFormula(aggregationFormula);
	  }
	  if(!Utils.isStringEmpty(formula)){
	  	value.setFormula(formula);
	  }
	  getValueList().add(value);
	  return value;
	}

	public FocList getBreakdownList(){
	  FocList list = getPropertyList(FLD_VIEW_BKDN_LIST);
	  list.setDirectImpactOnDatabase(true);
	  list.setDirectlyEditable(false);
	  return list;
	}
	
	public FocList getValueList(){
	  FocList list = getPropertyList(FLD_VIEW_VALUE_LIST);
	  list.setDirectImpactOnDatabase(true);
	  list.setDirectlyEditable(false);
	  return list;
	}
}
