/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import java.awt.event.*;
import java.text.*;

import com.foc.*;
import com.foc.property.*;

/**
 * @author 01Barmaja
 */
public class FGTimeField extends FGFormattedTextField implements FocusListener, ActionListener, FPropertyListener {
  private FProperty objectProperty = null;
  private int maxYear = 2100;

  public FGTimeField() {
    super(new SimpleDateFormat("HH:mm"));
    StaticComponent.setComponentToolTipText(this, "Time format -> hh:mm");
    setColumns(10);    
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

  /*
  public FGDateField(FProperty objProp) throws Exception {
    super(new SimpleDateFormat("dd/MM/yyyy"));
    setColumns(10);
    if (objProp != null) {
      FField focField = objProp.getFocField();
      setText(objProp.getString());

      objectProperty = objProp;
      this.addFocusListener(this);
      this.addActionListener(this);
      objectProperty.addListener(this);
    }
  }
  */

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
  
  public int getMaxYear() {
    return maxYear;
  }

  private void updateObjectPropertyValue() {
    try {
      if (objectProperty != null) {
        objectProperty.setString(getText());
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
  
  //This function allows the component to have the right column width  
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

	@Override
	public boolean isOutput() {
		return objectProperty.isOutput();
	}
  
  /*
   * protected Document createDefaultModel() { dateDocument = new
   * DateDocument(this); return dateDocument; }
   * 
   * 
   * static class DateDocument extends PlainDocument { FDateField fTextFld =
   * null;
   * 
   * public DateDocument(FDateField fTextFld){ this.fTextFld = fTextFld; }
   * 
   * public void insertString(int offs, String str, AttributeSet a) throws
   * BadLocationException { if (str != null && fTextFld != null) { char[] chars =
   * str.toCharArray(); int decal = 0; String prevString = fTextFld.getText();
   * char[] prevChars = prevString.toCharArray();
   * 
   * String newString = str; char[] newChars = new char[10]; int newIdx = 0;
   * 
   * for(int idx=0; idx <chars.length; idx++) { if(offs+idx == 0 &&
   * Integer.parseInt(new String(chars, idx, 1)) > 3) break;
   * 
   * if(offs+idx == 1){ int a0 = 0; int a1 = 0; if(offs == 0){ a0 =
   * Integer.parseInt(new String(chars, 0, 1)); a1 = Integer.parseInt(new
   * String(chars, 1, 1)); }else{ a0 = Integer.parseInt(new String(prevChars, 0,
   * 1)); a1 = Integer.parseInt(new String(chars, 0, 1)); }
   * 
   * int day = a0 * 10 + a1;
   * 
   * if(day > 31) break; }
   * 
   * if(offs+idx == 2){ if(chars[idx] != '/'){ decal++; newChars[newIdx++] =
   * '/'; } }
   * 
   * if(offs+idx+decal == 3 && Integer.parseInt(new String(chars, idx, 1)) > 1)
   * break;
   * 
   * if(offs+idx+decal == 4){ int a0 = 0; int a1 = 0; if(offs <= 3){ a0 =
   * Integer.parseInt(new String(chars, 3-(offs + decal), 1)); }else{ a0 =
   * Integer.parseInt(new String(prevChars, 3, 1)); }
   * 
   * a1 = Integer.parseInt(new String(chars, 4-(offs + decal), 1));
   * 
   * int month = a0 * 10 + a1; if(month > 12) break; }
   * 
   * if(offs+idx+decal == 5){ if(chars[idx] != '/'){ decal++; newChars[newIdx++] =
   * '/'; } }
   * 
   * if(offs+idx+decal > 5){ int a0 = 0; int a1 = 0; int a2 = 0; int a3 = 0;
   * 
   * if(offs <= 6){ a0 = Integer.parseInt(new String(chars, 6-(offs + decal),
   * 1)); }else{ a0 = Integer.parseInt(new String(prevChars, 6, 1)); }
   * 
   * if(offs+idx+decal > 6){ if(offs <= 7){ a1 = Integer.parseInt(new
   * String(chars, 7-(offs + decal), 1)); }else{ a1 = Integer.parseInt(new
   * String(prevChars, 7, 1)); } }
   * 
   * if(offs+idx+decal > 7){ if(offs <= 8){ a2 = Integer.parseInt(new
   * String(chars, 8-(offs + decal), 1)); }else{ a2 = Integer.parseInt(new
   * String(prevChars, 8, 1)); } }
   * 
   * if(offs+idx+decal > 8){ if(offs <= 9){ a2 = Integer.parseInt(new
   * String(chars, 9-(offs + decal), 1)); }else{ a2 = Integer.parseInt(new
   * String(prevChars, 9, 1)); } }
   * 
   * int year = a0 * 1000 + a1 * 100 + a2 * 10 + a3; if(year >
   * fTextFld.getMaxYear()) break; }
   * 
   * newChars[newIdx++] = chars[idx]; }
   * 
   * super.insertString(offs, new String(newChars, 0, newIdx), a); } } }
   */
}
