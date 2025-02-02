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
package com.foc.business.workflow.signing;

import com.foc.desc.*;
import com.foc.list.filter.*;

/**
 * @author 01Barmaja
 */
public class WFTransWrapperFilter extends FocListFilterBindedToList{

  public WFTransWrapperFilter(FocConstructor constr) {
    super(constr);
    newFocProperties();
    setFilterLevel(FocListFilter.LEVEL_MEMORY);
  }

  /*
  public void setIsDriver(int operation, boolean lock){
    BooleanCondition transactionTypeCondition = (BooleanCondition) findFilterCondition(WFTransWrapperFilterDesc.COND_IS_DRIVER);
    if(lock){
      transactionTypeCondition.forceToValue(this, operation);
    }else{
      transactionTypeCondition.setValue(this, operation);  
    }
  }

  public void setIsStoreKeeper(int operation, boolean lock){
    BooleanCondition transactionTypeCondition = (BooleanCondition) findFilterCondition(WFTransWrapperFilterDesc.COND_IS_STORE_KEEPER);
    if(lock){
      transactionTypeCondition.forceToValue(this, operation);
    }else{
      transactionTypeCondition.setValue(this, operation);  
    }
  }
  */
}



