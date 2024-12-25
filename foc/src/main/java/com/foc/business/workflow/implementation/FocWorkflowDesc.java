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
package com.foc.business.workflow.implementation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.foc.Globals;
import com.foc.business.status.IStatusHolderDesc;
import com.foc.business.status.StatusHolderDesc;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.util.Utils;

public abstract class FocWorkflowDesc extends FocDesc implements IStatusHolderDesc, IWorkflowDesc {

//	public abstract boolean isJoin();
	
  private static final int FLD_STATUS_SHIFT          = 1;
  private static final int FLD_CREATION_DATE_SHIFT   = 2;
  private static final int FLD_VALIDATION_DATE_SHIFT = 3;
  private static final int FLD_CLOSURE_DATE_SHIFT    = 4;
  private static final int FLD_CREATION_USER_SHIFT   = 5;
  private static final int FLD_SITE_1_SHIFT          = 6;

	private StatusHolderDesc statusHolderDesc = null;
	
	private String workflowCode  = null;
	private String workflowTitle = null;
	
	private int nextFldID = 1;
	private boolean listInTableEditable = false;
	
	public FocWorkflowDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique) {
		this(focObjectClass, dbResident, storageName, isKeyUnique, true);
	}
	
	public FocWorkflowDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique, boolean withWorkfllow) {
		super(focObjectClass, dbResident, storageName, isKeyUnique);
		
		if(withWorkfllow){
			initWorkflow();
		}
	}

	public void initWorkflow(){
		addSite1Field();
		
    statusHolderDesc = new StatusHolderDesc(this);
    statusHolderDesc.addFields();
    
    workflowDesc = new WorkflowDesc(this);
    workflowDesc.addFields();
	}
	
	protected void addSite1Field(){
    FField focFld = WFSiteDesc.newSiteField(this, FField.FNAME_SITE, iWorkflow_getFieldID_ForSite_1(), FField.NO_FIELD_ID);
    addField(focFld);
	}
	
  //-----------------
  //IStatusHolderDesc
  //-----------------
  
  @Override
  public StatusHolderDesc getStatusHolderDesc() {
    return statusHolderDesc;
  }

  @Override
  public int getFLD_STATUS() {
    return iWorkflow_getFieldIDShift() + WorkflowDesc.FLD_LAST_RESERVED + FLD_STATUS_SHIFT;
  }

  @Override
  public int getFLD_CREATION_DATE() {
    return iWorkflow_getFieldIDShift() + WorkflowDesc.FLD_LAST_RESERVED + FLD_CREATION_DATE_SHIFT;
  }

  @Override
  public int getFLD_VALIDATION_DATE() {
    return iWorkflow_getFieldIDShift() + WorkflowDesc.FLD_LAST_RESERVED + FLD_VALIDATION_DATE_SHIFT;
  }

  @Override
  public int getFLD_CLOSURE_DATE() {
    return iWorkflow_getFieldIDShift() + WorkflowDesc.FLD_LAST_RESERVED + FLD_CLOSURE_DATE_SHIFT;
  }

  @Override
  public int getFLD_CREATION_USER() {
    return iWorkflow_getFieldIDShift() + WorkflowDesc.FLD_LAST_RESERVED + FLD_CREATION_USER_SHIFT;
  }
  
  //-----------------
  //IWorkflowDesc
  //-----------------
  
//  @Override
//  public int iWorkflow_getFieldIDShift() {
//    return FLD_WORKFLOW_SHIFT;
//  }

  @Override
  public int iWorkflow_getFieldID_ForSite_1() {
  	return iWorkflow_getFieldIDShift() + WorkflowDesc.FLD_LAST_RESERVED + FLD_SITE_1_SHIFT;
  }

  @Override
  public int iWorkflow_getFieldID_ForSite_2() {
    return FField.NO_FIELD_ID;
  }

  @Override
  public WorkflowDesc iWorkflow_getWorkflowDesc() {
    return workflowDesc;
  }

  @Override
  public String iWorkflow_getTitle() {
  	if(!Utils.isStringEmpty(workflowTitle)){
  		return workflowTitle;
  	}
    return iWorkflow_getDBTitle();
  }

  @Override
  public String iWorkflow_getSpecificAdditionalWhere() {
    return null;//"("+FNAME_TYPE+"="+getTransactionType()+")";
  }
  
  @Override
	public int iWorkflow_getApprovalMethod() {
  	return WFTransactionConfigDesc.APPROVAL_METHOD_BY_WORKFLOW;
	}

	public void setWorkflowCode(String workflowCode) {
		this.workflowCode = workflowCode;
	}

	public void setWorkflowTitle(String workflowTitle) {
		this.workflowTitle = workflowTitle;
	}
	
	@Override
	public String iWorkflow_getDBTitle() {
		if(Utils.isStringEmpty(workflowCode)) {
			return getStorageName();
		}
		return workflowCode;
	}
	
	public boolean isListInTableEditable(){
		return listInTableEditable;
	}
	
	public void setListInTableEditable(boolean inTableEditable){
		listInTableEditable = inTableEditable;
	}
	
	protected FocList newFocList_Creation(){
		FocList list = super.newFocList();
		return list;
	}
	
	public FocList newFocList(){
		FocList list = newFocList_Creation();
		afterNewFocList(list);
		return list;
	}

	public void afterNewFocList(FocList list){
		if(list != null){
			list.setInTableEditable(isListInTableEditable());
		}
	}

	@Override
	public int nextFldID(){
		return nextFldID++;
	}

	public void afterParsing(){
		createListenersForPropertyChangedMethods();
	}
	
	private void createListenersForPropertyChangedMethods(){
		int level = 0;
  	Class<?> currentClass = getFocObjectClass();
    while (currentClass != null && currentClass != FocObject.class && currentClass != Object.class) {
  		Method[] declaredMethods = currentClass.getDeclaredMethods();
  		for(int i=0; i<declaredMethods.length; i++){
  			Method method = declaredMethods[i];
  			if(method.getName().startsWith("propertyChanged_")){
  				String propertyName = method.getName().substring("propertyChanged_".length());
  				if(!Utils.isStringEmpty(propertyName)){
  					FField fld = getFieldByName(propertyName);
  					if(fld != null){
  						Parameter[] params = method.getParameters();
  						if(params.length == 1 && params[0].getType() == FProperty.class){
  							fld.addListener(new PropertyChangedMethodListener(method));
  						}
  					}
  				}
  			}
  		}
      currentClass = currentClass.getSuperclass();
      level++;
    }
	}
	
	public class PropertyChangedMethodListener implements FPropertyListener {
		private Method method = null;
		
		public PropertyChangedMethodListener(Method method){
			this.method = method;
		}
		
		@Override
		public void propertyModified(FProperty property) {
			try{
				FocObject focObj = property != null ? property.getFocObject() : null;
				if(focObj != null){
	        Object[] args = new Object[1];
	        args[0] = property;
	        method.invoke(focObj, args);        	
				}
			}catch(Exception e){
				Globals.logException(e);
			}
		}

		@Override
		public void dispose() {
			method = null;
		}
	}

}
