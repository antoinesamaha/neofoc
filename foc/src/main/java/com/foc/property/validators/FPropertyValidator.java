/*
 * Created on Jul 25, 2005
 */
package com.foc.property.validators;

import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public interface FPropertyValidator{
  public boolean validateProperty(FProperty property);
  public void dispose();
}
