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
package com.foc.gui.table;

import java.util.ArrayList;
import javax.swing.table.TableColumnModel;

public class FColumnGroupHeaderConstructor implements Cloneable {
  private ArrayList<FColumnGroup> columnGroupFatherArray = null;
  private FTableView tableView = null;
  
  public FColumnGroupHeaderConstructor(FTableView tableView){
    columnGroupFatherArray = new ArrayList<FColumnGroup>();
    this.tableView = tableView;
  }
  
  public void dispose(){
    columnGroupFatherArray.clear();
    columnGroupFatherArray = null;
    tableView = null;
  }
  
  public Object clone() throws CloneNotSupportedException{
    return super.clone();
  }
  
  public int getColumnGroupFatherArraySize(){
    return columnGroupFatherArray.size();
  }

  public FColumnGroup getColumnGroupFatherAt(int at){
    return columnGroupFatherArray.get(at);
  }

  public void addChildGroup(FColumnGroup columnGroup){
    columnGroupFatherArray.add(columnGroup);
  }
  
  public FColumnGroupTableHeader constructHeader(FTable table){
    TableColumnModel        tableColumnModel = table.getColumnModel();
    FColumnGroupTableHeader tableHeader      = new FColumnGroupTableHeader(tableColumnModel);
    for( int j = 0; j < columnGroupFatherArray.size(); j++){
      tableHeader.addColumnGroup(columnGroupFatherArray.get(j));
    }
    table.setTableHeader(tableHeader);
    table.setDropable(false);
    return tableHeader;
  }

  public void cloneColumnGroupFatherArray(){
    ArrayList<FColumnGroup> clonnedColumnGroupFatherArray = new ArrayList<FColumnGroup>();
    for( int i = 0; i < columnGroupFatherArray.size(); i++){
      try {
        clonnedColumnGroupFatherArray.add((FColumnGroup)columnGroupFatherArray.get(i).clone());
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
    }
    columnGroupFatherArray = clonnedColumnGroupFatherArray; 
    replaceColumnGroupColumns(columnGroupFatherArray);
  }
  
  private FTableColumn getFTableColumnByIDFromTableView(int id){
    for( int i = 0; i < tableView.getColumnCount(); i++){
      if( tableView.getColumnAt(i).getID() == id ){
        return tableView.getColumnAt(i);
      }
    }
    return null;
  }
  
  private void replaceColumnGroupColumns(ArrayList<FColumnGroup> columnGroupList){
    for( int i = 0; i < columnGroupList.size(); i++){
      FColumnGroup columnGroup = columnGroupList.get(i);
      scanColumnGroup(columnGroup);
    }
  }
  
  private void scanColumnGroup(FColumnGroup columnGroup){
    if( columnGroup.getColumnGroupList().size() > 0 ){
      replaceColumnGroupColumns(columnGroup.getColumnGroupList());
    }
    if( columnGroup.getColumnList().size() > 0 ){
      for( int i = 0; i < columnGroup.getColumnList().size(); i++ ){
        FTableColumn ftableCol = columnGroup.getColumnList().get(i);
        columnGroup.getColumnList().set(i, getFTableColumnByIDFromTableView(ftableCol.getID()));
      }
    }
  }

  public void setTableView(FTableView tableView) {
    this.tableView = tableView;
  }
}
