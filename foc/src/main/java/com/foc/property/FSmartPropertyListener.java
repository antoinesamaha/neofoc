/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.util.*;

import com.foc.desc.*;
import com.foc.desc.field.*;

/**
 * @author 01Barmaja
 */
public class FSmartPropertyListener implements FPropertyListener {

  private FFieldPath watchPath = null;
  private FocObject rootFixedFocObject = null;
  private ArrayList<FProperty> listOfPropertiesListenedTo = null;

  private ArrayList<FPropertyListener> listeners = null;

  public FSmartPropertyListener(FocObject rootFixedFocObject, FFieldPath watchPath) {
    this.watchPath = watchPath;
    this.rootFixedFocObject = rootFixedFocObject;
    listOfPropertiesListenedTo = new ArrayList<FProperty>();
    listeners = new ArrayList<FPropertyListener>();

    resetInternalListeners();
  }

  public void dispose(){
    if(listOfPropertiesListenedTo != null){
      removeAllInternalListeners();
      listOfPropertiesListenedTo = null;
    }
    if(watchPath != null){
      //watchPath.dispose();
      watchPath = null;
    }
    rootFixedFocObject = null;
    
    if(listeners != null){
      Iterator iter = listeners.iterator();
      while(iter != null && iter.hasNext()){
        FPropertyListener propListener = (FPropertyListener) iter.next();
        if(propListener != null){
          propListener.dispose();
        }
        iter.remove();           
      }
      listeners.clear();
      listeners = null;
    }
  }  
  
  public void propertyModified(FProperty property) {
    resetInternalListeners();
    notifyListeners();
  }

  private void removeListenersStartingFromIndex(int index) {
  	if(listOfPropertiesListenedTo != null){
	    for (int i = index; i < listOfPropertiesListenedTo.size(); i++) {
	      FProperty prop = (FProperty) listOfPropertiesListenedTo.get(i);
	      prop.removeListener(this);
	    }
	    for (int i = listOfPropertiesListenedTo.size() - 1; i >= index; i--) {
	      listOfPropertiesListenedTo.remove(i);
	    }
  	}
  }

  private void listenToProperty(FProperty prop) {
    if (prop != null) {
      listOfPropertiesListenedTo.add(prop);
      prop.addListener(this);
    }
  }

  private void resetInternalListeners() {
    FProperty property = null;
    FocObject focObject = rootFixedFocObject;
    FProperty propertyListenedTo = null;

    if(watchPath != null){
      for (int i = 0; i < watchPath.size() && focObject != null; i++) {
        int fieldId = watchPath.get(i);
        property = focObject.getFocProperty(fieldId);
        propertyListenedTo = i < listOfPropertiesListenedTo.size() ? (FProperty) listOfPropertiesListenedTo.get(i) : null;
  
        if (propertyListenedTo != property) {
          if (propertyListenedTo != null) {
            removeListenersStartingFromIndex(i);
            propertyListenedTo = null;
          }
          if (property != null) {
            listenToProperty(property);
          }
        }
  
        if (property != null && property.isObjectProperty()) {
          focObject = (FocObject) property.getObject();
        } else {
          focObject = null;
        }
      }
    }
  }

  public void addListener(FPropertyListener propListener) {
    listeners.add(propListener);
  }
  
  public void removeListener(FPropertyListener propListener) {
    if(listeners != null){
      listeners.remove(propListener);
    }
  }

  public void removeAllInternalListeners() {
    this.removeListenersStartingFromIndex(0);
  }
  
  public void notifyListeners() {
    FProperty firstProperty = watchPath.getPropertyFromObject(rootFixedFocObject, 0);
    for (int i = 0; i < listeners.size(); i++) {
      FPropertyListener currListener = (FPropertyListener) listeners.get(i);
      if (currListener != null) {
        currListener.propertyModified(firstProperty);
      }
    }
  }
}
