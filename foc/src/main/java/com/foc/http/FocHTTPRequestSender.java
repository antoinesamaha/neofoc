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
package com.foc.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class FocHTTPRequestSender {

	private String message = null;
	private String userName = "NICOLAS";//Leave this for MCC test NICOLAS
	private String password = "nicolas123";//Leave this for MCC test nicolas123
	private boolean fromMobile = false;
	private String url = "http://localhost:8080/everproWebServer/FocLink/";
	
	public FocHTTPRequestSender(String message){
		this.message = message;
	}
	
	public String send(){
		return send(null);
	}
	
	public String send(HashMap<String, String> additionalParams){
		String returnedString = null;
		HttpURLConnection connection = null;
		HttpURLConnection ackConnection = null;
		BufferedReader serverResponse = null;
		try {
			// OPEN CONNECTION
			connection = (HttpURLConnection) new URL(url).openConnection();
//			connection = (HttpURLConnection) new URL("http://webmail.mcc-lb.com:8081/everpro/FocLink").openConnection();

			// SET REQUEST INFO
			connection.setRequestMethod("POST");

			if(additionalParams != null){
				Iterator<String> iter = (Iterator<String>) additionalParams.keySet().iterator();
				while(iter != null && iter.hasNext()){
					String key = iter.next();
					connection.setRequestProperty(key, additionalParams.get(key));
				}
			}
			connection.setDoOutput(true);

			if(!isFromMobile()){
				connection.getOutputStream().write(message.getBytes());
			}

			InputStream is = connection.getInputStream();
			returnedString = fromStream(is);
			
			connection.disconnect();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (ackConnection != null) {
				ackConnection.disconnect();
			}
			if (serverResponse != null) {
				try {
					serverResponse.close();
				} catch (Exception ex) {
				}
			}
		}
		return returnedString;
	}
	
	public static String fromStream(InputStream in) {
		StringBuilder out = new StringBuilder();
		try{
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	    }
		}catch(Exception e){
			e.printStackTrace();
		}
	  return out.toString();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isFromMobile() {
		return fromMobile;
	}

	public void setFromMobile(boolean fromMobile) {
		this.fromMobile = fromMobile;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
