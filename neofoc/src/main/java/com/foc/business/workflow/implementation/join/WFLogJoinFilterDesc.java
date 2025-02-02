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
package com.foc.business.workflow.implementation.join;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.list.filter.FilterDesc;
import com.foc.list.filter.FocDescForFilter;
import com.foc.property.validators.UniquePropertyValidator;

public abstract class WFLogJoinFilterDesc extends FocDescForFilter {

	protected abstract WFLogJoinDesc getLogJoinDesc();
	
	public static final int FLD_VIEW_NAME = 1000;
	
  public static final String COND_PROCESSED   = "PROCESSED";
  
  public WFLogJoinFilterDesc(WFLogJoinDesc logJoinDesc){
    super(WFLogJoinFilter.class, FocDesc.DB_RESIDENT, logJoinDesc.getStorageName()+"_FILTER", true);
    
    FField nameFld = getFieldByID(FField.FLD_NAME);
    if(nameFld != null){
    	nameFld.setSize(50);
    	nameFld.setMandatory(true);
    	nameFld.setPropertyValidator(new UniquePropertyValidator());
    }
  }

	@Override
	public FilterDesc getFilterDesc(){
    if(filterDesc == null){
      filterDesc = new FilterDesc(getLogJoinDesc());

//      DateCondition startDateCondition = new DateCondition(FFieldPath.newFieldPath(GenInspJoinDesc.SHIFT_INSPECTION+GenInspectionDesc.FLD_START_DATE), COND_START_DATE);
//      filterDesc.addCondition(startDateCondition);
//      
//      DateCondition endDateCondition = new DateCondition(FFieldPath.newFieldPath(GenInspJoinDesc.SHIFT_INSPECTION+GenInspectionDesc.FLD_END_DATE), COND_END_DATE);
//      filterDesc.addCondition(endDateCondition);
//      
//      BooleanCondition processedCondition = new BooleanCondition(FFieldPath.newFieldPath(GenInspJoinDesc.SHIFT_INSPECTION+GenInspectionDesc.FLD_PROCESSED), COND_PROCESSED);
//      filterDesc.addCondition(processedCondition);

      filterDesc.setNbrOfGuiColumns(1);
    }
    return filterDesc;
	}

}
