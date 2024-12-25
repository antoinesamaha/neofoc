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

import java.util.HashMap;

import com.foc.business.workflow.WFTitle;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.formula.FocSimpleFormulaContext;
import com.foc.formula.Formula;
import com.foc.list.FocList;
import com.foc.util.Utils;

public class WFMap extends FocObject {
	
	//private ArrayList<WFStage> stagesSorted = null;
	private HashMap<Integer, WFSignature> prevStageToSignature = null;
	
	public WFMap(FocConstructor constr){
		super(constr);
		newFocProperties();
	}
	
	public void dispose(){
		super.dispose();
		resetPreviousStage2SignatureMap();
	}
	
	public void resetPreviousStage2SignatureMap(){
		if(prevStageToSignature != null){
			prevStageToSignature.clear();
			prevStageToSignature = null;
		}		
	}
	
	private HashMap<Integer, WFSignature> getPrevStageToSignature(){
		if(prevStageToSignature == null){
			prevStageToSignature = new HashMap<Integer, WFSignature>();
			FocList list = getSignatureList();
			list.reloadFromDB();
			for(int i=0; i<list.size(); i++){
				WFSignature signature = (WFSignature) list.getFocObject(i);
				int prevStageRef = 0;
				if(signature.getPreviousStage() != null){
					prevStageRef = signature.getPreviousStage().getReference().getInteger();
				}
				prevStageToSignature.put(prevStageRef, signature);
				
			}
		}
		return prevStageToSignature;
	}

	public int findSignatureIndexForStartStage(WFStage stage){
		int index = -1;
		FocList list = getPropertyList(WFMapDesc.FLD_SIGNATURE_LIST);
		for(int i=0; i<list.size() && index < 0; i++){
			WFSignature s = (WFSignature) list.getFocObject(i);
			if(FocObject.equal(s.getPreviousStage(),stage)){
				index = i;
			}
		}
		return index;
	}

	public WFTitle getTitleInitialEdit() {
		return (WFTitle) getPropertyObject(WFMapDesc.FLD_TITLE_INITIAL_EDIT);
	}
	
	public WFStage getStageOfLockBegin() {
		return (WFStage) getPropertyObject(WFMapDesc.FLD_STAGE_OF_LOCK_BEGIN);
	}
	
	public FocList getSignatureList(){
		FocList list = getPropertyList(WFMapDesc.FLD_SIGNATURE_LIST);
		if(list != null){
			list.setListOrder(null);
			list.setOrderComparator(null);
			
			int position = 0;
			int index = findSignatureIndexForStartStage(null);
			while(index >= 0 && position<list.size()){
				WFSignature signature = (WFSignature) list.getFocObject(index);
				WFStage nextStage = signature.getTargetStage(); 
				
			  list.switchElementsAtPositions(index, position);
			  
			  index = findSignatureIndexForStartStage(nextStage);
				position++;
			}
			list.setDirectImpactOnDatabase(false);
			list.setDirectlyEditable(true);
		}
		return list;
	}
	
	public WFSignature findSignature_PreviousStage(FocObject focObject, WFStage stage){
		WFSignature foundSig = null;
		if(getPrevStageToSignature() != null){
			int refToSearchFor = 0;
			if(stage != null) refToSearchFor = stage.getReference().getInteger();
			foundSig = getPrevStageToSignature().get(refToSearchFor);
			
			if(foundSig != null && !Utils.isStringEmpty(foundSig.getConditionToRequireSignature())){
				//In this case we need to chack if the signature is to be skipped by condition
				
				FocSimpleFormulaContext formulaContext = new FocSimpleFormulaContext(new Formula(foundSig.getConditionToRequireSignature()));
				Object result = formulaContext.compute(focObject);
				boolean stageRequired = result != null && result instanceof Boolean ? (Boolean)result : false;
				
				if(!stageRequired){
					if(foundSig.getTargetStage() == null){
						foundSig = null;
					}else{
						foundSig = findSignature_PreviousStage(focObject, foundSig.getTargetStage());
					}
				}
			}
		}
		
		return foundSig;
	}
	 
}
