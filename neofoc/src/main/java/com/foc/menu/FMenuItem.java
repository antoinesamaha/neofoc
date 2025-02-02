/*
 * Created on 22-May-2005
 */
package com.foc.menu;

import javax.swing.*;

import com.foc.business.multilanguage.LanguageKey;

/**
 * @author 01Barmaja
 */
public class FMenuItem extends FMenu{
  
  AbstractAction action = null;
  
  public FMenuItem(LanguageKey langKey, String code, AbstractAction action){
    super(langKey, code);
    this.action = action ;
  }
  
  public FMenuItem(String title, int mnemonic, String code, AbstractAction action){
    super(title, mnemonic, code);
    this.action = action ;
  }
  
  public FMenuItem(LanguageKey langKey, AbstractAction action){
    super(langKey);
    this.action = action ;
  }
  
  public FMenuItem(String title, int mnemonic, AbstractAction action){
    super(title, mnemonic);
    this.action = action ;
  }
  
  public boolean isList(){
    return false;
  }
  
  public boolean isSeparator(){
  	return false;
  }
  
  public AbstractAction getAction() {
    return action;
  }
}
