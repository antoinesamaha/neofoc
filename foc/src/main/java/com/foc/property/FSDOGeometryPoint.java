/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.text.Format;

import com.foc.db.DBManager;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;

/**
 * @author Standard
 */
public class FSDOGeometryPoint extends FProperty implements Cloneable{
  private double lng = 0;
  private double lat = 0;
  private double backupLng = 0;
  private double backupLat = 0;
  
  public FSDOGeometryPoint(FocObject focObj, int fieldID, double lng, double lat) {
    super(focObj, fieldID);
    this.lng = lng;
    this.lat = lat;
  }

  protected Object clone() throws CloneNotSupportedException {
    FSDOGeometryPoint zClone = (FSDOGeometryPoint)super.clone();
    zClone.setLng(getLng());
    zClone.setLat(getLat());
    return zClone;
  }
  
  public boolean isEmpty(){
    return getString() == null || getString().trim().compareTo("") == 0;
  }
  
  public String getString() {
    return "";
  }
  
  public void setString_UserEditingEvent(String str) {
  	setString_Internal(str, true);
  }

  public void setString(String str) {
  	setString_Internal(str, false);
  }
  
  public void setString_Internal(String str, boolean userEditingEvent) {
    if(doSetString(str)){
      
      
      notifyListeners(userEditingEvent);
      //setInherited(false);
    }
  }

  public void setString_WithoutListeners(String str) {
//    this.str = str;
  }

  public boolean doSetString(String str){
//  	boolean allow = 		(this.str == null && str != null) 
//  									|| 	(this.str != null && str != null && this.str.compareTo(str) != 0) /*|| (this.str != null && str == null)*/ ;
//  	if(allow){
//  		allow = getFocField() == null || getFocField().isPropertyModificationAllowed(this, str, true);
//  	}
//    return allow; 
  	return true;
  }
  
  @Override
  protected void setSqlStringInternal(String str) {
 		//str = (str != null) ? str.replace("''", "\"") : null;	
    setString(str);
  }

  public String getSqlString() {
  	String sqlStr = "";
  	if(getProvider() == DBManager.PROVIDER_ORACLE){
  		sqlStr = "SDO_GEOMETRY(2001,4326,SDO_POINT_TYPE("+getLng()+","+getLat()+", NULL),NULL,NULL)";
  	}else{
  		
  	}
    return sqlStr;
  }

  public void setInteger(int iVal) {
    setString(String.valueOf(iVal));
  }

  public void setDouble(double dVal) {
    setString(String.valueOf(dVal));
  }

  public double getDouble() {
  	String String = "0";
//  	if(!str.isEmpty()){
//  		String = str;
//  	}
    return Double.parseDouble(String);
  }

  public void setObject(Object obj) {
    setString((String) obj);
  }

  public Object getObject() {
    return (Object) getString();
  }
  
  public Object getTableDisplayObject(Format format) {
  
    Object displayObj = null;
    String realValue = getString();
    if(hasNoRight()){
    	displayObj = FField.NO_RIGHTS_STRING;
    }else{
    	displayObj = realValue;
    }
    
    return displayObj;
  }

  public void setTableDisplayObject(Object obj, Format format) {
  	setString_UserEditingEvent((String) obj);
  }

  public void backup() {
    backupLng = getLng();    
    backupLat = getLat();
  }
  
  public void restore() {
    setLng(backupLng);    
    setLat(backupLat);
  }
  
  public void setEmptyValue(){
  	setString("");
  }

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}
}
