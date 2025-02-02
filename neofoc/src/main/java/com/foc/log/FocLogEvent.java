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
package com.foc.log;

import java.util.Date;

public interface FocLogEvent {
	public static final int EVENT_NONE            = 0;
	public static final int EVENT_SIGNATURE       = 1;
	public static final int EVENT_CREATION        = 2;
	public static final int EVENT_CANCELLATION    = 3;
	public static final int EVENT_MODIFICATION    = 4;
	public static final int EVENT_APPROVED        = 5;
	public static final int EVENT_CLOSED          = 6;
	public static final int EVENT_UNDO_SIGNATURE  = 7;
	public static final int EVENT_CUSTOM          = 8;
	public static final int EVENT_COMMENT         = 9;
	public static final int EVENT_REJECT          = 10;
	public static final int EVENT_OPENED          = 11;

	public static final int STATUS_EXCLUDED       =  0;
	public static final int STATUS_INCLUDED       =  1;
	public static final int STATUS_POSTED         =  2;
	public static final int STATUS_COMMITTED      =  3;
	public static final int STATUS_ERROR_START    =100;
	public static final int STATUS_ERROR_END      =120;
	
	//This indicates the Log Event primary key itself. It is not unique. 
	//If becomes unique when combined with the [EntityName]
	//Should be used to tell FOC if the Log line has been successfully committed or not
	public long   logEvent_GetLogEventReference();
	
	//Entity identification
	public String logEvent_GetEntityName();
	public long   logEvent_GetEntityReference();
	
	//Transaction identification (applicable when the Entity is a transaction) 
	public String logEvent_GetEntityCompanyName();
	public String logEvent_GetEntitySiteName();
	public String logEvent_GetEntityCode();
	public Date   logEvent_GetEntityDate();
	
	//Event Info
	public int    logEvent_GetEventType();
	public String logEvent_GetUsername();
	public Date   logEvent_GetDateTime();
	public String logEvent_GetComment();
	public String logEvent_GetChanges();
	public String logEvent_GetChangesCompressed();
	public String logEvent_GetDocumentZipped();
	public String logEvent_GetDocumentZippedCompressed();
	public String logEvent_GetDocumentHash();
	public int    logEvent_GetDocumentVersion();
	public int    logEvent_GetStatus();
}
