/*
 * Created on 15 fevr. 2004
 */
package com.foc.property;

import com.foc.desc.*;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FCurrRate extends FDouble {
	
	private boolean reverse = false;
	
  public FCurrRate(FocObject focObj, int fieldID, double dVal) {
    super(focObj, fieldID, dVal);
  }
  
  public String getSqlString() {
  	return isReverse() ? "-"+super.getSqlString() : super.getSqlString();
  }
  
  protected void setSqlStringInternal(String str) {
  	if(str.length() > 0 && str.charAt(0) == '-'){
  		setReverse(true);
  		setString(str.substring(1));
  	}else{
  		setReverse(false);
  		setString(str);	
  	}
  }
  
	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}
	
	public double getRateDisplay(){
		return getDouble();
	}

	public void setRateDisplay(double display){
		setDouble(display);
	}

	public double getRateFactor(){
		double factor = getDouble();
		if(isReverse() && factor != 0){
			factor = 1 / factor;
		}
		return factor;
	}
	
	public void setRateFactor_KeepSameReverse(double factor){
		if(isReverse() && factor != 0){
			factor = 1 / factor;
		}
		setDouble(factor);
	}
}
