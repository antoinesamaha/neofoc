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

import java.sql.Date;

import com.foc.Globals;
import com.foc.access.FocDataMap;
import com.foc.admin.FocUser;
import com.foc.business.adrBook.AdrBookParty;
import com.foc.business.notifier.FocNotificationConst;
import com.foc.business.notifier.FocNotificationEvent;
import com.foc.business.notifier.FocNotificationManager;
import com.foc.business.status.IStatusHolder;
import com.foc.business.status.StatusHolder;
import com.foc.business.status.StatusHolderDesc;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.map.WFMap;
import com.foc.business.workflow.map.WFSignature;
import com.foc.business.workflow.map.WFSignatureDesc;
import com.foc.business.workflow.map.WFStage;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.business.workflow.report.SignatureReportLine;
import com.foc.business.workflow.report.SignatureReportLineDesc;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.shared.dataStore.IFocData;

public class Workflow extends Loggable {

	public Workflow(IWorkflow focObject){
		super(focObject);
	}

	public IWorkflowDesc getIWorkflowDesc(){
		return (IWorkflowDesc) getILoggableDesc();
	}
	
	public WorkflowDesc getWorkflowDesc(){
		return getIWorkflowDesc().iWorkflow_getWorkflowDesc();
	}
	
	public void addLogLine(int eventType, WFTitle title, boolean onBehalfOf, WFStage prevStage, WFStage nextStage, String comment){
		//setArea(workflow.iWorkflow_getComputedSite());
		FocList focList = getLogList();
		WFLog   log     = null;

		log = (WFLog) focList.newEmptyItem();

		log.setEventType(eventType);
		log.setTitle(title);
		log.setOnBehalfOf(onBehalfOf);
		log.setPreviousStage(prevStage);
		log.setTargetStage(nextStage);
		
		log.setUser(Globals.getApp().getUser_ForThisSession());
		log.setDateTime(Globals.getDBManager().getCurrentTimeStamp_AsTime());
		log.setComment(comment);
		if(log.getUser() == null){
			
		}
		log.validate(true);
		if(Globals.getApp() != null) Globals.getApp().logListenerNotification(log);
	}
	
	public void addLogLine_Custom(String description){
		FocList focList = getLogList();
		WFLog   log     = null;

		log = (WFLog) focList.newEmptyItem();

		log.setEventType(WFLogDesc.EVENT_CUSTOM);
		
		log.setUser(Globals.getApp().getUser_ForThisSession());
		log.setDateTime(Globals.getDBManager().getCurrentTimeStamp_AsTime());
		log.setDescription(description);
		log.validate(true);
		if(Globals.getApp() != null) Globals.getApp().logListenerNotification(log);
	}
	
	public WFLog getSignatureAt(int index){
		WFLog foundLog = null;
		FocList logList = getLogList();
		
		for(int i=0; i<logList.size() && foundLog == null; i++){
			WFLog log = (WFLog) logList.getFocObject(i);
			if(log.getEventType() == WFLogDesc.EVENT_SIGNATURE && !log.getEventUndone()){
				if(index == 0){
					foundLog = log;
				}
				index--;
			}
		}
		
		return foundLog;
	}
	
	public FocList newWorkflowSignatureList(){
		FocList signatureList = new FocList(new FocLinkSimple(SignatureReportLineDesc.getInstance()));
		signatureList.setDirectlyEditable(true);
	
		int                 index      = 0;
		SignatureReportLine sigRepLine = null;
		
		FocList logList = getLogList();
		if(logList != null){
			for(int i=0; i<logList.size(); i++){
				WFLog log = (WFLog) logList.getFocObject(i);
				if(log.getEventType() == WFLogDesc.EVENT_SIGNATURE && !log.getEventUndone()){
					if(index == 3){
						index      = 0;
						sigRepLine = null;
					}
					if(sigRepLine == null){
						sigRepLine = (SignatureReportLine) signatureList.newEmptyItem();
						signatureList.add(sigRepLine);
					}
					sigRepLine.setTitle(index, log.getTitle().getDescription());
					sigRepLine.setName(index, log.getUser().getFullName());
					sigRepLine.setDate(index, log.getDateTime());
					sigRepLine.setSignature(index, log.getUser().getSignature());
					index++;
				}
			}
		}
		return signatureList;
	}

	public WFSite getSite_1(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		return (getFocObject() != null && workflowDesc != null) ? (WFSite) getFocObject().getPropertyObject(workflowDesc.getFieldID_Site_1()) : null;
	}

	public void setSite_1(WFSite site){
	  WorkflowDesc workflowDesc = getWorkflowDesc();
	  if(getFocObject() != null && workflowDesc != null){
	    getFocObject().setPropertyObject(workflowDesc.getFieldID_Site_1(), site);
	  }
	}
	 
	public WFSite getSite_2(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		return (getFocObject() != null && workflowDesc != null) ? (WFSite) getFocObject().getPropertyObject(workflowDesc.getFieldID_Site_2()) : null;
	}

	public WFSite getArea(){
		WFSite site = getSite_1();
		if(site == null){
			site = getSite_2();
		}
		return site;
	}
	
	 public void setArea(WFSite site){
		 setSite_1(site);
	 }

	/*
	public void setArea(WFSite area){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		getFocObject().setPropertyObject(workflowDesc.getFieldID_Site_1(), area);
	}
	*/

	public boolean isSimulation(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		if(workflowDesc != null){
			return getFocObject().getPropertyBoolean(workflowDesc.getFieldID_Simulation());
		}else{
			return false;
		}
	}

	public void setSimulation(boolean simul){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		if(workflowDesc != null){
			getFocObject().setPropertyBoolean(workflowDesc.getFieldID_Simulation(), simul);
		}
	}

	public WFStage getCurrentStage(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		if(workflowDesc != null){
			return (WFStage) getFocObject().getPropertyObject(workflowDesc.getFieldID_CurrentStage());
		}else{
			return null;
		}
	}
	
	public void setCurrentStage(WFStage stage){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		if(workflowDesc != null){
			getFocObject().setPropertyObject(workflowDesc.getFieldID_CurrentStage(), stage);
		}
	}

	public String getCancelReason(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		return getFocObject().getPropertyString(workflowDesc.getFieldID_CancelReason());
	}
	
	public void setCancelReason(String stage){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		getFocObject().setPropertyString(workflowDesc.getFieldID_CancelReason(), stage);
	}

	public String getComment(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		return getFocObject().getPropertyString(workflowDesc.getFieldID_Comment());
	}
	
	public void setComment(String stage){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		getFocObject().setPropertyString(workflowDesc.getFieldID_Comment(), stage);
	}
	
	public String getLastComment(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		return getFocObject().getPropertyString(workflowDesc.getFieldID_LastComment());
	}
	
	public void setLastComment(String stage){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		getFocObject().setPropertyString(workflowDesc.getFieldID_LastComment(), stage);
	}
	
	public void moveComment2LastComment(){
		//if(getComment() != null && !getComment().isEmpty()){
			setLastComment(getComment());
			setComment("");
		//}
	}

	public String getAllSignatures(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		return getFocObject().getPropertyString(workflowDesc.getFieldID_AllSignatures());
	}
	
	public void setAllSignatures(String allSignatures){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		getFocObject().setPropertyString(workflowDesc.getFieldID_AllSignatures(), allSignatures);
	}

	public void addSignatureToAllSignatures(FocUser user, WFTitle title, Date date){
		if(user != null && title != null && date != null){
			String addition = title.getName()+" : "+user.getFullName()+" The "+date.toString();
			String initial  = getAllSignatures();
			if(initial.isEmpty()){
				setAllSignatures(addition);
			}else{
				setAllSignatures(initial + "\n" + addition);
			}
		}
	}
	
	public AdrBookParty getAdrBookParty(){
	  return getILoggable() != null ? getILoggable().iWorkflow_getAdrBookParty() : null;
	}
	
	public boolean isCanceled(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		return getFocObject().getPropertyBoolean(workflowDesc.getFieldID_Canceled());
	}
	
	public void setCanceled(boolean canceled){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		getFocObject().setPropertyBoolean(workflowDesc.getFieldID_Canceled(), canceled);
	}
	
	/*
	public FocList newSignaturesList(){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		FocList list = (FocList) getFocObject().getPropertyList(workflowDesc.getFieldID_LogList());
		list.setDirectlyEditable(false);
		list.setDirectImpactOnDatabase(true);
		if(list.getOrderComparator() == null){
			list.setOrderComparator(new Comparator<FocListElement>(){
				@Override public int compare(FocListElement e1, FocListElement e2){
					WFLog o1 = (WFLog) e1.getFocObject();
					WFLog o2 = (WFLog) e2.getFocObject();
					long l = o1.getDateTime().getTime() - o2.getDateTime().getTime();
					int ret = 0;
					if(l < 0) ret = -1;
					if(l > 0) ret =  1;
					return ret;
				}
			});
		}
		return list;
	}
	*/

	public WFTitle getTitle(int i){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		return (WFTitle) getFocObject().getPropertyObject(workflowDesc.getFieldID_Title(i));
	}

	public void setTitle(int i, WFTitle title){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		getFocObject().setPropertyObject(workflowDesc.getFieldID_Title(i), title);
	}

	public boolean isHide(int i){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		return getFocObject().getPropertyBoolean(workflowDesc.getFieldID_Hide(i));
	}

	public void setHide(boolean hide){
		WFSignature currentSignature = nextSignature();
		if(currentSignature != null){
			int idx = currentSignature.getTitleIndex_ForUserAndArea(getArea());
			if(idx >= 0){
				setHide(idx, hide);
			}			
		}
	}
	
	public void setHide(int i, boolean hide){
		WorkflowDesc workflowDesc = getWorkflowDesc();
		getFocObject().setPropertyBoolean(workflowDesc.getFieldID_Hide(i), hide);
	}

	public boolean isTitleAlreadySigned(WFTitle title){
		boolean sign = false;
		for(int i=0; i<WFSignatureDesc.FLD_TITLE_COUNT && !sign; i++){
			WFTitle currentTitle = getTitle(i);
			if(currentTitle != null && currentTitle.getName().equals(title.getName())){
				sign = true;
			}
		}
		return sign;
	}
	
	//USERREFACTOR
	/*
	public boolean isAnyUserTitlesAlreadySigned(){
		ArrayList<WFOperator> arrayList = WFOperatorDesc.newListOfTitlesForUserAndArea(getArea());
		boolean signed = false;
		if(arrayList.size() == 0){
			//If this user is not meant to sign then we check if there is any signature at all this means he cannoe edit any more
			FocList logList = getLogList();
			for(int l=0; l<logList.size() && !signed; l++){
				WFLog log = (WFLog) logList.getFocObject(l);
				signed = !log.getEventUndone() && log.getEventType() == WFLogDesc.EVENT_SIGNATURE;
			}
		}else{
			for(int i=0; i<arrayList.size(); i++){
				WFOperator operator = arrayList.get(i);
	
				FocList logList = getLogList();
				for(int l=0; l<logList.size() && !signed; l++){
					WFLog log = (WFLog) logList.getFocObject(l);
					signed = 			log.getEventType() == WFLogDesc.EVENT_SIGNATURE
										&& !log.getEventUndone()
										&& 	log.getTitle().getReference().getInteger() == operator.getTitle().getReference().getInteger();
				}
			}
		}
		return signed;
	}
	*/

	public WFMap getMap(){
		return WFTransactionConfigDesc.getMap_ForTransaction(getIWorkflowDesc().iWorkflow_getDBTitle());
	}
	
	public WFSignature nextSignature(){
		WFSignature signature = null;
		WFMap       map       = getMap();
		if(map != null){
			FocList signatureList = map.getSignatureList();
			for(int s=0; s<signatureList.size(); s++){
				WFSignature sig = (WFSignature) signatureList.getFocObject(s);
				if(
								(sig.getPreviousStage() == null && getCurrentStage() == null) 
						||	(sig.getPreviousStage() != null && getCurrentStage() != null && sig.getPreviousStage().getName().equals(getCurrentStage().getName()))
						){
					signature = sig;
					break;
				}
			}
		}		
		return signature;
	}
	
	public void sign(){
		sign(null);
	}
	
	public void sign(String comment){
		WFSignature currentSignature = nextSignature();
		if(currentSignature != null){
			int idx = currentSignature.getTitleIndex_ForUserAndArea(getArea());
			if(idx >= 0){
				sign(currentSignature, idx, false, comment);
			}
		}
	}

	public void sign(WFSignature signature, int titleIndex, boolean onBehalfOf){
		sign(signature, titleIndex, onBehalfOf, "");
	}
	
	public void sign(WFSignature signature, int titleIndex, boolean onBehalfOf, String comment){
		IWorkflow iworkflow = (IWorkflow) getFocObject();
		if(signature != null && titleIndex >=0 && signature.getTitle(titleIndex) != null){
			boolean moveToNextSignature_BecauseLastTitleToSign = titleIndex + 1 >= WFSignatureDesc.FLD_TITLE_COUNT || signature.getTitle(titleIndex + 1) == null;
			if(iworkflow.iWorkflow_allowSignature(signature)){
				iworkflow.iWorkflow_getWorkflow().setHide(titleIndex, false);
				addLogLine(WFLogDesc.EVENT_SIGNATURE, signature.getTitle(titleIndex), onBehalfOf, signature.getPreviousStage(), signature.getTargetStage(), comment);
				addSignatureToAllSignatures(Globals.getApp().getUser_ForThisSession(), signature.getTitle(titleIndex), Globals.getApp().getSystemDate());
				
				if(moveToNextSignature_BecauseLastTitleToSign){
					WFStage targetStage = signature.getTargetStage();
					if(targetStage.isCloseStage() && getFocObject() instanceof IStatusHolder){
						IStatusHolder statusHolder = (IStatusHolder) getFocObject();
						statusHolder.getStatusHolder().setStatusToClosed();
					} else if(targetStage.isApprovalStage() && getFocObject() instanceof IStatusHolder) {
						IStatusHolder statusHolder = (IStatusHolder) getFocObject();
						statusHolder.getStatusHolder().setStatusToValidated();						
					}
					setCurrentStage(targetStage);
					moveComment2LastComment();
					for(int i=0; i<WFSignatureDesc.FLD_TITLE_COUNT; i++){
						setTitle(i, null);
					}
				}else{
					setTitle(titleIndex, signature.getTitle(titleIndex));
				}
			}			
			getFocObject().validate(true);

			//Triggering the signature Event 
		  FocDataMap focDataMap = new FocDataMap(getFocObject());
  		focDataMap.putString("EVT_TRANSACTION", getIWorkflowDesc().iWorkflow_getDBTitle());
  		focDataMap.putString("EVT_STAGE", getCurrentStage() != null ? getCurrentStage().getName() : "");
      FocNotificationManager.getInstance().fireEvent(new FocNotificationEvent(FocNotificationConst.EVT_TRANSACTION_SIGN, focDataMap));
		}
	}
	
	public void undoLastSignature(){
		undoLastSignature("");
	}
	
	public void undoLastSignature(String writtenComment){
		WFLog lastSignatureLog = getLastSignatureEvent(true);
		if(lastSignatureLog != null){
			lastSignatureLog.setEventUndone(true);
			lastSignatureLog.validate(true);
			setCurrentStage(lastSignatureLog.getPreviousStage());
			addLogLine(WFLogDesc.EVENT_UNDO_SIGNATURE, lastSignatureLog.getTitle(), lastSignatureLog.isOnBehalfOf(), lastSignatureLog.getPreviousStage(), lastSignatureLog.getTargetStage(), writtenComment);
			
			IWorkflow iworkflow = (IWorkflow) getFocObject();
			for(int i=0; i<WFSignatureDesc.FLD_TITLE_COUNT; i++){
				iworkflow.iWorkflow_getWorkflow().setHide(i, false);
			}
			
			//Triggering the signature Event 
		  FocDataMap focDataMap = new FocDataMap(getFocObject());
  		focDataMap.putString("EVT_TRANSACTION", getIWorkflowDesc().iWorkflow_getDBTitle());
  		focDataMap.putString("EVT_STAGE", getCurrentStage() != null ? getCurrentStage().getName() : "");
      FocNotificationManager.getInstance().fireEvent(new FocNotificationEvent(FocNotificationConst.EVT_TRANSACTION_UNSIGN, focDataMap));
		}
	}
	
	public void restoreLastComment(){
		FocList focList 					   = getLogList(true);
		WFLog 	lastEventWithComment = null;
		for(int i=focList.size()-1; i>=0 && lastEventWithComment == null; i--){
			WFLog log = (WFLog) focList.getFocObject(i);
			if(log != null && !log.getEventUndone() && log.getEventType() == WFLogDesc.EVENT_SIGNATURE){
				lastEventWithComment = log;
			}
		}

		
//		log.getCo
	}

	public WFLog getLastSignatureEvent(boolean forceReloadOfLogList){
		FocList focList 					= getLogList(forceReloadOfLogList);
		WFLog 	lastSignatureLog 	= null;
		if(focList != null){
			for(int i=focList.size()-1; i>=0 && lastSignatureLog == null; i--){
				WFLog log = (WFLog) focList.getFocObject(i);
				if(log != null && !log.getEventUndone() && log.getEventType() == WFLogDesc.EVENT_SIGNATURE){
					lastSignatureLog = log;
				}
			}
		}
		
		return lastSignatureLog;
	}
	
	public void undoSignaturesTo(WFLog targetWFLog){
		undoAllSignatures(targetWFLog, "");
	}
	
	private void undoAllSignatures(WFLog targetWFLog, String comment){
		FocList logList = getLogList();
		WFStage stage = null;
		if(logList != null){
			logList.loadIfNotLoadedFromDB();
			
			boolean reset = targetWFLog == null;
			for(int i=0; i<logList.size(); i++){
				WFLog log = (WFLog) logList.getFocObject(i);
				if(log.getEventType() == WFLogDesc.EVENT_SIGNATURE){
					reset = reset || log.equalsRef(targetWFLog);
					
					if(reset){
						log.setEventUndone(true);
						if(stage == null && targetWFLog != null) stage = log.getPreviousStage();
					}
				}
			}
			logList.validate(true);
		}
		setCurrentStage(stage);
		setCancelReason(comment != null ? comment : "");
		resetStatusToProposal();		
		getFocObject().validate(true);
		insertLogLine(WFLogDesc.EVENT_REJECT, comment != null ? comment : "");
	}
	
	public void undoAllSignatures(){
		undoAllSignatures(null, "");
	}

	public void undoAllSignatures(String comment){
		undoAllSignatures(null, comment);
	}

  private void resetStatusToProposal(){
  	IFocData focData = getFocObject();
  	if(focData != null && focData instanceof IStatusHolder){
  		IStatusHolder iStatusHolder = (IStatusHolder)focData;
  		StatusHolder  statusHolder  = iStatusHolder.getStatusHolder();
  		if(statusHolder != null && iStatusHolder.allowSettingStatusTo(StatusHolderDesc.STATUS_PROPOSAL)){
  			statusHolder.setStatus(StatusHolderDesc.STATUS_PROPOSAL);
  			statusHolder.setValidationDate(new Date(0));
  		}
  	}
  }
  
  public boolean isLastSignatureDoneByThisUser(boolean forceReloadOfLogList){
  	boolean lastSignatureByThisUser = false;
  	if(Globals.getApp() != null && Globals.getApp().getUser_ForThisSession() != null){
			WFLog 	lastSignatureLog 	= getLastSignatureEvent(forceReloadOfLogList);
			if(lastSignatureLog != null && lastSignatureLog.getUser().equalsRef(Globals.getApp().getUser_ForThisSession())){
				lastSignatureByThisUser = true;
			}
  	}
  	return lastSignatureByThisUser;
  }
}
