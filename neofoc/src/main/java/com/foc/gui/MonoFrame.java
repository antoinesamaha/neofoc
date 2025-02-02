/*
 * Created on 23-May-2005
 */
package com.foc.gui;

import java.awt.Dimension;

import javax.swing.SwingUtilities;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.menu.*;

/**
 * @author 01Barmaja
 */
public class MonoFrame implements Navigator {

  private MainFrame mainFrame = null;
  
  public MonoFrame(MainFrame mainFrame){
    this.mainFrame = mainFrame;
    mainFrame.initSize();
    mainFrame.setPreferredSize(new Dimension(ConfigInfo.getGuiNavigatorWidth(), ConfigInfo.getGuiNavigatorHeight()));
    //mainFrame.setMinimumSize(new Dimension(ConfigInfo.getGuiNavigatorWidth(), ConfigInfo.getGuiNavigatorHeight()));
    //mainFrame.pack();
  }
    
  public Dimension getViewportDimension(){
    return mainFrame.getPreferredSize();//getContentPane().getSize();
  }
  
  public FPanelSequence getActivePanelSequence(){
    return mainFrame.getPanelSequence();
  }
  
  public void changeView(FPanel panel) {
    FPanelSequence panSeq = mainFrame.getPanelSequence();
    panSeq.changePanel(panel);
    mainFrame.repaint();
    mainFrame.setVisible(false);
    if(Globals.getApp().isUnitTesting()){
    	mainFrame.setVisible(true);
    }
    SwingUtilities.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        mainFrame.setVisible(true);
      }
    });
  }
  
  public boolean goBack() {
    FPanelSequence panSeq = mainFrame.getPanelSequence();
    boolean exit = panSeq.goBack(true);
    if(!exit){
      mainFrame.setVisible(false);
      SwingUtilities.invokeLater(new Runnable() {
        
        @Override
        public void run() {
          mainFrame.setVisible(true);
        }
      });   
      //mainFrame.pack();
    }
    return exit;
  }
  
  public InternalFrame newView(FPanel panel) {
    changeView(panel);
    return null;
  }
  
  public void packActiveFrame(){
    mainFrame.pack();
  }
  
  public MenuConstructor getMainMenuConstructor(){
    MonoFrameMenuConstructor mc = new MonoFrameMenuConstructor();
    return mc;
  }
}
