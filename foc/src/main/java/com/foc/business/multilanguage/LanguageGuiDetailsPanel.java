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

import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JComponent;

import com.foc.desc.FocObject;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class LanguageGuiDetailsPanel extends FPanel{

	public final static int VIEW_SELECTION = 2; 
	
	private FPropertyListener nameListener = null;
	private Language          language     = null;
	
	public LanguageGuiDetailsPanel(FocObject obj, int viewID){
		language = (Language) obj;
		if(viewID == VIEW_SELECTION){
			setInsets(0, 0, 0, 0);
			FGTextField comp = (FGTextField) obj.getGuiComponent(LanguageDesc.FLD_DESCRIPTION);
			comp.setEditable(false);
			add(comp, 1, 0);
		}else{
			JComponent comp = (JComponent) obj.getGuiComponent(LanguageDesc.FLD_CODE);
			add(obj, LanguageDesc.FLD_NAME, 0, 0);
			add(comp, 2, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);			
			add(obj, LanguageDesc.FLD_DESCRIPTION, 0, 1, 2, 1);
			
	    nameListener = new FPropertyListener(){
				public void dispose() {
				}

				public void propertyModified(FProperty property) {
					Language lang = (Language) property.getFocObject();
					if(lang != null){
						HashMap<String, Locale> map = MultiLanguage.getAvailableLanguages();
						Locale loc  = map.get(lang.getName());
						lang.setCode(loc.getLanguage());
						lang.setDescription(lang.getName());
					}
				}
	    };
	    FProperty prop = obj.getFocProperty(LanguageDesc.FLD_NAME);
	    prop.addListener(nameListener);
	    
			FValidationPanel vPanel = showValidationPanel(true);
			vPanel.addSubject(obj);
		}
	}
	
	public void dispose(){
		super.dispose();
		if(nameListener != null && language != null){
			language.getFocProperty(LanguageDesc.FLD_NAME).removeListener(nameListener);
			nameListener.dispose();
		}
		language = null;			
		nameListener = null;
	}
}

