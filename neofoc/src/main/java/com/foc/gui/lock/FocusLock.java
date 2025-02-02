/*
 * Created on Sep 11, 2005
 */
package com.foc.gui.lock;

/**
 * @author 01Barmaja
 */
public abstract class FocusLock {
  public abstract Object getLockObject();
  public abstract boolean shouldHoldFocus(boolean displayMessage);
  public abstract void stopEditing();
  public abstract void dispose();
}
