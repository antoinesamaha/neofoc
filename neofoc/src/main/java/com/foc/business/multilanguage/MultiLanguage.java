/*
 * Created on 09-Jun-2005
 */
package com.foc.business.multilanguage;

import java.util.*;

import com.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class MultiLanguage {
  private static ArrayList<AppLanguage>  languageList       = null;
  private static HashMap<String, Locale> languagesAvailable = null;
  private static boolean                 multiLanguage      = false;
  private static AppLanguage             currentLanguage    = null;
  
  public static int DEFAULT_LANGUAGE = 0;
  
  private static ArrayList<AppLanguage> getLanguageList(){
    if(languageList == null){
      languageList = new ArrayList<AppLanguage>();
    }
    return languageList;
  }
  
  public static void addLanguage(AppLanguage language){
    getLanguageList().add(language);
  }

  public static AppLanguage getCurrentLanguage(){
    if(languageList == null){
    	Locale l = Locale.ENGLISH;
      AppLanguage english = new AppLanguage(l.getDisplayLanguage(), l);      
      addLanguage(english);
    }

    if(currentLanguage == null){
      ArrayList array = getLanguageList();
      currentLanguage = (AppLanguage) array.get(0); 
    }
    return currentLanguage;
  }
  
  public static void setCurrentLanguage(int langID){
    ArrayList array = getLanguageList();
    for(int i=0; i<array.size(); i++){
      AppLanguage language = (AppLanguage) array.get(i);
      if(langID == language.getId()){
        currentLanguage = language;
      }
    }
  }

  public static Locale getCurrentLocale(){
    AppLanguage lang = getCurrentLanguage();
    return lang.getLocale();
  }
  
  public static boolean isMultiLanguage(){
    //return getLanguageNumber() > 1;
  	return multiLanguage;
  }

  public static void setMultiLanguage(boolean multiLang){
  	multiLanguage = multiLang;
  }

  public static int getLanguageNumber(){
    return languageList != null ? languageList.size() : 1;
  }
  
  public static AppLanguage getLanguageAt(int i){
    return (AppLanguage) (languageList != null ? languageList.get(i) : null);
  }
  
  public static void fillMutipleChoices(FMultipleChoiceField multiFld){
    ArrayList array = (ArrayList) getLanguageList();
    if(array != null){
      for(int i=0; i<array.size(); i++){
        AppLanguage lang = (AppLanguage)array.get(i);
        multiFld.addChoice(lang.getId(), lang.getName());
      }
    }
  }

  public static HashMap<String, Locale> getAvailableLanguages(){
  	if(languagesAvailable == null){
  		languagesAvailable = new HashMap<String, Locale>();
  		for(int i=0; i<Locale.getAvailableLocales().length; i++){
  			Locale l = Locale.getAvailableLocales()[i];
  			languagesAvailable.put(l.getDisplayLanguage(), l);
  		}
  	}
  	return languagesAvailable;
  }
  
  // --------------------
  
  public static String getString(AppLanguage language, String bundle, String key){
    String str = key;
    if(key != null && language != null){
      str = language.getString(bundle, key);
    }
    return str;
  }
  
  public static int getMnemonic(AppLanguage language, String bundle, String key){
    int mnemonic = -9;
    if(key != null && language != null){
      mnemonic = language.getMnemonic(bundle, key);
    }
    return mnemonic;
  }

  public static String getToolTipText(AppLanguage language, String bundle, String key){
    String toolTipText = null;
    if(key != null && language != null){
      toolTipText = language.getToolTipText(bundle, key);
    }
    return toolTipText;
  }
  
  // --------------------
  
  public static String getString(String bundle, String key){
    return getString(getCurrentLanguage(), bundle, key);
  }
  
  public static int getMnemonic(String bundle, String key){
    return getMnemonic(getCurrentLanguage(), bundle, key);
  }

  public static String getToolTipText(String bundle, String key){
    return getToolTipText(getCurrentLanguage(), bundle, key);
  }
  
  // --------------------
  
  public static String getString(LanguageKey langKey){
    return langKey != null ? getString(getCurrentLanguage(), langKey.getBundle(), langKey.getKey()) : null;
  }
  
  public static int getMnemonic(LanguageKey langKey){
    return langKey != null ? getMnemonic(getCurrentLanguage(), langKey.getBundle(), langKey.getKey()) : -9;
  }

  public static String getToolTipText(LanguageKey langKey){
    return langKey != null ? getToolTipText(getCurrentLanguage(), langKey.getBundle(), langKey.getKey()) : null;
  }
  
  // --------------------
  
  public static String getString(AppLanguage language, LanguageKey langKey){
    return langKey != null ? getString(language, langKey.getBundle(), langKey.getKey()) : null;
  }
  
  public static int getMnemonic(AppLanguage language, LanguageKey langKey){
    return langKey != null ? getMnemonic(language, langKey.getBundle(), langKey.getKey()) : -9;
  }

  public static String getToolTipText(AppLanguage language, LanguageKey langKey){
    return langKey != null ? getToolTipText(language, langKey.getBundle(), langKey.getKey()) : null;
  }
  
  // --------------------
  
  private static FFieldArrayMultilangPlug plug = null; 
  
  public static FFieldArrayMultilangPlug getFieldArrayPlug(){
    if(plug == null){
      plug = new FFieldArrayMultilangPlug();
    }
    return plug;
  }
}
