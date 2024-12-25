/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import javax.swing.*;

import java.awt.event.*;

import javax.swing.text.*;

import com.foc.*;
import com.foc.property.*;
import com.foc.util.Encryptor;

/**
 * @author 01Barmaja
 */
public class FGPasswordField extends JPasswordField implements FocusListener, ActionListener, FPropertyListener {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3257285829398116660L;
  
  protected FProperty objectProperty = null;
  private TextDocument textDocument = null;
  boolean capital = false;
  int columnsLimit = 0;

  public FGPasswordField() {
    objectProperty = null;
    addFocusListener(this);
    addActionListener(this);
  }

  public void dispose(){
    if(objectProperty != null){
      objectProperty.removeListener(this);
      objectProperty = null;
    }
    removeFocusListener(this);
    removeActionListener(this);
  }
  
  public void setProperty(FProperty prop){
    if(prop != objectProperty){
      if(objectProperty != null){
        objectProperty.removeListener(this);
      }
      objectProperty = prop;
      setText(objectProperty.getString());
      objectProperty.addListener(this);
    }
  }
  
  public void setCapital(boolean capital) {
    this.capital = capital;
  }

  public void setColumnsLimit(int columnsLimit) {
    this.columnsLimit = columnsLimit;
  }

  public boolean getCapital() {
    return capital;
  }

  public int getColumnsLimit() {
    return columnsLimit;
  }
  
  public String getEncryptedPassword(){
    return Encryptor.encrypt_MD5(String.valueOf(getPassword()));
  }
  
  private void updateObjectPropertyValue() {
    try {
      if (objectProperty != null) {
        objectProperty.setString(getEncryptedPassword());
      }
    } catch (Exception exception) {
      Globals.logException(exception);
    }
  }

  // FocusListener
  // -------------
  public void focusGained(FocusEvent e) {

  }

  public void focusLost(FocusEvent e) {
    updateObjectPropertyValue();
  }

  // -------------

  // ActionListener
  // --------------
  public void actionPerformed(ActionEvent e) {
    updateObjectPropertyValue();
  }

  // --------------

  // PropertyListener
  // ----------------
  public void propertyModified(FProperty property) {
    if (objectProperty != null) {
      setText(objectProperty.getString());
    }
  }

  // ----------------

  protected Document createDefaultModel() {
    textDocument = new TextDocument(this);
    return textDocument;
  }

  static class TextDocument extends PlainDocument {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3834307328303706928L;
    
    FGPasswordField fTextFld = null;

    public TextDocument(FGPasswordField fTextFld) {
      this.fTextFld = fTextFld;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str != null && fTextFld != null) {
        char[] newStr = str.toCharArray();
        int newOffs = offs;
        int newLength = newStr.length;

        if (fTextFld.getCapital() || (fTextFld.getColumnsLimit() > 0 && fTextFld.getColumnsLimit() < newStr.length + offs)) {
          for (int i = 0; i < newStr.length; i++) {
            if (i + offs >= fTextFld.getColumnsLimit()) {
              newLength--;
            } else if (fTextFld.getCapital()) {
              newStr[i] = Character.toUpperCase(newStr[i]);
            }
          }
        }
        super.insertString(newOffs, new String(newStr, 0, newLength), a);
      }
    }
  }

  /**
   * @return
   */
  public FProperty getObjectProperty() {
    return objectProperty;
  }

  /**
   * @param property
   */
  public void setObjectProperty(FProperty property) {
    objectProperty = property;
  }

  protected int getColumnWidth(){
    return super.getColumnWidth();
    /*
    int width = super.getColumnWidth();
    Graphics2D g2d = (Graphics2D)getGraphics();
    if(g2d != null){
      Rectangle2D rect2D = getFont().getStringBounds("1", g2d.getFontRenderContext());
      width = (int)rect2D.getWidth();
      Globals.setCharDimensions(width, (int)rect2D.getHeight());
    }
    return width;
    */
  }
  
}
