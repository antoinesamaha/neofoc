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
package com.foc.focDataSourceDB.db.adaptor;

import java.util.Hashtable;
import java.util.Iterator;

import com.fab.model.project.FabProject;
import com.fab.model.project.FabProjectDesc;
import com.fab.model.project.FabWorkspace;
import com.fab.model.project.FabWorkspaceDesc;
import com.fab.model.table.TableDefinition;
import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.focDataSourceDB.db.DBManagerServer;
import com.foc.focDataSourceDB.db.SQLTableDetails;
import com.foc.focDataSourceDB.db.connectionPooling.ConnectionPool;

public class DBReverseEngineering {
	
	private String dbSourceKey = null;
	private String workspacePath = "c:/temp/reverseWorkspace";//"f:/eclipseworkspace_everpro"
	private String projectPath = "project/src";
	private String packageName = "com.myComp.fenix.station";
	
	private ITableFilter tableFilter = null;

	public DBReverseEngineering(String dbSourceKey, String workspacePath, String projectPath, String packageName){
		this.dbSourceKey = dbSourceKey;
		this.workspacePath = workspacePath;
		this.projectPath = projectPath;
		this.packageName = packageName;
	}
	
	public void dispose(){
		if(tableFilter != null){
			tableFilter.dispose();
			tableFilter = null;
		}
	}

	public ITableFilter getTableFilter() {
		return tableFilter;
	}

	public void setTableFilter(ITableFilter tableFilter) {
		this.tableFilter = tableFilter;
	}
	
	public String writeCode(){
  	ConnectionPool connectionPool = DBManagerServer.getInstance().getConnectionPool(dbSourceKey);
		String error = connectionPool == null ? "ConnectionPool null in DBReverseEngineering for dbSourceKey:" + dbSourceKey : null ;
		
		if(error == null){
			Hashtable<String, String> allTables = connectionPool.newActualTables(null);
			Iterator tableNames = allTables.values().iterator();
			while(tableNames != null && tableNames.hasNext()){
				String tableName = (String) tableNames.next();
				String nameToSearch = tableName; 
	
				if(tableFilter == null || tableFilter.isIncluded(nameToSearch)){
					FocDesc focDesc = Globals.getApp().getFocDescByName(nameToSearch);
					if(focDesc == null){ 
						focDesc = new FocDesc(FocObject.class, FocDesc.DB_RESIDENT, tableName, false);
						focDesc.setDbSourceKey(dbSourceKey);
						SQLTableDetails table = new SQLTableDetails(focDesc, true);//We are also getting the foreign keys
						table.buildRequest();
						table.execute();
						
						Hashtable<String, FField> actualFields = table.getFieldsHashtable();
						Hashtable<String, String> foreignKeys  = table.getForeignKeys();
						Iterator iter = actualFields.values().iterator();
						int fieldID = 1;
						while(iter.hasNext()){
							FField realFld = (FField) iter.next();
							
							if(realFld.isAutoIncrement()){//REF Field
								FField fld = focDesc.addReferenceField();
								fld.setName(realFld.getDBName());
							}else{
								realFld.setId(fieldID);
								focDesc.addField(realFld);
								fieldID++;
							}
						}
						
						TableDefinition tableDef = TableDefinition.getTableDefinitionForFocDesc(focDesc, foreignKeys);
						tableDef.setPackageName_ServerSide(packageName);
						
						FabProject   project   = new FabProject(new FocConstructor(FabProjectDesc.getInstance(), null));
						FabWorkspace workspace = new FabWorkspace(new FocConstructor(FabWorkspaceDesc.getInstance(), null));
						
						workspace.setWorkspacePath(workspacePath);
						project.setWorkspace(workspace);
						project.setProjectPath(projectPath);
						tableDef.setProject(project);
						tableDef.setPackageName_ServerSide(packageName);
						
						tableDef.setName(tableName);
						tableDef.setClassName(tableName);
						tableDef.setCW_ClassName_FocObject(tableName);
						
						tableDef.writeCode_ServerCode();
					}
				}
			}
		}
		return error;
	}
	
}
