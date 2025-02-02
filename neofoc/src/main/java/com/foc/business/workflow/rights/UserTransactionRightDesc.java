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
package com.foc.business.workflow.rights;

import com.foc.admin.FocUser;
import com.foc.admin.FocUserDesc;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.business.workflow.WFTitleDesc;
import com.foc.business.workflow.WorkflowTransactionFactory;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class UserTransactionRightDesc extends FocDesc {
	public static final int FLD_TRANSACTION   =  1;
	public static final int FLD_TITLE         =  2;
	public static final int FLD_USER          =  3;
	public static final int FLD_SITE          =  4;
	public static final int FLD_RIGHTS_LEVEL  =  5;
	
	public static final String DB_TABLE_NAME = "WF_USER_TRANS_RIGHT";

	public UserTransactionRightDesc(){
		super(UserTransactionRight.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		setGuiBrowsePanelClass(UserTransactionRightGuiBrowsePanel.class);
		setGuiDetailsPanelClass(UserTransactionRightGuiDetailsPanel.class);
		addReferenceField();

		FMultipleChoiceStringField sFld = new FMultipleChoiceStringField("TRANSACTION", "Transaction", FLD_TRANSACTION, true, 40);
		addField(sFld);
		
		/*
		FPropertyListener exclusivityListener = new FPropertyListener() {
			@Override
			public void propertyModified(FProperty property) {
				if(property.isManualyEdited()){
					UserTransactionRight superUser = (UserTransactionRight) property.getFocObject();
					FField      fld       = property.getFocField();
					
					if(fld != null){
						if(fld.getID() == FLD_TITLE){
							if(superUser.getTitle() != null) superUser.setUser(null);
						}else if(fld.getID() == FLD_USER){
							if(superUser.getUser() != null) superUser.setTitle(null);
						}
					}
				}
			}
			
			@Override
			public void dispose() {
			}
		};
		*/

		FObjectField objFld = new FObjectField("TITLE", "Title", FLD_TITLE, false, WFTitleDesc.getInstance(), "TITLE_");
		objFld.setSelectionList(WFTitleDesc.getList(FocList.NONE));
		objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
		objFld.setComboBoxCellEditor(WFTitleDesc.FLD_NAME);
		objFld.setDisplayField(WFTitleDesc.FLD_NAME);
		//objFld.addListener(exclusivityListener);
		addField(objFld);
		
		FObjectField fObjectFld = new FObjectField("USER", "User", FLD_USER, false, FocUser.getFocDesc(), "USER_");
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    //fObjectFld.setMandatory(true);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    //fObjectFld.addListener(exclusivityListener);
    addField(fObjectFld);

		fObjectFld = new FObjectField("RIGHT_LEVEL", "Right Level", FLD_RIGHTS_LEVEL, RightLevelDesc.getInstance());
    fObjectFld.setComboBoxCellEditor(FField.FLD_NAME);
    fObjectFld.setDisplayField(FField.FLD_NAME);
    //fObjectFld.setMandatory(true);
    fObjectFld.setSelectionList(RightLevelDesc.getList(FocList.NONE));
    //fObjectFld.addListener(exclusivityListener);
    addField(fObjectFld);

		objFld = new FObjectField("WF_SITE", "Site", FLD_SITE, WFSiteDesc.getInstance(), this, WFSiteDesc.FLD_USER_TRANSACTION_RIGHTS_LIST);
	  objFld.setComboBoxCellEditor(WFSiteDesc.FLD_NAME);
//	  objFld.setSelectionList(WFSiteDesc.getList(FocList.NONE));
	  objFld.setWithList(false);
		addField(objFld);
  }
	
	@Override
	protected void afterConstruction() {
		super.afterConstruction();
		FMultipleChoiceStringField fld = (FMultipleChoiceStringField) getFieldByID(FLD_TRANSACTION);
		fld.addChoice("");
		for(int i=0; i<WorkflowTransactionFactory.getInstance().getFocDescCount(); i++){
			IWorkflowDesc workflowDesc = WorkflowTransactionFactory.getInstance().getIWorkflowDesc(i);
			if(workflowDesc != null){
				fld.addChoice(workflowDesc.iWorkflow_getDBTitle());
			}
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
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static UserTransactionRightDesc getInstance() {
    return (UserTransactionRightDesc) getInstance(DB_TABLE_NAME, UserTransactionRightDesc.class);    
  }
}
