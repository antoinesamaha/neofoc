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

import com.foc.admin.FocUserDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FObjectField;
import com.foc.util.Utils;

public class FocLinkOutBoxDetailDesc extends FocDesc{
	public static final String DB_TABLE_NAME = "FOC_LINK_OUT_LOG_DETAILS";
	
	public static final int FLD_TO_USER          = 1;
	public static final int FLD_STATUS           = 2;
	public static final int FLD_RECEIVER_COMMENT = 3;
	public static final int FLD_OUT_LOG          = 4;
	
	public static final int    STATUS_POSTED          = 1;
	public static final int    STATUS_SENT            = 2;
	public static final int    STATUS_RECEIVED        = 3;
	public static final int    STATUS_RECEPTION_ERROR = 4;
	public static final String STATUS_POSTED_VALUE          = "Posted";
	public static final String STATUS_SENT_VALUE            = "Sent";
	public static final String STATUS_RECEIVED_VALUE        = "Received";
	public static final String STATUS_RECEPTION_ERROR_VALUE = "Reception Error";
	
	public FocLinkOutBoxDetailDesc(){
		super(FocLinkOutBoxDetail.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
		
		addReferenceField();
		
		FObjectField oFld = new FObjectField(FocUserDesc.DB_TABLE_NAME, "To User", FLD_TO_USER, FocUserDesc.getInstance());
		addField(oFld);
		
		FMultipleChoiceField mFld = new FMultipleChoiceField("STATUS", "Status", FLD_STATUS, false, 2);
		mFld.addChoice(STATUS_POSTED, STATUS_POSTED_VALUE);
		mFld.addChoice(STATUS_SENT, STATUS_SENT_VALUE);
		mFld.addChoice(STATUS_RECEIVED, STATUS_RECEIVED_VALUE);
		mFld.addChoice(STATUS_RECEPTION_ERROR, STATUS_RECEPTION_ERROR_VALUE);
		addField(mFld);
		
		FStringField cFld = new FStringField("RECEIVER_COMMENT", "Receiver Comment", FLD_RECEIVER_COMMENT, false, 200);
		addField(cFld);
		
		oFld = new FObjectField("OUT_LOG", "Out Log", FLD_OUT_LOG, FocLinkOutBoxDesc.getInstance(), this, FocLinkOutBoxDesc.FLD_DETAILS_LIST);
		oFld.setSelectionList(null);
		oFld.setWithList(false);
		addField(oFld);
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
	public static FocLinkOutBoxDetailDesc getInstance() {
		return (FocLinkOutBoxDetailDesc) getInstance(DB_TABLE_NAME, FocLinkOutBoxDetailDesc.class);    
	}
	
	public static Integer mapStatusStringToStatusInteger(String statusString){
		Integer status = null;

		if(Utils.isStringEmpty(statusString)){
			status = STATUS_POSTED;
		}
		else if(statusString.equals(STATUS_POSTED_VALUE)){
			status = STATUS_POSTED;
		}
		else if(statusString.equals(STATUS_SENT_VALUE)){
			status = STATUS_SENT;
		}
		else if(statusString.equals(STATUS_RECEIVED_VALUE)){
			status = STATUS_RECEIVED;
		}
		else if(statusString.equals(STATUS_RECEPTION_ERROR_VALUE)){
			status = STATUS_RECEPTION_ERROR;
		}

		return status;
	}
}
