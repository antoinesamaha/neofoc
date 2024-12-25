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
package com.foc.property;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FTypedObjectField;
import com.foc.list.FocList;

public class PropertyFocObjectLocator {
  private FocObject  locatedFocObject = null;
  private FProperty  locatedProperty  = null;
  private FField     locatedField     = null;
  private FFieldPath locatedPath      = null;
  private ArrayList  pathArray        = null;	
  
  private static final String CONTEXT_INDEX     = "INDEX[";
  private static final String CONTEXT_NODE_CODE = "NODE[";
  private static final String CONTEXT_PARENT    = "PARENT";
  private static final String CONTEXT_FATHER    = "FATHER";
  private static final String CONTEXT_SAME_COL  = "SAME_COL";
  
  public PropertyFocObjectLocator(){
    pathArray = new ArrayList();  	
  }
  
  public void dispose(){
    locatedFocObject = null;
    locatedProperty  = null;
    locatedField     = null;
    if(locatedPath != null) locatedPath.dispose();
    locatedPath      = null;
    pathArray        = null;
  }
  
  public FocObject getLocatedFocObject(){
    return locatedFocObject;
  }

  public FProperty getLocatedProperty(){
    return locatedProperty;
  }

  public FField getLocatedField(){
    return locatedField;
  }

  public FFieldPath getLocatedPath(){
  	if(pathArray != null && pathArray.size() > 0){
  		locatedPath = new FFieldPath(pathArray.size());
  		for(int i=0; i<pathArray.size(); i++){
  			locatedPath.set(i, (Integer)pathArray.get(i));
  		}
  	}
    return locatedPath;
  }

  private int getKeyFieldID(FocDesc desc){
    int keyFieldID = FField.NO_FIELD_ID;
    for( int i = 0; i < desc.getFieldsSize() && keyFieldID == FField.NO_FIELD_ID; i++ ){
      FField field = desc.getFieldAt(i);
      if( field.getKey() && field.getID() > 0 ){
        keyFieldID = field.getID();
      }
    }
    return keyFieldID;
  }
  
  private FocObject getItem(String token, FocList list, boolean byKey){
    FocObject focObj = null;
    
    int left = token.indexOf("[");
    String key = token.substring(left+1, token.length()-1);
    
    if(byKey){
      int keyFieldID = getKeyFieldID(list.getFocDesc());
      if(keyFieldID != FField.NO_FIELD_ID){
        if(byKey){
        	focObj = list.searchByPropertyStringValue(keyFieldID, key, true);
        }
      }
    }else{
    	int idx = Integer.valueOf(key);
    	focObj = list.getFocObject(idx);
    }
    
    return focObj;
  }
  
  private FProperty getParentSameColumn(FocObject currentObject, FocObject originFocObject, FProperty initialProperty){
  	return currentObject.getSameProperty(originFocObject, initialProperty);
  }
  
  public void parsePath(String str, FocDesc initialFocDesc, FocObject initialObject, FProperty initialProperty){
    FProperty currProp   = null;
    FField    currField  = null;    
    if(str != null && initialFocDesc!= null){
      FocDesc         currDesc   = initialFocDesc;
      FocObject       currObject = initialObject;

      StringTokenizer strTok     = new StringTokenizer(str, ":.");
      while(strTok.hasMoreTokens()){
        String nT = strTok.nextToken();
        if(currDesc != null){
          int fieldId = FField.NO_FIELD_ID;

          //This is for the analysis fields because they are not in the FocDesc
          //-------------------------------------------------------------------
          currField = currDesc.getFieldByName(nT);
          if(currField == null && currObject != null){
          	currField = currObject.getFieldByName(nT);
          }
          //Before    currField = currDesc.getFieldByName(nT);
          //-------------------------------------------------------------------

          if(currField == null){
            if(nT.startsWith(CONTEXT_NODE_CODE) || nT.startsWith(CONTEXT_INDEX)){
            	FocList list = null;
              if(currProp instanceof FList){
                FList fList = (FList) currProp;
                list = fList.getList();
              }else if(currObject != null && currObject.getFatherSubject() instanceof FocList){
                list = (FocList)currObject.getFatherSubject();
              }

              if(list != null){
                FocObject focObj = getItem(nT, list, nT.startsWith(CONTEXT_NODE_CODE));
                if( focObj != null ){
                  currDesc   = focObj.getThisFocDesc();
                  currObject = focObj;
                  locatedFocObject = focObj;
                }
              }
            }else if(			nT.equalsIgnoreCase(CONTEXT_PARENT)
            					||  nT.equalsIgnoreCase(CONTEXT_FATHER)){
              FocObject focObj = currObject.getFatherObject();
              if( focObj != null ){
                currDesc   = focObj.getThisFocDesc();
                currObject = focObj;
                locatedFocObject = focObj;
              }
            }else if(nT.equalsIgnoreCase(CONTEXT_SAME_COL) && initialProperty != null){
              
              currProp = getParentSameColumn(currObject, initialObject, initialProperty);
              if(currProp != null){
                currDesc   = currProp.getFocField().getFocDesc();
                currObject = currProp.getFocObject();
                locatedFocObject = currObject;
              }
            }	              
          }else{
            fieldId    = currField.getID();
            pathArray.add(fieldId);
            currProp   = currObject != null ? currObject.getFocProperty(fieldId) : null;
            
            if(currField instanceof FTypedObjectField){
              currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
              if(currObject != null){
                currDesc   = currObject.getThisFocDesc();
              }
            }else{
              currDesc   = currField.getFocDesc();
              currObject = (currProp != null && currProp.isObjectProperty()) ? ((FObject) currProp).getObject_CreateIfNeeded() : null;
              if(currDesc == null && currObject != null){
                currDesc = currObject.getThisFocDesc();
              }
            }
          }
        }
      }
    }
    
    if(currProp != null){
      locatedProperty  = currProp;
      locatedFocObject = currProp.getFocObject();
    }
    locatedField = currField;    
    
  }
}
