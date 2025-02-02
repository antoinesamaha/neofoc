/*
 * Created on 20-Apr-2005
 */
package com.fab.parameterSheet;

import java.util.Iterator;

import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class TypedParameterSheet extends FocTypedObject {
  
  public static FocDesc focDesc = null;
  
  public static ObjectTypeMap objectTypeMap = null;

  public static ObjectTypeMap getObjectTypeMap(){
    if(objectTypeMap == null){
      objectTypeMap = new ObjectTypeMap(); 
      Iterator iter = ParameterSheetFactory.iterator();
      while (iter != null && iter.hasNext()) {
        ParameterSheet paramSet = (ParameterSheet) iter.next();
        if (paramSet != null) {
          //FocList list = new FocList(new FocLinkSimple(paramSet.getFocDesc()));
          objectTypeMap.put(paramSet.getId(), paramSet.getName(), paramSet.getIFocDescDeclaration()/*list*/);
        }
      }    
      objectTypeMap.setDefaultType(ParameterSheetFactory.getDefaultId());
    }
    
    return objectTypeMap;
  }
  
  public TypedParameterSheet(FocConstructor constr) {
    super(constr);
    //initialize(getObjectTypeMap(), 1);
    //Globals.logString("Should not get into this constructor!!");
  }

  /*
  public TypedParameterSheet(FocConstructor constr, ObjectTypeMap typedListMap) {
    super(constr);
    initialize(typedListMap, 1);
  }
  */
  
  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      focDesc = new FocDesc(TypedParameterSheet.class, FocDesc.NOT_DB_RESIDENT, "TPD_PARAM_SET", false);
      focDesc.addReferenceField();
      
      //fillFocDesc(focDesc, getObjectTypeMap(), "PARAM_");
    }
    return focDesc;
  }
}
