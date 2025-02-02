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

import java.util.ArrayList;
import java.util.Iterator;

import com.foc.desc.FocObject;
import com.foc.formula.function.Function;
import com.foc.tree.TreeScanner;

public class Formula {
	private String                string             = null;
	private Function              mainFunction       = null; 
  private FocObject             formulaFocObject   = null;
	private ArrayList<Function>   usedFunctionsArray = null; 
	private FFormulaTree          formulaTree        = null;
	
	public Formula(String string){
		usedFunctionsArray = new ArrayList<Function>();
		setString(string);
		compile();
	}
	
	public void dispose(){
		string           = null;
		mainFunction     = null; 
    formulaFocObject = null;
    if(formulaTree != null){
    	formulaTree.dispose();
    	formulaTree = null;
    }
		disposeUsedFunctionsArray();
	}
	
	private void disposeUsedFunctionsArray(){
		if(usedFunctionsArray != null){
			Iterator<Function> iter = usedFunctionsArray.iterator();
			while(iter != null && iter.hasNext()){
				Function function = iter.next();
				if(function != null){
					function.dispose();
				}
			}
		}
		usedFunctionsArray.clear();
		usedFunctionsArray = null;
	}
	
	public void setString(String string){
		this.string = string;
	}
	
	public String getString(){
		return this.string;
	}
	
	public Function getMainFunction() {
		return mainFunction;
	}
	
	private void setFormulaTree(FFormulaTree formulaTree){
		this.formulaTree = formulaTree;
	}
	
	public FFormulaTree getFormulaTree(){
		return this.formulaTree;
	}
	
	public void setMainFunction(Function mainFunction) {
		if(mainFunction != null){
			this.mainFunction = mainFunction;
			this.mainFunction.setFormula(this);
		}
	}
	
	private void compile(){
		FFormulaTree formulaTree = new FFormulaTree(getString());
		formulaTree.growTree();
		setFormulaTree(formulaTree);
	}
	
	public boolean computeUponConstruction(){
		return mayNotComputeAutomaticly();
	}
	
	private boolean mayNotComputeAutomaticly(){
		boolean mayNotComputeAutomaticly = false;
		FFormulaTree formulaTree = getFormulaTree();
		if(formulaTree != null){
			FormulaTreeScaner scanner = new FormulaTreeScaner();
			formulaTree.scan(scanner);
			mayNotComputeAutomaticly = scanner.mayTreeNotComputeAutomaticly();
		}
		return mayNotComputeAutomaticly;
	}

	public Object compute(IFormulaContext context){
		FFormulaTree formulaTree = getFormulaTree();
		Object resultObject = null;
		if(formulaTree != null){
			//formulaTree.printDebug();
			formulaTree.scan(new FormulaTreeScaner(context));
			FFormulaNode rootNode    = (FFormulaNode) formulaTree.getRoot();
			FFormulaNode childLevel0 = (FFormulaNode) rootNode.getChildAt(0);
			if(childLevel0 != null){
				resultObject = childLevel0.getCalculatedValue();
			}
		}
		return resultObject;
	}

  public FocObject getFormulaFocObject() {
    return formulaFocObject;
  }

  public void setFormulaFocObject(FocObject formulaFocObject) {
    this.formulaFocObject = formulaFocObject;
  }
  
  private static class FormulaTreeScaner implements TreeScanner<FFormulaNode>{
  	private IFormulaContext context = null;
  	private boolean forComputation = false;
  	private boolean mayNotComputeAutomaticly = false;
  	
  	public FormulaTreeScaner(IFormulaContext context){
  		this.context = context; 
  		this.forComputation = true;
  	}
  	
  	public boolean mayTreeNotComputeAutomaticly(){
  		return this.mayNotComputeAutomaticly;
  	}
  	
  	public FormulaTreeScaner(){
  		this.forComputation = false;
  		mayNotComputeAutomaticly = false;
  	}
  	
		public void afterChildren(FFormulaNode node) {
			if(forComputation){
				afterChildren_Compute(node);
			}else{
				afterChildren_TestIFComputeAutomaticly(node);
			}
		}
		
		public void afterChildren_Compute(FFormulaNode node) {
			if(node != null && !node.isRoot()){
				node.computeCalculatedValue(this.context);
			}
		}
		
		public void afterChildren_TestIFComputeAutomaticly(FFormulaNode node) {
			if(node != null && !node.isRoot() && !node.isLeaf()){
				String functionName = node.getFunctionName();
				Function function = FunctionFactory.getInstance().getFunction(functionName);
				if(function != null && function.needsManualNotificationToCompute()){
					mayNotComputeAutomaticly = true;
				}
			}
		}

		public boolean beforChildren(FFormulaNode node) {
			return !mayNotComputeAutomaticly;
		}
  }
}


