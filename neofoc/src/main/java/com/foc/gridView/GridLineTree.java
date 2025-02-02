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

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.tree.TreeScanner;
import com.foc.tree.objectTree.FObjectNode;
import com.foc.tree.objectTree.FObjectTree;

@SuppressWarnings("serial")
public class GridLineTree extends FObjectTree<FObjectNode, GridLine> {

	private GridDefinition definition = null;
	
	public GridLineTree(GridDefinition definition){
		super(FObjectTree.ROOT_MODE_DISCONNECTED_OBJECT, false);
		this.definition = definition;
		setFatherNodeId(FField.FLD_FATHER_NODE_FIELD_ID);
		setDisplayFieldId(GridLineDesc.FLD_TITLE);
		
		growTreeFromFocList(definition.getGridLineList());
		computeAll();
	}
	
	public void dispose(){
		super.dispose();
		definition = null;
	}
	
	public void computeAll(){
		scan(new TreeScanner<FObjectNode>(){
			public boolean beforChildren(FObjectNode node) {
				return true;
			}
			
			public void afterChildren(FObjectNode node) {
				if(!node.isLeaf()){
					GridLineDesc desc = definition.getGridLineDesc();
					int fieldCount = desc.getLastFieldID() - desc.getFirstFieldID() + 1;
					int fields[] = new int[fieldCount];
					for(int i=0; i<fieldCount; i++){
						fields[i] = i + desc.getFirstFieldID();
					}

					for(int f=0; f<fieldCount; f++){
						double sum = 0;
						for(int n=0; n<node.getChildCount(); n++){
							FObjectNode child = (FObjectNode) node.getChildAt(n);
							double val = ((FocObject)child.getObject()).getPropertyDouble(fields[f]);
							sum += val;
							if(node.getTitle().equals("Bottecino")){
								Globals.logString("  Child ["+n+"]="+val+" sum = "+sum);		
							}
						}
						((FocObject)node.getObject()).setPropertyDouble(fields[f], sum);
					}
					
					//TreeFormulas.getInstance().sumFields(node, fields);
					//BDebug
					/*
					if(node.getTitle().equals("Bottecino")){
						Globals.logString("Begin : "+node.getTitle());
						for(int i=0; i<fieldCount; i++){
							Globals.logString("Field ["+i+"]="+((FocObject)node.getObject()).getPropertyDouble(fields[i]));;
						}
						
						//for(int i=0; i<fieldCount; i++){
						int i=0;
						{
							for(int n=0; n<node.getChildCount(); n++){
								FObjectNode child = (FObjectNode) node.getChildAt(n);
								if(child != null){
									Globals.logString("  Child ["+n+"]="+((FocObject)child.getObject()).getPropertyDouble(fields[i]));;		
								}
							}
						}
						//}
						
						Globals.logString("End : "+node.getTitle());
					}
					*/
					//EDebug
				}
			}
		});
	}
}
