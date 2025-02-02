/*
 * Created on Jul 25, 2005
 */
package com.foc.property.validators;

import com.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FDoubleValidator implements FPropertyValidator{
  private double roundingMultiplier = 1;
  private int    roundingRule       = ROUND_CLOSE;
  
  public static final int ROUND_UP = 1;
  public static final int ROUND_DOWN = 2;
  public static final int ROUND_CLOSE = 3;
  
  public FDoubleValidator(double roundingMultiplier, int roundingRule){
    this.roundingMultiplier = roundingMultiplier;
    this.roundingRule = roundingRule;
  }
  
  public void dispose(){
    
  }
  
  public boolean validateProperty(FProperty property){
    boolean propOk = true;
    double d = property.getDouble();
    d = d * roundingMultiplier;
    double dNew = (double) Double.valueOf(d).intValue();
    switch(roundingRule){
    case ROUND_UP:      
      if(d > dNew) dNew = dNew+1; 
      break;
    case ROUND_DOWN:
      break;
    case ROUND_CLOSE:
      if(d >= dNew + 0.5) dNew = dNew+1;      
      break;
    }    
    property.setDouble(dNew / roundingMultiplier);    
    return propOk;
  }
}
