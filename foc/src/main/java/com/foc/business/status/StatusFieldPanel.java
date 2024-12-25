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
package com.foc.business.status;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.foc.Globals;
import com.foc.business.status.StatusHolderDesc;
import com.foc.business.workflow.implementation.FGWorkflowButtonPanel;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.desc.FocObject;
import com.foc.gui.FGButton;
import com.foc.gui.FGOptionPane;
import com.foc.gui.FPanel;
import com.foc.gui.StaticComponent;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class StatusFieldPanel extends FPanel {
  
  private FocObject      focObject     = null;
  private StatusListener listener      = null;

  private FGButton       closeButton   = null;
  private FGButton       approveButton = null;
  private FGButton       cancelButton  = null;
  
  public static final String ICON_STATUS_APPROVE = "status_approve.gif";
  public static final String ICON_STATUS_CLOSE   = "status_close.gif";
  public static final String ICON_STATUS_CANCEL  = "status_cancel.gif";
  
  public StatusFieldPanel(FocObject focObj, int view, boolean editable, boolean withSignatureButton, FPanel mainPanel, int x, int y){
  	this(focObj, view, editable, withSignatureButton);
  	setInsets(0, 0, 0, 0);
  	mainPanel.addLabel("Status", x, y);
  	mainPanel.addField(this, x+1, y, GridBagConstraints.NONE);
  }
  
	public StatusFieldPanel(FocObject focObj, int view, boolean editable, boolean withSignatureButton){
		super("", FPanel.FILL_NONE);

		setInsets(0, 0, 0, 0);
		
    this.focObject = focObj;
    Component comp = null;

    boolean allowClosing = (focObj instanceof IWorkflow);
    if(allowClosing){
    	allowClosing = focObj.workflow_IsAllowClose();
    }
    if(allowClosing){
	    closeButton = new FGButton(Globals.getIcons().getFocIcon(ICON_STATUS_CLOSE));
	    StaticComponent.setComponentToolTipText(closeButton, "Close");
	    closeButton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e) {
	      	if(!FGOptionPane.popupOptionPane_YesNo("Confirm", "Close this transaction?")){
		      	getStatusHolder().setStatus(StatusHolderDesc.STATUS_CLOSED);
		      	getStatusHolder().setClosureDate(Globals.getDBManager().getCurrentTimeStamp_AsTime());
	      	}
	      }
	    });
    }

    boolean allowApprove = (focObj instanceof IWorkflow);
    if(allowApprove){
    	allowApprove = focObj.workflow_IsAllowApprove();
    }
    if(allowApprove){
	    approveButton = new FGButton(Globals.getIcons().getFocIcon(ICON_STATUS_APPROVE));
	    approveButton.setName(getButtonApproveName());
	    StaticComponent.setComponentToolTipText(approveButton, "Approve");
	    approveButton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e) {
	      	if(!FGOptionPane.popupOptionPane_YesNo("Confirm", "Approve this transaction?")){
		      	getStatusHolder().setStatusToValidated();
		      	getFocObject().validate(true);
	      	}
	      }
	    });
    }

    boolean allowCancel = (focObj instanceof IWorkflow);
    if(allowCancel){
    	allowCancel = focObj.workflow_IsAllowCancel();
    }
    if(allowCancel){
    	cancelButton = new FGButton(Globals.getIcons().getFocIcon(ICON_STATUS_CANCEL));
    	StaticComponent.setComponentToolTipText(cancelButton, "Cancel");
    	cancelButton.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e) {
	      	if(!FGOptionPane.popupOptionPane_YesNo("Confirm", "Cancel this transaction?")){
		      	getStatusHolder().setStatusToCanceled();
		      	getFocObject().validate(true);
	      	}
	      }
	    });
    }
        
    comp = addField(focObject, getIStatusHolderDesc().getFLD_STATUS(), 0, 0);
//    if(Globals.getApp().getGroup().allowStatusManualModif()){
//    	comp.setEnabled(true);
//    }else{
//    	comp.setEnabled(false);
//    }
    if(closeButton   != null) add(closeButton  , 3, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
    if(approveButton != null) add(approveButton, 2, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
    if(cancelButton  != null) add(cancelButton , 4, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
    
    if(withSignatureButton && focObject instanceof IWorkflow){
    	FGWorkflowButtonPanel signaturePanel = new FGWorkflowButtonPanel(focObject);
    	add(signaturePanel, 5, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
    }

    listener = new StatusListener(this);
   	focObject.getFocProperty(getIStatusHolderDesc().getFLD_STATUS()).addListener(listener);
    refreshStatusButtonsVisibility();
    
    if(!editable){
      //StaticComponent.setAllComponentsEnable(this, false, true);
    }
  }
  
  public void dispose(){
    super.dispose();
    if(listener != null){
    	if(focObject != null && focObject.getFocProperty(getIStatusHolderDesc().getFLD_STATUS()) != null){
    		focObject.getFocProperty(getIStatusHolderDesc().getFLD_STATUS()).removeListener(listener);
    	}
    }
    listener       = null;
    focObject      = null;
    
    closeButton    = null;
    approveButton  = null;
    cancelButton   = null;
  }
  
  public void refreshStatusButtonsVisibility(){
    if(focObject != null){
    	if(getStatusHolder().getStatus() == StatusHolderDesc.STATUS_NONE || getStatusHolder().getStatus() == StatusHolderDesc.STATUS_SYSTEM){
        if(closeButton   != null) closeButton.setVisible(false);
        if(approveButton != null) approveButton.setVisible(false);
        if(cancelButton  != null) cancelButton.setVisible(false);  
    	}else if(getStatusHolder().getStatus() == StatusHolderDesc.STATUS_PROPOSAL){
        if(closeButton   != null) closeButton.setVisible(false);
        if(approveButton != null) approveButton.setVisible(true);
        if(cancelButton  != null) cancelButton.setVisible(true);  
      
        setVisible(false);
        setVisible(true);  
      }else if(getStatusHolder().getStatus() == StatusHolderDesc.STATUS_APPROVED){
      	if(closeButton   != null) closeButton.setVisible(true);
      	if(approveButton != null) approveButton.setVisible(false);
      	if(cancelButton  != null) cancelButton.setVisible(true);
        
        setVisible(false);
        setVisible(true);
      }else if(getStatusHolder().getStatus() == StatusHolderDesc.STATUS_CANCELED || getStatusHolder().getStatus() == StatusHolderDesc.STATUS_CLOSED){
        if(closeButton   != null) closeButton.setVisible(false);
        if(approveButton != null) approveButton.setVisible(false);
        if(cancelButton  != null) cancelButton.setVisible(false);  
      }
    }
  }
  
  public class StatusListener implements FPropertyListener {
  	
  	StatusFieldPanel panel = null;
  	
  	public StatusListener(StatusFieldPanel panel){
  		this.panel = panel;
  	}
  	
    public void dispose() {
    	panel = null;
    }

    public void propertyModified(FProperty property) {
    	panel.refreshStatusButtonsVisibility();
    }
  }
  
  public IStatusHolderDesc getIStatusHolderDesc(){
  	return (IStatusHolderDesc)focObject.getThisFocDesc();
  }

  public IStatusHolder getIStatusHolder(){
  	return (IStatusHolder)focObject;
  }

  public StatusHolder getStatusHolder(){
  	return getIStatusHolder().getStatusHolder();
  }
  
  public FocObject getFocObject(){
  	return focObject;
  }
  
  public String getButtonApproveName(){
  	String prefix = (getFocObject() != null && getFocObject().getThisFocDesc() != null) ? getFocObject().getThisFocDesc().getStorageName() : "";
  	return prefix+"."+getButtonApproveName_Suffix();  
  }
  
  public static String getButtonApproveName_Suffix(){
  	return "BUTTON.STATUS_APPROVE";
  }
}
