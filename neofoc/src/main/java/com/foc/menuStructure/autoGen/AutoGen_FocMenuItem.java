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
package com.foc.menuStructure.autoGen;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.menuStructure.FocMenuItemConst;

@SuppressWarnings("serial")
public class AutoGen_FocMenuItem extends FocObject implements FocMenuItemConst {
  
  public AutoGen_FocMenuItem(FocConstructor constr){
    super(constr);
    newFocProperties();
  }

  public String getCode(){
    return (String) getPropertyString(FLD_CODE);
  }

  public void setCode(String obj){
    setPropertyString(FLD_CODE, obj);
  }

  public String getTitle(){
    return (String) getPropertyString(FLD_TITLE);
  }

  public void setTitle(String obj){
    setPropertyString(FLD_TITLE, obj);
  }

  public String getHelp(){
    return (String) getPropertyString(FLD_HELP);
  }

  public void setHelp(String obj){
    setPropertyString(FLD_HELP, obj);
  }
  
  public String getExtraAction0(){
    return getPropertyString(FLD_EXTRA_ACTION_0);
  }
  
  public void setExtraAction0(String extraAction0){
    setPropertyString(FLD_EXTRA_ACTION_0, extraAction0);
  }
  
  public String getExtraActionByIndex(int index){
    String result = null;
    if(index < FLD_EXTRA_COUNT){
      result = getPropertyString(FLD_EXTRA_ACTION_0 + index);  
    }
    return result;
  }
  
  public void setExtraActionByIndex(int index, String extraAction){
    if(index < FLD_EXTRA_COUNT){
      setPropertyString(FLD_EXTRA_ACTION_0 + index , extraAction);
    }
  }
}
