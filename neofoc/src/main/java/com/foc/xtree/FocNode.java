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
package com.foc.xtree;

import java.util.ArrayList;
import java.util.Iterator;

import com.foc.desc.FocObject;

public class FocNode {
	private FocNode   fatherNode = null;
	private FocObject focObject  = null;
	
	private ArrayList<FocNode> children = null;
	
	public FocNode(FocNode fatherNode, FocObject focObject){
		this.fatherNode = fatherNode;
		this.focObject = focObject;
		children = null;
		if(fatherNode != null){
			fatherNode.addChild(this);
		}
	}
	
	public FocNode getFatherNode(){
		return fatherNode;
	}
	
	public FocObject getFocObject(){
		return focObject;
	}
	
	public void addChild(FocNode childNode){
		if(children == null) children = new ArrayList<FocNode>();
		children.add(childNode);
	}
	
	public void removeChild(FocNode childNode){
		if(children == null) children = new ArrayList<FocNode>();
		children.remove(childNode);
	}
	
	public void scan(TreeScanner scanner){
		if(scanner != null){
			boolean goInside = scanner.beforChildren(this);
			if(goInside && children != null){
				Iterator iter = children.iterator();
				while(iter != null && iter.hasNext()){
					FocNode child = (FocNode) iter.next();
					child.scan(scanner);
				}				
			}
			scanner.afterChildren(this);			
		}
	}
}
