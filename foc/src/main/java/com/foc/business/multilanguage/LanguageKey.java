/*
 * Created on 09-Jun-2005
 */
package com.foc.business.multilanguage;

/**
 * @author 01Barmaja
 */
public class LanguageKey {
  private String bundle = null;
  private String key    = null;
  
  public LanguageKey(String bundle, String key){
    this.bundle = bundle;
    this.key    = key;
  }
  
  public String getBundle() {
    //return "properties/"+bundle;
    //return "c:/01barmaja/dev/java/app/mboq/properties/"+bundle;
    return bundle;    
  }
  
  public String getKey() {
    return key;
  }
}
