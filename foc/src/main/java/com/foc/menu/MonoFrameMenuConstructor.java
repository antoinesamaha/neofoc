/*
 * Created on 23-May-2005
 */
package com.foc.menu;

import java.awt.GridBagConstraints;

import com.foc.*;
import com.foc.gui.*;

/**
 * @author 01Barmaja
 */
public class MonoFrameMenuConstructor implements MenuConstructor{
  
  private FPanel containerPanel = null;
  private FPanel panel = null;
  private int y = 0;
  
  public MonoFrameMenuConstructor(){
    containerPanel = new FPanel();
    panel = new FPanel();
    panel.setMainPanelSising(FPanel.FILL_NONE);
    panel.setFill(FPanel.FILL_NONE);
    containerPanel.add(panel, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE);
  }
  
  public void showMenu(){
    Globals.getDisplayManager().changePanel(containerPanel);
  }
  
  public void addMenuList(FMenuList menuList, boolean isMainMenu){
    if(!isMainMenu){
      //FGLabel label = new FGLabel(menuList.getTitle());
      //panel.add(label, 0, y, java.awt.GridBagConstraints.EAST);
    }

    for(int i=0; i<menuList.menuCount(); i++){
      FMenu menu = menuList.getMenu(i);
      if(menu.isList()){
        addMenuList((FMenuList) menu, false);
      }else{
        addMenuItem((FMenuItem) menu);
      }
    }
  }
  
  public void addMenuItem(FMenuItem menuItem){
    FGButton button = new FGButton(menuItem.getTitle());
    button.addActionListener(menuItem.getAction());
    panel.add(button, 1, y++, 1, 1, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.HORIZONTAL);
  }
  
  public void addMainMenu(FMenu menu){
    if(menu != null){
      if(menu.isList()){
        addMenuList((FMenuList)menu, true);
      }else{
        addMenuItem((FMenuItem)menu);
      }
    }
  }  
}
