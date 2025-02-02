/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import java.awt.*;

import javax.swing.*;

import com.foc.*;

import java.awt.event.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

  private FPanelSequence panelSequence = null;
  
  /**
   * @throws java.awt.HeadlessException
   */
  public MainFrame(){
    super();

    setFont(Globals.getDisplayManager().getDefaultFont());
    
    setWindowTitle();
    
    if(Globals.getApp().isShowBarmajaIconAndTitle()){
	    try{
	    	this.setIconImage(Globals.getIcons().getLogoIcon().getImage());
	    }catch(Exception e){
	    	Globals.getDisplayManager().popupMessage("Logo icon not fount");
	    }
    }
    panelSequence = new FPanelSequence();
    setContentPane(panelSequence.getCenterPanel());
    
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    WindowListener exitListener = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        //Window window = e.getWindow();
        //window.setVisible(false);
        //window.dispose();
        Globals.getApp().exit();        
      }
    };
    
    addWindowListener(exitListener);    
  }

  public void setWindowTitle(){
    String windTitle = "";
    if(Globals.getApp() != null && Globals.getApp().getUser() != null && Globals.getApp().getCurrentCompany() != null){
    	windTitle = Globals.getApp().getCurrentCompany().getName();
    }
    if(ConfigInfo.getWindowTitle() != null && !ConfigInfo.getWindowTitle().isEmpty() && !windTitle.isEmpty()){
    	windTitle += " - ";
    }
    
    String commandWindowTitle = Globals.getApp().getCommandLineArgument("windowTitle");
    if(commandWindowTitle != null && !commandWindowTitle.isEmpty()){
    	windTitle += commandWindowTitle;
    }else{
    	windTitle += ConfigInfo.getWindowTitle();	
    }
    
    if(windTitle != null && windTitle.trim().compareTo("") != 0){
      if(Globals.getApp().isShowBarmajaIconAndTitle()){
        setTitle("01Barmaja   ->  "+windTitle);
      }else{
      	setTitle(windTitle);
      }
    }else{
    	if(Globals.getApp().isShowBarmajaIconAndTitle()){
    		setTitle("01Barmaja");
    	}
    }
  }
  
  public void initSize(){
    if(ConfigInfo.getGuiNavigatorWidth() == 0 && ConfigInfo.getGuiNavigatorHeight() == 0){
      setExtendedState(Frame.MAXIMIZED_BOTH);
      Rectangle rect = getBounds();
      ConfigInfo.setGuiNavigatorWidth(rect.width);   
      ConfigInfo.setGuiNavigatorHeight(rect.height);
    }else{
      setPreferredSize(new Dimension(ConfigInfo.getGuiNavigatorWidth(), ConfigInfo.getGuiNavigatorHeight()));
    }
  }
  
  public JComponent getMainPanel() {
    return panelSequence.getMainPanel();
  }

  /*
  private void adjustSize(Dimension dim) {
    Dimension realDim = dim;
    if (realDim.width < 700) {
      realDim.width = 700;
    }
    if (realDim.height < 550) {
      realDim.height = 550;
    }
    this.setMinimumSize(realDim);
    this.setPreferredSize(realDim);
    // this.mainPanel.setMinimumSize(realDim);
    // this.mainPanel.setPreferredSize(realDim);
    pack();
  }
  */

  public void setMainPanel(FPanel mainPanel) {
    panelSequence.setMainPanel(mainPanel);
  }
  
  public FPanelSequence getPanelSequence() {
    return panelSequence;
  }
  
  @Override
  public void pack(){
  	super.pack();
  }
}
