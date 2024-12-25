/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import javax.swing.*;

import com.foc.Globals;
import com.foc.gui.table.*;
import com.foc.property.FBoolean;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGToggleButton extends JToggleButton implements FPropertyListener, ItemListener {

  private FBoolean booleanProperty = null;
  private boolean doNotAdjustSize = false;
  private boolean disableValidationProcess = false;
  private FTable table = null;
  private Icon iconOn = null;
  private Icon iconOff = null;

  /**
   * @param label
   *        label
   */
  public FGToggleButton(String label) {
    super(label);
    setFont(Globals.getDisplayManager().getDefaultFont());
    addItemListener(this);
    setBackground(Globals.getDisplayManager().getBackgroundColor());
  }

  public FGToggleButton(Icon icon) {
    super(icon);
    setIconOn(icon);
    setIconOff(icon);
    int width = icon.getIconWidth();
    int height = icon.getIconHeight();
    Dimension dim = new Dimension(width + 8, height + 4);
    setPreferredSize(dim);
    doNotAdjustSize = true;
    addItemListener(this);
  }

  public FGToggleButton() {
    super();
    addItemListener(this);
  }

  public void dispose() {
    removeItemListener(this);
    if (booleanProperty != null) {
      booleanProperty.removeListener(this);
      booleanProperty = null;
    }
    table = null;
  }

  public void setProperty(FProperty prop) {
    if (prop != booleanProperty) {
      if (booleanProperty != null) {
        booleanProperty.removeListener(this);
      }
      booleanProperty = (FBoolean) prop;
      propertyModified(booleanProperty);
      if (booleanProperty != null) {
        booleanProperty.addListener(this);
      }
    }
  }

  public void adjustSizeToCharacters() {
    String text = this.getText();
    Globals.logString("Button text:" + text);
    int width = text.length() * 9;
    int height = 16;
    Dimension dim = new Dimension(width + 4, height);
    setPreferredSize(dim);
  }

  public Dimension getPreferredSize() {
    Dimension dim = super.getPreferredSize();
    if (!doNotAdjustSize) {
      dim.height = dim.height - 5;
    }
    return dim;
  }

  public boolean isDisableValidationProcess() {
    return disableValidationProcess && table != null;
  }

  public void setDisableValidationProcess(boolean disableValidationProcess, FTable table) {
    this.disableValidationProcess = disableValidationProcess;
    this.table = table;
  }

  public synchronized void requestFocus() {
    InputVerifier backup = null;
    if (isDisableValidationProcess()) {
      backup = table.getInputVerifier();
      table.setInputVerifier(null);
    }
    super.requestFocus();
    if (isDisableValidationProcess()) {
      table.setInputVerifier(backup);
    }
  }

  public synchronized boolean requestFocusInWindow() {
    InputVerifier backup = null;
    if (isDisableValidationProcess()) {
      backup = table.getInputVerifier();
      table.setInputVerifier(null);
    }
    boolean b = super.requestFocusInWindow();
    if (isDisableValidationProcess()) {
      table.setInputVerifier(backup);
    }
    return b;
  }

  public void itemStateChanged(ItemEvent e) {
    if (isSelected()) {
      //setBackground(Color.RED);
      //setBorder(null);
      if (iconOn != null) {
        setIcon(iconOn);
      }
    } else {
      //setBackground(null);
      //setBorder(BorderFactory.createRaisedBevelBorder());
      if (iconOff != null) {
        setIcon(iconOff);
      }
    }
    if (booleanProperty != null) {
      booleanProperty.setBoolean(isSelected());
    }
  }

  /* (non-Javadoc)
   * @see b01.foc.property.FPropertyListener#propertyModified(b01.foc.property.FProperty)
   */
  public void propertyModified(FProperty property) {
    if (booleanProperty != null) {
      setSelected(booleanProperty.getBoolean());
    }
  }

  public Icon getIconOn() {
    return iconOn;
  }

  public void setIconOn(Icon iconOn) {
    this.iconOn = iconOn;
  }

  public Icon getIconOff() {
    return iconOff;
  }

  public void setIconOff(Icon iconOff) {
    this.iconOff = iconOff;
  }
}
