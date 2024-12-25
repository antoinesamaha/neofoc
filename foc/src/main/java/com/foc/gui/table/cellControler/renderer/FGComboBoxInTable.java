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
package com.foc.gui.table.cellControler.renderer;

import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

@SuppressWarnings("serial")
public class FGComboBoxInTable extends JComboBox{

	//private JTable table  = null;
	//private int    height = 0   ;
	
	public FGComboBoxInTable(JTable table) {
		super();
		//this.table = table;
	}
	
	public void dispose(){
		//table = null;
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    //super.setBounds(x, y, width, height+getShiftDistance());
	}

	@Override
	public void paint(Graphics g) {
		//g.translate(0, -getShiftDistance());
		super.paint(g);
	}

	/*
	private int getShiftDistance(){
		int distance = 0;
		if(height == 0) height = getPreferredSize().height; 
		if(table.getRowHeight() < height){
			distance = height - table.getRowHeight();
			if(distance % 2 == 1){
				distance += 1;
			}
			distance = distance / 2;
		}
		return distance;
	}
	*/
}
