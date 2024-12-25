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

import javax.swing.Icon;

import com.foc.desc.FocObject;
import com.foc.list.FocList;
import com.foc.property.FProperty;
import com.foc.tree.FNode;
import com.foc.tree.FTree;
import com.foc.tree.TreeScanner;

public class FFormulaTree extends FTree {
	
	private String formulaLevel1Format = null; 
	
	@SuppressWarnings("unchecked")
	public FFormulaTree(String completeFormula){
		this.root                = (FFormulaNode) newRootNode();
		this.formulaLevel1Format = completeFormula;
	}
	
	public void dispose(){
		super.dispose();
		this.formulaLevel1Format = null;
	}
	
	@Override
	public int getDepthVisibilityLimit() {
		return 0;
	}

	@Override
	public FocList getFocList() {
		return null;
	}

	@Override
	public Icon getIconForNode(FNode node) {
		return null;
	}

	@Override
	public FProperty getTreeSpecialProperty(FNode node) {
		return null;
	}

	@Override
	public void growTreeFromFocList(FocList focList) {
	}
	
	@Override
	public FNode addFocObject(Object childObject) {
		return null;
	}

	@Override
	public boolean isNodeLocked(FNode node) {
		return false;
	}

	@Override
	public FocObject newEmptyItem(FNode node) {
		return null;
	}
	
	private String getFormulaLevel1Format(){
		return this.formulaLevel1Format;
	}
	
	public void growTree(){
		FNode root = getRoot();
		if(root != null){
			FFormulaNode childNode = (FFormulaNode) root.addChild("Main node");
			childNode.setExpression(getFormulaLevel1Format());
			childNode.growNode();
		}
	}
	
	public String getFormulaLevel0Format(){
		growTree();
		FFormulaTreeScaner scaner = new FFormulaTreeScaner();
		scan(scaner);
		return scaner.getFormulaLevel0Format();
	}
	
	private static class FFormulaTreeScaner implements TreeScanner<FFormulaNode>{
		
		private StringBuffer formulaLevel0Format = new StringBuffer();

		public void afterChildren(FFormulaNode node) {
			if(!node.isRoot()){
				String functionName = node.getFunctionName();
				if(functionName != null && functionName.length() > 0){
					String lastChar = formulaLevel0Format.substring(formulaLevel0Format.length() - 1);
					if(lastChar != null && lastChar.equals(FunctionFactory.ARGUMENT_SEPARATOR)){
						formulaLevel0Format.deleteCharAt(formulaLevel0Format.length() -1);
					}
					formulaLevel0Format.append(FunctionFactory.CLOSE_PARENTHESIS);
				}
				if(!node.getFatherNode().isRoot()){
					formulaLevel0Format.append(FunctionFactory.ARGUMENT_SEPARATOR);
				}
			}
		}

		public boolean beforChildren(FFormulaNode node) {
			if(!node.isRoot()){
				String functionName = node.getFunctionName();
				if(functionName != null && functionName.length() > 0){
					formulaLevel0Format.append(functionName);
					formulaLevel0Format.append(FunctionFactory.OPEN_PARENTHESIS);
				}else{
					String expression = node.getExpression();
					if(expression != null)
					formulaLevel0Format.append(expression);
				}
			}
			return true;
		}
		
		public String getFormulaLevel0Format(){
			return String.valueOf(formulaLevel0Format);
		}
	}

  @Override
  public int getDisplayCodeFieldID() {
    return NO_DISPLAY_CODE_ID;
  }

	@Override
	protected FNode newRootNode() {
		return new FFormulaRootNode(this);
	}

  @Override
  public void moveDown(FNode node) {
  }

  @Override
  public void moveUp(FNode node) {
  }
  
  @Override
  protected String printDebug_GetNodeDebugExpression(FNode node){
  	FFormulaNode formulaNode = (FFormulaNode) node;
  	if(formulaNode.getFunctionName() != null){
  		return formulaNode.getFunctionName() + "[" + formulaNode.getExpression() + "] = " + formulaNode.getCalculatedValue();
  	}else{
  		return "[" + formulaNode.getExpression() + "] = " + formulaNode.getCalculatedValue();
  	}
  }
}
