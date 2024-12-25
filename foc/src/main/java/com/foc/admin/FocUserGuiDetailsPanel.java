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
package com.foc.admin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.foc.Application;
import com.foc.ConfigInfo;
import com.foc.FocKeys;
import com.foc.FocLangKeys;
import com.foc.Globals;
import com.foc.business.BusinessModule;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.business.workflow.WFSiteDesc;
import com.foc.desc.FocObject;
import com.foc.event.FValidationListener;
import com.foc.gui.FGButton;
import com.foc.gui.FGComboBox;
import com.foc.gui.FGObjectComboBox;
import com.foc.gui.FGPasswordField;
import com.foc.gui.FGTextField;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.gui.StaticComponent;
import com.foc.list.FocList;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

@SuppressWarnings("serial")
public class FocUserGuiDetailsPanel extends FPanel{
  
  private FGPasswordField confirmPass = null;
  private FGPasswordField compPass    = null;
  private FGPasswordField oldPass     = null;
  private FGTextField     nameComp    = null;

  private int             viewID      = FocObject.DEFAULT_VIEW_ID; 
  private FocUser         user        = null;

  private FGObjectComboBox siteComboBox  = null; 
  private FGObjectComboBox titleComboBox = null;
  
  private FPropertyListener companyListener = null;
  private FPropertyListener siteListener    = null;
  		
  public FocUserGuiDetailsPanel(FocObject object, int viewID){
  	this.viewID = viewID;
    user   = (FocUser)object;
    switch(viewID){
    case FocUser.LOGIN_VIEW_ID:
    case FocUser.SET_PASSWORD_VIEW_ID:
    case FocUser.CHANGE_PASSWORD_VIEW_ID:
      newDetailsPanelLoginAndPassword(viewID);
      break;      
    case FocUser.CHANGE_LANGUAGE_VIEW_ID:
    case FocUser.USER_GROUP_INFO_VIEW_ID:
      newDetailsPanelLanguage(viewID);      
      break;      
    case FocUser.VIEW_CHANGE_MULTI_COMPANY_FILTER:
    	newDetailsPanel_ChangeCompany(viewID);
    	break;
    }
  }
  
  public void dispose(){
  	if(user != null && companyListener != null){
  		user.getFocProperty(FocUserDesc.FLD_CURRENT_COMPANY).removeListener(companyListener);
  	}
  	if(user != null && siteListener != null){
  		user.getFocProperty(FocUserDesc.FLD_CURRENT_SITE).removeListener(siteListener);
  	}

    user          = null;
    confirmPass   = null;
    compPass      = null;
    oldPass       = null;
    nameComp      = null; 
    siteComboBox  = null;
    titleComboBox = null; 
  }
  
  public int getViewID(){
  	return viewID;
  }
  
  public boolean setLockComponent(boolean lock){
  	boolean wasLock = false;
  	if(nameComp != null){
  		wasLock = nameComp.isEnabled();
  		nameComp.setEnabled(!lock);
  		if(compPass != null){
  			compPass.setEnabled(!lock);
  		}
  	}  	
  	return wasLock;
  }
  
  public void newDetailsPanelLanguage(int viewID) {
    FPanel panel = new FPanel();
    if(viewID != FocUser.USER_GROUP_INFO_VIEW_ID){
      panel.setTitle(MultiLanguage.getString(FocLangKeys.ADMIN_USER_PREFERENCES));
    }
    int y = 0;
    
    FGTextField txtComp = (FGTextField) user.getGuiComponent(FocUserDesc.FLD_NAME);
    txtComp.setEnabled(false);
    panel.add(MultiLanguage.getString(FocLangKeys.ADMIN_NAME), txtComp, 0, y++);
    
    if(MultiLanguage.isMultiLanguage()){
      FGComboBox multiChoice = (FGComboBox) user.getGuiComponent(FocUserDesc.FLD_LANGUAGE);
      panel.add(MultiLanguage.getString(FocLangKeys.ADMIN_LANGUAGE), multiChoice, 0, y++);
      if(viewID == FocUser.USER_GROUP_INFO_VIEW_ID){
        multiChoice.setEnabled(false);
      }
    }

    if(BusinessModule.getInstance().isMultiCompany() && !Globals.getApp().getUser().isAdmin()){
    	JComponent comp = (JComponent) panel.add(user, FocUserDesc.FLD_CURRENT_COMPANY, 0, y++);
    	comp.setEnabled(false);
    	comp = (JComponent) panel.add(user, FocUserDesc.FLD_MULTI_COMPANY_MODE, 0, y++);
    	comp.setEnabled(false);
    	comp = (JComponent) panel.add(user, FocUserDesc.FLD_CURRENT_SITE, 0, y++);
    	comp.setEnabled(false);
    	comp = (JComponent) panel.add(user, FocUserDesc.FLD_CURRENT_TITLE, 0, y++);
    	comp.setEnabled(false);
    }
    
    Component comp = (Component) user.getGuiComponent(FocUserDesc.FLD_FONT_SIZE);
    panel.add(MultiLanguage.getString(FocLangKeys.ADMIN_TEXT_SIZE), comp, 0, y++);

    comp = (Component) user.getGuiComponent(FocUserDesc.FLD_ENABLE_TOOL_TIP_TEXT);
    panel.add(comp, 1, y++);

    comp = (Component) user.getGuiComponent(FocUserDesc.FLD_PRINT_UPON_SAVE);
    panel.add(comp, 1, y++);

    comp = (Component) user.getGuiComponent(FocUserDesc.FLD_TRANSACTION_SORTING_INCREMENTAL);
    panel.add(comp, 1, y++);

    if(user.getSendEmailCommandLine().isEmpty()){
    	user.setSendEmailCommandLine("C:\\Program Files (x86)\\Microsoft Office\\Office14\\Outlook.exe\" /c ipm.note /m");
    }
    
    FGTextField txtFld = (FGTextField) user.getGuiComponent(FocUserDesc.FLD_EMAIL_SEND_COMMAND_LINE);
    txtFld.setColumns(70);
    panel.add("Send email command line", txtFld, 0, y++);

    FValidationPanel validPanel = panel.showValidationPanel(true);
    user.getMasterObject();
    user.getFatherSubject();
    user.forceControler(true);
    validPanel.addSubject(user);
    
    validPanel.setValidationListener(new FValidationListener(){

      public boolean proceedValidation(FValidationPanel panel) {
        return true;
      }

      public boolean proceedCancelation(FValidationPanel panel) {
        return true;
      }

      public void postValidation(FValidationPanel panel) {
        Globals.getDisplayManager().setDefaultFontSize(Globals.getApp().getUser().getFontSize());
      }

      public void postCancelation(FValidationPanel panel) {
      }
      
    });

    if(viewID == FocUser.USER_GROUP_INFO_VIEW_ID){
      FocGroup group = user.getGroup();
      FPanel groupPanel = group.newDetailsPanel(FocGroup.VIEW_READ_ONLY);
      panel.add(groupPanel, 0, y++, 2, 1);
      groupPanel.setBorder(MultiLanguage.getString(FocLangKeys.ADMIN_GROUP));
      
      validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    }else{
      user.forceControler(true);
      validPanel.addSubject(user);
    }
    add(panel, 0, 0);
  }
  
  public void newDetailsPanelLoginAndPassword(int viewID) {
    //FPanel wrapperPanel = new FPanel("", FPanel.FILL_NONE);
    setOpaque(false);
    FPanel panel = null;
    String passwordLabel = "Password";
    int line = 0;
    
    //Creating panels and changing labels
    //-----------------------------------
    if(viewID == FocUser.LOGIN_VIEW_ID){
      //panel = new FLoginPanel();
      panel = new FPanel();
      panel.setOpaque(false);
      //panel.setTitle("Login");
    }else if(viewID == FocUser.SET_PASSWORD_VIEW_ID){
      panel = new FPanel();
      passwordLabel = "New password";
    }else if(viewID == FocUser.CHANGE_PASSWORD_VIEW_ID){
      panel = new FPanel();
      passwordLabel = "New password";
      panel.setTitle("Change password");
    }
    
    FGPasswordField passGuiSample = (FGPasswordField) user.getGuiComponent(FocUserDesc.FLD_PASSWORD);    
    nameComp = (FGTextField) user.getGuiComponent(FocUserDesc.FLD_NAME);
    nameComp.setEnabled(viewID == FocUser.LOGIN_VIEW_ID);
    nameComp.setColumns(30);
    //panel.add("Name", nameComp, 0, line++);
    
    float fontSize = 17f;
    JLabel label = panel.add("Name", nameComp, 0, line++);
    Font font = label.getFont();
    label.setFont(font.deriveFont(fontSize));
    if(viewID == FocUser.LOGIN_VIEW_ID){
    	label.setForeground(Color.WHITE);
    }
    
    //Creating panels and changing labels
    //-----------------------------------
    if(viewID == FocUser.CHANGE_PASSWORD_VIEW_ID){
      oldPass = clonePasswordField(passGuiSample);
      panel.add("Password", oldPass, 0, line++);
    }else{
      oldPass = null;
    }
    
    compPass = clonePasswordField(passGuiSample);
    compPass.setName("PASSWORD");
    //panel.add(passwordLabel, compPass, 0, line++);
    
    label = panel.add(passwordLabel, compPass, 0, line++);
    font = label.getFont();
    label.setFont(font.deriveFont(fontSize));
    label.setForeground(Color.WHITE); 
    	
    AbstractAction validateAction = null;
    AbstractAction exitAction = new AbstractAction(){
      public void actionPerformed(ActionEvent e){
        if(getViewID() == FocUser.SET_PASSWORD_VIEW_ID || getViewID() == FocUser.CHANGE_PASSWORD_VIEW_ID){
          Globals.getDisplayManager().goBack();
        }else{
          Globals.getApp().exit();
        }
      }
    };      
    
    if(viewID == FocUser.SET_PASSWORD_VIEW_ID || viewID == FocUser.CHANGE_PASSWORD_VIEW_ID){
      confirmPass = clonePasswordField(passGuiSample);
      panel.add("Confirm password", confirmPass, 0, line++);

      validateAction = new AbstractAction(){
        public void actionPerformed(ActionEvent e){
          String typedPassword = String.valueOf(compPass.getEncryptedPassword());
          String typedConfirmation = String.valueOf(confirmPass.getEncryptedPassword());
          boolean proceed = true;
          
          if(oldPass != null){
          	int status = FocUser.getLoginStatus_ForUsernamePassword(user.getName(), oldPass.getEncryptedPassword());
          	proceed = status != Application.LOGIN_WRONG;
            //proceed = String.valueOf(oldPass.getEncryptedPassword()).compareTo(user.getPassword()) == 0;
          }

          if(!proceed){
          	String message = "Wrong password please try again or refer to ADMIN to reset password.";
          	if(user.isAdmin()){
          		message = "Wrong admin password please try again to reset password.";
          	}
            JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                message,
                "01Barmaja",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null);
          }else{
            if(typedPassword.compareTo(typedConfirmation) == 0){
              FProperty prop = user.getFocProperty(FocUserDesc.FLD_PASSWORD);
              prop.setString(typedPassword);
              user.save();
              Globals.getDisplayManager().popupMessage("Password changed successfully.");
              Globals.getDisplayManager().goBack();
            }else{
              JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                  "Password confirmation error please try again.",
                  "01Barmaja",
                  JOptionPane.DEFAULT_OPTION,
                  JOptionPane.WARNING_MESSAGE,
                  null);
            }
          }
        }
      };
    }    
    
    if(viewID == FocUser.LOGIN_VIEW_ID){
      validateAction = new AbstractAction(){
        public void actionPerformed(ActionEvent e){
        	FLoginPanel.getInstance().lockUserNamePassword();
        	String typedName = String.valueOf(nameComp.getText());
          String encriptedPassword = compPass.getEncryptedPassword();
          Globals.logDetail("PASSWORD:<"+encriptedPassword+">");
          FocUser.userLoginCheck(typedName, encriptedPassword);
          if(Globals.getApp().getLoginStatus() != Application.LOGIN_WRONG){
            Globals.getDisplayManager().setDefaultFontSize(Globals.getApp().getUser().getFontSize());
          }
        }
      };
    }else{
      panel.add(user, FocUserDesc.FLD_SIGNATURE_IMAGE, 0, line++);
    }

    FPanel buttonsPanel = new FPanel();
    buttonsPanel.setFill(FPanel.FILL_NONE);
    FGButton bLogin = new FGButton("Validate");
    bLogin.addActionListener(validateAction);
    bLogin.setName(FValidationPanel.getFullValidationButtonName(""));
    if(ConfigInfo.isUnitDevMode()){
    	StaticComponent.setComponentToolTipText(bLogin, FValidationPanel.BUTTON_VALIDATE);  
    }
    
    FGButton bExit = new FGButton("Exit");
    bExit.addActionListener(exitAction);
    bExit.setName(FValidationPanel.BUTTON_CANCEL);
        
    buttonsPanel.add(bLogin, 0, 0);
    buttonsPanel.add(bExit, 1, 0);      
    
    panel.getActionMap().put("login", validateAction);
    panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(FocKeys.getValidateStroke(), "login");
    
    panel.getActionMap().put("exit", exitAction);
    panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(FocKeys.getCancelStroke(), "exit");
    
    panel.add(buttonsPanel, 0, line++, 2 ,1);

    /*
    if(viewID == DEFAULT_VIEW_ID){
      panel.showValidationPanel(true);
    }
    */
    setCurrentDefaultFocusComponent(nameComp);
    panel.setFill(FPanel.FILL_NONE);

    setBackground(new Color(100, 100, 255, 0));
    
    panel.setBackground(getBackground());
    
    /*FPanel environmentPanel = new FPanel("", FPanel.FILL_NONE);
    environmentPanel.setBackground(getBackground());
    String environment = Globals.getApp().getDefaultEnvironment();
  
    FGTextField currentEnv = new FGTextField();
    currentEnv.setEditable(false);
    currentEnv.setPreferredSize(new Dimension(170, 20));
    if(environment != null ){
      currentEnv.setText(environment.substring(GuiConfigInfo.ENVIRONMENT_PREFIX.length()));  
    }
    
    label = environmentPanel.add("Current Environment", currentEnv, 0, 0);
    font = label.getFont();
    label.setFont(font.deriveFont(fontSize));
    
    FGButton switchButton = new FGButton("Switch Environment");
    environmentPanel.add(switchButton, 1, 1);
    switchButton.setEnabled(environment != null);
    switchButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        ConfigInfoWizardPanel panel = new ConfigInfoWizardPanel(new GuiConfigInfo(), ConfigInfoWizardPanel.STATE_DIRECTORY);
        panel.setWithRestart(true);
        Globals.getDisplayManager().popupDialog(panel, "", true);    
      }
    });
    */
    //panel.add(environmentPanel, 1, line);
    add(panel, 0, 0);
    //add(environmentPanel, 0, 1);
  }

  public void newDetailsPanel_ChangeCompany(int viewID) {
    FPanel panel = new FPanel();
    panel.setTitle("Multi Company Modification");

    panel.add(user, FocUserDesc.FLD_CURRENT_COMPANY, 0, 0);
    panel.add(user, FocUserDesc.FLD_MULTI_COMPANY_MODE, 0, 1);
    siteComboBox  = (FGObjectComboBox) panel.add(user, FocUserDesc.FLD_CURRENT_SITE, 0, 2);
    titleComboBox = (FGObjectComboBox) panel.add(user, FocUserDesc.FLD_CURRENT_TITLE, 0, 3);
    
    FValidationPanel validPanel = panel.showValidationPanel(true);
    user.getMasterObject();
    user.getFatherSubject();
    user.forceControler(true);
    validPanel.addSubject(user);
    validPanel.setValidationType(FValidationPanel.VALIDATION_OK_CANCEL);
    validPanel.setValidationButtonLabel("Login");
    validPanel.setCancelationButtonLabel("Exit");
    
    validPanel.setValidationListener(new FValidationListener(){

      public boolean proceedValidation(FValidationPanel panel) {
        return true;//!FGOptionPane.popupOptionPane_YesNo("Exit notification", "This session will exited automatically for the changes to take effect.\nDo you want to proceed?");
      }

      public boolean proceedCancelation(FValidationPanel panel) {
        return true;
      }

      public void postValidation(FValidationPanel panel) {
      	if(Globals.getDisplayManager() != null && Globals.getDisplayManager().getMainFrame() != null){
      		Globals.getDisplayManager().getMainFrame().setWindowTitle();
      	}
      	//It is important to refresh this list because the where condiion of it is changed when we change the Company
      	//I we don't do this line, the supplier orders and other transactions will not load properly when we change the 
      	//Company the first time. We need to relog so they load.
      	WFSiteDesc.getList(FocList.FORCE_RELOAD);
				//Globals.getApp().exit(false);
      	Globals.getApp().getUserSession_Swing().setUser(user);
      }

      public void postCancelation(FValidationPanel panel) {
      	Globals.getApp().exit(false);
      }
    });

    add(panel, 0, 0);
    
    companyListener = new FPropertyListener(){
			@Override
			public void propertyModified(FProperty property) {
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
//USERREFACTOR						
//						user.dispose_SitesList();
						siteComboBox.refreshList();
//USERREFACTOR						
//						user.companyChanged();
					}
				});
			}

			@Override
			public void dispose() {
			}
    };
    user.getFocProperty(FocUserDesc.FLD_CURRENT_COMPANY).addListener(companyListener);
    
    siteListener    = new FPropertyListener(){
			@Override
			public void propertyModified(FProperty property) {
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
//USERREFACTOR						
//user.dispose_TitlesList();
						titleComboBox.refreshList();
//USERREFACTOR						
//						user.siteChanged();
					}
				});
			}

			@Override
			public void dispose() {
			}
    };
    user.getFocProperty(FocUserDesc.FLD_CURRENT_SITE).addListener(siteListener);
  }
  
  private FGPasswordField clonePasswordField(FGPasswordField refPass){
    FGPasswordField pass = new FGPasswordField();
    pass.setCapital(refPass.getCapital());
    pass.setColumns(refPass.getColumns());
    pass.setColumnsLimit(refPass.getColumnsLimit());
    return pass;
  }
}
