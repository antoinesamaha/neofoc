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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class FColumnGroup implements Cloneable {
  
  private ArrayList<FTableColumn> columnList;
  private ArrayList<FColumnGroup> groupList;
  private Object headerValue;
  private int margin;
  private Color color = null;
  
  public FColumnGroup(Object headerValue){
    this(headerValue, null);
  }
  
  public FColumnGroup(Object headerValue,Color backGroundColor){
    setHeaderValue(headerValue);
    setColor(backGroundColor);
  }
  
  public Object clone() throws CloneNotSupportedException {
    Object obj = null;
    obj = super.clone();
    FColumnGroup columnGroup = (FColumnGroup)obj;
    
    if( columnGroup.groupList != null ){
      ArrayList<FColumnGroup> clonnedGroupList = new ArrayList<FColumnGroup>();
      for( int j = 0; j < columnGroup.groupList.size(); j++){
        clonnedGroupList.add((FColumnGroup)columnGroup.groupList.get(j).clone());
      }
      columnGroup.groupList = clonnedGroupList;  
    }
    
    if( columnGroup.columnList != null ){
      columnGroup.columnList = (ArrayList)columnGroup.columnList.clone();
    }
    
    return obj;
  }
  
  public ArrayList<FTableColumn> getColumnList() {
    if(this.columnList == null){
      columnList = new ArrayList<FTableColumn>();
    }
    return columnList;
  }
  
  public void setColumnList(ArrayList<FTableColumn> columnList) {
    this.columnList = columnList;
  }
  
  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }
  
  public ArrayList<FColumnGroup> getColumnGroupList() {
    if(this.groupList == null){
      this.groupList = new ArrayList<FColumnGroup>();
    }
    return groupList;
  }
  
  public void setGroupList(ArrayList<FColumnGroup> groupList) {
    this.groupList = groupList;
  }
  
  public Object getHeaderValue() {
    return headerValue;
  }
  
  public void setHeaderValue(Object headerValue) {
    this.headerValue = headerValue;
  }
  
  public int getMargin() {
    return margin;
  }
  
  public void setMargin(int margin) {
    this.margin = margin;
  }
  
  public TableCellRenderer getHeaderRenderer() {
    TableCellRenderer headerRenderer = null;
    ArrayList<FTableColumn> columnList = getColumnList();
    Iterator<FTableColumn> columnIter = columnList.iterator();
    while (columnIter != null && columnIter.hasNext() && headerRenderer == null){
      FTableColumn tableColumn = columnIter.next();
      headerRenderer = tableColumn.getTableColumn().getHeaderRenderer();
    }
    
    if(headerRenderer == null){
      ArrayList<FColumnGroup> groupList = getColumnGroupList();
      Iterator<FColumnGroup> groupIter = groupList.iterator();
      while(groupIter != null && groupIter.hasNext() && headerRenderer == null){
        FColumnGroup group = groupIter.next();
        headerRenderer = group.getHeaderRenderer();
      }
    }
    return headerRenderer;
  }
  
  public void addGroup(FColumnGroup groupColumn){
    getColumnGroupList().add(groupColumn);
  }
  
  public void removeGroup(FColumnGroup groupColumn){
    getColumnGroupList().remove(groupColumn);
  }
  
  public void addFTableColumn(FTableColumn fTableColumn){
  	fTableColumn.setHeaderBackColor(getColor());
    getColumnList().add(fTableColumn);
  }
  
  public void removeFTableColumn(FTableColumn fTableColumn){
    getColumnList().remove(fTableColumn);
  }

  public boolean getGroupsIndexesForColumn(FTableColumn fTableColumn, ArrayList<Integer> groupList){
    boolean found = false;
    ArrayList<FTableColumn> columnList = getColumnList();
    if(columnList.contains(fTableColumn)){
      found = true;
    }else{    	
      ArrayList<FColumnGroup> arrayList = getColumnGroupList();
      for(int i=0; i<arrayList.size(); i++){
        FColumnGroup groupColumn = arrayList.get(i);
        groupList.add(Integer.valueOf(i));
        found = groupColumn.getGroupsIndexesForColumn(fTableColumn, groupList);
        if(found){
        	break;
        }
        groupList.remove(Integer.valueOf(i));
      }
    }
    return found; 
  }
  
  public boolean getGroupsForColumn(FTableColumn fTableColumn, ArrayList<FColumnGroup> groupList){
    boolean found = false;
    ArrayList<FTableColumn> columnList = getColumnList();
    if(!columnList.contains(fTableColumn)){
      ArrayList<FColumnGroup> arrayList = getColumnGroupList();
      Iterator<FColumnGroup> iter = arrayList.iterator();
      while(iter.hasNext() && !found){
        FColumnGroup groupColumn = iter.next();
        found = groupColumn.getGroupsForColumn(fTableColumn, groupList);
      }
      if(found){
        groupList.add(0, this);
      }
    }else{
      found = true;
      groupList.add(this);
    }
    return found; 
  }
  
  public FColumnGroup getFatherGroupForColumn(FTableColumn fTableColumn){
  	FColumnGroup result = null;
  	FColumnGroup aGroup = this;
  	ArrayList<FTableColumn> columnList = getColumnList();
  	
  	if(columnList.contains(fTableColumn)){
  		result = aGroup;
  	}
  	
  	ArrayList<FColumnGroup> groupList = getColumnGroupList();
  	Iterator<FColumnGroup> iter = groupList.iterator();
  	while(iter.hasNext() && result == null){
  		aGroup = iter.next();
  		result = aGroup.getFatherGroupForColumn(fTableColumn);
  	}
  	return result;
  }
  
  public Dimension getSize(JTable table){
    Component comp = getHeaderRenderer().getTableCellRendererComponent(table, getHeaderValue(), false, false, -1, -1);
    Dimension size = new Dimension(0, comp.getPreferredSize().height);
    ArrayList<FTableColumn> columnList = getColumnList();
    Iterator<FTableColumn> columnIter = columnList.iterator();
    while(columnIter != null && columnIter.hasNext()){
      FTableColumn tableColumn = columnIter.next();
      if(tableColumn.isVisible()){
        size.width += tableColumn.getTableColumn().getWidth();
      }
    }
    
    ArrayList<FColumnGroup> columnGroupList = getColumnGroupList();
    Iterator<FColumnGroup> groupIter = columnGroupList.iterator();
    while(groupIter != null && groupIter.hasNext()){
      FColumnGroup columnGroup = groupIter.next();
      size.width += columnGroup.getSize(table).width;
      //size.height += columnGroup.getSize(table).height;
    }
    
    return size;
  }
  
  public void setColumnMargin(int margin){
    setMargin(margin);
    ArrayList<FColumnGroup> groupList = getColumnGroupList();
    Iterator< FColumnGroup> iter = groupList.iterator();
    while(iter.hasNext()){
      FColumnGroup groupColumn = (FColumnGroup)iter.next();
      groupColumn.setColumnMargin(margin);
    }
  }
}
