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
package com.foc.formula.function;

import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.formula.FFormulaNode;
import com.foc.formula.FunctionFactory;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.property.FAttributeLocationProperty;
import com.foc.property.FProperty;

public class FunctionStaticPath extends Function {
	private static final String FUNCTION_NAME = "STATIC_PATH";
	private static final String OPERATOR_SYMBOL = null;
	private FocDesc focDescToStartFieldPathFrom = null;
	private FocObject focObjectToStartFieldFrom = null;
	
	public void dispose(){
		super.dispose();
		this.focDescToStartFieldPathFrom = null;
		this.focObjectToStartFieldFrom = null;
	}
	
	private FocObject getFocObjectToStartFieldPathFrom(String focDescStorageName){
		if(focObjectToStartFieldFrom == null){
			FocLinkSimple link = new FocLinkSimple(getFocDescToStartFieldPathFrom(focDescStorageName));
			FocList list = new FocList(link);
			list.loadIfNotLoadedFromDB();
			if(list.size() > 1){
				Globals.getDisplayManager().popupMessage("Waring mulitple row found in DB for a static instance");
			}
			focObjectToStartFieldFrom = list.getFocObject(0);
			if(focObjectToStartFieldFrom == null){
				FocConstructor constr = new FocConstructor(getFocDescToStartFieldPathFrom(focDescStorageName), null);
				focObjectToStartFieldFrom = constr.newItem();
			}
		}
		return focObjectToStartFieldFrom;
	}
	
	private FocDesc getFocDescToStartFieldPathFrom(String focDescStorageName){
		if(focDescToStartFieldPathFrom == null){
			focDescToStartFieldPathFrom = Globals.getApp().getFocDescByName(focDescStorageName);
		}
		return focDescToStartFieldPathFrom;
	}
	
	public synchronized Object compute(FFormulaNode formulaNode){
		Object result = 0;
		if(formulaNode != null){
			FFormulaNode childNode = (FFormulaNode) formulaNode.getChildAt(0);
			if(childNode != null){
				String childeNodeValueString = childNode.getCalculatedValue_String();
				if(childeNodeValueString != null){
					FocObject focObject = getFocObjectToStartFieldPathFrom(childeNodeValueString);
					if(focObject != null){
						childNode = (FFormulaNode) formulaNode.getChildAt(1);
						if(childNode != null){
							childeNodeValueString = childNode.getCalculatedValue_String();
							if(childeNodeValueString != null){
								FProperty property = FAttributeLocationProperty.newFieldPathReturnProperty(false, childeNodeValueString, focObject.getThisFocDesc(), focObject);
								if(property != null){
									result = property.getObject();
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	protected int getArgumentPositionForStringPath(){
		return 1;
	}
	
	public boolean needsManualNotificationToCompute() {
		return true;
	}
	
	public String getName(){
		return FUNCTION_NAME;
	}
	
	public String getOperatorSymbol(){
		return OPERATOR_SYMBOL;
	}
	
	public int getOperatorPriority(){
		return FunctionFactory.PRIORITY_NOT_APPLICABLE;
	}
}
