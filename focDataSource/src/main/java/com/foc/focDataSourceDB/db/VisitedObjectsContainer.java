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
package com.foc.focDataSourceDB.db;

import java.util.HashMap;
import java.util.Iterator;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;

public class VisitedObjectsContainer {
	private FocDesc focDesc = null;
	private boolean withRef = false;
	private HashMap<FocObject, FocObject> mapObj = null;
	private HashMap<Integer, FocObject>   mapRef = null;
	
	public VisitedObjectsContainer(FocDesc focDesc){
		this.focDesc = focDesc;
		if(focDesc.getFieldByID(FField.REF_FIELD_ID) != null){
			withRef = true;
		}
		if(withRef){
			mapRef = new HashMap<Integer, FocObject>();
		}else{
			mapObj = new HashMap<FocObject, FocObject>();
		}
	}
	
	public void dispose(){
		focDesc = null;
	}

	public void push(FocObject obj){
		if(withRef){
			mapRef.put(obj.getReference().getInteger(), obj);
		}else{
			mapObj.put(obj, obj);
		}
	}
	
	public boolean contains(FocObject obj){
		boolean contains = false;
		if(withRef){
			contains = mapRef.get(obj.getReference().getInteger()) != null;
		}else{
			contains = mapObj.get(obj) != null;
		}
		return contains;
	}
	
	public Iterator<FocObject> valuesIterator(){
		Iterator<FocObject> iter = null;
		if(withRef){
			iter = mapRef.values().iterator();
		}else{
			iter = mapObj.values().iterator();
		}
		return iter;
	}
}
