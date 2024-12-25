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
package com.foc.gridView;

import com.foc.desc.FocDesc;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.pivot.FPivotRow;
import com.foc.tree.FNode;
import com.foc.tree.FTree;

public class GridDefinition {
	
	public static final char FIELD_NAME_SEPARATOR = '.';

	//Input
	private FTree tree         = null;
	private int   level        = 0   ;
	private int[] fieldsArray  = null;
	private int   codeFieldID  = FField.NO_FIELD_ID;
	private int   titleFieldID = FField.NO_FIELD_ID;
	private String[] fieldsPathArray  = null;

	//Constructed locally
	private GridLineDesc gridLineDesc = null;
	private GridLineList gridLineList = null;
	private GridLineTree gridLineTree = null;	

	public GridDefinition(FTree tree, int level, int codeFieldID, int titleFieldID, String[] fieldsPathArray){
		this.tree         = tree ;
		this.level        = level;
		this.codeFieldID  = codeFieldID;
		this.titleFieldID = titleFieldID;
		this.fieldsPathArray  = fieldsPathArray;
	}
	
	public GridDefinition(FTree tree, int level, int codeFieldID, int titleFieldID, int[] fieldsArray){
		this.tree         = tree ;
		this.level        = level;
		this.fieldsArray  = fieldsArray;
		this.codeFieldID  = codeFieldID;
		this.titleFieldID = titleFieldID;
	}

	public void dispose(){
		tree = null;
		if(fieldsArray != null){
			fieldsArray = null;
		}
		if(gridLineTree != null){
			gridLineTree.dispose();
			gridLineTree = null;
		}
		if(gridLineList != null){
			gridLineList.dispose();
			gridLineList = null;
		}
		if(gridLineDesc != null){
			gridLineDesc.dispose();
			gridLineDesc = null;
		}
		gridLineDesc = null;
		fieldsPathArray = null;
	}
	
	public FocObject getPivotRowObjectByAccountCode(String accountCode){
		FPivotRow pivotRow = null;
		if(getTree() != null){
			FNode fNode = getTree().getNodeByTitle(accountCode);
			pivotRow = fNode != null ? (FPivotRow) fNode.getObject() : null;
		}
		return pivotRow != null ? pivotRow.getPivotRowObject() : null;
	}
	
	public Object getFieldAt_Object(int at){
		Object value = null;
		if(fieldsArray != null){
			value = getFieldAt(at);
		}else{
			value = getFieldPathAt(at);
		}
		return value;
	}

	public int getFieldCount(){
		return fieldsArray != null ? fieldsArray.length : 0;
	}
	
	public int getFieldAt(int at){
		return fieldsArray != null && at < fieldsArray.length ? fieldsArray[at] : 0;
	}
	
	public int getFieldPathCount(){
		return fieldsPathArray != null ? fieldsPathArray.length : 0;
	}
	
	public String getFieldPathAt(int at){
		return fieldsPathArray != null && at < fieldsPathArray.length ? fieldsPathArray[at] : null;
	}
	
	public int getLevel(){
		return level;
	}

	public FTree getTree(){
		return tree;
	}
	
	public int getCodeFieldID() {
		return codeFieldID;
	}

	public int getTitleFieldID() {
		return titleFieldID;
	}

	public FocDesc getOriginalFocDesc(){
		return tree != null && tree.getFocList() != null ? tree.getFocList().getFocDesc() : null;
	}
	
	public GridLineDesc getGridLineDesc(){
		if(gridLineDesc == null){
			gridLineDesc = new GridLineDesc(this);
		}
		return gridLineDesc;
	}
	
	public GridLineList getGridLineList(){
		if(gridLineList == null){
			gridLineList = new GridLineList(this);
		}
		return gridLineList;
	}
	
	public GridLineTree getGridLineTree(){
		return getGridLineTree(true);
	}
	
	public GridLineTree getGridLineTree(boolean create){
		if(gridLineTree == null && create){
			gridLineTree = new GridLineTree(this);
		}
		return gridLineTree;
	}
}
