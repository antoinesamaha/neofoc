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
package com.foc;

import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import com.foc.gui.FPanelSequence;
import com.foc.gui.FWizardFooterPanel;
import com.foc.gui.FWizardPanel;

@SuppressWarnings("serial")
public class ConfigInfoWizardPanel extends FWizardPanel {

  private int       STATE_START             = 0;
  public static int STATE_DIRECTORY         = 1;
  public static int STATE_ENV_DECISION      = 2;
  public static int STATE_NEW_ENV           = 3;
  public static int STATE_EXISTING_ENV      = 4;
  public static int STATE_CONFIGINFO        = 5;
  public static int STATE_FINISH            = 6;
  
  private int currentState = STATE_START;
  private ArrayList<Integer> goBackPath = null;
  private boolean withRestart = false;
  
  private FPanelSequence panelSeq = null;
  private GuiConfigInfo guiConfigInfo = null;
  
  private String defaultEnvironment_backup = null;
  private String defaultAppDirectory_backup = null;
  
  public ConfigInfoWizardPanel(GuiConfigInfo guiConfigInfo, int state){
    this.guiConfigInfo = guiConfigInfo;
    panelSeq = new FPanelSequence();
    goBackPath = new ArrayList<Integer>();
    STATE_START = state;
    setState(state);
    add(panelSeq.getCenterPanel());
    //panelSeq.getCenterPanel().setPreferredSize(new Dimension( 330, 230));
    panelSeq.getCenterPanel().setPreferredSize(new Dimension( 550, 300));
    addWizardButtons();
    getCancelButton().setVisible(true);
    getPreviousButton().setVisible(false);
    
    defaultAppDirectory_backup = Globals.getApp().getDefaultAppDirectory();
    defaultEnvironment_backup = Globals.getApp().getDefaultEnvironment();
  }
  
  @Override
  public boolean nextAction() {
    
    stateChanged = false;
    guiConfigInfo.saveStatePanelVariables(currentState);
    if( currentState == STATE_DIRECTORY ){
      if( guiConfigInfo.getConfigWizardPanelVariables().isDirectoryPathEmpty() ){
        JOptionPane.showMessageDialog(null, "You did not specify a directory path", "Directory path missing", JOptionPane.ERROR_MESSAGE);
      }else{
        File dir = new File(guiConfigInfo.getConfigWizardPanelVariables().getDirectoryPath());
        /*boolean proceed = false;
        if( !dir.exists()){
          proceed = dir.mkdirs();
        }else{
          proceed = true;
        }*/
        if( dir.exists() ){
          if( guiConfigInfo.getConfigWizardPanelVariables().isExistingEnv() ){
            String directoryPathStringVal = guiConfigInfo.getConfigWizardPanelVariables().getDirectoryPath();
            String environment = guiConfigInfo.getConfigWizardPanelVariables().getExistingEnvironment();
            
            if( environment.equals(GuiConfigInfo.ENVIRONMENT_PREFIX)){
              JOptionPane.showMessageDialog(null, "No existing environments", "Environment missing", JOptionPane.ERROR_MESSAGE);
            }else{
              File file = new File(directoryPathStringVal+"/"+environment+"/properties/config.properties");
              if( file.exists()){
                Globals.getApp().setDefaultAppDirectory(directoryPathStringVal);
                Globals.getApp().setDefaultEnvironment(environment);
                
                if( withRestart ){
                  doRestartProcedure();
                }
                Globals.getDisplayManager().goBack();
                
              }else{
                int choice = JOptionPane.showConfirmDialog(null, "Do you want to configure the Config file ?", "Config File Not Found", JOptionPane.YES_NO_OPTION);
                if( choice == JOptionPane.YES_OPTION){
                  setState(STATE_CONFIGINFO);
                  getNextButton().setText(FWizardFooterPanel.BUTTON_LABEL_FINISH);
                  
                }
              }
            }
          }else{
            if (guiConfigInfo.getConfigWizardPanelVariables().getNewEnvironment().equals(GuiConfigInfo.ENVIRONMENT_PREFIX) ){
              JOptionPane.showMessageDialog(null, "You did not specify an environment", "Environment missing", JOptionPane.ERROR_MESSAGE);
            }else {
              File file = new File(guiConfigInfo.getConfigWizardPanelVariables().getDirectoryPath()+"/"+guiConfigInfo.getConfigWizardPanelVariables().getNewEnvironment());
              if( file.exists()){
                JOptionPane.showMessageDialog(null, "Please choose a new environment or go back to select an existing environment", "Environment already exists", JOptionPane.ERROR_MESSAGE);
              }else{
                setState(STATE_CONFIGINFO);
                getNextButton().setText(FWizardFooterPanel.BUTTON_LABEL_FINISH);  
              }
            }
          }
        }else{
          JOptionPane.showMessageDialog(null, "The directory chosen does not exist.", "Directory path missing", JOptionPane.ERROR_MESSAGE);
        }
      }
    }else if( currentState == STATE_ENV_DECISION ){
      if( guiConfigInfo.getConfigWizardPanelVariables().isExistingEnv() ){
        setState(STATE_EXISTING_ENV);
      }else{
        setState(STATE_NEW_ENV);
      }
    }else if( currentState == STATE_EXISTING_ENV ){
      
      
      
    }else if( currentState == STATE_NEW_ENV ){
      
      
      
    }else if( currentState == STATE_CONFIGINFO ){
      
      if( guiConfigInfo.getConfigWizardPanelVariables().isConfigFieldsEmpty() ){
        JOptionPane.showMessageDialog(null, "One or more fields are empty.", "Empty Field", JOptionPane.ERROR_MESSAGE);
      }else{
        try {
          String directoryPathStringVal = guiConfigInfo.getConfigWizardPanelVariables().getDirectoryPath();
          boolean isExistingEnvironment = guiConfigInfo.getConfigWizardPanelVariables().isExistingEnv();
          String environment = isExistingEnvironment ? guiConfigInfo.getConfigWizardPanelVariables().getExistingEnvironment() : guiConfigInfo.getConfigWizardPanelVariables().getNewEnvironment();
          
          if( !isExistingEnvironment ){
            File dir = new File(directoryPathStringVal+"/"+environment);
            if( !dir.exists() ){
              dir.mkdir();
            }
          }
          
          File dir = new File(directoryPathStringVal+"/"+environment+"/properties");
          if( !dir.exists() ){
            dir.mkdir();
          }
          
          String localhostValue = guiConfigInfo.getConfigWizardPanelVariables().getLocalhost();
          String portValue = guiConfigInfo.getConfigWizardPanelVariables().getPort();
          String schemaValue = guiConfigInfo.getConfigWizardPanelVariables().getSchema();
          String usernameValue = guiConfigInfo.getConfigWizardPanelVariables().getUsername();
          String passwordValue = guiConfigInfo.getConfigWizardPanelVariables().getPassword();
          
          
          FileWriter fileWriter = new FileWriter(directoryPathStringVal+"/"+environment+"/properties/config.properties");
          PrintWriter out = new PrintWriter(fileWriter, true);
          out.println("jdbc.drivers=com.mysql.jdbc.Driver");
          out.println("jdbc.url=jdbc:mysql://"+localhostValue+":"+portValue+"/"+schemaValue);
          out.println("jdbc.username="+usernameValue);
          out.println("jdbc.password="+passwordValue);
          
          out.println("gui.windowTitle="+ConfigInfo.getWindowTitle());
          out.println("gui.font.size="+ConfigInfo.getFontSize());
          out.println("unitDevMode="+(ConfigInfo.isUnitDevMode() ? 1 : 0));
          out.println("log.ConsoleActive="+(ConfigInfo.isLogConsoleActive() ? 1 : 0));
          out.println("log.fileActive="+(ConfigInfo.isLogFileActive() ? 1 : 0));
          out.println("log.popupExceptionDialog="+(ConfigInfo.isPopupExceptionDialog() ? 1 : 0));
          out.println("log.dbRequest="+(ConfigInfo.isLogDBRequestActive() ? 1 : 0));
          out.println("log.dbSelect="+(ConfigInfo.isLogDBSelectActive() ? 1 : 0));
          out.println("debug.showStatusColumn="+(ConfigInfo.isShowStatusColumn() ? 1 : 0));
          
          
          /*out.println("gui.font.size=14");
          out.println("unitDevMode=1");
          out.println("log.ConsoleActive=1");
          out.println("log.fileActive=0");
          out.println("log.popupExceptionDialog=1");
          out.println("log.dbRequest=1");
          out.println("debug.showStatusColumn=0");*/
          
          out.flush();
          fileWriter.close();
          
          String name = Globals.getApp().getName();
          Preferences prefs = Preferences.systemRoot();
          boolean exists = Globals.getApp().preferencesRegistryNodeExists(prefs, Application.REGISTRY_PARENT_APPLICATION_NODE_NAME+"/"+name);
          if( !exists ){
            Globals.getApp().setPreferencesRegistryStringValue(Application.REGISTRY_APPLICATION_INSTALL_DATE, ""+Globals.getApp().getSystemDate());
          }
          Globals.getApp().setDefaultAppDirectory(directoryPathStringVal);
          Globals.getApp().setDefaultEnvironment(environment);
          
        }catch( Exception a){
          Globals.logException(a);
        }
        if( withRestart ){
          doRestartProcedure();
        }
        Globals.getDisplayManager().goBack();  
      }
      
    }
    
    /*if( STATE_START == STATE_DIRECTORY || currentState != STATE_START){
      getPreviousButton().setVisible(true);  
    }*/
    if( stateChanged ){
      getPreviousButton().setVisible(true);  
    }
    
    
    
    panelSeq.getCenterPanel().setVisible(false);
    panelSeq.getCenterPanel().setVisible(true);
    
    return false;
  }

  
  @Override
  public boolean previousAction() {
    
    getNextButton().setText(FWizardFooterPanel.BUTTON_LABEL_NEXT);
    goBackPath.remove((Integer)currentState);
    currentState = goBackPath.get(goBackPath.size()-1);
    if( currentState == STATE_START){
      goBackPath.clear();
      goBackPath.add(STATE_START);
      getPreviousButton().setVisible(false);
    }
    
    panelSeq.goBack(false);
    return false;
  }
  
  @Override
  public boolean cancelAction() {
    
    Globals.getApp().setDefaultAppDirectory(defaultAppDirectory_backup);
    Globals.getApp().setDefaultEnvironment(defaultEnvironment_backup);
    if( withRestart ){
      Globals.getDisplayManager().goBack();
    }else{
      System.exit(0);  
    }
    return false;
  }

  private void doRestartProcedure(){
    int choice = JOptionPane.showConfirmDialog(null, "In order for the changes to take effect, you need to exit and restart the application.\nExit the application now?", "Restart required", JOptionPane.YES_NO_OPTION);
    if( choice == JOptionPane.YES_OPTION){
      System.exit(0);
    }
  }

  public int getCurrentState() {
    return currentState;
  }

  private boolean stateChanged = false;
  
  public void setState(int state) {
    if( state != STATE_START ){
      stateChanged = true;
    }
    goBackPath.add(state);
    this.currentState = state;
    panelSeq.changePanel(guiConfigInfo.getPanelFromState(state));
  }

  public boolean isWithRestart() {
    return withRestart;
  }

  public void setWithRestart(boolean withRestart) {
    this.withRestart = withRestart;
  }
  
}
