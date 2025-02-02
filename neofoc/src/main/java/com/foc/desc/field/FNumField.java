/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.sql.Types;
import java.text.NumberFormat;

import javax.swing.JTextField;

import com.fab.model.table.FieldDefinition;
import com.foc.ConfigInfo;
import com.foc.business.multilanguage.MultiLanguage;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.FGNumField;
import com.foc.gui.StaticComponent;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.TextCellControler;
import com.foc.list.filter.FilterCondition;
import com.foc.property.FDouble;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FNumField extends FField {
	
	public static final boolean DEFAULT_GROUPING_USED = true;
  private NumberFormat format               = null;
  private boolean      displayZeroValues    = true;
  private boolean      groupingUsed         = DEFAULT_GROUPING_USED;
  private double       zeroValue            = 0;
  private double       roundingPrecision    = -1;
  private boolean      useParenthesisForNegativeValues = false;
  //private boolean      minusIsUsedToReverse = false;
  
  public FNumField(String name, String title, int id, boolean key, int size, int decimals) {
    this(name, title, id, key, size, decimals, true);
  }

  public FNumField(String name, String title, int id, boolean key, int size, int decimals, boolean groupingUsed) {
    super(name, title, id, key, size, decimals);
    this.groupingUsed = groupingUsed;
  }
  
  public FNumField(String name, String title, int id, boolean key, int size, int decimals, boolean groupingUsed, boolean displayZeroValues) {
    super(name, title, id, key, size, decimals);
    this.groupingUsed = groupingUsed;
    this.displayZeroValues = displayZeroValues;
  }

  public int getDecimals() {
    if(decimals == 0 && getProvider()== DBManager.PROVIDER_ORACLE){
      size = size+2;
      if(size > 38) size = 38;
      decimals = 2;
    }
    return decimals;
  }
  
  public static int SqlType() {
    return Types.DOUBLE;
  }
  
  public int getSqlType() {
    return SqlType();
  }
  
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_DOUBLE;
  }

  public String getCreationString(String name) {//BINARY_
    if (	 getProvider()== DBManager.PROVIDER_ORACLE
    		|| getProvider()== DBManager.PROVIDER_POSTGRES){
      return " \"" + name + "\" NUMERIC" + "(" + getSize() + "," + getDecimals() + ")";
    }else if (getProvider()== DBManager.PROVIDER_MSSQL){
    	return " " + name + " [float]";
    }else if (getProvider()== DBManager.PROVIDER_H2){
    	return " \"" + name + "\" DOUBLE" ;
    }else{
      return " " + name + " DOUBLE" ;//+ "(" + getSize() + "," + getDecimals() + ")";
    }
  }

  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
  	FDouble prop = new FDouble(masterObj, getID(), defaultValue != null ? ((Double)defaultValue).doubleValue() : 0);
  	if(isAllowNullProperties() && defaultValue == null) {
  		prop.setValueNull(true);
  	}
    return prop;
  }

  public FProperty newProperty_ToImplement(FocObject masterObj){
  	FDouble prop = new FDouble(masterObj, getID(), 0);
  	if(isAllowNullProperties()) {
  		prop.setValueNull(true);
  	}
  	return prop;
  }
  
  @Override
  public NumberFormat getFormat(){
    if(format == null){
      format = FGNumField.newNumberFormat(this.getSize(), this.getDecimals(), this.groupingUsed);
    }
    return format;
  }
  
  public Component getGuiComponent(FProperty prop){
    NumberFormat format = getFormat();
    FGNumField numField = new FGNumField(format, getFieldDisplaySize());
    if(getToolTipText() != null){
    	StaticComponent.setComponentToolTipText(numField, getToolTipText());
    }
    if(prop != null) numField.setProperty(prop);
    return numField;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    JTextField guiCompForEditor = (JTextField) getGuiComponent(prop);
    guiCompForEditor.setHorizontalAlignment(JTextField.RIGHT);
    TextCellControler textCellCont = new TextCellControler(guiCompForEditor);
    textCellCont.setFormat(getFormat());
    return textCellCont;
  }

  public int getFieldDisplaySize(){ 
    int width = 1 + getSize() + getDecimals();
    if(getDecimals() > 0){
      width += 1;
    }
    width += getSize() / 3;
    return width;
  }
  
  public boolean isGroupingUsed(){
  	return this.groupingUsed;
  }
  
  public void setGroupingUsed(boolean groupingUsed){
  	this.groupingUsed = groupingUsed;
  }

  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }  
  
  public boolean isDisplayZeroValues() {
    return displayZeroValues;
  }
  
  public void setDisplayZeroValues(boolean displayZeroValues) {
    this.displayZeroValues = displayZeroValues;
  }
  
  protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
  	return null;
  }

	public double getZeroValue() {
		return zeroValue;
	}

	public void setZeroValue(double zeroValue) {
		this.zeroValue = zeroValue;
	}
	
	@Override
	public Class vaadin_getClass(){
    return Double.class;
  }

	public double getRoundingPrecision() {
		return roundingPrecision;
	}

	public void setRoundingPrecision(double roundingPrecision) {
		this.roundingPrecision = roundingPrecision;
	}

	/*
	public boolean isMinusIsUsedToReverse() {
		return minusIsUsedToReverse;
	}

	public void setMinusIsUsedToReverse(boolean minusIsUsedToReverse) {
		this.minusIsUsedToReverse = minusIsUsedToReverse;
	}
	*/
	
  public static NumberFormat newNumberFormat(int size, int decimals){
    return newNumberFormat(size, decimals, true);
  }

  public static NumberFormat newNumberFormat(int size, int decimals, boolean groupingUsed){
    NumberFormat form = NumberFormat.getInstance(MultiLanguage.getCurrentLocale());
    
    form.setMaximumFractionDigits(decimals);
    form.setMinimumFractionDigits(decimals);
    
    form.setMaximumIntegerDigits(size);
    form.setGroupingUsed(groupingUsed);
    return form;
  }

	public boolean isUseParenthesisForNegativeValues() {
		return useParenthesisForNegativeValues;
	}

	public void setUseParenthesisForNegativeValues(boolean useParenthesisForNegativeValues) {
		this.useParenthesisForNegativeValues = useParenthesisForNegativeValues;
	}
}
