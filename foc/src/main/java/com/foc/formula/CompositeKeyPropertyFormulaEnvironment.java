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
package com.foc.formula;

import java.util.ArrayList;

import com.foc.Globals;
import com.foc.db.SQLFilter;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.property.FProperty;

public class CompositeKeyPropertyFormulaEnvironment extends AbstractFormulaEnvironment {
  
  private IFormulaModel formulaModel = null;
  private FocList compositeKeyPropertyFormulaList = null;
  
  public CompositeKeyPropertyFormulaEnvironment(IFormulaModel formulaModel){
  	setFormulaModel(formulaModel);  	
  }
  
  public void dispose(){
    super.dispose();
    
    if(formulaModel != null){
      formulaModel.dispose();
      formulaModel = null;
    }
    
    if(compositeKeyPropertyFormulaList != null){
      compositeKeyPropertyFormulaList.dispose();
      compositeKeyPropertyFormulaList = null;
    }
  }
  
  private void setFormulaModel(IFormulaModel formulaModel){
  	this.formulaModel = formulaModel;
  }
  
  @Override
  public void loadAllFormulas() {
    getCompositeKeyPropertyFormulaList();
  }
  
  @Override
  public void applyAllFormulas() {
    FocList compositeKeyPropertyFormulaList = getCompositeKeyPropertyFormulaList();
    for(int i = 0; i < compositeKeyPropertyFormulaList.size(); i++){
      CompositeKeyPropertyFormula propertyFormula = (CompositeKeyPropertyFormula)compositeKeyPropertyFormulaList.getFocObject(i);
      try{
	      propertyFormula.setFormulaEnvironment(this);
	      FProperty property = formulaModel.getFormulaProperty(propertyFormula.getObjectRefs());
	      if(property != null){
	      	if(property.getFormula() == null || property.getPropertyFormulaContext() == null || property.getPropertyFormulaContext().getPropertyFormula() != propertyFormula){
	      		property.setPropertyFormulaContext(newPropertyFormulaContext(property, propertyFormula));
	      	}else{
	      		property.getPropertyFormulaContext().compute();
	      	}
	      	propertyFormula.setProperty(property);
	      }
      }catch(Exception e){
      	Globals.logException(e);
      }
    }
  }
  
  @Override
  public void applyFormulaToProperty(FProperty property, String expression) {
    if(property.isWithFormula()){
      PropertyFormulaContext propertyFormulaContext = property.getPropertyFormulaContext();
      PropertyFormula propertyFormula = propertyFormulaContext.getPropertyFormula();
      if(propertyFormula != null){
        propertyFormula.setExpression(expression);
        property.removeFormula();
        property.setPropertyFormulaContext(newPropertyFormulaContext(property, propertyFormula));
      }
    }else {
      newFormula(expression, property);
    }
  }
  
  public void newFormula(String expression, FProperty property){
    property.removeFormula();
    FocList propertyFormulaList = getCompositeKeyPropertyFormulaList();
    CompositeKeyPropertyFormula propertyFormula = (CompositeKeyPropertyFormula)propertyFormulaList.newEmptyItem();
    propertyFormula.setFormulaEnvironment(this);
    propertyFormula.setProperty(property);
    
    //propertyFormula.setFieldName(property.getFocField().getDBName());
    propertyFormula.setExpression(expression);
    
    propertyFormula.setObjectRefs(formulaModel.getObjectRefs(property));
    propertyFormula.setViewName(formulaModel.getViewName());
    propertyFormula.setFilterCriteria(formulaModel.getFilterCriteria());
    
    property.setPropertyFormulaContext(newPropertyFormulaContext(property, propertyFormula));
  }

  private void removeFormulas(ArrayList<CompositeKeyPropertyFormula> formulaToDelete){
    for(int i = 0; i < formulaToDelete.size(); i++){
      CompositeKeyPropertyFormula propertyFormula = (CompositeKeyPropertyFormula) formulaToDelete.get(i);
      try{
	      propertyFormula.setFormulaEnvironment(this);
	      FProperty property = formulaModel.getFormulaProperty(propertyFormula.getObjectRefs());
	      if(property != null){
	      	property.removeFormula();
	      	//applyFormulaToProperty(property, "");
	      }
	      
	      propertyFormula.setDeleted(true);
	      //propertyFormula.validate(true);
      }catch(Exception e){
      	Globals.logException(e);
      }
    }
  }
  
  public void removeAllFormulas_UnderTreeNode(FocObject fatherNodeObject){
  	removeAllFormulas_UnderTreeNode(fatherNodeObject, null);
  }

  private boolean isAncestorOfFocObject(FocObject fatherNodeObject, FocObject obj){
  	boolean isAncestor = false;
  	while(obj != null){
  		if(obj == fatherNodeObject){
  			isAncestor = true;
        break;
  		}
  		obj = obj.getFatherObject();
  	}
  	return isAncestor;
  }
  
  public void removeAllFormulas_UnderTreeNode(FocObject fatherNodeObject, String expressionToDecideOnFormulaDeletion){
    FocList compositeKeyPropertyFormulaList = getCompositeKeyPropertyFormulaList();
    ArrayList<CompositeKeyPropertyFormula> formulaToDelete = new ArrayList<CompositeKeyPropertyFormula>();
    for(int i = 0; i < compositeKeyPropertyFormulaList.size(); i++){
      CompositeKeyPropertyFormula propertyFormula = (CompositeKeyPropertyFormula) compositeKeyPropertyFormulaList.getFocObject(i);
      //Get the FocObject and see if it has as one of the ancestors the fatherNodeObject 
      if(propertyFormula.getProperty() != null){
      	FocObject obj = propertyFormula.getProperty().getFocObject();
      	
      	if(isAncestorOfFocObject(fatherNodeObject, obj)){
      		if(expressionToDecideOnFormulaDeletion == null || propertyFormula.getExpression().contains(expressionToDecideOnFormulaDeletion)){
      			formulaToDelete.add(propertyFormula);
      		}
      	}
      }
    }
    removeFormulas(formulaToDelete);
  }
  
  public void removeAllFormulas(){
    FocList compositeKeyPropertyFormulaList = getCompositeKeyPropertyFormulaList();
    ArrayList<CompositeKeyPropertyFormula> formulaToDelete = new ArrayList<CompositeKeyPropertyFormula>();
    for(int i = 0; i < compositeKeyPropertyFormulaList.size(); i++){
      CompositeKeyPropertyFormula propertyFormula = (CompositeKeyPropertyFormula) compositeKeyPropertyFormulaList.getFocObject(i);
      formulaToDelete.add(propertyFormula);
    }    
    removeFormulas(formulaToDelete);
    
    //compositeKeyPropertyFormulaList.removeAll();
  	/*
    FocList propertyFormulaList = getCompositeKeyPropertyFormulaList();
    if(propertyFormulaList != null){
    	for(int i=0; i<propertyFormulaList.size(); i++){
    		CompositeKeyPropertyFormula propertyFormula = (CompositeKeyPropertyFormula)propertyFormulaList.getFocObject(i);
    		FProperty                   property        = propertyFormula.getProperty();
   			propertyFormula.setDeleted(true);
   			if(property != null){
   				property.removeFormula();
   			}
    	}
    }
    */
  }
  
  public void removeFormula(FProperty property){
  	applyFormulaToProperty(property, "");
  	/*
    FocList propertyFormulaList = getCompositeKeyPropertyFormulaList();
    if(propertyFormulaList != null){
    	//B Changes
    	if(property != null && property.getPropertyFormulaContext() != null){
	    	CompositeKeyPropertyFormula propertyFormula = (CompositeKeyPropertyFormula)property.getPropertyFormulaContext().getPropertyFormula();
	    	if(propertyFormula != null){
	    		propertyFormula.setDeleted(true);
	    	}
	    }
//    	for(int i=0; i<propertyFormulaList.size(); i++){
//    		CompositeKeyPropertyFormula propertyFormula = (CompositeKeyPropertyFormula)propertyFormulaList.getFocObject(i);
//    		if(propertyFormula.getObjectRefs().equals(formulaModel.getObjectRefs(property))){
//    			propertyFormula.setDeleted(true);
//    		}
//    	}
    	//E Changes
    }
    property.removeFormula();
    */
  }

  private FocList getCompositeKeyPropertyFormulaList(){
    if(compositeKeyPropertyFormulaList == null){
      FocConstructor constr = new FocConstructor(CompositeKeyPropertyFormulaDesc.getInstance(), null);
      CompositeKeyPropertyFormula propertyFormula = new CompositeKeyPropertyFormula(constr);
      propertyFormula.setFilterCriteria(formulaModel.getFilterCriteria());
      propertyFormula.setViewName(formulaModel.getViewName());
      
      SQLFilter filter = new SQLFilter(propertyFormula, SQLFilter.FILTER_ON_SELECTED);
      filter.addSelectedField(CompositeKeyPropertyFormulaDesc.FLD_FILTER_CRITERIA);  
      filter.addSelectedField(CompositeKeyPropertyFormulaDesc.FLD_VIEW_NAME);
      
      compositeKeyPropertyFormulaList = new FocList(null, new FocLinkSimple(CompositeKeyPropertyFormulaDesc.getInstance()), filter);
      compositeKeyPropertyFormulaList.setDirectlyEditable(true);
      compositeKeyPropertyFormulaList.setDirectImpactOnDatabase(false);
      compositeKeyPropertyFormulaList.loadIfNotLoadedFromDB();  
    }
    
    return compositeKeyPropertyFormulaList;
  }

  public IFormulaModel getFormulaModel() {
    return formulaModel;
  }
  
  public void validateCompositeKeyPropertyFormulaList(){
  	FocList compositeKeyFmlLst = getCompositeKeyPropertyFormulaList();
  	for(int i=0; i<compositeKeyFmlLst.size(); i++){
  		CompositeKeyPropertyFormula keyPropFormula = (CompositeKeyPropertyFormula) compositeKeyFmlLst.getFocObject(i);
  		if(keyPropFormula != null) keyPropFormula.updateUsingProperty();
  	}
  	compositeKeyFmlLst.validate(false);
  }
}
