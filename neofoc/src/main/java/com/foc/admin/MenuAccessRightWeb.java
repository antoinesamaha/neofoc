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
package com.foc.admin;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;

public class MenuAccessRightWeb extends FocObject{

	private boolean    visited          = false;
	
  public MenuAccessRightWeb(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public void copy(MenuAccessRightWeb source){
    setGroup(source.getGroup());
    setFatherMenu(source.getFatherMenu());
    setCode(source.getCode());
    setTitle(source.getTitle());
    setRight(source.getRight());
    setCustomTitle(source.getCustomTitle());
  }
  
  public FocGroup getGroup(){
    return (FocGroup) getPropertyObject(MenuRightsDesc.FLD_GROUP);
  }
  
  public void setGroup(FocGroup group){
    setPropertyObject(MenuRightsDesc.FLD_GROUP, group);
  }
  
  public MenuAccessRightWeb getFatherMenu(){
    return (MenuAccessRightWeb) getPropertyObject(MenuRightsDesc.FLD_FATHER_MENU_RIGHT);
  }
 
  public void setFatherMenu(MenuAccessRightWeb menuRights){
    setPropertyObject(MenuRightsDesc.FLD_FATHER_MENU_RIGHT, menuRights);
  }
  
  public String getCode(){
    return getPropertyString(MenuRightsDesc.FLD_MENU_CODE);
  }

  public void setCode(String code){
    setPropertyString(MenuRightsDesc.FLD_MENU_CODE, code);
  }

  public String getTitle(){
    return getPropertyString(MenuRightsDesc.FLD_MENU_TITLE);
  }

  public void setTitle(String title){
    setPropertyString(MenuRightsDesc.FLD_MENU_TITLE, title);
  }

  public String getCustomTitle(){
    return getPropertyString(MenuRightsDesc.FLD_CUSTOM_TITLE);
  }

  public void setCustomTitle(String title){
    setPropertyString(MenuRightsDesc.FLD_CUSTOM_TITLE, title);
  }

  public String getTitleToDisplay(){
  	String str = getCustomTitle();
    return str.isEmpty() ? getTitle() : str;
  }

  public int getRight(){
    return getPropertyMultiChoice(MenuRightsDesc.FLD_RIGHT);
  }

  public void setRight(int right){
    setPropertyMultiChoice(MenuRightsDesc.FLD_RIGHT, right);
  }

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
