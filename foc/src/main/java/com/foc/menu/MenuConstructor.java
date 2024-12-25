/*
 * Created on 23-May-2005
 */
package com.foc.menu;

/**
 * @author 01Barmaja
 */
public interface MenuConstructor {
  public void showMenu();
  public void addMenuList(FMenuList menuList, boolean isMainMenu);
  public void addMenuItem(FMenuItem menuItem);
  public void addMainMenu(FMenu menu);
}
