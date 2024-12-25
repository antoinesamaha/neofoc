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
package com.foc.formula;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.foc.Globals;
import com.foc.formula.function.Function;
import com.foc.tree.FNode;

public class FFormulaNode extends FNode<FFormulaNode, Object>{

	private String expression      = null;
	private String functionName    = null;
	private Object calculatedValue = 0;
	
	public void dispose(){
		super.dispose();
		this.expression = null;
		functionName    = null;
	}
	
	@Override
	public FFormulaNode createChildNode(String childTitle) {
		FFormulaNode childNode = new FFormulaNode();
		childNode.setTitle(childTitle);
		childNode.setFatherNode(this);
		return childNode;
	}

	@Override
	public int getLevelDepth() {
		return 0;
	}
	
	private FunctionFactory getFunctionFactory(){
		return FunctionFactory.getInstance();
	}
	
	private String getNextUniqueChildTitle(){
		return String.valueOf(getChildCount() + 1);
	}
	
	private String getDelimiters(){
		return getFunctionFactory().getSpecialCharactersForFormulaLevel1Format();
	}
	
	public void setExpression(String expression){
		this.expression = expression != null ? expression.replaceAll(" ", "") : "";
	}
	
	public String getExpression(){
		return this.expression;
	}
	
	public void setFunctionName(String fucntionName){
		this.functionName = fucntionName;
	}
	
	public String getFunctionName(){
		return this.functionName;
	}
	
	private void setCalculatedValue(Object value){
		this.calculatedValue = value;
	}
	
	public Object getCalculatedValue(){
		return this.calculatedValue;
	}
	
	public double getCalculatedValue_double(){
		double valueDouble = 0;
		String valueString = getCalculatedValue_String();
		if(isValueDouble(valueString)){
			valueDouble = Double.valueOf(valueString);
		}
		return valueDouble;
	}
	
	public boolean getCalculatedValue_boolean(){
		boolean valueBoolean = false;
		String valueString = getCalculatedValue_String();
		if(isValueBoolean(valueString)){
			valueBoolean = Boolean.valueOf(valueString);
		}
		return valueBoolean;
	}
	
	
	public String getCalculatedValue_String(){
		String valueString = "";
		Object valueObject = getCalculatedValue();
		if(valueObject != null){
			if(valueObject instanceof Date){
				Date date = (Date) valueObject;
				try{
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
					valueString = simpleDateFormat.format(date);
				}catch(Exception ex){
					Globals.logException(ex);
				}
			}else{
				if(valueObject instanceof String){
					valueString = (String) valueObject;
				}else{
					valueString = String.valueOf(valueObject);
				}
			}
		}
		return valueString;
	}
	
	private boolean isOperator(String string){
		return getFunctionFactory().isOperator(string);
	}
	
	private int getOperatorPriority(String operator){
		return getFunctionFactory().getOperatorPriority(operator);
	}
	
	public boolean isNodeExpressionDoubleValue(){
		return isValueDouble(getExpression());
	}
	
	private static final String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
	private static final Pattern pattern = Pattern.compile(regExp);
	
	private boolean isValueDouble(String value){
	    Matcher m = pattern.matcher(value);
	    return m.matches();
//		boolean isDouble = false;
//		if(value != null){
//			isDouble = true;
//			try{
//				Double.valueOf(value);
//			}catch(NumberFormatException e){
//				isDouble = false;
//			}
//		}
//		return isDouble;
	}
	
	private boolean isValueBoolean(String value){
		boolean isBoolean = false;
		if(value != null){
			isBoolean = true;
			try{
				Boolean.valueOf(value);
			}catch(Exception e){
				isBoolean = false;
			}
		}
		return isBoolean;
	}
	
	//	300+((2*CURROBJ.FATHERNODE.QUANTITY+FATHER.OBJ.FACTOR)/8.2)*POWER(NODE.FACTOR+4,COS(2))*4000-20
	/*public void growNode(){
		String nodeExpression = getExpression();
		if(nodeExpression != null){
			
			String operatorWithMinimumPriority = "";// do not initialize to null because we are appending it to beforeOperator 
			StringBuffer beforeOperator = new StringBuffer();
			StringBuffer afterOperator = new StringBuffer();
			int numberOfOpenParentheses = 0;
			boolean containsGlobalParentethese = false;
			String beforeFirstParentethese = "";
			
			StringTokenizer tokenizer = new StringTokenizer(nodeExpression, getDelimiters(), true);
			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken();
				if(token != null){
					if(token.equals(FunctionFactory.OPEN_PARENTHESIS)){
						numberOfOpenParentheses++;
						if(operatorWithMinimumPriority.equals("")){//if we have a opened parentethese and we have not found an operator yet => we have global parentetheses maybe with a funtion name maybe not ex : POW(2+3+4) or (2+3+4)
							containsGlobalParentethese = true;
							if(numberOfOpenParentheses == 1){//if we have globals parentetheses we save the beforeOperator because it is the name of the function (if any) and we have to test if nbOfOpenParentethese == 1 because we may have POW((2+3)*4), 2) so we save the name of the function only when we read the first opened parentethese
								beforeFirstParentethese = String.valueOf(beforeOperator);
							}
						}
					}else if(token.equals(FunctionFactory.CLOSE_PARENTHESIS)){
						numberOfOpenParentheses--;
						if(numberOfOpenParentheses == 0 && tokenizer.hasMoreTokens()){
							containsGlobalParentethese = false;
						}						
					}
					if(numberOfOpenParentheses == 0){
						if(isOperator(token)){
							if(getOperatorPriority(token) < getOperatorPriority(operatorWithMinimumPriority)){// < and not <= becuase we want to conservet the priority form left to right
								beforeOperator.append(operatorWithMinimumPriority);
								beforeOperator.append(afterOperator);
								operatorWithMinimumPriority = token;
								afterOperator = new StringBuffer();
							}else{
								afterOperator.append(token);//Here we are sure that we have already found an operator because getOperatorPriority(token) > indexOfOperatorWithMinimumPriority 
							}
						}else{
							//if it is not an operator we append it to beforOperator or afterOperator
							if(operatorWithMinimumPriority.equals("")){
								beforeOperator.append(token);
							}else{
								afterOperator.append(token);
							}
						}
					}else{
						beforeOperator.append(token);
					}
				}
			}
			
			if(!operatorWithMinimumPriority.equals("")){
				//Create 2 child node that represents the to operand of the found operator : the first is the beforeOperator and the second is the afterOperator
				setTitle(getFunctionFactory().getFunctionNameForOperatorSymbol(operatorWithMinimumPriority));
				FFormulaNode childeNode = (FFormulaNode) addChild(String.valueOf(beforeOperator));
				if(childeNode != null){
					childeNode.growNode();
				}
				childeNode = (FFormulaNode) addChild(String.valueOf(afterOperator));
				if(childeNode != null){
					childeNode.growNode();
				}
				setExpression("");
			}else if(containsGlobalParentethese){
				if(beforeFirstParentethese.length() == 0){
					//remove the global parentethese
					setExpression(nodeExpression.substring(1, nodeExpression.length() -1));
					this.growNode();
				}else{
					//we have function
					if(getFunctionFactory().isFunctionName(beforeFirstParentethese)){
						setTitle(beforeFirstParentethese);
						growNodeFromFunctionExperssion(beforeFirstParentethese);
						setExpression("");
					}
				}
			}else{
				//it's leaf
				try{
					Double.valueOf(nodeExpression);//if no exception raised => it's a normal double the we let the title empty and the expression as it is
				}catch(NumberFormatException e){// it's a String : it represents a function to get from context : for now and just for test we consider that it is the "PATH" function so we set the title to "PATH" and the expression we leave it as it is 
					setTitle("PATH");//get the name of the function from functionFactory
				}
			}
		}
	}*/
	
	public void computeCalculatedValue(IFormulaContext context){
		String functionName = getFunctionName();
		if(functionName != null){
			Function function = FunctionFactory.getInstance().getFunction(functionName);
			if(function != null){
				setCalculatedValue(function.compute(this));
			}
		}else{
			if(isNodeExpressionDoubleValue()){
				setCalculatedValue(getExpression());
			}else{
				if(isSurroundedByStringDelimiters()){
					String nodeExpression = getExpression();
					if(nodeExpression.length()-1 >= 1){
						nodeExpression = nodeExpression.substring(1, nodeExpression.length()-1);
						setCalculatedValue(nodeExpression);
					}
				}else{
					try{
						Object contextEvaluatedExpression = context.evaluateExpression(getExpression());
						setCalculatedValue(contextEvaluatedExpression);
					}catch(FFormulaException e){
						setCalculatedValue(getExpression());
					}
				}
			}
		}
	}
	
	public boolean isSurroundedByStringDelimiters(){
		return getExpression() != null && getExpression().startsWith("\"") && getExpression().endsWith("\"") && getExpression().length() > 1;
	}
	
	public void growNode(){
		String nodeExpression = getExpression();
		if(nodeExpression != null){
			boolean isAStringExpression_NotToBeInterpreted = isSurroundedByStringDelimiters();
			
			if(!isAStringExpression_NotToBeInterpreted){
				String operatorWithMinimumPriority = "";// do not initialize to null because we are appending it to beforeOperator 
				StringBuffer beforeOperator = new StringBuffer();
				StringBuffer afterOperator = new StringBuffer();
				int numberOfOpenParentheses = 0;
				boolean containsGlobalParentethese = false;
				String beforeFirstParentethese = "";
				
				StringTokenizer tokenizer = new StringTokenizer(nodeExpression, getDelimiters(), true);
				while(tokenizer.hasMoreTokens()){
					String token = tokenizer.nextToken();
					if(token != null && !token.equals(FunctionFactory.SPACE)){
						boolean addTokenToBeforeOrAfterOperator = true;
						if(token.equals(FunctionFactory.OPEN_PARENTHESIS)){
							numberOfOpenParentheses++;
							if(operatorWithMinimumPriority.equals("")){//if we have a opened parentethese and we have not found an operator yet => we have global parentetheses maybe with a funtion name maybe not ex : POW(2+3+4) or (2+3+4)
								containsGlobalParentethese = true;
								if(numberOfOpenParentheses == 1){//if we have globals parentetheses we save the beforeOperator because it is the name of the function (if any) and we have to test if nbOfOpenParentethese == 1 because we may have POW((2+3)*4), 2) so we save the name of the function only when we read the first opened parentethese
									beforeFirstParentethese = String.valueOf(beforeOperator);
								}
							}
						}else if(token.equals(FunctionFactory.CLOSE_PARENTHESIS)){
							numberOfOpenParentheses--;
							if(numberOfOpenParentheses == 0 && tokenizer.hasMoreTokens()){
								containsGlobalParentethese = false;
							}						
						}
						if(numberOfOpenParentheses == 0){
							if(isOperator(token)){
								if(getOperatorPriority(token) > getOperatorPriority(operatorWithMinimumPriority)){// < and not <= becuase we want to conservet the priority form left to right
									beforeOperator.append(operatorWithMinimumPriority);
									beforeOperator.append(afterOperator);
									operatorWithMinimumPriority = token;
									afterOperator = new StringBuffer();
									addTokenToBeforeOrAfterOperator = false;
								}
							}
						}
						if(addTokenToBeforeOrAfterOperator){
							if(operatorWithMinimumPriority.equals("")){
								beforeOperator.append(token);
							}else{
								afterOperator.append(token);
							}
						}
					}
				}
				
				if(!operatorWithMinimumPriority.equals("")){
					//Create 2 child node that represents the to operand of the found operator : the first is the beforeOperator and the second is the afterOperator
					setFunctionName(getFunctionFactory().getFunctionNameForOperatorSymbol(operatorWithMinimumPriority));
					FFormulaNode childeNode = (FFormulaNode) addChild(getNextUniqueChildTitle());
					if(childeNode != null){
						childeNode.setExpression(String.valueOf(beforeOperator));
						childeNode.growNode();
					}
					childeNode = (FFormulaNode) addChild(getNextUniqueChildTitle());
					if(childeNode != null){
						childeNode.setExpression(String.valueOf(afterOperator));
						childeNode.growNode();
					}
					setExpression("");
				}else if(containsGlobalParentethese){
					if(beforeFirstParentethese.length() == 0){
						//remove the global parentethese
						setExpression(nodeExpression.substring(1, nodeExpression.length() -1));
						this.growNode();
					}else{
						//we have function
						if(getFunctionFactory().isFunctionName(beforeFirstParentethese)){
							setFunctionName(beforeFirstParentethese);
							growNodeFromFunctionExperssion(beforeFirstParentethese);
						}
						setExpression("");
					}
				}else{
					//it's leaf
					/*try{
						Double.valueOf(nodeExpression);//if no exception raised => it's a normal double the we let the title empty and the expression as it is
					}catch(NumberFormatException e){// it's a String : it represents a function to get from context : for now and just for test we consider that it is the "PATH" function so we set the title to "PATH" and the expression we leave it as it is 
						if(!nodeExpression.equals("true") && !nodeExpression.equals("TRUE") && !nodeExpression.equals("false") && !nodeExpression.equals("FALSE")){
							setFunctionName("PATH");//get the name of the function from functionFactory
							setExpression("");
							FFormulaNode childNode = (FFormulaNode) addChild(getNextUniqueChildTitle());
							childNode.setExpression(nodeExpression);
						}
					}*/
				}
			}
		}
	}

	private void growNodeFromFunctionExperssion(String functionName){
		if(functionName != null){
			String nodeExpression = getExpression();
			int numberOfOpenParentethese = 0;
			StringBuffer operand = new StringBuffer();
			nodeExpression = nodeExpression.substring(functionName.length() +1, nodeExpression.length()-1);//nodeExpression = nodeExpression.substring(functionName.length() -1 +1, nodeExpression.length()-1);// : thus we have removed the functionName, the first opened parentethese and the last closed parentethese
			String delimiters = FunctionFactory.ARGUMENT_SEPARATOR + FunctionFactory.OPEN_PARENTHESIS + FunctionFactory.CLOSE_PARENTHESIS;
			StringTokenizer tokenizer = new StringTokenizer(nodeExpression, delimiters, true);
			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken();
				if(token != null && !token.equals(FunctionFactory.SPACE)){
					//POWER(NODE.FACTOR+4,COS(2))
					if(token.equals(FunctionFactory.OPEN_PARENTHESIS)){
						numberOfOpenParentethese++;
					}else if (token.equals(FunctionFactory.CLOSE_PARENTHESIS)){
						numberOfOpenParentethese--;
					}
					if(numberOfOpenParentethese == 0){
						if(token.equals(FunctionFactory.ARGUMENT_SEPARATOR)){
							String operandNotNull = operand.length() > 0 ? String.valueOf(operand) : ""; 
//							if(operand.length() > 0){
								FFormulaNode childNode = (FFormulaNode) addChild(getNextUniqueChildTitle());
								if(childNode != null){
//									childNode.setExpression(String.valueOf(operand));
									childNode.setExpression(operandNotNull);
									childNode.growNode();
									operand = new StringBuffer();
								}
//							}
						}else{
							operand.append(token);
						}
					}else{
						operand.append(token);
					}
				}
			}
			if(operand.length() > 0){
				FFormulaNode childNode = (FFormulaNode) addChild(getNextUniqueChildTitle());
				if(childNode != null){
					childNode.setExpression(String.valueOf(operand));
					childNode.growNode();					
				}
			}
		}
	}
}
