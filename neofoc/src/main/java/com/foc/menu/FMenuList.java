/*
 * Created on 22-May-2005
 */
package com.foc.menu;

import java.util.*;

import com.foc.business.multilanguage.LanguageKey;

/**
 * @author 01Barmaja
 */
public class FMenuList extends FMenu{
  
  private LinkedList<FMenu> list = null;

  public FMenuList(LanguageKey langKey, String code){
    super(langKey, code);
    list = new LinkedList<FMenu>();
  }

  public FMenuList(String title, int mnemonic, String code){
    super(title, mnemonic, code);
    list = new LinkedList<FMenu>();
  }
  
  public FMenuList(LanguageKey langKey){
    super(langKey);
    list = new LinkedList<FMenu>();
  }

  public FMenuList(String title, int mnemonic){
    super(title, mnemonic);
    list = new LinkedList<FMenu>();
  }
  
  public void addMenu(FMenu menu){
    list.add(menu);
  }

  public void removeMenu(FMenu menu){
    list.remove(menu);
  }
  
  public int menuCount(){
    return list.size();
  }
  
  public FMenu getMenu(int i){
    return (FMenu) list.get(i);
  }
  
  public char getMnemonicForMenu(FMenu menu){
  	char mnemonic = ' ';
  	String menuTitle = menu.getTitle();
  	if(menuTitle != null){
  		boolean similarMnemoniFoundInPreviousMenus = true;
  		int i = -1;
  		while(similarMnemoniFoundInPreviousMenus && i < menuTitle.length()){
  			i++;
	  		mnemonic = menuTitle.charAt(i);
	  		//Iterator<String> iter = getHelpMenuesIterator();
	  		boolean found = false;
	  		boolean finish = false;
	  		//while(iter != null && iter.hasNext() && !found && !finish){
	  		for(int j = 0; j < menuCount() && !found && !finish; j++ ){
	  			FMenu aMenu = getMenu(j);
	  			String aMenuTitle = aMenu.getTitle();
	  			if(aMenuTitle.equals(menuTitle)){
	  				finish = true;
	  			}
	  			if(!finish){
		  			char aMenuMnemonic = getMnemonicForMenu(aMenu);
		  			found = aMenuMnemonic == mnemonic;
	  			}
	  		}
	  		similarMnemoniFoundInPreviousMenus = found;
  		}
  	}
  	return mnemonic;
  }
  
  public boolean isList(){
    return true;
  }
}
