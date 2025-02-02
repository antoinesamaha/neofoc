/*
 * Created on Oct 14, 2004
 */
package com.foc.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.event.FocEvent;
import com.foc.event.FocListener;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.property.FSmartPropertyListener;

/**
 * @author 01Barmaja
 */
public class FocListListener {
  private FocList list = null;
  private InternalListListener listListener = null;
  private InternalPropertyListener propertyListener = null;  
  private ArrayList<FFieldPath> propertiesToListenTo = null;
  private HashMap mapObjectListeners = null;
  private ArrayList listeners = null;
  private ArrayList<ListOperation> operationArray = null;

  /**
   * @param list
   * 
   * List listener that automatically plugs listeners on each property of each object the FocList.
   *  
   * through the addProperty() method we can add properties to listen to.
   * 
   */
  public FocListListener(FocList list){
  	//setFocList(list);
    this.list            = list;
    propertiesToListenTo = new ArrayList<FFieldPath>();
    listListener         = new InternalListListener(this);
    propertyListener     = new InternalPropertyListener(this);
    mapObjectListeners   = new HashMap();
    listeners            = new ArrayList();
  }

  public FocListListener clone(FocList focList) {
    FocListListener newListListener = new FocListListener(focList);
    
    for(int i=0; i<propertiesToListenTo.size(); i++){
      FFieldPath path = (FFieldPath) propertiesToListenTo.get(i);
      if(path != null){
      	FFieldPath newPath = (FFieldPath) path.clone();
      	newListListener.addProperty(newPath);
      }
    }
    
    if(listeners != null){
    	for(int i=0; i<listeners.size(); i++){
    		newListListener.addListener((FocListener)listeners.get(i));
    	}
    }
    
    return newListListener;
  }
  
  public void dispose(){
    removeAllObjectListeners();
 
    if(mapObjectListeners != null){
      Iterator iter = mapObjectListeners.values().iterator();
      while(iter != null && iter.hasNext()){
        ArrayList listeners = (ArrayList) iter.next();
        if(listeners != null){
          for(int i=listeners.size()-1; i>=0; i--){
            FSmartPropertyListener propListener = (FSmartPropertyListener)listeners.get(i);
            if(propListener != null){
              propListener.dispose();
              propListener = null;
            }
          }      
        }
      }
      mapObjectListeners.clear();      
      mapObjectListeners = null;
    }
    
    if(listListener != null){
      listListener.dispose();
      listListener = null;
    }
 
    if(propertyListener != null){
      propertyListener.dispose();
      propertyListener = null;
    }

    if(propertiesToListenTo != null){
      for(int i=0; i<propertiesToListenTo.size(); i++){
        FFieldPath path = (FFieldPath) propertiesToListenTo.get(i);
        if(path != null){
          path.dispose();
        }
      }
      propertiesToListenTo.clear();
      propertiesToListenTo = null;
    }
     
    if(listeners != null){
      for(int i=0; i<listeners.size(); i++){
        FocListener listener = (FocListener) listeners.get(i);
        listener.dispose();
      }
      listeners.clear();
      listeners = null;
    }
    
    disposeOperationArray();
    operationArray = null;
    
    list = null;
  }
  
  public FocDesc getFocDesc(){
  	return getFocList() != null ? getFocList().getFocDesc() : null;
  }
  
  public void addProperty(FFieldPath fieldPath){
  	if(fieldPath != null){
  		propertiesToListenTo.add(fieldPath);
  	}
  }
  
  public void addProperty(String fieldPathString){
  	FocDesc focDesc = getFocDesc();
  	if(focDesc != null){
  		FFieldPath fieldPath = FFieldPath.newFFieldPath(focDesc, fieldPathString);
  		addProperty(fieldPath);
  	}
  }
  
  public FocList getFocList() {
    return list;
  }
  
  public void setFocList(FocList list) {
    this.list = list;
  }

  public class ListAndPropertyListener implements FPropertyListener {
		public void dispose(){
		}

		public void propertyModified(FProperty property){
			
		}
  }
  
  /*
  public ListAndPropertyListener getFocListenerForDirectPropertiesThatDoNotNeedSmartListeners(){
  	if(focListenerForDirectPropertiesThatDoNotNeedSmartListeners == null){
  		focListenerForDirectPropertiesThatDoNotNeedSmartListeners = new ListAndPropertyListener();
  	}
  	return focListenerForDirectPropertiesThatDoNotNeedSmartListeners;
  }
  */
  
  private void removeObjectListeners(FocObject obj){
    ArrayList listeners = (ArrayList) mapObjectListeners.get(obj);
    if(listeners != null){
      for(int i=listeners.size()-1; i>=0; i--){
        FSmartPropertyListener propListener = (FSmartPropertyListener)listeners.get(i);
        if(propListener != null){
          propListener.removeListener(propertyListener);
          propListener.dispose();
        }
      }
      
      for(int i=0; i<propertiesToListenTo.size(); i++){
        FFieldPath fieldPath = (FFieldPath)propertiesToListenTo.get(i);
        if(fieldPath != null){
	        if(fieldPath.size() == 1){
	        	FProperty prop = fieldPath.getPropertyFromObject(obj);
	        	if(prop != null) prop.removeListener(propertyListener);
	        }
        }
      }
    }
    mapObjectListeners.remove(obj);
  }

  private void addObjectListeners(FocObject obj){
    ArrayList listeners = (ArrayList) mapObjectListeners.get(obj);
    if(listeners == null){
      listeners = new ArrayList();
      for(int i=0; i<propertiesToListenTo.size(); i++){
        FFieldPath fieldPath = (FFieldPath)propertiesToListenTo.get(i);
        if(fieldPath != null){
        	FProperty prop = null;
	        if(fieldPath.size() == 1){
	        	prop = fieldPath.getPropertyFromObject(obj);
	        }
	        if(prop != null){
	        	prop.addListener(propertyListener);
	        	//listeners.add(getFocListenerForDirectPropertiesThatDoNotNeedSmartListeners());
	        }else{
		        FSmartPropertyListener propListener = new FSmartPropertyListener(obj, fieldPath);
		        propListener.addListener(propertyListener);
		        listeners.add(propListener);
	        }
        }
      }
      
      mapObjectListeners.put(obj, listeners);
    }     
  }
  
  public void startListening(){
    /*if(list != null){
      Iterator iter = list.focObjectIterator();
      
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        addObjectListeners(obj);
      }      
    }*/
    list.iterate(new FocListIterator(0){
      @Override
      public boolean treatElement(FocListElement element, FocObject focObj) {
        addObjectListeners(focObj);
        return false;
      }
      
    });
  }

  public void removeAllObjectListeners(){
    if(list != null){
      Iterator iter = list.focObjectIterator();
      
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        removeObjectListeners(obj);
      }
    }
  }

  public void addListener(FocListener listener){
    if(listener != null){
      listeners.add(listener);
    }
  }

  public void removeListener(FocListener listener){
    if(listener != null){
      listeners.remove(listener);
    }
  }

  public void notifyListeners(){
  	notifyListeners(null);
  }

  public void notifyListeners(FocEvent evt){
    if(!list.isSleepListeners()){
      computeOperations();//We need to call operations before listeners. Example: JVLines Table. we have listeners to compute the Balance. When we balance need to be modified before we call copyMemoryToGui
      for(int i=0; i<listeners.size(); i++){
        FocListener focListener = (FocListener)listeners.get(i);
        if(focListener != null){
          focListener.focActionPerformed(evt);
        }
      }
    }
  }
  
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  //Operation computing
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  //-----------------------------------------------------------

  private ArrayList<ListOperation> getOperationArray(boolean create){
    if(operationArray == null && create){
      operationArray = new ArrayList<ListOperation>();
    }
    return operationArray;
  }
  
  public void addOperation(ListOperation operation){
    ArrayList<ListOperation> operationArray = getOperationArray(true);
    operationArray.add(operation);
  }

  public void computeOperations(){
    ArrayList operationArray = getOperationArray(false);
    
    if(operationArray != null){
      for(int i=0; i<operationArray.size(); i++){
        ListOperation sumCol = (ListOperation) operationArray.get(i);
        sumCol.reset();
      }    
      
      Iterator iter = list.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocObject obj = (FocObject) iter.next();
        if(obj != null){
          for(int i=0; i<operationArray.size(); i++){
            ListOperation sumCol = (ListOperation) operationArray.get(i);
            sumCol.treatObject(obj);
          }    
        }
      }
  
      for(int i=0; i<operationArray.size(); i++){
        ListOperation sumCol = (ListOperation) operationArray.get(i);
        sumCol.sendResult();
      }
    }
  }
  
  private void disposeOperationArray(){
    if(operationArray != null){
      for(int i=0; i<operationArray.size(); i++){
        ListOperation sumCol = (ListOperation) operationArray.get(i);
        sumCol.dispose();
      }
    }
  }
  
  //-----------------------------------------------------------
  //-----------------------------------------------------------
  //-----------------------------------------------------------

  public class InternalListListener implements FocListener{
    FocListListener listListener = null;
    
    public InternalListListener(FocListListener listListener){
      this.listListener = listListener;
      if( listListener.getFocList() != null ){
        listListener.getFocList().addFocListener(this);  
      }
    }
    
    public void dispose(){
      if(listListener != null){
        FocList focList = listListener.getFocList();
        if(focList != null){
          focList.removeFocListener(this);
        }
      }
      listListener = null;      
    }
    
    public void focActionPerformed(FocEvent evt) {
      FocObject obj = (FocObject) evt.getEventSubject();
      switch(evt.getID()){
        case FocEvent.ID_ITEM_ADD:
          listListener.addObjectListeners(obj);
          listListener.notifyListeners(evt);
          break;
        case FocEvent.ID_ITEM_REMOVE:
          listListener.removeObjectListeners(obj);
          listListener.notifyListeners(evt);
          break;
        case FocEvent.ID_WAIK_UP_LIST_LISTENERS:
          removeAllObjectListeners();
          startListening();
          listListener.notifyListeners(evt);
          break;
      }
    }
  }
  
  public class InternalPropertyListener implements FPropertyListener{
    FocListListener listListener = null;
    
    public InternalPropertyListener(FocListListener listListener){
      this.listListener = listListener;
    }
    
    public void dispose(){
      listListener = null;
    }
    
    public void propertyModified(FProperty prop) {
      if(listListener != null){
//      	Component comp = getFocList().getAttachedListPanel();
//      	if(comp == null && Globals.getDisplayManager() != null) comp = Globals.getDisplayManager().getMainFrame();
      	FocEvent evt = new FocEvent(getFocList(), FocEvent.TYPE_LIST+FocEvent.ID_ITEM_MODIFY, "");
      	evt.setEventSubject(prop.getFocObject());
      	evt.setEventPropertySubject(prop);
        listListener.notifyListeners(evt);
        evt.dispose();
      }
    }
  }
}
