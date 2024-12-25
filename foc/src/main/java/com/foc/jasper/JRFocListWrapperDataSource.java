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
package com.foc.jasper;

import com.foc.dataWrapper.FocListWrapper;
import com.foc.desc.FocObject;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class JRFocListWrapperDataSource implements JRDataSource {

  private FocListWrapper list       = null;
  private int       	   i          = 0   ;
  
  public JRFocListWrapperDataSource(FocListWrapper list){
    this.list = list;
    i = 0;
  }
  
  public void dispose(){
  	list = null;
  }
  
  public FocObject getCurrentFocObject(){
  	return i-1 < list.size() ? list.getAt(i-1) : null;
  }

  @Override
  public Object getFieldValue(JRField jrField) throws JRException {
  	return JRFocListDataSource.getFieldValue_Static(getCurrentFocObject(), i, jrField);
  }

  @Override
  public boolean next() throws JRException {
  	boolean next = list != null;
  	if(next){
  		i++;
	  	next = i < list.size();
  	}
    return next;
  }
}
