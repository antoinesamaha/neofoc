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
package com.foc.focDataSourceDB;

import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.ReferenceChecker;

public class ReferenceChecker_VoidObject {
	
	private SQLFilter sqlFilter = null;
	
	public ReferenceChecker_VoidObject(){
		
	}
	
	public void dispose(){
		if(sqlFilter != null){
			sqlFilter.dispose();
			sqlFilter = null;	
		}
	}
	
	public SQLFilter getSqlFilter() {
		return sqlFilter;
	}

	public void setSqlFilter(SQLFilter sqlFilter) {
		this.sqlFilter = sqlFilter;
	}
	
	public static SQLFilter getReferenceChecker_getSQLFilter(ReferenceChecker refCheck){
		SQLFilter sqlFilter = refCheck != null ? (SQLFilter) refCheck.getVoidObject() : null;
		if(sqlFilter == null){
	    FocConstructor constr         = new FocConstructor(refCheck.getFocDesc(), null, null);
	    FocObject 		 templateFocObj = constr.newItem();
	    if(templateFocObj != null){
	    	templateFocObj.setDbResident(false);
	    	sqlFilter = new SQLFilter(templateFocObj, SQLFilter.FILTER_ON_SELECTED);
	    	sqlFilter.setOwnerOfTemplate(true);
	    	sqlFilter.addSelectedField(refCheck.getObjectFieldID());
	    	refCheck.setVoidObject(sqlFilter);
	    }
		}
		return sqlFilter;
	}
	
	/*
	public static ReferenceChecker_VoidObject getReferenceChecker_VoidObject(ReferenceChecker refCheck){
		ReferenceChecker_VoidObject voidObj = null;
		if(refCheck != null){
			voidObj = (ReferenceChecker_VoidObject) refCheck.getVoidObject();
			if(voidObj == null){
				voidObj = new ReferenceChecker_VoidObject();
				refCheck.setVoidObject(voidObj);
			}
		}
		return voidObj;
	}
	
	public static SQLFilter getReferenceChecker_getSQLFilter(ReferenceChecker refCheck){
		ReferenceChecker_VoidObject voidObj 	= getReferenceChecker_VoidObject(refCheck);
		SQLFilter 									sqlFilter = voidObj != null ? voidObj.getSqlFilter() : null;
		if(sqlFilter == null){
	    FocConstructor constr         = new FocConstructor(refCheck.getFocDesc(), null, null);
	    FocObject 		 templateFocObj = constr.newItem();
	    if(templateFocObj != null){
	    	templateFocObj.setDbResident(false);
	    	sqlFilter = new SQLFilter(templateFocObj, SQLFilter.FILTER_ON_SELECTED);
	    	sqlFilter.setOwnerOfTemplate(true);
	    	sqlFilter.addSelectedField(refCheck.getObjectFieldID());
	    	voidObj.setSqlFilter(sqlFilter);
	    }
		}
		return sqlFilter;
	}
	*/
}
