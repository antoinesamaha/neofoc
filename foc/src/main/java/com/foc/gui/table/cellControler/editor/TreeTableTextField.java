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
package com.foc.gui.table.cellControler.editor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TreeTableTextField extends JPanel {
  public int offset;
  private GridBagConstraints constr = null;
  
  private Component codeComp  = null;
  private Component titleComp = null;
  
  public TreeTableTextField(){
    setLayout(new GridBagLayout());
    constr = new GridBagConstraints();
    constr.gridwidth = 1;
    constr.gridheight = 1;
    constr.insets.bottom = 0;
    constr.insets.top = 0;
    constr.insets.left = 0;
    constr.insets.right = 0;
    constr.weightx = 0.99;
    constr.weighty = 0.99;
    constr.fill = GridBagConstraints.BOTH;
    constr.anchor = GridBagConstraints.WEST;
    constr.gridx = 0;
    constr.gridy = 0;
  }
  
  public void dispose(){
  	codeComp  = null;
  	titleComp = null;
  }
  
  public void setBounds(int x, int y, int w, int h) {
    int newX = Math.max(x, offset);
    super.setBounds(newX, y, w - (newX - x), h);
  }
  
  public void addComponent(Component codeComp, Component titleComp){
  	this.codeComp  = codeComp ;
  	this.titleComp = titleComp;
  	
  	constr.gridx = 0;
  	constr.weightx = 0.99;
  	if(codeComp != null){
  		constr.weightx = 0.25;
  		add(codeComp, constr);
  		constr.gridx++;
  		constr.weightx = 0.75;
  	}
    add(titleComp, constr);
  }

	public Component getCodeComp() {
		return codeComp;
	}

	public Component getTitleComp() {
		return titleComp;
	}
}
