/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import com.foc.Globals;
import com.foc.desc.field.FBoolField;
import com.foc.event.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGDecisionPanel extends FPanel {

	private String   message            = null ;
	private String   validationLabel    = null ;
	private String   cancelLabel        = null ;
	
	private FBoolean booleanProp        = null ;
	private boolean  cancel             = false;
	private boolean  rememberMyDecision = false;
	
	public FGDecisionPanel(String message, String validationLabel, String cancelLabel){
		this.message         = message        ;
		this.validationLabel = validationLabel;
		this.cancelLabel     = cancelLabel    ;
		cancel = false;
		fill();
	}
	
	public void dispose(){
		if(booleanProp != null){
			booleanProp.dispose();
			booleanProp = null;
		}
	}
	
	public void fill(){
		add(new FGLabel(message), 0, 0, 2, 1);
		booleanProp = new FBoolean(null, 1, false);
		booleanProp.setFocField(new FBoolField("RIEN", "", 1, false));
		add(new FGLabel("Remember my decision"), 0, 1);
		add(booleanProp.getGuiComponent(), 1, 1);
		FValidationPanel vPanel = showValidationPanel(true);
		vPanel.setValidationButtonLabel(validationLabel);
		vPanel.setCancelationButtonLabel(cancelLabel);
		vPanel.setValidationListener(new FValidationListener() {
			@Override
			public boolean proceedValidation(FValidationPanel panel) {
				rememberMyDecision = booleanProp.getBoolean();
				cancel             = false;
				return true;
			}
			
			@Override
			public boolean proceedCancelation(FValidationPanel panel) {
				rememberMyDecision = booleanProp.getBoolean();
				cancel             = true;
				return true;
			}
			
			@Override
			public void postValidation(FValidationPanel panel) {
			}
			
			@Override
			public void postCancelation(FValidationPanel panel) {
			}
		});
	}

	public void popup(){
		Globals.getDisplayManager().popupDialog(this, "Decision panel", true);
	}
	
	public boolean isRememberMyDecision() {
		return rememberMyDecision;
	}

	public void setRememberMyDecision(boolean rememberMyDecision) {
		this.rememberMyDecision = rememberMyDecision;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}
}