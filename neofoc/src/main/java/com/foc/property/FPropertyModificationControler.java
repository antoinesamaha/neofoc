/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

/**
 * @author 01Barmaja
 */
public interface FPropertyModificationControler {
  public boolean isModificationAllowed(FProperty property, Object newValue);
  public void    dispose();
}
