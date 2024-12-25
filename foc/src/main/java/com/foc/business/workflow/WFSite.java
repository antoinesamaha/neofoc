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
package com.foc.business.workflow;

import java.util.ArrayList;

import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.business.workflow.rights.RightLevel;
import com.foc.business.workflow.rights.UserTransactionRight;
import com.foc.business.workflow.rights.UserTransactionRightDesc;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.shared.json.B01JsonBuilder;

@SuppressWarnings("serial")
public class WFSite extends FocObject {
	
	public static final String DEFAULT_SITE_NAME = "HQ";
	
	public WFSite(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public void dispose(){
		super.dispose();
	}
	
  public String getAddress(){
  	return getPropertyString(WFSiteDesc.FLD_ADDRESS);
  }
  
  public void setAddress(String address){
  	setPropertyString(WFSiteDesc.FLD_ADDRESS, address);
  }
  
  public boolean hasAncestorOrEqualTo(WFSite parent){
  	boolean has = false;
  	WFSite site = (WFSite) this;
  	while(!has && site != null){
  		has = site.equalsRef(parent);
  		WFSite fatherSite = (WFSite) site.getFatherObject();
  		if(fatherSite == site) break;
  		site = fatherSite;
  	}
  	return has;
  }

	public FocList getOperatorList(){
		FocList list = getPropertyList(WFSiteDesc.FLD_OPERATOR_LIST);
		if(list != null && list.getListOrder() == null){
			list.setListOrder(new FocListOrder(WFOperatorDesc.FLD_USER, WFOperatorDesc.FLD_AREA));
			list.setDirectImpactOnDatabase(false);
			list.setDirectlyEditable(true);
		}
		return list;
	}
	
	public String getTransactionCodePrefix(){
		return getPropertyString(WFSiteDesc.FLD_TRANSACTION_PREFIX);
	}
	
  public void setTransactionCodePrefix(String str){
  	String other = str;
  	if(str.length() > WFSiteDesc.LEN_TRANSACTION_PREFIX){
  		other = str.substring(0, WFSiteDesc.LEN_TRANSACTION_PREFIX);
  	}
  	setPropertyString(WFSiteDesc.FLD_TRANSACTION_PREFIX, other);
  }
	
  /*
	public FocList getSuperUserList(){
		FocList list = getPropertyList(WFSiteDesc.FLD_SUPER_USER_LIST);
		if(list != null && list.getListOrder() == null){
			list.setListOrder(new FocListOrder(WFSuperUserDesc.FLD_USER, WFSuperUserDesc.FLD_AREA));
			list.setDirectImpactOnDatabase(false);
			list.setDirectlyEditable(true);
		}
		return list;
	}
	*/

	public FocList getUserTransactionRightsList(){
		FocList list = getPropertyList(WFSiteDesc.FLD_USER_TRANSACTION_RIGHTS_LIST);
		if(list != null && list.getListOrder() == null){
			list.setListOrder(new FocListOrder(UserTransactionRightDesc.FLD_USER, UserTransactionRightDesc.FLD_SITE));
			list.setDirectImpactOnDatabase(false);
			list.setDirectlyEditable(true);
		}
		return list;
	}

	public ArrayList<WFTitle> newTitlesArrayForCurrentUser(){
		ArrayList<WFTitle> array = new ArrayList<WFTitle>();
		WFSite area = this;
		while(area != null){
			FocList opList = area.getOperatorList();
			for(int i=0; i<opList.size(); i++){
				WFOperator operator = (WFOperator) opList.getFocObject(i);
				if(operator.getUser().equalsRef(Globals.getApp().getUser())){
					array.add(operator.getTitle());
				}
			}
			area = (WFSite) area.getFatherObject();
		}
		return array;
	}
	
	/*
	private boolean scanSuperUserAndCheckIfCanDoActionForThisTransaction(String transactionBDTitle, int action){
		boolean can = false;
		
		ArrayList<WFTitle> titlesArray = newTitlesArrayForCurrentUser();
		
		FocList list = getSuperUserList();
		list.loadIfNotLoadedFromDB();
		for(int i=0; i<list.size() && !can; i++){
			WFSuperUser superUser = (WFSuperUser) list.getFocObject(i);
			if(			(superUser.getUser().equalsRef(Globals.getApp().getUser()) || titlesArray.contains(superUser.getTitle())) 
					&& 	superUser.getAction() == action
					&& (superUser.getTransactionDBTitle().isEmpty() || superUser.getTransactionDBTitle().equals(transactionBDTitle))){
				can = true;
			}
		}
		
		if(titlesArray != null){
			titlesArray.clear();
			titlesArray = null;
		}

		return can;
	}
	*/

	/*
	public boolean canThisUser_DoAction_OnThisTransaction(String transactionBDTitle, int action){
		boolean can = false;
		WFSite area = this;
		while(area != null && !can){
			can  = scanSuperUserAndCheckIfCanDoActionForThisTransaction(transactionBDTitle, action);
			area = (WFSite) area.getFatherObject();
		}
		return can;
	}
	*/
	
	public WFOperator findOrAddOperator(WFTitle title, FocUser newUser) {
		WFOperator operator = null;
		FocList list = getOperatorList();
		if(list != null){
			for(int i=0; i<list.size(); i++){
				WFOperator op = (WFOperator) list.getFocObject(i);
				if(FocObject.equal(op.getUser(), newUser) && FocObject.equal(op.getTitle(), title)){
					operator = op;
				}
			}
			if(operator == null){
				operator = (WFOperator) list.newEmptyItem();
				operator.setUser(newUser);
				operator.setTitle(title);
				operator.validate(true);
			}
		}
		return operator;
	}
	
	public UserTransactionRight findOrAddUserTransactionRight(WFTitle title, FocUser user, String transactionDBTitle, RightLevel rightLevel) {
		UserTransactionRight userTransactionRight = null;
		FocList list = getUserTransactionRightsList();
		if(list != null){
			for(int i=0; i<list.size(); i++){
				UserTransactionRight op = (UserTransactionRight) list.getFocObject(i);
				if(			FocObject.equal(op.getTitle(), title) 
						&& 	FocObject.equal(op.getUser(), user)
						&&  (
							   (transactionDBTitle == null || transactionDBTitle.isEmpty()) && (op.getTransactionDBTitle() == null || op.getTransactionDBTitle().isEmpty())
							|| (transactionDBTitle != null && op.getTransactionDBTitle() != null && op.getTransactionDBTitle().equals(transactionDBTitle))
							)
						){
					userTransactionRight = op;
				}
			}
			if(userTransactionRight == null){
				userTransactionRight = (UserTransactionRight) list.newEmptyItem();
				userTransactionRight.setTitle(title);
				userTransactionRight.setTransactionDBTitle(transactionDBTitle);
				userTransactionRight.setUser(user);
			}
			if(userTransactionRight != null){
				userTransactionRight.setWFRightsLevel(rightLevel);
				userTransactionRight.validate(true);
			}
		}
		return userTransactionRight;
	}	
	
	@Override
	public void toJson_Detailed(B01JsonBuilder builder) {
    builder.beginObject();
    appendKeyValueForFieldName(builder, null, FField.REF_FIELD_NAME);
    appendKeyValueForFieldName(builder, null, FField.FNAME_NAME);
    appendKeyValueForFieldName(builder, null, FField.FNAME_DESCRIPTION);
    appendKeyValueForFieldName(builder, null, WFSiteDesc.FNAME_TRANSACTION_PREFIX);
    
    appendKeyValueForFieldName(builder, null, WFOperatorDesc.DB_TABLE_NAME+"_LIST");
    appendKeyValueForFieldName(builder, null, UserTransactionRightDesc.DB_TABLE_NAME+"_LIST");
    
    builder.endObject();
	}
	
	@Override
	public void toJson_Embedded(B01JsonBuilder builder) {
    builder.beginObject();
    appendKeyValueForFieldName(builder, null, FField.REF_FIELD_NAME);
    appendKeyValueForFieldName(builder, null, FField.FNAME_NAME);
    builder.endObject();
	}
	
	@Override
	public void toJson_InList(B01JsonBuilder builder) {
    builder.beginObject();
    appendKeyValueForFieldName(builder, null, FField.REF_FIELD_NAME);
    appendKeyValueForFieldName(builder, null, FField.FNAME_NAME);
    appendKeyValueForFieldName(builder, null, FField.FNAME_DESCRIPTION);
    appendKeyValueForFieldName(builder, null, WFSiteDesc.FNAME_TRANSACTION_PREFIX);
    builder.endObject();
	}
	
}
