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

import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

/**
 * @author 01Barmaja
 */
public class CurrencyRateDesc extends FocDesc {
  
  public static final int LEN_RATE = 15;
  public static final int DEC_RATE =  6;
  
  public static final int FLD_CURRENCY    = 1;
  public static final int FLD_RATE        = 2;
  public static final int FLD_DATE        = 3;
  public static final int FLD_DATE_OBJECT = 4;
  
  public static final String DB_TABLE_NAME = "CURR_RATES";
  
  public CurrencyRateDesc() {
    super(CurrencyRate.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);

    FField focFld = addReferenceField();

    FObjectField focObjFld = new FObjectField("CURR", "Currency", FLD_CURRENCY, true, Currency.getFocDesc(), "CUR_");
    focObjFld.setSelectionList(Currency.getList(true));
    addField(focObjFld);

    focFld = new FNumField("RATE", "Rate", FLD_RATE, false, LEN_RATE, DEC_RATE);
    addField(focFld);
    
    focFld = new FDateField("DATE", "Date", FLD_DATE, true);
    addField(focFld);
    
    FObjectField dateFld = new FObjectField("DATE_OBJECT", "Date Object", FLD_DATE_OBJECT, false, CurrencyDateDesc.getInstance(), "M_", this, CurrencyDateDesc.FLD_RATE_LIST);
    addField(dateFld);
  }
  
  @Override
  public FocList newFocList(){
  	FocList list = new FocList(new FocLinkSimple(getInstance()));
  	list.setDirectImpactOnDatabase(true);
  	list.setDirectlyEditable(false);
  	
  	list.setListOrder(new FocListOrder(FLD_DATE));
  	return list;
  }
  
  public static CurrencyRateDesc getInstance(){
  	return (CurrencyRateDesc) getInstance(DB_TABLE_NAME, CurrencyRateDesc.class);
  }
}
