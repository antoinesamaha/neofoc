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
package com.foc.util.expression;

import java.util.ArrayList;

public class FocExpression {
	
  public static synchronized String parseExpression(String expression, IExpressionHandler handler){
    String exp = expression;
    if(exp != null){
      int idxDollar = exp.lastIndexOf("$");
      while(idxDollar >= 0 && idxDollar+2<exp.length()){
      	
      	exp = replaceSingleDollar(exp, idxDollar, handler);
      	
      	idxDollar = idxDollar-1 >= 0 ? exp.lastIndexOf("$", idxDollar-1) : -1;
      }
    }
    return exp;
  }

  public static synchronized String replaceSingleDollar(String expression, int idxDollar, IExpressionHandler handler){
    String exp = expression;
    if(exp != null){
      char type = exp.charAt(idxDollar + 1);
      char acc1 = exp.charAt(idxDollar + 2);
      if(acc1 == '{' && (type == 'P' || type == 'F' || type == 'V')){
        int idxAccolade2 = exp.indexOf("}", idxDollar);
        if(idxAccolade2 > 0){
        	String allExpression = exp.substring(idxDollar + 3, idxAccolade2);
        	String key = null;
        	
          int braketsStartIdx = allExpression.indexOf("(");
          int braketsStopIdx  = allExpression.lastIndexOf(")");

          //Arguments Array preparation   Check if we have (VALUE1, VALURE2, VALUE3)
          //---------------------------
          ArrayList<String> arguments = new ArrayList<String>(); 

          if(braketsStartIdx >=0 ){
          	key = allExpression.substring(0, braketsStartIdx);
          	String argumentsAll = allExpression.substring(braketsStartIdx+1, braketsStopIdx);
          	int lastIndex = 0;
          	int insideBrackets = 0;
          	for(int i=0; i<argumentsAll.length(); i++){
          		if(argumentsAll.charAt(i) == '('){
          			insideBrackets++;
          		}else if(argumentsAll.charAt(i) == ')'){
          			insideBrackets--;
          		}else if(argumentsAll.charAt(i) == ',' && insideBrackets == 0){
          			String arg = argumentsAll.substring(lastIndex, i);
          			arg = arg.trim();
          			arguments.add(arg);
          			lastIndex = i+1;
          		}
          	}
          	arguments.add(argumentsAll.substring(lastIndex, argumentsAll.length()));
          }else{
          	key = allExpression;
          }
          //---------------------------
          
          String keyReplacement = handler.handleFieldOrParameter(exp, type, idxDollar, idxAccolade2, key, arguments);
          if(keyReplacement != null){
            String newExp = exp.substring(0, idxDollar) + keyReplacement + exp.substring(idxAccolade2+1, exp.length());
            exp = newExp;
          }

//            String newExp = handler.handleFieldOrParameter(exp, type, idxDollar, idxAccolade2, key);
//            if(newExp != null){
//              exp = newExp;
//            }
        }
      }
    }
    return exp;
  }
}
