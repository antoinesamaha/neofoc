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
package com.fab.gui.html;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JEditorPane;

import com.foc.desc.FocObject;
import com.foc.gui.FGButton;
import com.foc.gui.FPanel;

@SuppressWarnings("serial")
public class TableHtmlGuiDetailsPanel extends FPanel {
	
	private TableHtml tableHtml = null;
	private JEditorPane editorPane = null;
	
	public TableHtmlGuiDetailsPanel(FocObject tableHtml, int viewId){
		super("Gui details", FPanel.FILL_BOTH);
		this.tableHtml = (TableHtml)tableHtml;
		
		int y = 0;
		
		add(tableHtml, TableHtmlDesc.FLD_TITLE, 0, y++);
		add(tableHtml, TableHtmlDesc.FLD_DESCRIPTION, 0, y++);
		add(tableHtml, TableHtmlDesc.FLD_HTML, 0, y++, 2, 1, GridBagConstraints.BOTH);
		editorPane = new JEditorPane();
		add(editorPane, 0, y++, 2, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH);
		FGButton button = new FGButton("Apply");
		add(button, 0, y++, 2, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editorPane.setText(getTableHtml().getHTML());
			}
		});
		editorPane.setContentType("text/html");
		editorPane.setText(getTableHtml().getHTML());
		//editorPane.setText("<h1>This is a big title</h1><br><b>Welcome</b>to 01Barmaja");
		setMinimumSize(new Dimension(100, 500));
	}
	
	public void dispose(){
		super.dispose();
		tableHtml = null;
		editorPane = null;
	}

	public TableHtml getTableHtml() {
		return tableHtml;
	}

	public void setTableHtml(TableHtml tableHtml) {
		this.tableHtml = tableHtml;
	}

}
