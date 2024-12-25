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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.business.printing.PrnLayout;
import com.foc.business.printing.PrnLayoutDesc;
import com.foc.business.printing.gui.PrintingAction;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.shared.dataStore.IFocData;
import com.foc.util.Utils;
import com.vaadin.ui.JavaScript;

@SuppressWarnings("serial")
public class FocNotificationEmail extends FocObject implements FocNotificationEmailConst {

	private IFocNotificationEmailLaunchStatus iFocNotificationEmailLaunchStatus = null;
	private HashMap<String, EmailAttachements> attachmentsMap   = null;
	private LinkedList<String>                 attachmentsOrder = null;
  private MimeMessage mimeMessage = null;
  private IFocData    focData     = null;

  public FocNotificationEmail(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  @Override
  public void dispose() {
  	super.dispose();
  	iFocNotificationEmailLaunchStatus = null;
  }

  @Deprecated
  public FocNotificationEmail(FocConstructor constr, FocNotificationEmailTemplate template, IFocData focData) {
    this(constr);

    init(template, focData);
    fill();
  }
  
  public FocNotificationEmail(FocNotificationEmailTemplate template, IFocData focData) {
  	this(template, focData, null);
  }
  
  public FocNotificationEmail(FocNotificationEmailTemplate template, IFocData focData, IFocNotificationEmailLaunchStatus iFocNotificationEmailLaunchStatus) {
    this(new FocConstructor(FocNotificationEmailDesc.getInstance(), null));
    this.iFocNotificationEmailLaunchStatus = iFocNotificationEmailLaunchStatus;
    init(template, focData);
  }
  
  public void init(FocNotificationEmailTemplate template, IFocData focData){
    setTemplate(template);
    setEmailFocData(focData);
    adjustTemplate();
  }
  
  public void fill(){
    generateMimeMessage();
  }
  
  public void adjustTemplate(){
    if (getTemplate() != null) {
      setSubject(getTemplate().getSubject(focData));
      setText(getTemplate().getText(focData));
      setRecipients(getTemplate().getRecipients(focData));
      setBcc(getTemplate().getBcc(focData));
//      setSender("01barmaja@01barmaja.com");
    } else {
      setSubject("");
      setText("");
//      setSender("01barmaja@01barmaja.com");
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

  public InternetAddress[] convertStringToInternetAddress(String recipients) {
    InternetAddress[] internetArray = null;
    if (recipients != null && !recipients.equals("") && !recipients.isEmpty()) {
      String[] recipientArray = recipients.split(",");
      internetArray = new InternetAddress[recipientArray.length];

      for (int i = 0; i < recipientArray.length; i++) {
        try {
        	String address = recipientArray[i] != null ? recipientArray[i].trim() : "";
        	if(!Utils.isStringEmpty(address)) {
        		internetArray[i] = new InternetAddress(address);
        	}
        } catch (AddressException e) {
          e.printStackTrace();
        }
      }
    }

    return internetArray;
  }

  public InternetAddress[] getRecipientsMime() {
    InternetAddress[] internetArray = convertStringToInternetAddress(getRecipients());
    return internetArray;
  }

  public void setRecipients(String to) {
    setPropertyString(FLD_RECIPIENTS, to);
  }

  public String getcc() {
  	return getPropertyString(FLD_CC);
  }
  
  public InternetAddress[] getCCMime() {
    InternetAddress[] internetArray = convertStringToInternetAddress(getcc());
    return internetArray;
  }

  public void setcc(String cc) {
    setPropertyString(FLD_CC, cc);
  }
  
  public String getBcc() {
    return getPropertyString(FLD_BCC);
  }
  
  public InternetAddress[] getBCCMime() {
    InternetAddress[] internetArray = convertStringToInternetAddress(getBcc());
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
    if (emailAccount.getEncryptionType() == EMailAccount.ENCRYPTION_TYPE_SSL) {
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", emailAccount.getPort());//Try 25

    //Outgoing server (SMTP) 25, 587, or 2587 (to connect using STARTTLS), or 465 or 2465 (to connect using TLS Wrapper).
    if (emailAccount.getEncryptionType() != EMailAccount.ENCRYPTION_TYPE_NONE) {
	    if(emailAccount.getPort() == 25 || emailAccount.getPort() == 587 || emailAccount.getPort() == 2587){
	    	props.put("mail.smtp.starttls.enable", "true");
	    	props.put("mail.smtp.starttls.required", "true");
	    }
    }
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
    	boolean html = false;
    	if(getTemplate() != null) html = getTemplate().isHTML(); 
    			
    	if(html) mime.setHeader("Content-Type", "text/html");
    	
      mime.setRecipients(Message.RecipientType.TO, getRecipientsMime());
      mime.setRecipients(Message.RecipientType.BCC, getBCCMime());
      mime.setRecipients(Message.RecipientType.CC, getCCMime());
      mime.setFrom(new InternetAddress(emailAccount.getSender()));
      mime.setSubject(getSubject(), "UTF-8");
      
      mime.setText(getText(), "UTF-8");
      Globals.logString("Subject: "+getSubject());
      Globals.logString("Text: "+getText());
      
      if(html) {
      	mime.setContent(getText(), "text/html; charset=UTF-8");
      } else {
        MimeMultipart mimeMultipart = getMimeMultipart();
  			if(mimeMultipart != null){
  				mime.setContent(mimeMultipart);
  			}
      }
			
    } catch (AddressException e) {
      Globals.logException(e);
    } catch (MessagingException e) {
    	Globals.logException(e);
    }
    mimeMessage = mime;
    return mime;
  }

  /*
   try {
   
   mime.setHeader("Content-Type", "text/html");
   
   mime.setRecipients(Message.RecipientType.TO, getRecipientsMime());
   mime.setRecipients(Message.RecipientType.BCC, getBCCMime());
   mime.setRecipients(Message.RecipientType.CC, getCCMime());
   mime.setFrom(new InternetAddress(emailAccount.getSender()));
   mime.setSubject(getSubject(), "UTF-8");

   //mime.setText(getText(), "UTF-8");

   // Globals.logString("Subject: "+getSubject());
   // Globals.logString("Text: "+getText());

   mime.setContent(getText(), "text/html; charset=UTF-8");

   //MimeMultipart mimeMultipart = getMimeMultipart();
   //if (mimeMultipart != null) {
    //mime.setContent(mimeMultipart);
    //mime.setContent(mimeMultipart, "text/html; charset=UTF-8");
   //}
   
  } catch (AddressException e) {
   Globals.logException(e);
  } catch (MessagingException e) {
   Globals.logException(e);
  }
   */
  
  public void send() {
    try {
    	if(ConfigInfo.isUseLocalEmailClientForNotification()){
    		send_UsingLocalEmailClient();
    	}else{
    		sendWithException();
    	}
    	notifyEmailLaunchStatusHandlerIfExists(false);
    } catch (Exception e1) {
    	notifyEmailLaunchStatusHandlerIfExists(true);
    	Globals.logException(e1);
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
  
  private byte[] inputStreamToByteArray(InputStream inputStream){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try{
			int byteRead;
			while((byteRead = inputStream.read()) != -1) {
				byteArrayOutputStream.write(byteRead);
			}
			byteArrayOutputStream.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}
  
  private MimeMultipart getMimeMultipart(){
		MimeMultipart multipart = new MimeMultipart("related");
		try {
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			
			String htmlText = getText();
			//If we use this setContent(htmlText, "text/html"); we will lose the text format
//			messageBodyPart.setContent(htmlText, "text/html");
			//So to keep text formatted we should use setText(htmlText); method
			messageBodyPart.setText(htmlText, "UTF-8");
			multipart.addBodyPart(messageBodyPart);

			//If the Template comes with a report template we add that template to the attachment
			if(getTemplate() != null && !Utils.isStringEmpty(getTemplate().getPrintFileName())){
				PrnLayout layout = PrnLayoutDesc.getInstance().findPrnLayout(getTemplate().getPrintFileName());
				if(layout != null){
					IFocData focData = getEmailFocData();
					if(focData instanceof FocObject){
						PrintingAction printingAction = ((FocObject) focData).getThisFocDesc().newPrintingAction();
		  			printingAction.setObjectToPrint(focData);
		  			printingAction.initLauncher();
						layout.attachToEmail(this, printingAction, true);
					}
				}
		  }
			
			if(attachmentsOrder != null) {
				for(int i=0; i<attachmentsOrder.size(); i++) {
					String repname = attachmentsOrder.get(i);
					EmailAttachements imgAtt = attachmentsMap.get(repname);
					if(imgAtt != null) {
						InputStream imageStream = imgAtt.getInputStream();
						if(imageStream != null){
							messageBodyPart = new MimeBodyPart();
							messageBodyPart.setFileName(imgAtt.getName());
							DataSource fds = new ByteArrayDataSource(inputStreamToByteArray(imageStream), imgAtt.getMimeType());
							DataHandler dataHandler = new DataHandler(fds);
				      messageBodyPart.setDataHandler(dataHandler);
				      messageBodyPart.setHeader("Content-Type", fds.getContentType()); 
				      messageBodyPart.setHeader("Content-Transfer-Encoding", "base64"); 
				      messageBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);	      
				      multipart.addBodyPart(messageBodyPart);	      
						}						
					}
				}
			}
			
//      This Creates The noname.txt file
//			messageBodyPart = new MimeBodyPart();
//			messageBodyPart.setText(getText());
//			multipart.addBodyPart(messageBodyPart);
	  } catch (Exception e) {
	      Globals.logException(e);
	  }
		return multipart;
	}
  
  private void send_UsingLocalEmailClient(){
  	if(getText() != null){
	  	String text = getText().replaceAll("\n", "%0D%0A");
	  	text = text.replace('\'', '-');
	  	String recipients = getRecipients() != null ? getRecipients().replaceAll(" ", "") : "";
	  	String javaScript = "var win = window.open('mailto:"+recipients+"?body="+text+"', '_blank'); win.close();";
	    JavaScript.getCurrent().execute(javaScript);
  	}
  }
  
  public void addAttachment(String name, InputStream inputStream, String mimeType){
		if(attachmentsMap == null) {
			attachmentsMap = new HashMap<String, FocNotificationEmail.EmailAttachements>();
		}
		if(attachmentsOrder == null) {
			attachmentsOrder = new LinkedList<String>();
		}
		if(!attachmentsOrder.contains(name)) {
			attachmentsOrder.add(name);
		}
		attachmentsMap.put(name, new EmailAttachements(name, inputStream, mimeType));
	}
	
	public class EmailAttachements {
		private String      name        = null;
		private String      mimeType    = null;
		private InputStream inputStream = null;

		public EmailAttachements(String name, InputStream inputStream, String mimeType){
			this.name        = name;
			this.mimeType    = mimeType;
			this.inputStream = inputStream;
		}

		public String getName() {
			return name;
		}

		public String getMimeType() {
			return mimeType;
		}

		public InputStream getInputStream() {
			return inputStream;
		}
	}
	
	private void notifyEmailLaunchStatusHandlerIfExists(boolean error){
		if(getFocNotificationEmailLaunchStatus() != null){
			getFocNotificationEmailLaunchStatus().emailSendStatusHandler(getEmailFocData(), error);
		}
	}

	public IFocNotificationEmailLaunchStatus getFocNotificationEmailLaunchStatus() {
		return iFocNotificationEmailLaunchStatus;
	}

	public void setFocNotificationEmailSendStatus(IFocNotificationEmailLaunchStatus iFocNotificationEmailLaunchStatus) {
		this.iFocNotificationEmailLaunchStatus = iFocNotificationEmailLaunchStatus;
	}
}
