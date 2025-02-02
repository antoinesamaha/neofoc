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

import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FListField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class LoggableDesc {
	public static final int FLD_LOG_LIST         = 1;
	public static final int FLD_LAST_MODIF_DATE  = 2;
	public static final int FLD_LAST_MODIF_USER  = 3;
	
	public static final int FLD_LOGGABLE_RESERVED= 5;

	private ILoggableDesc   loggableDesc  = null;

	public LoggableDesc(ILoggableDesc focDesc){
		loggableDesc = focDesc;
	}
	
	public void dispose(){
		loggableDesc = null;
	}
	
	public FocDesc getFocDesc(){
		return (FocDesc) loggableDesc;
	}
	
	public ILoggableDesc getILoggableDesc(){
		return loggableDesc;
	}

	public void addFields(){
		int fldID = loggableDesc.iWorkflow_getFieldIDShift();
				
		if(getFocDesc().getFieldByID(fldID+FLD_LAST_MODIF_DATE) == null){
			FDateTimeField focFld = new FDateTimeField("LAST_MODIF_DATE", "Last modification", fldID+FLD_LAST_MODIF_DATE, false);
	    focFld.setAllwaysLocked(true);
	    focFld.setTimeRelevant(true);
	    getFocDesc().addField(focFld);
		}

		if(getFocDesc().getFieldByID(fldID+FLD_LAST_MODIF_USER) == null){
	    FObjectField fObjectFld = new FObjectField("LAST_MODIF_USER", "Last Modification User", fldID+FLD_LAST_MODIF_USER, false, FocUser.getFocDesc(), "LAST_MODIF_USER_");
	    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
	    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
	    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
	    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
	    fObjectFld.setAllwaysLocked(true);
	    getFocDesc().addField(fObjectFld);
		}
	}

	public WFLogDesc getWFLogDesc(){
		FocDesc focDesc = (FocDesc) getILoggableDesc();
		FListField listField = focDesc != null ? (FListField) focDesc.getFieldByID(getFieldID_LogList()) : null;
		return listField != null ? (WFLogDesc) listField.getFocDesc() : null;
	}

	public int getFieldID_LogList(){
		return getILoggableDesc().iWorkflow_getFieldIDShift() + FLD_LOG_LIST;
	}
	
	public int getFieldID_LastModificationDate(){
		return getILoggableDesc().iWorkflow_getFieldIDShift() + FLD_LAST_MODIF_DATE;
	}

	public int getFieldID_LastModificationUser(){
		return getILoggableDesc().iWorkflow_getFieldIDShift() + FLD_LAST_MODIF_USER;
	}

}
