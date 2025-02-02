/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import java.awt.Color;
import java.text.Format;
import java.util.StringTokenizer;

import com.foc.desc.*;
import com.foc.desc.field.FColorField;

/**
 * @author Standard
 */
public class FColorProperty extends FString implements Cloneable{
  private Color color;
  private Color backupColor = null;
  
  private static String formatColor(Color color){
  	return color != null ? color.getRed()+","+color.getGreen()+","+color.getBlue() : "";
  }

  private static Color parseColor(String colorStr){
  	Color clr = null;
  	if(colorStr != null){
	  	StringTokenizer strTokenizer = new StringTokenizer(colorStr, ",", false);
	  	int[] colorIntArray = {-1, -1, -1};
	  	int idx = 0;
	  	while(strTokenizer.hasMoreTokens()){
	  		colorIntArray[idx++] = Integer.valueOf(strTokenizer.nextToken());
	  	}
	  	if(colorIntArray[0] >= 0 && colorIntArray[1] >= 0 && colorIntArray[2] >= 0){
	  		clr = new Color(colorIntArray[0], colorIntArray[1], colorIntArray[2]); 
	  	}else{
	  		clr = null;
	  	}
  	}
  	return clr;
  }

  public FColorProperty(FocObject focObj, int fieldID, Color color) {
    super(focObj, fieldID, formatColor(color));
  }

  protected Object clone() throws CloneNotSupportedException {
    FColorProperty zClone = (FColorProperty)super.clone();
    zClone.setColor(getColor());
    return zClone;
  }
  
  public boolean isEmpty(){
    return getColor() == null;
  }
  
  public Color getColor(){
  	return color;
  }
  
  public void setColor(Color color){
  	boolean changed = color != this.color;
  	if(!changed && color != null && this.color != null){
  		changed = color.getRed() != this.color.getRed() || color.getGreen() != this.color.getGreen() || color.getBlue() != this.color.getBlue();
  	}
  	this.color = color;
  	if(changed){
  		notifyListeners();
  	}
  }
  
  public String getString() {
    return formatColor(getColor());
  }

  public void setString(String str) {
  	setColor(parseColor(str));
  }

  public void setInteger(int iVal) {
  }

  public int getInteger() {
  	return 0;
  }

  public void setDouble(double dVal) {
  }

  public double getDouble() {
    return 0;
  }

  public void setObject(Object obj) {
    setColor((Color) obj);
  }

  public Color getObject() {
    return getColor();
  }
  
  public Object getTableDisplayObject(Format format) {
  	Color clr = getColor();
  	if(clr == null){
  		FColorField clrFld = (FColorField) getFocField();
  		clr = clrFld.getNullColorDisplay();
  	}
    return clr;
  }

  public void setTableDisplayObject(Object obj, Format format) {
    setColor((Color)obj);
  }

  public void backup(){
    backupColor = color;    
  }
  
  public void restore(){
    color = backupColor ;
  }
}
