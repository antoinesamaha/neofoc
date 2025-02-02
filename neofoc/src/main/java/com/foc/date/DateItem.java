/*
 * Created on Aug 31, 2005
 */
package com.foc.date;

import java.sql.*;

/**
 * @author 01Barmaja
 */
public class DateItem implements Comparable{
  private Date date = null;
  private Object object = null;

  public DateItem(){
  }
  
  public DateItem(Date date, Object object){
    this.date = date;
    this.object = object;
  }
  
  public int hashCode() {
    return (int)(date.getTime() / com.foc.Globals.DAY_TIME);
  }
  
  public int compareTo(Object other){
    int ret = 1;
    if(other != null){
      DateItem otherItem = (DateItem) other;
      ret = date.compareTo(otherItem.getDate());
    }
    return ret;
  }
  
  public Date getDate() {
    return date;
  }
  
  public void setDate(Date date) {
    this.date = date;
  }
  
  public Object getObject() {
    return object;
  }
  
  public void setObject(Object object) {
    this.object = object;
  }
}
