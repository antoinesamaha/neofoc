/*
 * Created on 23-May-2005
 */
package com.foc.gui;

import java.awt.Dimension;

import com.foc.menu.*;

/**
 * @author 01Barmaja
 */
public interface Navigator {
  public Dimension getViewportDimension();
  public FPanelSequence getActivePanelSequence();
  public MenuConstructor getMainMenuConstructor();
  public void changeView(FPanel panel);
  public InternalFrame newView(FPanel panel);
  public void packActiveFrame();
  public boolean goBack();//return true if should exit from aplication  
}
