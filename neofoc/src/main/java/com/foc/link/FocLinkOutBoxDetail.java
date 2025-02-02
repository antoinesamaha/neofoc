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
package com.foc.link;

import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;

@SuppressWarnings("serial")
public class FocLinkOutBoxDetail extends FocObject {

	public FocLinkOutBoxDetail(FocConstructor constr) {
		super(constr);
		newFocProperties();
	}
	
	public FocUser getToUser(){
		return (FocUser) getPropertyObject(FocLinkOutBoxDetailDesc.FLD_TO_USER);
	}
	
	public void setToUser(FocUser user){
		setPropertyObject(FocLinkOutBoxDetailDesc.FLD_TO_USER, user);
	}
	
	public int getStatus(){
		return getPropertyMultiChoice(FocLinkOutBoxDetailDesc.FLD_STATUS);
	}
	
	public void setStatus(int status){
		setPropertyMultiChoice(FocLinkOutBoxDetailDesc.FLD_STATUS, status);
	}
	
	public String getReceiverComment(){
		return getPropertyString(FocLinkOutBoxDetailDesc.FLD_RECEIVER_COMMENT);
	}
	
	public void setReceiverComment(String receiverComment){
		setPropertyString(FocLinkOutBoxDetailDesc.FLD_RECEIVER_COMMENT, receiverComment);
	}
	
  public static FocDesc getFocDesc() {
    return FocLinkOutBoxDetailDesc.getInstance();
  }

}
