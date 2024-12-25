/*
 * Created on Aug 3, 2005
 */
package com.foc.gui.fieldPathChooser;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.gui.*;
import com.foc.property.*;
import com.foc.stringList.*;

/**
 * @author 01Barmaja
 */
public class FieldPathChooser{
  FAttributeLocationProperty pathProperty = null;
  StringList strList = null;
  
  public FieldPathChooser(FAttributeLocationProperty pathProperty){
    this.pathProperty = pathProperty;
    strList = new StringList();
    updateFieldsList();
  }

  public FPanel newSelectionPanel(){
    FGTextField textField = new FGTextField();
    textField.setProperty(pathProperty);
    textField.setEditable(false);
    textField.setColumns(50);
    
    strList.addSelectionListener(new FPropertyListener(){
      public void propertyModified(FProperty property) {
        StringListItem strItem = (StringListItem) property.getObject();
        
        FocDesc focDesc = getCurrentFocDesc();
        FField selectedFld = focDesc.getFieldByName(strItem.getString());
        FFieldPath fldPath = (FFieldPath) pathProperty.getFieldPath();
        if(fldPath == null){
          fldPath = new FFieldPath(1);
          fldPath.set(0, selectedFld.getID());
          pathProperty.setFieldPath(fldPath);          
        }
        fldPath.add(selectedFld.getID());
        pathProperty.setFieldPath(fldPath);
        updateFieldsList();
      }

      public void dispose() {
      }
    });
    
    FGButton add = new FGButton("Add");
    add.addActionListener(new AbstractAction(){
      private static final long serialVersionUID = 3617857477798999353L;

      public void actionPerformed(ActionEvent e) {
        strList.validateSelectedObject();
      }
    });
    FGButton remove = new FGButton("Remove");
    remove.addActionListener(new AbstractAction(){
      private static final long serialVersionUID = 4050206336300102712L;

      public void actionPerformed(ActionEvent e) {
        FFieldPath fldPath = (FFieldPath) pathProperty.getFieldPath();
        fldPath.removeLast();
        pathProperty.setFieldPath(fldPath);
        updateFieldsList();
      }
    });
    FPanel buttonsPanel = new FPanel();
    buttonsPanel.add(add,0,0);
    buttonsPanel.add(remove,1,0);
    
    FPanel mainPanel = new FPanel();
    mainPanel.add(strList.newSelectionPanel(StringList.VIEW_NO_VALIDATION), 0, 0);
    mainPanel.add(buttonsPanel, 0, 1);
    mainPanel.add(textField, 0, 2);
    
    FValidationPanel validPanel = mainPanel.showValidationPanel(true);
    validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    
    return mainPanel;
  }
  
  private FocDesc getCurrentFocDesc(){
    FocDesc currDesc = null;
    FFieldPath fldPath = (FFieldPath) pathProperty.getFieldPath();
    FocDesc oDesc = pathProperty.getBaseFocDesc();
    if(oDesc != null){
      currDesc = oDesc;
      if(fldPath != null && fldPath.size() > 0){
        currDesc = fldPath.getDescFromDesc(oDesc);
      }
    }    
    return currDesc;
  }
  
  private void updateFieldsList(){
    strList.removeAll();
    FocDesc currDesc = getCurrentFocDesc();
    if(currDesc != null){
      FocFieldEnum fldEnum = new FocFieldEnum(currDesc, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(fldEnum != null && fldEnum.hasNext()){
        FField fld = (FField) fldEnum.next();
        if(fld != null){
          strList.addString(fld.getName());
        }
      }
    }
  }
  
}
