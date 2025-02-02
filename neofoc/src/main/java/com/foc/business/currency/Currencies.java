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
// DATE HASHTABLE
// STATIC

/*
 * Created on 01-Feb-2005
 */
package com.foc.business.currency;

import com.foc.FocLangKeys;
import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FObjectField;
import com.foc.event.FValidationListener;
import com.foc.gui.FGAbstractComboBox;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocLink;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public class Currencies extends FocObject {
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public Currencies(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public Currency getBaseCurrency(){
  	return (Currency) getPropertyObject(FLD_BASE_CURRENCY);
  }
  
  public void setBaseCurrency(Currency currency){
  	setPropertyObject(FLD_BASE_CURRENCY, currency);
  }
  
  public Currency getDefaultViewCurrency(){
  	return (Currency) getPropertyObject(FLD_DEFAULT_VIEW_CURRENCY);
  }
  
  public void setDefaultViewCurrency(Currency currency){
  	setPropertyObject(FLD_DEFAULT_VIEW_CURRENCY, currency);
  }
  
  public boolean isBaseCurrencyOk(){
    Currency baseCurrency = getBaseCurrency();
    return baseCurrency != null && baseCurrency.getName() != null && baseCurrency.getName().trim().compareTo("") != 0;
  }
  
  public boolean hasInitError(){
    return !isBaseCurrencyOk();
  }

  public boolean hasInitErrorWithMessage(String message){
    boolean b = hasInitError();
    if(b){
      Globals.showNotification("Multi currency module initialization", (message != null) ? message : "The multi currency module is not initialized.Please initalize it !", IFocEnvironment.TYPE_WARNING_MESSAGE);
    }
    return b;
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    FPanel panel =new FPanel();
    
    FGAbstractComboBox comboBox = (FGAbstractComboBox) getGuiComponent(FLD_BASE_CURRENCY);
    comboBox.setEnabled(isCreated());
    panel.add(MultiLanguage.getString(FocLangKeys.CURR_BASE_CURRENCY), comboBox, 0, 1);
    
    comboBox = (FGAbstractComboBox) getGuiComponent(FLD_DEFAULT_VIEW_CURRENCY);
    comboBox.setEnabled(isCreated());
    panel.add(MultiLanguage.getString(FocLangKeys.CURR_DEFAULT_VIEW_CURRENCY), comboBox, 0, 2);
    
    FValidationPanel validPanel = panel.showValidationPanel(true);
    if(validPanel != null){
      validPanel.addSubject(this);
      
      FValidationListener validListener = new FValidationListener(){
        public boolean proceedValidation(FValidationPanel panel) {
          return !hasInitErrorWithMessage(null);
        }

        public boolean proceedCancelation(FValidationPanel panel) {
          hasInitErrorWithMessage(null);
          return true;
        }

        public void postValidation(FValidationPanel panel) {
        }

        public void postCancelation(FValidationPanel panel) {
        }
      };
      validPanel.setValidationListener(validListener);
    }
    
    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocList getList(){
    FocLink link = new FocLinkSimple(getFocDesc());
    FocList list = new FocList(link);
    list.reloadFromDB();
    
    return list;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final int FLD_BASE_CURRENCY         = 1;
  public static final int FLD_DEFAULT_VIEW_CURRENCY = 2;
  
  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      focDesc = new FocDesc(Currencies.class, FocDesc.DB_RESIDENT, "CURR_CONFIG", false);

      focDesc.addReferenceField();
      
      FObjectField focObjFld = new FObjectField("BASE_CURR", "Base currency", FLD_BASE_CURRENCY, true, Currency.getFocDesc(), "CURR_");
      focObjFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
      focObjFld.setMandatory(true);
      focObjFld.setSelectionList(Currency.getList(true));
      focObjFld.setComboBoxCellEditor(Currency.FLD_NAME);
      focDesc.addField(focObjFld);
      //focFld.setLockValueAfterCreation(true);
      
      focObjFld = new FObjectField("DEFAULT_VIEW_CURRENCY", "Default View Currency", FLD_DEFAULT_VIEW_CURRENCY, true, Currency.getFocDesc(), "VIEW_CURR_");
      focObjFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
      focObjFld.setMandatory(true);
      focObjFld.setSelectionList(Currency.getList(true));
      focObjFld.setComboBoxCellEditor(Currency.FLD_NAME);
      focDesc.addField(focObjFld);
    }
    return focDesc;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // STATIC
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static Currencies currencies = null;
  private static final java.sql.Date nullDate = new java.sql.Date(0);
  
  public static Currencies getCurrencies(){
    Currencies ret = currencies;
    if(ret == null){
      FocList list = getList();
      if(list.size() > 0){
        currencies = (Currencies) list.getFocObject(0);
        ret = currencies;
      }
      if(ret == null){
        FocConstructor constr = new FocConstructor(Currencies.getFocDesc(), null, null);
        currencies = (Currencies) constr.newItem();
        ret = currencies;
        ret.setCreated(true);
      }
    }
    return ret;
  }

  public static Currency getCurrencyByName(String currName){
  	FocList list = Currency.getList(false);
  	return (Currency) list.searchByPropertyStringValue(Currency.FLD_NAME, currName);
  }

  public static double getRate(Currency curr1, Currency curr2){
    return getRate(Globals.getApp().getSystemDate(), curr1, curr2);
  }
  
  public static double getRate(java.sql.Date date, Currency curr){
    double rate = 0;
    if(curr != null && !getCurrencies().hasInitErrorWithMessage(null)){
      if(getCurrencies().getBaseCurrency().getName().compareTo(curr.getName()) != 0){
        rate = curr.getRate(date);        
      }else{
        rate = 1;
      }
    }
    return rate;
  }

  public static boolean isReverseForDisplay(Currency curr1, Currency curr2){
  	boolean reverse = false;
  	
  	if(curr1 != null && curr2 != null){
	  	if(Currencies.getCurrencies().getBaseCurrency().equalsRef(curr1) && !curr2.isReverse()){
	  		reverse = true;//Exemple USD / EUR
	  	}else if(Currencies.getCurrencies().getBaseCurrency().equalsRef(curr2) && curr1.isReverse()){
	  		reverse = true;//Exemple LBP / USD
	  	}else if(!Currencies.getCurrencies().getBaseCurrency().equalsRef(curr1) && !Currencies.getCurrencies().getBaseCurrency().equalsRef(curr2) && !curr2.isReverse() && curr1.isReverse()){
	  		reverse = true;//Exemple EUR / LBP
	  	}
  	}  	
  	return reverse;
  }
  
  public static double getRate(java.sql.Date date, Currency curr1, Currency curr2){
    double rate = 0;
    if(!getCurrencies().hasInitErrorWithMessage(null)){
    	if(curr1 != null && curr2 != null){
	      if(curr1.getName().compareTo(curr2.getName()) == 0){
	        rate = 1;
	      }else{
	        double r1 = getRate(date, curr1);
	        double r2 = getRate(date, curr2);
	        
	        if(r1 > 0 && r2 > 0){
	          rate = r1 / r2;
	        }
	      }
    	}
    }    
    return rate;
  }

  public static FObjectField newCurrencyField(String name, String title, int id, String prefix){
  	FObjectField objFld = new FObjectField(name, title, id, false, Currency.getFocDesc(), prefix);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setComboBoxCellEditor(Currency.FLD_NAME);
    objFld.setSelectionList(Currency.getList(true));
    return objFld;
  }
}

