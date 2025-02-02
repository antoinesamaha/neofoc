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
package com.foc.access;

import com.foc.desc.FocDesc;
import com.foc.desc.field.FBoolField;
import com.foc.desc.field.FStringField;
import com.foc.desc.field.FDateTimeField;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceField;

public class FocLogLineDesc extends FocDesc implements FocLogLineConst {
  
  public FocLogLineDesc() {
    super(FocLogLine.class, FocDesc.NOT_DB_RESIDENT, "LOGGER", false);
    FField focFld = addReferenceField();

    addOrderField();
    setWithObjectTree();
    
    focFld = new FDateTimeField("DATE_TIME", "Time", FLD_DATE_TIME, false);
    addField(focFld);

    FMultipleChoiceField mFld = new FMultipleChoiceField("TYPE", "Type", FLD_TYPE, false, 2);
    addField(mFld);
    mFld.addChoice(TYPE_INFO   , "Info"   );
    mFld.addChoice(TYPE_TEST   , "Test"   );
    mFld.addChoice(TYPE_COMMAND, "Command");
    mFld.addChoice(TYPE_ERROR  , "Error"  );
    mFld.addChoice(TYPE_WARNING, "Warning");
    mFld.addChoice(TYPE_FAILURE, "Failure");

    focFld = new FStringField("MESSAGE", "Message", FLD_MESSAGE, false, LEN_MESSAGE);
    addField(focFld);
    
    FBoolField boolFld = new FBoolField("SUCCESSFUL", "Successful", FLD_SUCCESSFUL, false);
    addField(boolFld);
  }

  private static FocDesc focDesc = null;
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new FocLogLineDesc();
    }
    return focDesc;
  }

}
