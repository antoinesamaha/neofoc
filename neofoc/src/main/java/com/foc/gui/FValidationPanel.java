/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import javax.swing.*;

import com.foc.*;
import com.foc.access.*;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.business.printing.gui.PrintingAction;
import com.foc.business.workflow.implementation.IWorkflow;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.event.*;
import com.foc.list.*;

import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.util.Iterator;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FValidationPanel extends FPanel implements FIFooterPanel {

	private AccessConsole accessConsole = null;
  private FocList listWithSelection = null;

  private FGButtonAction validationAction = null;
  private FGButtonAction cancelationAction = null;

  private FGButton validationButton  = null;
  private FGButton cancelationButton = null;
  
  private boolean cancelled            = false;
  private int     validationType       = VALIDATION_SAVE_CANCEL;
  private int     selectionType        = SELECTION_DISABLED;

  private boolean popupConfirmationDialog = true;
  private boolean validateWhenGetFocus = false;
  
  public static final int VALIDATION_SAVE_CANCEL = 1;
  public static final int VALIDATION_OK_CANCEL   = 2;
  public static final int VALIDATION_OK          = 3;

  public static final int SELECTION_ENABLED = 1;
  public static final int SELECTION_DISABLED = 2;
  
  public static final String BUTTON_VALIDATE           = "VALIDATE";
  public static final String BUTTON_CANCEL             = "CANCEL"  ;
  public static final String BUTTON_SAVE_WITHOUT_EXIST = "SAVE"    ;
  
  private FValidationListener validationListener = null;
  private FPanel              internalPanel      = null;
  
  private int nextGridX = 5;//Minimum 3 because we have the Save & Exit, Save, Cancel.
  
  private PrintingAction printingAction = null;
  
  public void init() {
    init(true);
  }
  
  public void init(boolean normalButtonsPositioning) {
    accessConsole = new AccessConsole();
    internalPanel = new FPanel();
    internalPanel.setFill(FPanel.FILL_NONE);
    setButtonsAccordingToFlags(normalButtonsPositioning);
    add(internalPanel, 0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE);
    nextGridX = 5;
  }

  /**
   * 
   */
  public FValidationPanel() {
    this(true);
  }
  
  public FValidationPanel(boolean normalButtonsPositioning) {
    this(normalButtonsPositioning, null);
  }
  
  FValidationPanel(boolean normalButtonsPositioning, String frameName) {
    super();
    setName(getFullName(frameName));
    init(normalButtonsPositioning);
  }
  
  public void dispose(){
    super.dispose();

    accessConsole = null;
    listWithSelection = null;

    validationAction = null;
    cancelationAction = null;

    validationButton = null;
    cancelationButton = null;

    validationListener = null;
    
    if(internalPanel != null){
      internalPanel.dispose();
      internalPanel = null;
    }
    
    if(printingAction != null){
    	printingAction.dispose();
    	printingAction = null;
    }
  }
  
  public static String getFullName(String name){
  	return name != null ? name+".FVALIDATIONPANEL" : ".FVALIDATIONPANEL";
  }
  
  public void addButton(JComponent button){
    internalPanel.add(button, nextGridX++, 0); 
  }

  public boolean doValidateWithoutGoBack(){
  	boolean error = true;
    FValidationListener validListener = getValidationListener();
    if(validListener == null || validListener.proceedValidation(this)){
    	boolean isValidated = accesConsoleValidate();
      if(isValidated){
        if (selectionType == FValidationPanel.SELECTION_ENABLED) {
          if (listWithSelection != null) {
            listWithSelection.validateSelectedObject();
          }
        }
        if(validListener != null){
          validListener.postValidation(this);
        }
        if(Globals.getApp().getUser() != null && Globals.getApp().getUser().isPrintUponSave()){
        	Iterator<AccessSubject> iter = accessConsole.newSubjectIterator();
        	while(iter != null && iter.hasNext()){
        		AccessSubject sub = iter.next();
        		if(sub != null && sub instanceof IWorkflow){
        			FocObject obj = (FocObject) sub;
        			if(obj.getThisFocDesc() != null && printingAction != null){
        				popupPrintingPanel();
        			}
        		}
        	}
        }
        error = false;
      }
    }
    return error;
  }

  public void doValidate(){
  	if(!doValidateWithoutGoBack()){
  		Globals.getDisplayManager().goBack();
  	}
  }
  
  protected boolean accesConsoleValidate(){
  	return accessConsole != null ? accessConsole.validate(true, true) : true;
  }
  
  public void cancel(){
    //Globals.getDisplayManager().stopEditingCurrentEdit();
    
    FValidationListener validListener = getValidationListener();
    if(validListener == null || validListener.proceedCancelation(this)){
    	setCancelled(true);
      accessConsole.cancel();
      fireEvent(FocEvent.ID_RESTORE);      
      if (selectionType == FValidationPanel.SELECTION_ENABLED) {
        if (listWithSelection != null) {
          listWithSelection.cancellingSelection();
        }
      }
      Globals.getDisplayManager().goBack();      
      if(validListener != null){
        validListener.postCancelation(this);
      }
    }
  }
  
  public void cancelAction() {
    cancel();
  }
  
  public void setValidationButtonLabel(String valid) {
    if (valid != null && valid.compareTo("") != 0) {
      validationButton.setText(valid);
    }
  }

  public void setCancelationButtonLabel(String cancel) {
    if (cancel != null && cancel.compareTo("") != 0) {
      cancelationButton.setText(cancel);
    }
  }

  public void fireEvent(int id) {
    FocEvent focEvent = new FocEvent(this, FocEvent.composeId(FocEvent.TYPE_ORDER, id), null);
    notifyListeners(focEvent);
  }

  public Iterator newSubjectIterator() {
    Iterator iter = null;
    if (accessConsole != null) {
      iter = accessConsole.newSubjectIterator();
    }    
    return iter;
  }

  public void removeSubject(AccessSubject subject) {
    if (accessConsole != null) {
      accessConsole.removeSubject(subject);
    }    
  }
  
  public void addSubject(AccessSubject subject) {
    if(accessConsole != null){
      accessConsole.addSubject(subject);

      boolean oneSaveCancel = false;
      boolean oneOkCancel = false;
      boolean oneDirectDatabaseLink = false;
      boolean oneSelection = false;
      
      String buttonNamePrefix = null;

      Iterator iter = accessConsole.newSubjectIterator();
      while(iter != null && iter.hasNext()){
        AccessSubject currSubject = (AccessSubject) iter.next();

        if(currSubject != null){
          boolean currSaveCancel = false;
          boolean currOkCancel = false;
          boolean currDirectDatabaseLink = false;
          boolean currSelection = false;

          if(currSubject.isControler()){
            currSaveCancel = true;
          } else {
            currOkCancel = true;
          }

          if(currSubject instanceof FocList){
            FocList list = (FocList) currSubject;

            currSelection = list.getSelectionProperty() != null;
            if (currSelection) listWithSelection = list;
            currDirectDatabaseLink = list.isDirectImpactOnDatabase();
          }

          if(currDirectDatabaseLink){
            currSaveCancel = false;
            currOkCancel = false;
          }

          oneSaveCancel = oneSaveCancel || currSaveCancel;
          oneOkCancel = oneOkCancel || currOkCancel;
          oneDirectDatabaseLink = oneDirectDatabaseLink || currDirectDatabaseLink;
          oneSelection = oneSelection || currSelection;
        }
        
        if(buttonNamePrefix == null){
	        if(currSubject instanceof FocObject){
	        	buttonNamePrefix = ((FocObject)currSubject).getThisFocDesc().getStorageName();
	        }else if(currSubject instanceof FocList){
	        	buttonNamePrefix = ((FocList)currSubject).getFocDesc().getStorageName();
	        }
        }else{
        	buttonNamePrefix = "";
        }
      }
      
      if(buttonNamePrefix == null) buttonNamePrefix = "";
      
      if(validationButton  != null) validationButton.setName(getFullValidationButtonName(buttonNamePrefix)); 
      if(cancelationButton != null) cancelationButton.setName(getFullCancellationButtonName(buttonNamePrefix));
      
      if(oneSaveCancel){
        this.setValidationType(VALIDATION_SAVE_CANCEL);
      }else if (oneOkCancel){
        this.setValidationType(VALIDATION_OK_CANCEL);
      }else if (oneDirectDatabaseLink){
        this.setValidationType(VALIDATION_OK);
      }

      if(oneSelection){
        this.setSelectionType(SELECTION_ENABLED);
      }else{
        this.setSelectionType(SELECTION_DISABLED);
      }
    }
    
    subject.doBackupWithPropagation();
  }

  /**
   * @return Returns the selectionType.
   */
  public int getSelectionType() {
    return selectionType;
  }

  /**
   * @param selectionType
   *          The selectionType to set.
   */
  public void setSelectionType(int selectionType) {
    this.selectionType = selectionType;
    setButtonsAccordingToFlags();
  }

  /**
   * @return Returns the validationType.
   */
  public int getValidationType() {
    return validationType;
  }

  /**
   * @param validationType
   *          The validationType to set.
   */
  public void setValidationType(int validationType) {
    this.validationType = validationType;
    setButtonsAccordingToFlags();
  }
  
  public void setValidationType(int validationType,boolean normalButtonsPositioning){
    this.validationType = validationType;
    setButtonsAccordingToFlags(normalButtonsPositioning);
  }

  private void treatGuiCancelationEvent(){
    try {
      int dialogRet = JOptionPane.NO_OPTION;
      
      if(isAskForConfirmationForExit()){
        dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
            "Do you want to confirm the changes you made?",
            "01Barmaja - Warning",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null);
      }
      
      switch(dialogRet){
      case JOptionPane.YES_OPTION:
        doValidate();
        break;
      case JOptionPane.NO_OPTION:
        Globals.getDisplayManager().removeLockFocusForObject(null);
        cancel();
        
        break;
      case JOptionPane.CANCEL_OPTION:
        break;
      }
    } catch (Exception e1) {
      Globals.logException(e1);
    }
  }  
  
  private void setButtonsAccordingToFlags() {
    setButtonsAccordingToFlags(true);
  }
  
  private void setButtonsAccordingToFlags(boolean normalButtonsPositioning) {
    
  	int saveButtonX        = 0;
    int validationButtonX  = 1;
    int cancelationButtonX = 2;
    
    if(!normalButtonsPositioning){
    	saveButtonX        = 2;
      validationButtonX  = 1;
      cancelationButtonX = 0;
    }
    boolean validButtonVisible = true;// The validation is always visible

    if (validButtonVisible) {// If visible we need to set it

      // We start by choosing the label
      String validationLabel = MultiLanguage.getString(FocLangKeys.COMMAND_SAVE);
      int validationMnemonic = MultiLanguage.getMnemonic(FocLangKeys.COMMAND_SAVE);
      String validationToolTipText = MultiLanguage.getToolTipText(FocLangKeys.COMMAND_SAVE);;      
      if (selectionType == SELECTION_ENABLED) {
        validationLabel = MultiLanguage.getString(FocLangKeys.COMMAND_SELECT);
        validationMnemonic = MultiLanguage.getMnemonic(FocLangKeys.COMMAND_SELECT);
        validationToolTipText = MultiLanguage.getToolTipText(FocLangKeys.COMMAND_SELECT);;
      } else if (validationType == VALIDATION_OK || validationType == VALIDATION_OK_CANCEL) {
        validationLabel = MultiLanguage.getString(FocLangKeys.COMMAND_OK);
        validationMnemonic = MultiLanguage.getMnemonic(FocLangKeys.COMMAND_OK);
        validationToolTipText = MultiLanguage.getToolTipText(FocLangKeys.COMMAND_OK);;
      }

      // If it is null we create it or else we change the label
      if (validationButton == null) {
        validationButton = new FGButton(validationLabel);
        validationButton.setMnemonic(validationMnemonic);
        
        validationButton.setName(getName()+"."+BUTTON_VALIDATE);
        validationButton.setName(getFullValidationButtonName("")); 
        internalPanel.add(validationButton, validationButtonX, 0);
        // We set the action
        validationAction = new FGButtonAction(validationButton) {
          public void focActionPerformed(ActionEvent e) {
            try {
              boolean b = validationButton.requestFocusInWindow();
              
              if(!Globals.getDisplayManager().shouldLockFocus(true)){
                if(!validationButton.hasFocus() && b){
                  setValidateWhenGetFocus(true); 
                }else{
                  doValidate();
                }
              }
            } catch (Exception e1) {
              Globals.logException(e1);
            }
          }
        };
        //to be removed
        //validationButton.setEnabled(false);
        //to be removed
        validationButton.addActionListener(validationAction);
        validationAction.setAssociatedButton(validationButton);
        validationButton.addFocusListener(new FocusListener(){
          public void focusGained(FocusEvent e) {
            if(isValidateWhenGetFocus()){
              setValidateWhenGetFocus(false);
              doValidate();
            }
          }
          public void focusLost(FocusEvent e) {
          }
        });
      } else {
        setValidationButtonLabel(validationLabel);
        if(ConfigInfo.isUnitDevMode()){
        	StaticComponent.setComponentToolTipText(validationButton, validationButton.getName());
        }else{
        	StaticComponent.setComponentToolTipText(validationButton, validationToolTipText);  
        }
      }
      // Adding listeners and setting key strokes
      getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK), "save");
      getActionMap().put("save", validationAction);
    } else {
      getInputMap(WHEN_IN_FOCUSED_WINDOW).remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK));
      getActionMap().remove("save");
    }

    // We set the visibility mode
    if (validationButton != null) {
      validationButton.setVisible(validButtonVisible);
      //validationButton.setVisible(false);
    }

    // --------------------------------------
    // Cancelation
    // --------------------------------------

    boolean cancelButtonVisible = validationType != VALIDATION_OK || selectionType == SELECTION_ENABLED;// The
                                                                  // validation
                                                                  // is allways
                                                                  // visible

    if (cancelButtonVisible) {// If visible we need to set it

      // We start by choosing the label
      String cancelationLabel = MultiLanguage.getString(FocLangKeys.COMMAND_CANCEL);
      int cancelationMnemonic = MultiLanguage.getMnemonic(FocLangKeys.COMMAND_CANCEL);
      String cancelationTTT = MultiLanguage.getToolTipText(FocLangKeys.COMMAND_CANCEL);

      // If it is null we create it or else we change the label
      if (cancelationButton == null) {
        cancelationButton = new FGButton(cancelationLabel);
        cancelationButton.setMnemonic(cancelationMnemonic);
        cancelationButton.setName(getFullCancellationButtonName(""));
        internalPanel.add(cancelationButton, cancelationButtonX, 0);
        // We set the action
        cancelationAction = new FGButtonAction(cancelationButton) {
          public void focActionPerformed(ActionEvent e) {
            treatGuiCancelationEvent();
          }
        };
        cancelationButton.addActionListener(cancelationAction);
        cancelationAction.setAssociatedButton(cancelationButton);
      } else {
        setCancelationButtonLabel(cancelationLabel);
        if(ConfigInfo.isUnitDevMode()){
          //cancelationButton.setToolTipText(getName()+"."+BUTTON_CANCEL);
        	StaticComponent.setComponentToolTipText(cancelationButton, cancelationButton.getName());
        }else{
        	StaticComponent.setComponentToolTipText(cancelationButton, cancelationTTT);  
        }
      }
    }

    // We set the visibility mode
    if (cancelationButton != null) {
      cancelationButton.setVisible(cancelButtonVisible);
    }
    
    this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "goBack");
    this.getActionMap().put("goBack", validationType == VALIDATION_OK ? validationAction : cancelationAction);
    
    addPrintButtonIfApplicable();
  }
  
  public FValidationListener getValidationListener() {
    return validationListener;
  }
  
  public void setValidationListener(FValidationListener validationListener) {
    this.validationListener = validationListener;
  }
  
  public boolean isAskForConfirmationForExit() {
    return popupConfirmationDialog && accessConsole.needValidationWithPropagation();
  }
  
  public boolean isValidateWhenGetFocus() {
    return validateWhenGetFocus;
  }
  
  public void setValidateWhenGetFocus(boolean validateWhenGetFocus) {
    this.validateWhenGetFocus = validateWhenGetFocus;
  }
  
  public void setAskForConfirmationForExit(boolean askForConfirmationForExit) {
    this.popupConfirmationDialog = askForConfirmationForExit;
  }
  
  public FGButton getValidationButton(){
    return validationButton;
  }
  
  public FGButton getCancelationButton(){
    return cancelationButton;
  }
  
  public void disabelButtons(){
    if (validationButton != null){
      validationButton.setEnabled(false);
    }
    if (cancelationButton != null){
      cancelationButton.setEnabled(false);
    }
  }
  
  public void enabelButtons(){
    if (validationButton != null){
      validationButton.setEnabled(true);
    }
    if (cancelationButton != null){
      cancelationButton.setEnabled(true);
    }
  }
  
  public static String getFullValidationButtonName(String prefix){
  	return prefix+"."+BUTTON_VALIDATE;
  }
  
  public static String getFullCancellationButtonName(String prefix){
  	return prefix+"."+BUTTON_CANCEL;
  }
  
  public FocObject getObjectToPrint(){
  	FocObject objectToPrint = null;
  	if(accessConsole != null){
  		Iterator iter = accessConsole.newSubjectIterator();
  		while(iter != null && iter.hasNext()){
  			AccessSubject subject = (AccessSubject) iter.next();
  			if(subject instanceof FocObject){
  				if(objectToPrint != null){
  					objectToPrint = null;
  					break;
  				}else{
  					objectToPrint = (FocObject) subject;
  				}
  			}
  		}
  	}
  	return objectToPrint;
  }
  
  public void addPrintButtonIfApplicable(){
  	if(getObjectToPrint() != null){
  		FocDesc focDesc = getObjectToPrint().getThisFocDesc();
  		printingAction = focDesc != null ? focDesc.newPrintingAction() : null;
  		if(printingAction != null){
  			printingAction.initLauncher();
	  		FGButton printButton = new FGButton(Globals.getIcons().getPrintIcon());
	  		printButton.addActionListener(new ActionListener() {
	  			@Override
					public void actionPerformed(ActionEvent e) {
	  				popupPrintingPanel();
	  				/*
	  				PrintingAction action = getObjectToPrint().getThisFocDesc().getPrintingAction();
	  				action.setObjectToPrint_Owner(false);
	  				action.setObjectToPrint(getObjectToPrint());
	  				action.popupPrintingPanel();
	  				*/
	  			}
				});
		    internalPanel.add(printButton, 10, 0);
  		}
		}
  }

  public void popupPrintingPanel(){
  	if(printingAction != null){
//			PrintingAction action = getObjectToPrint().getThisFocDesc().getPrintingAction();
  		printingAction.setObjectToPrint_Owner(false);
  		printingAction.setObjectToPrint(getObjectToPrint());
  		printingAction.popupPrintingPanel();
  	}
  }
  
  public void addSaveOnlyButton(){
		FGButton saveButton = new FGButton(Globals.getIcons().getSaveIcon());
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Globals.setMouseComputing(true);
				doValidateWithoutGoBack();
				Globals.setMouseComputing(false);
				/*
				PrintingAction action = getObjectToPrint().getThisFocDesc().getPrintingAction();
				action.setObjectToPrint_Owner(false);
				action.setObjectToPrint(getObjectToPrint());
				action.popupPrintingPanel();
				*/
			}
		});
    internalPanel.add(saveButton, 9, 0);
  }

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}