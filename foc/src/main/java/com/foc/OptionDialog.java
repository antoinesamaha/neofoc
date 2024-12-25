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

import java.util.ArrayList;

import com.foc.shared.dataStore.IFocData;

public abstract class OptionDialog {
	public abstract boolean executeOption(String optionName);
	
	private Object optionDialog_Form = null;
	
	private String title   = null;
	private String message = null;
	private String width   = "-1px";
	private String height  = "200px";
  private IFocData focData = null;
  private boolean html = false;
	
	private ArrayList<Option> optionArray = null; 
	
	public OptionDialog(String title, String message){
		this.title   = title;
		this.message = message;
	}
	
	public OptionDialog(String title, String message, IFocData focData){
		this.title   = title;
		this.message = message;
		this.focData = focData;
	}

	public void dispose(){
		if(optionArray != null){
			for(int i=0; i<optionArray.size() ; i++){
				Option option = optionArray.get(i);
				option.dispose();
			}
			optionArray.clear();
			optionArray = null;
		}
		focData = null;
		message = null;
		optionDialog_Form = null;
	}
	
	public IFocData getOptionFocData(){
		return focData;
	}
	
	public boolean isHtml() {
    return html;
  }

  public void setHtml(boolean html) {
    this.html = html;
  }

	public void addOption(String name, String caption){
		if(optionArray == null) optionArray = new ArrayList<OptionDialog.Option>();
		Option option = new Option(name, caption);
		optionArray.add(option);
	}

	public String[] getOptionNamesArray(){
		String[] names = new String[getOptionCount()];
		for(int i=0; i<getOptionCount(); i++){
			names[i] = getOptionNameAt(i);
		}
		return names;
	}
	
	public String[] getOptionCaptionArray(){
		String[] captions = new String[getOptionCount()];
		for(int i=0; i<getOptionCount(); i++){
			captions[i] = getOptionCaptionAt(i);
		}
		return captions;
	}
	
	/*
	public String[] getOptionNames(){
		return options;
	}	

	public String[] getOptions(){
		return options;
	}
	*/
	
	public int getOptionCount(){
		return optionArray != null ? optionArray.size() : 0;
	}
	
	public String getOptionCaptionAt(int i){
		return optionArray != null ? optionArray.get(i).getCaption() : "";
	}
	
	public String getOptionNameAt(int i){
		return optionArray != null ? optionArray.get(i).getName() : "";
	}

	public Option getOptionAt(int index){
		return optionArray != null ? optionArray.get(index) : null;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public Object popup(){
		return Globals.popupDialog(this);
	}

	public void setOptionButtonAt(int i, Object guiObject){
		Option option = getOptionAt(i);
		if(option != null){
			option.setGuiObject(guiObject);
		}
	}

	public Object getOptionButtonAt(int i){
		Object guiObject = null;
		Option option = getOptionAt(i);
		if(option != null){
			guiObject = option.getGuiObject();
		}
		return guiObject;
	}

	public Object getOptionDialog_Form() {
		return optionDialog_Form;
	}

	public void setOptionDialog_Form(Object optionDialog_Form) {
		this.optionDialog_Form = optionDialog_Form;
	}
	
	private class Option {
		String caption = null;
		String name    = null;
		Object guiObject = null;

		public Option(String name, String caption){
			setCaption(caption);
			setName(name);
		}
		
		public void dispose(){
			guiObject = null;
		}
		
		public String getCaption() {
			return caption;
		}
		
		public void setCaption(String caption) {
			this.caption = caption;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Object getGuiObject() {
			return guiObject;
		}

		public void setGuiObject(Object guiObject) {
			this.guiObject = guiObject;
		}
	}
}
