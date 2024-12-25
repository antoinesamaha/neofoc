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
package com.foc.business.workflow.signing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.business.calendar.FCalendar;
import com.foc.business.department.Department;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WorkflowTransactionFactory;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.Workflow;
import com.foc.business.workflow.implementation.WorkflowDesc;
import com.foc.business.workflow.map.WFMap;
import com.foc.business.workflow.map.WFSignature;
import com.foc.business.workflow.map.WFStage;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.list.FocListWithFilter;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class WFTransactionWrapperList extends FocListWithFilter{

	private HashMap<String, FocList> originalListMap = null;
	private Filter                   additionalFilterForSignatures          = null;
	
	public WFTransactionWrapperList(){
		super(WFTransWrapperFilterDesc.getInstance(), new FocLinkSimple(WFTransactionWrapperDesc.getInstance()));
		setDirectImpactOnDatabase(true);
		setDirectlyEditable(false);
		originalListMap = new HashMap<String, FocList>();
		getFocListFilter().setActive(this, true);
	}

	public void dispose(){
		super.dispose();
		if(originalListMap != null){
			Iterator<FocList> iter = originalListMap.values().iterator();
			while(iter != null && iter.hasNext()){
				FocList list = iter.next();
				if(isListOwner(list.getFocDesc())){
					list.dispose();
				}
			}
			
			originalListMap.clear();
			originalListMap = null;
		}
	}
	
	public boolean isShowHiddenTransactions(){
		return false;
	}
	
	public Filter getAdditionalFilterForSignature() {
		return additionalFilterForSignatures;
	}

	public void setAdditionalFilterForSignature(Filter filter) {
		this.additionalFilterForSignatures = filter;
	}

	private boolean isListOwner(FocDesc focDesc){
		return focDesc == null || !focDesc.isListInCache();
	}
	
	public FocList getTransactionList(FocDesc focDesc) {
		FocList       transList    = null;
		IWorkflowDesc workflowDesc = (focDesc instanceof IWorkflowDesc) ? (IWorkflowDesc) focDesc : null;

		if(!isListOwner(focDesc)){					
			transList = focDesc.getFocList(FocList.LOAD_IF_NEEDED);
		}else{
			ArrayList<SiteStageCouple> siteStageCoupleArrayList = WorkflowDesc.getSiteStageCoulpeArrayList(workflowDesc);
			String  additionalWhere = buildWhere(workflowDesc, siteStageCoupleArrayList);
			if(!Utils.isStringEmpty(additionalWhere)) {
				transList = focDesc.newFocList();
				transList.getFilter().putAdditionalWhere("SIGNING", additionalWhere);
				transList.loadIfNotLoadedFromDB();
			}
		}

		return transList;
	}
	
	private int countOrAddTransactionsToWraper(FocList transList, boolean addToWrapper) {
		int count = 0;
		if(transList != null && transList.getFocDesc() instanceof IWorkflowDesc){
			IWorkflowDesc workflowDesc = (IWorkflowDesc) transList.getFocDesc();

			WFMap map = WFTransactionConfigDesc.getMap_ForTransaction(workflowDesc.iWorkflow_getDBTitle());
			if(map != null){
				
				for(int t=0; t<transList.size(); t++){
					FocObject focObj   = transList.getFocObject(t);
					IWorkflow workflow = (IWorkflow) focObj; 
					WFSignatureNeededResult result = focObj.workflow_NeedsSignatureOfThisUser_AsTitleIndex(map);
					int titleIndex = result.getTitleIndex();
					
					if(titleIndex >= 0 && !workflow.iWorkflow_getWorkflow().isHide(titleIndex)){
						if(additionalFilterForSignatures == null || additionalFilterForSignatures.passesFilter(focObj.getReferenceInt(), focObj)){
							if(addToWrapper) {
								WFTransactionWrapper wrapper = (WFTransactionWrapper) newEmptyItem();
								wrapper.setCreated(false);//If we keep it created then when the user opens a wrapper form and clacels it gets removed from the foclist
								wrapper.setWorkflow(workflow);
			//					  wrapper.setSignature(result.getSignature());
								
								wrapper.setTitle(result.getSignature().getTitle(titleIndex));
								wrapper.setIsOnBehalfOf(result.isOnBehalfOf());
								
			//					wrapper.setTitleIndex(idx);
			//					wrapper.setSignature(currentSignature);
								add(wrapper);
							}
							count++;
						}
					}
				}
			}
		}
		return count; 
	}
	
	private int getCountOfPendingSignaturesForFocDesc(FocDesc focDesc) {
		int count = 0;
		FocList transList = getTransactionList(focDesc); 
		if(transList != null) {
//			if(isListOwner(focDesc)) {
			  transList.reloadFromDB();
//			} else {
//				transList.loadIfNotLoadedFromDB();
//			}
			count = countOrAddTransactionsToWraper(transList, false);
		}
		return count;
	}

	public void fillWrapperListForFocDesc(FocDesc focDesc) {
		if(focDesc != null && focDesc instanceof IWorkflowDesc) {
			FocList transList = getTransactionList(focDesc);
			
			if(transList != null){
				countOrAddTransactionsToWraper(transList, true);
				originalListMap.put(((IWorkflowDesc)focDesc).iWorkflow_getDBTitle(), transList);
			}
		}
	}
	
	public void fill(){
		for(int i=0; i<WorkflowTransactionFactory.getInstance().getFocDescCount(); i++){
			FocDesc       focDesc      = (FocDesc)       WorkflowTransactionFactory.getInstance().getFocDescAt(i);
			IWorkflowDesc workflowDesc = (IWorkflowDesc) WorkflowTransactionFactory.getInstance().getIWorkflowDesc(i);

			WFMap map = WFTransactionConfigDesc.getMap_ForTransaction(workflowDesc.iWorkflow_getDBTitle());
			if(map != null){
				fillWrapperListForFocDesc(focDesc); 
			}
		}
		
		setDefaultListOrder();
//		setOrderComparator(new Comparator<WFTransactionWrapper>() {
//			@Override
//			public int compare(WFTransactionWrapper w0, WFTransactionWrapper w1) {
//				int ret= 0;
//				
//				if(w0 != null && w1 != null) {
//					long compDate = FCalendar.compareDatesRegardlessOfTime(w0.getTransactionDate(), w1.getTransactionDate());
//					if(compDate > 0) ret = 1;
//					else if(compDate < 0) ret = -1;
//					
//					if(ret == 0) {
//						
//					}
//				}
//				
//				return ret;
//			}
//		});
	}

	public void setDefaultListOrder() {
		FocListOrder order = new FocListOrder();
		order.setReverted(true);
		order.addField(FFieldPath.newFieldPath(WFTransactionWrapperDesc.FLD_TRANSACTION_DATE));
		order.addField(FFieldPath.newFieldPath(WFTransactionWrapperDesc.FLD_TRANSACTION_TYPE));
		order.addField(FFieldPath.newFieldPath(WFTransactionWrapperDesc.FLD_TRANSACTION_CODE));
		setListOrder(order);
	}
	
  private void fill2(){
		for(int i=0; i<WorkflowTransactionFactory.getInstance().getFocDescCount(); i++){
			FocDesc       focDesc      = (FocDesc)       WorkflowTransactionFactory.getInstance().getFocDescAt(i);
			IWorkflowDesc workflowDesc = (IWorkflowDesc) WorkflowTransactionFactory.getInstance().getIWorkflowDesc(i);

			//Call the method that returns the Couple of Stages, Sites to look for.
			ArrayList<SiteStageCouple> siteStageCoupleArrayList = WorkflowDesc.getSiteStageCoulpeArrayList(workflowDesc);
			//Call the build and pass the Array of couples as parameter
			String  additionalWhere = buildWhere(workflowDesc, siteStageCoupleArrayList);
			
//			String  additionalWhere = buildWhere(workflowDesc);
			if(additionalWhere != null){
				FocList originalList    = new FocList(new FocLinkSimple(focDesc));
				originalList.setDirectImpactOnDatabase(true);
				originalList.setDirectlyEditable(false);
				originalList.getFilter().putAdditionalWhere("SIGNING", additionalWhere);
				originalList.loadIfNotLoadedFromDB();
				
				//Case of all WBSPointerDesc. We need to remove all items from other Sites because filter could not be in the request
				if(isSiteAReflectingField(workflowDesc)){
					ArrayList<FocObject> toRemoveArray = new ArrayList<FocObject>();
					
  				//use the same Array of couples as parameter
					for(int o=0; o<originalList.size(); o++){
						boolean toBeRemoved = true;
						FocObject currObj = originalList.getFocObject(o);
						
						if(!currObj.iStatusHolder_isTransactionCanceled()){
							FObjectField areaField  = getSiteField(workflowDesc);
							FObjectField stageField = getStageField(workflowDesc);
							
							WFSite     currentSite       = (WFSite) currObj.getPropertyObject(areaField.getID());
							WFStage    currentStage      = (WFStage) currObj.getPropertyObject(stageField.getID());
							Department currentDepartment = currObj.getDepartment();
							
							for(int j=0; j<siteStageCoupleArrayList.size() && toBeRemoved; j++){
								SiteStageCouple operatorInofrmation = siteStageCoupleArrayList.get(j);
								WFSite     site       = operatorInofrmation.getSite();
								WFStage    stage      = operatorInofrmation.getStage();
								Department department = operatorInofrmation.getDepartment();
								
								boolean isSiteEqual       = FocObject.equal(currentSite, site);
								boolean isStageEqual      = FocObject.equal(currentStage, stage);
								boolean isDepartmentEqual = department == null || FocObject.equal(currentDepartment, department);
								
								if(isSiteEqual && isStageEqual && isDepartmentEqual){
									toBeRemoved = false;
								}
							}
						}
						
						if(toBeRemoved){
							toRemoveArray.add(currObj);
						}
					}
					
					for(int o=0; o<toRemoveArray.size(); o++){
						originalList.remove(toRemoveArray.get(o));
					}
				}
				
				for(int o=0; o<originalList.size(); o++){
					IWorkflow iWorkflow = (IWorkflow) originalList.getFocObject(o);
					Workflow  workflow  = iWorkflow.iWorkflow_getWorkflow();
					
					WFSignature currentSignature = iWorkflow.iWorkflow_getWorkflow().nextSignature();
					if(currentSignature != null){
						int idx = currentSignature.getTitleIndex_ForUserAndArea(workflow.getArea());
						if(idx >= 0){
							if(workflow.getTitle(idx) == null && iWorkflow.iWorkflow_allowSignature(currentSignature)){
								WFTransactionWrapper wrapper = (WFTransactionWrapper) newEmptyItem();
								wrapper.setWorkflow(iWorkflow);
//								wrapper.setTitleIndex(idx);
//								wrapper.setSignature(currentSignature);
								add(wrapper);
								//break;
							}
						}
					}
				}
				
				originalListMap.put(workflowDesc.iWorkflow_getDBTitle(), originalList);
			}
		}
		getFocListFilter().setActive(true);
		
		FocListOrder order = new FocListOrder();
		order.addField(FFieldPath.newFieldPath(WFTransactionWrapperDesc.FLD_TRANSACTION_TYPE));
		order.addField(FFieldPath.newFieldPath(WFTransactionWrapperDesc.FLD_TRANSACTION_CODE));
		setListOrder(order);
	}	
	
//	public void fill(){
//		for(int i=0; i<WorkflowTransactionFactory.getInstance().getFocDescCount(); i++){
//			FocDesc       focDesc      = (FocDesc)       WorkflowTransactionFactory.getInstance().getFocDescAt(i);
//			IWorkflowDesc workflowDesc = (IWorkflowDesc) WorkflowTransactionFactory.getInstance().getIWorkflowDesc(i);
//
//			//Call the method that returns the Couple of Stages, Sites to look for.
//			ArrayList<OperatorInofrmation> operatorInofrmationsList = getOperatorInofrmationList(workflowDesc);
//			//Call the build and pass the Array of couples as parameter
//			String  additionalWhere = buildWhere(workflowDesc, operatorInofrmationsList);
//			
//			
////			String  additionalWhere = buildWhere(workflowDesc);
//			if(additionalWhere != null){
//				FocList originalList    = new FocList(new FocLinkSimple(focDesc));
//				originalList.getFilter().putAdditionalWhere("SIGNING", additionalWhere);
//				originalList.loadIfNotLoadedFromDB();
//				
//				//Case of all WBSPointerDesc. We need to remove all items from other Sites because filter could not be in the request
//				if(isSiteAReflectingField(workflowDesc)){
//					getSiteField(workflowDesc);
//
//					boolean toDelete = true;
//					ArrayList<FocObject> toRemoveArray = new ArrayList<FocObject>();
//					
//  				//use the same Array of couples as parameter
//					
//					ArrayList<WFOperator> titlesAndAreasForUser = WFOperatorDesc.getOperatorsArrayForThisUser_AllAreas();
//					
//					for(int j=0; j<titlesAndAreasForUser.size(); j++){
//						WFOperator operator = titlesAndAreasForUser.get(j);
//						if(operator != null){
//							WFTitle      title      = operator.getTitle();
//							WFSite       area       = operator.getArea();
//							
//							FObjectField areaField  = getSiteField(workflowDesc);
//							FObjectField stageField = getStageField(workflowDesc);
//							
////							int titleReference = title.getReference().getInteger();
////							int siteReference  = area.getReference().getInteger();
//							
//							for(int o=0; o<originalList.size(); o++){//fdf
//								FocObject currObj = originalList.getFocObject(o);
//								
//								WFSite  currentSite  = (WFSite) currObj.getFocProperty(areaField.getID()).getObject();
//								WFStage currentStage = (WFStage) currObj.getFocProperty(stageField.getID()).getObject();
//								
//								if(currentSite != null && currentStage != null){
//									FocObject.equal(o1, o2)
//									if(currentSite.equalsRef(area) && currentStage.equalsRef(stageField)){
//										
//									}
//								}
//							}
//						}
//					}
//				}
//				
//				for(int o=0; o<originalList.size(); o++){
//					IWorkflow iWorkflow = (IWorkflow) originalList.getFocObject(o);
//					Workflow  workflow  = iWorkflow.iWorkflow_getWorkflow();
//					
//					WFSignature currentSignature = iWorkflow.iWorkflow_getWorkflow().nextSignature();
//					if(currentSignature != null){
//						int idx = currentSignature.getTitleIndex_ForUserAndArea(workflow.getArea());
//						if(idx >= 0){
//							if(workflow.getTitle(idx) == null){
//								WFTransactionWrapper wrapper = (WFTransactionWrapper) newEmptyItem();
//								wrapper.setWorkflow(iWorkflow);
//								wrapper.setTitleIndex(idx);
//								wrapper.setSignature(currentSignature);
//								add(wrapper);
//								//break;
//							}
//						}
//					}
//				}
//				
//				originalListMap.put(workflowDesc.iWorkflow_getDBTitle(), originalList);
//			}
//		}
//		getFocListFilter().setActive(true);
//		
//		FocListOrder order = new FocListOrder();
//		order.addField(FFieldPath.newFieldPath(WFTransactionWrapperDesc.FLD_TRANSACTION_TYPE));
//		order.addField(FFieldPath.newFieldPath(WFTransactionWrapperDesc.FLD_TRANSACTION_CODE));
//		setListOrder(order);
//	}
	
//	private String buildWhere(IWorkflowDesc workflowDesc){
//		String str = null;
//		
//		WFMap map = WFTransactionConfigDesc.getMap_ForTransaction(workflowDesc.iWorkflow_getDBTitle());
//		ArrayList<WFOperator> titlesAndAreasForUser = WFOperatorDesc.getOperatorsArrayForThisUser_AllAreas(); 
//		
//		if(map != null){
//			for(int i=0; i<titlesAndAreasForUser.size(); i++){
//				WFOperator operator = titlesAndAreasForUser.get(i);
//				if(operator != null){
//					WFTitle      title      = operator.getTitle();
//					WFSite       area       = operator.getArea();
//					FObjectField areaField  = getSiteField(workflowDesc);
//					FObjectField stageField = getStageField(workflowDesc);
//					
//					FocList signatureList = map.getSignatureList();
//					for(int s=0; s<signatureList.size(); s++){
//						WFSignature sig = (WFSignature) signatureList.getFocObject(s);
//						if(sig.isTitleToSigne(title)){
//							WFStage prevStage = sig.getPreviousStage();
//							
//							int     stageRef  = prevStage != null ? prevStage.getReference().getInteger() : 0;
//							if(str != null){
//								str += " OR ";
//							}else{
//								str = "";
//							}
//							if(isSiteAReflectingField(workflowDesc)){//This is the case of all WBSPointerDesc. They are filtered in the memory after the request
//								str += "("+stageField.getDBName()+"="+stageRef+")";
//							}else{
//								str += "("+areaField.getDBName()+"="+area.getReference().getInteger()+" AND "+stageField.getDBName()+"="+stageRef+")";	
//							}
//							
//						}
//					}
//				}
//			}
//			
//			if(str != null){
//				str = "("+str+")";
//				String addWhere = workflowDesc.iWorkflow_getSpecificAdditionalWhere();
//				if(addWhere != null && !addWhere.isEmpty()){
//					str = "("+str+" and "+addWhere+")";
//				}
//			}		
//		}
//		
//		return str;
//	}
	
	private FObjectField getSiteField(IWorkflowDesc workflowDesc){
		FObjectField areaField = null; 
		if(workflowDesc != null && workflowDesc.iWorkflow_getWorkflowDesc() != null){
			areaField  = (FObjectField) ((FocDesc)workflowDesc).getFieldByID(workflowDesc.iWorkflow_getWorkflowDesc().getFieldID_Site_1());
		}
		return areaField; 
	}

	private FObjectField getStageField(IWorkflowDesc workflowDesc){
		FObjectField stageField = null; 
		if(workflowDesc != null && workflowDesc.iWorkflow_getWorkflowDesc() != null){
			stageField  = (FObjectField) ((FocDesc)workflowDesc).getFieldByID(workflowDesc.iWorkflow_getWorkflowDesc().getFieldID_CurrentStage());
		}
		return stageField; 
	}

	private boolean isSiteAReflectingField(IWorkflowDesc workflowDesc){
		boolean reflecting = false;
		FObjectField areaField  = getSiteField(workflowDesc);
		if(areaField != null){
			reflecting = areaField.isReflectingField();
		}
		return reflecting;
	}
	
	private String buildWhere(IWorkflowDesc workflowDesc, ArrayList<SiteStageCouple> siteStageArrayList){
		String str = null;
		if(siteStageArrayList != null){
			
			FObjectField areaField  = getSiteField(workflowDesc);
			FObjectField stageField = getStageField(workflowDesc);
			
			for(SiteStageCouple operatorInofrmation : siteStageArrayList){
				if(operatorInofrmation != null){
					WFSite  area      = operatorInofrmation.getSite();
					WFStage prevStage = operatorInofrmation.getStage();
					
					int stageRef  = prevStage != null ? prevStage.getReference().getInteger() : 0;
					if(str != null){
						str += " OR ";
					}else{
						str = "";
					}
					if(isSiteAReflectingField(workflowDesc)){//This is the case of all WBSPointerDesc. They are filtered in the memory after the request
						str += "("+stageField.getDBName()+"="+stageRef+")";
					}else{
						str += "("+areaField.getDBName()+"="+area.getReferenceInt()+" AND "+stageField.getDBName()+"="+stageRef+")";	
					}
				}
			}
		
			if(str != null){
				str = "("+str+")";
				String addWhere = workflowDesc.iWorkflow_getSpecificAdditionalWhere();
				if(addWhere != null && !addWhere.isEmpty()){
					str = "("+str+" and "+addWhere+")";
				}
			}
		}
		return str;
	}

	public WFTransactionWrapper getVisibleObjectAt(int tableRow){
		WFTransactionWrapper wrapper = null;
		if(getVisibleArray() != null && tableRow < getVisibleArray().size() && tableRow >= 0){
			int listRow = getVisibleArray().get(tableRow);
			wrapper = (WFTransactionWrapper) getFocObject(listRow);
		}
		return wrapper;
	}
	
	private ArrayList<Integer> getVisibleArray(){
		return getFocListFilter().getVisibleArray();
	}

	@Override
	public synchronized void remove(FocObject focObj) {
		super.remove(focObj);
	}
	
	public static int getCountOfPendingSignatures(FocDesc focDesc) {
		int count = 0;
		WFTransactionWrapperList transactionWrapperList = new WFTransactionWrapperList();
		count = transactionWrapperList.getCountOfPendingSignaturesForFocDesc(focDesc);
		transactionWrapperList.dispose();
		return count;
	}
}
