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
import java.util.ArrayList;

import com.foc.*;
import com.foc.admin.FocGroup;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.event.*;
import com.foc.gui.*;
import com.foc.gui.table.*;
import com.foc.list.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class DateLine extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public DateLine(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  public Object getDate_Object(){
  	FDate date = (FDate) getFocProperty(DateLineDesc.FLD_DATE);
  	return date.getObject();
  }

  public Date getDate(){
  	return getPropertyDate(DateLineDesc.FLD_DATE);
  }
  
  public void setDate(Date date){
  	setPropertyDate(DateLineDesc.FLD_DATE, date);
  }
  
  private int getRateFieldIdx_ForCurrency(Currency currency){
  	int fldId = 0;
  	if(currency != null){
	  	DateLineDesc dateLineDesc = (DateLineDesc) getThisFocDesc();
	  	if(dateLineDesc != null){
		  	int idx = dateLineDesc.getCurrencyIndex(currency);
		  	if(idx >=0 ){
		  		fldId = dateLineDesc.getCurrencyFieldId_At(idx);
		  	}
	  	}
  	}
  	return fldId;
  }
  
  public double getRate(Currency currency){
  	int fldId = getRateFieldIdx_ForCurrency(currency);
  	return fldId > 0 ? getPropertyDouble(fldId) : 0;
  }
  
  public void setRate(Currency currency, double rate){
  	int fldId = getRateFieldIdx_ForCurrency(currency);
  	if(fldId > 0){
  		setPropertyDouble(fldId, rate);
  	}
  }

  public double getRateAt(int idx){
  	double rate = 0;
  	if(idx >= 0 && ((DateLineDesc) getThisFocDesc()).getCurrencyCount() > idx){
  		rate = getPropertyDouble(DateLineDesc.FLD_CURR_RATE+idx);
  	}
  	return rate;
  }

  public Currency getCurrencyAt(int idx){
  	return ((DateLineDesc) getThisFocDesc()).getCurrencyAt(idx);
  }

  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    return null;
  }
}
