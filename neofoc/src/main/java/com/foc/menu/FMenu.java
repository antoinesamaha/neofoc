/*
 * Created on 22-May-2005
 */
package com.foc.menu;

import com.foc.Globals;
import com.foc.admin.MenuRightsDesc;
import com.foc.business.multilanguage.LanguageKey;
import com.foc.business.multilanguage.MultiLanguage;

/**
 * @author 01Barmaja
 */
public abstract class FMenu {
  private LanguageKey langKey  = null;
  private String      title    = null;
  private int         mnemonic = -1  ;
  private boolean     enabled  = true;
  private String      code     = null;
  
  public abstract boolean isList();

  public FMenu(LanguageKey langKey){
    this(langKey, null);
  }

  public FMenu(String title, int mnemonic){
    this(title, mnemonic, null);
  }
  
  public FMenu(String title, int mnemonic, String code){
    this.title    = title;
    this.mnemonic = mnemonic; 
    this.code     = code;
  }
  
  public FMenu(LanguageKey langKey, String code){
    this.langKey = langKey;
    this.code    = code;
  }
  
  public String getTitle() {
    return (title != null) ? title : MultiLanguage.getString(langKey); 
  }

  public int getMnemonic() {
    return (title != null) ? mnemonic : MultiLanguage.getMnemonic(langKey); 
  }
  
  public void setTitle(String title){
  	this.title = title;
  }
  
  public void setMnemonic(int mnemonic){
  	this.mnemonic = mnemonic;
  }
  
  public void setEnabled(boolean enabled){
  	this.enabled = enabled;
  }
  
  public boolean isEnabled(){
  	return this.enabled;
  }

  public String getCode() {
    return code;
  }
  
  public void setCode(String code) {
    this.code = code;
    if(code.length() > MenuRightsDesc.LEN_MENU_CODE){
    	String truncatedCode = code.substring(0, MenuRightsDesc.LEN_MENU_CODE);
   		Globals.logString("Menu code '"+code+"' too long truncated to '"+truncatedCode+"'");
    	this.code = truncatedCode;
    }
  }
  
  public FMenu findMenuForCode(String code){
  	FMenu found = null;
  	if(code != null && !code.isEmpty()){
	  	if(getCode() != null && getCode().equals(code)){
	  		found = this;
	  	}else if(isList()){
	  		FMenuList list = (FMenuList) this;
	  		for(int i=0; i<list.menuCount() && found == null; i++){
	  			FMenu menu = list.getMenu(i);
	  			found = menu.findMenuForCode(code);
	  		}
	  	}
  	}
  	return found;
  }
}
