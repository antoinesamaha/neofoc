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
package com.foc.gui.table.view;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.table.JTableHeader;

import com.foc.admin.FocUser;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObject;
import com.foc.desc.field.FField;
import com.foc.gui.table.FColumnGroupTableHeader;
import com.foc.gui.table.FTableColumn;
import com.foc.gui.table.FTableColumnRepresentation;
import com.foc.gui.table.FTableView;
import com.foc.list.FocList;
import com.foc.list.FocListElement;
import com.foc.list.FocListOrder;
import com.foc.property.FObject;
import com.foc.property.FString;

public class ViewConfig extends FocObject{
	private HashMap<Integer, ColumnsConfig> map = null;
	
  public ViewConfig(FocConstructor constr){
    super(constr);
    newFocProperties();
  }

  public void dispose(){
  	super.dispose();
  }
  
  public void dispose_ColumnsConfigMap(){
  	if(map != null){
  		map.clear();
  		map = null;
  	}
  }
  
  public static void disposeColumnsConfigFocList(FocList list){
    list.dispose();
    list = null;
  }
  
  @Override
  protected void duplication_CopyPropertiesAndSetToCreated(FocObject dupObj, int[] excludedProperties){
  	if(excludedProperties == null){
  		excludedProperties = new int[1];
  		excludedProperties[0] = ViewConfigDesc.FLD_CODE;
  	}
  	dupObj.copyPropertiesFrom(this, excludedProperties);
  }
  
  @Override
  public void duplicationModification(FocObject source){
  	ViewConfig view = (ViewConfig) source;
  	
  	int[] excludedProps = new int[1];
  	excludedProps[0] = ColumnsConfigDesc.FLD_VIEW_CONFIG;
  	FocList srcList = view.getColumnsConfigList();
  	FocList tarList = getColumnsConfigList();
  	for(int i=0; i<srcList.size(); i++){
  		ColumnsConfig srcColConf = (ColumnsConfig) srcList.getFocObject(i);
  		ColumnsConfig tarColConf = (ColumnsConfig) tarList.newEmptyItem();
  		
  		tarColConf.copyPropertiesFrom(srcColConf, excludedProps);
  		tarColConf.setCreated(true);
  		tarList.add(tarColConf);
  	}
  	tarList.copy(srcList);
  }
  
  public HashMap<Integer, ColumnsConfig> getColumnsConfigMap(boolean create){
  	if(map == null && create){
  		map = new HashMap<Integer, ColumnsConfig>();
  	}
  	return map;
  }
  
  public FocUser getUser(){
    FObject user = (FObject)getFocProperty(ViewConfigDesc.FLD_USER);
    return (FocUser)user.getObject_CreateIfNeeded();
  }
  
  public void setUser(FocUser user){
    FObject objProp = (FObject) getFocProperty(ViewConfigDesc.FLD_USER);
    objProp.setObject(user);
  }
  
  public String getViewKey(){
    FString viewKey = (FString)getFocProperty(ViewConfigDesc.FLD_VIEW_KEY);
    return viewKey.getString();
  }
  
  public void setViewKey(String viewKey){
    FString viewProp = (FString)getFocProperty(ViewConfigDesc.FLD_VIEW_KEY);
    if(viewProp != null){
      viewProp.setString(viewKey);
    }
  }

  public FocList getColumnsConfigList(){
  	FocList list = getPropertyList(ViewConfigDesc.FLD_COLUMN_ID_LIST);
  	if(list.getListOrder() != getColumnListOrder()){
  		list.setListOrder(getColumnListOrder());
  	}
  	return list;
  }
  
  public FocList getColumnsConfigList_Full(FTableView tableView){
  	FocList list = getColumnsConfigList();
  	
		FColumnGroupTableHeader tableHeader = null;
  	if(tableView.getTable() != null){
  		JTableHeader jTableHeader = tableView.getTable().getTableHeader();
  		if(jTableHeader != null && jTableHeader instanceof FColumnGroupTableHeader){
  			tableHeader = (FColumnGroupTableHeader) jTableHeader;
  		}
  	}
  	
  	int maxOrder = getMaxOrderAndSetMaxForZeroOrder(list);
  	maxOrder++;
  	
    for(int c=0; c < tableView.getColumnCount(); c++){
      FTableColumn col = tableView.getColumnAt(c);
      if(col.isShowConfigurable() && col.getColumnRepresentation() == null){
	      String title = "Line number";
	      if(col.getID() != FField.LINE_NUMBER_FIELD_ID){
	      	title = col.getColumnTitleInViewConfiguration(tableHeader);
	      }
	      
	      maxOrder = pushColumnConfig(list, col.getID(), title, maxOrder, col.getExplanation());
      }
    }

    for(int cp = 0; cp < tableView.getColumnRepresentationCount(); cp++){
      FTableColumnRepresentation col = tableView.getColumnRepresentationAt(cp);
      if(col.isShowConfigurable()){
	      String title = col.getColumnTitleInViewConfiguration(null);
	      pushColumnConfig(list, col.getID(), title, maxOrder++, col.getExplanation());
      }
    }
    
    //Scan Column titles and hide all that have empty titles
  	for(int i=list.size()-1; i>=0; i--){
  		ColumnsConfig colConfig = (ColumnsConfig) list.getFocObject(i);
  		if(colConfig != null && colConfig.getColumnDefaultTitle().isEmpty()){
  			FocListElement elem = list.getFocListElement(i);
  			if(elem != null){
  				//elem.setHide(true);
  				elem.getFocObject().getFocProperty(ColumnsConfigDesc.FLD_COLUMN_TITLE).setBackground(Color.GRAY);
  			}
  		}
  	}
  	
  	list.rebuildArrayList();
    list.sort();
  	
  	return list;
  }

	private int getMaxOrderAndSetMaxForZeroOrder(FocList list){
		int maxOrder = 0;
		for(int i=0; i<list.size(); i++){
			ColumnsConfig colConfig = (ColumnsConfig) list.getFocObject(i);
			if(colConfig != null && maxOrder < colConfig.getColumnOrder()){
				maxOrder = colConfig.getColumnOrder();
			}
		}
		for(int i=0; i<list.size(); i++){
			ColumnsConfig colConfig = (ColumnsConfig) list.getFocObject(i);
			if(colConfig != null && colConfig.getColumnOrder() == 0){
				colConfig.setColumnOrder(maxOrder++);
			}
		}
		
		return maxOrder;
	}  
  
	private ColumnsConfig findColumnConfig(FocList list, int id){
	  ColumnsConfig foundColConfig = null;
		for(int i=0; i<list.size() && foundColConfig == null; i++){
			ColumnsConfig colConfig = (ColumnsConfig) list.getFocObject(i);
			if(colConfig != null && colConfig.getColumnID() == id){
				foundColConfig = colConfig;
			}
		}
		return foundColConfig;
	}	
	
	private int pushColumnConfig(FocList list, int id, String title, int c, String explanation){
	  ColumnsConfig foundColConfig = findColumnConfig_InMap(id);
	  if(foundColConfig == null){
	  	foundColConfig = findColumnConfig(list, id);
	  }
		if(foundColConfig == null){
			foundColConfig = (ColumnsConfig) list.newEmptyItem();
			foundColConfig.setColumnID(id);
			foundColConfig.setShow(true);
			foundColConfig.setColumnOrder(c++);			
			list.add(foundColConfig);
		}
		if(foundColConfig != null){
			foundColConfig.setColumnDefaultTitle(title);
			foundColConfig.setColumnExplanation(explanation);
			if(foundColConfig.getColumnTitle().isEmpty()){
				foundColConfig.setColumnTitle(title);
			}
			if(findColumnConfig_InMap(id) == null){
				HashMap<Integer, ColumnsConfig> map = getColumnsConfigMap(true);
				map.put(id, foundColConfig);
			}
		}
		return c;
	}
	
	public ColumnsConfig findColumnConfig_InMap(int id){
		ColumnsConfig foundColConfig = null;
		HashMap<Integer, ColumnsConfig> map = getColumnsConfigMap(true);
		if(map != null){
			foundColConfig = map.get(id);
		}
		return foundColConfig;
	}
	
	private static FocListOrder columnListOrder = null; 
	public static FocListOrder getColumnListOrder(){
		if(columnListOrder == null){
			columnListOrder = new FocListOrder(ColumnsConfigDesc.FLD_COLUMN_ORDER);
		}
		return columnListOrder;
	}	
}
