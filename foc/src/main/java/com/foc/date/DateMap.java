/*
 * Created on Aug 30, 2005
 */
package com.foc.date;

import java.sql.Date;
import java.util.*;

import com.foc.Globals;
import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
public class DateMap {
  private HashMap dateMap = null;
  
  public DateMap(){
    dateMap = new HashMap();
  }
  
  public void put(Date date){
    put(date, null);
  }
  
  public void put(Date date, Object obj){
    DateItem item = new DateItem(date, obj);
    dateMap.put(date, item);
  }

  public Object get(Date date){
    DateItem item = (DateItem) dateMap.get(date);
    return item != null ? item.getObject() : null;
  }
  
  public Set keySet(){
    return dateMap.keySet();
  }
  
  public Collection values() {
    return dateMap.values();
  }
  
  public Iterator iterator(){
    return new DateIterator(this);    
  }
  
  public void fillFromTableColumn(FocDesc focDesc, int fieldID){
  	ArrayList arrayList = Globals.getApp().getDataSource().command_Select(focDesc, fieldID, true, null);
  	for(int l=0; l<arrayList.size(); l++)	{
      put((Date)arrayList.get(l));
  	}
  }
}
