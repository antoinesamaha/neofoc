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
package com.foc.desc.parsers.filter;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import com.foc.list.filter.FocListFilter;

public class ParsedFilter {
	
	private String tableName   = null;
	private int    filterLevel = FocListFilter.LEVEL_DATABASE;

	private ArrayList<ParsedFilterCondition> conditionArray = null;
	
	public ParsedFilter(String tableName){
		this.tableName = tableName;
	}
	
	public void addCondition(ParsedFilterCondition condition){
		if(conditionArray == null){
			conditionArray = new ArrayList<ParsedFilterCondition>();
		}
		
		conditionArray.add(condition);
	}
	
	public void addCondition(Attributes att){
		addCondition(new ParsedFilterCondition(att));
	}

	public String getTableName() {
		return tableName;
	}
	
	public int getConitionCount(){
		return conditionArray != null ? conditionArray.size() : 0;
	}
	
	public int getFilterLevel() {
		return filterLevel;
	}

	public void setFilterLevel(int filterLevel) {
		this.filterLevel = filterLevel;
	}
	
	public ParsedFilterCondition getConitionAt(int index){
		return (ParsedFilterCondition) (conditionArray != null ? conditionArray.get(index) : null);
	}
}
