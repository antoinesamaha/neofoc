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
import com.foc.desc.field.FField;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class AdrBookPartyGuiBrowsePanel extends FListPanel {
  
	public static final int VIEW_DEFAULT   = FocObject.DEFAULT_VIEW_ID;
	public static final int VIEW_SELECTION = 1;

  public static final String VIEW_KEY_ADR_BK_PARTY_LIST = "CRM_ADR_BOOK_PARTY_LIST";
  public static final String VIEW_CONTEXT_STANDARD      = "Standard";

  private boolean editable              = false;
  private boolean showValidationPanel   = false;
  private boolean showEditButton        = true;
  private boolean showAddRemoveButton   = false;
  private boolean showSelectionColumn   = false;
  
  private boolean showSelectionColumnPanel = false;
  
  private FocList list   = null;
  private int     viewID = VIEW_DEFAULT;
  
  public AdrBookPartyGuiBrowsePanel(FocList list){
    super("Address Book Parties", FPanel.FILL_BOTH);
    setWithScroll(false);
    if(list == null){
      list = AdrBookPartyDesc.getList(FocList.FORCE_RELOAD);
    }
    this.list = list;
    setFocList(list);
  }
  
  public AdrBookPartyGuiBrowsePanel(FocList list, int viewID){
    this(list);
    this.viewID = viewID;
    setShowValidationPanel(true);
    setShowAddRemoveButton(true);
    init();
  }
  
  public void dispose(){
    super.dispose();
    list = null;
  }
  
  public void init(){
    
    FTableView tableView = getTableView();
    FocDesc    desc      = AdrBookPartyDesc.getInstance();
    
    if(isShowSelectionColumn()){
      tableView.addSelectionColumn();
    }
    if(isShowSelectionColumnPanel()){
      FPanel selectAllUnselectAllPanel = getSelecAllUnselectAllPanel();
      add(selectAllUnselectAllPanel, 0, 5);
    }
    
    if(viewID == VIEW_SELECTION){
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_CODE, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_NAME, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_EXTERNAL_CODE, false);
	    setMainPanelSising(MAIN_PANEL_FILL_VERTICAL);
    }else{
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_CODE, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_NAME, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_EXTERNAL_CODE, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_INDUSTRY, false);
	    tableView.addColumn(desc, FField.FLD_COMPANY, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_LANGUAGE, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_PHONE_1, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_PHONE_2, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_MOBILE, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_FAX, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_EMAIL, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_COUNTRY, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_REGION, false);
	    tableView.addColumn(desc, AdrBookPartyDesc.FLD_CITY, false);
    }
    
		tableView.setViewKey(VIEW_KEY_ADR_BK_PARTY_LIST);
		tableView.setViewContext(VIEW_CONTEXT_STANDARD);

    construct();
    
    tableView.setColumnResizingMode(FTableView.COLUMN_AUTO_RESIZE_MODE);
    
    if(isShowValidationPanel()){
      FValidationPanel selectionPanel = showValidationPanel(true);
      //selectionPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
      selectionPanel.addSubject(list);
    }
    
    requestFocusOnCurrentItem();
    
    addFilterExpressionPanel();

    showEditButton(isShowEditButton());
    showAddButton(isShowAddRemoveButton());
    showRemoveButton(isShowAddRemoveButton());
    showColomnSelectorButton(true);
  }

  public boolean isEditable() {
    return editable;
  }

  public void setEditable(boolean editable) {
    this.editable = editable;
  }

  public boolean isShowValidationPanel() {
    return showValidationPanel;
  }

  public void setShowValidationPanel(boolean showValidationPanel) {
    this.showValidationPanel = showValidationPanel;
  }

  public boolean isShowEditButton() {
    return showEditButton;
  }

  public void setShowEditButton(boolean showEditButton) {
    this.showEditButton = showEditButton;
  }

  public boolean isShowAddRemoveButton() {
    return showAddRemoveButton;
  }

  public void setShowAddRemoveButton(boolean showAddRemoveButton) {
    this.showAddRemoveButton = showAddRemoveButton;
  }

  public boolean isShowSelectionColumn() {
    return showSelectionColumn;
  }

  public void setShowSelectionColumn(boolean showSelectionColumn) {
    this.showSelectionColumn = showSelectionColumn;
  }

  public boolean isShowSelectionColumnPanel() {
    return showSelectionColumnPanel;
  }

  public void setShowSelectionColumnPanel(boolean showSelectionColumnPanel) {
    this.showSelectionColumnPanel = showSelectionColumnPanel;
  }
}
