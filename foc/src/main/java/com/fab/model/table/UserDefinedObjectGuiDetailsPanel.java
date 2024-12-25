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
package com.fab.model.table;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.util.Iterator;

import javax.swing.JEditorPane;
import javax.swing.border.Border;

import com.fab.gui.details.GuiDetails;
import com.fab.gui.details.GuiDetailsComponent;
import com.fab.gui.html.TableHtml;
import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.FGButton;
import com.foc.gui.FGLabel;
import com.foc.gui.FGTabbedPane;
import com.foc.gui.FPanel;
import com.foc.gui.FValidationPanel;
import com.foc.list.FocList;

@SuppressWarnings("serial")
public class UserDefinedObjectGuiDetailsPanel extends FPanel {
	
	private FocObject  focObject = null;
	private GuiDetails detailsViewDefintion = null;
	public UserDefinedObjectGuiDetailsPanel(FocObject focObject, int viewId){
		if(focObject != null){
			GuiDetails detailsViewDefinition = TableDefinition.getDetailsViewDefinitionForFocDescAndViewId(focObject.getThisFocDesc(), viewId);
			this.detailsViewDefintion = detailsViewDefinition;
			init(focObject, detailsViewDefinition);
		}
	}
	
	public UserDefinedObjectGuiDetailsPanel(FocObject focObject, GuiDetails detailsViewDefinition){
		this.detailsViewDefintion = detailsViewDefinition;
		init(focObject, detailsViewDefinition);
	}
	
	public void dispose(){
		super.dispose();
		this.focObject = null;
	}
	
	public void createBorder(){
		if(detailsViewDefintion != null){
			setBorder(detailsViewDefintion.getTitle());
		}
	}
	
	private void init(FocObject focObject, GuiDetails detailsViewDefinition){
		this.focObject = focObject;
		if(detailsViewDefinition != null){
			int viewMode = detailsViewDefinition.getViewMode();
			if(viewMode == GuiDetails.VIEW_MODE_ID_NORMAL){
				init_NORMAL_VIEW(detailsViewDefinition);
			}else{
				init_TABBED_PANEL_VIEW(detailsViewDefinition);
			}
			if(detailsViewDefinition.isShowValidationPanel()){
				FValidationPanel validPanel = showValidationPanel(true);
				if(validPanel != null){
					if(detailsViewDefinition.isAddSubjectToValidationPanel()){
						validPanel.addSubject(focObject);
						validPanel.setValidationType(FValidationPanel.VALIDATION_SAVE_CANCEL);
					}else{
						validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
					}
				}
			}
			setFrameTitle(detailsViewDefinition.getTitle());
			setMainPanelSising(FPanel.FILL_NONE);
		}else{
			init_NO_VIEW_DEFINITION();
			FValidationPanel validPanel = showValidationPanel(true);
			if(validPanel != null){
				validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void init_NORMAL_VIEW(GuiDetails detailsViewDefinition){
		FocList guidDetailsFieldList = detailsViewDefinition.getGuiDetailsFieldList();
		Iterator<GuiDetailsComponent> iter = guidDetailsFieldList.focObjectIterator();
		while(iter != null && iter.hasNext()){
			int                 fill                   = GridBagConstraints.NONE;
			GuiDetailsComponent detailsFieldDefinition = iter.next();
			Component           comp                   = detailsFieldDefinition.getComponentForFocObject(focObject);
			FieldDefinition     fieldDefinition        = detailsFieldDefinition.getFieldDefinition();
			String              title                  = "";
			if(comp != null){ 
				if(comp instanceof FPanel){
					fill = ((FPanel)comp).getFill();
				}
				boolean withTtile = fieldDefinition !=null && fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_LIST_FIELD && (fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_OBJECT_FIELD || detailsFieldDefinition.getComponentGuiDetails() == null);
				withTtile = withTtile && fieldDefinition.getSQLType() != FieldDefinition.SQL_TYPE_ID_BOOLEAN;
				if(withTtile){
					FField field = focObject.getThisFocDesc().getFieldByID(fieldDefinition.getID());
					title = field.getTitle();
				}
				int gridWidth  = detailsFieldDefinition.getGridWidth();
				int gridHeight = detailsFieldDefinition.getGridHeight();
				if(gridWidth  == 0) gridWidth  = 1;
				if(gridHeight == 0) gridHeight = 1;
				
		  	addLabel(title, detailsFieldDefinition.getX(), detailsFieldDefinition.getY(), 1, gridHeight);
		  	addField(comp, detailsFieldDefinition.getX()+1, detailsFieldDefinition.getY(), gridWidth, gridHeight, fill);
			}
		}
	}
	
	/*@SuppressWarnings("unchecked")
	private void init_TABBED_PANEL_VIEW(GuiDetails detailsViewDefinition){
		FGTabbedPane tabbedPan = new FGTabbedPane();
		FocList guiDetailsFieldList = detailsViewDefinition.getGuiDetailsFieldList();
		
		for(int i=0; i<guiDetailsFieldList.size(); i++){
			GuiDetailsComponent detailsFieldDefinition = (GuiDetailsComponent)guiDetailsFieldList.getFocObject(i);

			FieldDefinition fieldDefinition = detailsFieldDefinition.getFieldDefinition();
			FPanel panel = null;
			if(fieldDefinition != null){
				if(fieldDefinition.getSQLType() == FieldDefinition.TYPE_ID_FOBJECT_FIELD){
					FocObject focObj = focObject.getPropertyObject(fieldDefinition.getID());
					panel = new UserDefinedObjectGuiDetailsPanel(focObj, detailsFieldDefinition.getViewId());
				}
			}else{
				panel = new UserDefinedObjectGuiDetailsPanel(focObject, detailsFieldDefinition.getViewId());
			}
			if(panel != null){
				tabbedPan.add(panel.getFrameTitle(), panel);
			}
		}
		add(tabbedPan, 0, 0);
	}*/
	
	@SuppressWarnings("unchecked")
	private void init_TABBED_PANEL_VIEW(GuiDetails detailsViewDefinition){
		FGTabbedPane tabbedPan = new FGTabbedPane();
		FocList guiDetailsFieldList = detailsViewDefinition.getGuiDetailsFieldList();
		
		for(int i=0; i<guiDetailsFieldList.size(); i++){
			GuiDetailsComponent detailsFieldDefinition = (GuiDetailsComponent)guiDetailsFieldList.getFocObject(i);
			if(detailsFieldDefinition != null){
				FPanel panel = (FPanel) detailsFieldDefinition.getComponentForFocObject(focObject);
				if(panel != null){
					panel.setBorder((Border)null);
					tabbedPan.add(panel.getFrameTitle(), panel);
				}
			}
		}
		add(tabbedPan, 0, 0);
	}
	
	private void init_NO_VIEW_DEFINITION(){
		FGLabel label = new FGLabel("Could not find view definition for object.");
		add(label, 0, 0);
		setFrameTitle("No view definition found");
	}
	
	@Override
	public FValidationPanel showValidationPanel(boolean show, boolean normalButtonsPositioning) {
		FValidationPanel vPanel = super.showValidationPanel(show, normalButtonsPositioning);
		
		if(getDetailsViewDefintion() != null && getDetailsViewDefintion().getTableDefinition() != null && getDetailsViewDefintion().getTableDefinition().getHtmlFormList() != null){
			FocList   htmlFormList = getDetailsViewDefintion().getTableDefinition().getHtmlFormList();
			TableHtml html         = (TableHtml) htmlFormList.getAnyItem();
			if(html != null){
				FGButton print = new FGButton("Print...");
				vPanel.addButton(print);
				print.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						FocList   htmlFormList = detailsViewDefintion.getTableDefinition().getHtmlFormList();
						TableHtml html         = (TableHtml) htmlFormList.getAnyItem();
						
						JEditorPane editorPane = new JEditorPane();
						editorPane.setContentType("text/html");
						editorPane.setText(html.getHTML());
						try{
							editorPane.print(null, null, true, null, null, false);
						}catch (PrinterException e1){
							Globals.logException(e1);
						}
					}
				});
			}
		}
		return vPanel; 
	}

	public FocObject getFocObject() {
		return focObject;
	}

	public void setFocObject(FocObject focObject) {
		this.focObject = focObject;
	}

	public GuiDetails getDetailsViewDefintion() {
		return detailsViewDefintion;
	}

	public void setDetailsViewDefintion(GuiDetails detailsViewDefintion) {
		this.detailsViewDefintion = detailsViewDefintion;
	}
}
