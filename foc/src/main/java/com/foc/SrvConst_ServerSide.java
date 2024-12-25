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
package com.foc;

public interface SrvConst_ServerSide {
	//-------------------------------------------
	// Header Keys
	//-------------------------------------------
	public static final String HEADER_KEY_MESSAGE_TYPE = "messageType";
	public static final String HEADER_KEY_MESSAGE      = "message";
	public static final String HEADER_KEY_APPLICATION  = "application";
	public static final String HEADER_KEY_USERNAME     = "username";
	public static final String HEADER_KEY_PASSWORD     = "password";
	
	public static final String APPLICATION_MOBILE      = "mobile";
	public static final String APPLICATION_PHOTO       = "photo";
	
	//-------------------------------------------
	// PREDEFINED MESSAGES
	//-------------------------------------------
	public static final String MSG_TYPE_LOGIN      = "LOGIN";
	public static final String MSG_TYPE_IMAGE      = "IMAGE";
	public static final String MSG_TYPE_DATA       = "DATA";	
	
	//-------------------------------------------
	// JSON Keys and Values
	//-------------------------------------------
	public static final String KEY_COMMAND    = "CMD";	
	public static final String VAL_UPDATE     = "U";
	public static final String VAL_INSERT     = "I";
	public static final String VAL_DELETE     = "D";
	public static final String VAL_SELECT     = "S";
	public static final String VAL_LOGIN      = "LOGIN" ;
	public static final String VAL_PROJECT_FILTER_LIST       = "PROJECT_FILTER_LIST";
	public static final String VAL_WBS_ACTIVE_JOBS_ONLY      = "ACTIVE_JOBS_ONLY";
	public static final String VAL_SNAG_LIST_FROM_WBS        = "SNAG_LIST_FROM_WBS";
	public static final String VAL_SNAG_LIST_FOR_PHOTO_ALBUM = "SNAG_LIST_FOR_PHOTO_ALBUM";
	public static final String VAL_PROJECT_RELATED_CONTACTS  = "PROJECT_RELATED_CONTACTS";

	public static final String LIST_KEY                             = "LIST_KEY";
	public static final String LIST_KEY_MY_TEAMS                    = "MY_TEAMS";
	public static final String LIST_KEY_TEAM_USERS                  = "TEAM_USERS";
	public static final String LIST_KEY_USER_MENU_RIGHTS            = "USER_MENU_RIGHTS";
	public static final String LIST_KEY_RES_TIME_CELL_LAST_2_DAYS   = "RES_TIME_CELL_LAST_2_DAYS";
	public static final String LIST_KEY_WBS_ALL                     = "WBS_ALL";
	public static final String LIST_KEY_BKDN_FILTER_ON_ACTIVITY     = "BKDN_FILTER_ON_ACTIVITY";
	public static final String LIST_KEY_WBS_INSPECTION_AREA         = "WBS_INSPECTION_AREA";
	public static final String LIST_KEY_INSPECTION_AREA_ATTACHMENTS = "INSPECTION_AREA_ATTACHMENTS";
	public static final String LIST_KEY_WBS_ATTACHMENTS             = "LIST_KEY_WBS_ATTACHMENTS";
	
	public static final String KEY_PARENT_STORAGE  = "PARENT_STORAGE";
	public static final String KEY_PARENT_FILTER   = "PARENT_FILTER";
	public static final String KEY_PARENT_PATH_TO_LIST = "PARENT_PATH_TO_LIST";
	
	public static final String KEY_STORAGE         = "STORAGE";
	public static final String KEY_FIELDS          = "FIELDS";
	public static final String KEY_REFERENCE       = "REF";
	public static final String KEY_OBJECT          = "OBJECT";
	public static final String KEY_LIST_KEY        = "LIST_KEY";
	public static final String KEY_EXTRA_PARAMS    = "XTRA_PARAMS";
	public static final String KEY_FORCE_CONTROLER = "FORCE_CONTROLER";
	
	public static final String RESULT         = "RESULT";
	public static final String RES_ERROR      = "ERROR";
	public static final String RES_OK         = "OK";
	
	public static final String FIELD_SELECTION_SEPARATOR = ",";
}
