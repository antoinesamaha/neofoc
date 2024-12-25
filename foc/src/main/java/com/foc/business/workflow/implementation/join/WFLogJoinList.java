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

import com.foc.business.workflow.implementation.WFLogDesc;
import com.foc.db.SQLFilter;
import com.foc.list.FocLinkJoinRequest;
import com.foc.list.FocListOrder;
import com.foc.list.FocListWithFilter;
import com.foc.log.FocLogEvent;

@SuppressWarnings("serial")
public class WFLogJoinList extends FocListWithFilter {

	public WFLogJoinList(WFLogJoinFilterDesc filterDesc, WFLogJoinDesc joinDesc){
		super(filterDesc, new FocLinkJoinRequest(joinDesc.getFocRequestDesc()));
		setListOrder(new FocListOrder(WFLogDesc.FLD_DATE_TIME));
	}

	public void setFilter_ByStatus(){
		SQLFilter filter = getFilter();
		filter.putAdditionalWhere("STATUS", "EVT_STATUS="+FocLogEvent.STATUS_INCLUDED+" OR EVT_STATUS="+FocLogEvent.STATUS_POSTED+" OR (EVT_STATUS>="+FocLogEvent.STATUS_ERROR_START+" AND EVT_STATUS<"+FocLogEvent.STATUS_ERROR_END+")");
	}
	
}
