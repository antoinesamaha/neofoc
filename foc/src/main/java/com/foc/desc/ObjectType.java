/*
 * Created on 20-Apr-2005
 */
package com.foc.desc;

import com.foc.IFocDescDeclaration;
import com.foc.desc.field.*;
import com.foc.list.*;

/**
 * @author 01Barmaja
 */
public class ObjectType implements FMultipleChoiceItemInterface {
  private int type = 0;
  private String title = null;
  private IFocDescDeclaration iFocDescDeclaration = null;
  private FocList selectionList = null;
  private FocDesc focDesc = null; 

  public ObjectType(int type, String title, IFocDescDeclaration iFocDescDeclaration){
    this.type = type;
    this.title = title;
    this.iFocDescDeclaration = iFocDescDeclaration;
    this.selectionList = null;
  }
  
  public ObjectType(int type, String title, FocList selectionList){
    this.type = type;
    this.title = title;
    this.iFocDescDeclaration = null;
    this.selectionList = selectionList;
  }
  
  public void dispose(){
    iFocDescDeclaration = null;
    selectionList = null;
  }
    
  public int getType(){
    return type;
  }

  //This method is an implementation of the MultipleChoiceInterface
  public int getId(){
    return getType();
  }
  
  public String getTitle(){
    return title;
  }
  
  public FocList getSelectionList(){
    return selectionList;
  }
  
  public void setSelectionList(FocList list){
    selectionList = list;
  }
  
  public FocDesc getFocDesc(){
  	if(focDesc == null){
	    if(selectionList != null){
	    	focDesc = selectionList.getFocDesc();
	    }
	    if(focDesc == null){
	    	focDesc = this.iFocDescDeclaration.getFocDescription();
	    }
  	}
    return focDesc; 
  }
}
