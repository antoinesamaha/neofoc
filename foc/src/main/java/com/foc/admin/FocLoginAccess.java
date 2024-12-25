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
package com.foc.admin;

import com.foc.Application;
import com.foc.Globals;
import com.foc.util.Encryptor;

public class FocLoginAccess {
	private FocUser user        = null;
	private int     loginStatus = Application.LOGIN_WRONG;
	
	public FocLoginAccess(){
	}
	
	public void dispose(){
		user = null;
	}
	
	public int checkUserPassword(String userName, String encryptedPassword){
		return checkUserPassword(userName, encryptedPassword, true);
	}
	
	public int checkUserPassword(String userName, String encryptedPassword, boolean setUserAsRootUser){
		return checkUserPassword(userName, encryptedPassword, setUserAsRootUser, false);
	}
	
	public int checkUserPassword(String userName, String encryptedPassword, boolean setUserAsRootUser, boolean withRefresh){
  	loginStatus   = Application.LOGIN_WRONG;
    user          = FocUser.findUser(userName);
    String  typedPassword = encryptedPassword;
    
    //We have a case for unset Passwords
    if (user != null){
    	if(withRefresh) user.load();
    		
    	boolean credentialsCorrect = false;
    	if(user.getPassword().isEmpty()){
    		String emptyEncriptionPassword = Encryptor.encrypt_MD5("");    		
    		credentialsCorrect = typedPassword.startsWith(emptyEncriptionPassword);
    	}else if(typedPassword.startsWith(user.getPassword())){
    		credentialsCorrect = true;
    	}    	
    	if(credentialsCorrect && !user.isSuspended() && !user.isLocked()){
        if(setUserAsRootUser) Globals.getApp().setUser(user);
        if (user.isAdmin()) {
        	loginStatus = Application.LOGIN_ADMIN;
        } else {
        	loginStatus = Application.LOGIN_VALID;
        }
    	}
    }
    return loginStatus;
	}

	public FocUser getUser() {
		return user;
	}

	public void setUser(FocUser user) {
		this.user = user;
	}
}
