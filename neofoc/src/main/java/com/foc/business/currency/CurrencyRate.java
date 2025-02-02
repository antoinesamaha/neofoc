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

import java.sql.Date;

import com.foc.*;
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
public class CurrencyRate extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public CurrencyRate(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public void setCurrency(Currency currency){
  	setPropertyObject(CurrencyRateDesc.FLD_CURRENCY, currency);
  }
  
  public Currency getCurrency(){
  	return (Currency) getPropertyObject(CurrencyRateDesc.FLD_CURRENCY);
  }

  public double getRate(){
    return getPropertyDouble(CurrencyRateDesc.FLD_RATE);
  }
  
  public void setRate(double rate){
    setPropertyDouble(CurrencyRateDesc.FLD_RATE, rate);
  }
  
  public FDouble getRateProperty(){
    return (FDouble) getFocProperty(CurrencyRateDesc.FLD_RATE);
  }
  
  @Deprecated
  public CurrencyDate getCurrencyDate_Depricated(){
  	return (CurrencyDate) getPropertyObject(CurrencyRateDesc.FLD_DATE_OBJECT);
  }
  
  public Date getDate(){
  	return getPropertyDate(CurrencyRateDesc.FLD_DATE);
  }

  public void setDate(Date date){
  	setPropertyDate(CurrencyRateDesc.FLD_DATE, date);
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static int COL_CURRENCY = 1;
  private static int COL_RATE = 2;
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    FocDesc desc = CurrencyRateDesc.getInstance();
    FListPanel selectionPanel = null;
    if (desc != null && list != null) {
      list.setDirectImpactOnDatabase(false);

      selectionPanel = new FListPanel(list);
      FTableView tableView = selectionPanel.getTableView();
      
      FTableColumn col = null;

      col = new FTableColumn(desc, FFieldPath.newFieldPath(CurrencyRateDesc.FLD_CURRENCY), COL_CURRENCY, MultiLanguage.getString(FocLangKeys.CURR_CURRENCY), true);
      tableView.addColumn(col);

      col = new FTableColumn(desc, FFieldPath.newFieldPath(CurrencyRateDesc.FLD_RATE), COL_RATE, MultiLanguage.getString(FocLangKeys.CURR_RATE), true);
      tableView.addColumn(col);
      
      selectionPanel.construct();

      selectionPanel.setDirectlyEditable(true);

      selectionPanel.requestFocusOnCurrentItem();
      selectionPanel.showEditButton(false);
    }
    
    return selectionPanel;
  }
}
