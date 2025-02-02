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
package com.foc.business.currency.focUnit;

import com.foc.Globals;
import com.foc.business.currency.Currencies;
import com.foc.business.currency.Currency;
import com.foc.fUnit.FocTestCase;
import com.foc.fUnit.FocTestSuite;

public class CurrencyFocUnit extends FocTestCase {

  public CurrencyFocUnit(FocTestSuite testSuite, String functionName){
    super(testSuite, functionName);
  }

  protected void menuBarOpenCurrencyList(){
    menu_ClickByPath(new String[]{"Currency", "Currency list"});
  }

  protected void menuBarOpenCurrencyGeneralConfiguration(){
    menu_ClickByPath(new String[]{"Currency", "Configuration"});
  }

  protected void menuBarCurrencyRatesOpen(){
    menu_ClickByPath(new String[]{"Currency", "Rates"});
  }
  
  protected void insertCurrencyDefinition(String currency, boolean inversed){
    table_ClickInsertButton();
    sleep(1);
    int row = table_GetRowCount() - 1;
    table_SetValue(row, Currency.FLD_NAME, currency);
    table_SetValue(row, Currency.FLD_RATE_RATIO, inversed ? Currency.RATE_RATIO_LABEL_BASIC_THIS : Currency.RATE_RATIO_LABEL_THIS_BASIC);
  }
  
  protected void insertCurrencyArray(Object currenciesToDefine[]){
    try {
      logBeginStep("Insert All Currencies ");
      {
      	menuBarOpenCurrencyList();
        setDefaultFocDesc(Currency.getFocDesc());
        
        for(int i=0; i<currenciesToDefine.length; i=i+2){
        	insertCurrencyDefinition((String) currenciesToDefine[i], Boolean.valueOf((Boolean)currenciesToDefine[i+1]));        	
        }

        button_ClickValidate();
        /*
        menuBarOpenCurrencyList();
        sleep(2);
        setDefaultFocDesc(Currency.getFocDesc());        

        for(int i=0; i<currenciesToDefine.length; i=i+2){
        	table_FindAndSelectRow(Currency.FLD_NAME, (String) currenciesToDefine[i]);
        }
        button_ClickValidate();
        */        
      }
      logEndStep();
      sleep(1);
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
  protected void currencyGeneralConfig(String date, String viewCurrency, String baseCurrency){
    try {
      logBeginStep("Currency Config");
      {
      	menuBarOpenCurrencyGeneralConfiguration();
        setDefaultFocDesc(Currencies.getFocDesc());
        
        guiComponent_SetValue(Currencies.FLD_DEFAULT_VIEW_CURRENCY, viewCurrency);
        guiComponent_SetValue(Currencies.FLD_BASE_CURRENCY, baseCurrency);
        
        button_ClickValidate();
      }
      logEndStep();
      sleep(1);
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
      
  protected void rates_Validate(){
  	button_ClickValidate("");
  }
  
  protected void rates_setCurrencyRate(String date, String curr1, String curr2, double rate){
  	int row = table_FindAndSelectRow(date);
  	table_SetValue(row, curr1+"/"+curr2, String.valueOf(rate));
  }
}
