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
package com.foc.jasper;

import java.util.HashMap;

import com.foc.Globals;
import com.foc.business.status.IStatusHolder;
import com.foc.business.status.StatusHolderDesc;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.map.WFTransactionConfig;
import com.foc.dataDictionary.FocDataDictionary;
import com.foc.dataDictionary.IFocDataResolver;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.list.FocList;
import com.foc.property.FAttributeLocationProperty;
import com.foc.property.FProperty;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class JRFocObjectParameters extends HashMap<String, Object>{

	private FocObject focObject = null;
	
	public JRFocObjectParameters(FocObject focObject){
		this.focObject = focObject;
		put("SUBREPORT_DIR", "reports/");
		
		IStatusHolder iStatusHolder = (focObject instanceof IStatusHolder) ? (IStatusHolder) focObject : null;
		boolean doNotPRintTheProposalTag = false;
				
		if(focObject instanceof IWorkflow && ((IWorkflow)focObject).iWorkflow_getWorkflow() != null){
      FocList             sigList             = ((IWorkflow)focObject).iWorkflow_getWorkflow().newWorkflowSignatureList();
      JRFocListDataSource signatureDataSource = new JRFocListDataSource(sigList);
      put("DATA_SOURCE_WORKFLOW_SIGNATURES", signatureDataSource);
      
      WFTransactionConfig assignment = focObject.workflow_getTransactionConfig();
      if(assignment != null){
      	String docTitle = "";
      	if(iStatusHolder.getStatusHolder().getStatus() == StatusHolderDesc.STATUS_PROPOSAL || iStatusHolder.getStatusHolder().getStatus() == StatusHolderDesc.STATUS_SYSTEM){
      		docTitle = assignment.getTransactionTitle_Proposal();	
      	}else{
      		docTitle = assignment.getTransactionTitle();
      	}
      	doNotPRintTheProposalTag = assignment.doNotPrintTheProposalTag(); 
      	put("DOC_TITLE", docTitle);
      }
		}
		
		if(iStatusHolder != null && iStatusHolder.getStatusHolder() != null){
			if(doNotPRintTheProposalTag){
				put("X_STATUS", "");	
			}else{
				put("X_STATUS", iStatusHolder.getStatusHolder().getPrintedStatus());
			}
		}
	}
	
	public void dispose(){
		focObject = null;
		clear();
	}

	@Override
	public boolean containsKey(Object key) {
		boolean contains = super.containsKey(key);
		if(!contains){
			contains = get(key) != null;
		}
		return contains;
	}

	@Override
	public Object get(Object key) {
		Globals.logString("JRFocObjectParameters.get( "+key+" )");
		Object retObj = super.get(key);
		try {
			if(retObj == null && focObject != null){
		    FFieldPath fieldPath = FAttributeLocationProperty.newFieldPath(false, (String)key, focObject.getThisFocDesc());
		    if(fieldPath != null && fieldPath.size() > 0 && fieldPath.get(0) != FField.NO_FIELD_ID){
			    FProperty  prop = fieldPath.getPropertyFromObject(focObject);
			    if(prop != null){
	//		    	retObj = prop.getObject();
			    	if(prop.getObject() != null && prop.getObject() instanceof String){//&& prop instanceof FBlobStringProperty
			    		String val = String.valueOf(prop.getObject());
		    			retObj = Utils.htmlToText(val);
		    		}else{
		    			retObj = prop.getObject();
		    		}
			    }else{
			    	FField    fld   = fieldPath.getFieldFromDesc(focObject.getThisFocDesc());
			    	FProperty prop2 = fld != null ? fld.newProperty(null) : null;
			    	if(prop2 != null){
				    	retObj = prop2.getObject();
				    	prop2.dispose();
			    	}
			    }
		    }
		    if(retObj == null){
		    	IFocDataResolver focDataResolver = FocDataDictionary.getInstance().getParameter(key+"");
		    	retObj = focDataResolver != null ? focDataResolver.getValue(null, null) : null;
		    }
			}
		}catch(Exception e) {
			Globals.logException(e);
		}
		return retObj;
	}

	public FocObject getFocObject() {
		return focObject;
	}
	
	/*
	@Override
	public void clear() {
		int debug = 3;
	}


	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return null;
	}

	@Override
	public Object get(Object key) {
    FFieldPath fieldPath = FAttributeLocationProperty.newFieldPath(false, (String)key, focObject.getThisFocDesc());
    FProperty  prop      = fieldPath.getPropertyFromObject(focObject);
    Object     retObj    = null;
    if(prop != null){
    	retObj = prop.getObject();
    }
		
		return retObj;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set<String> keySet() {
		return null;
	}

	@Override
	public Object put(String key, Object value) {
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		int debug = 3;
	}

	@Override
	public Object remove(Object key) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Collection<Object> values() {
		return null;
	}
	*/
}
