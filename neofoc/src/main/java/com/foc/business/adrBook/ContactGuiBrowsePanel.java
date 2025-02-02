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
package com.foc.business.adrBook;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class ContactGuiBrowsePanel extends FListPanel{
  
	public static final int VIEW_DEFAULT            = FocObject.DEFAULT_VIEW_ID;
	public static final int VIEW_MULTIPLE_SELECTION = 1;
	public static final int VIEW_SELECTION          = 2;
	
  private FocDesc desc   = null;
  private int     viewID = VIEW_DEFAULT;
  //private FocList list = null;
  
  public static final String VIEW_KEY_CONTACT_LIST = "CRM_CONTACT_LIST";
  public static final String VIEW_CONTEXT_STANDARD = "Standard";

  private static final int SHIFT_PARTY = 1000;
  
  public ContactGuiBrowsePanel(FocList list, int viewID){
    super("Contact", FPanel.FILL_BOTH);
    setWithScroll(false);
    this.viewID = viewID;
    desc = ContactDesc.getInstance();
    //this.list = list;
    if(desc != null){
      if(list == null){
        list = ContactDesc.getList(FocList.FORCE_RELOAD);
      }
      setFocList(list);
    }
    init();
  }
  
  public void dispose(){
    super.dispose();
    desc = null;
    //list = null;
  }
  
  public void init(){
    FTableView tableView = getTableView();
    
    tableView.addLineNumberColumn();
    if(viewID == VIEW_MULTIPLE_SELECTION){
    	tableView.addSelectionColumn();
    }
    FTableColumn tCol = tableView.addColumn(desc, FFieldPath.newFieldPath(ContactDesc.FLD_ADR_BOOK_PARTY, AdrBookPartyDesc.FLD_CODE), SHIFT_PARTY + AdrBookPartyDesc.FLD_CODE, false);
    tCol.setTitle("Company Code");
    tCol = tableView.addColumn(desc, ContactDesc.FLD_COMPANY_NAME, false);
    tCol.setTitle("Company Name");
    tCol = tableView.addColumn(desc, FFieldPath.newFieldPath(ContactDesc.FLD_ADR_BOOK_PARTY, AdrBookPartyDesc.FLD_INDUSTRY), SHIFT_PARTY + AdrBookPartyDesc.FLD_INDUSTRY, false);
    tCol.setTitle("Industry");
    tableView.addColumn(desc, ContactDesc.FLD_TITLE, false);
    tableView.addColumn(desc, ContactDesc.FLD_FIRST_NAME, false);
    tableView.addColumn(desc, ContactDesc.FLD_FAMILY_NAME, false);
    tableView.addColumn(desc, ContactDesc.FLD_POSITION_STR, false);
    //tableView.addColumn(desc, ContactDesc.FLD_POSITION, false);
    tableView.addColumn(desc, ContactDesc.FLD_INTRODUCTION, false);
    tableView.addColumn(desc, ContactDesc.FLD_PHONE_1, false);
    tableView.addColumn(desc, ContactDesc.FLD_PHONE_2, false);
    tableView.addColumn(desc, ContactDesc.FLD_MOBILE, false);
    tableView.addColumn(desc, ContactDesc.FLD_EMAIL, false);
    tableView.addColumn(desc, ContactDesc.FLD_EMAIL_2, false);
    
		tableView.setViewKey(VIEW_KEY_CONTACT_LIST);
		tableView.setViewContext(VIEW_CONTEXT_STANDARD);

    construct();
    
    addFilterExpressionPanel();
    tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
    
    requestFocusOnCurrentItem();
    showEditButton(true);
    showAddButton(true);
    showRemoveButton(true);
    showColomnSelectorButton(true);
    
    FValidationPanel validPanel = showValidationPanel(true);
    if(viewID == VIEW_MULTIPLE_SELECTION || viewID == VIEW_SELECTION){
    	validPanel.addSubject(getFocList());
    	validPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
    }else{
    	validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    }
  }
}
