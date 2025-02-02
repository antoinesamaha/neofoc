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
package com.foc.tree;

import com.foc.business.currency.Currencies;
import com.foc.business.currency.Currency;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.property.FProperty;

public class TreeFormulas {

	public double getSingleFieldChildAverage(FNode node, int fieldID){
    return getSingleFieldChildSum(node, fieldID, true) / (double)node.getChildCount();
  }
  
  public double getSingleFieldChildSum(FNode node, int fieldID){
    return getSingleFieldChildSum(node, fieldID, true);
  }
  
  public double getSingleFieldChildSum(FNode node, int fieldID, boolean condition){
    
    double sum = 0;
    for( int i = 0; i < node.getChildCount(); i++){
      FNode childNode = node.getChildAt(i);
      
      if( condition ){
        if( childNode != null && childNode.getObject() != null ){
          FocObject focObj = (FocObject)childNode.getObject();
          sum += focObj.getPropertyDouble(fieldID);
        }  
      }
    }
    
    return sum;
  }
  
  public double[] getManyFieldChildSum( FNode node, int [] fieldIDs ){
    
    double [] sum = new double[fieldIDs.length];

    for(int i = 0; i < node.getChildCount(); i++){
      FNode childNode = node.getChildAt(i);
      if( childNode != null && childNode.getObject() != null ){
        FocObject focObj = (FocObject)childNode.getObject();
        for( int j = 0; j < sum.length; j++){
          sum[j] += focObj.getPropertyDouble(fieldIDs[j]);
        }
      }
    }
    return sum;
  }
  
  public double sumFields(FNode node, int [] fieldIDs){
  	return sumFields(node, fieldIDs, null);
  }
  
  public double sumFields(FNode node, int [] fieldIDs, INodeItertorFilter nodeIteratorFilter){
  	double totalHorizontally = 0;
		FocObject obj = node != null ? (FocObject) node.getObject() : null;
		if(obj != null){
			if(fieldIDs.length > 0){
	  		double[] valueArray = new double[fieldIDs.length];
  		
		    for(int i = 0; i < node.getChildCount(); i++){
		      FNode childNode = node.getChildAt(i);
		      if(childNode != null && childNode.getObject() != null && (nodeIteratorFilter == null || nodeIteratorFilter.includeNode(childNode))){
		        FocObject focObj = (FocObject)childNode.getObject();
		        for(int j = 0; j < fieldIDs.length; j++){
		        	int    fldID      = fieldIDs[j];
		        	double valueToAdd = focObj.getPropertyDouble(fldID);
		        	double value      = 0; 
		        	if(i == 0){
		        		valueArray[j] = valueToAdd;
		        		//value = valueToAdd;
		        	}else{
		        		valueArray[j] += valueToAdd;
		        		//FProperty prop = obj.getFocProperty(fldID);	        		
		        		//value = prop.getDouble() + valueToAdd;
		        	}
	        		
		        	FProperty prop = obj.getFocProperty(fldID);
		        	if(prop != null){
		        		prop.setDouble(value);
		        	}
		        }
		      }
		    }
		    
	      for(int j = 0; j < fieldIDs.length; j++){
	      	int fldID = fieldIDs[j];
	      	FProperty prop = obj.getFocProperty(fldID);
	      	if(prop != null){
	      		prop.setDouble(valueArray[j]);
	      	}
	      	totalHorizontally += valueArray[j];
	      }
  		}
  	}
		return totalHorizontally;
  }
  
  public void calculateAverageNodesAccordingToCurrency(FTree tree, final FFieldPath currencyFieldID, final FFieldPath tobeSummedFieldID){
  	calculateAverageNodesAccordingToCurrency(tree.getRoot(), currencyFieldID, tobeSummedFieldID);  	
  }
  
  public void calculateAverageNodesAccordingToCurrency(FNode rootNode, final FFieldPath currencyFieldID, final FFieldPath tobeSummedFieldID){
  	rootNode.scan(new TreeScanner<FNode>(){

      public void afterChildren(FNode node) {
        FNode fatherNode = node;
        FocObject fatherFocObj = (FocObject) fatherNode.getObject();
        
        FProperty fatherCurrProp = null; 
        Currency  fatherCurr     = null;
        if(currencyFieldID != null){
        	fatherCurrProp = currencyFieldID.getPropertyFromObject(fatherFocObj);
          fatherCurr = fatherCurrProp != null ? (Currency) fatherCurrProp.getObject() : null;
        }
          	
        double sum = 0;
        int childCount = fatherNode.getChildCount();
        for(int i = 0; i < fatherNode.getChildCount(); i++){
          FNode     childNode   = fatherNode.getChildAt(i);
          FocObject childFocObj = (FocObject) childNode.getObject();
          
          double rate = 1;
          if(currencyFieldID != null && fatherCurr != null){
            FProperty childCurrProp = currencyFieldID.getPropertyFromObject(childFocObj);
            if( childCurrProp != null ){
              Currency childCurr = (Currency) childCurrProp.getObject();
              if( childCurr != null ){
                rate = Currencies.getRate(fatherCurr, childCurr);
              }
            }
          }  
          
          FProperty toBeSummedProp = tobeSummedFieldID.getPropertyFromObject(childFocObj);
          if( toBeSummedProp != null ){
            sum += toBeSummedProp.getDouble() * rate;  
          }
        }
        if( sum != 0 ){
          FProperty toBeSummedProp = tobeSummedFieldID.getPropertyFromObject(fatherFocObj);
          if( toBeSummedProp != null ){
            toBeSummedProp.setDouble(sum/childCount);  
          }
        }
      }

      public boolean beforChildren(FNode node) {
        return true;
      }
      
    });
  }
  
  private static TreeFormulas treeFormulas = null;
  public static TreeFormulas getInstance(){
    if( treeFormulas == null ){
      treeFormulas = new TreeFormulas();
    }
    return treeFormulas;
  }
  
}
