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
package com.foc.gui;

import com.foc.desc.FocObject;
import com.foc.tree.FNode;

public class DoubleClickEvent {
	private int       row       = -1;
	private int       col       = -1;
	private FocObject focObject = null;
	private FNode     node      = null;
	
	public DoubleClickEvent(int row, int col){
		this.row = row;
		this.col = col;
	}
	
	public void dispose(){
		focObject = null;
		node = null;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public FocObject getFocObject() {
		return focObject;
	}

	public void setFocObject(FocObject focObject) {
		this.focObject = focObject;
	}

	public FNode getNode() {
		return node;
	}

	public void setNode(FNode node) {
		this.node = node;
	}
}
