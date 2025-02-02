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
package com.foc.desc.parsers.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.foc.Globals;
import com.foc.annotations.model.FocEntity;
import com.foc.annotations.model.FocFilterCondition;
import com.foc.annotations.model.FocGroupByField;
import com.foc.annotations.model.FocJoin;
import com.foc.annotations.model.FocWorkflow;
import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;
import com.foc.desc.field.FField;
import com.foc.desc.parsers.FocDescDeclaration_ParsedBased;
import com.foc.desc.parsers.fields.FocFieldFactory;
import com.foc.desc.parsers.fields.IFocFieldType;
import com.foc.desc.parsers.filter.ParsedFilter;
import com.foc.desc.parsers.filter.ParsedFilterCondition;
import com.foc.desc.parsers.join.ParsedJoin;
import com.foc.desc.parsers.predefinedFields.FocPredefinedFieldFactory;
import com.foc.desc.parsers.predefinedFields.IFocPredefinedFieldType;
import com.foc.list.FocListGroupBy;
import com.foc.util.FocAnnotationUtil;
import com.foc.util.Utils;

public class FocDescDeclaration_PojoBased extends FocDescDeclaration_ParsedBased {

	private Class<PojoFocDesc>   descClass   = null;
	private Class<PojoFocObject> objClass    = null;
	private PojoFocDesc          focDesc     = null;
	
	public FocDescDeclaration_PojoBased(FocModule module, String name, String storageName, Class<PojoFocDesc> descClass, Class<PojoFocObject> objClass){
		super(module, name, storageName);
		this.descClass   = descClass;
		this.objClass    = objClass;
	}
	
	@Override
	public FocDesc getFocDescription() {
		if(focDesc == null){
			focDesc = newFocDesc();
			focDesc.setModule(getFocModule());
			PojoFocDesc pojoFocDesc = (PojoFocDesc) focDesc; 
			readAnnotations(pojoFocDesc);
			if(pojoFocDesc != null) pojoFocDesc.afterParsing();
		}
    return focDesc;
	}
	
	private PojoFocDesc newFocDesc(){
	  try {
	    if (descClass != null) {
				FocEntity entity = (FocEntity) FocAnnotationUtil.findAnnotation(objClass, FocEntity.class);
				if(entity != null){			
		    	
		      Class[] clss = new Class[4];
		      Object[] args = new Object[4];
		      {
		      	clss[0] = Class.class;
		      	args[0] = objClass;
	
		      	clss[1] = boolean.class;
		      	args[1] = entity.dbResident();
		      	
		      	clss[2] = String.class;
		      	args[2] = getStorageName();
		      	
		      	clss[3] = boolean.class;
		      	args[3] = Boolean.FALSE;
		      }
		      Constructor<PojoFocDesc> methodGetFocDesc = null;
		      try{
		      	methodGetFocDesc = descClass.getConstructor(clss);
		      }catch(NoSuchMethodException e){
		      	Globals.logException(e);
		      }
		      if(methodGetFocDesc != null){
		      	focDesc = (PojoFocDesc) methodGetFocDesc.newInstance(args);
		      	focDesc.setName(getName());
		      	
						Globals.getApp().putIFocDescDeclaration(getName(), this);
		      }
		    }
	    }
	  } catch (Exception e) {
	  	Globals.logString("Exception while getting FocDesc for class : "+descClass.getName());
	    Globals.logException(e);
	  }
	  return focDesc;
	}

	private void readAnnotations(PojoFocDesc focDesc){
		try{
      FocEntity entity = (FocEntity) FocAnnotationUtil.findAnnotation(objClass, FocEntity.class);
      if(entity != null) {
	      if(entity.isTree()) focDesc.setWithObjectTree();
	      focDesc.setListInCache(entity.cached());
	      if(entity.hasRevision()){
	      	focDesc.setWithObjectTree();
	      	
	      }
	      if(entity.joins() != null) {
	      	for(FocJoin joinAnn : entity.joins()) {
	  	    	ParsedJoin join = new ParsedJoin(focDesc, joinAnn);
	  	    	focDesc.putJoin(join);
	      	}
	      }

	      focDesc.setAllowAdaptDataModel(entity.allowAdaptDataModel());
	      focDesc.setLogicalDeleteEnabled(entity.logicalDelete());
	      
	      if(!Utils.isStringEmpty(entity.filterOnTable())){
	      	ParsedFilter parsedFilter = new ParsedFilter(entity.filterOnTable());
	      	focDesc.setParsedFilter(parsedFilter);
	      	
	      	if(entity.filterConditions() != null && entity.filterConditions().length > 0) {
	      		for(FocFilterCondition cond : entity.filterConditions()) {
	      			ParsedFilterCondition parsedConditon = new ParsedFilterCondition(cond);
	      			parsedFilter.addCondition(parsedConditon);
	      		}
	      	}
	      	
	      	parsedFilter.setFilterLevel(entity.filterLevel());
	      }

      	if(entity.groupByFields() != null && entity.groupByFields().length > 0) {
      		for(FocGroupByField groupByField : entity.groupByFields()) {
      			if(!Utils.isStringEmpty(groupByField.name())) {
	      			FocListGroupBy groupByList = focDesc.getGroupBy(); 
	      			if(groupByList == null) {
	      				groupByList = new FocListGroupBy();
	      				focDesc.setGroupBy(groupByList);
	      			}
	      			groupByList.addAtomicExpression(groupByField.name());
      			}
      		}
      	}

	      FocWorkflow workflow = (FocWorkflow) FocAnnotationUtil.findAnnotation(objClass, FocWorkflow.class);
	      if(workflow != null){
		    	String workflowCode  = workflow.code();
		    	String workflowTitle = workflow.title();
		    	focDesc.initWorkflow();
		    	
		    	if(Utils.isStringEmpty(workflowTitle)) workflowTitle = focDesc.getStorageName(); 
		    	focDesc.setWorkflowTitle(workflowTitle);
		    	
		    	if(Utils.isStringEmpty(workflowCode)) workflowCode = focDesc.getStorageName();
		    	focDesc.setWorkflowCode(workflowCode);
	      }
	      
	      Annotation[] ann = objClass.getAnnotations();
	    	for(Annotation a : ann){
	    		String simpleName = a.annotationType().getSimpleName();
	    		if(simpleName.startsWith("Foc") && simpleName.length() > 3){
	    			String fieldTypeName = simpleName.substring(3);
		      	IFocPredefinedFieldType fieldType = FocPredefinedFieldFactory.getInstance().get(fieldTypeName);
		      	if(fieldType != null){
	  	      	FField focField = fieldType.newFField(focDesc, a);
		      	}
	    		}
	    	}
	      
	    	//We also check fields from super classes
	      Field[] fields = objClass.getFields();
	      readFieldArray(fields);

	    	/*
	    	Class<?> currentClass = objClass;
	      while (currentClass != null && currentClass != FocObject.class && currentClass != Object.class) {
		      Field[] fields = currentClass.getFields();
		      readFieldArray(fields);
		      currentClass = currentClass.getSuperclass();
	      }
	      */
	      
	      String reportContext = entity.reportContext();
	      if(!Utils.isStringEmpty(reportContext)) {
	      	focDesc.setReportContext(reportContext);
	      }
      }
    } catch (Exception e) {
      Globals.logString("Could not load file : " + focDesc.getClass().getName());
      Globals.logException(e);
    }
	}

	private void readFieldArray(Field[] fields) {
	  for(Field f : fields){
	  	Annotation[] ann = f.getAnnotations();
	  	for(Annotation a : ann){
	  		String simpleName = a.annotationType().getSimpleName();
	  		if(simpleName.startsWith("Foc") && simpleName.length() > 3){
	  			String fieldTypeName = simpleName.substring(3);
	
	      	IFocFieldType fieldType = FocFieldFactory.getInstance().get(fieldTypeName);
	      	if(fieldType != null){
		      	FField focField = fieldType.newFField(null, f, a);
		      	focField.setId(focDesc.nextFldID());
		      	focDesc.addField(focField);
	      	}
	  		}
	  	}
	  }
	}

}
