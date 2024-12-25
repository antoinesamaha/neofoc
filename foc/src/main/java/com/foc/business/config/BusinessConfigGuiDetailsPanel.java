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
package com.foc.business.config;

import java.awt.GridBagConstraints;

import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class BusinessConfigGuiDetailsPanel extends FPanel {
  
  public BusinessConfigGuiDetailsPanel(BusinessConfig basicsConfig, int view){
    super("Business Configuration", FPanel.FILL_NONE);    
    
    int y = 0;
            
    FPanel partyCoding = new FPanel();
    partyCoding.setBorder("Party coding");
    add(partyCoding, 0, y++, 5, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE);
    
    y = 0;

    partyCoding.add(basicsConfig, BusinessConfigDesc.FLD_ADR_BOOK_PARTY_PREFIX, 0, y++);
    partyCoding.add(basicsConfig, BusinessConfigDesc.FLD_ADR_BOOK_PARTY_SEPERATOR, 0, y++);
    partyCoding.add(basicsConfig, BusinessConfigDesc.FLD_ADR_BOOK_PARTY_NBR_DIGITS, 0, y++);
    partyCoding.add(basicsConfig, BusinessConfigDesc.FLD_ADR_BOOK_PARTY_RESET_NUMBERING_WHEN_PREFIX_CHANGE, 1, y++);
  }
}
