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
package com.foc.jasper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FIntField;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FNumField;
import com.foc.list.FocList;
import com.foc.list.filter.IFocListFilter;
import com.foc.property.FProperty;
import com.foc.shared.dataStore.IFocData;
import com.foc.util.Utils;

public class JRFocListDataSource implements JRDataSource{

	//private HashMap<String, FocObject> parameterObjetsMap = null;
  private FocList   	   list       = null;
  private int       	   i          = 0   ;
  private IFocListFilter listFilter = null;
  
  public JRFocListDataSource(){
    this.list = null;
    i = 0;
  }
  
  public JRFocListDataSource(FocList list){
    this.list = list;
    i = 0;
  }
  
  public void dispose(){
  	list       = null;
  	listFilter = null;
  }
  
  public FocObject getCurrentFocObject(){
  	return i-1 < list.size() ? list.getFocObject(i-1) : null;
  }
  
  public boolean isCurrentFocObjectVisible(){
  	FocObject obj      = getCurrentFocObject();
  	boolean   included = obj != null;
  	if(included && listFilter != null){
  		included = listFilter.includeObject(obj);
  	}
  	return included;
  }

  @Override
  public Object getFieldValue(JRField jrField) throws JRException {
  	return getFieldValue_Static(getCurrentFocObject(), i, jrField);
  }

  //JRDataSource
  public boolean next() throws JRException {
  	boolean next = list != null;
  	if(next){
	  	do{
	  		i++;
	  	}while(i < list.size() && !isCurrentFocObjectVisible());
	  	next = isCurrentFocObjectVisible();
  	}
    return next;
  }

	public void setListFilter(IFocListFilter listFilter) {
		this.listFilter = listFilter;
	}
	
	
	public static synchronized Object getFieldValue_Static(FocObject obj, int index, JRField jrField) throws JRException {
  	Object retObj = null;
  	
  	if(jrField.getName().equals("LINE_NO")){
  		retObj = Integer.valueOf(index);
  	}else if(jrField.getName().equals("TREE_NODE_INDENT_LABEL")){
  		String treeNodeIndentLabel = "";
  		while(obj != null && obj.getFatherObject() != null){
  			treeNodeIndentLabel += "   ";
  			obj = obj.getFatherObject();
  		}
  		retObj = treeNodeIndentLabel;
  	}else{
	    //New Way 2015-10-05
	    IFocData focData = obj.iFocData_getDataByPath(jrField.getName());
	    if(focData != null){
	    	if(focData instanceof FProperty){
	    		FProperty prop = (FProperty) focData;	    		
//	    		if(focData instanceof FBlobStringProperty && prop.getObject() != null && prop.getObject() instanceof String){
	    		if(prop.getObject() != null && prop.getObject() instanceof String){//&& prop instanceof FBlobStringProperty
//	    			retObj = Jsoup.parse(String.valueOf(prop.getObject())).text();
	    			String val = String.valueOf(prop.getObject());
	    			retObj = Utils.htmlToText(val);
	    		}else{
	    			retObj = prop.getObject();
	    		}
	    	}else if(focData instanceof FField){
	    		FField field = (FField) focData; 
			    if(field instanceof FNumField){
			    	FNumField numField = (FNumField) field;
			    	retObj = numField.getZeroValue();
			    }else if(field instanceof FMultipleChoiceField){
			    	retObj = "";
			    }else if(field instanceof FIntField){
			    	retObj = 0;
			    }
	    	}else{
	    		retObj = focData.iFocData_getValue();
	    	}
	    }
	    
	    if(retObj == null){
	    	if(jrField.getValueClass() == Double.class){
	    		retObj = new Double(0);
	    	}else if(jrField.getValueClass() == Integer.class){
	    		retObj = new Integer(0);
	    	}else if(jrField.getValueClass() == Number.class){
	    		retObj = new Double(0);
	    	}else if(jrField.getValueClass() == String.class){
	    		retObj = "";
	    	}
	    }
      //---------------------------------
  	}
    
    return retObj;
  }
}
