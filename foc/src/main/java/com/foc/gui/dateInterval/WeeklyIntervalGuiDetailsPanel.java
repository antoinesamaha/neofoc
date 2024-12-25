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
package com.foc.gui.dateInterval;

import java.awt.Component;
import java.sql.Date;

import com.foc.desc.FocObject;
import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class WeeklyIntervalGuiDetailsPanel extends FPanel{

  private DateInterval stockreport = null;
  
  public WeeklyIntervalGuiDetailsPanel(FocObject object, int viewID){
    super("", FPanel.FILL_NONE);   
    stockreport = (DateInterval)object;
    
    setName("By Week");
    add(stockreport, DateIntervalDesc.FLD_WEEK, 0, 0);
    add(stockreport, DateIntervalDesc.FLD_YEAR, 2, 0);
    Component comp = add(stockreport, DateIntervalDesc.FLD_FDATE, 0, 1);
    comp.setEnabled(false);
    comp = add(stockreport, DateIntervalDesc.FLD_LDATE, 2, 1);
    comp.setEnabled(false);
    
  }
  
  public void dispose(){
    super.dispose();
    stockreport = null;
  }

  public DateInterval getStockreport() {
    return stockreport;
  }
  
  protected Date getFirstDate(){
    return stockreport.getPropertyDate(DateIntervalDesc.FLD_FDATE); 
  }
  
  protected Date getLastDate(){
    return stockreport.getPropertyDate(DateIntervalDesc.FLD_LDATE); 
  }
  
}
