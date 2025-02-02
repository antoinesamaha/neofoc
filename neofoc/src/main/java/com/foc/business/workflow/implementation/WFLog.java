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

import com.foc.admin.FocUser;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.business.workflow.map.WFStage;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.log.FocLogEvent;
import com.foc.property.FObject;
import com.foc.property.FString;
import com.foc.util.Utils;

public class WFLog extends FocObject implements FocLogEvent {
	
	public WFLog(FocConstructor constr){
		super(constr);
		newFocProperties();
//		if(ConfigInfo.isLogListeningEnabled()) {
//			setEventStatus(WFLog.STATUS_INCLUDED);
//		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	public FocObject getObjectLogged() {
		FObject prop = (FObject) getFocProperty(WFLogDesc.FLD_MASTER);
		return (FocObject) prop.getObject();
	}
	
	public void setObjectLogged(FocObject focObject) {
		FObject prop = (FObject) getFocProperty(WFLogDesc.FLD_MASTER);
		prop.setObject_WithoutListeners(focObject);
	}
	
	public void setLogSubjectReference(long ref) {
		FObject prop = (FObject) getFocProperty(WFLogDesc.FLD_MASTER);
		if(prop != null) prop.setLocalReferenceInt(ref);
	}
	
	public void setEventType(int type){
		setPropertyMultiChoice(WFLogDesc.FLD_EVENT_TYPE, type);
	}

	public int getEventType(){
		return getPropertyMultiChoice(WFLogDesc.FLD_EVENT_TYPE);
	}

	public int getEventStatus(){
		return getPropertyMultiChoice(WFLogDesc.FLD_EVT_STATUS);
	}
	
	public void setEventStatus(int status){
		setPropertyMultiChoice(WFLogDesc.FLD_EVT_STATUS, status);
	}

	public String getEventTypeTitle(){
		return getPropertyMultiChoiceTitle(WFLogDesc.FLD_EVENT_TYPE);
	}

	public void setUser(FocUser user){
		setPropertyObject(WFLogDesc.FLD_USER, user);
	}

	public FocUser getUser(){
		return (FocUser) getPropertyObject(WFLogDesc.FLD_USER);
	}

	public void setDateTime(Date date){
		setPropertyDate(WFLogDesc.FLD_DATE_TIME, date);
	}

	public Date getDateTime(){
		return getPropertyDate(WFLogDesc.FLD_DATE_TIME);
	}

	public void setDescription(String str){
		setPropertyString(WFLogDesc.FLD_DESCERIPTION, str);
	}

	public String getDescription(){
		return getPropertyString(WFLogDesc.FLD_DESCERIPTION);
	}

	public void setTitle(WFTitle title){
		setPropertyObject(WFLogDesc.FLD_TITLE, title);
	}

	public WFTitle getTitle(){
		return (WFTitle) getPropertyObject(WFLogDesc.FLD_TITLE);
	}

	public void setOnBehalfOf(boolean onBehalfOf){
		setPropertyMultiChoice(WFLogDesc.FLD_ON_BEHALF_OF, onBehalfOf ? 1 : 0);
	}

	public boolean isOnBehalfOf(){
		int onBehalf = getPropertyMultiChoice(WFLogDesc.FLD_ON_BEHALF_OF);
		return onBehalf == 1 ? true : false;
	}

	public void setComment(String comment){
		setPropertyString(WFLogDesc.FLD_COMMENT, comment);
	}

	public String getComment(){
		return getPropertyString(WFLogDesc.FLD_COMMENT);
	}
	
	public void setChanges(String changes){
		if(changes != null) {
			String changesZipped = Utils.compressString(changes);
			if(changesZipped.length()>3999) {
				changes = changes.substring(0, 1900);
			}
		}
		setPropertyString(WFLogDesc.FLD_CHANGES, changes);
	}

	public String getChanges(){
		return getPropertyString(WFLogDesc.FLD_CHANGES);
	}
	
	public String getChangesCompressed(){
		FString prop = (FString) getFocProperty(WFLogDesc.FLD_CHANGES);
		return prop != null ? prop.getStringCompressed() : null;
	}
	
	public void setDocZip(String doc){
		if(doc != null) {
			String zippedDoc = Utils.compressString(doc);
			if(zippedDoc.length() > 3999) {
				doc = doc.substring(0, 1900);
			}
		}
		setPropertyString(WFLogDesc.FLD_DOC_ZIP, doc);
	}

	public String getDocZip(){
		return getPropertyString(WFLogDesc.FLD_DOC_ZIP);
	}
	
	public String getDocZipCompressed(){
		FString prop = (FString) getFocProperty(WFLogDesc.FLD_DOC_ZIP);
		return prop != null ? prop.getStringCompressed() : null;
	}
	
	public void setDocVersion(int changes){
		setPropertyInteger(WFLogDesc.FLD_DOC_VERSION, changes);
	}

	public int getDocVersion(){
		return getPropertyInteger(WFLogDesc.FLD_DOC_VERSION);
	}
	
	public void setDocHash(String changes){
		setPropertyString(WFLogDesc.FLD_DOC_HASH, changes);
	}

	public String getDocHash(){
		return getPropertyString(WFLogDesc.FLD_DOC_HASH);
	}
	
	public void setPreviousStage(WFStage stage){
		setPropertyObject(WFLogDesc.FLD_PREVIOUS_STAGE, stage);
	}

	public WFStage getPreviousStage(){
		return (WFStage) getPropertyObject(WFLogDesc.FLD_PREVIOUS_STAGE);
	}

	public void setTargetStage(WFStage stage){
		setPropertyObject(WFLogDesc.FLD_TARGET_STAGE, stage);
	}

	public WFStage getTargetStage(){
		return (WFStage) getPropertyObject(WFLogDesc.FLD_TARGET_STAGE);
	}
	
	public boolean getEventUndone(){
		return getPropertyBoolean(WFLogDesc.FLD_EVENT_UNDONE);
	}
	
	public void setEventUndone(boolean undone){
		setPropertyBoolean(WFLogDesc.FLD_EVENT_UNDONE, undone);
	}
	
	public String getSignedTransactionXML(){
    return getPropertyString(WFLogDesc.FLD_SIGNED_TRANSACTION_XML);
  }
  
  public void setSignedTransactionXML(String xmlContent){
    setPropertyString(WFLogDesc.FLD_SIGNED_TRANSACTION_XML, xmlContent);
  }

	@Override
	public String logEvent_GetEntityName() {
		FField  masterField = getThisFocDesc().getFieldByID(WFLogDesc.FLD_MASTER);
		FocDesc tableBeingLogged = masterField != null ? masterField.getFocDesc() : null;
		String storageName = tableBeingLogged != null ? tableBeingLogged.getStorageName() : "";
		return storageName;
	}

	@Override
	public long logEvent_GetEntityReference() {
		FObject prop = (FObject) getFocProperty(WFLogDesc.FLD_MASTER);
		long ref = prop != null ? prop.getLocalReferenceInt() : 0;
		return ref;
	}

	@Override
	public String logEvent_GetEntityCompanyName() {
		FocObject master = getObjectLogged();
		String compName = master != null && master.getCompany() != null ? master.getCompany().getName() : null;   
		return compName;
	}

	@Override
	public String logEvent_GetEntitySiteName() {
		FocObject master = getObjectLogged();
		WFSite site = master != null ? master.workflow_GetSite() : null;
		String siteName = site != null ? site.getName() : null;   
		return siteName;
	}

	@Override
	public String logEvent_GetEntityCode() {
		FocObject master = getObjectLogged();
		String code = master != null && master.code_getCode() != null ? master.code_getCode() : null;   
		return code;
	}

	@Override
	public Date logEvent_GetEntityDate() {
		FocObject master = getObjectLogged();
		return master != null ? master.getDate() : null;   
	}

	@Override
	public int logEvent_GetEventType() {
		return getEventType();
	}

	@Override
	public String logEvent_GetUsername() {
		String userName = "";
		
		FocUser user = getUser();
		if(user != null) {
			userName = user.getFullName();
			if(Utils.isStringEmpty(userName)) {
				userName = user.getName();
			}
		}
		return userName;
	}

	@Override
	public java.util.Date logEvent_GetDateTime() {
		return getDateTime();
	}

	@Override
	public String logEvent_GetComment() {
		return getComment();
	}

	@Override
	public String logEvent_GetChanges() {
		return getChanges();
	}
	
	@Override
	public String logEvent_GetChangesCompressed() {
		return getChangesCompressed();
	}
	
	@Override
	public long logEvent_GetLogEventReference() {
		return getReferenceInt();
	}

	@Override
	public int logEvent_GetStatus() {
		return getEventStatus();
	}

	@Override
	public String logEvent_GetDocumentZipped() {
		return getDocZip();
	}
	
	@Override
	public String logEvent_GetDocumentZippedCompressed() {
		return getDocZipCompressed();
	}

	@Override
	public String logEvent_GetDocumentHash() {
		return getDocHash();
	}

	@Override
	public int logEvent_GetDocumentVersion() {
		return getDocVersion();
	}
}
