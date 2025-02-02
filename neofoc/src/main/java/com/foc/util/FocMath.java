/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.util;

import com.foc.Globals;

public class FocMath {

	public static final double equalityEpsilon = 0.00001;

  public static int getNbrOfDecimalsForPrecision(String precision){
  	int nbrOfDecimals = 0;
  	
  	boolean startCounting = false;
  	for(int i=0; i<precision.length(); i++){
  		char c = precision.charAt(i);
  		if(startCounting){
  			nbrOfDecimals++;
  		}
  		if(c == '.') startCounting = true;
  	}
  	
  	return nbrOfDecimals;
  }
  
  public static double trunc(double val, double prec){
  	double res = val;
		if(prec != 0){
			double precI = 1 / prec;
			res = precI >= 1 ? res * precI : res / prec;
			res = Math.round(res);//This line is because I had val=318.4 prec=0.01
			                      //At that line it came res = 31899.999999993
			                      //Without the round, the floor gives 31899
			res = Math.floor(res);
			//res = Double.Integer.(int) res;
			res = prec >= 1 ? res * prec : res / precI;
		}
		return res;
  }
	
  public static double precisionStringToDouble(String precision){
  	double res = -1;
		Double prec = null;
		try{
			if(precision != null && !precision.isEmpty()) prec = Double.valueOf(precision); 
		}catch(Exception e){
			Globals.logString("Error Parsing rounding precision string :"+precision);
		}
		if(prec != null){
			res = prec.doubleValue();
		}
		return res;
  }
  
  public static double round(double val, String precision){
  	double prec = precisionStringToDouble(precision);
  	double res = val;
  	if(prec >= 0){
  		res = round(val, prec);
  	}
  	return res;
  }
  	
//		Double prec = null;
//		try{
//			if(precision != null && !precision.isEmpty()) prec = Double.valueOf(precision); 
//		}catch(Exception e){
//			Globals.logString("Error Parsing rounding precision string :"+precision);
//		}
//		if(prec != null){
//			res = round(val, prec.doubleValue());
//		}
//		return res;

  public static double round(double val, double prec){
  	double res = val;
		if(prec != 0){
			double precI = 1 / prec;
			res = precI >= 1 ? res * precI : res / prec;
			res = Math.round(res);
			res = prec >= 1 ? res * prec : res / precI;
		}
		return res;
  }
  
  public static double ceil(double val, String precision){
  	double prec = precisionStringToDouble(precision);
  	double res = val;
  	if(prec >= 0){
  		res = ceil(val, prec);
  	}
  	return res;
  }
  
  public static double ceil(double val, double prec){
  	double res = val;
		if(prec != 0){
			double precI = 1 / prec;
			res = precI >= 1 ? res * precI : res / prec;
			res = Math.ceil(res);
			res = prec >= 1 ? res * prec : res / precI;
		}
		return res;
  }

  public static double floor(double val, double prec){
  	double res = val;
		if(prec != 0){
			double precI = 1 / prec;
			res = precI >= 1 ? res * precI : res / prec;
			res = Math.floor(res);
			res = prec >= 1 ? res * prec : res / precI;
		}
		return res;
  }

  public static boolean equals(double x, double y){
  	return equals(x, y, equalityEpsilon);
  }
  
  public static boolean equals(double x, double y, double eps){
  	return x == y || (x < y + eps && x > y - eps);
  }
  
//  public static int parseInteger(String str){
//  	int v = 0;
//  	try{
//  		v = Integer.valueOf(str);
//  	}catch(Exception e){
//  		//Globals.logException(e);
//  	}
//  	return v;
//  }

  public static long parseLong(String str){
  	long v = 0;
  	try{
 			v = Long.valueOf(str);  			
  	}catch(Exception e){
  		try{//When we have a str = 1.0 the Integer.valueOf(str) gives exception
				Double doubleValue = Double.valueOf(str);
				v = doubleValue.longValue();
  		}catch(Exception e2){
  		}
  	}
  	return v;
  }

  public static int parseInteger(String str){
  	int v = 0;
  	try{
 			v = Integer.valueOf(str);  			
  	}catch(Exception e){
  		try{//When we have a str = 1.0 the Integer.valueOf(str) gives exception
				Double doubleValue = Double.valueOf(str);
				v = doubleValue.intValue();
  		}catch(Exception e2){
  		}
  	}
  	return v;
  }
  
  public static int div(double quotien, double dividend){
  	int div = 0;
  	double rem = quotien;
  	
  	while(rem > dividend){
  		rem = rem - dividend;
  		div++;
  	}
  	
  	return div;
  }
  
  public static double modulo(double quotien, double dividend){
  	double rem = quotien;
  	
  	while(rem > dividend){
  		rem = rem - dividend;
  	}
  	
  	return rem;
  }
}
