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
package com.foc.json;

import java.util.ArrayList;

public abstract class B01JsonSaxCallBack_Abstract implements B01JsonSaxCallBack_Interface {

	private ArrayList arrObject = null;
	//private ArrayList arrLists  = null;
	
	public B01JsonSaxCallBack_Abstract(){
		arrObject = new ArrayList();
		//arrLists  = new ArrayList();
	}
		
	public void dispose(){
		if(arrObject != null){
			arrObject.clear();
			arrObject = null;
		}
		/*
		if(arrLists != null){
			arrLists.clear();
			arrLists = null;
		}
		*/
	}
	
	public void objStack_Push(Object obj){
		arrObject.add(obj);
	}

	public Object objStack_GetFirst(){
		return objStack_GetLevelObject(0);
	}
	
	public Object objStack_GetLast(){
		return objStack_GetLevelObject(arrObject.size() - 1);
	}

	public Object objStack_RemoveLast(){
		Object obj = null;
		if(arrObject != null && arrObject.size() > 0){
			obj = arrObject.remove(arrObject.size() - 1);
		}
		return obj;
	}
	
	public Object objStack_GetLevelObject(int level){//0 means the last
		return arrObject != null && level >= 0 && arrObject.size()-(1+level) >= 0 ? arrObject.get(arrObject.size()-(1+level)) : null;
	}
	/*
	public void lstStack_Push(Object obj){
		arrLists.add(obj);
	}

	public Object lstStack_GetLast(){
		return lstStack_GetLevelObject(0);
	}

	public Object lstStack_RemoveLast(){
		Object obj = null;
		if(arrLists != null && arrLists.size() > 0){
			obj = arrLists.remove(arrLists.size() - 1);
		}
		return obj;
	}
	
	public Object lstStack_GetLevelObject(int level){//0 means the last
		return arrLists != null && arrLists.size()-(1+level) >= 0 ? arrLists.get(arrLists.size()-(1+level)) : null;
	}
	*/
	@Override
	public void startObject() {
	}

	@Override
	public void startList() {
	}

	@Override
	public void endObject() {
		objStack_RemoveLast();
	}

	@Override
	public void endList() {
		objStack_RemoveLast();
	}

	@Override
	public void startString() {
	}

	@Override
	public void endString(StringBuffer strBuff) {
	}

	@Override
	public void newKey(StringBuffer key) {
	}

	@Override
	public void newValue(StringBuffer value) {
	}
}
