/*
 * Created on 27-Apr-2005
 */
package com.foc.gui;

import javax.swing.*;

/**
 * @author 01Barmaja
 */
public class MainPanel {
  JComponent currentDefaultFocusComponent = null;
  JComponent thePanel = null;
  
  public MainPanel(JComponent panel){
    thePanel = panel;
  }
  
  /**
   * @return Returns the currentDefaultFocusComponent.
   */
  public JComponent getCurrentDefaultFocusComponent() {
    return currentDefaultFocusComponent;
  }
  
  /**
   * @param currentDefaultFocusComponent The currentDefaultFocusComponent to set.
   */
  public void setCurrentDefaultFocusComponent(JComponent currentDefaultFocusComponent) {
    this.currentDefaultFocusComponent = currentDefaultFocusComponent;
  }
  
  public void refreshFocus(){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        currentDefaultFocusComponent.requestFocusInWindow();
      }
    });
  }
  
  /**
   * @return Returns the thePanel.
   */
  public JComponent getThePanel() {
    return thePanel;
  }
  
  /**
   * @param thePanel The thePanel to set.
   */
  public void setThePanel(JComponent thePanel) {
    this.thePanel = thePanel;
  }
}
