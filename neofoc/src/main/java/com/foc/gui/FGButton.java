/*
 * Created on 14 fevr. 2004
 */
package com.foc.gui;

import javax.swing.*;

import com.foc.Globals;
import com.foc.gui.table.*;

import java.awt.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FGButton extends JButton {
  
  private boolean doNotAdjustSize = false;
  private boolean disableValidationProcess = false;
  private FTable table = null;
  
  /**
   * @param label
   *        label used
   */
  public FGButton(String label) {
    super(label);
    setFont(Globals.getDisplayManager().getDefaultFont());
    setMultiClickThreshhold(200);
    /*
    if(label != null){
      Graphics2D g = (Graphics2D) this.getGraphics();
      Font font = this.getFont();
      if(font == null) Globals.logString("font null");
      if(g == null) Globals.logString("g null");
      font.getf
      font.getStringBounds("dsd", g.getFontRenderContext());
      int width = label.length() * 11;
      int height = Globals.CHAR_HEIGHT;
      Dimension dim = new Dimension(width, height);
      setPreferredSize(dim);
    } 
    */   
  }

  /**
   * @param label
   * 				button caption
   * @param icon
   *        button icon
   */
  public FGButton(String label, Icon icon) {
    super(label, icon);
    setFont(Globals.getDisplayManager().getDefaultFont());
    setMultiClickThreshhold(200);
  }

  /**
   * @param icon
   *        Button icon
   */
  public FGButton(Icon icon) {
    super(icon);
    setFont(Globals.getDisplayManager().getDefaultFont());
    int width = icon != null ? icon.getIconWidth() : 0;
    int height = icon != null ? icon.getIconHeight() : 0;
    Dimension dim = new Dimension(width+4, height+4);
    if(Globals.getDisplayManager() != null && Globals.getDisplayManager().getLookAndFeel() == DisplayManager.LOOK_AND_FEEL_NIMBUS){
    	dim = new Dimension(width+8, height+8);
    }
    setPreferredSize(dim);
    doNotAdjustSize = true;
    setMultiClickThreshhold(200);
  }

  /**
   * 
   */
  public FGButton() {
    super();
    setFont(Globals.getDisplayManager().getDefaultFont());
    setMultiClickThreshhold(200);
  }
  
  public void dispose(){
    table = null;
  }
  
  public void adjustSizeToCharacters(){
    String text = this.getText();
    int width = text.length()*9;   
    int height = 16;
    Dimension dim = new Dimension(width+4, height);
    setPreferredSize(dim);
  }
  
  public Dimension getPreferredSize() {
    Dimension dim = super.getPreferredSize();
    if(!doNotAdjustSize) dim.height = dim.height - 5; 
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
    if(isDisableValidationProcess()){
      backup = table.getInputVerifier();
      table.setInputVerifier(null);
    }
    super.requestFocus();
    if(isDisableValidationProcess()){
      table.setInputVerifier(backup);
    }
  }

  public synchronized boolean requestFocusInWindow() {
    InputVerifier backup = null;
    if(isDisableValidationProcess()){
      backup = table.getInputVerifier();
      table.setInputVerifier(null);
    }
    boolean b = super.requestFocusInWindow();
    if(isDisableValidationProcess()){
      table.setInputVerifier(backup);
    }
    return b;
  }

}
