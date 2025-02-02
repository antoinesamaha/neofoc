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
package com.foc.business.calendar;

import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;

public class FCalendarList extends FocList {

	public FCalendarList() {
		super(new FocLinkSimple(FCalendarDesc.getInstance()));
    setDirectlyEditable(false);
    setDirectImpactOnDatabase(true);
    if(getListOrder() == null){
      FocListOrder order = new FocListOrder(FCalendarDesc.FLD_NAME);
      setListOrder(order);
    }
	}

	@Override
	public void setDirectlyEditable(boolean directlyEditable) {
		super.setDirectlyEditable(directlyEditable);
	}
	
}
