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
package com.foc.business.workflow.map;

import java.util.ArrayList;
import java.util.Iterator;

import com.fab.gui.xmlView.IXMLViewDictionary;
import com.foc.Globals;
import com.foc.admin.FocUser;
import com.foc.business.workflow.WFOperator;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFTitle;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.shared.xmlView.XMLViewKey;

@SuppressWarnings("serial")
public class WFSignature extends FocObject {
	
	public WFSignature(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public String getTransactionContext(){
		return getPropertyString(WFSignatureDesc.FLD_TRANSACTION_CONTEXT);
	}
	
	public void setTransactionContext(String transactionContext){
		setPropertyString(WFSignatureDesc.FLD_TRANSACTION_CONTEXT, transactionContext);
	}
	
	public FocUser getFocUser(){
		return (FocUser) getPropertyObject(WFSignatureDesc.FLD_USER);
	}
	
	public void setUser(FocUser user){
		setPropertyObject(WFSignatureDesc.FLD_USER, user);
	}
	
	public String getTransactionView(){
		return getPropertyString(WFSignatureDesc.FLD_TRANSACTION_VIEW);
	}
	
	public void setTransactionView(String transactionView){
		setPropertyString(WFSignatureDesc.FLD_TRANSACTION_VIEW, transactionView);
	}
	
	public String getConditionToRequireSignature(){
		return getPropertyString(WFSignatureDesc.FLD_CONDITION_TO_REQUIRE_SIGNATURE);
	}
	
	public void setConditionToRequireSignature(String formula){
		setPropertyString(WFSignatureDesc.FLD_CONDITION_TO_REQUIRE_SIGNATURE, formula);
	}
	
	public WFTitle getTitle(int i){
		return (WFTitle) getPropertyObject(WFSignatureDesc.FLD_TITLE_FIRST+i);
	}

	public WFStage getPreviousStage(){
		return (WFStage) getPropertyObject(WFSignatureDesc.FLD_PREVIOUS_STAGE);
	}

	public void setPreviousStage(WFStage stage){
		setPropertyObject(WFSignatureDesc.FLD_PREVIOUS_STAGE, stage);
	}

	public WFStage getTargetStage(){
		return (WFStage) getPropertyObject(WFSignatureDesc.FLD_TARGET_STAGE);
	}

	public boolean isTitleToSigne(WFTitle title){
		return getTitleIndex(title) >= 0;
	}
	
	public String getSignCaption(){
		return getPropertyString(WFSignatureDesc.FLD_SignCaption);
	}
	
	public void setSignCaption(String formula){
		setPropertyString(WFSignatureDesc.FLD_SignCaption, formula);
	}
	
	public String getRejectCaption(){
		return getPropertyString(WFSignatureDesc.FLD_RejectCaption);
	}
	
	public void setRejectCaption(String caption){
		setPropertyString(WFSignatureDesc.FLD_RejectCaption, caption);
	}
	
	public boolean isRejectHidden() {
		return getPropertyBoolean(WFSignatureDesc.FLD_RejectHidden);
	}
	
	public int getTitleIndex(WFTitle title){
		int idx = -1;
		for(int i=0; i<WFSignatureDesc.FLD_TITLE_COUNT && idx < 0; i++){
			WFTitle currentTitle = getTitle(i);
			if(currentTitle != null && currentTitle.getName().equals(title.getName())){
				idx = i;
			}
		}
		return idx;
	}
	
	public int getTitleIndex_ForUserAndArea(WFSite area){
		int 									idx 									= -1;
		if(area != null){
			ArrayList<WFOperator> titlesAndAreasForUser = FocUser.getOperatorsArrayForThisUser_AllAreas(true);
			
			for(int t=0; t<titlesAndAreasForUser.size() && idx < 0; t++){
				WFOperator operator = titlesAndAreasForUser.get(t);
				if(operator != null && operator.getArea() != null && operator.getArea().getName().equals(area.getName())){
					WFTitle title = operator.getTitle();
					idx = getTitleIndex(title);
				}
			}
		}
		return idx;
	}
	
	@Override
	public ArrayList<String> getMultipleChoiceStringBased_ArrayOfValues(int fieldID) {
		ArrayList<String> array = super.getMultipleChoiceStringBased_ArrayOfValues(fieldID);
		if(Globals.getIFocNotification() != null && Globals.getIFocNotification().getXMLViewDictionary() != null){
			IXMLViewDictionary ixmlViewDictionary = Globals.getIFocNotification().getXMLViewDictionary();
			if(ixmlViewDictionary != null){
				
				if(array == null){
					array = new ArrayList<String>();
				}
				
				Iterator<XMLViewKey> xmlViewKeyItr = ixmlViewDictionary.newIterator();
				if(xmlViewKeyItr != null){
					
					if(fieldID == WFSignatureDesc.FLD_TRANSACTION_CONTEXT){
						while(xmlViewKeyItr.hasNext()){
							XMLViewKey xmlViewKey = xmlViewKeyItr.next();
							if(xmlViewKey != null){
								String storageName = xmlViewKey.getStorageName();
								
								String context = xmlViewKey.getContext();
								if(!array.contains(context)){
									array.add(context);
								}
							}
						}
					}else if(fieldID == WFSignatureDesc.FLD_TRANSACTION_VIEW){
						while(xmlViewKeyItr.hasNext()){
							XMLViewKey xmlViewKey = xmlViewKeyItr.next();
							if(xmlViewKey != null){
								String storageName = xmlViewKey.getStorageName();
								
								String userView = xmlViewKey.getUserView();
								if(!array.contains(userView)){
									array.add(userView);
								}
							}
						}
					}
					
				}
			}
		}
		return array;
	}

}
