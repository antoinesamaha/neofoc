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
package com.foc.business.status;

import java.sql.Date;

import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.business.notifier.FocNotificationConst;
import com.foc.business.notifier.FocNotificationEvent;
import com.foc.business.notifier.FocNotificationManager;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.business.workflow.map.WFTransactionConfig;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.desc.FocObject;
import com.foc.event.FocEvent;
import com.foc.property.FMultipleChoice;

public class StatusHolder {
	
	private FocObject focObject = null; 
		
	public StatusHolder(FocObject focObject){
		this.focObject = focObject;
	}
	
	public void dispose(){
		focObject = null;
	}
	
	private void setDeletableFlagAccordingToStatus(){
		if(focObject != null && getStatus() == StatusHolderDesc.STATUS_CLOSED){
			focObject.setDeletable(false);
		}
	}

	public void fillCreationInfo(){
		int initialStatus = StatusHolderDesc.STATUS_PROPOSAL;
		if(focObject instanceof IWorkflow){
			WFTransactionConfig assignment    = WFTransactionConfigDesc.getTransactionConfig_ForTransaction((IWorkflow)focObject);
			if(assignment != null && assignment.isApproveOnCreation()){
				initialStatus = StatusHolderDesc.STATUS_APPROVED;
			}
		}
		
		if(getStatus() < initialStatus){
			setStatus(initialStatus);
			getIStatusHolder().afterSettingStatusTo(initialStatus);
		}
  	setCreationUser(Globals.getApp().getUser_ForThisSession());
  	setCreationDate(Globals.getApp().getSystemDate());
	}
	
	public IStatusHolderDesc getFocDesc(){
		return focObject != null ? (IStatusHolderDesc) focObject.getThisFocDesc() : null;
	}
	
	public IStatusHolder getIStatusHolder(){
		return (IStatusHolder) focObject;
	}
	
	public int getStatus(){
		return focObject.getPropertyInteger(getFocDesc().getFLD_STATUS()); 
	}
	
	public boolean isProposal(){
		return getStatus() == StatusHolderDesc.STATUS_PROPOSAL;
	}

	public void setStatus(int status){
		focObject.setPropertyInteger(getFocDesc().getFLD_STATUS(), status); 
	}

	public Date getCreationDate(){
		return focObject.getPropertyDate(getFocDesc().getFLD_CREATION_DATE()); 
	}

	public void setCreationDate(Date date){
		focObject.setPropertyDate(getFocDesc().getFLD_CREATION_DATE(), date); 
	}

	public Date getValidationDate(){
		return focObject.getPropertyDate(getFocDesc().getFLD_VALIDATION_DATE()); 
	}

	public void setValidationDate(Date date){
		focObject.setPropertyDate(getFocDesc().getFLD_VALIDATION_DATE(), date); 
	}

	public Date getClosureDate(){
		return focObject.getPropertyDate(getFocDesc().getFLD_CLOSURE_DATE()); 
	}

	public void setClosureDate(Date date){
		focObject.setPropertyDate(getFocDesc().getFLD_CLOSURE_DATE(), date); 
	}

	public FocUser getCreationUser(){
		return (FocUser) focObject.getPropertyObject(getFocDesc().getFLD_CREATION_USER()); 
	}
	
	public void setCreationUser(FocUser user){
		focObject.setPropertyObject(getFocDesc().getFLD_CREATION_USER(), user); 
	}
	
  public boolean resetStatusToProposal(){
  	boolean ok = getStatus() != StatusHolderDesc.STATUS_PROPOSAL;
  	if(ok){
  		ok = getIStatusHolder().allowSettingStatusTo(StatusHolderDesc.STATUS_PROPOSAL);
	    if(ok){
	    	setStatus(StatusHolderDesc.STATUS_PROPOSAL);
	    }
  	}
  	return !ok;
  }
  
  public boolean resetStatusToApproved(){
  	boolean ok = getStatus() == StatusHolderDesc.STATUS_CANCELED || getStatus() == StatusHolderDesc.STATUS_CLOSED;
  	if(ok){
  		ok = getIStatusHolder().allowSettingStatusTo(StatusHolderDesc.STATUS_APPROVED);
	    if(ok){
	    	setStatus(StatusHolderDesc.STATUS_APPROVED);
	    }
  	}
  	return !ok;
  }
  
  public boolean setStatusToValidated(){
  	return setStatusToValidated(true);
  }
  
  public boolean setStatusToValidated(boolean promptForChange){
  	boolean ok = getStatus() == StatusHolderDesc.STATUS_PROPOSAL;
  	if(ok){
	    if(getIStatusHolder().allowSettingStatusTo(StatusHolderDesc.STATUS_APPROVED)){
				Globals.getApp().getDataSource().transaction_setShouldSurroundWithTransactionIfRequest();
				try{
			    setStatus(StatusHolderDesc.STATUS_APPROVED);
			    if(getIStatusHolder() instanceof IWorkflow){
			    	IWorkflow iWorkflow = (IWorkflow) getIStatusHolder();
			    	if(iWorkflow != null && iWorkflow.iWorkflow_getWorkflow() != null){
			    		iWorkflow.iWorkflow_getWorkflow().addLogLine(WFLogDesc.EVENT_APPROVED);
			    	}
			    }
		    	setValidationDate(Globals.getDBManager().getCurrentTimeStamp_AsTime());
//		    	focObject.code_copyProposalCode(promptForChange);
		    	if(!focObject.code_getPrefix_ForStatus(StatusHolderDesc.STATUS_PROPOSAL).isEmpty() && !focObject.code_getPrefix_ForStatus(StatusHolderDesc.STATUS_PROPOSAL).equals(focObject.code_getPrefix_ForStatus(StatusHolderDesc.STATUS_APPROVED))){
		    		focObject.code_resetCodeIfPrefixHasChanged();
		    	}
		    	getIStatusHolder().afterSettingStatusTo(StatusHolderDesc.STATUS_APPROVED);
		    	if(focObject != null){
	  	    	FocNotificationManager.getInstance().fireEvent(new FocNotificationEvent(FocNotificationConst.EVT_TRANSACTION_APPROVE, focObject));
	  	    	focObject.fireEvent(FocEvent.ID_TRANSACTION_APPROVE);
		    	}
				}catch(Exception e){
					Globals.logException(e);
				}
				Globals.getApp().getDataSource().transaction_SeeIfShouldCommit();
	    }
  	}
  	return ok;
  }

  public boolean setStatusToCanceled(){
  	return setStatusToCanceled("");
  }
  
  public boolean setStatusToCanceled(String comment){
  	boolean ok = getStatus() != StatusHolderDesc.STATUS_CANCELED;
  	if(ok){
	    if(getIStatusHolder().allowSettingStatusTo(StatusHolderDesc.STATUS_CANCELED)){
		    setStatus(StatusHolderDesc.STATUS_CANCELED);
		    if(getIStatusHolder() instanceof IWorkflow){
		    	IWorkflow iWorkflow = (IWorkflow) getIStatusHolder();
		    	if(iWorkflow != null && iWorkflow.iWorkflow_getWorkflow() != null){
		    		iWorkflow.iWorkflow_getWorkflow().addLogLine(WFLogDesc.EVENT_CANCELLATION, null, false, null, null, comment);
		    	}
		    }
	    	//setValidationDate(Globals.getDBManager().getCurrentTimeStamp_AsTime());
	    	getIStatusHolder().afterSettingStatusTo(StatusHolderDesc.STATUS_CANCELED);
	    	if(focObject != null){
  	    	FocNotificationManager.getInstance().fireEvent(new FocNotificationEvent(FocNotificationConst.EVT_TRANSACTION_CANCEL, focObject));
          focObject.fireEvent(FocEvent.ID_TRANSACTION_CANCEL);
	    	}
	    }
  	}
  	return ok;
  }
  
  public boolean setStatusToClosed(){
    return setStatusToClosed(true);
  }
  
  public boolean setStatusToClosed(boolean checkRights){
  	boolean ok = getStatus() == StatusHolderDesc.STATUS_APPROVED;
  	if(ok){
	    if(!checkRights || getIStatusHolder().allowSettingStatusTo(StatusHolderDesc.STATUS_CLOSED)){
		    setStatus(StatusHolderDesc.STATUS_CLOSED);
		    if(getIStatusHolder() instanceof IWorkflow){
		    	IWorkflow iWorkflow = (IWorkflow) getIStatusHolder();
		    	if(iWorkflow != null && iWorkflow.iWorkflow_getWorkflow() != null){
		    		iWorkflow.iWorkflow_getWorkflow().addLogLine(WFLogDesc.EVENT_CLOSED);
		    	}
		    }
	    	setClosureDate(Globals.getDBManager().getCurrentTimeStamp_AsTime());
	    	getIStatusHolder().afterSettingStatusTo(StatusHolderDesc.STATUS_CLOSED);
	    	if(focObject != null){
  	    	FocNotificationManager.getInstance().fireEvent(new FocNotificationEvent(FocNotificationConst.EVT_TRANSACTION_CLOSE, focObject));
          focObject.fireEvent(FocEvent.ID_TRANSACTION_CLOSE);
	    	}
	    }
	    setDeletableFlagAccordingToStatus();
  	}
  	return ok;
  }

  public boolean acceptSystem(){
  	boolean ok = getStatus() == StatusHolderDesc.STATUS_SYSTEM;
  	if(ok){
	    if(getIStatusHolder().allowSettingStatusTo(StatusHolderDesc.STATUS_PROPOSAL)){
	    	setStatus(StatusHolderDesc.STATUS_NONE);
	    	setStatus(StatusHolderDesc.STATUS_PROPOSAL);
	    	getIStatusHolder().afterSettingStatusTo(StatusHolderDesc.STATUS_PROPOSAL);
	    }
  	}
  	return ok;
  }
  
  public String getPrintedStatus(){
  	String st = "";
  	if(getStatus() != StatusHolderDesc.STATUS_APPROVED){
  		FMultipleChoice mProp = (FMultipleChoice) focObject.getFocProperty(getFocDesc().getFLD_STATUS());
  		st = mProp.getString();
  	}
  	return st ;
  }
}
