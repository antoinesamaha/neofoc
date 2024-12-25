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
package com.foc.business.notifier;

import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.shared.dataStore.IFocData;

@SuppressWarnings("serial")
public class FocNotificationEmailTemplate extends FocObject implements FocNotificationEmailConst {
   
  public FocNotificationEmailTemplate(FocConstructor constr){
    super(constr);
    newFocProperties();
  }
  
  public String getName(){
    return getPropertyString(FLD_TEMPLATE_NAME);
  }
  
  public void setName(String name){
    setPropertyString(FLD_TEMPLATE_NAME, name);
  }

  public boolean isHTML(){
    return getPropertyBoolean(FLD_HTML);
  }
  
  public String getSubject(){
    return getPropertyString(FLD_SUBJECT);
  }
  
  public String getRecipients(){
    return getPropertyString(FLD_RECIPIENTS);
  }
  
  public String getRecipients(final IFocData focData){
    return resolveString(getRecipients(), focData);
  }
  
  public void setRecipients(String recipients){
    setPropertyString(FLD_RECIPIENTS, recipients);
  }
  
  public void addRecipient(String recipient){
    String temp = getRecipients();
    temp.concat(","+recipient);
    setRecipients(temp);
  }
  
  public String getBcc(){
    return getPropertyString(FLD_BCC);
  }
  
  public String getBcc(final IFocData focData){
    return resolveString(getBcc(), focData);
  }
  
  public void setBcc(String bcc){
    setPropertyString(FLD_BCC, bcc);
  }
  
  private String resolveString(String expression, IFocData focData){
  	return Globals.getIFocNotification().getFocDataDictionary().resolveExpression(focData, expression, false);
  }

  public String getSubject(IFocData focData){
    return resolveString(getSubject(), focData);
  }
  
  public void setSubject(String subject){
    setPropertyString(FLD_SUBJECT, subject);
  }
  
  public String getText(){
    return getPropertyString(FLD_TEXT);
  }
  
  public String getText(final IFocData focData){
    return resolveString(getText(), focData);    
  }
  
  public void setText(String text){
    setPropertyString(FLD_TEXT, text);
  }
  
  public String getPrintFileName(){
    return getPropertyString(FLD_PRN_FILE_NAME);
  }
  
  public void setPrintFileName(String value){
    setPropertyString(FLD_PRN_FILE_NAME, value);
  }
  
  public FocNotificationEmail newEmail(IFocData focData){
  	FocNotificationEmail email = new FocNotificationEmail(new FocConstructor(FocNotificationEmailDesc.getInstance(), null), this, focData);
  	return email;
  }
}
