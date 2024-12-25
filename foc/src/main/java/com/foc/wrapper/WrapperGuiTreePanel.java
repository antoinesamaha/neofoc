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
package com.foc.wrapper;

import com.foc.desc.FocDesc;
import com.foc.gui.FPanel;
import com.foc.gui.FTreeTablePanel;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;

/*public class WrapperGuiTreePanel extends FPanel{

  private FTreeTablePanel selectionPanel    = null;
  private FocList wrapperBasicResourceList  = null;
  private WrapperTree tree     = null;
  
  public WrapperGuiTreePanel(FocList list, int viewID){
    super("", FPanel.FILL_BOTH);
    
    wrapperBasicResourceList = list;
    FocDesc desc = list.getFocDesc();
    
    if (desc != null && list != null) {
      wrapperBasicResourceList.setDirectImpactOnDatabase(false);
      
      tree = new WrapperTree(wrapperBasicResourceList, viewID);
      
      selectionPanel = new FTreeTablePanel(tree);
      
      addColumns();
      //tableView.addColumn(desc, WrapperDesc.FATHER_NODE_ID, true);
      
      selectionPanel.construct();
      selectionPanel.showRemoveButton(false);
      selectionPanel.showAddButton(false);
      selectionPanel.showEditButton(false);
      showValidationPanel(true);
    }
    add(selectionPanel,0,0);
  }
  
  public void dispose(){
  	super.dispose();
  }
  
  public WrapperTree getTree() {
    return tree;
  }
  
  protected void addColumns(){
    
  }
  
  public FTreeTablePanel getSelectionPanel(){
    return selectionPanel;
  }
}*/

@SuppressWarnings("serial")
public class WrapperGuiTreePanel extends FTreeTablePanel{
	
	private WrapperTree wrapperTree = null;
  
	public WrapperGuiTreePanel(int viewID){
    setMainPanelSising(FPanel.FILL_BOTH);
	}
	
  public WrapperGuiTreePanel(FocList list, int viewID){
    //super("", FPanel.FILL_BOTH);
    setMainPanelSising(FPanel.FILL_BOTH);
    WrapperTree wrapperTree = newWrapperTree(list, viewID);
    init(wrapperTree, viewID);
  }
  
  public WrapperGuiTreePanel(WrapperTree wrapperTree, int viewID){
    //super("", FPanel.FILL_BOTH);
  	setMainPanelSising(FPanel.FILL_BOTH);
    init(wrapperTree, viewID);
  }
  
  public void init(WrapperTree wrapperTree, int viewID){
    this.wrapperTree = wrapperTree;
    wrapperTree.setViewID(viewID);
    
    FocList list = wrapperTree.getFocList();
    FocDesc desc = list.getFocDesc();
    if (desc != null && list != null) {
      list.setDirectImpactOnDatabase(false);
      setTree(wrapperTree);
      addColumns(getTableView(), desc, viewID);
      construct();
      showRemoveButton(false);
      showAddButton(false);
      showEditButton(false);
      
      showValidationPanel(true);
    }
  }
  
  public void dispose(){
  	super.dispose();
    this.wrapperTree = null;
  }
  
  public WrapperTree getWrapperTree(){
  	return this.wrapperTree;
  }
  
  protected WrapperTree newWrapperTree(FocList focList, int viewID){
  	return new WrapperTree(focList, viewID);
  }
  
  protected void addColumns(FTableView tableView, FocDesc focDesc, int viewID){
  }
}
