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
package com.foc.admin;

import java.util.ArrayList;
import java.util.Arrays;

import com.foc.Globals;
import com.foc.business.workflow.WorkflowTransactionFactory;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.signing.WFTransactionWrapper;
import com.foc.business.workflow.signing.WFTransactionWrapperList;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.util.Utils;

@SuppressWarnings("serial")
public class FocUserHistory extends FocObject implements FocUserHistoryConst {

  public FocUserHistory(FocConstructor constr) {
    super(constr);
    newFocProperties();
    setFullscreen(FocUserHistoryConst.MODE_FULLSCREEN);
  }

  public FocUser getUser() {
    return (FocUser) getPropertyObject(FLD_USER);
  }

  public void setUser(FocUser user) {
    setPropertyObject(FLD_USER, user);
  }
  
  public int isFullscreen(){
    return getPropertyInteger(FLD_FULLSCREEN);
  }
  
  public void setFullscreen(int mode){
    setPropertyInteger(FLD_FULLSCREEN, mode);
  }
  
  public String getHistory() {
    return getPropertyString(FLD_HISTORY);
  }

  public void addHistory(String menuCode) {
    String result = "";
    if (!getHistory().equals("")) {
      String[] fullHistoryString = getHistory().split(HISTORY_DELIMITER);
      ArrayList fullHistoryArray = new ArrayList(Arrays.asList(fullHistoryString));

      if (fullHistoryArray != null) {

        // If the history contains more than HISTORY_SIZE entries, trim it down.
        while (fullHistoryArray.size() >= HISTORY_SIZE) {
          fullHistoryArray.remove(0);
        }

        boolean found = false;
        // Trying to find if the page we're visiting has been visited before.
        for (int i = 0; i < fullHistoryArray.size(); i++) {
          String currentString = (String) fullHistoryArray.get(i);
          String[] currentStringArray = currentString.split(INNER_DELIMITER);

          if (currentStringArray[0].equals(menuCode)) {
            try {
              Integer count = Integer.parseInt(currentStringArray[1]) + 1;
              currentStringArray[1] = count.toString();
              String swapString = currentStringArray[0] + INNER_DELIMITER + currentStringArray[1];
              fullHistoryArray.remove(i);
              fullHistoryArray.add(swapString);
              found = true;
              break;
            } catch (NumberFormatException e) {
              e.printStackTrace();
            }
          }
        }

        if (!found) {
          fullHistoryArray.add(menuCode + INNER_DELIMITER + "1");
        }

        result = (String) fullHistoryArray.get(0);
        for (int i = 1; i < fullHistoryArray.size(); i++) {
          result = result.concat(HISTORY_DELIMITER + (String) fullHistoryArray.get(i));
        }
      }
    } else {
      result = menuCode + INNER_DELIMITER + "1";
    }

    setPropertyString(FLD_HISTORY, result);
  }
  
  public void clearHistory(){
    setPropertyString(FLD_HISTORY, "");
  }

  public boolean findHistory(String menuCode) {
    boolean result = false;
    if (!getHistory().equals("")) {
      String[] fullHistoryString = getHistory().split(HISTORY_DELIMITER);

      for (int i = 0; i < fullHistoryString.length; i++) {
        String[] innerString = fullHistoryString[i].split(INNER_DELIMITER);
        if (innerString[0].equals(menuCode)) {
          result = true;
          break;
        }
      }
    }

    return result;
  }

  // -----------------------------------------
  // Recent Transactions Visited
  // -----------------------------------------
  
  public String getRecentTransactions() {
    return getPropertyString(FLD_RECENT_TRANSACTIONS);
  }

  public WFTransactionWrapperList newRecentTransactionList(){
  	WFTransactionWrapperList transactionWrapperList = new WFTransactionWrapperList();
  	String transactionHistory = getRecentTransactions();
  	
    String[] fullHistoryString = transactionHistory.split(HISTORY_DELIMITER);
    ArrayList<String> fullHistoryArray = new ArrayList<String>(Arrays.asList(fullHistoryString));

    for(int i=fullHistoryArray.size()-1; i>=0; i--){
    	String transKey = fullHistoryArray.get(i);
    	String[] transKeyArray = transKey.split(INNER_DELIMITER);	
    	
    	if(transKeyArray.length == 2){
	    	String transName   = transKeyArray[0];
	    	String transRef    = transKeyArray[1];
	    	int    transRefInt = Utils.parseInteger(transRef, 0);
	    	FocDesc iWorkflowDesc = (FocDesc) WorkflowTransactionFactory.getInstance().findWorkflowDesc_ByDBTitle(transName);
	    	
	    	if(iWorkflowDesc != null && transRefInt > 0){
	    		IWorkflow iWorkflow = null;
	    		if(iWorkflowDesc.isListInCache()){
			    	FocList transList = iWorkflowDesc.getFocList(FocList.LOAD_IF_NEEDED);
			    	iWorkflow = (IWorkflow) transList.searchByReference(transRefInt);
	    		}else{
	    			iWorkflow = (IWorkflow) iWorkflowDesc.newObject(transRefInt, false);
	    		}
	    		
		    	if(iWorkflow != null){
		    		
		    		boolean isForCurrentCompany = Globals.getApp().getCurrentCompany().equalsRef(((FocObject)iWorkflow).getCompany());
		    		
		    		isForCurrentCompany =    isForCurrentCompany || (
				    				                     iWorkflow.iWorkflow_getWorkflow() != null
				    								          && Globals.getApp().getCurrentCompany() != null
				    				                  && iWorkflow.iWorkflow_getWorkflow().getSite_1() != null
				    				                  && Globals.getApp().getCurrentCompany().equalsRef(iWorkflow.iWorkflow_getWorkflow().getSite_1().getCompany())); 
		    		
		    		boolean addToHistoryList = false;
			    	if(			Globals.getApp().getCurrentCompany() != null 
			    			&& 	iWorkflow.iWorkflow_getWorkflow() != null 
			    			&&  isForCurrentCompany
//			    			&&  Globals.getApp().getCurrentCompany().equalsRef(((FocObject)iWorkflow).getCompany()) 
			    		  && 	Globals.getApp().getCurrentSite() != null 
			    		  &&	(iWorkflow.iWorkflow_getWorkflow().getSite_1() == null || Globals.getApp().getCurrentSite().equalsRef(iWorkflow.iWorkflow_getWorkflow().getSite_1()))
			    		  ){
			    		addToHistoryList = true;
			    	}
			    	
			    	if(iWorkflow != null && addToHistoryList){
				    	WFTransactionWrapper transactionWrapper = (WFTransactionWrapper) transactionWrapperList.newEmptyItem();
				    	transactionWrapper.setWorkflow(iWorkflow);
				    	transactionWrapperList.add(transactionWrapper);
			    	}
		    	}
	    	}
    	}
    }
  	return transactionWrapperList;
  }
  
  public void addRecentTransaction(FocObject focObject) {
  	if(focObject != null && focObject instanceof IWorkflow && !focObject.isCreated() && focObject.getReference() != null && focObject.getThisFocDesc() != null && focObject.getThisFocDesc() instanceof IWorkflowDesc){
      int ref = focObject.getReference().getInteger();
      IWorkflowDesc focDesc = (IWorkflowDesc) focObject.getThisFocDesc();
      
      if(ref > 0 && focDesc != null){
      	String transKey = focDesc.iWorkflow_getDBTitle()+INNER_DELIMITER+ref;
	  		
		    String result = "";
		    String recentTrans = getRecentTransactions();
		    if (recentTrans != null && !recentTrans.equals("")) {
		      String[] fullHistoryString = recentTrans.split(HISTORY_DELIMITER);
		      ArrayList fullHistoryArray = new ArrayList(Arrays.asList(fullHistoryString));
		
		      if (fullHistoryArray != null) {
		        // If the history contains more than HISTORY_SIZE entries, trim it down.
		
		        int idxFound = -1;
		        for (int i = 0; i < fullHistoryArray.size() && idxFound < 0; i++) {
		          String currentString = (String) fullHistoryArray.get(i);
		          
		          if(currentString.equals(transKey)){
		          	idxFound = i;
		          }
		        }
		        
		        if(idxFound < 0){
			        while (fullHistoryArray.size() >= HISTORY_SIZE) {
			          fullHistoryArray.remove(0);
			        }
		        	fullHistoryArray.add(transKey);
		        }else{
		        	for(int i=idxFound; i<fullHistoryArray.size()-1; i++){
		        		fullHistoryArray.set(i, fullHistoryArray.get(i+1));
		        	}
		        	fullHistoryArray.set(fullHistoryArray.size()-1, transKey);
		        }
		
		        result = (String) fullHistoryArray.get(0);
		        for (int i = 1; i < fullHistoryArray.size(); i++) {
		          result = result.concat(HISTORY_DELIMITER + (String) fullHistoryArray.get(i));
		        }
		      }
		    } else {
		      result = transKey;
		    }
		
		    setPropertyString(FLD_RECENT_TRANSACTIONS, result);
	  	}
  	}
  }
  
  public void clearRecentTransdactions(){
    setPropertyString(FLD_RECENT_TRANSACTIONS, "");
  }
}
