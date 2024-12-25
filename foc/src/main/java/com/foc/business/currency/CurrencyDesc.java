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

import java.util.Locale;

import com.foc.business.country.Country;
import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.list.FocLink;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

/**
 * @author 01Barmaja
 */
public class CurrencyDesc extends FocDesc implements AutoPopulatable, CurrencyConstants {
	
  public static String DB_TABLE_NAME = "CURR";
  
  public CurrencyDesc() {
    super(Currency.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);

    FField focFld = addReferenceField();

    focFld = new FStringField("NAME", "Name", FLD_NAME, true, 3);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);

    focFld = new FStringField("DESCRIP", "Description", FLD_DESCRIPTION, true, 30);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);

    FMultipleChoiceField multFld = new FMultipleChoiceField("RATE_MODE", "Rate mode", FLD_RATE_RATIO, false, 2);
    multFld.setLockValueAfterCreation(true);
    multFld.addChoice(RATE_RATIO_BASIC_THIS, RATE_RATIO_LABEL_BASIC_THIS);
    multFld.addChoice(RATE_RATIO_THIS_BASIC, RATE_RATIO_LABEL_THIS_BASIC);
    addField(multFld);
  }

  public static FObjectField newObjectField(String name, String title, int fldID){
	  FObjectField objFld = new FObjectField(name, title, fldID, getInstance());
	  objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
	  objFld.setComboBoxCellEditor(CurrencyDesc.FLD_NAME);
	  objFld.setSelectionList(Currency.getList(true));
	  return objFld;
  }
  
  public FocList newFocList(){
    FocLink link = new FocLinkSimple(CurrencyDesc.getInstance());
    FocList focList = new FocList(link);

    FocListOrder listOrder = new FocListOrder();
    listOrder.addField(FFieldPath.newFieldPath(FLD_NAME));
    focList.setListOrder(listOrder);

  	return focList; 
  }
  
  public static CurrencyDesc getInstance(){
  	return (CurrencyDesc) getInstance(DB_TABLE_NAME, CurrencyDesc.class);
  }

	public static Currency makeSureCurrencyExist(String curName, String descrip, boolean reverse){
		Currency cur = Currencies.getCurrencyByName(curName);
		if(cur == null){
			FocList currencyList = Currency.getList(false);
			cur = (Currency) currencyList.newEmptyItem();
			cur.setName(curName);
			cur.setDescrip(descrip);
			cur.setReverse(reverse);
			cur.validate(true);
			currencyList.validate(true);
		}
		return cur;
	}
  
	@Override
	public boolean populate() {
		makeSureCurrencyExist("USD", "U.S. Dollars", false);
    makeSureCurrencyExist("EUR", "Euro", false);
		return false;
	}

	@Override
	public String getAutoPopulatableTitle() {
		return "Currencies (EUR,USD)";
	}
}
