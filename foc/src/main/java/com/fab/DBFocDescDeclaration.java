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
package com.fab;

import java.util.ArrayList;
import java.util.Iterator;

import com.fab.model.table.TableDefinition;
import com.fab.model.table.UserDefinedDesc;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.desc.FocDesc;
import com.foc.desc.FocDescMap;
import com.foc.desc.FocModule;

public class DBFocDescDeclaration implements IFocDescDeclaration{
	private TableDefinition tableDefinition    = null ;
	private FocDesc         focDesc            = null ;
	private boolean         fieldsAlreadyAdded = false;
	private boolean         gettingFocDesc     = false;
	private FocModule       module             = null;   
	
	private DBFocDescDeclaration(FocModule module, TableDefinition tableDefinition){
		this.tableDefinition = tableDefinition;
		fieldsAlreadyAdded   = false;		
		this.module = module;
	}
	
	public void dispose(){
		this.focDesc         = null;
		this.tableDefinition = null;
		this.module          = null;
	}
	
	public int getTableReference(){
		return (tableDefinition != null ? tableDefinition.getReference().getInteger() : 0);
	}
	
	public String getTableName() {
		return (tableDefinition != null ? tableDefinition.getName() : "");
	}

	public boolean isGettingFocDesc(){
		return gettingFocDesc;
	}

	public void setGettingFocDesc(boolean getting){
		gettingFocDesc = getting;
	}
	
	public FocDesc getFocDescription() {
		if(focDesc == null && !isGettingFocDesc()){
			setGettingFocDesc(true);
			if(tableDefinition.isAlreadyExisting()){
				if(!fieldsAlreadyAdded){
					FocDesc focDescLocal = tableDefinition.getExistingTableDesc();
					
					ArrayList<FocDesc> doneDescs = new ArrayList<FocDesc>();
					
					applyTableDefinitionToFocDesc(focDescLocal);
					doneDescs.add(focDescLocal);
					fieldsAlreadyAdded = true;
					
					//Applying to FocDesc that extend the initial one (Like the Bills / Invoice case)
					if(focDescLocal != null){
					  Iterator<FocDesc> iter = FocDescMap.getInstance().values().iterator();
						while(iter != null && iter.hasNext()){
							FocDesc focDesc = iter.next();
							if(focDesc != null && focDesc.getStorageName() != null && focDesc.getStorageName().equals(focDescLocal.getStorageName())){
								if(!doneDescs.contains(focDesc)){
									applyTableDefinitionToFocDesc(focDesc);
									doneDescs.add(focDesc);
								}
							}
						}
					}
				}
			}else{
				focDesc = new UserDefinedDesc(module, tableDefinition);
				focDesc.addFieldsFromTableDefinition();
			}
			setGettingFocDesc(false);
		}
		return focDesc;
	}

	public void applyTableDefinitionToFocDesc(FocDesc focDescLocal){
		if(focDescLocal != null){
			focDescLocal.setFabTableDefinition(tableDefinition);
			tableDefinition.addFieldsToFocDesc(focDescLocal);
			focDescLocal.setModule(module);
		}
	}
	
	public static IFocDescDeclaration getDbFocDescDeclaration(TableDefinition tableDefinition){
		IFocDescDeclaration declaration = null;
		if(tableDefinition != null){
			String name = tableDefinition.getName();
			declaration = (IFocDescDeclaration)Globals.getApp().getIFocDescDeclarationByName(name);
			if(declaration == null){
				//if(!ConfigInfo.isForDevelopment()){
					declaration = new DBFocDescDeclaration(FabModule.getInstance(), tableDefinition);
					Globals.getApp().putIFocDescDeclaration(name, declaration);
				//}
			}
		}
		return declaration;
	}
	
	public int getPriority() {
		return IFocDescDeclaration.PRIORITY_SECOND;
	}

	@Override
	public FocModule getFocModule() {
		return module;
	}
	
	@Override
	public String getName() {
		return (getFocDescription() != null) ? getFocDescription().getStorageName() : null;
	}
}
