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

import com.foc.ConfigInfo;
import com.foc.admin.FocDBLog;
import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.business.workflow.LoggableFactory;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.business.workflow.WorkflowTransactionFactory;
import com.foc.business.workflow.map.WFStageDesc;
import com.foc.business.workflow.signing.WFTransactionWrapperDesc;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBlobStringField;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.desc.field.FReferenceField;
import com.foc.desc.field.FStringField;
import com.foc.list.FocList;
import com.foc.log.FocLogEvent;

public class WFLogDesc extends FocDesc {
	public static final int FLD_MASTER                 = 1;
	public static final int FLD_USER                   = 2;
	public static final int FLD_DATE_TIME              = 3;
	public static final int FLD_TITLE                  = 4;
	public static final int FLD_PREVIOUS_STAGE         = 5;
	public static final int FLD_TARGET_STAGE           = 6;
	public static final int FLD_EVENT_UNDONE           = 7;
	public static final int FLD_EVENT_TYPE             = 8;
	public static final int FLD_ON_BEHALF_OF           = 9;
	public static final int FLD_DESCERIPTION           = FField.FLD_DESCRIPTION;
	public static final int FLD_COMMENT                = 10;
	public static final int FLD_SIGNED_TRANSACTION_XML = 11;
	public static final int FLD_EVT_STATUS             = 12;//0, 1-Posted, 2-Committed
	public static final int FLD_EVENT_STATUS_ERROR     = 13;
	public static final int FLD_CHANGES                = 14;
	public static final int FLD_DOC_ZIP                = 15;
	public static final int FLD_DOC_HASH               = 16;
	public static final int FLD_DOC_VERSION            = 17;
	
	public static final int EVENT_NONE            = FocLogEvent.EVENT_NONE;
	public static final int EVENT_SIGNATURE       = FocLogEvent.EVENT_SIGNATURE;
	public static final int EVENT_CREATION        = FocLogEvent.EVENT_CREATION;
	public static final int EVENT_CANCELLATION    = FocLogEvent.EVENT_CANCELLATION;
	public static final int EVENT_MODIFICATION    = FocLogEvent.EVENT_MODIFICATION;
	public static final int EVENT_APPROVED        = FocLogEvent.EVENT_APPROVED;
	public static final int EVENT_CLOSED          = FocLogEvent.EVENT_CLOSED;
	public static final int EVENT_UNDO_SIGNATURE  = FocLogEvent.EVENT_UNDO_SIGNATURE;
	public static final int EVENT_CUSTOM          = FocLogEvent.EVENT_CUSTOM;
	public static final int EVENT_COMMENT         = FocLogEvent.EVENT_COMMENT;
	public static final int EVENT_REJECT          = FocLogEvent.EVENT_REJECT;
	public static final int EVENT_OPENED          = FocLogEvent.EVENT_OPENED;
	
	public static final int LEN_FLD_COMMENT      =   400;
	public static final int LEN_FLD_CHANGES      = 10000;
//	public static final int LEN_FLD_CHANGES      =  4000;
	public static final int LEN_FLD_ZIPPED_DOC   =  20000;
	public static final int LEN_STATUS_ERROR     =  4000;
	
	public static final String WF_LOG_VIEW_KEY = "WF_LOG";
	
	public static final String FNAME_EVT_STATUS   = "EVT_STATUS";
	public static final String FNAME_MASTER_REF   = "MASTER_REF";
	public static final String FNAME_EVENT_TYPE   = "EVENT_TYPE";
	public static final String FNAME_DATE_TIME    = "DATE_TIME";
	
	public static final String FNAME_CHANGES      = "CHANGES";
	public static final String FNAME_DocZip       = "DocZip";
	
	public WFLogDesc(ILoggableDesc iWFDesc){
		this(iWFDesc, ((FocDesc) iWFDesc).getStorageName());
	}
		
	public WFLogDesc(ILoggableDesc iWFDesc, String dbTableSuffix){
		super(WFLog.class, FocDesc.DB_RESIDENT, getStorageName_ForTransactionStorageName(dbTableSuffix), false);
		setGuiBrowsePanelClass(WFLogGuiBrowsePanel.class);
		addReferenceField();
		setListInCache(false);
		
		FocDBLog.addTablesToExclude(getStorageName_ForTransactionStorageName(dbTableSuffix));
		
		boolean isWorkflow = iWFDesc instanceof IWorkflowDesc; 
		
		if(isWorkflow) {
			WorkflowTransactionFactory.getInstance().add((IWorkflowDesc) iWFDesc);
		}
		LoggableFactory.getInstance().add((ILoggableDesc) iWFDesc);
		
		addDescriptionField();
		
		FocDesc masterFocDesc = (FocDesc) iWFDesc;
		
		FObjectField objFld = new FObjectField("WF_MASTER", "MASTER", FLD_MASTER, false, masterFocDesc, "MASTER_", this, iWFDesc.iWorkflow_getWorkflowDesc().getFieldID_LogList());
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setDisplayField(FField.REF_FIELD_ID);
		objFld.setComboBoxCellEditor(FField.REF_FIELD_ID);
		objFld.setWithList(false);
		FReferenceField refFld = (FReferenceField) masterFocDesc.getFieldByID(FField.REF_FIELD_ID);
		if(refFld != null){
			if(refFld.getName() != FField.REF_FIELD_NAME){
				objFld.setForcedDBName("MASTER_"+refFld.getName());
			}
			addField(objFld);
		}
		
		FMultipleChoiceField statusFld = new FMultipleChoiceField(FNAME_EVT_STATUS, "Event Status", FLD_EVT_STATUS, false, 4);
		statusFld.setSortItems(false);
		statusFld.addChoice(FocLogEvent.STATUS_EXCLUDED , "Excluded");
		statusFld.addChoice(FocLogEvent.STATUS_INCLUDED , "Included");
		statusFld.addChoice(FocLogEvent.STATUS_POSTED   , "Posted");
		statusFld.addChoice(FocLogEvent.STATUS_COMMITTED, "Committed");
		for(int i=FocLogEvent.STATUS_ERROR_START; i<FocLogEvent.STATUS_ERROR_END; i++) {
			statusFld.addChoice(i, "Error "+i);
		}
		addField(statusFld);
		
		FMultipleChoiceField mFld = newEventTypeField(FNAME_EVENT_TYPE, FLD_EVENT_TYPE);
		addField(mFld);

    FDateTimeField dateTimeFld = new FDateTimeField(FNAME_DATE_TIME, "Date|Time", FLD_DATE_TIME, false);
    dateTimeFld.setMandatory(true);    
    dateTimeFld.setTimeRelevant(true);
    dateTimeFld.setAllwaysLocked(true);
    addField(dateTimeFld);
		
    FObjectField fObjectFld = new FObjectField("USER", "User", FLD_USER, false, FocUser.getFocDesc(), "USER_");
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setMandatory(false);
//    fObjectFld.setAllowLoadListFromFocDesc(false);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    fObjectFld.setAllwaysLocked(true);
    addField(fObjectFld);
		
		objFld = new FObjectField("TITLE", "Title", FLD_TITLE, false, WFTitleDesc.getInstance(), "TITLE_");
		objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFTitleDesc.FLD_NAME);
		objFld.setDisplayField(WFTitleDesc.FLD_NAME);
		addField(objFld);

		FStringField chfld = new FStringField("COMMENT", "Comment", FLD_COMMENT, false, LEN_FLD_COMMENT);
    addField(chfld);

    int changesLength = LEN_FLD_CHANGES;
    if(getProvider() == DBManager.PROVIDER_MYSQL) changesLength = 4000;
		chfld = new FStringField(FNAME_CHANGES, "Changes", FLD_CHANGES, false, changesLength);
		chfld.setCompress(true);
    addField(chfld);
    
    int zippedLength = LEN_FLD_ZIPPED_DOC;
    if(getProvider() == DBManager.PROVIDER_MYSQL) zippedLength = 4000;
    FStringField zipfld = new FStringField(FNAME_DocZip, "Document Zipped", FLD_DOC_ZIP, false, zippedLength);
    zipfld.setAllwaysLocked(true);
    zipfld.setCompress(true);
    addField(zipfld);

    FIntField verfld = new FIntField("DocVersion", "Document version", FLD_DOC_VERSION, false, 10);
    verfld.setAllwaysLocked(true);
    addField(verfld);
    
    FStringField hashfld = new FStringField("DocHash", "Document hash", FLD_DOC_HASH, false, 22);
    hashfld.setAllwaysLocked(true);
    addField(hashfld);

    FBlobStringField focFld = new FBlobStringField("SIGNED_TRANSACTION_XML", "Signed Transaction XML", FLD_SIGNED_TRANSACTION_XML, false, 300, 5);
    addField(focFld);

		if (isWorkflow) {
			objFld = WFStageDesc.newFieldStage("TARGET_STAGE", FLD_TARGET_STAGE);
			addField(objFld);
			
			objFld = WFStageDesc.newFieldStage("PREVIOUS_STAGE", FLD_PREVIOUS_STAGE);
			addField(objFld);
	
			addField(WFTransactionWrapperDesc.newOnBehalfOfField(FLD_ON_BEHALF_OF));
			
			FBoolField bFld = new FBoolField("EVENT_UNDONE", "Event Undone", FLD_EVENT_UNDONE, false);
			addField(bFld);
		}

		chfld = new FStringField("STATUS_ERROR", "Status error", FLD_EVENT_STATUS_ERROR, false, LEN_STATUS_ERROR);
    addField(chfld);

		/*
		objFld = new FObjectField("TARGET_STAGE", "Target|Stage", FLD_TARGET_STAGE, false, WFStageDesc.getInstance(), "TARGET_STAGE_");
		objFld.setSelectionList(WFStageDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFStageDesc.FLD_NAME);
		objFld.setDisplayField(WFStageDesc.FLD_NAME);

		objFld = new FObjectField("PREVIOUS_STAGE", "Previous|Stage", FLD_PREVIOUS_STAGE, false, WFStageDesc.getInstance(), "PREVIOUS_STAGE_");
		objFld.setSelectionList(WFStageDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFStageDesc.FLD_NAME);
		objFld.setDisplayField(WFStageDesc.FLD_NAME);
		addField(objFld);
		*/
  }

	public static FMultipleChoiceField newEventTypeField(String fieldName, int fldID) {
		FMultipleChoiceField mFld = new FMultipleChoiceField(fieldName, "Event Type", fldID, false, 2);
		mFld.addChoice(EVENT_NONE, ConfigInfo.isArabic() ? "-" : "none");
		mFld.addChoice(EVENT_SIGNATURE, ConfigInfo.isArabic() ? "موافقة" : "Signature");
		mFld.addChoice(EVENT_CANCELLATION, ConfigInfo.isArabic() ? "الغاء" : "Cancellation");
		mFld.addChoice(EVENT_MODIFICATION, ConfigInfo.isArabic() ? "تعديل" : "Modification");
		mFld.addChoice(EVENT_CREATION, ConfigInfo.isArabic() ? "ادخال" : "Creation");
		mFld.addChoice(EVENT_APPROVED, ConfigInfo.isArabic() ? "تصديق" : "Approval");
		mFld.addChoice(EVENT_CLOSED, ConfigInfo.isArabic() ? "ختم" : "Closure");
		mFld.addChoice(EVENT_UNDO_SIGNATURE, ConfigInfo.isArabic() ? "الغاء الموافقة" : "Undo Signature");
		mFld.addChoice(EVENT_CUSTOM, ConfigInfo.isArabic() ? "Custom" : "Custom");
		mFld.addChoice(EVENT_COMMENT, ConfigInfo.isArabic() ? "ملاحظة" : "Comment");
		mFld.addChoice(EVENT_REJECT, ConfigInfo.isArabic() ? "الغاء الموافقات السابقة" : "Reject");
		mFld.addChoice(EVENT_OPENED, ConfigInfo.isArabic() ? "اطلاع" : "Opened");
		return mFld;
	}
	
	public static String getStorageName_ForWorkflowDesc(IWorkflowDesc iWFDesc){
		return getStorageName_ForTransactionStorageName(((FocDesc) iWFDesc).getStorageName());
	}
	
	public static String getStorageName_ForTransactionStorageName(String transStorageName){
		return "WF_LOG_"+transStorageName;
	}
}
