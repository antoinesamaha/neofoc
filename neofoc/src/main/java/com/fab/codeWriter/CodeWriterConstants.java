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
package com.fab.codeWriter;

public interface CodeWriterConstants {
	public static final String INTERMEDIATE_PACKAGE_CLIENT = "client";
	public static final String INTERMEDIATE_PACKAGE_SERVER = "server";

	public static final String CLASS_NAME_SUFFIX_CONSTANT               = "Const";
	public static final String CLASS_NAME_SUFFIX_FOC_DESC               = "Desc";
	public static final String CLASS_NAME_SUFFIX_WEB_CLIENT             = "Clt";
	public static final String CLASS_NAME_SUFFIX_PROXY                  = "Proxy";
	public static final String CLASS_NAME_SUFFIX_PROXY_DESC             = "ProxyDesc";
	public static final String CLASS_NAME_SUFFIX_PROXY_LIST             = "ProxyList";
	public static final String CLASS_NAME_SUFFIX_PROXY_LIST_PANEL       = "ProxyListPanel";
	public static final String CLASS_NAME_SUFFIX_FOC_LIST               = "FocList";
	public static final String CLASS_NAME_SUFFIX_FAB_DECLARATION        = "FabDeclaration";
	public static final String CLASS_NAME_SUFFIX_SERVICE                = "Service";
	public static final String CLASS_NAME_SUFFIX_SERVICE_ASYNC          = "ServiceAsync";
	public static final String CLASS_NAME_SUFFIX_SERVICE_IMPLEMENTATION = "ServiceImpl";
	public static final String CLASS_NAME_SUFFIX_SERVICE_INSTANCE       = "ServiceInstance";
	public static final String CLASS_NAME_SUFFIX_DETAIL_PANEL           = "ProxyGuiDetailsPanel";
	public static final String CLASS_NAME_SUFFIX_PROXY_GUI_TABLE        = "ProxyGuiTable";
	
	public static final String PACKAGE_NAME_AUTO_GEN              = "autoGen";
	public static final String CLASS_NAME_PREFIX_AUTO_GEN         = "AutoGen_";
	public static final String FLD_CONSTANT_PREFIX                = "FLD_";
	
	public static final String XML_VIEW_FILE_NAME_SEPERATOR       = "^";
	public static final String XML_VIEW_TABLE_SUFFIX              = XML_VIEW_FILE_NAME_SEPERATOR+"Table";
	public static final String XML_VIEW_FORM_SUFFIX               = XML_VIEW_FILE_NAME_SEPERATOR+"Form";
}
