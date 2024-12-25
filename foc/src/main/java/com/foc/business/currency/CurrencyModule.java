/*
 * Created on Oct 14, 2004
 */
package com.foc.business.currency;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.foc.*;
import com.foc.business.calendar.FCalendar;
import com.foc.desc.*;
import com.foc.gui.FPanel;
import com.foc.list.FocList;
import com.foc.menu.*;

/**
 * @author 01Barmaja
 */
public class CurrencyModule extends FocModule {
	
  private static CurrencyModule currencyModule = null;
  private static CurrencyModule getInstance(){
  	if(currencyModule == null){
  		currencyModule = new CurrencyModule();
  	}
  	return currencyModule;
  }
  
  public static void includeCurrencyModule(){
  	CurrencyModule.getInstance().declare();
  }
  
  @SuppressWarnings("serial")
	public static FMenuList newConfigurationMenuList(){
    AbstractAction configMenuAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        FPanel panel = Currencies.getCurrencies().newDetailsPanel(0);
        Globals.getDisplayManager().newInternalFrame(panel);
      }
    };
    
    FMenuList mainMenu = new FMenuList(FocLangKeys.CURR_CURRENCY, "CURRENCY MAIN MENU");
    
    FMenuItem currencies = new FMenuItem(FocLangKeys.CURR_CURRENCIES, "CURRENCY",new FMenuAction(Currency.getFocDesc(), true));
    FMenuItem config = new FMenuItem(FocLangKeys.CURR_CONFIG, "CONFIG CURRENCY",configMenuAction);
    
    mainMenu.addMenu(currencies);
    mainMenu.addMenu(config);
    return mainMenu;
  }
  
  @SuppressWarnings("serial")
	public static FMenuList newRatesMenuList(){
    FMenuList mainMenu = new FMenuList(FocLangKeys.CURR_CURRENCY);
    
    AbstractAction configMenuAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        FPanel panel = Currencies.getCurrencies().newDetailsPanel(0);
        Globals.getDisplayManager().newInternalFrame(panel);
      }
    };
    FMenuItem currencies = new FMenuItem("Currency list", 'L', new FMenuAction(Currency.getFocDesc(), true));
    FMenuItem config = new FMenuItem("Configuration", 'C', configMenuAction);
    
    mainMenu.addMenu(currencies);
    mainMenu.addMenu(config);
    
    mainMenu.addMenu(new FMenuSeparator());
    AbstractAction ratesAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
      	DateLineGuiBrowsePanel panel = new DateLineGuiBrowsePanel(null);
        Globals.getDisplayManager().newInternalFrame(panel);
      }
    };
    
    FMenuItem currencyRates = new FMenuItem(FocLangKeys.CURR_RATES, ratesAction);
    
    mainMenu.addMenu(currencyRates);
    return mainMenu;
  }

	@Override
	public void declareFocObjectsOnce() {
    declareFocDescClass(Currency.class);
    declareFocDescClass(CurrencyRateDesc.class);
    declareFocDescClass(CurrencyDateDesc.class);
    declareFocDescClass(Currencies.class);
	}

	public void addApplicationMenu(FMenuList menuList) {
		
	}

	public void addConfigurationMenu(FMenuList menuList) {
		
	}

	public void afterApplicationEntry() {
		
	}

	public void afterApplicationLogin() {
		
	}

	public void beforeAdaptDataModel() {
		
	}

	public void afterAdaptDataModel() {
		
		//Get the list of all rates and copy the date from the depricated object date to the line itself
		FocList list = CurrencyRateDesc.getInstance().getFocList();
		list.loadIfNotLoadedFromDB();
		
		for(int i=0; i<list.size(); i++){
			CurrencyRate currRate = (CurrencyRate) list.getFocObject(i);
			
			if(currRate != null && FCalendar.isDateZero(currRate.getDate())){
				CurrencyDate dateObjet = currRate.getCurrencyDate_Depricated();
				currRate.setDate(dateObjet.getDate());
				currRate.validate(true);
			}
		}
		
		list.validate(true);
	}

	public void declare() {
    Application app = Globals.getApp();
    app.declareModule(this);
    app.setCurrencyModuleIncluded(true);
	}

	public void dispose() {
		
	}
}
