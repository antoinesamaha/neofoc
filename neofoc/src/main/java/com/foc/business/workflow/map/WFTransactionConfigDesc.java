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

import com.foc.Globals;
import com.foc.admin.FocVersion;
import com.foc.business.company.Company;
import com.foc.business.company.CompanyDesc;
import com.foc.business.workflow.WFSite;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WorkflowModule;
import com.foc.business.workflow.WorkflowTransactionFactory;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FCompanyField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.foc.property.validators.FNumLimitValidator;

public class WFTransactionConfigDesc extends FocDesc {
	public static final int FLD_TRANSACTION                             =  1;
	public static final int FLD_MAP                                     =  2;
	public static final int FLD_DEFAULT_AREA                            =  3;
	public static final int FLD_APPROVAL_METHOD                         =  4;
	public static final int FLD_CODE_PREFIX_USE_SITE_PREFIX             =  5;
	public static final int FLD_CODE_PREFIX_TRANSACTION_PREFIX          =  6;
	public static final int FLD_CODE_PREFIX_TRANSACTION_PREFIX_PROPOSAL =  7;
	public static final int FLD_CODE_PREFIX_NUMBER_OF_DIGITS            =  8;
	public static final int FLD_PROMPT_FOR_APPROVE_UPON_VALIDATION      =  9;
	public static final int FLD_TRANSACTION_TITLE_PROPOSAL              = 10;
	public static final int FLD_TRANSACTION_TITLE                       = 11;
	public static final int FLD_INCLUDE_PROJECT_CODE                    = 12;
	public static final int FLD_LEAVE_CODE_EMPTY                        = 13;
	public static final int FLD_CODE_BY_STE                             = 14;
	public static final int FLD_FORBID_ANCESTOR_TO_ACCESS               = 15;
	
	public static final int FLD_WF_FIELD_LOCK_STAGE_LIST                = 19;
	public static final int FLD_FUNCTIONAL_STAGE_LIST                   = 20;
	
	public static final int APPROVAL_METHOD_BY_WORKFLOW = 0;
	public static final int APPROVAL_METHOD_AT_CREATION = 1;
	
	public static final int LEN_TRANSACTION_NAME        = 40;
	
	public static final String DB_TABLE_NAME = "WF_ASSIGNEMENT";
	
	public WFTransactionConfigDesc(){
		super(WFTransactionConfig.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
		setGuiBrowsePanelClass(WFTransactionConfigGuiBrowsePanel.class);
		setGuiDetailsPanelClass(WFTransactionConfigGuiDetailsPanel.class);
		addReferenceField();

		FCompanyField compField = new FCompanyField(true, true);
		addField(compField);
		
		FMultipleChoiceStringField sFld = new FMultipleChoiceStringField("TRANSACTION", "Transaction", FLD_TRANSACTION, true, LEN_TRANSACTION_NAME);
		sFld.setAllwaysLocked(true);
		addField(sFld);
		
		FObjectField oFld = new FObjectField("MAP", "Map", FLD_MAP, WFMapDesc.getInstance());
		oFld.setSelectionList(WFMapDesc.getList(FocList.NONE));
		oFld.setDisplayField(WFMapDesc.FLD_NAME);
		oFld.setComboBoxCellEditor(WFMapDesc.FLD_NAME);
		addField(oFld);
		
		FObjectField fld = WFSiteDesc.newSiteField(FLD_DEFAULT_AREA, false);
		addField(fld);
		
		FMultipleChoiceField mFld = new FMultipleChoiceField("APPROVAL_METHOD", "Approval|Method", FLD_APPROVAL_METHOD, false, 1);
		mFld.addChoice(APPROVAL_METHOD_AT_CREATION, "At Creation");
		mFld.addChoice(APPROVAL_METHOD_BY_WORKFLOW, "By Workflow Signatures");
		addField(mFld);
		
		FBoolField bFld = new FBoolField("PROMPT_APPROVE_UPON_VALID", "Prompt|for Approve|Upon Save", FLD_PROMPT_FOR_APPROVE_UPON_VALIDATION, false);
		addField(bFld);

		bFld = new FBoolField("CODE_PREFIX_USE_SITE_PREFIX", "Use|Site|Prefix", FLD_CODE_PREFIX_USE_SITE_PREFIX, false);
		addField(bFld);
		
		FStringField cFld = new FStringField("CODE_PREFIX_TRANSACTION_PREFIX", "Transaction|Prefix", FLD_CODE_PREFIX_TRANSACTION_PREFIX, false, FocDesc.LEN_CODE_FOC);
		addField(cFld);

		cFld = new FStringField("CODE_PREFIX_PROPOSAL", "Proposal|Transaction|Prefix", FLD_CODE_PREFIX_TRANSACTION_PREFIX_PROPOSAL, false, FocDesc.LEN_CODE_FOC);
		addField(cFld);

		cFld = new FStringField("TRANS_TITLE_PREFIX", "Transaction|Title|Proposal", FLD_TRANSACTION_TITLE_PROPOSAL, false, 50);
		addField(cFld);

		cFld = new FStringField("TRANS_TITLE", "Transaction|Title", FLD_TRANSACTION_TITLE, false, 50);
		addField(cFld);
		
		bFld = new FBoolField("INCLUDE_PROJECT_CODE", "Include Project Code", FLD_INCLUDE_PROJECT_CODE, false);
		addField(bFld);

		bFld = new FBoolField("CODE_BY_SITE", "Code By Site", FLD_CODE_BY_STE, false);
		addField(bFld);
		
		bFld = new FBoolField("FORBID_ANCESTOR_ACCESS", "Allow ancestor access", FLD_FORBID_ANCESTOR_TO_ACCESS, false);
		addField(bFld);

		bFld = new FBoolField("LEAVE_CODE_EMPTY", "Leave Code Empty", FLD_LEAVE_CODE_EMPTY, false);
		addField(bFld);

    FIntField intFocFld = new FIntField("CODE_PREFIX_NUMBER_OF_DIGITS", "Code|Nbr|Digits|(<=6)", FLD_CODE_PREFIX_NUMBER_OF_DIGITS, false, 1);
    intFocFld.setPropertyValidator(new FNumLimitValidator(0, 6));
    addField(intFocFld);
  }
	
	@Override
	protected void afterConstruction() {
		super.afterConstruction();
		FMultipleChoiceStringField fld = (FMultipleChoiceStringField) getFieldByID(FLD_TRANSACTION);
		for(int i=0; i<WorkflowTransactionFactory.getInstance().getFocDescCount(); i++){
			IWorkflowDesc workflowDesc = WorkflowTransactionFactory.getInstance().getIWorkflowDesc(i);
			if(workflowDesc != null){
				fld.addChoice(workflowDesc.iWorkflow_getDBTitle());
			}
		}
	}
	
	@Override
	public void afterAdaptTableModel() {
		super.afterAdaptTableModel();

		completeListForAllCompanies();
	}
	
	public static void completeListForAllCompanies(){
		FocList focList = getList(FocList.NONE);
		focList.getFilter().setFilterByCompany(false);
		focList.loadIfNotLoadedFromDB();
		
		FocList companyList = CompanyDesc.getList(FocList.LOAD_IF_NEEDED);
		for(int i=0; i<companyList.size(); i++){
			Company comp = (Company) companyList.getFocObject(i);
			completeList(focList, comp);	
		}
		focList.removeAll();
		focList.getFilter().setFilterByCompany(true);
		focList.setLoaded(false);
	}

	public static void completeList(FocList focList, Company company){
		if(company != null){
      FocList wfConfigList = getList(FocList.NONE);
      wfConfigList.getFilter().setFilterByCompany(false);
      wfConfigList.loadIfNotLoadedFromDB();

			for(int i=0; i<WorkflowTransactionFactory.getInstance().getFocDescCount(); i++){
				IWorkflowDesc workflowDesc = WorkflowTransactionFactory.getInstance().getIWorkflowDesc(i);
				if(workflowDesc != null){
					String        transactionDBTitle = workflowDesc.iWorkflow_getDBTitle();
				  WFTransactionConfig assignement = getTransactionConfig_ForTransaction(company, transactionDBTitle);
					if(assignement == null){
						assignement = getTransactionConfig_ForTransaction(company, transactionDBTitle);
						assignement = (WFTransactionConfig) focList.newEmptyItem();
						assignement.setTransactionDBTitle(transactionDBTitle);
						assignement.setCompany(company);
						focList.add(assignement);
						assignement.codePrefix_setTransactionPrefix(workflowDesc.iWorkflow_getCodePrefix());
						assignement.codePrefix_setTransactionPrefixForProposal(workflowDesc.iWorkflow_getCodePrefix_ForProforma());
						assignement.codePrefix_setNbrOfDigits(workflowDesc.iWorkflow_getCode_NumberOfDigits());
						assignement.setApprovalMethod(workflowDesc.iWorkflow_getApprovalMethod());
						assignement.validate(true);
					}
					assignement.validate(true);
				}
			}
			focList.validate(true);
		}
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
	
	public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_TRANSACTION);
      list.setListOrder(order);
    }
    return list;
  }

  public static WFTransactionConfig getTransactionConfig_ForTransaction(Company currentCompany, String transactionName){
    WFTransactionConfig  w    = null;
    if(currentCompany != null){
    	FocDesc focDesc = getInstance();
    	if(focDesc != null){
	    	FocList list = focDesc.getFocList();
	    	if(list != null){
		    	list.loadIfNotLoadedFromDB();
		      
		//      Globals.logString("Searching for :"+currentCompany+" TITLE:"+transactionName);
		//      Globals.logString("-----------------------------------------------------------------------------");

		      for(int i=0; i<list.size(); i++){
		        WFTransactionConfig temp = (WFTransactionConfig) list.getFocObject(i);
		  
		//        Globals.logString("Company :"+temp.getCompany()+" TITLE:"+temp.getTransactionDBTitle());
		        
		        if(temp.getTransactionDBTitle().equals(transactionName) && currentCompany.equalsRef(temp.getCompany())){
		          w = temp;
		          break; 
		        }
		      }
	    	}
    	}
    }
    return w;
  }
  
  public static WFTransactionConfig getTransactionConfig_ForTransaction(String transactionName){
    WFTransactionConfig  w    = null;
    Company currentCompany = Globals.getApp() != null ? Globals.getApp().getCurrentCompany() : null;
    w = getTransactionConfig_ForTransaction(currentCompany, transactionName);
    return w;
  }

	public static WFTransactionConfig getTransactionConfig_ForTransaction(IWorkflowDesc workflowDesc){
		return getTransactionConfig_ForTransaction(workflowDesc.iWorkflow_getDBTitle());
	}
	
	public static WFTransactionConfig getTransactionConfig_ForTransaction(IWorkflow workflow){
		return workflow.iWorkflow_getWorkflow() != null ? getTransactionConfig_ForTransaction(workflow.iWorkflow_getWorkflow().getIWorkflowDesc()) : null;
	}

	public static WFMap getMap_ForTransaction(String transactionName){
		WFTransactionConfig  w    = getTransactionConfig_ForTransaction(transactionName);
		WFMap          map  = w != null ? w.getWorkflowMap() : null;
		return map;
	}
	
	public static WFSite getDefaultArea_ForTransaction(IWorkflow workflow){
		WFTransactionConfig wfAssi = getTransactionConfig_ForTransaction(workflow);
		return wfAssi.getDefaultArea();
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, WFTransactionConfigDesc.class);    
  }
	
	@Override
	public void beforeAdaptTableModel(boolean alreadyExists) {
  	FocVersion dbVersion = FocVersion.getDBVersionForModule(WorkflowModule.MODULE_NAME);
		if(alreadyExists && (dbVersion == null || dbVersion.getId() <= WorkflowModule.VERSION_ID_LAST_BEFORE_FLD_NAME_CUT)){
  		if(Globals.getApp() != null && Globals.getApp().getDataSource() != null && Globals.getApp().getDataSource().getUtility() != null){
	  		Globals.getApp().getDataSource().getUtility().dbUtil_RenameColumnsText(getStorageName(), "CODE_PREFIX_TRANS_PREFIX_PROPOSAL", "CODE_PREFIX_PROPOSAL", FocDesc.LEN_CODE_FOC);
	  		Globals.getApp().getDataSource().getUtility().dbUtil_RenameColumnsInteger(getStorageName(), "PROMPT_FOR_APPROVE_UPON_VALIDATION", "PROMPT_APPROVE_UPON_VALID");
  		}
		}
	}

}
