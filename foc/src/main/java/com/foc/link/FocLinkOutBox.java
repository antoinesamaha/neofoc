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
package com.foc.link;

import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class FocLinkOutBox extends FocObject {

	public FocLinkOutBox(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public FocUser getFromUser(){
		return (FocUser) getPropertyObject(FocLinkOutBoxDesc.FLD_FROM_USER);
	}
	
	public void setFromUser(FocUser user){
		setPropertyObject(FocLinkOutBoxDesc.FLD_FROM_USER, user);
	}
	
	public String getStorage(){
		return getPropertyString(FocLinkOutBoxDesc.FLD_STORAGE);
	}
	
	public void setStorage(String storage){
		setPropertyString(FocLinkOutBoxDesc.FLD_STORAGE, storage);
	}
	
	public int getTransactionType(){
		return getPropertyInteger(FocLinkOutBoxDesc.FLD_TRANSACTION_TYPE);
	}
	
	public void setTransactionType(int transactionType){
		setPropertyInteger(FocLinkOutBoxDesc.FLD_TRANSACTION_TYPE, transactionType);
	}
	
	public String getXmlMessage(){
		return getPropertyString(FocLinkOutBoxDesc.FLD_XML_MESSAGE);
	}
	
	public void setXmlMessage(String xmlMessage){
		setPropertyString(FocLinkOutBoxDesc.FLD_XML_MESSAGE, xmlMessage);
	}
	
	public FocList getDetailsList(){
		FocList focList = getPropertyList(FocLinkOutBoxDesc.FLD_DETAILS_LIST);
		if(focList != null){
			focList.setDirectImpactOnDatabase(true);
			focList.setDirectlyEditable(false);
		}
		return focList;
	}
	
  public static FocDesc getFocDesc() {
    return FocLinkOutBoxDesc.getInstance();
  }

}
