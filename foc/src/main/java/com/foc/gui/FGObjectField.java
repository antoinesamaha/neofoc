/*
 * Created on 14 fevr. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.foc.gui;

import com.foc.desc.*;
import com.foc.desc.field.*;
import com.foc.property.*;

/**
 * @author Standard
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
@SuppressWarnings("serial")
public class FGObjectField extends FGTextField {

  private int displayFieldID = -1;

  public FGObjectField(FProperty objProp, int displayFieldID) throws Exception {
    super();
    if (objProp != null) {
      FObjectField focField = (FObjectField) objProp.getFocField();
      FocDesc focDesc = focField != null ? focField.getFocDesc() : null;
      this.displayFieldID = displayFieldID;
      FField displayField = focDesc.getFieldByID(displayFieldID);
      propertyModified(objProp);

      int size = displayField.getSize();
      setColumns(size);
      setColumnsLimit(size);

      objectProperty = objProp;
      this.addFocusListener(this);
      this.addActionListener(this);
      objectProperty.addListener(this);
      this.setEditable(false);
    }
  }

  public void popupSelectionPanel() {
    if (objectProperty != null) {
      FObject fObj = (FObject) objectProperty;
      fObj.popupSelectionPanel();
    }
  }

  // PropertyListener
  // ----------------
  public void propertyModified(FProperty property) {
    if (property != null) {
      FocObject focObj   = (FocObject) property.getObject();
      FProperty dispProp = focObj.getFocProperty(displayFieldID);
      if (dispProp != null) {
        setText(dispProp.getString());
      }
    }
  }

  // ----------------

  /**
   * @return
   */
  public int getDisplayFieldID() {
    return displayFieldID;
  }

  /**
   * @param i
   */
  public void setDisplayFieldID(int i) {
    displayFieldID = i;
  }

}
