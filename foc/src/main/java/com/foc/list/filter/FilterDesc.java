/*
 * Created on Sep 9, 2005
 */
package com.foc.list.filter;

import java.util.*;

import com.foc.business.dateShifter.DateShifterDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.desc.field.FFieldPath;
import com.foc.desc.field.FStringField;

/**
 * @author 01Barmaja
 */
public class FilterDesc {
  private FocDesc                    subjectFocDesc  = null;
  private ArrayList<FilterCondition> conditionList   = null;
  private int                        nbrOfGuiColumns = 1   ;
//  private boolean                    hasOneMemoryOnlyConditionAtLeast = false; 
  
  private HashMap<Integer, DateShifterDesc> dateShifterDescMap = null; 
  
  public FilterDesc(FocDesc subjectFocDesc){
    this.subjectFocDesc = subjectFocDesc ;
    
    if (subjectFocDesc != null && subjectFocDesc.isRevisionSupportEnabled()){
      RevisionCondition cond = new RevisionCondition(FFieldPath.newFieldPath(FField.CREATION_REVISION_FIELD_ID), "C_R");
      addCondition(cond);
      
    }
  }
  
  public void dispose() {
  	if(dateShifterDescMap != null) {
  		Iterator iter = dateShifterDescMap.values().iterator();
  		while(iter != null && iter.hasNext()) {
  			DateShifterDesc dateShifterDesc = (DateShifterDesc) iter.next();
  			if(dateShifterDesc != null) dateShifterDesc.dispose();
  		}
  		dateShifterDescMap.clear();
  	}
  	
  	if(conditionList != null) {
  		for(int i=0; i<conditionList.size(); i++) {
  			FilterCondition condition = conditionList.get(i);
  			condition.dispose();
  			condition = null;
  		}
  		conditionList.clear();
  		conditionList = null;
  	}
  	
  	subjectFocDesc = null;
  }

  public String getDbSourceKey(){
  	return subjectFocDesc != null ? subjectFocDesc.getDbSourceKey() : null;
  }

  public int getProvider(){
  	return subjectFocDesc != null ? subjectFocDesc.getProvider() : null;
  }
  
  private ArrayList<FilterCondition> getConditionList(boolean create){
    if(conditionList == null && create){
      conditionList = new ArrayList<FilterCondition>();
    }
    return conditionList;
  }
  
  public void addCondition(FilterCondition cond){
    ArrayList<FilterCondition> condLst = getConditionList(true);
    condLst.add(cond);
    cond.setFilterDesc(this);
//    if(cond.isMemoryOnly()) hasOneMemoryOnlyConditionAtLeast = true;
  }

  public int getConditionCount(){
    ArrayList condLst = getConditionList(false);
    return condLst != null ? condLst.size() : 0; 
  }
  
  public FilterCondition getConditionAt(int i){
    ArrayList condLst = getConditionList(false);
    return condLst != null ? (FilterCondition) condLst.get(i) : null;
  }
  
  public FilterCondition findConditionByFieldPrefix(String prefix){
    FilterCondition foundCond = null;
    for(int i=0; i<getConditionCount(); i++){
      FilterCondition cond = getConditionAt(i);
      if(cond.getFieldPrefix().compareTo(prefix) == 0){
        foundCond = cond;
        break;
      }
    }       
    return foundCond;
  }
  
  public void fillProperties(FocObject fatherObject){    
    for(int i=0; i<getConditionCount(); i++){
      FilterCondition cond = getConditionAt(i);
      cond.fillProperties(fatherObject);
    }
  }
  
  public int fillDesc(FocDesc focDesc, int idStart){
		//This will be managed automatically to store the Filter Description, a summary sentence
  	if(focDesc != null){
			FStringField descriptionField = focDesc.addDescriptionField();
			descriptionField.setSize(1000);
  	}
  	
    for(int i=0; i<getConditionCount(); i++){
      FilterCondition cond = getConditionAt(i);
      idStart = cond.fillDesc(focDesc, idStart);
    }
    return idStart;
  }
  
  public FocDesc getSubjectFocDesc() {
    return subjectFocDesc;
  }

	public int getNbrOfGuiColumns() {
		return nbrOfGuiColumns;
	}

	public void setNbrOfGuiColumns(int nbrOfGuiColumns) {
		this.nbrOfGuiColumns = nbrOfGuiColumns;
	}
	
	public void putDateShifterDesc(int key, DateShifterDesc dateShifterDesc) {
		if(dateShifterDescMap == null) {
			dateShifterDescMap = new HashMap<Integer, DateShifterDesc>();
		}
		if(dateShifterDescMap != null) {
			dateShifterDescMap.put(key, dateShifterDesc);
		}
	}
	
	public DateShifterDesc getDateShifterDesc(int key) {
		DateShifterDesc dateShifterDesc = null;
		if(dateShifterDescMap != null) {
			dateShifterDesc = dateShifterDescMap.get(key);  
		}
		return dateShifterDesc;
	}

//	public boolean isHasOneMemoryOnlyConditionAtLeast() {
//		return hasOneMemoryOnlyConditionAtLeast;
//	}
}
