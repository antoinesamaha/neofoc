/*
 * Created on 27-Avril-2005
 */
package com.foc;

import java.awt.event.KeyEvent;

import javax.swing.*;

/**
 * @author 01Barmaja
 */
public class FocKeys {
  
  public static KeyStroke getCellEditStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0/*KeyEvent.CTRL_MASK*/);
  	//return KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
    /*
    KeyStroke test = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
    return KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK);
    */
  }

  public static KeyStroke getRowEditStrokeCtrl() {
  	return KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK);
  }
  
  public static KeyStroke getRowEditStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK);
  }
  
  public static KeyStroke getDeleteStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
  }

  public static KeyStroke getInsertStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
  }
  
  public static KeyStroke getFilterStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.ALT_MASK);
  }
  
  public static KeyStroke getPrintStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK);
  }
  
  public static KeyStroke getColumnSelectorStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_MASK);
  }
  
  public static KeyStroke getExpandAllStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.ALT_MASK);
  }
  
  public static KeyStroke getCollapseAllStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.ALT_MASK);
  }
  
  public static KeyStroke getExpandUpToThisLevelStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.ALT_MASK);
  }
  
  public static KeyStroke getMoveDownStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.ALT_MASK);
  }
  
  public static KeyStroke getMoveUpStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.ALT_MASK);
  }
  
  public static KeyStroke getCollapseUpToThisLevelStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.ALT_MASK);
  }
  
  public static KeyStroke getValidateStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK);
  }

  public static KeyStroke getDuplicateStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK);
  }
  
  public static KeyStroke getCancelStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  }
  
  public static KeyStroke getToggleFocusStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK);
  }
  
  public static KeyStroke getToggleZoomStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK);
  }
  
  public static KeyStroke getControlCStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
  }
  
  public static KeyStroke getControlVStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK);
  }
  
  public static KeyStroke getFindStroke() {
    return KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK);
  }
}
