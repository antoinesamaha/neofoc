/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.util.*;

/**
 * @author 01Barmaja
 */
public class FListOrderFieldList {
  ArrayList listOfPaths = null;

  public FListOrderFieldList() {
    listOfPaths = new ArrayList();
  }

  public void dispose(){
    for(int i=0; i<listOfPaths.size(); i++){
      OrderField orderField = (OrderField) listOfPaths.get(i);
      if(orderField != null){
        orderField.dispose();
        orderField = null;
      }
    }
  }
  
  public void add(FFieldPath path, boolean ascending) {
    if (path != null && listOfPaths != null) {
      OrderField orderField = new OrderField(path, ascending);
      listOfPaths.add(orderField);
    }
  }

  public void add(FFieldPath path) {
    add(path, true);
  }

  private OrderField get(int at) {
    return listOfPaths != null ? (OrderField) listOfPaths.get(at) : null;
  }

  public FFieldPath getFieldPathAt(int at) {
    FFieldPath path = null;
    OrderField of = get(at);
    if(of != null){
      path = of.getFieldPath();
    }
    return path;
  }

  public boolean isFieldAscending(int at) {
    boolean asc = true;
    OrderField of = get(at);
    if(of != null){
      asc = of.isAscending();
    }
    return asc;
  }

  public int size() {
    return listOfPaths != null ? listOfPaths.size() : 0;
  }
  
  private class OrderField {
    private FFieldPath path = null;
    private boolean ascending = false;
    
    public OrderField(FFieldPath path, boolean ascending){
      this.ascending = ascending;
      this.path = path;
    }
    
    public void dispose(){
      if(path != null){
        path.dispose();
        path = null;        
      }          
    }
    
    public boolean isAscending() {
      return ascending;
    }
    
    public FFieldPath getFieldPath() {
      return this.path;
    }
  }
}
