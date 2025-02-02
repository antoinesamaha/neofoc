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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.foc.property.FEMailProperty;
import com.foc.util.EMail;

@SuppressWarnings("serial")
public class FGEMailComponent extends FPanel {
	private JComponent     jTextComp = null; 
	private FEMailProperty property  = null;
	
	public FGEMailComponent(FEMailProperty property, JComponent jTextComp){
		super();
		this.property = property;
		add(jTextComp, 0, 0);
		FGButton sendMail = new FGButton("Send");
		add(sendMail, 1, 0);

		sendMail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(getProperty() != null && getProperty().getString() != null && !getProperty().getString().isEmpty()){
					EMail.sendMail(getProperty().getString(), null);
				}
			}
		});
	}
	
	public void dispose(){
		super.dispose();
		property = null;
		jTextComp = null;
	}
	
	public FEMailProperty getProperty(){
		return property;
	}
}
