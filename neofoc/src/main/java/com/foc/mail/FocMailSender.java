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
package com.foc.mail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.foc.Globals;
import com.foc.util.FocFileUtil;

public class FocMailSender {

	public static final int ENCRYPTION_TYPE_NONE = 0;
	public static final int ENCRYPTION_TYPE_SSL  = 1;
	public static final int ENCRYPTION_TYPE_TLS  = 2;

	private ArrayList<String> toAddresses  = null;
	private ArrayList<String> ccAddresses  = null;
	private ArrayList<String> bccAddresses = null;
	
	//private String to   = "antoines@01barmaja.com";
	private String from = "hussein.tahtah@01barmaja.com";
	private String host = "smtp.gmail.com";
	private String port = "465";
			
	private int    encryptionConnectionType = ENCRYPTION_TYPE_SSL;
	
	private String username = "";
	private String password = "";
	
	private String subject = "Testing Subject";
	private String message = "Dear Mail Crawler,\n\n No spam to my email, please!";
	
	private HashMap<String,ImageAttachements> imageMap = null;
	
	public FocMailSender(){
		toAddresses  = new ArrayList<String>();
		ccAddresses  = new ArrayList<String>();
		bccAddresses = new ArrayList<String>();
	}
	
	public void dispose(){
		if(toAddresses != null){
			toAddresses.clear();
			toAddresses = null;
		}
		if(bccAddresses != null){
			bccAddresses.clear();
			bccAddresses = null;
		}
		if(ccAddresses != null){
			ccAddresses.clear();
			ccAddresses = null;
		}
	}
	
	public void addTo(String to){
		if(toAddresses != null ) toAddresses.add(to);
	}

	public void addCC(String to){
		if(ccAddresses != null ) ccAddresses.add(to);
	}

	public void addBcc(String to){
		if(bccAddresses != null ) bccAddresses.add(to);
	}

	public void send(String username, String password) {
		setUsername(username);
		setPassword(password);

		send();
	}
	
	public void send() {
		Globals.logString("Before Filling the properties");
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", getHost());
		props.put("mail.smtp.socketFactory.port", getPort());
		if(getEncryptionConnectionType() == ENCRYPTION_TYPE_SSL){
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", getPort());

		Globals.logString("After Filling the properties");
		Globals.logString("Before creating the session");
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				Globals.logString("Before Authentication callback");
				PasswordAuthentication pwa = new PasswordAuthentication(getUsername(), getPassword());
				Globals.logString("After  Authentication callback");
				return pwa;
			}
		});
		Globals.logString("After creating the session");
		
		try{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));

			setRecipients(message, Message.RecipientType.TO , toAddresses );
			setRecipients(message, Message.RecipientType.CC , ccAddresses );
			setRecipients(message, Message.RecipientType.BCC, bccAddresses);
			
			message.setSubject(getSubject());
			message.setText(getMessage());
			
			MimeMultipart mimeMultipart = getMimeMultipart();
			if(mimeMultipart != null){
				message.setContent(mimeMultipart);
			}
			Globals.logString("Before Transport Send");
			Transport.send(message);
			Globals.logString("After Transport Send");
			
		}catch (MessagingException e){
			Globals.logString("In the Exception");
			Globals.logException(e);
		}
	}
	
	private void setRecipients(Message message, RecipientType recipientType, ArrayList<String> toAddresses) throws MessagingException {
		for(int i=0; i<toAddresses.size(); i++){
			if(i == 0){
				message.setRecipients(recipientType, InternetAddress.parse(toAddresses.get(i)));
			}else{
				message.addRecipients(recipientType, InternetAddress.parse(toAddresses.get(i)));
			}
		}
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getEncryptionConnectionType() {
		return encryptionConnectionType;
	}

	public void setEncryptionConnectionType(int encryptionConnectionType) {
		this.encryptionConnectionType = encryptionConnectionType;
	}
	/*
	private InputStream getImageInputStream(){
		return inputStream;
	}
	
	private String getImageMimeType(){
		return mimeType;
	}
	*/
	
	private MimeMultipart getMimeMultipart(){
		MimeMultipart multipart = new MimeMultipart("related");
		try {
			BodyPart messageBodyPart = new MimeBodyPart();
			
			String htmlText = getMessage();//"<H1>" + getSubject() + "</H1><img src=\"cid:image\">";
			messageBodyPart.setContent(htmlText, "text/html");
			multipart.addBodyPart(messageBodyPart);
			
			if(imageMap != null){
				Iterator<ImageAttachements> iter = imageMap.values().iterator();
				while(iter != null && iter.hasNext()){
					ImageAttachements imgAtt = iter.next();
					if(imgAtt != null){
						InputStream imageStream = imgAtt.getInputStream();
						if(imageStream != null){
							messageBodyPart = new MimeBodyPart();
							DataSource fds = new ByteArrayDataSource(inputStreamToByteArray(imageStream), imgAtt.getMimeType());
							DataHandler dataHandler = new DataHandler(fds);
				      messageBodyPart.setDataHandler(dataHandler);
				      messageBodyPart.setHeader("Content-ID","<"+imgAtt.getName()+">");
				      messageBodyPart.setDisposition(MimeBodyPart.INLINE);	      
				      multipart.addBodyPart(messageBodyPart);	      
						}						
					}
				}
			}
			
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(getMessage());
			multipart.addBodyPart(messageBodyPart);
	  } catch (Exception e) {
	      e.printStackTrace();
	  }
		return multipart;
	}
		
	public byte[] inputStreamToByteArray(InputStream inputStream){
		return FocFileUtil.inputStreamToByteArray(inputStream);
	}

	public void addImage(String name, InputStream inputStream, String mimeType){
		if(imageMap == null){
			imageMap = new HashMap<String, FocMailSender.ImageAttachements>();
		}
		imageMap.put(name, new ImageAttachements(name, inputStream, mimeType));
	}
	
	public class ImageAttachements {
		private String      name        = null;
		private String      mimeType    = null;
		private InputStream inputStream = null;

		public ImageAttachements(String name, InputStream inputStream, String mimeType){
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
}
