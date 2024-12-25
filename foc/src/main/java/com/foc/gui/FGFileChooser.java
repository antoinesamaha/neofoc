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
package com.foc.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import com.foc.Globals;
import com.foc.jasper.ExtensionFileFilter;
import com.foc.property.FString;

public class FGFileChooser {
	
	private FString pathProperty            = null;
	private String  pathString              = null;
	
	private String  explicitDefaultPath     = null; 
	private String  windowTitle             = ""  ;
	public  boolean acceptAllFileFilterUsed = false;
	private int     mode                    = JFileChooser.FILES_ONLY;
	
	private Component parent            		= null;
	private FPanel    panel             		= null;
	
	private ArrayList<ExtensionFileFilter> extensionFileFilters = null;
	
	public static final int MODE_FILES_ONLY            = JFileChooser.FILES_ONLY;
	public static final int MODE_DIRECTORIES_ONLY      = JFileChooser.DIRECTORIES_ONLY;
	public static final int MODE_FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;
	
	private boolean canceled = false;
	
	public FGFileChooser(FString filePathProperty, Component parent, String windowTitle, int mode, String filter, String filterDescription){
		pathProperty = filePathProperty;
		this.windowTitle = windowTitle;
		this.mode = mode;
		this.parent = parent;
		addExtensionFileFilter(filter, filterDescription);
	}
	
	public void dispose(){
		pathProperty        = null;
		pathString          = null;
		explicitDefaultPath = null;
		windowTitle         = ""  ;
		panel               = null;
		parent              = null;
		if(extensionFileFilters != null){
			extensionFileFilters.clear();
			extensionFileFilters = null;
		}
	}
	
	public void addExtensionFileFilter(String filter, String filterDescription){
		if(filter != null && !filter.isEmpty()){
			if(extensionFileFilters == null) extensionFileFilters = new ArrayList<ExtensionFileFilter>();
	    ExtensionFileFilter flt = new ExtensionFileFilter(filter, (filterDescription != null) ? filterDescription : "");
			extensionFileFilters.add(flt);
		}
	}
	
	public boolean isAcceptAllFileFilterUsed() {
		return acceptAllFileFilterUsed;
	}

	public void setAcceptAllFileFilterUsed(boolean acceptAllFileFilterUsed) {
		this.acceptAllFileFilterUsed = acceptAllFileFilterUsed;
	}
	
	public String getPath(){
		String path = pathString;
		if(pathProperty != null){
			path = pathProperty.getString();
		}
		return path;
	}
	
	public void setPath(String path){
		pathString = path;
		if(pathProperty != null){
			pathProperty.setString(path);
		}
	}	
	
	public void popupFileChooser(){
    String outputPath = null;

    String defaultPath = explicitDefaultPath != null ? explicitDefaultPath.trim() : getPath();
    if(defaultPath.equals("")){
    	defaultPath = System.getProperty("user.dir");
    }
    
    JFileChooser fch = null;
    if(defaultPath != null && !defaultPath.equals("")){
      defaultPath = defaultPath.replaceAll("\\\\","/");
    }

    fch = new JFileChooser(defaultPath);
    fch.setDialogTitle(windowTitle);
    fch.setSelectedFile(new File(getPath()));
    
    if(extensionFileFilters != null){
    	for(int i=0; i<extensionFileFilters.size(); i++){
    		ExtensionFileFilter flt = extensionFileFilters.get(i);
    		fch.setFileFilter(flt);
    	}
    }
    
    fch.setFileSelectionMode(mode);
    fch.setAcceptAllFileFilterUsed(isAcceptAllFileFilterUsed());
    int result = fch.showDialog(parent, "Ok");
    
    if (result == JFileChooser.APPROVE_OPTION){
    	setCanceled(false);
      try{
        outputPath = fch.getSelectedFile().toString();
        outputPath = outputPath.replaceAll("\\\\","/");
        setPath(outputPath);
      }catch( Exception e ){
      	Globals.logException(e);
      }
    }else{
    	setCanceled(true);
    }
	}
	
	@SuppressWarnings("serial")
	public FPanel getTextAndButtonPanel(){
		panel = new FPanel();
		
		FGTextField textComp = null;
		if(pathProperty != null){
			textComp = (FGTextField) pathProperty.getGuiComponent();
		}else{
			textComp = new FGTextField();
			textComp.setText(pathString);
		}
		textComp.setColumns(70);
		
		FGButton button = new FGButton("Browse");
		button.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				popupFileChooser();
			}
		});
		
		panel.add(new JLabel("Path"), 0, 0);
		panel.add(textComp, 1, 0);
		panel.add(button, 2, 0);
		
		return panel;
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
}
