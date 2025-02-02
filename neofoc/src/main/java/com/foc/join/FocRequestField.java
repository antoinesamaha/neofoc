/*
 * Created on Jan 9, 2006
 */
package com.foc.join;

import com.foc.desc.field.FField;

/**
 * @author 01Barmaja
 */
public class FocRequestField {
  private int        id             = 0;
  private TableAlias tableAlias     = null;
  private int        fieldId        = FField.NO_FIELD_ID;
  private String     groupByFormula = null;
  private String     groupByFormula_AdditionalField = null;
  
  public FocRequestField(int id, TableAlias tableAlias, int fieldId){
    this.id         = id;
    this.tableAlias = tableAlias;
    this.fieldId    = fieldId;
  }
  
  public void dispose(){
  	tableAlias = null;
  }

  public int getId() {
    return id;
  }
    
  public TableAlias getTableAlias() {
    return tableAlias;
  }

  public FField getField() {
    return tableAlias.getFocDesc().getFieldByID(fieldId);
  }

	public String getGroupByFormula() {
		return groupByFormula;
	}

	public void setGroupByFormula(String groupByFormula) {
		this.groupByFormula = groupByFormula;
	}

	public String getGroupByFormula_AdditionalField() {
		return groupByFormula_AdditionalField;
	}

	public void setGroupByFormula_AdditionalField(String groupByFormula_AdditionalField) {
		this.groupByFormula_AdditionalField = groupByFormula_AdditionalField;
	}
}
