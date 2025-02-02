/*
 * Created on Jun 27, 2005
 */
package com.foc.desc.field;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 01Barmaja
 */
public class FFieldContainer {
  private ArrayList list = null;
  private HashMap<Integer, FField> mapByID           = null;
  private HashMap<String , FField> mapByName         = null;
  private HashMap<String , FField> mapByNameWithDots = null;
  
  public FFieldContainer(){
    list = new ArrayList();
    mapByID   = new HashMap<Integer, FField>();
    mapByName = new HashMap<String, FField>();
  }
  
  public void dispose(){
    if(list != null){
      list.clear();
      list = null;
    }
    if(mapByID != null){
      mapByID.clear();
      mapByID = null;
    }
    if(mapByName != null){
      mapByName.clear();
      mapByName = null;
    }
    if(mapByNameWithDots != null){
    	mapByNameWithDots.clear();
    	mapByNameWithDots = null;
    }
  }
  
  private String getNameWithoutDot(String name){
	  String  fieldNameWithoutDot = name;
		if(fieldNameWithoutDot.contains(".")){
			fieldNameWithoutDot = fieldNameWithoutDot.substring(fieldNameWithoutDot.indexOf(".")+1);
		}
		return fieldNameWithoutDot;
  }
  
  public void add(FField field){
    list.add(field);
    mapByID.put(field.getID(), field);
    Object obj = mapByName.put(getNameWithoutDot(field.getName()), field);
    if(field.getName().contains(".")){
    	if(mapByNameWithDots == null){
    		mapByNameWithDots = new HashMap<String, FField>();
    	}
    	mapByNameWithDots.put(field.getName(), field);
    }
  }
  
  public void remove(FField field){
    list.remove(field);
    mapByID.remove(field.getID());
    mapByName.remove(getNameWithoutDot(field.getName()));
    if(mapByNameWithDots != null) mapByNameWithDots.remove(field.getName());
  }
  
  public int size(){
    return list.size();
  }

  public FField get(int i){
    return (list != null) ? (FField) list.get(i) : null;
  }

  public FField getByID(int id){
    return mapByID.get(id);
  }

  public FField getByName_NoChecks(String name){
  	return mapByName.get(name);
  }
  
  public FField getByName(String orgName){
  	String name  = orgName.replace('-', '.');
  	FField found = mapByNameWithDots != null ? mapByNameWithDots.get(name) : null;
  	if(found == null){
  		found = getByName_NoChecks(getNameWithoutDot(name));
//  		found = mapByName.get(getNameWithoutDot(name));
  	}
  	
  	/*
  	String name           = orgName.replace('-', '.');
  	
  	//For a less constraining condition
  	//---------------------------------
  	boolean nameWithoutDotIsDifferent = false;
  	String nameWithoutDot = name;
  	if(nameWithoutDot.contains(".")){
  		nameWithoutDot = nameWithoutDot.substring(nameWithoutDot.indexOf(".")+1);
  		nameWithoutDotIsDifferent = true;
  	}
  	//---------------------------------
    
  	FField found           = null;
  	FField foundWithoutDot = null;
    for(int i=0; i<size(); i++){
      FField field = get(i);
            
      if(name.compareTo(field.getName()) == 0){
        found = field;
        break;
      }
    	
      //For a less constraining condition
      //---------------------------------
      boolean fieldNameWithoutDotIsDifferent = false;
      String  fieldNameWithoutDot = field.getName();
    	if(fieldNameWithoutDot.contains(".")){
    		fieldNameWithoutDot = fieldNameWithoutDot.substring(fieldNameWithoutDot.indexOf(".")+1);
    		fieldNameWithoutDotIsDifferent = true;
    	}

    	if(fieldNameWithoutDotIsDifferent || nameWithoutDotIsDifferent){
	      if(nameWithoutDot.compareTo(fieldNameWithoutDot) == 0){
	      	foundWithoutDot = field;
	      }
    	}
    	//---------------------------------
    }
    
    if(found == null) found = foundWithoutDot;
    */
    //In this case we look for a similar one that contains the expression without the prefixes if the result is unique
    //----------------------------------------------------------------------------------------------------------------
//    if(found == null){
//    	if(name.contains(".")) name = name.substring(name.indexOf(".")+1);
//      for(int i=0; i<size(); i++){
//        FField field = get(i);
//        String fldName = field.getName();
//      	if(fldName.contains(".")) fldName = fldName.substring(fldName.indexOf(".")+1);
//        if(name.compareTo(fldName) == 0){
//          found = field;
//          break;
//        }
//    	}
//    }
    //----------------------------------------------------------------------------------------------------------------
    return found;
  }  
}
