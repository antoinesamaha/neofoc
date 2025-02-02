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
package com.foc.business.notifier;

import java.sql.Date;

import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class DocMsg extends FocObject{

	public DocMsg(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public FocUser getSender(){
		return (FocUser) getPropertyObject(DocMsgDesc.FLD_SENDER);
	}
	
	public void setSender(FocUser sender){
		setPropertyObject(DocMsgDesc.FLD_SENDER, sender);
	}
	
	public FocUser getReceiver(){
		return (FocUser) getPropertyObject(DocMsgDesc.FLD_RECEIVER);
	}
	
	public void setReceiver(FocUser receiver){
		setPropertyObject(DocMsgDesc.FLD_RECEIVER, receiver);
	}
	
	public boolean isMessageSent(){
		return getPropertyBoolean(DocMsgDesc.FLD_SENT);
	}
	
	public void setMessageSent(boolean sent){
		setPropertyBoolean(DocMsgDesc.FLD_SENT, sent);
	}
	
	public Date getSendDate(){
		return getPropertyDate(DocMsgDesc.FLD_SEND_DATE);
	}
	
	public void setSendDate(Date date){
		setPropertyDate(DocMsgDesc.FLD_SEND_DATE, date);
	}
	
	public boolean isMessageRead(){
		return getPropertyBoolean(DocMsgDesc.FLD_IS_READ);
	}
	
	public void setMessageRead(boolean isRead){
		setPropertyBoolean(DocMsgDesc.FLD_IS_READ, isRead);
	}
	
	public Date getReadDate(){
		return getPropertyDate(DocMsgDesc.FLD_READ_DATE);
	}
	
	public void setReadDate(Date date){
		setPropertyDate(DocMsgDesc.FLD_READ_DATE, date);
	}
	
	public DocMsgContent getDocMsgContent(){
		return (DocMsgContent) getPropertyObject(DocMsgDesc.FLD_DOC_MSG_CONTENT);
	}
	
	public void setDocMsgContent(DocMsgContent docMsgContent){
		setPropertyObject(DocMsgDesc.FLD_DOC_MSG_CONTENT, docMsgContent);
	}
}
