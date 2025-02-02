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
// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION
// RATES ARRAY

/*
 * Created on 01-Feb-2005
 */
package com.foc.business.currency;

import com.foc.*;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.event.FValidationListener;
import com.foc.gui.*;
import com.foc.gui.table.*;
import com.foc.list.*;

/**
 * @author 01Barmaja
 */
public class Currency extends FocObject implements CurrencyConstants {
	
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public Currency(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public void dispose(){
  	super.dispose();
//  	dispose_ratesArray();
  }
  
  public String getName(){
  	return getPropertyString(FLD_NAME);
  }

  public void setName(String name){
  	setPropertyString(FLD_NAME, name);
  }

  public String getDescrip(){
    return getPropertyString(FLD_DESCRIPTION);
  }

  public void setDescrip(String descrip){
    setPropertyString(FLD_DESCRIPTION, descrip);
  }

  public boolean isReverse(){
  	return getPropertyMultiChoice(FLD_RATE_RATIO) == RATE_RATIO_BASIC_THIS; 
  }

  public void setReverse(boolean reverse){
  	setRateRatio(reverse ? RATE_RATIO_BASIC_THIS : RATE_RATIO_THIS_BASIC);
  }
  	
  public void setRateRatio(int ratio){
  	setPropertyMultiChoice(FLD_RATE_RATIO, ratio); 
  }

  public static void printDebug2(FocList list){
    Globals.logString("");
    Globals.logString("Currency list");
    for(int i=0; i<list.size(); i++){
      Currency curr = (Currency) list.getFocObject(i);
      Globals.logString(curr.getReference().getInteger()+" "+curr.getName()+" "+curr.getReference());
    }
    Globals.logString("");
  }
  
  public static void printDebug(){
    FocList list = getList(false);
    printDebug2(list);
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel();
    FGTextField comp = (FGTextField) getGuiComponent(FLD_NAME);
    comp.setEnabled(false);
    comp.setColumns(4);
    panel.add(comp, 0, 0);
    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocList focList = null;

  public static final int VIEW_CURRENCY_N2N_SELECTION_PANEL = 2;
	public final static int VIEW_FOR_SELECTION                = 3;
	
  public static FocList createList(){
    FocLink link = new FocLinkSimple(CurrencyDesc.getInstance());
    FocList focList = new FocList(link);

    FocListOrder listOrder = new FocListOrder();
    listOrder.addField(FFieldPath.newFieldPath(FLD_NAME));
    focList.setListOrder(listOrder);
    
    return focList;
  }
  
  public static FocList getList(boolean doNotTryLoading){
  	return CurrencyDesc.getInstance().getFocList(doNotTryLoading ? FocList.NONE : FocList.LOAD_IF_NEEDED);
//    if(focList == null){
//      focList = createList();
//    }
//    
//    if(!doNotTryLoading){
//      if(focList.loadIfNotLoadedFromDB()){
//        DateLine.resetDescription();
//      }
//    }
//    
//    return focList;
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FocDesc desc = getFocDesc();
    FListPanel selectionPanel = null;
    if(desc != null){
      if(list == null){
        list = CurrencyDesc.getInstance().getFocList(FocList.LOAD_IF_NEEDED);
      }
      if(list != null){
        list.setDirectImpactOnDatabase(false);

        selectionPanel = new FListPanel(list);
        FTableView tableView = selectionPanel.getTableView();
        
        if(viewID == VIEW_CURRENCY_N2N_SELECTION_PANEL){
          tableView.addSelectionColumn();
        }
        
        tableView.addColumn(desc, FLD_NAME, 10, true);
        tableView.addColumn(desc, FLD_DESCRIPTION, 30, true);
        tableView.addColumn(desc, FLD_RATE_RATIO, 14, true);

        selectionPanel.construct();

        if(viewID == VIEW_CURRENCY_N2N_SELECTION_PANEL){
          selectionPanel.setDirectlyEditable(false);
          selectionPanel.showEditButton(false);
          selectionPanel.showModificationButtons(false);
        }else{
          selectionPanel.setDirectlyEditable(true);

          /*
          Currencies currencies = Currencies.getCurrencies();
          FPanel datesPanel = currencies.newDetailsPanel(0);
          mainPanel.add(datesPanel, 0, 0);
          */
          
          FValidationPanel savePanel = selectionPanel.showValidationPanel(true);
          if (savePanel != null) {
            //list.setFatherSubject(null);
            savePanel.addSubject(list);
            //savePanel.addSubject(currencies);
            savePanel.setValidationListener(new FValidationListener(){
              public boolean proceedValidation(FValidationPanel panel) {
                return true;
              }
              public boolean proceedCancelation(FValidationPanel panel) {
                return true;
              }
              public void postValidation(FValidationPanel panel) {
                /*
                FocList list = getList();
                list.reloadFromDB();
                DateLine.resetDescription();
                */
                DateLineList.getInstance(false).setNeedReset(true);
              }
              public void postCancelation(FValidationPanel panel) {
                /*
                FocList list = getList();
                list.reloadFromDB();
                DateLine.resetDescription();
                */
              	DateLineList.getInstance(false).setNeedReset(true);
              }
            });
          }
          
          selectionPanel.requestFocusOnCurrentItem();
          selectionPanel.showModificationButtons(viewID != VIEW_FOR_SELECTION);
          selectionPanel.showEditButton(false);
        }
      }
    }
    selectionPanel.setFrameTitle(MultiLanguage.getString(FocLangKeys.CURR_CURRENCY));
    selectionPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_VERTICAL);
    return selectionPanel;
  }

  /**
   * Returns the rate of this currency against the Base currency as selected in the configuration of the Currency modules
   */
  public double getRate(java.sql.Date date){
  	DateLineList dateLineList = DateLineList.getInstance(true);
  	double rate = dateLineList != null ? dateLineList.getRate(date, this) : 0;
  	if(isReverse() && rate > 0) rate = 1/rate;
  	return rate;
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getFocDesc() {
  	return CurrencyDesc.getInstance();
  }
}
