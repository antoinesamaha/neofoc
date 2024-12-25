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
package com.foc.saas.manager;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.list.FocList;

public class SaaSConfigDesc extends FocDesc {
	
	public static final int FREE_PLAN_USER_MAX_ALLOWED_RFQ_CREATION = 3;
	
  public static final int FLD_APPLICATION_TYPE      =  1;
  public static final int FLD_PLAN                  =  2;
  public static final int FLD_RENEWED_UNTIL         =  3;
  
  public static final int PLAN_FREE    = 0;
  public static final int PLAN_LEVEL_1 = 1;
  public static final int PLAN_LEVEL_2 = 2;
  public static final int PLAN_LEVEL_3 = 3;
  
  public static final int APPLICATION_TYPE_NONE         = 0;
  public static final int APPLICATION_TYPE_PROCUREMENT  = 1;

  public static final String APP_TYPE_PROCUREMENT = "Procurement";
  public static final String PLAN_NAME_FREE       = "Free";
  
	public static final String DB_TABLE_NAME = "SAAS_CONFIG";
	
  public SaaSConfigDesc(){
    super(SaaSConfig.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);

    addReferenceField();
    
    FMultipleChoiceField mField = newApplicationTypeField(FLD_APPLICATION_TYPE);
    addField(mField);
    
    mField = newPlanField(FLD_PLAN);
    addField(mField);
    
    FDateField dateFld = new FDateField("RENEWED_UNTIL", "Renewed_Unti", FLD_RENEWED_UNTIL, false);
    addField(dateFld);
  }
  
  public static FMultipleChoiceField newApplicationTypeField(int fieldID){
    FMultipleChoiceField mField = new FMultipleChoiceField("APPLICATION_TYPE", "Application Type", fieldID, false, 3);
    mField.addChoice(APPLICATION_TYPE_NONE, "None");
    mField.addChoice(APPLICATION_TYPE_PROCUREMENT, APP_TYPE_PROCUREMENT);
    return mField;
  }
  
  public static FMultipleChoiceField newPlanField(int fieldID){
  	FMultipleChoiceField mField = new FMultipleChoiceField("PLAN", "Plan", fieldID, false, 4);
    mField.addChoice(PLAN_FREE, PLAN_NAME_FREE);
    mField.addChoice(PLAN_LEVEL_1, "Level 1");
    mField.addChoice(PLAN_LEVEL_2, "Level 2");
    mField.addChoice(PLAN_LEVEL_3, "Level 3");
    return mField;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocList getList(int mode){
  	return getInstance().getFocList(mode);
  }
  
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    return list;    
  }
    
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
  	return getInstance(DB_TABLE_NAME, SaaSConfigDesc.class);
  }
}
