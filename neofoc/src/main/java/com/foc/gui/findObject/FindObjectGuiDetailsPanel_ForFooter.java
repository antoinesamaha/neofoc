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
package com.foc.gui.findObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import com.foc.desc.FocObject;
import com.foc.desc.field.FFieldPath;
import com.foc.gui.FAbstractListPanel;
import com.foc.gui.FGTextField;
import com.foc.gui.FListPanel;
import com.foc.gui.FPanel;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.table.FTable;
import com.foc.gui.treeTable.FTreeTableModel;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListIterator;
import com.foc.property.FProperty;
import com.foc.property.FPropertyListener;
import com.foc.tree.FNode;
import com.foc.tree.FTree;
import com.foc.tree.TreeScanner;

@SuppressWarnings("serial")
public class FindObjectGuiDetailsPanel_ForFooter extends FPanel {
  
  private FindObject            findObject            = null;
  private boolean               findObjectOwner       = false;
  private FAbstractListPanel    listPanel             = null;
  private ArrayList<FFieldPath> fldIDArray            = null;
  private FGTextField           expressionField       = null;
  public FGTextField getExpressionField() {
		return expressionField;
	}

	private FocObject             selectedObject        = null;
  private ArrayList<String>     multipleExpressions   = null;
  
  public FindObjectGuiDetailsPanel_ForFooter(FAbstractListPanel listPanel){
  	this(null, listPanel);
  	fldIDArray = new ArrayList<FFieldPath>();
  }
  
  public FindObjectGuiDetailsPanel_ForFooter(FocObject fo, FAbstractListPanel listPanel){
    super("Find", FPanel.FILL_NONE);   
    this.findObject = (FindObject) fo;
    this.listPanel  = listPanel;
    
    if(findObject == null){
    	findObject = new FindObject();
    	findObjectOwner = true;
    }
    
    findObject.setContains(true);
    findObject.setStartsWith(false);
    
    addLabel("ftr", 0, 0);
    expressionField = (FGTextField) addField(findObject, FindObjectDesc.FLD_FIND, 1, 0);
    expressionField.setRealTimePropertyUpdate(true);
    expressionField.setColumns(6);
    
    SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				getExpressionField().requestFocusInWindow();		
			}
		});
    getExpressionField().requestFocusInWindow();
    
    findObject.getFocProperty(FindObjectDesc.FLD_FIND).addListener(new FPropertyListener(){
			@Override
			public void dispose() {
			}

			@Override
			public void propertyModified(FProperty property) {
				executeFind();
			}
    });
  }
  
  public void dispose(){
    super.dispose();
    if(findObjectOwner && findObject != null){
    	findObject.dispose();
    	findObjectOwner = false;
    }
    findObject = null;
    listPanel = null;
    if(fldIDArray != null){
    	fldIDArray.clear();
    	fldIDArray = null;
    }
    expressionField = null;
    dispose_multipleExpressions();
  }
  
  public ArrayList<String> getMultipleExpressions(){
  	if(multipleExpressions == null){
  		multipleExpressions = new ArrayList<String>();
  		if(findObject != null){
	  		String findExpression = findObject.getFindExpression();
	  		StringTokenizer tok = new StringTokenizer(findExpression, ",", false);
	  		while(tok != null && tok.hasMoreTokens()){
	  			String token = tok.nextToken();
	  			multipleExpressions.add(token);
	  		}
  		}
  	}
  	return multipleExpressions;
  }
  
  public void dispose_multipleExpressions(){
  	if(multipleExpressions != null){
  		multipleExpressions.clear();
  		multipleExpressions = null;
  	}
  }
  
  public void setExpression(String expression){
  	if(findObject != null){
  		findObject.setFindExpression(expression);
  	}
  }
  
  public void addField(FFieldPath fldPath){
  	fldIDArray.add(fldPath);
  }

  public void removeFind(){
		if(findObject != null && !findObject.getFindExpression().isEmpty()){
			findObject.setFindExpression("");
			executeFind();
		}
  }
  
  private boolean isStringLikeExpression(String str){
  	boolean include = findObject.getFindExpression().isEmpty();
    if(!include && str != null && str.length() > 0){
    	ArrayList<String> expressionsArray_Or = getMultipleExpressions();
    	for(int i=0; i<expressionsArray_Or.size() && !include; i++){
    		String oneExpression = expressionsArray_Or.get(i);
        if(findObject.isStartsWith() && str.toUpperCase().startsWith(oneExpression.toUpperCase())){
        	include = true;
        }else if(findObject.isContains() && str.toUpperCase().contains(oneExpression.toUpperCase())){
        	include = true;
        }
    	}
    	
    	/*
      String findExpression = findObject.getFindExpression();
      if(findObject.isStartsWith() && str.toUpperCase().startsWith(findExpression.toUpperCase())){
      	include = true;
      }else if(findObject.isContains() && str.toUpperCase().contains(findExpression.toUpperCase())){
      	include = true;
      }
      */
    }
    return include;
  }

  public boolean isIncludeObject(FocObject focObj){
  	boolean include = false;
		for(int f=0; fldIDArray != null && f<fldIDArray.size() && !include; f++){
			FFieldPath fldPath = fldIDArray.get(f);
			FProperty prop    = fldPath.getPropertyFromObject(focObj);
			Object    currObj = prop != null ? prop.getTableDisplayObject(null) : null;
			if(currObj != null){
	      if(currObj instanceof String){
	        String str = (String) currObj;
	        include = isStringLikeExpression(str);
	      }
			}
		}
		return include;
  }

  public void executeFind_ForList(){
  	FocList list = getListPanel().getFocList();
  	if(list != null){
  		dispose_multipleExpressions();//So that we build a new expressions list
  		
	  	list.iterate(new FocListIterator(){
				@Override
				public boolean treatElement(FocListElement element, FocObject focObj) {
	  			boolean include = false;
	  			if(!include){
	  				include = isIncludeObject(focObj);
	  			}
	  			element.setHide_Soft(!include);
					return false;
				}
	  	});
	    
			getListPanel().afterQuickFilterApplication();
			
	  	list.rebuildArrayList();
	  	if(getListPanel() instanceof FTreeTablePanel){
	  		FTreeTablePanel treeTablePanel = (FTreeTablePanel) listPanel;
	  		FTree           tree           = treeTablePanel.getTree();
	  		tree.resetVisibleChildren();
	  		((FTreeTableModel)treeTablePanel.getTableModel()).refreshGui();
	  		treeTablePanel.expandAll();
	  		tree.scanVisible(new TreeScanner<FNode>(){
					@Override
					public void afterChildren(FNode node) {
					}
	
					@Override
					public boolean beforChildren(FNode node) {
						if(node.isLeaf()){
							setSelectedObject((FocObject) node.getObject());
						}
						return !node.isLeaf();
					}
	  		});
	  	}else{
	  		if(list.size() > 0){
	  			setSelectedObject(list.getFocObject(0));
	  		}
	  	}
	  	getListPanel().getTableModel().fireTableDataChanged();
	  	getListPanel().requestFocusOnTable();
	  	if(getSelectedObject() != null){
	  		getListPanel().setSelectedObject(getSelectedObject());
	  	}
  	}
  }
  
  public void executeFind_ForTree(){
		FTreeTablePanel treeTablePanel = (FTreeTablePanel) listPanel;
		FTree           tree           = treeTablePanel.getTree();
		if(tree != null){
			dispose_multipleExpressions();//So that we build a new expressions list
			
			tree.scan(new TreeScanner<FNode>() {
				@Override
				public void afterChildren(FNode node){
					FocObject focObj = (FocObject) node.getObject();
					
 					boolean include = isStringLikeExpression(node.getTitle());
					if(!include){
						include = isIncludeObject(focObj);
					}
					
					//For Leaf I want to set the visibility to FALSE if it does not contain the string.
					//Because initially the visibility is ANY so the ANY on leaf means visible.
					//Otherwise I will get an empty tree in the beginning
					if(include){
						node.setVisibilityFlagForFilter(FNode.VISIBILITY_TRUE);
					}else{
						node.setVisibilityFlagForFilter(node.isLeaf() ? FNode.VISIBILITY_FALSE : FNode.VISIBILITY_ANY);
					}
				}

				@Override
				public boolean beforChildren(FNode node) {
					return true;
				}
			});
  		  	
			getListPanel().afterQuickFilterApplication();
			
			tree.resetVisibleChildren();
			((FTreeTableModel)treeTablePanel.getTableModel()).refreshGui();
			treeTablePanel.expandAll();
			tree.scanVisible(new TreeScanner<FNode>(){
				@Override
				public void afterChildren(FNode node) {
				}
	
				@Override
				public boolean beforChildren(FNode node) {
					if(node.isLeaf()){
						setSelectedObject((FocObject) node.getObject());
					}
					return !node.isLeaf();
				}
			});
			getListPanel().getTableModel().fireTableDataChanged();
			getListPanel().requestFocusOnTable();
			if(getSelectedObject() != null){
				getListPanel().setSelectedObject(getSelectedObject());
			}
		}
  }
  
  public void executeFind(){
  	if(getListPanel() instanceof FTreeTablePanel){
  		executeFind_ForTree();
  	}else if(getListPanel() instanceof FListPanel){
  		executeFind_ForList();
  	}
  	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(expressionField != null){
					expressionField.requestFocus();
				}
			}
		});
  }
  
  /*
  public void executeFind_ForList(){
  	FocList list = getListPanel().getFocList();
  	if(list != null){
	  	list.iterate(new FocListIterator(){
				@Override
				public boolean treatElement(FocListElement element, FocObject focObj) {
	  			boolean include = false;
	  			if(includeTreeNodeTitles && getListPanel() instanceof FTreeTablePanel){
	  				FTreeTablePanel treePanel = (FTreeTablePanel) getListPanel();
	  				FNode node = treePanel.getTree().findNodeFromFocObject(focObj);
	  				if(node != null){
	  					include = isStringLikeExpression(node.getTitle());
	  				}
	  			}
	  			for(int f=0; fldIDArray != null && f<fldIDArray.size() && !include; f++){
	  				FFieldPath fldPath = fldIDArray.get(f);
	  				FProperty prop    = fldPath.getPropertyFromObject(focObj);
	  				Object    currObj = prop != null ? prop.getTableDisplayObject(null) : null;
	  				if(currObj != null){
	  	        if(currObj instanceof String){
	  	          String str = (String) currObj;
	  	          include = isStringLikeExpression(str);
	  	        }
	  				}
	  			}
	  			element.setHide(!include);
	
					return false;
				}
	  	});
	  	
	  	list.rebuildArrayList();
	  	if(getListPanel() instanceof FTreeTablePanel){
	  		FTreeTablePanel treeTablePanel = (FTreeTablePanel) listPanel;
	  		FTree           tree           = treeTablePanel.getTree();
	  		tree.resetVisibleChildren();
	  		((FTreeTableModel)treeTablePanel.getTableModel()).refreshGui();
	  		treeTablePanel.expandAll();
	  		tree.scanVisible(new TreeScanner<FNode>(){
					@Override
					public void afterChildren(FNode node) {
					}
	
					@Override
					public boolean beforChildren(FNode node) {
						if(node.isLeaf()){
							setSelectedObject((FocObject) node.getObject());
						}
						return !node.isLeaf();
					}
	  		});
	  	}else{
	  		if(list.size() > 0){
	  			setSelectedObject(list.getFocObject(0));
	  		}
	  	}
	  	getListPanel().getTableModel().fireTableDataChanged();
	  	getListPanel().requestFocusOnTable();
	  	if(getSelectedObject() != null){
	  		getListPanel().setSelectedObject(getSelectedObject());
	  	}
  	}
  }
  */
  
  public FAbstractListPanel getListPanel(){
  	return listPanel;
  }

  public void executeFind2(){
    FTable table = FindObjectGuiDetailsPanel_ForFooter.this.listPanel.getTable();
    
    int foundRow = -1;
    int row = table.getSelectedRow();
    
    int col = table.getSelectedColumn();
      
    if(row+1 < table.getRowCount()){
      for(int i=row+1; i<table.getTableModel().getRowCount() && foundRow < 0; i++){
        Object obj = table.getTableModel().getValueAt(i, col);
        if(obj instanceof String){
          String str = (String) obj;
          
          if( str != null && str.length() > 0 ){
            String findExpression = findObject.getFindExpression();
            if(findObject.isStartsWith() && str.toUpperCase().startsWith(findExpression.toUpperCase())){
              foundRow = i;
            }else if(findObject.isContains() && str.toUpperCase().contains(findExpression.toUpperCase())){
              foundRow = i;
            }
          }
        }
      }
    }
    
    if(foundRow >= 0){
      table.setRowSelectionInterval(foundRow, foundRow);
    }
  }
  
  public boolean requestFocusForExpression(){
  	boolean success = false;
  	if(expressionField != null){
  		success = expressionField.requestFocusInWindow();
  	}
  	return success;
  }

	public FocObject getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(FocObject selectedObject) {
		this.selectedObject = selectedObject;
	}
}
