/*
 * Created on 14 fevr. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.foc.gui;

import javax.swing.*;
import java.awt.event.*;

import javax.swing.text.*;

import com.foc.*;
import com.foc.desc.field.*;
import com.foc.property.*;

/**
 * @author Standard
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FDateField_my extends JTextField implements FocusListener, ActionListener, FPropertyListener {

  private FProperty objectProperty = null;
  private DateDocument dateDocument = null;
  private int maxYear = 2100;

  public FDateField_my() {
    objectProperty = null;
  }

  public FDateField_my(FProperty objProp) throws Exception {
    super();
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

  public int getMaxYear() {
    return maxYear;
  }

  private void updateObjectPropertyValue() {
    try {
      if (objectProperty != null) {
        objectProperty.setString(getText());
        Globals.logString(getText());
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
    dateDocument = new DateDocument(this);
    return dateDocument;
  }

  static class DateDocument extends PlainDocument {
    FDateField_my fTextFld = null;

    public DateDocument(FDateField_my fTextFld) {
      this.fTextFld = fTextFld;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str != null && fTextFld != null) {
        char[] chars = str.toCharArray();
        int decal = 0;
        String prevString = fTextFld.getText();
        char[] prevChars = prevString.toCharArray();

        String newString = str;
        char[] newChars = new char[10];
        int newIdx = 0;

        for (int idx = 0; idx < chars.length; idx++) {
          if (offs + idx == 0 && Integer.parseInt(new String(chars, idx, 1)) > 3) break;

          if (offs + idx == 1) {
            int a0 = 0;
            int a1 = 0;
            if (offs == 0) {
              a0 = Integer.parseInt(new String(chars, 0, 1));
              a1 = Integer.parseInt(new String(chars, 1, 1));
            } else {
              a0 = Integer.parseInt(new String(prevChars, 0, 1));
              a1 = Integer.parseInt(new String(chars, 0, 1));
            }

            int day = a0 * 10 + a1;

            if (day > 31) break;
          }

          if (offs + idx == 2) {
            if (chars[idx] != '/') {
              decal++;
              newChars[newIdx++] = '/';
            }
          }

          if (offs + idx + decal == 3 && Integer.parseInt(new String(chars, idx, 1)) > 1) break;

          if (offs + idx + decal == 4) {
            int a0 = 0;
            int a1 = 0;
            if (offs <= 3) {
              a0 = Integer.parseInt(new String(chars, 3 - (offs + decal), 1));
            } else {
              a0 = Integer.parseInt(new String(prevChars, 3, 1));
            }

            a1 = Integer.parseInt(new String(chars, 4 - (offs + decal), 1));

            int month = a0 * 10 + a1;
            if (month > 12) break;
          }

          if (offs + idx + decal == 5) {
            if (chars[idx] != '/') {
              decal++;
              newChars[newIdx++] = '/';
            }
          }

          if (offs + idx + decal > 5) {
            int a0 = 0;
            int a1 = 0;
            int a2 = 0;
            int a3 = 0;

            if (offs <= 6) {
              a0 = Integer.parseInt(new String(chars, 6 - (offs + decal), 1));
            } else {
              a0 = Integer.parseInt(new String(prevChars, 6, 1));
            }

            if (offs + idx + decal > 6) {
              if (offs <= 7) {
                a1 = Integer.parseInt(new String(chars, 7 - (offs + decal), 1));
              } else {
                a1 = Integer.parseInt(new String(prevChars, 7, 1));
              }
            }

            if (offs + idx + decal > 7) {
              if (offs <= 8) {
                a2 = Integer.parseInt(new String(chars, 8 - (offs + decal), 1));
              } else {
                a2 = Integer.parseInt(new String(prevChars, 8, 1));
              }
            }

            if (offs + idx + decal > 8) {
              if (offs <= 9) {
                a2 = Integer.parseInt(new String(chars, 9 - (offs + decal), 1));
              } else {
                a2 = Integer.parseInt(new String(prevChars, 9, 1));
              }
            }

            int year = a0 * 1000 + a1 * 100 + a2 * 10 + a3;
            if (year > fTextFld.getMaxYear()) break;
          }

          newChars[newIdx++] = chars[idx];
        }

        super.insertString(offs, new String(newChars, 0, newIdx), a);
      }
    }
  }

  /* (non-Javadoc)
   * @see b01.foc.property.FPropertyListener#dispose()
   */
  public void dispose() {
  }
}
