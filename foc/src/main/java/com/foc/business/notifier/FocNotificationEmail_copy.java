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

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.email.EMailAccount;
import com.foc.shared.dataStore.IFocData;

@SuppressWarnings("serial")
public class FocNotificationEmail_copy extends FocObject implements FocNotificationEmailConst {

//  public static final int ENCRYPTION_TYPE_NONE = 0;
//  public static final int ENCRYPTION_TYPE_SSL  = 1;
//  public static final int ENCRYPTION_TYPE_TLS  = 2;

  private MimeMessage mimeMessage = null;
  private IFocData    focData     = null;
//  private int encryptionConnectionType = ENCRYPTION_TYPE_SSL;
//
//  private String host = "email-smtp.us-east-1.amazonaws.com";
//  // private int port = 587;
//  private int port = 465;
//  private String username = "AKIAIVMOV3G7F55RILIQ";
//  private String password = "Ajmnx7ON3LoKYMtFdbx+LPqls3HnArYkGlJ96/42qFKu";

  public FocNotificationEmail_copy(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }

  @Deprecated
  public FocNotificationEmail_copy(FocConstructor constr, FocNotificationEmailTemplate template, IFocData focData) {
    this(constr);

    init(template, focData);
    fill();
  }
  
  public FocNotificationEmail_copy(FocNotificationEmailTemplate template, IFocData focData) {
    this(new FocConstructor(FocNotificationEmailDesc.getInstance(), null));
    init(template, focData);
  }
  
  private void init(FocNotificationEmailTemplate template, IFocData focData){
    setTemplate(template);
    setEmailFocData(focData);
    adjustTemplate();
  }
  
  public void fill(){
    generateMimeMessage();
  }
  
  public void adjustTemplate(){
    if (getTemplate() != null && getEmailFocData() != null) {
      setSubject(getTemplate().getSubject(focData));
      setText(getTemplate().getText(focData));
      setRecipients(getTemplate().getRecipients(focData));
      setBcc(getTemplate().getBcc(focData));
      setSender("01barmaja@01barmaja.com");
    } else {
      setSubject("");
      setText("");
//      setRecipients("placeholder@01barmaja.com");
      setSender("01barmaja@01barmaja.com");
    }
  }

  public String getSender() {
    return getPropertyString(FLD_SENDER);
  }

  public void setSender(String from) {
    setPropertyString(FLD_SENDER, from);
  }

  public String getRecipients() {
    return getPropertyString(FLD_RECIPIENTS);
  }

  public InternetAddress[] getRecipientsMime() {
    InternetAddress[] internetArray = null;
    String recipients = getRecipients();
    if (recipients != null && !recipients.equals("") && !recipients.isEmpty()) {
      String[] recipientArray = recipients.split(",");
      internetArray = new InternetAddress[recipientArray.length];

      for (int i = 0; i < recipientArray.length; i++) {
        try {
          internetArray[i] = new InternetAddress(recipientArray[i]);
        } catch (AddressException e) {
          e.printStackTrace();
        }
      }
    }

    return internetArray;
  }

  public void setRecipients(String to) {
    setPropertyString(FLD_RECIPIENTS, to);
  }

  public String getBcc() {
    return getPropertyString(FLD_BCC);
  }

  public InternetAddress[] getBccMime() {
    InternetAddress[] internetArray = null;
    String bcc = getBcc();

    if (bcc != null && !bcc.equals("") && !bcc.isEmpty()) {
      String[] bccArray = bcc.split(",");
      internetArray = new InternetAddress[bccArray.length];

      for (int i = 0; i < bccArray.length; i++) {
        try {
          internetArray[i] = new InternetAddress(bccArray[i]);
        } catch (AddressException e) {
          e.printStackTrace();
        }
      }
    }
    return internetArray;
  }

  public void setBcc(String bcc) {
    setPropertyString(FLD_BCC, bcc);
  }

  public String getSubject() {
    return getPropertyString(FLD_SUBJECT);
  }

  public void setSubject(String subject) {
    setPropertyString(FLD_SUBJECT, subject);
  }

  public String getText() {
    return getPropertyString(FLD_TEXT);
  }

  public void setText(String text) {
    setPropertyString(FLD_TEXT, text);
  }

  public FocNotificationEmailTemplate getTemplate() {
    return (FocNotificationEmailTemplate) getPropertyObject(FLD_TEMPLATE_NAME);
  }

  public void setTemplate(FocNotificationEmailTemplate template) {
    setPropertyObject(FLD_TEMPLATE_NAME, template);
  }
  
  public void setEmailFocData(IFocData data){
    focData = data;
  }
  
  public IFocData getEmailFocData(){
    return focData;
  }

//  public int getEncryptionConnectionType() {
//    return encryptionConnectionType;
//  }
//
//  public void setEncryptionConnectionType(int type) {
//    encryptionConnectionType = type;
//  }
//
//  private String getUsername() {
//    return username;
//  }
//
//  private String getPassword() {
//    return password;
//  }
//
//  public String getHost() {
//    return host;
//  }
//
//  public void setHost(String host) {
//    this.host = host;
//  }
//
//  public int getPort() {
//    return port;
//  }
//
//  public void setPort(int port) {
//    this.port = port;
//  }

  public MimeMessage generateMimeMessage() {
  	EMailAccount emailAccount = EMailAccount.getInstance();
  	return generateMimeMessage(emailAccount);
  }
  
  public MimeMessage generateMimeMessage(final EMailAccount emailAccount) {
    // Get system properties
    Properties props = new Properties();

    // Setup mail server
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.host", emailAccount.getHost());
    props.put("mail.smtp.socketFactory.port", emailAccount.getPort());
    if (emailAccount.getEncryptionConnectionType() == EMailAccount.ENCRYPTION_TYPE_SSL) {
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", emailAccount.getPort());//Try 25

    //Outgoing server (SMTP) 25, 587, or 2587 (to connect using STARTTLS), or 465 or 2465 (to connect using TLS Wrapper).
    if(emailAccount.getPort() == 25 || emailAccount.getPort() == 587 || emailAccount.getPort() == 2587){
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.starttls.required", "true");
    }
    
    
    
    
/*
    // Create a Properties object to contain connection configuration information.
	Properties props = System.getProperties();
	props.put("mail.transport.protocol", "smtp");
	props.put("mail.smtp.port", PORT); 
	
	// Set properties indicating that we want to use STARTTLS to encrypt the connection.
	// The SMTP session will begin on an unencrypted connection, and then the client
    // will issue a STARTTLS command to upgrade to an encrypted connection.
	props.put("mail.smtp.auth", "true");
	props.put("mail.smtp.starttls.enable", "true");
	props.put("mail.smtp.starttls.required", "true");

    // Create a Session object to represent a mail session with the specified properties. 
	Session session = Session.getDefaultInstance(props);

    // Create a message with the specified information. 
    MimeMessage msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(FROM));
    msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
    msg.setSubject(SUBJECT);
    msg.setContent(BODY,"text/plain");
        
    // Create a transport.        
    Transport transport = session.getTransport();
                
    // Send the message.
    try
    {
        System.out.println("Attempting to send an email through the Amazon SES SMTP interface...");
        
        // Connect to Amazon SES using the SMTP username and password you specified above.
        transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
    	
        // Send the email.
        transport.sendMessage(msg, msg.getAllRecipients());
        System.out.println("Email sent!");
    }
    catch (Exception ex) {
        System.out.println("The email was not sent.");
        System.out.println("Error message: " + ex.getMessage());
    }
    finally
    {
        // Close and terminate the connection.
        transport.close();        	
    }
    
    */
    
    
    
    
    
    
    
    
    // Get the default Session object.

    Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
//      	EMailAccount emailAccount = EMailAccount.getInstance();
        PasswordAuthentication pwa = new PasswordAuthentication(emailAccount.getUsername(), emailAccount.getPassword());
        return pwa;
      }
    });

    MimeMessage mime = new MimeMessage(session);
    try {
      mime.setRecipients(Message.RecipientType.TO, getRecipientsMime());
      mime.setRecipients(Message.RecipientType.BCC, getBccMime());
      mime.setFrom(new InternetAddress(getSender()));
      mime.setSubject(getSubject());
      mime.setText(getText());
    } catch (AddressException e) {
      e.printStackTrace();
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    mimeMessage = mime;
    return mime;
  }

  public void send() {
    try {
    	if(ConfigInfo.isUseLocalEmailClientForNotification()){
    		send_UsingLocalEmailClient();
    	}else{
    		sendWithException();
    	}
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }
  
  public void sendWithException() throws Exception {
  	if(!Globals.getApp().isUnitTest()){//When Unit test we do not send the emails
	  	if(mimeMessage == null){
	  		generateMimeMessage();
	  	}
	    if(mimeMessage != null) {
	      Transport.send(mimeMessage);
	    }
  	}
  }
  
  /*private void send_UsingLocalEmailClient(){
  	String text = getText().replace('\'', '-');
  	String javaScript = "var win = window.open('mailto:"+getRecipients()+"?body="+text+"', '_blank'); win.close();";
    JavaScript.getCurrent().execute(javaScript);
  }*/
  
  private void send_UsingLocalEmailClient(){
  	if(getText() != null){
	  	String text = getText().replaceAll("\n", "%0D%0A");
	  	text = text.replace('\'', '-');
	  	String recipients = getRecipients() != null ? getRecipients().replaceAll(" ", "") : "";
	  	String javaScript = "var win = window.open('mailto:"+recipients+"?body="+text+"', '_blank'); win.close();";
  	}
  }
}
