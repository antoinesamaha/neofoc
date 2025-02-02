/*
 * Created on 19-May-2005
 */
package com.fab.parameterSheet;

import java.util.*;

import com.fab.FabModule;
import com.foc.ClassFocDescDeclaration;
import com.foc.desc.*;
import com.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class ParameterSheetFactory {
  private static HashMap<Integer, ParameterSheet> paramSetList = null;
  private static int defaultId = -9;
  private static final int EMPTY_PARAM_SET_ID = 0;
  private static final String EMPTY_PARAM_SET_TITLE = "Empty";
  private static ArrayList<FDescField> filledFDescFieldList = null;

  public static final int PREDEFINED_PARAMSET_ID_EMPLOYEE = 1000; 
  public static final String PREDEFINED_PARAMSET_NAME_EMPLOYEE = "Employee";  
  
  public static HashMap<Integer, ParameterSheet> getHashMap(){
    if(paramSetList == null){
      paramSetList = new HashMap<Integer, ParameterSheet>();
    }
    return paramSetList;
  }
  
  public static void addParameterSet(ParameterSheet paramSet){
    HashMap<Integer, ParameterSheet> paramSetList = getHashMap();
    if(paramSet != null && paramSetList != null){
      paramSetList.put(paramSet.getId(), paramSet);
      addNewlyAddedParamSetToAllFilledFDescFields(paramSet);
    }
  }

  /*Not in use*/public static ParameterSheet getParameterSet(int id){/*Not in use*/
    ParameterSheet paramSet = null;
    HashMap<Integer, ParameterSheet> paramSetList = getHashMap();
    if(paramSetList != null){
      paramSet = paramSetList.get(id);
    }
    return paramSet;
  }

  public static ParameterSheet getParameterSet(FocDesc focDesc){
    ParameterSheet foundParamSet = null;
    HashMap<Integer, ParameterSheet> paramSetList = getHashMap();
    if(paramSetList != null){
      Iterator iter = iterator();
      while(iter != null && iter.hasNext()){
        ParameterSheet paramSet = (ParameterSheet) iter.next();
        if(paramSet != null){
          if(paramSet.getFocDesc() == focDesc){
            foundParamSet = paramSet;
          }
        }
      }
    }
    return foundParamSet;
  }
  
  public static void setEmptyParamSetAsDefaultParamSet(){
  	ParameterSheet emptyParameterSet = new ParameterSheet(ParameterSheetFactory.EMPTY_PARAM_SET_ID, ParameterSheetFactory.EMPTY_PARAM_SET_TITLE, new ClassFocDescDeclaration(FabModule.getInstance(), Empty.class));
  	addParameterSet(emptyParameterSet);
  	setDefaultId(ParameterSheetFactory.EMPTY_PARAM_SET_ID);
  }
  
  private static ArrayList<FDescField> getFilledFDescFieldList(){
  	if(ParameterSheetFactory.filledFDescFieldList == null){
  		ParameterSheetFactory.filledFDescFieldList = new ArrayList<FDescField>();
  	}
  	return ParameterSheetFactory.filledFDescFieldList;
  }
  
  private static void addFilledFDescField(FDescField fDescField){
  	ArrayList<FDescField> fDescFieldList = getFilledFDescFieldList();
  	fDescFieldList.add(fDescField);
  }
  
  private static void addNewlyAddedParamSetToAllFilledFDescFields(ParameterSheet newlyAddedParamSet){
  	Iterator<FDescField> iter = getFilledFDescFieldList().iterator();
  	while(iter != null && iter.hasNext()){
  		FDescField descField = iter.next();
  		descField.addFocDescChoice(newlyAddedParamSet.getId(), newlyAddedParamSet.getName(), newlyAddedParamSet.getIFocDescDeclaration());
  	}
  	TypedParameterSheet.getObjectTypeMap().put(newlyAddedParamSet.getId(), newlyAddedParamSet.getName(), newlyAddedParamSet.getIFocDescDeclaration());
  }
  
  public static Iterator iterator(){
    Iterator iter = null;
    HashMap<Integer, ParameterSheet> paramSetList = getHashMap();
    if(paramSetList != null){
      iter = paramSetList.values().iterator();
    }
    return iter;
  }
  
  /*Not in use*/public static void fillMultipleChoice(FMultipleChoiceField multipleChoice){/*Not in use*/
    if(multipleChoice != null){
      Iterator iter = iterator();      
      while(iter != null && iter.hasNext()){
        ParameterSheet paramSet = (ParameterSheet) iter.next();
        multipleChoice.addChoice(paramSet.getId(), paramSet.getName());      
      }
    }
  }

  public static void fillDescFieldChoice(FDescField descField){
    if(descField != null){
      Iterator iter = iterator();      
      while(iter != null && iter.hasNext()){
        ParameterSheet paramSet = (ParameterSheet) iter.next();
        //descField.addFocDescChoice(paramSet.getId(), paramSet.getName(), paramSet.getFocObjectClass());      
        descField.addFocDescChoice(paramSet.getId(), paramSet.getName(), paramSet.getIFocDescDeclaration());
      }
      addFilledFDescField(descField);
    }
  }

  public static void fillDescFieldChoice(FDescFieldStringBased descField){
    if(descField != null){
      Iterator iter = iterator();      
      while(iter != null && iter.hasNext()){
        ParameterSheet paramSet = (ParameterSheet) iter.next();
        descField.addChoice(paramSet.getFocDesc().getStorageName());
      }
      //addFilledFDescField(descField);
    }
  }
  
  public static int getDefaultId(){
    if(defaultId < 0){
      Iterator iter = iterator();      
      if(iter != null && iter.hasNext()){
        ParameterSheet paramSet = (ParameterSheet) iter.next();
        defaultId = paramSet.getId();
      }
    }
    return defaultId;
  }
  
  public static void setDefaultId(int defaultId) {
    ParameterSheetFactory.defaultId = defaultId;
  }
}
