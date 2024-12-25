/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import javax.swing.*;

import com.foc.*;
import com.foc.desc.*;
import com.foc.desc.field.FField;
import com.foc.property.*;

import java.awt.event.*;
import java.util.ArrayList;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FObjectPanel extends FPanel implements FPropertyListener {
  private FObject objProp             = null;
  private int     viewID              = FocObject.DEFAULT_VIEW_ID;
  private int     zoomViewID          = FocObject.DEFAULT_VIEW_ID;
  private boolean selectButtonVisible = true;
  private boolean zoomButtonVisible   = false;
  private int     buttonAllignment    = HORIZONTAL; 
  private FPanel  detailsPanel        = null;

  private FGButton  selectButton          = null;
  private FGButton  removeSelectionButton = null;
  private FGButton  zoomButton            = null;
  private FocObject focObjectCreatedToGeneratePanelForNULLValues = null;  

  private ArrayList<FObjectPanelListener> listeners = null;
  
  public static final int HORIZONTAL = 1;
  public static final int VERTICAL   = 2;
  
  public FObjectPanel() {
  	setInsets(0, 0, 0, 0);
    showEastPanel(true);
    setFill(FPanel.FILL_NONE);
    listeners = new ArrayList<FObjectPanelListener>();    
  }
  
  public void dispose(){
    super.dispose();
    
    if(listeners != null){
    	listeners.clear();
    	listeners = null;
    }
    
    if(objProp != null){
      objProp.removeListener(this);
      objProp = null;      
    }
    
    if(detailsPanel != null){
      detailsPanel.dispose();
      detailsPanel = null;
    }

    if(selectButton != null){
      selectButton = null;
    }
    
    if(removeSelectionButton != null){
    	removeSelectionButton = null;
    }
    
    if(zoomButton != null){
      zoomButton = null;
    }
    
    if(focObjectCreatedToGeneratePanelForNULLValues != null){
      focObjectCreatedToGeneratePanelForNULLValues.dispose();
      focObjectCreatedToGeneratePanelForNULLValues = null;
    }
  }
  
  public void setProperty(FProperty property){
    this.objProp = (FObject) property;
    objProp.addListener(this);
    propertyModified(objProp);
  }
  
  public void setViewID(int viewID){
    this.viewID = viewID;
  }
  
  public FObject getFObject() {
    return objProp;
  }

  public FocObject getFocObject() {
    FocObject obj = null;
    if (objProp != null) {
      FObject objProp2 = (FObject) objProp;
      obj = (FocObject) objProp2.getObject();
    }
    return obj;
  }

  private void setDetailsPanel(FPanel detailsPanel) {
    if (this.detailsPanel != detailsPanel){
      if(this.detailsPanel != null) {
        this.detailsPanel.setVisible(false);
        this.remove(this.detailsPanel);
      }
      
      this.detailsPanel = detailsPanel;
      this.add(detailsPanel, 0, 0);
      adaptGuiToFlags();      
      this.repaint();
      this.setVisible(false);
      this.setVisible(true);
    }
  }

  private void adaptGuiToFlags() {
    FPanel eastPanel = getEastPanel();
    int xSel  = 0;
    int ySel  = 0;
    int xDel  = 1;
    int yDel  = 0;
    int xEdit = 2;
    int yEdit = 0;
    
    if(buttonAllignment == VERTICAL){
      xSel  = 0;
      ySel  = 0;
      xDel  = 0;
      yDel  = 1;
      xEdit = 0;
      yEdit = 2;      
    }
    
    if (selectButton == null) {
      selectButton = new FGButton(Globals.getIcons().getSelectIcon());
      if(objProp != null && objProp.getFocObject() != null && objProp.getFocObject().getThisFocDesc() != null && objProp.getFocField() != null){
      	selectButton.setName(getSelectButtonName(objProp.getFocObject().getThisFocDesc(), objProp.getFocField().getID()));
      }
      if(ConfigInfo.isUnitDevMode()){
      	StaticComponent.setComponentToolTipText(selectButton, selectButton.getName());
      }
      eastPanel.add(selectButton, xSel, ySel);

      AbstractAction selectAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          try {
            FObject objProp = getFObject();
            if (objProp != null) {
              objProp.popupSelectionPanel();
            }
            notifyFocListeners(true, false);
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
      selectButton.addActionListener(selectAction);

    }
    
    if(removeSelectionButton == null){
    	removeSelectionButton = new FGButton(Globals.getIcons().getDeleteIcon());
//      if(objProp != null && objProp.getFocObject() != null && objProp.getFocObject().getThisFocDesc() != null && objProp.getFocField() != null){
//      	selectButton.setName(getSelectButtonName(objProp.getFocObject().getThisFocDesc(), objProp.getFocField().getID()));
//      }
//      if(ConfigInfo.isUnitDevMode()){
//      	StaticComponent.setComponentToolTipText(removeSelectionButton, removeSelectionButton.getName());
//      }
      eastPanel.add(removeSelectionButton, xDel, yDel);

      AbstractAction removeAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          try {
            FObject objProp = getFObject();
            if (objProp != null && objProp.getObject() != null) {
            	if(!FGOptionPane.popupOptionPane_YesNo("Remove selected value", "Are you sure you want to remove selected value?")){
            		objProp.setObject(null);
            	}
            }
            notifyFocListeners(true, false);
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
      removeSelectionButton.addActionListener(removeAction);
    }
    
    if (zoomButton == null) {
      zoomButton = new FGButton(Globals.getIcons().getEditIcon());
      eastPanel.add(zoomButton, xEdit, yEdit);
      AbstractAction zoomAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          try {
            FObject objProp = getFObject();
            if (objProp != null) {
              FocObject obj = (FocObject)objProp.getObject();
              if(obj != null){
                obj.forceControler(true);
                FPanel panel = obj.newDetailsPanel(zoomViewID);
                Globals.getDisplayManager().changePanel(panel);
              }
            }
            notifyFocListeners(false, true);
          } catch (Exception e1) {
            Globals.logException(e1);
          }
        }
      };
      zoomButton.addActionListener(zoomAction);      
    }
    
    selectButton.setVisible(selectButtonVisible);
    removeSelectionButton.setVisible(selectButtonVisible);
    zoomButton.setVisible(zoomButtonVisible);
  }

  public void setSelectButtonVisible(boolean b) {
    selectButtonVisible = b;
    adaptGuiToFlags();
  }

  public void setZoomButtonVisible(boolean b) {
    zoomButtonVisible = b;
    adaptGuiToFlags();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see b01.foc.property.FPropertyListener#propertyModified(b01.foc.property.FProperty)
   */
  public void propertyModified(FProperty property) {
    if (objProp != null) {
      //FocObject debugObj = (FocObject) objProp.getObject_CreateIfNeeded();
      FocObject focObj = (FocObject) objProp.getObject_CreateIfNeeded();
      if(focObj == null){
        if(focObjectCreatedToGeneratePanelForNULLValues == null){
          FocConstructor constr = new FocConstructor(objProp.getFocDesc(), null);
          focObjectCreatedToGeneratePanelForNULLValues = constr.newItem();
        }
        focObj = focObjectCreatedToGeneratePanelForNULLValues;
      }
      FPanel panel = focObj.newDetailsPanel(viewID);
      setDetailsPanel(panel);
    }
  }
  
  public int getButtonAllignment() {
    return buttonAllignment;
  }
  
  public void setButtonAllignment(int buttonAllignment) {
    this.buttonAllignment = buttonAllignment;
  }
  
  public int getZoomViewID() {
    return zoomViewID;
  }
  
  public void setZoomViewID(int zoomViewID) {
    this.zoomViewID = zoomViewID;
  }
  
  public void setSelectButtonName(String name){
    this.selectButton.setName(name);
  }
  
  public void addFocListener(FObjectPanelListener listener){
  	listeners.add(listener);
  }
  
  public void removeFocListener(FObjectPanelListener listener){
  	listeners.remove(listener);
  }
  
  public void notifyFocListeners(boolean selection, boolean zooming){
  	for(int i=0; i<listeners.size(); i++){
  		FObjectPanelListener listener = listeners.get(i);
  		if(selection){
  			listener.afterSelection(this);
  		}
  		if(zooming){
  			listener.afterZoom(this);
  		}
  	}
  }

  public static String getSelectButtonSuffix(FocDesc focDesc, int fieldID){
  	String name = "";
  	if(focDesc != null){
  		FField fld = focDesc.getFieldByID(fieldID);
  		if(fld != null){
		  	name = fld.getName()+".BUTTON";
  		}
  	}
  	return name;
  }

  public static String getSelectButtonName(FocDesc focDesc, int fieldID){
  	String name = "";
  	if(focDesc != null){
  		FField fld    = focDesc.getFieldByID(fieldID);
  		String suffix = getSelectButtonSuffix(focDesc, fieldID);
  		if(fld != null && suffix != null && !suffix.isEmpty()){
		  	name = focDesc.getStorageName() +"."+suffix;
  		}
  	}
  	return name;
  }
}