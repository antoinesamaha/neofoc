/*
 * Created on Aug 30, 2005
 */
package com.foc.date;

import java.sql.Date;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class DateIterator implements Iterator{
  private DateMap   map      = null;
  private ArrayList dateList = null;
  private int       index    = -1;
  
  public DateIterator(DateMap map){
    this.map = map;
    if(map != null){
      dateList = new ArrayList();
      
      Collection values = map.values();
      if(values != null){
        Iterator iter = values.iterator();
        while(iter != null && iter.hasNext()){
          DateItem dateItem = (DateItem) iter.next();
          dateList.add(dateItem);
        }
        Collections.sort(dateList);
        index = -1;
      }
    }
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#hasNext()
   */
  public boolean hasNext() {    
    return index < dateList.size() - 1;
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#next()
   */
  public Object next() {
    DateItem dateItem = (DateItem)dateList.get(++index);
    Date date = dateItem.getDate();
    return (Object) date;
  }

  public Object previous() {
    Date date = null;
    if(index >= 0){
      DateItem dateItem = (DateItem)dateList.get(index--);
      date = dateItem.getDate();
    }
    return date;
  }

  /* (non-Javadoc)
   * @see java.util.Iterator#remove()
   */
  public void remove() {
    //Date dateToRemove = (Date) dateList.get(index);
    //map.dateToRemove
  }
  
  public boolean findAndGoto(Date date){
    boolean found = false; 
    for(int i=0; i<dateList.size(); i++){
      DateItem currDate = (DateItem) dateList.get(i);
      int comp = currDate.getDate().compareTo(date); 
      found = comp == 0;
      if(found){
        index = i-1;
        break;
      }else if(comp > 0){
        break;
      }
    }
    return found;
  }
  
  public Object getObject(){
    DateItem dateItem = (DateItem) dateList.get(index);
    return (Object) dateItem.getObject();
  }   
}
