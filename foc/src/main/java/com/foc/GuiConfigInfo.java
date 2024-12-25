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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Enumeration;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.foc.desc.FocObject;
import com.foc.gui.FColorProvider;
import com.foc.gui.FGLabel;
import com.foc.gui.FGTextArea;
import com.foc.gui.FPanel;
import com.foc.list.FocList;

public class GuiConfigInfo {
  
  private JFormattedTextField directoryPath = null;
  private ButtonGroup radioButtonGroup = null;
  private JTextField newEnvTextField = null;
  private JComboBox existingEnvComboBox = null;
  private JRadioButton existingEnvRadio = null;
  private JRadioButton newEnvRadio = null;
  private ConfigWizardPanelVariables configWizardPanelVariables = null;
  public static final String ENVIRONMENT_PREFIX = "env_";
  public static final String EXISTING_ENVIRONMENT = "Select an existing environment";
  public static final String NEW_ENVIRONMENT = "Install a new environment";
  
  final static String localhost = "DB server host";
  final static String port = "DB server port";
  final static String schema = "DB name (DB schema)";
  final static String default_schema = "test";
  final static String username = "DB User Name";
  final static String password = "DB UserPassword";
  final static float  fontSize = 11f;
  final static Color  captionTextColor = FColorProvider.getColorAt(2);
  
  public GuiConfigInfo(){
    configWizardPanelVariables = new ConfigWizardPanelVariables();
  }
  
  private String[] getDefaultJdbcUrlVals(String jdbcURL){
    String []urlValue = null;
    
    if( jdbcURL.indexOf("mysql") != -1 ){
      urlValue = new String[3];
      String localhost = jdbcURL.substring(jdbcURL.indexOf("//", 10)+2, jdbcURL.indexOf(':', 13));
      String port = jdbcURL.substring(jdbcURL.indexOf(':', 13)+1, jdbcURL.indexOf('/', 13));
      String schema = jdbcURL.substring(jdbcURL.indexOf('/', 13)+1, jdbcURL.length());
      urlValue[0]= localhost;
      urlValue[1] = port;
      urlValue[2] = schema;  
    }
    return urlValue;
  }
  
  public FPanel getConfigInfoPanel(){
    FPanel configInfoPanel = new FPanel("", FPanel.FILL_NONE);

    ConfigInfo.loadFile();
    configInfoPanel.add(new FGLabel("Database credentials:"), 0, 0);
    
    FocList configInfoList = ConfigInfoObjectDesc.getList(FocList.LOAD_IF_NEEDED);
    configInfoList.removeAll();
    String []urlValue = getDefaultJdbcUrlVals(ConfigInfo.getJdbcURL());
    FocObject focObj = null;
    if( urlValue != null ){
      focObj = configInfoList.newEmptyItem();
      focObj.setPropertyString(ConfigInfoObjectDesc.FLD_PROPERTY, localhost);
      focObj.setPropertyString(ConfigInfoObjectDesc.FLD_VALUE, urlValue[0]);
      focObj = configInfoList.newEmptyItem();
      focObj.setPropertyString(ConfigInfoObjectDesc.FLD_PROPERTY, port);
      focObj.setPropertyString(ConfigInfoObjectDesc.FLD_VALUE, urlValue[1]);
      focObj = configInfoList.newEmptyItem();
      focObj.setPropertyString(ConfigInfoObjectDesc.FLD_PROPERTY, schema);
      focObj.setPropertyString(ConfigInfoObjectDesc.FLD_VALUE, urlValue[2]);  
    }
    focObj = configInfoList.newEmptyItem();
    focObj.setPropertyString(ConfigInfoObjectDesc.FLD_PROPERTY, username);
    focObj.setPropertyString(ConfigInfoObjectDesc.FLD_VALUE, ConfigInfo.getJdbcUserName());
    focObj = configInfoList.newEmptyItem();
    focObj.setPropertyString(ConfigInfoObjectDesc.FLD_PROPERTY, password);
    focObj.setPropertyString(ConfigInfoObjectDesc.FLD_VALUE, ConfigInfo.getJdbcPassword());
    
    configInfoPanel.add(new ConfigInfoObjectGuiBrowsePanel(configInfoList, FocObject.DEFAULT_VIEW_ID), 0, 1);
    
    return configInfoPanel;
  }
  
  public FPanel getDirectoryPanel(){
    
    FPanel directoryPanel = new FPanel("", FPanel.FILL_NONE);
    
    
    FGTextArea textAreaLabel = new FGTextArea();
    textAreaLabel.setEditable(false);
    textAreaLabel.setText("Welcome to 01Barmaja installation wizard."+
        "\nThis wizard will help you:"
        +"\n        * install a new application environment with multi-users access."
        +"\n        * switch to an existing application environment.\n\n\n");
    
    textAreaLabel.setBackground(directoryPanel.getBackground());
    Font font = new FGLabel().getFont().deriveFont(fontSize);
    textAreaLabel.setForeground(captionTextColor);
    textAreaLabel.setFont(font);
    
    directoryPanel.add(textAreaLabel, 0, 0);
    
    FPanel directorySubPanel = new FPanel("", FPanel.FILL_NONE);
    
    directoryPath = new JFormattedTextField();
    directoryPath.setEditable(false);
    directoryPath.setPreferredSize(new Dimension(200, 20));
    directorySubPanel.add("Directory:", directoryPath, 0, 0);
    
    directoryPath.addPropertyChangeListener("value", new PropertyChangeListener(){

      public void propertyChange(PropertyChangeEvent evt) {
        File dir = new File(directoryPath.getText());
        if( dir.exists()){
          boolean exists = false;
          File[] files = dir.listFiles();
          existingEnvComboBox.removeAllItems();  
          for( int i = 0; i < files.length; i++){
            if( files[i].getName().startsWith(GuiConfigInfo.ENVIRONMENT_PREFIX)){
              exists = true;
              existingEnvComboBox.addItem(files[i].getName().substring(GuiConfigInfo.ENVIRONMENT_PREFIX.length()));  
            }
          }  
          existingEnvRadio.setEnabled(exists);
          existingEnvRadio.setSelected(exists);
          existingEnvComboBox.setEnabled(exists);
          newEnvTextField.setEditable(!exists);
          newEnvRadio.setSelected(!exists);
        }else{
          existingEnvRadio.setEnabled(false);
          existingEnvRadio.setSelected(false);
          existingEnvComboBox.setEnabled(false);
          newEnvTextField.setEditable(true);
        }
      }
    });
    
    
    final JButton browseButton = new JButton("...");
    browseButton.setPreferredSize(new Dimension(20, 20));
    
    
    directorySubPanel.add(browseButton, 2, 0);
    browseButton.addActionListener(new ActionListener(){

      public void actionPerformed(ActionEvent e) {
        directoryPath.setValue(getSelectedDirectoryPath());
      }
      
    });
    
    directoryPanel.add(directorySubPanel, 0, 1);
    
    FPanel environmentInputValues = new FPanel("", FPanel.FILL_NONE);
    environmentInputValues.add(getExistingEnvironmentPanel(), 0, 0);
    environmentInputValues.add(getNewEnvironmentPanel(), 0, 1);
    
    FPanel wrapperEnvironmentPanel = new FPanel("", FPanel.FILL_NONE);
    wrapperEnvironmentPanel.add(getEnvironmentDecisionPanel(), 0, 0);
    wrapperEnvironmentPanel.add(environmentInputValues, 1, 0);
    
    directoryPanel.add(wrapperEnvironmentPanel, 0, 2);
    
    if( configWizardPanelVariables.getDirectoryPath() != null ){
      directoryPath.setValue(configWizardPanelVariables.getDirectoryPath());  
    }
    
    String defaultEnvironment = Globals.getApp().getDefaultEnvironment();
    if( defaultEnvironment != null && defaultEnvironment.length() > ENVIRONMENT_PREFIX.length()){
      existingEnvComboBox.setSelectedItem(""+Globals.getApp().getDefaultEnvironment().substring(ENVIRONMENT_PREFIX.length()));  
    }
    
    return directoryPanel;
  }
  
  public FPanel getEnvironmentDecisionPanel(){
    
    FPanel environmentDecisionPanel = new FPanel("", FPanel.FILL_NONE);
    
    /*
    FPanel captionPanel = new FPanel();
    
    FGLabel label1 = new FGLabel("An Environment is where your application preferences");
    Font font = label1.getFont().deriveFont(fontSize);
    label1.setFont(font);
    label1.setForeground(captionTextColor);
    FGLabel label2 = new FGLabel("are stored.");
    label2.setFont(font);
    label2.setForeground(captionTextColor);
    FGLabel label3 = new FGLabel("e.g. Database Credentials");
    label3.setFont(font);
    label3.setForeground(captionTextColor);
    
    captionPanel.add(label1, 0, 0);
    captionPanel.add(label2, 0, 1);
    captionPanel.add(label3, 0, 2);
    
    environmentDecisionPanel.add(captionPanel, 0, 0);*/
    
    
    FPanel environmentDecisionSubPanel = new FPanel("", FPanel.FILL_NONE);
    radioButtonGroup = new ButtonGroup();
    existingEnvRadio = new JRadioButton(EXISTING_ENVIRONMENT);
    existingEnvRadio.setBackground(environmentDecisionSubPanel.getBackground());
    
    existingEnvRadio.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        if( existingEnvRadio.isSelected() ){
          existingEnvComboBox.setEnabled(true);
          newEnvTextField.setEditable(false);
        }else{
          existingEnvComboBox.setEnabled(false);
          newEnvTextField.setEditable(true);
        }
      }
    });
    environmentDecisionSubPanel.add(existingEnvRadio, 0, 0);
    radioButtonGroup.add(existingEnvRadio);
    existingEnvRadio.setEnabled(false);
    
    newEnvRadio = new JRadioButton(NEW_ENVIRONMENT);
    newEnvRadio.setBackground(environmentDecisionSubPanel.getBackground());
    newEnvRadio.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        if( newEnvRadio.isSelected() ){
          existingEnvComboBox.setEnabled(false);
          newEnvTextField.setEditable(true);
        }else{
          existingEnvComboBox.setEnabled(true);
          newEnvTextField.setEditable(false);
        }
      }
    });
    environmentDecisionSubPanel.add(newEnvRadio, 0, 1);
    radioButtonGroup.add(newEnvRadio);
    newEnvRadio.setSelected(true);
    
    environmentDecisionPanel.add(environmentDecisionSubPanel, 0, 1);
    
    
    return environmentDecisionPanel;
  }
 
  public FPanel getNewEnvironmentPanel(){
    FPanel newEnvPanel = new FPanel("", FPanel.FILL_NONE);
    newEnvTextField = new JTextField();
    newEnvTextField.setPreferredSize(new Dimension(130, 20));
    newEnvPanel.add(newEnvTextField, 0, 0);
    return newEnvPanel;
  }
  
  public FPanel getExistingEnvironmentPanel(){
    FPanel existingEnvPanel = new FPanel("", FPanel.FILL_NONE);
    existingEnvComboBox = new JComboBox();
    existingEnvComboBox.setPreferredSize(new Dimension(130, 20));
    existingEnvPanel.add(existingEnvComboBox, 0, 0);
    existingEnvComboBox.setEnabled(false);
    
    /*File dir = new File(configWizardPanelVariables.getDirectoryPath());
    boolean directoryExists = dir.exists();
    if( directoryExists ){
      File[] files = dir.listFiles();
      for( int i = 0; i < files.length; i++){
        if( files[i].getName().startsWith(GuiConfigInfo.ENVIRONMENT_PREFIX)){
          envComboBox.addItem(files[i].getName().substring(GuiConfigInfo.ENVIRONMENT_PREFIX.length()));  
        }
      }  
    }*/
    
    return existingEnvPanel;
  }
  
  public FPanel getPanelFromState( int state ){
    FPanel statePanel = null;
    if( state == ConfigInfoWizardPanel.STATE_DIRECTORY ){
      statePanel = getDirectoryPanel();
    }else if ( state == ConfigInfoWizardPanel.STATE_ENV_DECISION){
      statePanel = getEnvironmentDecisionPanel();
    }else if( state == ConfigInfoWizardPanel.STATE_NEW_ENV ){
      statePanel = getNewEnvironmentPanel();
    }else if( state == ConfigInfoWizardPanel.STATE_EXISTING_ENV ){
      statePanel = getExistingEnvironmentPanel();
    }else if( state == ConfigInfoWizardPanel.STATE_CONFIGINFO ){
      statePanel = getConfigInfoPanel();
    }
    return statePanel;
  }
  
  public void saveStatePanelVariables(int state){
    
    if( state == ConfigInfoWizardPanel.STATE_DIRECTORY ){
      
      configWizardPanelVariables.setDirectoryPathEmpty(false);
      
      if( directoryPath.getText().equals("")){
        configWizardPanelVariables.setDirectoryPathEmpty(true);
      }else{
        configWizardPanelVariables.setDirectoryPath(directoryPath.getText());  
        if( existingEnvRadio.isSelected()){
          String existingEnv = existingEnvComboBox.getSelectedItem() == null ? "" : ""+existingEnvComboBox.getSelectedItem();
          configWizardPanelVariables.setExistingEnvironment(existingEnv);
          configWizardPanelVariables.setExistingEnv(true);
        }else{
          configWizardPanelVariables.setNewEnvironment(newEnvTextField.getText());
          configWizardPanelVariables.setExistingEnv(false);
        }

      }
      
    }else if( state == ConfigInfoWizardPanel.STATE_ENV_DECISION ){
      JRadioButton selectedRadioButton = null;
      Enumeration enumer = radioButtonGroup.getElements();
      while( enumer.hasMoreElements()){
        selectedRadioButton = (JRadioButton)enumer.nextElement();
        if( selectedRadioButton.isSelected()){
          break;
        }
      }
      if( selectedRadioButton.getText().equals(EXISTING_ENVIRONMENT)){
        configWizardPanelVariables.setExistingEnv(true);
      }else if( selectedRadioButton.getText().equals(NEW_ENVIRONMENT)){
        configWizardPanelVariables.setExistingEnv(false);
      }
    }else if( state == ConfigInfoWizardPanel.STATE_NEW_ENV ){
      configWizardPanelVariables.setNewEnvironment(newEnvTextField.getText());
      
    }else if( state == ConfigInfoWizardPanel.STATE_EXISTING_ENV ){
      String existingEnv = existingEnvComboBox.getSelectedItem() == null ? "" : ""+existingEnvComboBox.getSelectedItem();
      configWizardPanelVariables.setExistingEnvironment(existingEnv);
    }else if( state == ConfigInfoWizardPanel.STATE_CONFIGINFO ){
      
      FocList configInfoList = ConfigInfoObjectDesc.getList(FocList.LOAD_IF_NEEDED);
      
      configWizardPanelVariables.setConfigFieldsEmpty(false);
      for( int j = 0; j < configInfoList.size(); j++){
        FocObject focObj = configInfoList.getFocObject(j);
        if( focObj.getPropertyString(ConfigInfoObjectDesc.FLD_VALUE).equals("") ){
          configWizardPanelVariables.setConfigFieldsEmpty(true);
          break;
        }
      }
      
      configWizardPanelVariables.setLocalhost(getValueFromList(configInfoList, localhost));
      configWizardPanelVariables.setPort(getValueFromList(configInfoList, port));
      configWizardPanelVariables.setSchema(getValueFromList(configInfoList, schema));
      configWizardPanelVariables.setUsername(getValueFromList(configInfoList, username));
      configWizardPanelVariables.setPassword(getValueFromList(configInfoList, password));
    }
  }
  
  private String getValueFromList(FocList list, String value){
    FocObject focObj = list.searchByPropertyStringValue(ConfigInfoObjectDesc.FLD_PROPERTY, value);
    return focObj == null ? "" : focObj.getPropertyString(ConfigInfoObjectDesc.FLD_VALUE);
  }
  
  
  private String getSelectedDirectoryPath() {
    String outputPath = null;
    JFileChooser fch = new JFileChooser("C:\\");
    fch.setDialogTitle("Select directory");
    fch.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
    int result = fch.showDialog(null, "OK");
    if ( result == JFileChooser.CANCEL_OPTION ){
      return null;  
    }else{
      try{
        outputPath = fch.getSelectedFile().toString();
      }catch( Exception e ){
        Globals.logException(e);
      }
      return outputPath.toLowerCase();
    }
  }

  public ConfigWizardPanelVariables getConfigWizardPanelVariables() {
    return configWizardPanelVariables;
  }

  public void setConfigWizardPanelVariables(ConfigWizardPanelVariables configWizardPanelVariables) {
    this.configWizardPanelVariables = configWizardPanelVariables;
  }
}
