/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.util.*;

import com.foc.IFocDescDeclaration;
import com.foc.desc.*;
import com.foc.property.FDescProperty;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FDescField extends FMultipleChoiceField {

  //private HashMap<Integer, Class> choicesMap = null;  
	private HashMap<Integer, IFocDescDeclaration> choicesMap = null;
  
  public FDescField(String name, String title, int id, boolean key) {
    super(name, title, id, key, 5);
    //choicesMap = new HashMap<Integer, Class>();
    choicesMap = new HashMap<Integer, IFocDescDeclaration>();
  }
  
  public FProperty newProperty(FocObject masterObj, Object defaultValue){
    return new FDescProperty(masterObj, getID(), defaultValue != null? ((Integer)defaultValue).intValue() : 0);
  }
  
  public int getFieldDisplaySize(){ 
    return 20;
  }
  
  /*public void addFocDescChoice(int id, String title, Class choiceFocClass){
    addChoice(id, title);
    choicesMap.put(Integer.valueOf(id), choiceFocClass);
  }*/
  
  public void addFocDescChoice(int id, String title, IFocDescDeclaration iFocDescDeclaration){
    addChoice(id, title);
    choicesMap.put(Integer.valueOf(id), iFocDescDeclaration);
  }
  
  public FocDesc getChoiceFocDesc(int id){
    //return FocDesc.getFocDescriptionForDescClass((Class) choicesMap.get(Integer.valueOf(id)));
  	//return ClassFocDescDeclaration.getFocDescriptionForClass((Class)choicesMap.get(Integer.valueOf(id)));
  	IFocDescDeclaration iFocDescDeclaration = choicesMap.get(Integer.valueOf(id)); 
  	return iFocDescDeclaration.getFocDescription();
  }
}
