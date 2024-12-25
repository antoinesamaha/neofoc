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

import java.util.ArrayList;
import java.util.HashMap;

import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.business.department.Department;
import com.foc.business.workflow.WFOperator;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.map.WFMap;
import com.foc.business.workflow.map.WFSignature;
import com.foc.business.workflow.map.WFSignatureDesc;
import com.foc.business.workflow.map.WFStage;
import com.foc.business.workflow.map.WFStageDesc;
import com.foc.business.workflow.map.WFTransactionConfigDesc;
import com.foc.business.workflow.rights.RightLevel;
import com.foc.business.workflow.rights.UserTransactionRight;
import com.foc.business.workflow.signing.SiteStageCouple;
import com.foc.desc.FocObject;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

public class WorkflowDesc extends LoggableDesc {
//	public static final int FLD_CURRENT_STAGE    = 1; 
//	public static final int FLD_CANCEL_REASON    = 2;
//	public static final int FLD_CANCELED         = 3;
//	public static final int FLD_LOG_LIST         = 4;
//	public static final int FLD_ALL_SIGNATURES   = 5;
//	public static final int FLD_FUNCTIONAL_STAGE = 6;
//	public static final int FLD_LAST_COMMENT     = 7;
//	public static final int FLD_COMMENT          = 8;
//	public static final int FLD_SIMULATION       = 9;
//	public static final int FLD_LAST_MODIF_DATE  = 10;
//	public static final int FLD_LAST_MODIF_USER  = 11;
//
//	public static final int FLD_TITLE_FIRST      = 20;
//	
//	public static final int FLD_HIDE_FIRST       = 30;
//	
//	public static final int FLD_LAST_RESERVED    = 50;

	public static final int FLD_LAST_COMMENT     = FLD_LOGGABLE_RESERVED+1;
	public static final int FLD_COMMENT          = FLD_LOGGABLE_RESERVED+2;
	public static final int FLD_CURRENT_STAGE    = FLD_LOGGABLE_RESERVED+3; 
	public static final int FLD_CANCEL_REASON    = FLD_LOGGABLE_RESERVED+4;
	public static final int FLD_CANCELED         = FLD_LOGGABLE_RESERVED+5;
	public static final int FLD_ALL_SIGNATURES   = FLD_LOGGABLE_RESERVED+6;
	public static final int FLD_SIMULATION       = FLD_LOGGABLE_RESERVED+7;
	public static final int FLD_FUNCTIONAL_STAGE = FLD_LOGGABLE_RESERVED+8;
	
	public static final int FLD_TITLE_FIRST      = FLD_LOGGABLE_RESERVED+10;
	
	public static final int FLD_HIDE_FIRST       = FLD_LOGGABLE_RESERVED+20;

	public static final int FLD_LAST_RESERVED    = FLD_LOGGABLE_RESERVED+50;
	
  //This is not an actual field or property it is simply interpreted by the focDesc upon getFocDataByPath
	public static String FNAME_SIGNATURE     = "SIGNATURE";
	public static String FNAME_CANCEL_REASON = "WF_CANCEL_REASON";
	public static String FNAME_CURRENT_STAGE = "WF_CURRENT_STAGE";
	
	private HashMap<Integer, RightLevel> rightLevelMap = null;//Site ref to rightLevelMAP
	private RightLevel                   rightLevel    = null;
	//private WFFunctionalStagesArray functionStageArray = null;
	
	public static final String FNAME_SIMULATION = "SIMULATION";

	public WorkflowDesc(IWorkflowDesc focDesc){
		super(focDesc);
		//functionStageArray = new WFFunctionalStagesArray();
		//focDesc.iWorkflow_fillFunctionalStagesArray(functionStageArray);
	}

	public void dispose(){
		super.dispose();
		
		if(rightLevelMap != null){
			rightLevelMap.clear();
			rightLevelMap = null;
		}
		/*
		if(functionStageArray != null){
			functionStageArray.dispose();
			functionStageArray = null;
		}
		*/
	}
	
	private HashMap<Integer, RightLevel> getRightLevelMap(){
		if(rightLevelMap == null && !Globals.getApp().isWebServer()){
			rightLevelMap = new HashMap<Integer, RightLevel>();
		}
		return rightLevelMap;
	}
	
	/*
	public WFFunctionalStagesArray getFunctionStageArray(){
		return functionStageArray;
	}
	*/
	
	public IWorkflowDesc getIWorkflowDesc(){
		return (IWorkflowDesc) getILoggableDesc();
	}
		
	public void addFields(){
		super.addFields();
		
		int fldID = getILoggableDesc().iWorkflow_getFieldIDShift();

		if(getFocDesc().getFieldByID(fldID+FLD_CURRENT_STAGE) == null){
			FObjectField objFld = new FObjectField(FNAME_CURRENT_STAGE, "Workflow|Stage", fldID+FLD_CURRENT_STAGE, false, WFStageDesc.getInstance(), "WF_CURRENT_STAGE_");
			objFld.setSelectionList(WFStageDesc.getList(FocList.NONE));
			objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
			objFld.setDisplayField(WFStageDesc.FLD_NAME);
			objFld.setComboBoxCellEditor(WFStageDesc.FLD_NAME);
			getFocDesc().addField(objFld);
		}
		
		if(getFocDesc().getFieldByID(fldID+FLD_CANCELED) == null){
			FBoolField bFld = new FBoolField("WF_CANCELED", "Canceled", fldID+FLD_CANCELED, false);
			getFocDesc().addField(bFld);
		}

		if(getFocDesc().getFieldByID(fldID+FLD_CANCEL_REASON) == null){
//			FMultipleChoiceStringField cFld = new FMultipleChoiceStringField("WF_CANCEL_REASON", "Cancel reason", fldID+FLD_CANCEL_REASON, false, 100);
//			cFld.setChoicesAreFromSameColumn(getFocDesc());
			FStringField cFld = new FStringField(FNAME_CANCEL_REASON, "Cancel reason", fldID+FLD_CANCEL_REASON, false, 500);
			getFocDesc().addField(cFld);
		}
		
		if(getFocDesc().getFieldByID(fldID+FLD_LAST_COMMENT) == null){
			FStringField cFld = new FStringField("WF_LAST_COMMENT", "Last comment", fldID+FLD_LAST_COMMENT, false, WFLogDesc.LEN_FLD_COMMENT);
			cFld.setAllwaysLocked(true);
			getFocDesc().addField(cFld);
		}

		if(getFocDesc().getFieldByID(fldID+FLD_COMMENT) == null){
			FStringField cFld = new FStringField("WF_COMMENT", "Comment", fldID+FLD_COMMENT, false, WFLogDesc.LEN_FLD_COMMENT);
			getFocDesc().addField(cFld);
		}
		
		WFSignatureDesc.addTitleFields(getFocDesc(), fldID+FLD_TITLE_FIRST);
		
		for(int i=0; i<WFSignatureDesc.FLD_TITLE_COUNT; i++){
			int index = i+1;
			if(getFocDesc().getFieldByID(fldID+FLD_HIDE_FIRST+i) == null){
				FBoolField bFld = new FBoolField("WF_HIDE_"+index, "Hide "+index, fldID+FLD_HIDE_FIRST+i, false);
				getFocDesc().addField(bFld);
			}
		}

		if(getFocDesc().getFieldByID(fldID+FLD_ALL_SIGNATURES) == null){
			FBlobStringField cFld = new FBlobStringField("ALL_SIGNATURES", "Signatures", fldID+FLD_ALL_SIGNATURES, false, 6, 70);
			cFld.setAllwaysLocked(true);
			getFocDesc().addField(cFld);
		}
		
		if(getFocDesc().getFieldByID(fldID+FLD_SIMULATION) == null){
			FBoolField bFld = new FBoolField(FNAME_SIMULATION, "Simulation", fldID+FLD_SIMULATION, false);
			getFocDesc().addField(bFld);
			bFld.addListener(new FPropertyListener() {
				@Override
				public void propertyModified(FProperty property) {
					if(property != null && property.getFocObject() != null && !property.isLastModifiedBySetSQLString() && property.isManualyEdited()){
						property.getFocObject().code_resetCodeIfPrefixHasChanged();
					}
				}
				
				@Override
				public void dispose() {
				}
			});
		}
		
		/*
		FMultipleChoiceField mFld = new FMultipleChoiceField("FUNCTIONAL_STAGE", "Functional|Stage", fldID+FLD_FUNCTIONAL_STAGE, false, 3);
		if(functionStageArray != null){
			for(int i=0; i<functionStageArray.getSize(); i++){
				mFld.addChoice(functionStageArray.getFunctionalStageAt(i), functionStageArray.getFunctionalTitleAt(i));
			}
		}
		getFocDesc().addField(mFld);
		*/
	}
	
	public int getFieldID_Site_1(){
		return getIWorkflowDesc().iWorkflow_getFieldID_ForSite_1();
	}

	public int getFieldID_Site_2(){
		return getIWorkflowDesc().iWorkflow_getFieldID_ForSite_2();
	}

	public int getFieldID_FunctionalStage(){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_FUNCTIONAL_STAGE;
	}

	public int getFieldID_CurrentStage(){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_CURRENT_STAGE;
	}
	
	public int getFieldID_Canceled(){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_CANCELED;
	}

	public int getFieldID_CancelReason(){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_CANCEL_REASON;
	}
	
	public int getFieldID_LastComment(){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_LAST_COMMENT;
	}
	
	public int getFieldID_Comment(){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_COMMENT;
	}
	
	public int getFieldID_AllSignatures(){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_ALL_SIGNATURES;
	}

	public int getFieldID_Simulation(){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_SIMULATION;
	}
	
	public int getFieldID_Title(int idx){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_TITLE_FIRST + idx;
	}

	public int getFieldID_Hide(int idx){
		return getIWorkflowDesc().iWorkflow_getFieldIDShift() + FLD_HIDE_FIRST + idx;
	}

	public RightLevel getRightLevel(WFSite site){
		RightLevel rightLevel = null;
		if(site != null){
			HashMap<Integer, RightLevel> map = getRightLevelMap();
//			if(map != null){
				rightLevel = map != null ? map.get(site.getReference().getInteger()) : null;
				if(rightLevel == null){
					double foundStrength = 0;
					
					//1- User Transaction
					//2- User Any Transaction
					//3- Title Transaction
					//4- Title Any Transaction
					
					//User        is 100
					//Title       is  10
					//Transaction is   1

					WFSite currSite = site;
					while(currSite != null){
						FocList userTransRightList = currSite.getUserTransactionRightsList();
						for(int i=0; i<userTransRightList.size(); i++){
							UserTransactionRight usrTransRight = (UserTransactionRight) userTransRightList.getFocObject(i);
		
							double  thisStrength       = 0;
							
							int     thisTransaction    = 0;
							int     thisUser           = 0;
							int     thisTitle          = 0;
							
							if(usrTransRight.getTransactionDBTitle() != null && !usrTransRight.getTransactionDBTitle().isEmpty()){
								if(usrTransRight.getTransactionDBTitle().equals(getIWorkflowDesc().iWorkflow_getDBTitle())){
									thisTransaction =  1;
								}else{
									thisTransaction = -1;
								}
							}
		
							if(usrTransRight.getUser() != null){
								if(usrTransRight.getUser().equalsRef(Globals.getApp().getUser_ForThisSession())){
									thisUser =  1;
								}else{
									thisUser = -1;
								}
							}
		
							if(usrTransRight.getTitle() != null){
								if(Globals.getApp().getCurrentTitle() != null && usrTransRight.getTitle().equalsRef(Globals.getApp().getCurrentTitle())){
									thisTitle =  1;
								}else{
									thisTitle = -1;
								}
							}
							
							if(thisTitle >= 0 && thisUser >= 0 && thisTransaction >= 0){
								thisStrength = thisUser * 100 + thisTitle * 10 + thisTransaction;
								if(thisStrength > foundStrength){
									foundStrength = thisStrength;
									rightLevel = usrTransRight.getWFRightsLevel();
								}
							}
						}
						WFSite fatherSite = (WFSite) currSite.getFatherObject();
						if(fatherSite == currSite) break;
						currSite = fatherSite;
					}
					if(map != null) map.put(site.getReference().getInteger(), rightLevel);
				}
			}
//		}
		return rightLevel;
	}

  public boolean isAllowRead(){
  	return rightLevel != null ? rightLevel.isAllowRead() : false; 
  }

  public boolean isAllowInsert(){
  	return rightLevel != null ? rightLevel.isAllowInsert() : false; 
  }
  
  public boolean isAllowDeleteDraft(){
  	return rightLevel != null ? rightLevel.isAllowDeleteDraft() : false; 
  }

  public boolean isAllowDeleteApproved(){
  	return rightLevel != null ? rightLevel.isAllowDeleteApprove() : false; 
  }

  public boolean isAllowModifyDraft(){
  	return rightLevel != null ? rightLevel.isAllowModifyDraft() : false; 
  }

  public boolean isAllowModifyApproved(){
  	return rightLevel != null ? rightLevel.isAllowModifyApproved() : false; 
  }

  public boolean isAllowModifyCodeDraft(){
  	return rightLevel != null ? rightLevel.isAllowModifyCodeDraft() : false; 
  }

  public boolean isAllowModifyCodeApproved(){
  	return rightLevel != null ? rightLevel.isAllowModifyCodeApproved() : false; 
  }

  public boolean isAllowCancel(){
  	return rightLevel != null ? rightLevel.isAllowCancel() : false; 
  }

  public boolean isAllowClose(){
  	return rightLevel != null ? rightLevel.isAllowClose() : false; 
  }

  public boolean isAllowPrintDraft(){
  	return rightLevel != null ? rightLevel.isAllowPrintDraft() : false; 
  }

  public boolean isAllowPrintApprove(){
  	return rightLevel != null ? rightLevel.isAllowPrintApprove() : false; 
  }

  public boolean isAllowApprove(){
  	return rightLevel != null ? rightLevel.isAllowApprove() : false; 
  }

	private static void mergeOperatorsArrays(ArrayList<WFOperator> a1, ArrayList<WFOperator> a2){
		if(a1 != null && a2 != null){
			for(WFOperator opp2 : a2){
				boolean doAdd = true;
				for(WFOperator opp1 : a1){
					doAdd = !FocObject.equal(opp1.getTitle(), opp2.getTitle()) || !FocObject.equal(opp1.getArea(), opp2.getArea());
					if(!doAdd) break;
				}
				if(doAdd){
					a1.add(opp2);
				}
			}
		}
	}
  
	//Method that returns an Array of internal Class (WFSite, WFStage)
	public static ArrayList<SiteStageCouple> getSiteStageCoulpeArrayList(IWorkflowDesc workflowDesc){
		ArrayList<SiteStageCouple> operatorInofrmationsList = null;
		
		WFMap map = WFTransactionConfigDesc.getMap_ForTransaction(workflowDesc.iWorkflow_getDBTitle());
		FocUser user = Globals.getApp().getUser_ForThisSession();
		ArrayList<WFOperator> titlesAndAreasForUser = new ArrayList<WFOperator>(); 
		ArrayList<WFOperator> tempTitlesAndAreasForUser = user != null ? user.getOperatorsArray_AllAreas(true) : null;
		mergeOperatorsArrays(titlesAndAreasForUser, tempTitlesAndAreasForUser);
		
		FocList userList = FocUserDesc.getList();
		if(userList != null){
			for(int i=0; i<userList.size(); i++){
				FocUser otherUser = (FocUser) userList.getFocObject(i);
				if(otherUser != null){
					FocUser actingAsUser = otherUser.getReplacementUserActingAs();
					if(FocObject.equal(actingAsUser, user)){
						ArrayList<WFOperator> byReplacementTitlesAndAreasForUser = otherUser.getOperatorsArray_AllAreas(true);
						if(byReplacementTitlesAndAreasForUser != null){
							mergeOperatorsArrays(titlesAndAreasForUser, byReplacementTitlesAndAreasForUser);
						}
					}
				}
			}
		}
		
		if(map != null && titlesAndAreasForUser != null){
			for(WFOperator operator : titlesAndAreasForUser){
				if(operator != null){
					WFTitle      title      = operator.getTitle();
					WFSite       area       = operator.getArea();
					Department   department = operator.getDepartment();
					
					FocList signatureList = map.getSignatureList();
					for(int s=0; s<signatureList.size(); s++){
						WFSignature sig = (WFSignature) signatureList.getFocObject(s);
						if(sig.isTitleToSigne(title)){
							WFStage prevStage = sig.getPreviousStage();
							
							if(operatorInofrmationsList == null){
								operatorInofrmationsList = new ArrayList<SiteStageCouple>();
							}
							operatorInofrmationsList.add(new SiteStageCouple(area, department, prevStage));
						}
					}
				}
			}
		}
		return operatorInofrmationsList;
	}
}
