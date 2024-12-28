/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.text.Format;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.desc.FocObject;
import com.foc.desc.field.FMultipleChoiceField;
import com.foc.desc.field.FMultipleChoiceItem;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FMultipleChoice extends FInt implements IFMultipleChoiceProperty {
	
	private HashMap<Integer, FMultipleChoiceItem> localChoicesList = null;
	
	private HashMap<Integer, FMultipleChoiceItem> getLocalChoicesList(){
		if(this.localChoicesList == null){
			resetLocalSourceList();
		}
		return this.localChoicesList;
	}
	
	public void resetLocalSourceList(){
		this.localChoicesList = new HashMap<Integer, FMultipleChoiceItem>();
	}

	public void removeLocalSourceList(){
		this.localChoicesList = null;
	}
	
	public void addLocalChoice(int id, String title){
		FMultipleChoiceItem item = new FMultipleChoiceItem(id, title);
		getLocalChoicesList().put(id, item);
	}
	
	private Iterator getLocalChoiceIterator() {
    Iterator iter = null;
    if (localChoicesList != null) {
      Collection coll = localChoicesList.values();
      if (coll != null) {
        iter = coll.iterator();
      }
    }
    return iter;
  }
	
  public Iterator getChoiceIterator() {
    Iterator iter = getLocalChoiceIterator();
    if(iter == null){
	    FMultipleChoiceField field = (FMultipleChoiceField) this.getFocField();
	    if (field != null) {
	      iter = field.getChoiceIterator();
	    }
    }
    return iter;
  }
  
  public FMultipleChoiceItem getChoiceItemForKey(int key){
  	FMultipleChoiceItem multipleChoiceItem = null;
  	if(this.localChoicesList != null){
  		multipleChoiceItem = this.localChoicesList.get(key);
  	}else{
  		FMultipleChoiceField fMultField = (FMultipleChoiceField) getFocField();
      if (fMultField != null) {
      	multipleChoiceItem = fMultField.getChoiceItemForKey(iVal);
      }
  	}
  	return multipleChoiceItem;
  }

  public FMultipleChoice(FocObject focObj, int fieldID, int iVal) {
    super(focObj, fieldID, iVal);
  }

  public int compareTo(FProperty prop) {
    return (prop != null) ? this.getString().compareTo(prop.getString()) : 1;
  }
  
  public void setSqlStringInternal(String str) {
    if (str == null || str.compareTo("") == 0) {
      setInteger(0, false, false);
    } else {
      setInteger(Integer.parseInt(str), false, false);
    }
  }

  public String getSqlString() {
    return String.valueOf(iVal);
  }

  /*public String getString() {
    String ret = "";
    FMultipleChoiceField fMultField = (FMultipleChoiceField) getFocField();
    if (fMultField != null) {
      FMultipleChoiceItem item = fMultField.getChoiceItemForKey(iVal);
      if (item != null) {
        ret = item.getTitle();
      }
    }
    return ret;
  }*/
  
  public String getString() {
    String ret = "";
    FMultipleChoiceItem item = getChoiceItemForKey(iVal);
    if (item != null) {
      ret = item.getTitle();
    }
    return ret;
  }

  public void setString(String str) {
    if (str == null || str.compareTo("") == 0) {
      setInteger(0);
    } else {
      Iterator iter = getChoiceIterator();
      while (iter != null && iter.hasNext()) {
        FMultipleChoiceItem item = (FMultipleChoiceItem) iter.next();
        if (item.getTitle().compareTo(str) == 0) {
          setInteger(item.getId());
          break;
        }
      }
    }
  }

  public Object getXMLValue(){
    return getString();
  }
  
  public void setObject(Object obj) {
    if (obj != null) {
    	//try{
    		setString((String) obj);
    	//}catch(Exception e){
    	//	Globals.logException(e);
    	//}
    }
  }

  public Object getObject() {
    return getString();
  }

  @Override
  public Object vaadin_TableDisplayObject(Format format, String captionProperty){
  	return getString();
  }
  
  //-------------------------------
  // VAADIN Property implementation
  //-------------------------------
  
  @Override
  public Object getValue() {
  	return getChoiceItemForKey(getInteger());
  }

  @Override
  public void setValue(Object newValue) {
  	FMultipleChoiceItem multiItem = (FMultipleChoiceItem) newValue;
  	if(multiItem != null){
  		setInteger((Integer) multiItem.getId());
  	}
  }
}
