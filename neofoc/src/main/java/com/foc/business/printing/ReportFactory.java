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
package com.foc.business.printing;

import java.util.HashMap;
import java.util.Map;

import com.foc.list.FocList;

public class ReportFactory {

	private FocList contextList = null;
	private Map<String, PrnLayout> prnLayoutDicMap = null;
	
	private ReportFactory(){
	}
	
	public void dispose(){
		if(contextList != null){
			contextList.dispose();
			contextList = null;
		}
	}
	
	private Map<String, PrnLayout> getPrnLayoutDicMap(){
    if(prnLayoutDicMap == null)
    	prnLayoutDicMap = new HashMap<String, PrnLayout>();
    return prnLayoutDicMap;
  }
	
//	public PrnLayout put(PrnLayout layout){
//    return getPrnLayoutDicMap().put(layout.getPrnLayoutKey().getStringKey(), layout); 
//  }
	
	public FocList getContextList(){
		if(contextList == null){
			contextList = PrnContextDesc.getList(FocList.FORCE_RELOAD);
		}
		return contextList;
	}
	
	public PrnContext findContext(String dbName){
		FocList contextList = getContextList();
		PrnContext context = (PrnContext) contextList.searchByPropertyStringValue(PrnContextDesc.FLD_DB_NAME, dbName);
		return context; 
	}
	
	public PrnContext pushContext(String dbName, String name, String description){
		PrnContext cont = findContext(dbName);
		if(cont == null){
			FocList contextList = getContextList();
			cont = (PrnContext) contextList.newEmptyItem();
			cont.setDBName(dbName);
		}
		if(cont != null){
			cont.setName(name);
			cont.setDescription(description);
			cont.validate(true);
		}
		return cont;
	}
	
	public void loadReportsFromTable(){
  	FocList listOfViews = PrnLayoutDefinitionDesc.getList(FocList.LOAD_IF_NEEDED);
  	for(int i=0; i<listOfViews.size(); i++){
  		PrnLayoutDefinition prnLayoutDefinition = (PrnLayoutDefinition) listOfViews.getFocObject(i);
  		putPrnLayoutDefinition(prnLayoutDefinition);
  	}
  }
	
	public PrnLayout putPrnLayoutDefinition(PrnLayoutDefinition layoutDefinition) {
		PrnLayout layout = pushLayout(layoutDefinition.getContextDBName(), layoutDefinition.getFileName(), layoutDefinition.getName(), layoutDefinition.getDescription());
		if(layout != null){
			layout.setLayoutDefinition(layoutDefinition);
		}
    return layout;
  }

	public PrnLayout pushLayout(String contextDBName, String fileName, String name, String description){
		PrnLayout layout = null;
		PrnContext context = findContext(contextDBName);
		if(context != null){
			layout = context.pushLayout(fileName, name, description);
		}
		return layout;
	}
	
	private static ReportFactory instance = null;
	public static ReportFactory getInstance(){
		if(instance == null){
			instance = new ReportFactory();
		}
		return instance;
	}
}
