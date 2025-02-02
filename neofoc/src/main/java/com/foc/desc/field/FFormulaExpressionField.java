/*
 * Created on May 15, 2008
 */
package com.foc.desc.field;

import java.awt.Component;

import javax.swing.JTextField;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.gui.FGFormulaEditorPanel;
import com.foc.gui.table.cellControler.AbstractCellControler;
import com.foc.gui.table.cellControler.TextCellControler;
import com.foc.property.FFormulaExpression;
import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FFormulaExpressionField extends FStringField {
	private FocDesc originDesc = null;
	
  public FFormulaExpressionField(String name, String title, int id, FocDesc originDesc){
    super(name, title, id, false, 250);
    this.originDesc = originDesc;
  }
  
  public FProperty newProperty_ToImplement(FocObject masterObj, Object defaultValue){
    return new FFormulaExpression(masterObj, getID(), (String)defaultValue);
  }
  
  public Component getGuiComponent(FProperty prop){
  	FocObject originObj = prop != null ? prop.getFocObject() : null;
  	FocDesc   desc      = originObj != null ? originObj.getThisFocDesc() : null;
  	FGFormulaEditorPanel panel = new FGFormulaEditorPanel(getOriginDesc(), (getOriginDesc() == desc) ? originObj : null, (FFormulaExpression) prop);
    return panel;
  }
  
  public AbstractCellControler getTableCellEditor_ToImplement(FProperty prop){
    JTextField guiComp = (JTextField) super.getGuiComponent(prop);
    return new TextCellControler(guiComp);
  }

	public FocDesc getOriginDesc() {
		return originDesc;
	}

	public void setOriginDesc(FocDesc originDesc) {
		this.originDesc = originDesc;
	}
}
