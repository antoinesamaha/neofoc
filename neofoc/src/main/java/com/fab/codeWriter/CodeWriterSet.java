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

import com.fab.model.table.TableDefinition;

public class CodeWriterSet {

	private TableDefinition tableDefinition = null;
	
	public CodeWriterSet(TableDefinition tableDefinition){
		this.tableDefinition = tableDefinition;
	}
	
	public void dispose(){
		tableDefinition = null;
		
		if(codeWriter_Const != null){
			codeWriter_Const.dispose();
			codeWriter_Const = null;
		}
		
		if(codeWriter_FocDesc != null){
			codeWriter_FocDesc.dispose();
			codeWriter_FocDesc = null;
		}
		
		if(codeWriter_FocObject != null){
			codeWriter_FocObject.dispose();
			codeWriter_FocObject = null;
		}

		if(codeWriter_PojoFocObject != null){
			codeWriter_PojoFocObject.dispose();
			codeWriter_PojoFocObject = null;
		}

		if(codeWriter_Proxy != null){
			codeWriter_Proxy.dispose();
			codeWriter_Proxy = null;
		}
		
		if(codeWriter_ProxyList != null){
			codeWriter_ProxyList.dispose();
			codeWriter_ProxyList = null;
		}
		
		if(codeWriter_GuiProxyPanel != null){
			codeWriter_GuiProxyPanel.dispose();
			codeWriter_GuiProxyPanel = null;
		}
		
		if(codeWriter_GuiProxyListPanel != null){
			codeWriter_GuiProxyListPanel.dispose();
			codeWriter_GuiProxyListPanel = null;
		}
		
		if(codeWriter_GuiProxyListTable != null){
			codeWriter_GuiProxyListTable.dispose();
			codeWriter_GuiProxyListTable = null;
		}
	}
	
	public TableDefinition getTableDefinition(){
		return tableDefinition;
	}
	
	private CodeWriter_Const codeWriter_Const = null;
	public CodeWriter_Const getCodeWriter_Const(){
		if(codeWriter_Const == null) codeWriter_Const = new CodeWriter_Const(this);
		return codeWriter_Const;
	}
	
	private CodeWriter_FocDesc codeWriter_FocDesc = null;
	public CodeWriter_FocDesc getCodeWriter_FocDesc(){
		if(codeWriter_FocDesc == null) codeWriter_FocDesc = new CodeWriter_FocDesc(this);
		return codeWriter_FocDesc;
	}
	
	private CodeWriter_FocObject codeWriter_FocObject = null;
	public CodeWriter_FocObject getCodeWriter_FocObject(){
		if(codeWriter_FocObject == null) codeWriter_FocObject = new CodeWriter_FocObject(this);
		return codeWriter_FocObject;
	}
	
	private CodeWriter_XMLTable codeWriter_XMLTable = null;
	public CodeWriter_XMLTable getCodeWriter_XMLTable(){
		if(codeWriter_XMLTable == null) codeWriter_XMLTable = new CodeWriter_XMLTable(this);
		return codeWriter_XMLTable;
	}
	
	private CodeWriter_XMLForm codeWriter_XMLForm = null;
	public CodeWriter_XMLForm getCodeWriter_XMLForm(){
		if(codeWriter_XMLForm == null) codeWriter_XMLForm = new CodeWriter_XMLForm(this);
		return codeWriter_XMLForm;
	}

	private CodeWriter_PojoFocObject codeWriter_PojoFocObject = null;
	public CodeWriter_PojoFocObject getCodeWriter_PojoFocObject(){
		if(codeWriter_PojoFocObject == null) codeWriter_PojoFocObject = new CodeWriter_PojoFocObject(this);
		return codeWriter_PojoFocObject;
	}

	private CodeWriter_Proxy codeWriter_Proxy = null;
	public CodeWriter_Proxy getCodeWriter_Proxy(){
		if(codeWriter_Proxy == null) codeWriter_Proxy = new CodeWriter_Proxy(this);
		return codeWriter_Proxy;
	}	

	private CodeWriter_ProxyDesc codeWriter_ProxyDesc = null;
	public CodeWriter_ProxyDesc getCodeWriter_ProxyDesc(){
		if(codeWriter_ProxyDesc == null) codeWriter_ProxyDesc = new CodeWriter_ProxyDesc(this);
		return codeWriter_ProxyDesc;
	}	

	private CodeWriter_ProxyList codeWriter_ProxyList = null;
	public CodeWriter_ProxyList getCodeWriter_ProxyList(){
		if(codeWriter_ProxyList == null) codeWriter_ProxyList = new CodeWriter_ProxyList(this);
		return codeWriter_ProxyList;
	}
	
	private CodeWriter_GuiProxyPanel codeWriter_GuiProxyPanel = null;
	public CodeWriter_GuiProxyPanel getCodeWriter_GuiProxyPanel(){
		if(codeWriter_GuiProxyPanel == null) codeWriter_GuiProxyPanel = new CodeWriter_GuiProxyPanel(this);
		return codeWriter_GuiProxyPanel;
	}
	
	private CodeWriter_GuiProxyListPanel codeWriter_GuiProxyListPanel = null;
	public CodeWriter_GuiProxyListPanel getCodeWriter_GuiProxyListPanel(){
		if(codeWriter_GuiProxyListPanel == null) codeWriter_GuiProxyListPanel = new CodeWriter_GuiProxyListPanel(this);
		return codeWriter_GuiProxyListPanel;
	}
	
	private CodeWriter_GuiProxyListTable codeWriter_GuiProxyListTable = null;
	public CodeWriter_GuiProxyListTable getCodeWriter_GuiProxyListTable(){
		if(codeWriter_GuiProxyListTable == null) codeWriter_GuiProxyListTable = new CodeWriter_GuiProxyListTable(this);
		return codeWriter_GuiProxyListTable;
	}
}
