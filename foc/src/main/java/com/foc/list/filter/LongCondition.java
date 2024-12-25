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
//IMPLEMENTED

/*
 * Created on Sep 9, 2005
 */
package com.foc.list.filter;

import com.foc.desc.field.FFieldPath;

/**
 * @author 01Barmaja
 */
public class LongCondition extends NumCondition {
  
  public LongCondition(FFieldPath numFieldPath, String fieldPrefix){
    super(numFieldPath, fieldPrefix);
  }

  public LongCondition(int fieldID){
  	super(fieldID);
  }

  public void setToValue(FocListFilter filter, int operation, long firstValue, long lastValue){
  	setToValue(filter, operation, (double) firstValue, (double) lastValue);
  }

  @Override
  protected String getFirstValueString(FocListFilter filter) {
    double firstValue = getFirstValue(filter);
    long l = (long) firstValue;
  	return "" + l;
  }

  @Override
  protected String getLastValueString(FocListFilter filter) {
    double lastValue = getLastValue(filter);
    long l = (long) lastValue;
  	return "" + l;
  }

}
