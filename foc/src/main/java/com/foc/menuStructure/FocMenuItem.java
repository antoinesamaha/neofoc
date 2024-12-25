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
package com.foc.menuStructure;

import com.foc.desc.FocConstructor;
import com.foc.menuStructure.autoGen.AutoGen_FocMenuItem;

@SuppressWarnings("serial")
public class FocMenuItem extends AutoGen_FocMenuItem {
	
  private IFocMenuItemAction menuAction = null;
  
//	private ArrayList<IFocMenuItemAction> extraActions = null;
	
  public FocMenuItem(FocConstructor constr){
    super(constr);
    setGuest(false);
  }
  
  public void dispose() {
  	super.dispose();
  	menuAction = null;
  }

	public IFocMenuItemAction getMenuAction() {
		return menuAction;
	}

	public void setMenuAction(IFocMenuItemAction menuAction) {
	  this.menuAction = menuAction;
	}
	
//	
//	public ArrayList<IFocMenuItemAction> getExtraActions() {
//	  if(extraActions == null && FLD_EXTRA_COUNT>0){
//	    extraActions = new ArrayList<IFocMenuItemAction>();
//	  }
//	  return extraActions;
//	}
//	
//	public int getExtraActionCount() {
//	  return getExtraActions().size();
//	}
//	
//	public boolean hasExtraActions() {
//	  if(getExtraActionCount() > 0) return true;
//	  else return false;
//	}
//	
//	
//	public void addExtraAction(IFocMenuItemAction extraAction) {
//	  getExtraActions().add(extraAction);
//	}
  
	public FocMenuItemList getMenuList(){
		return (FocMenuItemList) getFatherSubject();
	}
	
	public FocMenuItem pushMenu(String code){
		return getMenuList().pushMenu(this, code ,code);
	}
	
	public FocMenuItem pushMenu(String code, String title){
		FocMenuItem item = null;
		item = getMenuList().pushMenu(this, code ,title);
		return item;
	}
	
  public FocMenuItem pushMenu(FocMenuItem menuItem){
    FocMenuItem newMenuItem = getMenuList().pushMenu(this, menuItem.getCode(), menuItem.getTitle());
    newMenuItem.setGuest(menuItem.getGuest());
    newMenuItem.setMenuAction(menuItem.getMenuAction());
    return newMenuItem;
  }
  
  
  // Methods added to find out if a menu item is allowed for a guest or not
  
  public boolean getGuest(){
    return (boolean) getPropertyBoolean(FLD_GUEST);
  }

  public void setGuest(boolean obj){
    setPropertyBoolean(FLD_GUEST, obj);
  }
  
  public void setHasAccess(boolean hasAccess){
  	setPropertyBoolean(FocMenuItemDesc.FLD_HAS_ACCESS, hasAccess);
  }
  
  public boolean getHasAccess(){
  	return getPropertyBoolean(FocMenuItemDesc.FLD_HAS_ACCESS);
  }
	
}
