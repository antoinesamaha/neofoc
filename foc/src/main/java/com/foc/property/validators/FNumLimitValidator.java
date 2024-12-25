/*
 * Created on Jul 25, 2005
 */
package com.foc.property.validators;

import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FNumLimitValidator implements FPropertyValidator{

  private boolean minActive = false; 
  private boolean maxActive = false;
  private double min = 0;
  private double max = 0;
  
  public FNumLimitValidator(double min, double max){
    this.min = min;
    this.max = max;
    minActive = true;
    maxActive = true;
  }

  public FNumLimitValidator(boolean isMin, double value){
    this.min = value;
    this.max = value;
    minActive = isMin;
    maxActive = !isMin;
  }

  public void dispose(){
    
  }
  
  public boolean validateProperty(FProperty property){
    double d = property.getDouble();
    
    if(minActive && d < min){
      property.setDouble(min);
    }else if(maxActive && d > max){
      property.setDouble(max);
    }
    return true;
  }
}
