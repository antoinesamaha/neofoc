/*
 * Created on 22-May-2005
 */
package com.foc.menu;

import java.awt.Component;

import javax.swing.*;

import com.foc.gui.*;

/**
 * @author 01Barmaja
 */
public class MenuBarConstructor implements MenuConstructor {
  private MainFrame mainFrame = null;
  private JMenuBar menuBar = null;
  private JMenuItem currentMenuItem = null;
  
  public MenuBarConstructor(MainFrame mainFrame, JMenuBar menuBar){
    this.menuBar   = menuBar;
    this.mainFrame = mainFrame;
  }
  
  public JMenuBar getMenuBar() {
    return menuBar;
  }
  
  public void setMenuBar(JMenuBar menuBar) {
    this.menuBar = menuBar;
  }
  
  public void addMenuList(FMenuList menuList, boolean isMainMenu){
    if(menuList != null){
      JMenu jMenu = null;
      if(!isMainMenu){      
        String title = menuList.getTitle();
        int mnemonic = menuList.getMnemonic();
        jMenu = new JMenu(title);
        //jMenu.setEnabled(menuList.isEnabled());
        jMenu.setVisible(menuList.isEnabled());

        if(mnemonic >= 0) jMenu.setMnemonic(mnemonic);
        addJMenuItem(jMenu);
      }        
      JMenuItem backupCurrentMenuItem = currentMenuItem;
      currentMenuItem = jMenu;
      
      for(int i=0; i<menuList.menuCount(); i++){
        FMenu menu = menuList.getMenu(i);
        if(menu.isList()){
          addMenuList((FMenuList) menu, false);
        }else{
          addMenuItem((FMenuItem) menu);
        }
      }
      
      currentMenuItem = backupCurrentMenuItem;
    }
  }
  
  public void addMenuItem(FMenuItem menuItem){
    if(menuItem != null){
      String title   = menuItem.getTitle();
      String guiName = menuItem.getCode();
      if(guiName == null || guiName.isEmpty()) guiName = title;
      int mnemonic = menuItem.getMnemonic();
      
      if(menuItem.isSeparator()){
      	JSeparator jMenu = new JSeparator();
      	addJMenuItem(jMenu);
      }else{
	      JMenuItem jMenu = new JMenuItem(title);
	      jMenu.setName("MENU."+guiName);
	      //jMenu.setEnabled(menuItem.isEnabled());
	      jMenu.setVisible(menuItem.isEnabled());
	      if(mnemonic >= 0) jMenu.setMnemonic(mnemonic);
	      addJMenuItem(jMenu);
	      jMenu.addActionListener(menuItem.getAction());
      }
    }
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
  
  private void addJMenuItem(Component jMenuItem){
    if(currentMenuItem != null){
      currentMenuItem.add(jMenuItem);
    }else{
      menuBar.add(jMenuItem);
    }
  }
  
  public void showMenu(){
    mainFrame.setJMenuBar(menuBar);
    mainFrame.setVisible(false);
    mainFrame.setVisible(true);
    
    //Globals.getDisplayManager().newInternalFrame(new FPanel());
    //Globals.getDisplayManager().goBackDontCheckDialogs();    
  }
}
