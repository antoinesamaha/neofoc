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
// DESCRIPTION
// LIST

/*
 * Created on 01-Feb-2005
 */
package com.foc.business.currency;

import java.awt.Component;

import com.foc.FocLangKeys;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.gui.*;
import com.foc.gui.table.*;
import com.foc.list.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class CurrencyDate extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public CurrencyDate(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public java.sql.Date getDate(){
    FDate pDate = (FDate) getFocProperty(CurrencyDateDesc.FLD_DATE);
    return pDate != null ? pDate.getDate() : null;
  }
  
  public FocList getRateList(){
    FList list = (FList) getFocProperty(CurrencyDateDesc.FLD_RATE_LIST);
    return list != null ? list.getList() : null;
  }

  private CurrencyRate getCurrencyRate(Currency curr){
    FocList rl = getRateList();
    return (CurrencyRate) rl.searchByPropertyObjectValue(CurrencyRateDesc.FLD_CURRENCY, curr);
  }

  public double getCurrencyRateValue(Currency curr){
    CurrencyRate currRate = getCurrencyRate(curr);
    double d = 0;
    if(currRate != null && currRate.getRateProperty() != null){
    	d = currRate.getRateProperty().getDouble();
    	if(curr.isReverse()){
    		d = 1/d;
    	}
    }
    return d;
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel(); 
    
    Component comp = getGuiComponent(CurrencyDateDesc.FLD_DATE);
    comp.setEnabled(false);
    panel.add(MultiLanguage.getString(FocLangKeys.CURR_DATE), comp, 0, 0);

    FocList list = getRateList();
    FPanel ratesPanel = CurrencyRate.newBrowsePanel(list, 0);
    
    panel.add(ratesPanel, 0, 1, 2, 1);
    
    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static final int COL_DATE =  1; 
  
  public static FocList getList(int mode){
  	return CurrencyDateDesc.getInstance().getFocList(mode);
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FocDesc desc = CurrencyDateDesc.getInstance();
    FPanel panel = new FPanel();
    FListPanel selectionPanel = null;
    if (desc != null) {
      if(list == null){
        list = getList(FocList.LOAD_IF_NEEDED);
      }
      list.setDirectImpactOnDatabase(false);

      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();
      
      FTableColumn col = null;

      col = new FTableColumn(desc, FFieldPath.newFieldPath(CurrencyDateDesc.FLD_DATE), COL_DATE, MultiLanguage.getString(FocLangKeys.CURR_DATE), true);
      tableView.addColumn(col);

      selectionPanel.construct();

      selectionPanel.setDirectlyEditable(true);
      
      FGCurrentItemPanel dateValuesContainer = new FGCurrentItemPanel(selectionPanel, 0);
      panel.add(selectionPanel, 0, 0);
      panel.add(dateValuesContainer, 1, 0);

      FValidationPanel savePanel = panel.showValidationPanel(true);
      if (savePanel != null) {
        list.setFatherSubject(null);
        savePanel.addSubject(list);
      }

      selectionPanel.requestFocusOnCurrentItem();
      selectionPanel.showEditButton(false);
      selectionPanel.setFrameTitle(MultiLanguage.getString(FocLangKeys.CURR_RATES));
    }
    
    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

//  private static FocDesc focDesc = null;
//
//  public static final int FLD_DATE = 1;
//  public static final int FLD_RATE_LIST = 2;
//  
//  private static FocLink ratesLink = null;
//  
//  private static FocLink getRatesLink() {
//    if (ratesLink == null) {
//      ratesLink = new FocLinkOne2N(getFocDesc(), CurrencyRate.getFocDesc());
//    }
//    return ratesLink;
//  }  
//  
//  public static FocDesc getFocDesc() {
//    if (focDesc == null) {
//      FField focFld = null;
//      focDesc = new FocDesc(CurrencyDate.class, FocDesc.DB_RESIDENT, "CURR_DATES", false);
//
//      focFld = focDesc.addReferenceField();
//
//      focFld = new FDateField("DATE", "Date", FLD_DATE, true);
//      focDesc.addField(focFld);
//      
//      focFld = new FListField("RATES", "Rates", FLD_RATE_LIST, getRatesLink());
//      focDesc.addField(focFld);
//    }
//    return focDesc;
//  }
}
