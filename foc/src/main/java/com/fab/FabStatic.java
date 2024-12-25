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
package com.fab;

import java.util.ArrayList;

import com.foc.desc.field.FDescFieldStringBased;

public class FabStatic {
	private static ArrayList<FDescFieldStringBased> fieldArray = null;
	public static void addStringBasedField(FDescFieldStringBased fieldToAdd){
		if(fieldArray == null){
			fieldArray = new ArrayList<FDescFieldStringBased>();
		}
		fieldArray.add(fieldToAdd);
	}
	
	public static void refreshAllTableFieldChoices(){
		if(fieldArray != null){
			for(int i=0; i<fieldArray.size(); i++){
				FDescFieldStringBased field = fieldArray.get(i);
				if(field != null){
					field.re_fillWithAllDeclaredFocDesc();
				}
			}
		}
	}
}
