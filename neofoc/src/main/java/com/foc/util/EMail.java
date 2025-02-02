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
package com.foc.util;

import java.io.IOException;
import java.util.ArrayList;

import com.foc.Globals;

public class EMail {

	public static boolean sendMail(String recepient, String attachment){
		boolean error = false;
		String commanddLine = Globals.getApp().getUser().getSendEmailCommandLine();
		
		ArrayList<String> argArray = new ArrayList<String>();
		argArray.add(commanddLine);
		argArray.add("/c");
		argArray.add("ipm.note");
		if(recepient != null && !recepient.isEmpty()){
			argArray.add("/m");
			argArray.add(recepient);
		}
		if(attachment != null && !attachment.isEmpty()){
			argArray.add("/a");
			argArray.add(attachment);
		}
		
		String[] args = new String[argArray.size()];
		for(int i=0; i<argArray.size(); i++){
			args[i] = argArray.get(i);
		}
		
		try{
			Runtime.getRuntime().exec(args);
		}catch (IOException e){
			error = true;
			Globals.logException(e);
		}
		return error;
	}
	
}
