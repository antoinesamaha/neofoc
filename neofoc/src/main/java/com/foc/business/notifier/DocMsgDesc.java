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

import com.foc.admin.FocUserDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FDateField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocList;

public class DocMsgDesc extends FocDesc{

	public final static int FLD_SENDER          = 1;
	public final static int FLD_RECEIVER        = 2;
	public final static int FLD_SENT            = 3;
	public final static int FLD_SEND_DATE       = 4;
	public final static int FLD_IS_READ         = 5;
	public final static int FLD_READ_DATE       = 6;
	public final static int FLD_DOC_MSG_CONTENT = 7;
	
	public static final String DB_TABLE_NAME = "DOC_MSG";
	
  public DocMsgDesc(){
    super(DocMsg.class, FocDesc.DB_RESIDENT, DB_TABLE_NAME, false);
    
    addReferenceField();
    
    FObjectField fObjectFld = new FObjectField("SENDER", "Sender", FLD_SENDER, FocUserDesc.getInstance());
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    addField(fObjectFld);
    
    fObjectFld = new FObjectField("RECEIVER", "Receiver", FLD_RECEIVER, FocUserDesc.getInstance());
    fObjectFld.setComboBoxCellEditor(FocUserDesc.FLD_NAME);
    fObjectFld.setDisplayField(FocUserDesc.FLD_NAME);
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    addField(fObjectFld);
    
    FBoolField boolField = new FBoolField("SENT", "Sent", FLD_SENT, false);
    addField(boolField);
    
    FDateField dateField = new FDateField("SEND_DATE", "Send Date", FLD_SEND_DATE, false);
    addField(dateField);
    
    boolField = new FBoolField("IS_READ", "Is Read", FLD_IS_READ, false);
    addField(boolField);
    
    dateField = new FDateField("READ_DATE", "Read Date", FLD_READ_DATE, false);
    addField(dateField);
    
    fObjectFld = new FObjectField("DOC_MSG_CONTENT", "Doc. Msg. Content", FLD_DOC_MSG_CONTENT, DocMsgContentDesc.getInstance());
    fObjectFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    fObjectFld.setSelectionList(FocUserDesc.getList(FocList.NONE));
    addField(fObjectFld);
  }
  
  public static FocList getList(int mode){
  	return getInstance().getFocList(mode);
  }
  
  @Override
  public FocList newFocList(){
  	FocList list = super.newFocList();
  	if(list != null){
	    list.setDirectlyEditable(false);
	    list.setDirectImpactOnDatabase(true);
  	}
    return list;
  }
  
  public static DocMsgDesc getInstance(){
		return (DocMsgDesc) getInstance(DB_TABLE_NAME, DocMsgDesc.class);
	}
}
