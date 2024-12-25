/*
 * Created on Oct 14, 2004
 */
package com.foc.desc.field;

import java.awt.Component;
import java.sql.Types;

import com.fab.model.table.FieldDefinition;
import com.foc.db.DBManager;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.list.filter.FilterCondition;
import com.foc.property.FProperty;
import com.foc.property.FSDOGeometryPoint;
import com.foc.property.FString;

/**
 * @author 01Barmaja
 */
public class FSDOGeometryPointField extends FField {

  public FSDOGeometryPointField(String name, String title, int id, boolean key) {
    super(name, title, id, key, 0, 0);
  }

  public static int SqlType() {
    return Types.STRUCT;
  }

  public int getSqlType() {
    return SqlType();
  }
  
  public int getFabType() {
    return FieldDefinition.SQL_TYPE_ID_CHAR_FIELD;
  }

  public String getCreationString(String name) {
    if (getProvider()== DBManager.PROVIDER_ORACLE){
   		return " \"" + name + "\" SDO_GEOMETRY ";
    } else {
      return "";
    }
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FSDOGeometryPoint(masterObj, getID(), 0, 0);
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj){
    return newProperty(masterObj, "");
  }
  
  public int compareSQLDeclaration(FField field){
 		return 0;
  }
  
  public boolean isObjectContainer(){
    return false;
  }

  public FocDesc getFocDesc(){
    return null;
  }
  
  public void addReferenceLocations(FocDesc pointerDesc){
    
  }
  
	@Override
	protected FilterCondition getFilterCondition(FFieldPath fieldPath, String conditionPrefix){
		return null;
	}

	@Override
	public Component getGuiComponent(FProperty prop) {
		return null;
	}

	@Override
	public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop) {
		return null;
	}
}
