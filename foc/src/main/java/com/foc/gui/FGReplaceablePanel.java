/*
 * Created on 07-Jun-2005
 */
package com.foc.gui;

import javax.swing.SwingUtilities;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGReplaceablePanel extends FPanel {
  private FPanel currentDetailsPanel = null; 
  private FPanel currentDetailsPanelToClean = null;
  
  public FGReplaceablePanel(FPanel initialDetailsPanel){
    super();
    replacePanel(initialDetailsPanel);
  }
  
  public void dispose(){
    cleanCentralPanel_Step2();
    currentDetailsPanel = null; 
    currentDetailsPanelToClean = null;
    super.dispose();
  }
  
  public void cleanCentralPanel_Step1(){
  	super.cleanCentralPanel();
  	currentDetailsPanelToClean = currentDetailsPanel; 
  }
  
  public void cleanCentralPanel_Step2(){
	  if(currentDetailsPanelToClean != null){
	  	StaticComponent.cleanComponent(currentDetailsPanelToClean);
	  	currentDetailsPanelToClean = null;
	  }
  }
  
  public void cleanCentralPanel(){
  	cleanCentralPanel_Step1();
  	cleanCentralPanel_Step2();  	
  }
    
  public void replacePanel(FPanel newPanel){
    if(newPanel != null && newPanel != currentDetailsPanel){
    	cleanCentralPanel_Step1();
    	currentDetailsPanel = newPanel;
    	currentDetailsPanel.setFill(FPanel.FILL_BOTH);
      add(currentDetailsPanel, 0, 0);
      currentDetailsPanel.setVisible(false);
      currentDetailsPanel.setVisible(true);
    }
    SwingUtilities.invokeLater(new Runnable(){
			public void run() {
        cleanCentralPanel_Step2();      	
			}
    });
  }
  
  public FPanel getReplacementPanel(){
    return currentDetailsPanel;
  }
}
