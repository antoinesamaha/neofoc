/*
 * Created on 07-Jun-2005
 */
package com.foc.gui;

import javax.swing.SwingUtilities;
import javax.swing.event.*;

import com.foc.Globals;
import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class FGCurrentItemPanel extends FPanel implements ListSelectionListener{
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3617858577310823472L;
  private FAbstractListPanel listPanel = null;
  private int detailedViewID = 0;
  private FocObject currentDisplayedObject = null;
  private FPanel currentDetailsPanel = null; 
  private FPanel currentDetailsPanelToClean = null;
  
  public FGCurrentItemPanel(FAbstractListPanel listPanel, int detailedViewID){
    super();
    this.listPanel = listPanel;
    this.detailedViewID = detailedViewID;
    refreshContents();
    listPanel.getTable().addSelectionListener(this);
    //setBackground(Color.ORANGE);
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
  
  public void dispose(){
    listPanel = null;
    currentDisplayedObject = null;
    cleanCentralPanel();     
  }
  
  public void refreshContents(){
    if (listPanel != null) {
     // listPanel.recaptureCursorPositionIfGood();
      
      FocObject selectedItem = (FocObject) listPanel.getSelectedObject();
      boolean guiChanged = false;

      /*if (selectedItem == null){
        FocList list = listPanel.getFocList();
        selectedItem = list.getAnyItem();
      }*/

      if(currentDisplayedObject != selectedItem){
        currentDisplayedObject = selectedItem;
        guiChanged = true;
      }
      
      
      //ATTENTION DEBUG
      //ATTENTION DEBUG      
      //FocList list = listPanel.getFocList();
      //if(list.getFocDesc().getStorageName().compareTo("L3SAMPLE") == 0) guiChanged = false;
      //ATTENTIO DEBUG      
      
      if (guiChanged) {
      	Globals.logString("NEW PANEL CREATED!!!!");
        cleanCentralPanel_Step1();
        if(currentDisplayedObject != null){
          currentDetailsPanel = currentDisplayedObject.newDetailsPanel(detailedViewID);
          //add(panel, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH);
          //add(new FGLabel("rerer"), 1, 1);
          if(currentDetailsPanel != null){
          	currentDetailsPanel.setFill(FPanel.FILL_BOTH);
            add(currentDetailsPanel, 0, 0);
            currentDetailsPanel.setVisible(false);
            currentDetailsPanel.setVisible(true);
          }
        }
        SwingUtilities.invokeLater(new Runnable(){
					public void run() {
		        cleanCentralPanel_Step2();      	
					}
        });
      }
    }
  }
  
  public FPanel getCurrentItemPanel(){
    return currentDetailsPanel;
  }
  
  public void valueChanged(ListSelectionEvent e) {
    refreshContents();
  }

}
