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
package com.foc.desc.parsers;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.Globals;
import com.foc.business.workflow.implementation.FocWorkflowDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.FocDescMap;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.parsers.filter.ParsedFilter;
import com.foc.desc.parsers.filter.ParsedFilterCondition;
import com.foc.desc.parsers.join.ParsedJoin;
import com.foc.join.FocRequestDesc;
import com.foc.join.TableAlias;
import com.foc.list.filter.FilterCondition;
import com.foc.list.filter.FilterConditionFactory;
import com.foc.list.filter.FilterDesc;
import com.foc.list.filter.ObjectCondition;
import com.foc.shared.dataStore.AbstractDataStore;
import com.foc.util.Utils;

public abstract class ParsedFocDesc extends FocWorkflowDesc {

	private HashMap<String, ParsedJoin> joinMap     = null;//Will remain null if not a join table
	private FocRequestDesc              requestDesc = null;//Will remain null if not a join table
	
	private ParsedFilter                xmlFilter   = null;//Will remain null if not a Filter
	private FilterDesc                  filterDesc  = null;//Will remain null if not a filter
	
	public ParsedFocDesc(Class focObjectClass, boolean dbResident, String storageName, boolean isKeyUnique, boolean withWorkfllow) {
		super(focObjectClass, dbResident, storageName, isKeyUnique, withWorkfllow);
	}
	
	public static ParsedFocDesc getInstance(String tableName){
		return (ParsedFocDesc) (FocDescMap.getInstance() != null ? FocDescMap.getInstance().get(tableName, AbstractDataStore.TRANSACTION_TYPE_NONE) : null);
	}
	
  //Implementation of the Workflow
  //------------------------------
	@Override
	public boolean workflow_IsWorkflowSubject() {
		return iWorkflow_getWorkflowDesc() != null && super.workflow_IsWorkflowSubject();
	}

	@Override
	public int iWorkflow_getFieldIDShift() {
		return 2000;
	}

//	@Override
//	public String iWorkflow_getDBTitle() {
//		return getStorageName();
//	}

	@Override
	public String iWorkflow_getCodePrefix() {
		return "";
	}

	@Override
	public String iWorkflow_getCodePrefix_ForProforma() {
		return "";
	}
	//-----------------------------------
	
  protected void afterConstruction_1(){
  	super.afterConstruction_1();
    FocRequestDesc reqDesc = getFocRequestDesc(true);
    if(reqDesc != null){
    	//This is the Join Case
    	reqDesc.fillFocDesc(this);
    }
  	
  	filterDesc = getFilterDesc();
  	if(filterDesc != null){
  		filterDesc.fillDesc(this, nextFldID());
  	}
  	
//  	FocListGroupBy groupBy = getGroupBy();
//  	if(groupBy != null){
//  		FocFieldEnum enumer = newFocFieldEnum(FocFieldEnum.CAT_ALL_DB, FocFieldEnum.LEVEL_PLAIN);
//  		while(enumer != null){
//  			enumer.getFieldCompleteName(this);
//  		}
//  	}
  }

  public FilterDesc getFilterDesc_WithoutCreation() {
  	return filterDesc;
  }
  
//	@Override
	public FilterDesc getFilterDesc() {
		ParsedFilter xmlFilter = getParsedFilter();
		if(filterDesc == null && xmlFilter != null){
			FocDesc subjectFocDesc = Globals.getApp().getFocDescByName(xmlFilter.getTableName());
			if(subjectFocDesc != null){
				filterDesc = new FilterDesc(subjectFocDesc);
				
				for(int i=0; i<xmlFilter.getConitionCount(); i++){
					ParsedFilterCondition condition = xmlFilter.getConitionAt(i);

					FField fld = (FField) subjectFocDesc.iFocData_getDataByPath(condition.getFieldPath());
					if(fld != null){
						FFieldPath fieldPath = FFieldPath.newFFieldPath(subjectFocDesc, condition.getFieldPath());
						FilterCondition cond = null;
						if (!Utils.isStringEmpty(condition.getClassName())) {
							cond = newItem(condition.getClassName(), fieldPath, condition.getPrefix());
						} else {
							cond = FilterConditionFactory.newConditionForField(fld, fieldPath, condition.getPrefix());
						}
						if(cond != null){
							cond.setLevel(condition.getLevel());
							if(!Utils.isStringEmpty(condition.getCaption())){
								cond.setFieldLabel(condition.getCaption());
							}
							if(!Utils.isStringEmpty(condition.getCaptionProperty())){
								if(cond instanceof ObjectCondition){
									((ObjectCondition) cond).setCaptionProperty(condition.getCaptionProperty()); 
								}
							}
							filterDesc.addCondition(cond);
						}
					}else{
						Globals.logString("FocDesc not found for filter Subject Table "+xmlFilter.getTableName());
					}
				}
			}else{
				Globals.logString("FocDesc not found for filter Subject Table "+xmlFilter.getTableName());
			}
		}
		return filterDesc;
	}
	
	public FilterCondition newItem(String className, FFieldPath stringFieldPath, String fieldPrefix){
		FilterCondition condition = null;

    try{
      Class<FilterCondition> cls = (Class<FilterCondition>) Class.forName(className);
      if(cls != null){ 
        Class[] classes = new Class[2];
        classes[0] = FFieldPath.class;
        classes[1] = String.class;
    
        Object[] objects = new Object[2];
        objects[0] = stringFieldPath;
        objects[1] = fieldPrefix;
        
        Constructor<FilterCondition> construct = cls.getConstructor(classes);
        if(construct != null){
          condition = (FilterCondition) construct.newInstance(objects);
        }
      }
    }catch (Exception e){
      Globals.logException(e);
      if(className != null){
      	Globals.logString("Could not create condition for Class:"+className);
      }
    }
    return condition;
  }

	
  
	public FocRequestDesc getFocRequestDesc() {
		return getFocRequestDesc(false);
	}
	
	// Request Methods
	// ---------------
	public FocRequestDesc getFocRequestDesc(boolean create) {
    if(requestDesc == null && create){
    	requestDesc = null;
    	
    	ArrayList<ParsedJoin> joinArray = new ArrayList<ParsedJoin>();
    	
    	Iterator<ParsedJoin> iter = newJoinIterator();
    	if(iter != null && iter.hasNext()){
    		requestDesc = new FocRequestDesc();
	    	while(iter != null && iter.hasNext()){
	    		ParsedJoin xmlJoin = iter.next();

	    		joinArray.add(xmlJoin);
	    	}
    	}

    	if(joinArray != null && joinArray.size() > 0){
    		Collections.sort(joinArray, new Comparator<ParsedJoin>() {
					@Override
					public int compare(ParsedJoin o1, ParsedJoin o2) {
						return o1.getOrder() - o2.getOrder();
					}
    		});
    		
    		for(int i=0; i<joinArray.size(); i++){
    			ParsedJoin xmlJoin = joinArray.get(i);
    			if(xmlJoin != null){
			  		TableAlias tableAlias = xmlJoin.getTableAlias();
			  		requestDesc.addTableAlias(tableAlias);
    			}
    		}
    	}
    }
		return requestDesc;
	}
	// ---------------
	
	// Join Methods
	// ------------
  public ParsedJoin getJoin(String alias){
  	return joinMap != null ? joinMap.get(alias) : null;
  }
  
  public void putJoin(ParsedJoin join){  
		if(joinMap == null) joinMap = new HashMap<String, ParsedJoin>();
		join.setOrder(joinMap.size());
		joinMap.put(join.getAlias(), join);
  }

  public Iterator<ParsedJoin> newJoinIterator(){
  	return (joinMap != null && joinMap.size() > 0) ? joinMap.values().iterator() : null;
  }
  
  public boolean hasReferenceFromAJoinTable() {
  	boolean foundPrimaryKeyJoin = false;
  	Iterator<ParsedJoin> iter = newJoinIterator();
  	while(iter != null && iter.hasNext() && !foundPrimaryKeyJoin) {
  		ParsedJoin join     = iter.next();
  		foundPrimaryKeyJoin = join.isPrimaryKey();
  	}
  	return foundPrimaryKeyJoin;
  }
  
	@Override
  public boolean isJoin(){
		boolean join = super.isJoin();
		if(!join){
			join = getFocRequestDesc(false) != null;
		}
  	return join;
  }
	
	//Is we want to check if isJoin() before the FocRequestDesc construction
	public boolean hasJoinNode(){
		return joinMap != null && joinMap.size() > 0;
	}
	// ------------
	
	public ParsedFilter getParsedFilter() {
		return xmlFilter;
	}

	public void setParsedFilter(ParsedFilter filter) {
		this.xmlFilter = filter;
	}

}
