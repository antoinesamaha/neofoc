/*
 * Created on Jan 9, 2006
 */
package com.foc.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.foc.Globals;
import com.foc.IFocEnvironment;
import com.foc.desc.FocDesc;
import com.foc.desc.field.FField;
import com.foc.desc.field.FMultipleChoiceStringField;
import com.foc.desc.field.FObjectField;
import com.foc.list.FocListGroupBy;
import com.foc.util.Utils;

/**
 * @author 01Barmaja
 */
public class FocRequestDesc {
  private FocDesc    focDesc = null;
  private TableAlias rootTableAlias = null;
  private LinkedHashMap<String, TableAlias> aliasMap = null;
  private ArrayList<FocRequestField>        fieldList = null;
  
  @Deprecated
  public FocRequestDesc(FocDesc mainDesc){
    aliasMap      = new LinkedHashMap<String, TableAlias>();
    fieldList     = new ArrayList<FocRequestField>();
  }

  public FocRequestDesc(){
    aliasMap      = new LinkedHashMap<String, TableAlias>();
    fieldList     = new ArrayList<FocRequestField>();
  }
  
  public void dispose(){
    focDesc  = null;
    if(aliasMap != null){
    	Iterator iter = aliasMap.values().iterator();
    	while(iter.hasNext()){
    		TableAlias tableAlias = (TableAlias) iter.next();
    		if(tableAlias != null){
    			tableAlias.dispose();
    		}
    	}
    	aliasMap.clear();
    	aliasMap = null;
    }
    if(fieldList != null){
	    for(int i=0; i<fieldList.size(); i++){
	    	FocRequestField reqFld = (FocRequestField) fieldList.get(i);
	    	if(reqFld != null){
	    		reqFld.dispose();
	    	}
	    }
	    fieldList.clear();
	    fieldList = null;
    }
    rootTableAlias = null;  	
  }
  
  public TableAlias getRootTableAlias(){
    return rootTableAlias;
  }
  
  public void addTableAlias(TableAlias tableAlias){
    aliasMap.put(tableAlias.getAlias(), tableAlias);
    if(tableAlias.getJoinCount() == 0){
      rootTableAlias = tableAlias ;
    }
  }

  public TableAlias getTableAlias(String tableAlias){
    return aliasMap != null ? aliasMap.get(tableAlias) : null;
  }

  public void addField(FocRequestField reqField){
  	if(reqField.getId() != FField.FLD_SYNC_IS_NEW_OBJECT){
  		fieldList.add(reqField);
  	}
  }

  public boolean containsField(int fldId){
  	boolean contains = false;
    Iterator iter = fieldList.iterator();
    while(iter != null && iter.hasNext() && !contains){
      FocRequestField reqField = (FocRequestField) iter.next();
      if(reqField != null && fldId == reqField.getId()){
      	contains = true;
      }
    }
    return contains;
  }

  public Iterator newFieldIterator(){
    return fieldList.iterator();
  }

  public Iterator newAliasIterator(){
    return aliasMap.values().iterator();
  }
  
  public Collection<TableAlias> newAliasArray(){
  	ArrayList<TableAlias> array = new ArrayList<TableAlias>(); 
    Iterator iter = newAliasIterator();
    while(iter != null && iter.hasNext()){
      TableAlias alias = (TableAlias) iter.next();
      if(alias != null){
      	array.add(alias);
      }
    }
    
  	Collections.sort(array, new Comparator<TableAlias>() {
			@Override
			public int compare(TableAlias ta1, TableAlias ta2) {
				return ta1.getOrder() - ta2.getOrder();
			}
		});

    return array;
  }
  
  public void fillFocDesc(FocDesc focDesc){
    if(focDesc != null){
      this.focDesc = focDesc;
      Iterator iter = fieldList.iterator();
      while(iter != null && iter.hasNext()){
        FocRequestField reqField = (FocRequestField) iter.next();
        if(reqField != null){
        	if(focDesc.getFieldByID(reqField.getId()) == null){
	          FField field    = reqField.getField();
	          FField newField = null;
	          try {
	          	if(field == null){
	          		Globals.showNotification("Field Not Found", "Could not find field:"+reqField.getId(), IFocEnvironment.TYPE_ERROR_MESSAGE);
	          	}else{
		            newField = (FField) field.cloneWithoutListeners();
			          newField.setJoinOriginalField(field);
		           	newField.setName(reqField.getTableAlias().getAlias()+"."+field.getName());
		            if(newField instanceof FObjectField){
		              FObjectField newObjField = (FObjectField) newField;
		              newObjField.setKeyPrefix(reqField.getTableAlias().getAlias()+"."+newObjField.getKeyPrefix());
		              if(!Utils.isStringEmpty(newObjField.getForcedDBName())){
		              	newObjField.setForcedDBName(reqField.getTableAlias().getAlias()+"."+newObjField.getForcedDBName());
		              }
		            }
		            newField.setId(reqField.getId());
		            if(newField instanceof FMultipleChoiceStringField){
		            	FMultipleChoiceStringField mcsFld = (FMultipleChoiceStringField) newField;
		            	mcsFld.setChoicesSelection_FieldID(field.getID());
		            }
								String groupByFormula = reqField.getGroupByFormula();
								if (!Utils.isStringEmpty(groupByFormula)) {
									FocListGroupBy groupBy = focDesc.getGroupBy();
									if (groupBy != null) {
										newField = groupBy.addField_FormulaSingleText(focDesc, newField, groupByFormula, reqField.getGroupByFormula_AdditionalField());
									} else {
										Globals.logString("Missing Group By expression.");
									}
								}
							}
	          } catch (CloneNotSupportedException e) {
	            Globals.logException(e);            
	          }
	          focDesc.addField(newField);
	          if(field.getIndexOfPropertyInDummyArray() > 0){
	          	focDesc.addDummyProperty(newField.getID());
	          }
        	}
        }
      }
    }
  }
  
  public void fillRequestDescWithJoinFields(){
  	fillRequestDescWithJoinFields(200);
  }
  
  public void fillRequestDescWithJoinFields(int firstFieldID){
    FocRequestField reqFld = new FocRequestField(firstFieldID++, getRootTableAlias(), FField.REF_FIELD_ID);
    addField(reqFld);
    
    Iterator iter = aliasMap.values().iterator();
    while(iter != null && iter.hasNext()){
      TableAlias tableAlias = (TableAlias) iter.next();
      if(tableAlias != null){
      	for(int i=0; i<tableAlias.getJoinCount(); i++){
	        Join join = tableAlias.getJoin(i);
	        if(join != null){
	          firstFieldID = join.fillRequestDescWithJoinFields(this, firstFieldID);
	        }
      	}
      }
    }
  }

  public FocDesc getFocDesc() {
    return focDesc;
  }
  
  public String getLinkCondition(){
    StringBuffer str = new StringBuffer();
    
    boolean firstCondition = true;
    
    Iterator iter = newAliasIterator();
    while(iter != null && iter.hasNext()){
      TableAlias alias =(TableAlias) iter.next();
      if(alias != null){
      	for(int i=0; i<alias.getJoinCount(); i++){
	        Join join = alias.getJoin(i);
	        if(join != null){
	          String joinCondition = join.getLinkCondition();
	          
	          if(joinCondition != null && joinCondition.length() > 0){
	            if(!firstCondition){
	              str.append(" AND "); 
	            }
	            str.append("(");
	            str.append(joinCondition);
	            str.append(")");
	            firstCondition = false;
	          }
	        }
      	}
      }
    }

    return str.toString();
  }
}
