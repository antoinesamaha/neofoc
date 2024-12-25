/*
 * Created on 09-Jun-2005
 */
package com.foc;

import com.foc.business.multilanguage.LanguageKey;

/**
 * @author 01Barmaja
 */
public class FocLangKeys {

  public static final String MENU_BUNDEL = "focMenu";
  
  public static final LanguageKey MENU_ADMIN_MENU = new LanguageKey(MENU_BUNDEL, "adminMenu");
  public static final LanguageKey MENU_USER = new LanguageKey(MENU_BUNDEL, "user");  
  public static final LanguageKey MENU_GROUP = new LanguageKey(MENU_BUNDEL, "group");  
  public static final LanguageKey MENU_ADAPT_DATA_MODEL = new LanguageKey(MENU_BUNDEL, "adaptDataModel");
  public static final LanguageKey MENU_CHECK_LOCKS = new LanguageKey(MENU_BUNDEL, "checkLocks");
  public static final LanguageKey MENU_CASHDESKS = new LanguageKey(MENU_BUNDEL, "cashdesk");

  public static final LanguageKey MENU_TOOLS = new LanguageKey(MENU_BUNDEL, "tools");
  public static final LanguageKey MENU_HELP = new LanguageKey(MENU_BUNDEL, "help");  
  public static final LanguageKey MENU_EXIT = new LanguageKey(MENU_BUNDEL, "exit");  
  public static final LanguageKey MENU_USER_GROUP_INFO = new LanguageKey(MENU_BUNDEL, "userGroupInfo");  
  public static final LanguageKey MENU_CHANGE_PASSWORD = new LanguageKey(MENU_BUNDEL, "changePassword");  
  public static final LanguageKey MENU_USER_PREFERENCES = new LanguageKey(MENU_BUNDEL, "userPreferences");
  public static final LanguageKey MENU_ABOUT = new LanguageKey(MENU_BUNDEL, "about");    
  
  public static final String ADMIN_BUNDEL = "focAdmin";
  public static final LanguageKey ADMIN_USER = new LanguageKey(ADMIN_BUNDEL, "user");
  public static final LanguageKey ADMIN_SUPER_USER = new LanguageKey(ADMIN_BUNDEL, "superUser");
  public static final LanguageKey ADMIN_GROUP = new LanguageKey(ADMIN_BUNDEL, "group");  
  public static final LanguageKey ADMIN_CHANGE_LANGUAGE = new LanguageKey(ADMIN_BUNDEL, "changeLanguage");
  public static final LanguageKey ADMIN_USER_PREFERENCES = new LanguageKey(ADMIN_BUNDEL, "userPreferences");
  public static final LanguageKey ADMIN_NAME = new LanguageKey(ADMIN_BUNDEL, "name");
  public static final LanguageKey ADMIN_LANGUAGE = new LanguageKey(ADMIN_BUNDEL, "language");
  public static final LanguageKey ADMIN_TEXT_SIZE = new LanguageKey(ADMIN_BUNDEL, "textSize");
  //public static final LanguageKey ADMIN_CHANGE_LANGUAGE = new LanguageKey(ADMIN_BUNDEL, "Changelanguage");  
  
  public static final String COMMAND_BUNDEL = "focCommand";
  public static final LanguageKey COMMAND_SAVE = new LanguageKey(COMMAND_BUNDEL, "save");
  public static final LanguageKey COMMAND_OK = new LanguageKey(COMMAND_BUNDEL, "ok");  
  public static final LanguageKey COMMAND_CANCEL = new LanguageKey(COMMAND_BUNDEL, "cancel");  
  public static final LanguageKey COMMAND_SELECT = new LanguageKey(COMMAND_BUNDEL, "select");  
  public static final LanguageKey COMMAND_VALIDATE = new LanguageKey(COMMAND_BUNDEL, "validate");
  public static final LanguageKey COMMAND_PRINT = new LanguageKey(COMMAND_BUNDEL, "print");

  public static final String CURR_BUNDEL = "focCurrency";
  public static final LanguageKey CURR_CURRENCY = new LanguageKey(CURR_BUNDEL, "currency");
  public static final LanguageKey CURR_CURRENCIES = new LanguageKey(CURR_BUNDEL, "currencies");
  public static final LanguageKey CURR_RATES = new LanguageKey(CURR_BUNDEL, "rates");
  public static final LanguageKey CURR_RATE = new LanguageKey(CURR_BUNDEL, "rate");
  public static final LanguageKey CURR_DATE = new LanguageKey(CURR_BUNDEL, "date");  
  public static final LanguageKey CURR_FIRST_DATE = new LanguageKey(CURR_BUNDEL, "firstDate");
  public static final LanguageKey CURR_BASE_CURRENCY = new LanguageKey(CURR_BUNDEL, "baseCurrency");
  public static final LanguageKey CURR_CONFIG = new LanguageKey(CURR_BUNDEL, "config");
  public static final LanguageKey CURR_DEFAULT_VIEW_CURRENCY = new LanguageKey(CURR_BUNDEL, "defaultViewCurrency");
  
  public static final String CASHDESK_BUNDEL = "focCashDesk";
  public static final LanguageKey CASH_CASHDESK_CONFIG = new LanguageKey(CASHDESK_BUNDEL, "cashDeskConfig");
  public static final LanguageKey CASH_CASHDESK = new LanguageKey(CASHDESK_BUNDEL, "cashDesk");
  public static final LanguageKey CASH_MOVEMENT_ERROR_CHECK = new LanguageKey(CASHDESK_BUNDEL, "movementErrorCheck");
  public static final LanguageKey CASH_CASHDESK_OWNER = new LanguageKey(CASHDESK_BUNDEL, "cashDeskOwner");
  public static final LanguageKey CASH_CASH = new LanguageKey(CASHDESK_BUNDEL, "cash");
  public static final LanguageKey CASH_CHECK = new LanguageKey(CASHDESK_BUNDEL, "check");
  public static final LanguageKey CASH_DATE = new LanguageKey(CASHDESK_BUNDEL, "date");
  public static final LanguageKey CASH_FIRST_DATE = new LanguageKey(CASHDESK_BUNDEL, "firstDate");
  public static final LanguageKey CASH_LAST_DATE = new LanguageKey(CASHDESK_BUNDEL, "lastDate");
  public static final LanguageKey CASH_INTERVAL_MODE = new LanguageKey(CASHDESK_BUNDEL, "intervalMode");
  public static final LanguageKey CASH_OPENING = new LanguageKey(CASHDESK_BUNDEL, "opening");
  public static final LanguageKey CASH_CLOSING = new LanguageKey(CASHDESK_BUNDEL, "closing");
  public static final LanguageKey CASH_AMMOUNT = new LanguageKey(CASHDESK_BUNDEL, "ammount");
  public static final LanguageKey CASH_OPEN_AMMOUNT = new LanguageKey(CASHDESK_BUNDEL, "openAmmount");
  public static final LanguageKey CASH_CLOSE_AMMOUNT = new LanguageKey(CASHDESK_BUNDEL, "closeAmmount");
  public static final LanguageKey CASH_VARIATION_AMMOUNT = new LanguageKey(CASHDESK_BUNDEL, "variationAmmount");
  public static final LanguageKey CASH_IS_CLOSED = new LanguageKey(CASHDESK_BUNDEL, "isClosed");
  public static final LanguageKey CASH_IS_OPENED = new LanguageKey(CASHDESK_BUNDEL, "isOpened");
  public static final LanguageKey CASH_OPENING_AT = new LanguageKey(CASHDESK_BUNDEL, "openingAt");
  public static final LanguageKey CASH_CLOSING_AT = new LanguageKey(CASHDESK_BUNDEL, "closingAt");  
  public static final LanguageKey CASH_PREVIOUS_CLOSING = new LanguageKey(CASHDESK_BUNDEL, "previousClosing");
  public static final LanguageKey CASH_COMPUTED_CLOSING = new LanguageKey(CASHDESK_BUNDEL, "computedClosing");
  public static final LanguageKey CASH_HISTORY = new LanguageKey(CASHDESK_BUNDEL, "history");
  
  public static final LanguageKey CASH_SELECT_USER = new LanguageKey(CASHDESK_BUNDEL, "selectuser");
  
  public static final String GENERAL_BUNDEL = "focGeneral";
  public static final LanguageKey GEN_DESCRIPTION = new LanguageKey(GENERAL_BUNDEL, "description");
  public static final LanguageKey GEN_NAME = new LanguageKey(GENERAL_BUNDEL, "name");
  
}
