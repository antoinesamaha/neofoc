/*
 * Created on 21-Apr-2005
 */
package com.foc.desc;

import java.util.*;

import com.foc.IFocDescDeclaration;
import com.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public class ObjectTypeMap {
  private HashMap<Integer, ObjectType> typesListMap = null;
  private int defaultType = -1;

  public ObjectTypeMap(){
    typesListMap = new HashMap<Integer, ObjectType>();
    defaultType = -1;
  }
  
  public void dispose(){
    if( typesListMap != null ){
      
      Iterator iter = typesListMap.values().iterator();
      while( iter != null && iter.hasNext() ){
        ObjectType oType = (ObjectType)iter.next();
        if( oType != null ){
          oType.dispose();
        }
      }
      
      typesListMap.clear();
      typesListMap = null;
    }
  }

  /*public ObjectTypeMap(ObjectTypeMap sourceMap){
    typesListMap = new HashMap();
    Iterator iter = sourceMap.typesListMap.values().iterator();
    while(iter != null && iter.hasNext()){
      ObjectType sourceType = (ObjectType) iter.next();
      if(sourceType != null){
        ObjectType targetType = new ObjectType(sourceType);
        typesListMap.put(Integer.valueOf(targetType.getId()), targetType);
      }
    }
    defaultType = sourceMap.getDefaultType();
  }*/
  
  /*Not in use*/public void put(int type, String title, FocList list){/*Not in use*/
    ObjectType oType = new ObjectType(type, title, list);
    typesListMap.put(Integer.valueOf(type), oType);
  }

  public void put(int type, String title, IFocDescDeclaration iFocDescDeclaration){
    ObjectType oType = new ObjectType(type, title, iFocDescDeclaration);
    typesListMap.put(Integer.valueOf(type), oType);
  }
  
  public void setListForType(int type, FocList list){
    ObjectType objType = get(type);
    if(objType != null){
      objType.setSelectionList(list);
    }
  }
  
  public ObjectType get(int type){
    ObjectType objType = null;
    if(typesListMap != null){
      objType = (ObjectType) typesListMap.get(Integer.valueOf(type));
    }
    return objType;
  }
  
  public Iterator iterator(){
    Collection values = typesListMap.values(); 
    return values != null ? values.iterator() : null;
  }
  
  public void setDefaultType(int type){
    this.defaultType = type;
  }
  
  public int getDefaultType(){
    int ret = -1;
    if(typesListMap.containsKey(Integer.valueOf(defaultType))){
      ret = defaultType;
    }else{
      Collection coll = typesListMap.entrySet();
      if(coll != null){
        Iterator iter = coll.iterator();
        if(iter != null && iter.hasNext()){
          Integer integ = (Integer) iter.next();
          ret = integ.intValue();
        }
      }
    }
    return ret;
  }

  public ObjectType findObjectType(FocDesc focDesc){
    ObjectType foundObjType = null; 
    Iterator iter = iterator();
    while(iter != null && iter.hasNext()){
      ObjectType objType = (ObjectType)iter.next();
      if(objType.getFocDesc() == focDesc){
        foundObjType = objType; 
      }
    }
    return foundObjType;
  }
  
  public ObjectType findObjectType(FocObject obj){
    return findObjectType(obj.getThisFocDesc());
  }
}
