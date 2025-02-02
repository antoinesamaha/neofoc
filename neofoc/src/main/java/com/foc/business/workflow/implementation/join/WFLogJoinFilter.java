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
package com.foc.business.workflow.implementation.join;

import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.list.filter.FocListFilter;
import com.foc.list.filter.FocListFilterBindedToList;

@SuppressWarnings("serial")
public class WFLogJoinFilter extends FocListFilterBindedToList {

  public WFLogJoinFilter(FocDesc filterDesc) {
  	super(new FocConstructor(filterDesc, null));
  }
  
  public WFLogJoinFilter(FocConstructor constr) {
    super(constr);
    newFocProperties();
    setFilterLevel(FocListFilter.LEVEL_DATABASE);
  }
}



