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
package com.foc.business.multilanguage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.foc.desc.AutoPopulatable;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class LanguageDesc extends FocDesc implements AutoPopulatable{

  public static final int FLD_CODE        = 1;
  public static final int FLD_NAME        = 2;
  public static final int FLD_DESCRIPTION = 3;
  
  public static final String DB_TABLE_NAME = "LANGUAGE";
  
  public LanguageDesc() {
    super(Language.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, true);
    setGuiBrowsePanelClass(LanguageGuiBrowsePanel.class);
    setGuiDetailsPanelClass(LanguageGuiDetailsPanel.class);
        
    addReferenceField();
   
    FMultipleChoiceStringField codeField = new FMultipleChoiceStringField("NAME", "Name", FLD_NAME, false, 40);
    HashMap<String, Locale> map = MultiLanguage.getAvailableLanguages();
    Iterator<String> iter = map.keySet().iterator();
    while(map != null && iter != null && iter.hasNext()){
    	String code = iter.next();
    	codeField.addChoice(code);
    }
    codeField.setMandatory(true);
    addField(codeField);
    
    codeField = new FMultipleChoiceStringField("CODE", "Iso code", FLD_CODE, false, 2);
    map = MultiLanguage.getAvailableLanguages();
    Iterator<Locale> iterL = map.values().iterator();
    while(map != null && iterL != null && iterL.hasNext()){
    	Locale loc  = iterL.next();    	
    	codeField.addChoice(loc.getLanguage());
    }
    codeField.setAllwaysLocked(true);
    codeField.setMandatory(true);
    addField(codeField);
    
    FStringField cFld = new FStringField("DESCRIP", "Description", FLD_DESCRIPTION, true, 40);
    cFld.setMandatory(true);
    addField(cFld);
  }

  @Override
	public void beforeLogin() {
		super.beforeLogin();
		
		FocList list = getList(FocList.FORCE_RELOAD);
		for(int i=0; i<list.size(); i++){
			Language lang = (Language) list.getFocObject(i);
			if(lang != null){
				AppLanguage foundAppLang = null;
				AppLanguage appLanguage  = null;
				for(int a=0; a<MultiLanguage.getLanguageNumber() && foundAppLang == null; a++){
					appLanguage = (AppLanguage) MultiLanguage.getLanguageAt(a);
					if(appLanguage != null && appLanguage.getLocale() != null && appLanguage.getLocale().getLanguage().equals(lang.getCode())){
						foundAppLang = appLanguage;
					}
				}
				
				if(foundAppLang == null){
					foundAppLang = new AppLanguage(lang);
					MultiLanguage.addLanguage(foundAppLang);
				}else{
					foundAppLang.setLanguage(lang);
				}
			}
		}

		for(int i=0; i<MultiLanguage.getLanguageNumber(); i++){
			AppLanguage appLanguage = MultiLanguage.getLanguageAt(i);
			if(appLanguage != null && appLanguage.getLanguage() == null){
				Language language = (Language) list.newEmptyItem();
				if(language != null){
					appLanguage.setAndFillLanguage(language);
					language.validate(true);
				}
			}
		}
		list.validate(true);		
	}
  
	private void createDefaultLanguages(){
	}
	
	@Override
	public void afterAdaptTableModel() {
	}
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocList getList(int mode){
    return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
    FocList list = super.newFocList();
    list.setDirectlyEditable(false);
    list.setDirectImpactOnDatabase(true);
    if(list.getListOrder() == null){
      list.setListOrder(new FocListOrder(LanguageDesc.FLD_DESCRIPTION));
    }
    
    return list;
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FocDesc getInstance() {
    return getInstance(DB_TABLE_NAME, LanguageDesc.class);    
  }

	public String getAutoPopulatableTitle() {
		return "Languages";
	}

	public boolean populate() {
		FocList list = getList(FocList.FORCE_RELOAD);
		
		for(int i=0; i<Locale.getAvailableLocales().length; i++){
			Locale l = Locale.getAvailableLocales()[i];
			String   languageDisplay = l.getDisplayLanguage();
			String   languageCode    = l.getLanguage();
			if(!languageDisplay.isEmpty() && !languageCode.isEmpty()){
				Language lang = (Language) list.searchByPropertyStringValue(LanguageDesc.FLD_DESCRIPTION, languageDisplay);
				if(lang == null){
					lang = (Language) list.searchByPropertyStringValue(LanguageDesc.FLD_NAME, languageDisplay);
				}
				if(lang == null){
					lang = (Language) list.searchByPropertyStringValue(LanguageDesc.FLD_CODE, languageCode);
				}
				if(lang == null){
					lang = (Language) list.newEmptyItem();
					lang.setName(languageDisplay);
					lang.setDescription(languageDisplay);
					lang.setCode(languageCode);
					list.add(lang);
				}
			}
		}
		
		list.validate(true);
		return false;
	}
}
