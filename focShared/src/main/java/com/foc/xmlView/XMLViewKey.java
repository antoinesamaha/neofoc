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
package com.foc.xmlView;

public class XMLViewKey implements IXMLViewConst{
  
  public static final String DOCUMENT_HEADER_STORAGE_NAME = "DOCUMENT_HEADER";
  public static final int LEN_VIEW            = 30;
  
  private int     type             = 0   ;
  private String  context          = null;
  private String  userView         = null;
  private String  storageName      = null;
  private boolean forNewObjectOnly = false;
  private boolean printerFriendly  = false; 
  private boolean mobileFriendly   = false;
  private String  language         = null;
  
	private String strKey            = null;
  
  public XMLViewKey(){
  }

  public XMLViewKey(String storageName, int type, String context, String userView){
    setStorageName(storageName);
    setType(type);
    setContext(context);
    setUserView(userView);
  }

  public XMLViewKey(String storageName, int type){
    setStorageName(storageName);
    setType(type);
    setContext(CONTEXT_DEFAULT);
    setUserView(VIEW_DEFAULT);
  }
  
  public XMLViewKey(XMLViewKey sourceKey){
    setStorageName(sourceKey.getStorageName());
    setType(sourceKey.getType());
    setContext(sourceKey.getContext());
    setUserView(sourceKey.getUserView());
    setLanguage(sourceKey.getLanguage());
    setMobileFriendly(sourceKey.isMobileFriendly());
    setForNewObjectOnly(sourceKey.isForNewObjectOnly());
    setPrinterFriendly(sourceKey.isPrinterFriendly());
  }

  public void dispose(){
  	
  }
  
  public boolean isEqual_WithoutSelectedView(XMLViewKey key){
    boolean equal = key != null && key.getStorageName().equals(getStorageName()) && key.getContext().equals(getContext()) && key.getType() == getType();
    return equal;
  }
  
  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setType(String type) {
  	if(type.equals(TYPE_NAME_FORM)){
  		setType(TYPE_FORM);
  	}else if(type.equals(TYPE_NAME_TABLE)){
  		setType(TYPE_TABLE);
  	}else if(type.equals(TYPE_NAME_TREE)){
  		setType(TYPE_TREE);
  	}else if(type.equals(TYPE_NAME_PIVOT)){
  	  setType(TYPE_PIVOT);
  	}
  }
  
  public String getContext() {
    return context;
  }

  public void setContext(String context) {
  	if(context == null) context = CONTEXT_DEFAULT;
    this.context = context;
  }

  public String getUserView() {
    return userView;
  }

  public void setUserView(String userView) {
  	if(userView == null) userView = VIEW_DEFAULT;
    this.userView = userView;
    if(userView.toLowerCase().contains("print")){
    	setPrinterFriendly(true);
    }
  }

  public String getStorageName() {
    return storageName;
  }

  public void setStorageName(String storageName) {
    this.storageName = storageName;
  }
  
  public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

  public String builStringKey(){
  	String keyString = getStorageName()+";"+getType()+";"+getContext()+";"+getUserView();
  	if(isForNewObjectOnly()){
  		keyString += ";NEW_ONLY";
  	}
  	if(isMobileFriendly()){
  		keyString += ";Mobile";
  	}
  	String lang = getLanguage();
  	if(lang != null && !lang.isEmpty()) {
  		keyString += ";"+lang;
  	}
  	return keyString;
  }
  
  public String getStringKey(){
  	if(strKey == null){
  		strKey = builStringKey();
  	}
  	return strKey; 
  }

	public boolean isForNewObjectOnly() {
		return forNewObjectOnly;
	}

	public void setForNewObjectOnly(boolean forNewObjectOnly) {
		this.forNewObjectOnly = forNewObjectOnly;
	}

	public boolean isPrinterFriendly() {
		return printerFriendly;
	}

	public void setPrinterFriendly(boolean printerFriendly) {
		this.printerFriendly = printerFriendly;
	}

	public boolean isMobileFriendly() {
		return mobileFriendly;
	}

	public void setMobileFriendly(boolean mobileFriendly) {
		this.mobileFriendly = mobileFriendly;
	}
}
