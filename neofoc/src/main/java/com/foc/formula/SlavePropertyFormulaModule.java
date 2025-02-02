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
package com.foc.formula;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

import com.foc.*;
import com.foc.desc.*;
import com.foc.gui.FPopupMenu;
import com.foc.menu.FMenuList;

/**
 * @author 01Barmaja
 */
public class SlavePropertyFormulaModule extends FocModule {

  public SlavePropertyFormulaModule() {
  }
  
  public static JMenuItem addPopUpMenu(FPopupMenu popupMenu, ActionListener listener){
    
    JMenuItem fieldFormulas = new JMenuItem("Field Formulas");
    fieldFormulas.addActionListener(listener);
    
    popupMenu.add(fieldFormulas);
    return fieldFormulas;
  }

  @Override
  public void declareFocObjectsOnce() {
    declareFocDescClass(SlavePropertyFormulaDesc.class);
  }
  
  public void declare() {
    Application app = Globals.getApp();
    app.declareModule(this);
  }

  public void addApplicationMenu(FMenuList menuList) {
  }

  public void addConfigurationMenu(FMenuList menuList) {
  }

  public void afterAdaptDataModel() {
  }

  public void afterApplicationEntry() {
  }

  public void afterApplicationLogin() {
  }

  public void beforeAdaptDataModel() {
  }

  public void dispose() {
  }
  
  private static SlavePropertyFormulaModule module = null;
  public static SlavePropertyFormulaModule getInstance(){
    if(module == null){
      module = new SlavePropertyFormulaModule();
    }
    return module;
  }
}
